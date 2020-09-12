package com.github.psambit9791.jdsp.windows;

import java.util.Arrays;

/**
 * <h1>BoxCar Window</h1>
 * Also known as a rectangular window or Dirichlet window, this is equivalent to no window at all.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */
public class BoxCar extends _Window {

    double[] window;
    boolean sym;
    int len;

    /**
     * This constructor initialises the BoxCar class.
     * @throws java.lang.IllegalArgumentException if window length is less than 1
     * @param len Length of the window
     * @param sym Whether the window is symmetric
     */
    public BoxCar(int len, boolean sym) throws IllegalArgumentException {
        this.sym = sym;
        this.len = len;
        if (lenGuard(len)) {
            throw new IllegalArgumentException("Window Length must be greater than 0");
        }
    }

    /**
     * This constructor initialises the BoxCar class.
     * @throws java.lang.IllegalArgumentException if window length is less than 1. Symmetricity is set to True.
     * @param len Length of the window
     */
    public BoxCar(int len) throws IllegalArgumentException {
        this.sym = true;
        this.len = len;
        if (lenGuard(len)) {
            throw new IllegalArgumentException("Window Length must be greater than 0");
        }
    }

    /**
     * Generates and returns the BoxCar Window
     * @return double[] the generated window
     */
    public double[] getWindow() {
        int tempLen = super.extend(this.len, this.sym);
        this.window = new double[tempLen];
        Arrays.fill(this.window, 1);
        this.window = super.truncate(this.window);
        return this.window;
    }
}