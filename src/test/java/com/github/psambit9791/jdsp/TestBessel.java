/*
 * Copyright (c) 2020 Sambit Paul
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.psambit9791.jdsp;

import com.github.psambit9791.jdsp.filter.Bessel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBessel {
    // 5Hz Sine + 15Hz Sine + 30Hz Sine sampled @ 100Hz with Nyquist of 50Hz
    private double[] signal1 = {0.0, 2.069, 0.951, 0.53, 1.314, 0.0, -0.588, 1.706, 2.127, 0.167, -0.0, -0.167, -2.127,
            -1.706, 0.588, 0.0, -1.314, -0.53, -0.951, -2.069, -0.0, 2.069, 0.951, 0.53, 1.314, 0.0, -0.588, 1.706,
            2.127, 0.167, -0.0,-0.167, -2.127, -1.706, 0.588, -0.0, -1.314, -0.53, -0.951, -2.069, -0.0, 2.069, 0.951,
            0.53, 1.314, 0.0,-0.588, 1.706, 2.127, 0.167, -0.0, -0.167, -2.127, -1.706, 0.588, 0.0, -1.314, -0.53,
            -0.951, -2.069, -0.0, 2.069, 0.951, 0.53, 1.314, 0.0, -0.588, 1.706, 2.127, 0.167, -0.0, -0.167, -2.127,
            -1.706, 0.588, 0.0, -1.314, -0.53, -0.951, -2.069, -0.0, 2.069, 0.951, 0.53, 1.314, 0.0, -0.588, 1.706,
            2.127, 0.167, -0.0, -0.167, -2.127, -1.706, 0.588, 0.0, -1.314, -0.53, -0.951, -2.069};

    // 2Hz Sine + 20Hz Sine + 45Hz Sine sampled @ 100Hz with Nyquist of 50Hz
    private double[] signal2 = {0.0, 1.385, 0.249, 0.589, -1.42, 1.588, 0.685, 2.167, -0.331, 0.263, 0.951, 1.624, 2.174,
            -0.399, 0.982, -0.049, 2.807, 0.623, 0.771, -0.576, 0.588, 1.742, 0.368, 0.47, -1.777, 1.0, -0.125, 1.148,
            -1.544, -1.124, -0.588, -0.043, 0.405, -2.241, -0.905, -1.951, 0.92, -1.219, -0.998, -2.242, -0.951, 0.355,
            -0.844, -0.549, -2.587, 0.412, -0.482, 1.029, -1.424, -0.767, -0.0, 0.767, 1.424, -1.029, 0.482, -0.412,
            2.587, 0.549, 0.844, -0.355, 0.951, 2.242, 0.998, 1.219, -0.92, 1.951, 0.905, 2.241, -0.405, 0.043, 0.588,
            1.124, 1.544, -1.148, 0.125, -1.0, 1.777, -0.47, -0.368, -1.742, -0.588, 0.576, -0.771, -0.623, -2.807,
            0.049, -0.982, 0.399, -2.174, -1.624, -0.951, -0.263, 0.331, -2.167, -0.685, -1.588, 1.42, -0.589, -0.249,
            -1.385};

    private Bessel flt1 = new Bessel(this.signal1, 100);
    private Bessel flt2 = new Bessel(this.signal2, 100);

    @Test
    public void LowPassTest1() {
        final double[] out = {0.0, 0.14, 0.647, 1.197, 1.197, 0.857, 0.553, 0.238, 0.368, 1.129, 1.413, 0.677, -0.235,
                -0.925, -1.4, -1.104, -0.285, -0.141, -0.676, -1.039, -1.164, -0.92, 0.137, 1.228, 1.309, 0.857, 0.527,
                0.238, 0.374, 1.129, 1.411, 0.677, -0.234, -0.925, -1.4, -1.104, -0.285, -0.141, -0.676, -1.039, -1.164,
                -0.92, 0.137, 1.228, 1.309, 0.857, 0.527, 0.238, 0.374, 1.129, 1.411, 0.677, -0.234, -0.925, -1.4,
                -1.104, -0.285, -0.141, -0.676, -1.039, -1.164, -0.92, 0.137, 1.228, 1.309, 0.857, 0.527, 0.238, 0.374,
                1.129, 1.411, 0.677, -0.234,-0.925, -1.4, -1.104, -0.285, -0.141, -0.676, -1.039, -1.164, -0.92, 0.137,
                1.228, 1.309, 0.857, 0.527, 0.238, 0.374, 1.129, 1.411, 0.677, -0.234, -0.925, -1.4, -1.104, -0.285,
                -0.141, -0.676, -1.039};

        double[] result = flt1.low_pass_filter(4, 9);
        Assertions.assertArrayEquals(result, out, 0.001);
    }

    @Test
    public void LowPassTest2() {
        final double[] out = {0.0, 0.022, 0.123, 0.312, 0.463, 0.43, 0.282, 0.271, 0.51, 0.801, 0.88, 0.765, 0.72, 0.892,
                1.108, 1.114, 0.926, 0.808, 0.905, 1.044, 0.975, 0.713, 0.524, 0.555, 0.635, 0.512, 0.204, -0.023,
                -0.022, 0.037, -0.097, -0.407, -0.626, -0.607, -0.521, -0.62, -0.885, -1.053, -0.976, -0.826, -0.856,
                -1.049, -1.142, -0.988, -0.762, -0.716, -0.836, -0.858, -0.639, -0.353, -0.253, -0.327, -0.311, -0.062,
                0.245, 0.355, 0.284, 0.292, 0.523, 0.803, 0.878, 0.762, 0.719, 0.892, 1.108, 1.114, 0.926, 0.808, 0.904,
                1.044, 0.975, 0.713, 0.524, 0.556, 0.635, 0.512, 0.204, -0.023, -0.022, 0.037, -0.097, -0.407, -0.626,
                -0.607, -0.521, -0.62, -0.885, -1.053, -0.976, -0.826, -0.856, -1.049, -1.142, -0.988, -0.762, -0.716,
                -0.836, -0.859, -0.639, -0.353};

        double[] result = flt2.low_pass_filter(4, 5);
        Assertions.assertArrayEquals(result, out, 0.001);
    }

    @Test
    public void HighPassTest1() {
        final double[] out = {0.0, 0.64, -1.065, -0.108, 0.654, -0.411, 0.191, 1.11, -0.604, -1.312, 0.45, 0.512,
                -0.352, 0.776, 0.761,-1.248, -0.878, 0.734, 0.044, -0.187, 1.087, 0.094, -1.518, -0.206, 0.761,
                -0.267, 0.286, 1.142, -0.614, -1.338, 0.425, 0.496, -0.358, 0.775, 0.764, -1.245, -0.876, 0.735, 0.044,
                -0.187, 1.087, 0.094, -1.518, -0.206, 0.761, -0.267, 0.286, 1.142, -0.614, -1.338, 0.425, 0.496, -0.358,
                0.775, 0.764, -1.245, -0.876, 0.735, 0.044, -0.187, 1.087, 0.094, -1.518, -0.206, 0.761, -0.267, 0.286,
                1.142, -0.614, -1.338, 0.425, 0.496, -0.358, 0.775, 0.764, -1.245, -0.876, 0.735, 0.044, -0.187, 1.087,
                0.094, -1.518, -0.206, 0.761, -0.267,0.286, 1.142, -0.614, -1.338, 0.425, 0.496, -0.358, 0.775, 0.764,
                -1.245, -0.876, 0.735, 0.044, -0.187};

        double[] result = flt1.high_pass_filter(4, 29);
        Assertions.assertArrayEquals(result, out, 0.001);
    }

    @Test
    public void HighPassTest2() {
        final double[] out = {0.0, 0.118, -0.438, 0.597, -0.493, 0.749, -1.182, 0.981, -0.674, 0.821, -0.596, -0.028,
                0.152, -0.226, 0.798, -1.004, 0.771, -0.96, 1.089, -0.593, 0.325, -0.367, -0.124, 0.638, -0.57, 0.733,
                -1.166, 0.987, -0.677, 0.82, -0.596, -0.028, 0.152, -0.226, 0.797, -1.004, 0.771, -0.96, 1.089, -0.593,
                0.325, -0.367, -0.124, 0.638, -0.57, 0.733, -1.166, 0.987, -0.677, 0.82, -0.596, -0.028, 0.152, -0.226,
                0.798, -1.004, 0.771, -0.96, 1.089, -0.593, 0.325, -0.367, -0.124, 0.638, -0.57, 0.733, -1.166, 0.987,
                -0.677, 0.82, -0.596, -0.028, 0.152, -0.226, 0.798, -1.004, 0.771, -0.96, 1.089, -0.593, 0.325, -0.367,
                -0.125, 0.638, -0.57, 0.733, -1.166, 0.987, -0.677, 0.82, -0.596, -0.028, 0.152, -0.226, 0.797, -1.004,
                0.771, -0.96, 1.089, -0.592};

        double[] result = flt2.high_pass_filter(4, 40);
        Assertions.assertArrayEquals(result, out, 0.001);
    }

    @Test
    public void BandPassTest1() {
        final double[] out = {0.0, 0.053, 0.186, 0.12, -0.312, -0.563, -0.248, 0.219, 0.589, 0.734, 0.149, -0.822,
                -0.94, -0.138, 0.608, 0.95, 0.755, -0.208, -1.106, -0.9, -0.043, 0.591, 0.843, 0.446, -0.585, -1.117,
                -0.534, 0.334, 0.854, 0.934, 0.249, -0.791, -0.964, -0.2, 0.537, 0.895, 0.722, -0.22, -1.102, -0.885,
                -0.025, 0.607, 0.855, 0.452,-0.584, -1.119, -0.538, 0.329, 0.85, 0.932, 0.248, -0.791, -0.964, -0.199,
                0.538, 0.896, 0.723, -0.22, -1.102, -0.886, -0.025, 0.607, 0.854, 0.451, -0.584, -1.119, -0.538, 0.329,
                0.85, 0.932, 0.248, -0.791, -0.964, -0.199, 0.538, 0.896, 0.723, -0.22, -1.102, -0.886, -0.025, 0.607,
                0.854, 0.451, -0.584, -1.119, -0.538, 0.329, 0.85, 0.932, 0.248, -0.791, -0.964, -0.199, 0.538, 0.896,
                0.723, -0.22, -1.102, -0.886};

        double[] result = flt1.band_pass_filter(4, 12, 18);
        Assertions.assertArrayEquals(result, out, 0.001);
    }

    @Test
    public void BandPassTest2() {
        final double[] out = {0.0, 0.34, 0.31, -0.547, -0.828, 0.007, 1.062, 0.546, -0.647, -0.928, -0.091, 0.99, 0.468,
                -0.583, -0.983, 0.028, 0.983, 0.553, -0.538, -0.97, 0.109, 0.934, 0.643, -0.615, -0.9, 0.043, 0.962,
                0.615, -0.638, -0.881, -0.023, 1.017, 0.525, -0.571, -0.963, 0.023, 0.968, 0.531, -0.572, -1.005, 0.068,
                0.892, 0.601, -0.656, -0.939, 0.005, 0.927, 0.583, -0.666, -0.907, -0.047, 0.997, 0.509, -0.584, -0.972,
                0.018, 0.967, 0.533, -0.567, -0.996, 0.081, 0.908, 0.62, -0.634, -0.915, 0.033, 0.956, 0.612, -0.636,
                -0.876, -0.015, 1.028, 0.538, -0.556, -0.947, 0.041, 0.987, 0.551, -0.552, -0.985, 0.088, 0.911, 0.619,
                -0.639, -0.923, 0.02, 0.94, 0.593, -0.658, -0.901, -0.042, 0.999, 0.508, -0.587, -0.978, 0.01, 0.957,
                0.521, -0.58, -1.011};

        double[] result = flt2.band_pass_filter(4, 12, 30);
        Assertions.assertArrayEquals(result, out, 0.001);
    }

    @Test
    public void BandStopTest1() {
        final double[] out = {0.0, 0.994, -0.333, 0.726, 1.576, 0.277, 0.591, 1.782, 0.533, -0.157, 1.256, 0.711,
                -0.912, -0.044, 0.343, -1.421, -1.314, -0.099, -1.206, -1.652, 0.151, 0.133, -0.914, 0.5, 1.381, 0.06,
                0.513, 1.9, 0.723, -0.027, 1.303, 0.717, -0.931, -0.108, 0.243, -1.498, -1.31, -0.016, -1.111, -1.61,
                0.126, 0.077, -0.958, 0.485,1.389, 0.078, 0.535, 1.919, 0.732, -0.036, 1.28, 0.696, -0.938, -0.096,
                0.262, -1.485, -1.31, -0.027, -1.122, -1.616, 0.128, 0.082, -0.953, 0.488, 1.389, 0.076, 0.531, 1.916,
                0.731, -0.034, 1.283, 0.697, -0.937,-0.097, 0.261, -1.486, -1.31, -0.026, -1.121, -1.615, 0.128, 0.082,
                -0.953, 0.487, 1.389, 0.076, 0.532, 1.917, 0.731, -0.034, 1.282, 0.697, -0.937, -0.097, 0.261, -1.486,
                -1.31, -0.026, -1.121, -1.615};

        double[] result = flt1.band_stop_filter(4, 7, 28);
        Assertions.assertArrayEquals(result, out, 0.001);
    }

    @Test
    public void BandStopTest2() {
        final double[] out = {0.0, 0.756, -0.121, 1.048, -0.357, 1.404, -0.682, 1.462, 0.243, 1.482, 0.745, 0.71, 1.394,
                0.274, 1.873, 0.035, 2.012, 0.019, 1.454, 0.237, 0.874, 0.824, 0.036, 1.001, -0.701, 1.115, -0.958,
                0.766, -0.979, 0.061, -0.655, -0.754, -0.232, -1.468, 0.044, -1.91, 0.016, -1.869, -0.272, -1.441,
                -0.835, -0.806, -1.35, -0.119, -1.646, 0.305, -1.572, 0.418, -1.058, 0.22, -0.271, -0.146, 0.588,
                -0.446, 1.269, -0.493, 1.583, -0.198, 1.482, 0.396, 1.067, 1.11, 0.53, 1.702, 0.107, 1.966, -0.039,
                1.788, 0.131, 1.216, 0.519, 0.423, 0.924, -0.349, 1.126, -0.872, 0.974, -1.026, 0.447, -0.832, -0.335,
                -0.441, -1.146, -0.071, -1.739, 0.077, -1.94, -0.094, -1.704, -0.542, -1.136, -1.102, -0.446, -1.543,
                0.126, -1.661, 0.408, -1.361, 0.349, -0.69};

        double[] result = flt2.band_stop_filter(4, 12, 30);
        Assertions.assertArrayEquals(result, out, 0.001);
    }
}
