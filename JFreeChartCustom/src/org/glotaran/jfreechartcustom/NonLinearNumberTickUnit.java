/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import java.io.Serializable;
import java.text.NumberFormat;
import org.jfree.chart.axis.NumberTickUnit;

/**
 * A numerical tick unit for a non-linear scale, i.e. where a value on the axis is represented by an integer index
 * such as an pixel, but the actual value for that pixel is some other value that relates in a non-linear way.
 */
public class NonLinearNumberTickUnit extends NumberTickUnit implements Serializable {
    
    private double[] indexToValue;
    private boolean inverted;

    /** For serialization. */
    private static final long serialVersionUID = 1249459506627654442L;

    /** A formatter for the tick unit. */
    private NumberFormat formatter;

    /**
     * Creates a new number tick unit.
     *
     * @param size  the size of the tick unit.
     */
    public NonLinearNumberTickUnit(double size,double[] indexToValue) {
        this(size, NumberFormat.getNumberInstance(),indexToValue,false);
    }
    
        /**
     * Creates a new number tick unit.
     *
     * @param size  the size of the tick unit.
     */
    public NonLinearNumberTickUnit(double size,double[] indexToValue,boolean inverted) {
        this(size, NumberFormat.getNumberInstance(),indexToValue,inverted);
    }

    /**
     * Creates a new number tick unit.
     *
     * @param size  the size of the tick unit.
     * @param formatter  a number formatter for the tick unit (<code>null</code>
     *                   not permitted).
     */
    public NonLinearNumberTickUnit(double size, NumberFormat formatter,double[] indexToValue, boolean inverted) {
        super(size);
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        this.formatter = formatter;
        this.indexToValue = indexToValue;
        this.inverted = inverted;
    }
    
        /**
     * Creates a new number tick unit.
     *
     * @param size  the size of the tick unit.
     * @param formatter  a number formatter for the tick unit (<code>null</code>
     *                   not permitted).
     */
    public NonLinearNumberTickUnit(double size, NumberFormat formatter,double[] indexToValue) {
        super(size);
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        this.formatter = formatter;
        this.indexToValue = indexToValue;
    }

    /**
     * Creates a new number tick unit.
     *
     * @param size  the size of the tick unit.
     * @param formatter  a number formatter for the tick unit (<code>null</code>
     *                   not permitted).
     * @param minorTickCount  the number of minor ticks.
     *
     * @since 1.0.7
     */
    public NonLinearNumberTickUnit(double size, NumberFormat formatter,
            int minorTickCount,double[] indexToValue, boolean inverted) {
        super(size, formatter, minorTickCount);
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        this.formatter = formatter;
        this.indexToValue = indexToValue;
        this.inverted=inverted;
    }

    /**
     * Converts a value to a string.
     *
     * @param value  the value.
     *
     * @return The formatted string.
     */
    @Override
    public String valueToString(double value) {
        String retString;
        if (indexToValue!=null) {
            if (value>=0 && value <= indexToValue.length) {
                if(inverted) {
                    if((int)value==0) {
                       return "";
                    }
                    value = indexToValue[indexToValue.length-(int)value];
                } else {
                      if((int)value==indexToValue.length) {
                       return "";
                    }
                    value = indexToValue[(int)value];
                }
            }
        }
        retString = this.formatter.format(value);
        if (retString.contains("E0")) {
                retString = retString.replace("E0", "");
        }
        return retString;
    }
    
}

