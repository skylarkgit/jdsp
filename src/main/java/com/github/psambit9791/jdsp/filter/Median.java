package com.github.psambit9791.jdsp.filter;

import com.github.psambit9791.jdsp.UtilMethods;
import org.apache.commons.math3.stat.StatUtils;

import java.util.Arrays;

/**
 * <h1>Median Filter</h1>
 * The Median class implements median filter which can be applied on the input signal using a window based on the kernel size.
 * Use: Unlike an averaging filter, he median filter can preserve sharp edges while completely suppressing isolated out-of-range noise.
 * Reference <a href="http://fourier.eng.hmc.edu/e161/lectures/smooth_sharpen/node2.html">article</a> for more information on 1-D Median Filter.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */

public class Median {

    private double[] signal;
    private int windowSize;

    /**
     * This constructor initialises the prerequisites
     * required to use Median filter.
     * @param s Signal to be filtered
     */
    public Median(double[] s) {
        if (3 >= s.length) {
            throw new IllegalArgumentException("Signal Length has to be greater than 3.");
        }
        this.signal = s;
        this.windowSize = 3;
    }

    /**
     * This constructor initialises the prerequisites
     * required to use Bessel filter.
     * @param s Signal to be filtered
     * @param wsize Window or kernel size
     */
    public Median(double[] s, int wsize) {
        if (wsize >= s.length) {
            throw new IllegalArgumentException("Window size cannot be greater than or equal to signal length");
        }
        this.signal = s;
        this.windowSize = wsize;
    }

    /**
     * This method implements a median filter with given parameters, applies it on the signal and returns it.
     * @return double[] Filtered signal
     */
    public double[] median_filter() {
        int paddingSize = (this.windowSize - 1)/2;
        double[] cons = new double[paddingSize];
        double[] newSignal = new double[this.signal.length];
        Arrays.fill(cons, 0);
        double[] paddedSignal = {};
        paddedSignal = UtilMethods.concatenateArray(paddedSignal, cons);
        paddedSignal = UtilMethods.concatenateArray(paddedSignal, this.signal);
        paddedSignal = UtilMethods.concatenateArray(paddedSignal, cons);
        for (int i = 0; i<this.signal.length; i++) {
            newSignal[i] = StatUtils.percentile(paddedSignal, i, this.windowSize, 50);
        }
        return newSignal;
    }

}