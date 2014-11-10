package org.glotaran.jfreechartcustom;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.renderer.GrayPaintScale;

/**
 *
 * @author Sergey
 */
public class GrayPaintScalePlus extends GrayPaintScale {

    /** The lower bound. */
    private double lowerBound;
    /** The upper bound. */
    private double upperBound;
    /** Value which will be colored in green color instead of grayscale; */
    private double additionValue;

    public GrayPaintScalePlus() {
        super(0.0, 1.0);
        this.additionValue = -1;
    }

    public GrayPaintScalePlus(double lowerBound, double upperBound, double additionvalue)
            throws IllegalArgumentException {
        super(lowerBound, upperBound);
        this.additionValue = additionvalue;
    }

    public double GetAdditionValue() {
        return this.additionValue;
    }

    @Override
    public Paint getPaint(double value) {
        if (value != this.additionValue) {
            return super.getPaint(value);
        } else {
            return Color.green;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GrayPaintScalePlus)) {
            return false;
        }
        GrayPaintScalePlus that = (GrayPaintScalePlus) obj;
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (this.additionValue != that.additionValue) {
            return false;
        }
        return true;
    }
}
