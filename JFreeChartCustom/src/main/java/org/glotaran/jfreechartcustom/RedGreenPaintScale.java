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
public class RedGreenPaintScale extends GrayPaintScale {

    /** The lower bound. */
    private double lowerBound;
    /** The upper bound. */
    private double upperBound;
    /** if true than blue is a color for lower bound else
     * red is a color for lower bound  */
    private boolean colormode;
    /** if true than values from out of bounds will be black*/
    private boolean mode;

    public RedGreenPaintScale() {
        super(0.0, 1.0);
        this.lowerBound = 0.0;
        this.upperBound = 1.0;
        this.colormode = true;
        this.mode = true;
    }

    public RedGreenPaintScale(double lowerBound, double upperBound)
            throws IllegalArgumentException {
        super(lowerBound, upperBound);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.colormode = true;
        this.mode = true;

    }

    public RedGreenPaintScale(double lowerBound, double upperBound, boolean colormode, boolean mode)
            throws IllegalArgumentException {
        super(lowerBound, upperBound);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.colormode = colormode;
        this.mode = mode;
    }

    @Override
    public Paint getPaint(double value) {
        float lum;
        if (((value < this.lowerBound) || (value > this.upperBound)) && mode) {
            return Color.black;
        } else {
            value = Math.max(value, this.lowerBound);
            value = Math.min(value, this.upperBound);

            if (colormode) {
                if (value <= 0){
                    lum = (float) (1-((value - this.lowerBound) / (-this.lowerBound)) );
                    return new Color(Color.HSBtoRGB((float) 0.33333, lum, (float) 1.0));
                } else {
                    lum = (float) (1-((this.upperBound - value) / this.upperBound) );
                    return new Color(Color.HSBtoRGB((float) 0.0, lum, (float) 1.0));
                } 
            } else {
                if (value <= 0){
                    lum = (float) (1-((value - this.lowerBound) / (-this.lowerBound)) );
                    return new Color(Color.HSBtoRGB((float) 0.0, lum, (float) 1.0));
                } else {
                    lum = (float) (1-((this.upperBound - value) / this.upperBound) );
                    return new Color(Color.HSBtoRGB((float) 0.33333, lum, (float) 1.0));
                } 
//                lum = (float) ((value - this.lowerBound) / (this.upperBound - this.lowerBound) * 0.666667);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RedGreenPaintScale)) {
            return false;
        }
        RedGreenPaintScale that = (RedGreenPaintScale) obj;
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
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.lowerBound) ^ (Double.doubleToLongBits(this.lowerBound) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.upperBound) ^ (Double.doubleToLongBits(this.upperBound) >>> 32));
        return hash;
    }
}
