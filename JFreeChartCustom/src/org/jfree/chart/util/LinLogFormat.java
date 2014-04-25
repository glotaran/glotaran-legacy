/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2010, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * --------------
 * LogFormat.java
 * --------------
 * (C) Copyright 2010, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Joris Snellenburg (for VU University Amsterdam);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 14-Aug-2010 : initial version
 *
 */
package org.jfree.chart.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * A number formatter for linear or logarithmic values.
 * This formatter does not support parsing.
 *
 * @since 1.0.13
 */
public class LinLogFormat extends NumberFormat {

    private final static long serialVersionUID = 1L;
    /** The log base value. */
    private double base;
    /** The natural logarithm of the base value. */
    private double baseLog;
    /** The lower bound from which to format the numbers on a linear scale. */
    private double lowerLinearBound;
    /** The upper bound until which to format the numbers on a linear scale. */
    private double upperLinearBound;
    /** The label for the log base (for example, "e"). */
    private String baseLabel;
    /**
     * The label for the power symbol.
     *
     * @since 1.0.10
     */
    private String powerLabel;
    /** A flag that controls whether or not the base is shown. */
    private boolean showBase;
    /** The number formatter for the exponent. */
    private NumberFormat exponentFormatter;
    private NumberFormat DEFAULT_EXPONENT_FORMATTER = new DecimalFormat("0.####");
    /** The number formatter for the significand */
    private NumberFormat significandFormatter;
    private NumberFormat DEFAULT_SIGNIFICAND_FORMATTER = new DecimalFormat("0.####");
    //** The default minimum value to display on the linear scale **//
    private final static double MIN_LINEAR_DISPLAY_VALUE = 0.01;
    //** The minimum value to display on the linear scale **//
    private double minLinearDisplayValue;
    //** The default maximum value to display on the linear scale **//
    private final static double MAX_LINEAR_DISPLAY_VALUE = 9999.0;
    //** The minimum value to display on the linear scale **//
    private double maxLinearDisplayValue;

    /**
     * Creates a new instance using base 10.
     *
     * @since 1.0.13
     */
    public LinLogFormat() {
        this(10, "10", true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Creates a new instance.
     *
     * @param base  the base.
     * @param baseLabel  the base label (<code>null</code> not permitted).
     * @param showBase  a flag that controls whether or not the base value is
     *                  shown.
     */
    public LinLogFormat(double base, String baseLabel, boolean showBase, double leftLinearBound, double rightLinearBound) {
        this(base, baseLabel, "^", showBase, leftLinearBound, rightLinearBound);
    }
    
        /**
     * Creates a new instance.
     *
     * @param base  the base.
     * @param baseLabel  the base label (<code>null</code> not permitted).
     * @param powerLabel  the power label (<code>null</code> not permitted).
     * @param showBase  a flag that controls whether or not the base value is
     *                  shown.
     *
     * @since 1.0.10
     */
    public LinLogFormat(double base, String baseLabel, String powerLabel,
            boolean showBase, double lowerLinearBound, double upperLinearBound) {
        if (baseLabel == null) {
            throw new IllegalArgumentException("Null 'baseLabel' argument.");
        }
        if (powerLabel == null) {
            throw new IllegalArgumentException("Null 'powerLabel' argument.");
        }
        this.base = base;
        this.baseLog = Math.log(this.base);
        this.baseLabel = baseLabel;
        this.showBase = showBase;
        this.powerLabel = powerLabel;
        this.lowerLinearBound = lowerLinearBound;
        this.upperLinearBound = upperLinearBound;
        this.minLinearDisplayValue = MIN_LINEAR_DISPLAY_VALUE;
        this.maxLinearDisplayValue = MAX_LINEAR_DISPLAY_VALUE;
        this.exponentFormatter = DEFAULT_EXPONENT_FORMATTER;
        this.significandFormatter = DEFAULT_SIGNIFICAND_FORMATTER;
    }

    /**
     * Returns the number format used for the exponent.
     *
     * @return The number format (never <code>null</code>).
     *
     * @since 1.0.13.
     */
    public NumberFormat getExponentFormat() {
        return (NumberFormat) this.exponentFormatter.clone();
    }

    /**
     * Sets the number format used for the exponent.
     *
     * @param format  the formatter (<code>null</code> not permitted).
     *
     * @since 1.0.13
     */
    public void setExponentFormat(NumberFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Null 'format' argument.");
        }
        this.exponentFormatter = format;
    }
    
        /**
     * Returns the number format used for the significand.
     *
     * @return The number format (never <code>null</code>).
     *
     * @since 1.0.13.
     */
    public NumberFormat getSignificandFormat() {
        return (NumberFormat) this.significandFormatter.clone();
    }

    /**
     * Sets the number format used for the significand.
     *
     * @param format  the formatter (<code>null</code> not permitted).
     *
     * @since 1.0.13
     */
    public void setSignificandFormat(NumberFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Null 'format' argument.");
        }
        this.significandFormatter = format;
    }

