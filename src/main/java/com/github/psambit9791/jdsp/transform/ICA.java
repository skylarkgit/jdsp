/*
 *
 *  * Copyright (c) 2020 Sambit Paul
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.github.psambit9791.jdsp.transform;

import com.github.psambit9791.jdsp.misc.Random;
import com.github.psambit9791.jdsp.misc.UtilMethods;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

import java.util.Arrays;

public class ICA {

    private double[][] signal;
    private double[][] zm_signal;
    private double[][] output;
    private double alpha = 1.0;
    public double[] gx;
    public double g_x;
    public double[][] w_init;
    private int max_iter = 200;
    private double tol = 1E-4;
    private String whiten = "unit-variance";
    private String func = "logcosh";
    private long seed = 42;
    private double[] mean_;
    private int components;


    private void logcosh_(double[] x) {
        this.gx = new double[x.length];
        if (this.alpha < 1.0 || this.alpha > 2.0) {
            throw new IllegalArgumentException("alpha should be between 1.0 and 2.0");
        }

        double temp = 0.0;
        for (int j=0; j<this.gx.length; j++) {
            this.gx[j] = FastMath.tanh(x[j] * this.alpha);
            temp += this.alpha * (1 - Math.pow(this.gx[j], 2));
        }
        this.g_x = temp/this.gx.length;
    }

    private void exp_(double[] x) {
        this.gx = new double[x.length];
        double temp = 0.0;
        for (int j=0; j<this.gx.length; j++) {
            double exp = FastMath.exp((0 - Math.pow(x[j], 2))/2.0);
            this.gx[j] = x[j] * exp;
            temp += (1 - Math.pow(x[j], 2)) * exp;
        }
        this.g_x = temp/this.gx.length;
    }

    private void cube_(double[] x) {
        this.gx = new double[x.length];
        double temp = 0.0;
        for (int j=0; j<this.gx.length; j++) {
            this.gx[j] = Math.pow(x[j], 3);
            temp += 3 * Math.pow(x[j], 2);
        }
        this.g_x = temp/this.gx.length;
    }

    /**
     * This constructor initialises the prerequisites required to use ICA.
     * @param signal Multi-dimensional signal to be transformed. Dimension 1: Samples, Dimension 2: Channels
     * @param func The functional form of the G function used in the approximation to neg-entropy. Can be "logcosh", "exp" or "cube".
     * @param whiten Specifies the whitening strategy. Can be one of "unit-variance", "arbitrary-variance" or empty string.
     * @param w_init Initial un-mixing array. Defaults to values drawn from a normal distribution.
     * @param max_iter Maximum number of iterations during fit.
     * @param tol A positive scalar giving the tolerance at which the un-mixing matrix is considered to have converged. Defaults to 1E-4.
     * @param alpha G Function argument - only used in case of logcosh
     */
    public ICA(double[][] signal, String func, String whiten, double[][] w_init, int max_iter, double tol, double alpha) {
        this.signal = signal;
        this.components = this.signal[0].length;
        if ((w_init.length != w_init[0].length) || (w_init.length != this.components)) {
            throw new IllegalArgumentException("w_init should be a square matrix and the shape should be same as the number of components in signal");
        }
        if (!func.equals("logcosh") && !func.equals("exp") && !func.equals("cube")) {
            throw new IllegalArgumentException("func should be one of logcosh, exp or cube");
        }
        if (func.equals("logcosh")) {
            if ((alpha > 2) || (alpha < 1)) {
                throw new IllegalArgumentException("alpha should be between 1 and 2");
            }
        }
        if (!whiten.equals("unit-variance") && !whiten.equals("arbitrary-variance") && !whiten.isEmpty()) {
            throw new IllegalArgumentException("whiten must be one of \"unit-variance\", \"arbitrary-variance\" or an empty string. ");
        }
        this.func = func;
        this.whiten = whiten;
        this.w_init = w_init;
        this.max_iter = max_iter;
        this.tol = tol;
        this.alpha = alpha;
    }

    /**
     * This constructor initialises the prerequisites required to use ICA.
     * @param signal Multi-dimensional signal to be transformed. Dimension 1: Samples, Dimension 2: Channels
     * @param func The functional form of the G function used in the approximation to neg-entropy. Can be "logcosh", "exp" or "cube".
     * @param whiten Specifies the whitening strategy. Can be one of "unit-variance", "arbitrary-variance" or empty string.
     * @param max_iter Maximum number of iterations during fit.
     * @param alpha G Function argument - only used in case of logcosh.
     * @param random_state Random seed to initialise w_init.
     */
    public ICA(double[][] signal, String func, String whiten, int max_iter, double alpha, long random_state) {
        this.signal = signal;
        this.components = this.signal[0].length;
        if (!func.equals("logcosh") && !func.equals("exp") && !func.equals("cube")) {
            throw new IllegalArgumentException("func should be one of logcosh, exp or cube");
        }
        if (func.equals("logcosh")) {
            if ((alpha > 2) || (alpha < 1)) {
                throw new IllegalArgumentException("alpha should be between 1 and 2");
            }
        }
        if (!whiten.equals("unit-variance") && !whiten.equals("arbitrary-variance") && !whiten.isEmpty()) {
            throw new IllegalArgumentException("whiten must be one of \"unit-variance\", \"arbitrary-variance\" or an empty string. ");
        }
        this.func = func;
        this.whiten = whiten;
        this.seed = random_state;
        this.max_iter = max_iter;
        Random r1 = new Random(this.seed, new int[] {this.components, this.components});
        this.w_init = r1.randomNormal2D();
    }

    /**
     * This constructor initialises the prerequisites required to use ICA.
     * @param signal Multi-dimensional signal to be transformed. Dimension 1: Samples, Dimension 2: Channels
     * @param func The functional form of the G function used in the approximation to neg-entropy. Can be "logcosh", "exp" or "cube".
     * @param alpha G Function argument - only used in case of logcosh.
     * @param random_state Random seed to initialise w_init.
     */
    public ICA(double[][] signal, String func, double alpha, long random_state) {
        this.signal = signal;
        this.components = this.signal[0].length;
        if (!func.equals("logcosh") && !func.equals("exp") && !func.equals("cube")) {
            throw new IllegalArgumentException("func should be one of logcosh, exp or cube");
        }
        if (func.equals("logcosh")) {
            if ((alpha > 2) || (alpha < 1)) {
                throw new IllegalArgumentException("alpha should be between 1 and 2");
            }
        }
        this.func = func;
        this.alpha = alpha;
        this.seed = random_state;
        Random r1 = new Random(this.seed, new int[] {this.components, this.components});
        this.w_init = r1.randomNormal2D();
    }

    /**
     * This constructor initialises the prerequisites required to use ICA.
     * @param signal Multi-dimensional signal to be transformed. Dimension 1: Samples, Dimension 2: Channels
     * @param func The functional form of the G function used in the approximation to neg-entropy. Can be "logcosh", "exp" or "cube".
     * @param alpha G Function argument - only used in case of logcosh.
     */
    public ICA(double[][] signal, String func, double alpha) {
        this.signal = signal;
        this.components = this.signal[0].length;
        if (!func.equals("logcosh") && !func.equals("exp") && !func.equals("cube")) {
            throw new IllegalArgumentException("func should be one of logcosh, exp or cube");
        }
        if (func.equals("logcosh")) {
            if ((alpha > 2) || (alpha < 1)) {
                throw new IllegalArgumentException("alpha should be between 1 and 2");
            }
        }
        this.func = func;
        this.alpha = alpha;
        Random r1 = new Random(this.seed, new int[] {this.components, this.components});
        this.w_init = r1.randomNormal2D();
    }

    /**
     * This constructor initialises the prerequisites required to use ICA.
     * @param signal Multi-dimensional signal to be transformed. Dimension 1: Samples, Dimension 2: Channels
     * @param func The functional form of the G function used in the approximation to neg-entropy. Can be "logcosh", "exp" or "cube". For logcosh, alpha is set to 1.
     */
    public ICA(double[][] signal, String func) {
        this.signal = signal;
        this.components = this.signal[0].length;
        if (!func.equals("logcosh") && !func.equals("exp") && !func.equals("cube")) {
            throw new IllegalArgumentException("func should be one of logcosh, exp or cube");
        }
        this.gx = new double[this.signal.length];
        this.g_x = 0;
        this.func = func;
        Random r1 = new Random(this.seed, new int[] {this.components, this.components});
        this.w_init = r1.randomNormal2D();
    }

    /**
     * This constructor initialises the prerequisites required to use ICA.
     * @param signal Multi-dimensional signal to be transformed. Dimension 1: Samples, Dimension 2: Channels
     */
    public ICA(double[][] signal) {
        this.signal = signal;
        this.components = this.signal[0].length;
        Random r1 = new Random(this.seed, new int[] {this.components, this.components});
        this.w_init = r1.randomNormal2D();
    }

    public void fit() {
        double n_samples = this.signal.length;
        double[][] sigT = UtilMethods.transpose(this.signal);
        double[][] X1;

        if (!this.whiten.isEmpty()) {
            this.zm_signal = UtilMethods.transpose(this.signal);
            this.mean_ = new double[sigT.length];
            for (int i=0; i<sigT.length; i++) {
                this.mean_[i] = StatUtils.mean(sigT[i]);
                this.zm_signal[i] = UtilMethods.zeroCenter(sigT[i]);
            }


            RealMatrix m = MatrixUtils.createRealMatrix(this.zm_signal);
            SingularValueDecomposition svdM = new SingularValueDecomposition(m);

            double[][] U = svdM.getU().getData();
            double[][] S = svdM.getS().getData();
            double[][] V = svdM.getVT().getData();

            double[][][] temp2 = this.svdFlip(U, V);
            U = temp2[0];

            for (int i=0; i<U.length; i++) {
                U[i] = MathArrays.ebeMultiply(U[i], UtilMethods.sign(U[0]));
            }

            for (int i=0; i<S.length; i++) {
                for (int j=0; j<S.length; j++) {
                    S[i][j] = S[j][j];
                }
            }

            double[][] K = UtilMethods.ebeDivide(MatrixUtils.createRealMatrix(U), MatrixUtils.createRealMatrix(S)).getData();
            K = UtilMethods.transpose(K);
            X1 = UtilMethods.matrixMultiply(K, this.zm_signal);
            for (int i=0; i<X1.length; i++) {
                X1[i] = UtilMethods.scalarArithmetic(X1[i], Math.sqrt(n_samples), "mul");
            }
        }
        else {
            X1 = sigT;
        }
        double[][] W = this.icaDef(X1, this.func, this.max_iter, this.w_init);

    }

    private double[] _gs_decorrelation(double[] w, double[][] W, int j) {
        double[][] sub_W = UtilMethods.subarray(W, j, W.length);
        if (j == 0) {
            sub_W = new double[w.length][w.length];
            for (double[] row: sub_W)
                Arrays.fill(row, 0);
        }
        else {
            sub_W = UtilMethods.transpose(UtilMethods.matrixMultiply(UtilMethods.transpose(sub_W), sub_W));
        }

        double[] _w = new double[w.length];
        for (int i=0; i<_w.length; i++) {
            _w[i] = StatUtils.sum(MathArrays.ebeMultiply(sub_W[i], w));
        }


        w = MathArrays.ebeSubtract(w, _w);
        return w;
    }


    private double[][] icaDef(double[][] X, String function, int max_iterations, double[][] w_init) {
        double[][] W = new double[this.components][this.components];
        for (double[] doubles : W) {
            Arrays.fill(doubles, 0.0);
        }

        for (int j=0; j<this.components; j++) {
            double[] w = w_init[j];
            double divisor = Math.sqrt(StatUtils.sum(UtilMethods.scalarArithmetic(w, 2, "pow")));
            w = UtilMethods.scalarArithmetic(w, divisor, "div");
            for (int i=0; i<max_iterations; i++) {
                double[] wX = UtilMethods.flattenMatrix(UtilMethods.matrixMultiply(new double[][]{w}, X));
                if (function.equals("logcosh")) {
                    this.logcosh_(wX);
                } else if (function.equals("cube")) {
                    this.cube_(wX);
                } else {
                    this.exp_(wX);
                }

                double[] w1 = new double[X.length];
                for (int h=0; h<w1.length; h++) {
                    w1[h] = StatUtils.mean(MathArrays.ebeMultiply(X[h], this.gx));
                }
                w1 = MathArrays.ebeSubtract(w1, UtilMethods.scalarArithmetic(w, this.g_x, "mul"));
                w1 = this._gs_decorrelation(w1, W, j);
                double divisor2 = Math.sqrt(StatUtils.sum(UtilMethods.scalarArithmetic(w1, 2, "pow")));
                w1 = UtilMethods.scalarArithmetic(w1, divisor2, "div");

                double lim = Math.abs(Math.abs(StatUtils.sum(MathArrays.ebeMultiply(w1, w))) - 1);
                w = w1;

                if (lim < this.tol) {
                    break;
                }
            }
            W[j] = w;
        }
        System.out.println("W Matrix");
        for (double[] doubles : W) {
            System.out.println(Arrays.toString(doubles));
        }
        System.out.println();
        System.out.println();
        return W;
    }


    // Flip eigenvectors' sign to enforce deterministic output
    // Use reference to understand: https://prod-ng.sandia.gov/techlib-noauth/access-control.cgi/2007/076422.pdf
    private double[][][] svdFlip(double[][] U, double[][] V) {
        double[][] U_new = UtilMethods.absoluteArray(U);
        int[] max_abs_cols = new int[U[0].length];
        double[] signs = new double[U[0].length];

        for (int j=0; j<U_new[0].length; j++) {
            double[] column_vals = new double[U_new.length];
            for(int i=0; i<U_new.length; i++) {
                column_vals[i] = U_new[i][j];
            }
            max_abs_cols[j] = UtilMethods.argmax(column_vals, false);
        }

        for (int i=0; i< max_abs_cols.length; i++) {
            signs[i] = Math.signum(U[max_abs_cols[i]][i]);
        }

        for (int i=0; i<U.length; i++) {
            U[i] = MathArrays.ebeMultiply(U[i], signs);
        }

        V = UtilMethods.transpose(V);
        for (int i=0; i<V.length; i++) {
            V[i] = MathArrays.ebeMultiply(V[i], signs);
        }
        V = UtilMethods.transpose(V);

        double[][][] out = {U, V};
        return out;
    }

}
