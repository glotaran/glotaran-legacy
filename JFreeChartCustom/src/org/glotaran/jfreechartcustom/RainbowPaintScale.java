/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.renderer.GrayPaintScale;

/**
 * @author Sergey
 * for presenting values in colorscale from red to blue 
 * values from out of bounds will be black in case of mode true
 * default is true 
 * colormode define a color for min bound
 * default is lower - blue;
 *  
 */
public class RainbowPaintScale extends GrayPaintScale {

    /** The lower bound. */
    private double lowerBound;
    /** The upper bound. */
    private double upperBound;
    /** if true than blue is a color for lower bound else
     * red is a color for lower bound  */
    private boolean colormode;
    /** if true than values from out of bounds will be black*/
    private boolean mode;

    public RainbowPaintScale() {
        super(0.0, 1.0);
        this.lowerBound = 0.0;
        this.upperBound = 1.0;
        this.colormode = true;
        this.mode = true;
    }

    public RainbowPaintScale(double lowerBound, double upperBound)
            throws IllegalArgumentException {
        super(lowerBound, upperBound);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.colormode = true;
        this.mode = true;

    }

    public RainbowPaintScale(double lowerBound, double upperBound, boolean colormode, boolean mode)
            throws IllegalArgumentException {
        super(lowerBound, upperBound);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.colormode = colormode;
        this.mode = mode;
    }

    @Override
    public Paint getPaint(double value) {
        float hue;
        if (((value < this.lowerBound) || (value > this.upperBound)) && mode) {
            return Color.black;
        } else {
            value = Math.max(value, this.lowerBound);
            value = Math.min(value, this.upperBound);

            if (colormode) {
                hue = (float) ((this.upperBound - value) / (this.upperBound - this.lowerBound) * 0.666667);
            } else {
                hue = (float) ((value - this.lowerBound) / (this.upperBound - this.lowerBound) * 0.666667);
            }
        }
        return new Color(Color.HSBtoRGB(hue, (float) 1.0, (float) 1.0));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RainbowPaintScale)) {
            return false;
        }
        RainbowPaintScale that = (RainbowPaintScale) obj;
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.lowerBound) ^ (Double.doubleToLongBits(this.lowerBound) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.upperBound) ^ (Double.doubleToLongBits(this.upperBound) >>> 32));
        return hash;
    }
}