    /**
     * Calculates the log of a given value.
     *
     * @param value  the value.
     *
     * @return The log of the value.
     */
    private double calculateLog(double value) {
        return Math.log(value) / this.baseLog;
    }

    public double getMaxLinearDisplayValue() {
        return maxLinearDisplayValue;
    }

    public void setMaxLinearDisplayValue(double maxLinearDisplayValue) {
        this.maxLinearDisplayValue = maxLinearDisplayValue;
    }

    public double getMinLinearDisplayValue() {
        return minLinearDisplayValue;
    }

    public void setMinLinearDisplayValue(double minLinearDisplayValue) {
        this.minLinearDisplayValue = minLinearDisplayValue;
    }

    /**
     * Returns a formatted representation of the specified number.
     *
     * @param number  the number.
     * @param toAppendTo  the string buffer to append to.
     * @param pos  the position.
     *
     * @return A string buffer containing the formatted value.
     */
    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo,
            FieldPosition pos) {
        StringBuffer result = new StringBuffer();

        if (number >= lowerLinearBound && number <= upperLinearBound) {
            if (Math.ceil(Math.abs(number)) >= minLinearDisplayValue && Math.ceil(Math.abs(number)) <= maxLinearDisplayValue || number==0) {
                result.append(this.significandFormatter.format(number));
            } else {
                if (number < 0) {
                    result.append("-");
                }
                if (this.showBase) {
                    if (this.base == 10) {
                        result.append("1");
                    }
                    result.append(this.baseLabel);
                    result.append(this.powerLabel);
                }
                result.append(this.exponentFormatter.format(calculateLog(Math.abs(number))));
            }
        } else {
                if (number < 0) {
                    result.append("-");
                }
                if (this.showBase) {
                    if (this.base == 10) {
                        result.append("1");
                    }
                    result.append(this.baseLabel);
                    result.append(this.powerLabel);
                }
                result.append(this.exponentFormatter.format(calculateLog(Math.abs(number))));    
        }
        if (toAppendTo != null) {
            return toAppendTo.append(result);
        } else {
            return result;
        }
    }

    /**
     * Formats the specified number as a hexadecimal string.  The decimal
     * fraction is ignored.
     *
     * @param number  the number to format.
     * @param toAppendTo  the buffer to append to (ignored here).
     * @param pos  the field position (ignored here).
     *
     * @return The string buffer.
     */
    public StringBuffer format(long number, StringBuffer toAppendTo,
            FieldPosition pos) {
        return format((double) number, toAppendTo, pos);
//        StringBuffer result = new StringBuffer();
//        if (this.showBase) {
//            result.append(this.baseLabel);
//            result.append("^");
//        }
//        result.append(this.exponentFormatter.format(calculateLog(number)));
//        return result;
    }

    /**
     * Parsing is not implemented, so this method always returns
     * <code>null</code>.
     *
     * @param source  ignored.
     * @param parsePosition  ignored.
     *
     * @return Always <code>null</code>.
     */
    public Number parse(String source, ParsePosition parsePosition) {
        return null; // don't bother with parsing
    }

    /**
     * Tests this formatter for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LinLogFormat)) {
            return false;
        }
        LinLogFormat that = (LinLogFormat) obj;
        if (this.base != that.base) {
            return false;
        }
        if (!this.baseLabel.equals(that.baseLabel)) {
            return false;
        }
        if (this.baseLog != that.baseLog) {
            return false;
        }
        if (this.showBase != that.showBase) {
            return false;
        }
        if (!this.exponentFormatter.equals(that.exponentFormatter)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a clone of this instance.
     *
     * @return A clone.
     */
    public Object clone() {
        LinLogFormat clone = (LinLogFormat) super.clone();
        clone.exponentFormatter = (NumberFormat) this.exponentFormatter.clone();
        clone.significandFormatter = (NumberFormat) this.significandFormatter.clone();
        return clone;
    }
}
