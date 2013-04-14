/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.util.LinLogFormat;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 * A numerical axis that uses a logarithmic scale.  The class is an
 * alternative to the {@link LogarithmicAxis} class.
 *
 * @since 1.0.7
 */
public class LinLogAxis extends ValueAxis {

    private final static long serialVersionUID = 1L;
    /** The logarithm base. */
    private double base = 10.0;
    /** The logarithm of the base value - cached for performance. */
    private double baseLog = Math.log(10.0);
    /** The current tick unit. */
    private NumberTickUnit tickUnit;
    /** The override number format. */
    private NumberFormat numberFormatOverride;
    /** The number at which the axis goes from linear to logarithmic*/
    private double leftLinearBoundValue, rightLinearBoundValue, linearRangeScalingFactor;
    private final double DEFAULT_MINIMUM_PIXELS_PER_MAJOR_TICK = 50;
    private double DEFAULT_LINEAR_RANGE_SCALING_VALUE = 0.4;
    private final double DEFAULT_LEFT_LINEAR_BOUND_VALUE = Double.NEGATIVE_INFINITY;
    private final double DEFAULT_RIGHT_LINEAR_BOUND_VALUE = Double.POSITIVE_INFINITY;
    private final double DEFAULT_CENTRAL_VALUE = 0.0;
    private double leftRange = -1, rightRange = -1, leftLinRange = -1, rightLinRange = -1, linRange = -1, totalRange = -1;
    private double centralValue;
    private double maximumLabelWidth;
    private int userSpecifiedLabelWidth = 0;

    /**
     * Creates a new <code>LogAxis</code> with no label.
     */
    public LinLogAxis() {
        this(null);
    }

    /**
     * Creates a new <code>LogAxis</code> with the given label.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public LinLogAxis(String label) {
        super(label, new StandardTickUnitSource());
        this.tickUnit = new NumberTickUnit(1.0, new DecimalFormat("0.#"), 9);
        this.centralValue = DEFAULT_CENTRAL_VALUE;
        this.leftLinearBoundValue = DEFAULT_LEFT_LINEAR_BOUND_VALUE;
        this.rightLinearBoundValue = DEFAULT_RIGHT_LINEAR_BOUND_VALUE;
        this.linearRangeScalingFactor = DEFAULT_LINEAR_RANGE_SCALING_VALUE;
    }

    /**
     * Creates a new <code>LogAxis</code> with the given label.
     *
     * @param label  the axis label (<code>null</code> permitted).
     * @param linearBoundValue number at which the axis goes from linear to logarithmic
     * (<code>null</code> permitted).
     */
    public LinLogAxis(String label, double linearBoundValue) {
        super(label, new StandardTickUnitSource());
        this.tickUnit = new NumberTickUnit(1.0, new DecimalFormat("0.#"), 9);
        this.centralValue = DEFAULT_CENTRAL_VALUE;
        if (leftLinearBoundValue < rightLinearBoundValue) {
            this.leftLinearBoundValue = -Math.abs(linearBoundValue);
            this.rightLinearBoundValue = Math.abs(linearBoundValue);
        } else {
            this.leftLinearBoundValue = DEFAULT_LEFT_LINEAR_BOUND_VALUE;
            this.rightLinearBoundValue = DEFAULT_RIGHT_LINEAR_BOUND_VALUE;
        }
        this.linearRangeScalingFactor = DEFAULT_LINEAR_RANGE_SCALING_VALUE;
    }

    /**
     * Creates a new <code>LogAxis</code> with the given label.
     *
     * @param label  the axis label (<code>null</code> permitted).
     * @param rightLinearBoundValue number at which the axis goes from linear to logarithmic
     * (<code>null</code> permitted).
     * @param leftLinearBoundValue  number at which the axis goes from logarithmic to linear
     */
    public LinLogAxis(String label, double rightLinearBoundValue, double leftLinearBoundValue) {
        super(label, new StandardTickUnitSource());
        this.tickUnit = new NumberTickUnit(1.0, new DecimalFormat("0.#"), 9);
        this.centralValue = DEFAULT_CENTRAL_VALUE;
        if (leftLinearBoundValue < rightLinearBoundValue) {
            this.leftLinearBoundValue = -Math.abs(leftLinearBoundValue);
            this.rightLinearBoundValue = Math.abs(rightLinearBoundValue);
        } else {
            this.leftLinearBoundValue = DEFAULT_LEFT_LINEAR_BOUND_VALUE;
            this.rightLinearBoundValue = DEFAULT_RIGHT_LINEAR_BOUND_VALUE;
        }
        this.linearRangeScalingFactor = DEFAULT_LINEAR_RANGE_SCALING_VALUE;
    }

    /**
     * Creates a new <code>LogAxis</code> with the given label.
     *
     * @param label  the axis label (<code>null</code> permitted).
     * @param rightLinearBoundValue number at which the axis goes from linear to logarithmic
     * (<code>null</code> permitted).
     * @param leftLinearBoundValue  number at which the axis goes from logarithmic to linear
     * @param linearRangeScalingFactor value between 0 and 1 to signify the scale of the linear part of the axis with respect to the logarithmic part, the default is 0.2 or 20% of the total axis is linear
     */
    public LinLogAxis(String label, double rightLinearBoundValue, double leftLinearBoundValue, double linearRangeScalingFactor) {
        //super(label, createLogTickUnits(Locale.getDefault()));
        super(label, new StandardTickUnitSource());
        this.tickUnit = new NumberTickUnit(1.0, new DecimalFormat("0.#"), 9);
        this.centralValue = DEFAULT_CENTRAL_VALUE;
        if (leftLinearBoundValue < rightLinearBoundValue) {
            this.leftLinearBoundValue = -Math.abs(leftLinearBoundValue);
            this.rightLinearBoundValue = Math.abs(rightLinearBoundValue);
        } else {
            this.leftLinearBoundValue = DEFAULT_LEFT_LINEAR_BOUND_VALUE;
            this.rightLinearBoundValue = DEFAULT_RIGHT_LINEAR_BOUND_VALUE;
        }
        if (linearRangeScalingFactor >= 0 && linearRangeScalingFactor <= 1) {
            this.linearRangeScalingFactor = linearRangeScalingFactor;
            this.DEFAULT_LINEAR_RANGE_SCALING_VALUE = linearRangeScalingFactor;
        } else {
            this.linearRangeScalingFactor = DEFAULT_LINEAR_RANGE_SCALING_VALUE;
        }
        //setStandardTickUnits(createLinLogTickUnits(Locale.getDefault(), leftLinearBoundValue, rightLinearBoundValue));
        setNumberFormatOverride(new LinLogFormat(10, "e", "", true, leftLinearBoundValue, rightLinearBoundValue));

    }

    /**
     * Returns the base for the logarithm calculation.
     *
     * @return The base for the logarithm calculation.
     *
     * @see #setBase(double)
     */
    public double getBase() {
        return this.base;
    }

    /**
     * Sets the base for the logarithm calculation and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param base  the base value (must be > 1.0).
     *
     * @see #getBase()
     */
    public void setBase(double base) {
        if (base <= 1.0) {
            throw new IllegalArgumentException("Requires 'base' > 1.0.");
        }
        this.base = base;
        this.baseLog = Math.log(base);
        notifyListeners(new AxisChangeEvent(this));
    }

    public void setLinearRangeScalingFactor(double linearRangeScalingFactor) {
        this.linearRangeScalingFactor = linearRangeScalingFactor;
    }

    public int getUserSpecifiedLabelWidth() {
        return userSpecifiedLabelWidth;
    }

    public void setUserSpecifiedLabelWidth(int userSpecifiedLabelWidth) {
        this.userSpecifiedLabelWidth = userSpecifiedLabelWidth;
    }

    /**
     * Returns the current tick unit.
     *
     * @return The current tick unit.
     *
     * @see #setTickUnit(NumberTickUnit)
     */
    public NumberTickUnit getTickUnit() {
        return this.tickUnit;
    }

    /**
     * Sets the tick unit for the axis and sends an {@link AxisChangeEvent} to
     * all registered listeners.  A side effect of calling this method is that
     * the "auto-select" feature for tick units is switched off (you can
     * restore it using the {@link ValueAxis#setAutoTickUnitSelection(boolean)}
     * method).
     *
     * @param unit  the new tick unit (<code>null</code> not permitted).
     *
     * @see #getTickUnit()
     */
    public void setTickUnit(NumberTickUnit unit) {
        // defer argument checking...
        setTickUnit(unit, true, true);
    }

    /**
     * Sets the tick unit for the axis and, if requested, sends an
     * {@link AxisChangeEvent} to all registered listeners.  In addition, an
     * option is provided to turn off the "auto-select" feature for tick units
     * (you can restore it using the
     * {@link ValueAxis#setAutoTickUnitSelection(boolean)} method).
     *
     * @param unit  the new tick unit (<code>null</code> not permitted).
     * @param notify  notify listeners?
     * @param turnOffAutoSelect  turn off the auto-tick selection?
     *
     * @see #getTickUnit()
     */
    public void setTickUnit(NumberTickUnit unit, boolean notify,
            boolean turnOffAutoSelect) {

        if (unit == null) {
            throw new IllegalArgumentException("Null 'unit' argument.");
        }
        this.tickUnit = unit;
        if (turnOffAutoSelect) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the number format override.  If this is non-null, then it will
     * be used to format the numbers on the axis.
     *
     * @return The number formatter (possibly <code>null</code>).
     *
     * @see #setNumberFormatOverride(NumberFormat)
     */
    public NumberFormat getNumberFormatOverride() {
        return this.numberFormatOverride;
    }

    /**
     * Sets the number format override.  If this is non-null, then it will be
     * used to format the numbers on the axis.
     *
     * @param formatter  the number formatter (<code>null</code> permitted).
     *
     * @see #getNumberFormatOverride()
     */
    public void setNumberFormatOverride(NumberFormat formatter) {
        this.numberFormatOverride = formatter;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Calculates the log of the given value, using the current base.
     *
     * @param value  the value.
     *
     * @return The log of the given value.
     *
     * @see #calculateValue(double)
     * @see #getBase()
     */
    public double calculateLog(double value) {
        return Math.log(value) / this.baseLog;
    }

    /**
     * Calculates the value from a given log.
     *
     * @param log  the log value (must be > 0.0).
     *
     * @return The value with the given log.
     *
     * @see #calculateLog(double)
     * @see #getBase()
     */
    public double calculateValue(double log) {
        return Math.pow(this.base, log);
    }

    /**
     * Converts a value on the axis scale to a Java2D coordinate relative to
     * the given <code>area</code>, based on the axis running along the
     * specified <code>edge</code>.
     *
     * @param value  the data value.
     * @param area  the area.
     * @param edge  the edge.
     *
     * @return The Java2D coordinate corresponding to <code>value</code>.
     */
    @Override
    public double valueToJava2D(double value, Rectangle2D area,
            RectangleEdge edge) {

        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            max = area.getMinY();
            min = area.getMaxY();
        }
        if (isInverted()) {
            return max
                    - (convertToGenericValue(value)) * (max - min);
        } else {
            return min
                    + (convertToGenericValue(value)) * (max - min);
        }

    }

    /**
     * Converts a Java2D coordinate to an axis value, assuming that the
     * axis covers the specified <code>edge</code> of the <code>area</code>.
     *
     * @param java2DValue  the Java2D coordinate.
     * @param area  the area.
     * @param edge  the edge that the axis belongs to.
     *
     * @return A value along the axis scale.
     */
    @Override
    public double java2DToValue(double java2DValue, Rectangle2D area,
            RectangleEdge edge) {
        double genericValue = java2DValue / area.getWidth();
        return convertFromGenericValue(genericValue);
    }

    /**
     * Configures the axis.  This method is typically called when an axis
     * is assigned to a new plot.
     */
    @Override
    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    /**
     * Adjusts the axis range to match the data range that the axis is
     * required to display.
     *
     */
    @Override
    protected void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot == null) {
            return;  // no plot, no data
        }
        setLinearRangeScalingFactor(DEFAULT_LINEAR_RANGE_SCALING_VALUE);
        calculateTotalRange();
        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;

            Range r = vap.getDataRange(this);
            if (r == null) {
                r = getDefaultAutoRange();
            }

            double upper = r.getUpperBound();
            double lower = r.getLowerBound();
            double range = upper - lower;

            // ensure the autorange is at least <minRange> in size...
            double minRange = getAutoRangeMinimumSize();
            if (range < minRange) {
                double expand = (minRange - range) / 2;
                upper = upper + expand;
                lower = lower - expand;
                if (lower == upper) { // see bug report 1549218
                    double adjust = Math.abs(lower) / 10.0;
                    lower = lower - adjust;
                    upper = upper + adjust;
                }
            }

            if (lower < leftLinearBoundValue && upper > rightLinearBoundValue) {
                upper = calculateValue(calculateLog(upper) + getUpperMargin() * totalRange);
                lower = -calculateValue(calculateLog(Math.abs(lower)) + getLowerMargin() * totalRange);
            } else if (lower < leftLinearBoundValue && upper < rightLinearBoundValue && upper > leftLinearBoundValue) {
                upper = upper + getUpperMargin() * totalRange;
                lower = lower - getLowerMargin() * totalRange;
            } else if (lower < leftLinearBoundValue && upper < rightLinearBoundValue) {
                upper = upper + getUpperMargin() * totalRange;
                lower = lower - getLowerMargin() * totalRange;
            } else if (lower > leftLinearBoundValue && lower < rightLinearBoundValue && upper > rightLinearBoundValue) {
                upper = upper + getUpperMargin() * totalRange;
                lower = lower - getLowerMargin() * totalRange;
            } else if (lower > leftLinearBoundValue && lower < rightLinearBoundValue && upper < rightLinearBoundValue && upper > leftLinearBoundValue) {
                upper = upper + getUpperMargin() * totalRange;
                lower = lower - getLowerMargin() * totalRange;
            } else { //lower > rightLinearBoundValue && upper > rightLinearBoundValue               
                upper = upper + getUpperMargin() * totalRange;
                lower = lower - getLowerMargin() * totalRange;
            }
            setRange(new Range(lower, upper), false, false);
        }
    }

    @Override
    public void setRange(Range range) {
        super.setRange(range);
        calculateTotalRange();
    }

    @Override
    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        super.setRange(range, turnOffAutoRange, notify);
        calculateTotalRange();
    }

    /**
     * Returns a value on the data axis based on an input from 0 to 1.
     *
     */
    private double convertFromGenericValue(double value) {
        Range range = getRange();
        double lower = range.getLowerBound();
        double upper = range.getUpperBound();
        double result = 0;
        double leftPercent = leftRange / totalRange;
        double centerPercent = linRange / totalRange;
        double rightPercent = rightRange / totalRange;

        if (value < leftPercent) {
            result = -calculateValue(
                    calculateLog(Math.abs(Math.min(upper, leftLinearBoundValue)))
                    + (1 - value / leftPercent)
                    * (calculateLog(Math.abs(lower))
                    - calculateLog(Math.abs(Math.min(upper, leftLinearBoundValue)))));
        } else if (value >= leftPercent && value <= (leftPercent + centerPercent)) {
            result = Math.max(lower, leftLinearBoundValue)
                    + Math.abs((value - leftPercent) / centerPercent * (Math.min(upper, rightLinearBoundValue) - Math.max(lower, leftLinearBoundValue)));
        } else {
            result = calculateValue(
                    calculateLog(Math.abs(Math.max(lower, rightLinearBoundValue)))
                    + ((value - leftPercent - centerPercent) / rightPercent)
                    * (calculateLog(Math.abs(upper))
                    - calculateLog(Math.abs(Math.max(lower, rightLinearBoundValue)))));
        }
        return result;
    }

    /**
     * Returns a value from 0 to 1 depending on the location of value with
     * respect to the axis.
     *
     */
    private double convertToGenericValue(double value) {
        Range range = getRange();
        double lower = range.getLowerBound();
        double upper = range.getUpperBound();
        double result = 0;

        if (value < leftLinearBoundValue) {
            if (value < lower) {
                result = -leftRange * Math.abs(calculateLog(Math.abs(lower)) - calculateLog(Math.abs(value)))
                        / Math.abs(calculateLog(Math.abs(lower)) - calculateLog(Math.abs(Math.min(upper, leftLinearBoundValue))));
            } else if (value > upper) {
                result = leftRange * (1 + Math.abs(calculateLog(Math.abs(upper)) - calculateLog(Math.abs(value)))
                        / Math.abs(calculateLog(Math.abs(lower)) - calculateLog(Math.abs(Math.min(upper, leftLinearBoundValue)))));
            } else {
                result = leftRange * Math.abs(calculateLog(Math.abs(lower)) - calculateLog(Math.abs(value)))
                        / Math.abs(calculateLog(Math.abs(lower)) - calculateLog(Math.abs(Math.min(upper, leftLinearBoundValue))));
            }
        } else if (value >= leftLinearBoundValue && value <= rightLinearBoundValue) {
            if (value < lower && lower > rightLinearBoundValue) { //not yet different from the final else statement
                result = leftRange + linRange
                        * (value - Math.max(leftLinearBoundValue, lower))
                        / (Math.min(rightLinearBoundValue, upper) - Math.max(leftLinearBoundValue, lower));
            } else if (value > upper && upper < leftLinearBoundValue) { //not yet different from the final else statement
                double[] ranges = calculateTotalRanges(lower, value);
                double tempLeftRange = ranges[0];
                double tempLinRange = ranges[1];
                result = tempLeftRange + tempLinRange
                        * (value - Math.max(leftLinearBoundValue, lower))
                        / (Math.min(rightLinearBoundValue, upper) - Math.max(leftLinearBoundValue, lower));
            } else {
                result = leftRange + linRange
                        * (Math.min(upper, value) - Math.max(leftLinearBoundValue, lower))
                        / (Math.min(rightLinearBoundValue, upper) - Math.max(leftLinearBoundValue, lower));
            }
        } else {
            if (value < lower) {
                result = leftRange + linRange + rightRange * (-(Math.abs(calculateLog(Math.abs(value)) - calculateLog(Math.abs(Math.max(lower, rightLinearBoundValue))))
                        / Math.abs(calculateLog(Math.abs(upper)) - calculateLog(Math.abs(Math.max(lower, rightLinearBoundValue))))));
            } else if (value > upper) {
                result = leftRange + linRange + rightRange * (1 + Math.abs(calculateLog(Math.abs(value)) - calculateLog(Math.abs(Math.max(upper, rightLinearBoundValue))))
                        / Math.abs(calculateLog(Math.abs(upper)) - calculateLog(Math.abs(Math.max(lower, rightLinearBoundValue)))));
            } else {
                result = leftRange + linRange + rightRange * (Math.abs(calculateLog(Math.abs(value)) - calculateLog(Math.abs(Math.max(lower, rightLinearBoundValue))))
                        / Math.abs(calculateLog(Math.abs(upper)) - calculateLog(Math.abs(Math.max(lower, rightLinearBoundValue)))));
            }
        }

        if (totalRange != 0) {
            return result / totalRange;
        } else {
            return 0;
        }
    }

    /**
     * Calculates the total range of the entire axis, converting the length of
     * the logarithmic part of the axis to the same units as the linear part.
     *
     * Beforehand we do not know what kind of data the user will supply to
     * the Axis. This data could stretch the whole of the lower logarithmic
     * part, the linear part and the upper logarithmic part of the axis. Or
     * for instance only cover part of the linear part of the axis. In
     * total that means there are 6 scenarios, defined below:
     * Possible scenarios for lower and upper bound, the * symbols indicate
     * the lower and upper bounds of the data.
     * Log |  Lin  | Log  ||
     * 1: *---|-------|---*  ||
     * 2: *---|---*---|----  ||
     * 3: *-*-|-------|----  ||
     * 4: ----|---*---|---*  ||
     * 5: ----|---*-*-|----  ||
     * 6: ----|-------|-*-*  ||
     * The total range of the axis is calculated in generic units where we
     * take the logarithm of the length of the logarithmic parts of the
     * axis, multiplied by the percentage that is reserved for displaying
     * the logarithmic part of the axis, combined with the length of the
     * linear part multiplied by the percentage that is reserved for
     * displaying the linear data.
     * The total range, according to the 6 scenarios is then defined as
     * follows. Here lb=leftLinearBoundValue, rb=rightLinearBoundValue,
     * l=lower bound for the data, u=upper bound for the data.
     * 1: log(lb-l)+(rb-lb)+log(u-rb)
     * 2: log(lb-l)+(u-lb)
     * 3: log(lb-l)+log(u-lb)
     * 4: (rb-l)+log(u-rb)
     * 5: (rb-l)+(u-rb)
     * 6: log(rb-l)+log(u-rb)
     */
    private void calculateTotalRange() {
        Range range = getRange();
        double lower = range.getLowerBound();
        double upper = range.getUpperBound();
        double[] ranges = calculateTotalRanges(lower, upper);
        leftRange = ranges[0];
        linRange = ranges[1];
        rightRange = ranges[2];
        totalRange = leftRange + linRange + rightRange;
    }

    private double[] calculateTotalRanges(double lower, double upper) {
        double a = 0, b1 = 0, b2 = 0, c = 0, s = linearRangeScalingFactor;

        if (lower < leftLinearBoundValue && upper > rightLinearBoundValue) {
            a = Math.abs(calculateLog(Math.abs(leftLinearBoundValue)) - calculateLog(Math.abs(lower)));
            b1 = Math.abs(centralValue - leftLinearBoundValue);
            b2 = Math.abs(rightLinearBoundValue - centralValue);
            c = Math.abs(calculateLog(Math.abs(rightLinearBoundValue)) - calculateLog(Math.abs(upper)));
        } else if (lower < leftLinearBoundValue && upper <= rightLinearBoundValue && upper > leftLinearBoundValue) {
            a = calculateLog(Math.abs(leftLinearBoundValue - lower));
            if (upper < centralValue) {
                b1 = Math.abs(upper - leftLinearBoundValue);
            } else {
                b1 = Math.abs(centralValue - leftLinearBoundValue);
                b2 = Math.abs(upper - centralValue);
            }
        } else if (lower < leftLinearBoundValue && upper <= rightLinearBoundValue) {
            a = Math.abs(calculateLog(Math.abs(upper)) - calculateLog(Math.abs(lower)));
        } else if (lower >= leftLinearBoundValue && lower < rightLinearBoundValue && upper > rightLinearBoundValue) {
            if (lower > centralValue) {
                b1 = Math.abs(rightLinearBoundValue - lower);
            } else {
                b1 = Math.abs(centralValue - lower);
                b2 = Math.abs(centralValue - rightLinearBoundValue);
            }
            c = Math.abs(calculateLog(Math.abs(upper)) - calculateLog(Math.abs(rightLinearBoundValue)));
        } else if (lower >= leftLinearBoundValue && lower < rightLinearBoundValue && upper <= rightLinearBoundValue && upper > leftLinearBoundValue) {
            if (lower < centralValue && upper > centralValue) {
                b1 = centralValue - lower;
                b2 = upper - centralValue;
            } else {
                b2 = upper - lower;
            }
        } else { //lower > rightLinearBoundValue && upper > rightLinearBoundValue
            c = Math.abs(calculateLog(Math.abs(upper)) - calculateLog(Math.abs(lower)));
        }

        if ((b1 + b2) != 0) {
            leftLinRange = s * (a + b1 + b2 + c) * (b1) / (b1 + b2);
            rightLinRange = s * (a + b1 + b2 + c) * (b2) / (b1 + b2);
        } else {
            leftLinRange = 0;
            rightLinRange = 0;
        }
        linRange = leftLinRange + rightLinRange;
        if ((a + c) != 0) {
            leftRange = (1 - s) * (a + b1 + b2 + c) * a / (a + c);
            rightRange = (1 - s) * (a + b1 + b2 + c) * c / (a + c);
        } else {
            leftRange = 0;
            rightRange = 0;
        }
        return new double[]{leftRange, linRange, rightRange};
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device (<code>null</code> not permitted).
     * @param cursor  the cursor location (determines where to draw the axis).
     * @param plotArea  the area within which the axes and plot should be drawn.
     * @param dataArea  the area within which the data should be drawn.
     * @param edge  the axis location (<code>null</code> not permitted).
     * @param plotState  collects information about the plot
     *                   (<code>null</code> permitted).
     *
     * @return The axis state (never <code>null</code>).
     */
    @Override
    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea,
            Rectangle2D dataArea, RectangleEdge edge,
            PlotRenderingInfo plotState) {

        AxisState state = null;
        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            state = new AxisState(cursor);
            // even though the axis is not visible, we need ticks for the
            // gridlines...
            List<Tick> ticks = refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }
        state = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
        createAndAddEntity(
                cursor, state, dataArea, edge, plotState);

        return state;

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     *
     */
    @Override
    public List<Tick> refreshTicks(Graphics2D g2, AxisState state,
            Rectangle2D dataArea, RectangleEdge edge) {

        List<Tick> result = new java.util.ArrayList<Tick>();
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = refreshTicksHorizontal(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            result = refreshTicksVertical(g2, dataArea, edge);
        }
        return result;
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    protected List<Tick> refreshTicksHorizontal(Graphics2D g2,
            Rectangle2D dataArea, RectangleEdge edge) {

        return calculateTicks(g2, dataArea, edge);
    }

    /**
     * Calculates the number of visible ticks.
     *
     * @return The number of visible ticks on the axis.
     */
    protected int calculateVisibleTickCount() {

        double unit = getTickUnit().getSize();
        return (int) ((totalRange / unit) + 1);
    }

    /**
     * Calculates the value of the lowest visible tick on the axis.
     *
     * @return The value of the lowest visible tick on the axis.
     *
     * @see #calculateHighestVisibleTickValue()
     */
    protected double calculateLowestVisibleTickValue() {

        double unit = getTickUnit().getSize();
        double index = Math.ceil(getRange().getLowerBound() / unit);
        return index * unit;
    }

    /**
     * Returns a list of ticks for an axis at the left or right of the chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param edge  the edge.
     *
     * @return A list of ticks.
     */
    protected List<Tick> refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea,
            RectangleEdge edge) {

        return calculateTicks(g2, dataArea, edge);
    }

    /**
     * Converts a length in data coordinates into the corresponding length in
     * Java2D coordinates.
     *
     * @param length  the length.
     * @param area  the plot area.
     * @param edge  the edge along which the axis lies.
     *
     * @return The length in Java2D coordinates.
     *
     * @since 1.0.7
     */
    public double exponentLengthToJava2D(double length, Rectangle2D area,
            RectangleEdge edge) {
        double zero = convertToGenericValue(0);
        double l = convertToGenericValue(length);
        double lower = getRange().getLength();
        double upper = getRange().getLowerBound();
        return Math.abs(l - zero) / totalRange * area.getWidth();
        //double one = valueToJava2D(calculateValue(1.0), area, edge);
        //double l = valueToJava2D(calculateValue(length + 1.0), area, edge);
        //return Math.abs(l - one);

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the axis location.
     *
     * @since 1.0.7
     */
    protected void selectVerticalAutoTickUnit(Graphics2D g2,
            Rectangle2D dataArea,
            RectangleEdge edge) {

        double tickLabelHeight = estimateMaximumTickLabelHeight(g2);

        // start with the current tick unit...
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());


        double unitHeight = exponentLengthToJava2D(unit1.getSize(), dataArea,
                edge);

        // then extrapolate...
        double guess = (tickLabelHeight / unitHeight) * unit1.getSize();
        NumberTickUnit unit2 = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);
        double unit2Height = exponentLengthToJava2D(unit2.getSize(), dataArea,
                edge);
        tickLabelHeight = estimateMaximumTickLabelHeight(g2);
        if (tickLabelHeight > unit2Height) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }
        setTickUnit(unit2, false, false);
    }

    /**
     * Estimates the maximum tick label height.
     *
     * @param g2  the graphics device.
     *
     * @return The maximum height.
     *
     * @since 1.0.7
     */
    protected double estimateMaximumTickLabelHeight(Graphics2D g2) {

        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();
        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        result += tickLabelFont.getLineMetrics("123", frc).getHeight();
        return result;
    }

    /**
     * Estimates the maximum width of the tick labels, assuming the specified
     * tick unit is used.
     * <P>
     * Rather than computing the string bounds of every tick on the axis, we
     * just look at two values: the lower bound and the upper bound for the
     * axis.  These two values will usually be representative.
     *
     * @param g2  the graphics device.
     * @param unit  the tick unit to use for calculation.
     *
     * @return The estimated maximum width of the tick labels.
     *
     * @since 1.0.7
     */
    protected double estimateMaximumTickLabelWidth(Graphics2D g2,
            TickUnit unit) {

        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        if (isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of the
            // font)...
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = getTickLabelFont().getLineMetrics("0", frc);
            result += lm.getHeight();
        } else {
            // look at lower and upper bounds...
            FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
            Range range = getRange();
            double lower = range.getLowerBound();
            double upper = range.getUpperBound();
            String lowerStr = "";
            String lowerLinStr = "";
            String upperStr = "";
            String upperLinStr = "";
            LinLogFormat formatter = (LinLogFormat) getNumberFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                lowerLinStr = formatter.format(Math.min(leftLinearBoundValue,
                        formatter.getMinLinearDisplayValue()));
                upperStr = formatter.format(upper);
                upperLinStr = formatter.format(Math.max(leftLinearBoundValue,
                        formatter.getMaxLinearDisplayValue()));
            } else {
                lowerStr = unit.valueToString(lower);
                upperStr = unit.valueToString(upper);
            }
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(lowerLinStr);
            double w3 = fm.stringWidth(upperStr);
            double w4 = fm.stringWidth(upperLinStr);
            result += Math.max(Math.max(w1, w2), Math.max(w3, w4));
        }

        return result;
    }

    /**
     * Estimates the width for a particular tick label, assuming the
     * LinLogFormat numberformatter is used.
     * <P>
     * Rather than computing the string bounds of every tick on the axis, we
     * just look at two values: the lower bound and the upper bound for the
     * axis.  These two values will usually be representative.
     *
     * @param g2  the graphics device.
     * @param unit  the tick unit to use for calculation.
     *
     * @return The estimated maximum width of the tick labels.
     *
     * @since 1.0.7
     */
    protected double estimateTickLabelWidth(Graphics2D g2,
            double value, NumberFormat formatter) {

        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        if (isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of the
            // font)...
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = getTickLabelFont().getLineMetrics("0", frc);
            result += lm.getHeight();
        } else {
            // look at lower and upper bounds...
            FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
            String lowerStr = "";
            if (formatter != null) {
                lowerStr = formatter.format(value);
            }
            double w1 = fm.stringWidth(lowerStr);
            result += w1;
        }
        return result;
    }

    /**
     * Zooms in on the current range.
     *
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     */
    @Override
    public void zoomRange(double lowerPercent, double upperPercent) {
        Range adjusted = null;
        if (isInverted()) {
            adjusted = new Range(convertFromGenericValue(lowerPercent), convertFromGenericValue(upperPercent));
        } else {
            adjusted = new Range(convertFromGenericValue(lowerPercent), convertFromGenericValue(upperPercent));
        }
//        double newScalingFactor = (convertToGenericValue(Math.min(rightLinearBoundValue,getRange().getUpperBound())) -
//                convertToGenericValue(Math.max(leftLinearBoundValue,getRange().getLowerBound()))) /
//                (convertToGenericValue(Math.min(rightLinearBoundValue,adjusted.getUpperBound())) -
//                convertToGenericValue(Math.max(leftLinearBoundValue,adjusted.getLowerBound())));
//        setLinearRangeScalingFactor(newScalingFactor);
        setRange(adjusted);
    }

    /**
     * Slides the axis range by the specified percentage.
     *
     * @param percent  the percentage.
     *
     * @since 1.0.13
     */
    @Override
    public void pan(double percent) {
        //TODO: This method is not working correctly yet. It requires a
        //different approach than linear or logarithmic panning, this problem
        // is most noticible near the borders of the linear and logarithmic region
        Range adjusted = null;
        Range range = getRange();
        double lower = range.getLowerBound();
        double upper = range.getUpperBound();
        double lowerBound = lower;
        double upperBound = upper;
        double length = upperBound - lowerBound;
        double adj = length * percent;
        lowerBound = lowerBound + adj;
        upperBound = upperBound + adj;

        if (isInverted()) {
            adjusted = new Range(lowerBound, upperBound);
        } else {
            adjusted = new Range(lowerBound, upperBound);
        }
        setRange(adjusted);
    }

    /**
     * Creates a tick label for the specified value.  Note that this method
     * was 'private' prior to version 1.0.10.
     *
     * @param value  the value.
     *
     * @return The label.
     *
     * @since 1.0.10
     */
    protected String createTickLabel(double value) {
        if (this.numberFormatOverride != null) {
            return this.numberFormatOverride.format(value);
        } else {
            return this.tickUnit.valueToString(value);
        }
    }

    /**
     * Tests this axis for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LinLogAxis)) {
            return false;
        }
        LinLogAxis that = (LinLogAxis) obj;
        if (this.base != that.base) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = 193;
        long temp = Double.doubleToLongBits(this.base);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        if (this.numberFormatOverride != null) {
            result = 37 * result + this.numberFormatOverride.hashCode();
        }
        result = 37 * result + this.tickUnit.hashCode();
        return result;
    }

    /**
     * Returns a collection of tick units for log (base 10) values.
     * Uses a given Locale to create the DecimalFormats.
     *
     * @param locale the locale to use to represent Numbers.
     *
     * @return A collection of tick units for integer values.
     *
     * @since 1.0.7
     */
    public static TickUnitSource createLinLogTickUnits(Locale locale, double left, double right) {
        TickUnits units = new TickUnits();
        LinLogFormat linLogNumberFormat = new LinLogFormat(10, "e", "", true, left, right);
        units.add(new NumberTickUnit(0.0001, linLogNumberFormat, 10));
        units.add(new NumberTickUnit(0.001, linLogNumberFormat, 10));
        units.add(new NumberTickUnit(0.01, linLogNumberFormat, 10));
        units.add(new NumberTickUnit(0.1, linLogNumberFormat, 10));
        units.add(new NumberTickUnit(1, linLogNumberFormat, 10));
        units.add(new NumberTickUnit(10, linLogNumberFormat, 10));
        units.add(new NumberTickUnit(1000, linLogNumberFormat, 10));
        units.add(new NumberTickUnit(10000, linLogNumberFormat, 10));
        return units;
    }

    private boolean majorTickNotOverlapping(double lower, double upper, Graphics2D g2, Rectangle2D area, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            double location1 = valueToJava2D(lower, area, edge);
            double location2 = valueToJava2D(upper, area, edge);
            double width1 = estimateTickLabelWidth(g2, lower, getNumberFormatOverride());
            double width2 = estimateTickLabelWidth(g2, upper, getNumberFormatOverride());
            return (location1 + Math.min(width1, getCurrentLabelWidth()) / 2) < (location2 - width2 / 2);
        } else {
            return true;
        }
    }

    private double getCurrentLabelWidth() {
        if (getUserSpecifiedLabelWidth() != 0) {
            return userSpecifiedLabelWidth;
        } else {
            return maximumLabelWidth;
        }
    }

    private List<Tick> calculateTicks(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        Range range = getRange();
        double upper = range.getUpperBound();
        double lower = range.getLowerBound();
        List<Tick> ticks = new ArrayList<Tick>();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        maximumLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());
        TextAnchor textAnchor, labelAnchor;
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelAnchor = TextAnchor.CENTER_RIGHT;
            if (edge == RectangleEdge.TOP) {
                textAnchor = TextAnchor.BOTTOM_CENTER;
            } else {
                textAnchor = TextAnchor.TOP_CENTER;
            }
        } else {
            labelAnchor = TextAnchor.CENTER_RIGHT;
            if (edge == RectangleEdge.LEFT) {
                textAnchor = TextAnchor.CENTER_RIGHT;
            } else {
                textAnchor = TextAnchor.CENTER_LEFT;
            }
        }

        double start1 = Math.signum(leftLinearBoundValue) * Math.pow(this.base, Math.ceil(calculateLog(Math.abs(Math.min(leftLinearBoundValue, upper)))));
        double end1 = getLowerBound();
        double start2 = Math.max(lower, centralValue);
        double end2 = Math.max(lower, leftLinearBoundValue);
        double start3 = Math.max(lower, centralValue);
        double end3 = Math.min(upper, rightLinearBoundValue);
        double start4 = Math.signum(rightLinearBoundValue) * Math.pow(this.base, Math.ceil(calculateLog(Math.abs(Math.max(lower, rightLinearBoundValue)))));
        double end4 = getUpperBound();

        int minorTickCount = 10;

        //The left logarithmic part
        double current = start1;
        double previous = current;
        while (end1 <= current && start1 >= end1 && end1 < leftLinearBoundValue) {
            if (range.contains(current)) {
                if (current == previous || majorTickNotOverlapping(current, previous, g2, dataArea, edge)) {
                    ticks.add(new NumberTick(TickType.MAJOR, current, createTickLabel(current),
                            textAnchor, labelAnchor, 0.0));
                    previous = current;
                } else {
                    ticks.add(new NumberTick(TickType.MAJOR, current, "",
                            textAnchor, labelAnchor, 0.0));
                }
            }
            // add minor ticks (for gridlines)
            double next = Math.signum(current) * Math.pow(this.base, calculateLog(Math.abs(current))
                    + 1);
            for (int i = 1; i < minorTickCount; i++) {
                double minorV = current + Math.signum(current) * i * ((Math.abs(next)) / minorTickCount);
                if (range.contains(minorV)) {
                    ticks.add(new NumberTick(TickType.MINOR, minorV, "",
                            textAnchor, labelAnchor, 0.0));
                }
            }
            current = next;
        }

        //The left central linear part
        current = start2;
        previous = start2;
        while (current > end2 && start2 >= end2 && (end2 > leftLinearBoundValue && start2 <= centralValue)) {
            if (range.contains(current)) {
                if (majorTickNotOverlapping(current, previous, g2, dataArea, edge) && majorTickNotOverlapping(start1, current, g2, dataArea, edge)) {
                    ticks.add(new NumberTick(TickType.MAJOR, current, createTickLabel(current),
                            textAnchor, labelAnchor, 0.0));
                    previous = current;
                } else {
                    ticks.add(new NumberTick(TickType.MAJOR, current, "",
                            textAnchor, labelAnchor, 0.0));
                }
            }
            // add minor ticks (for gridlines)
            double numberOfTickUnits = Math.max(1, Math.round((dataArea.getWidth() * (convertToGenericValue(start2) - convertToGenericValue(end2)) / DEFAULT_MINIMUM_PIXELS_PER_MAJOR_TICK)));
            double test = (end2 - start2) / numberOfTickUnits;
            double power = calculateValue(Math.ceil(calculateLog(Math.abs(test))));
            double test2 = Math.abs(test / power);
            double sign = Math.signum(test);
            if (test2 == 1) {
                test = sign * 1 * power;
            } else if (test2 < 1 && test2 >= 0.5) {
                test = sign * 0.5 * power;
            } else if (test2 < 0.5 && test2 >= 0.2) {
                test = sign * 0.2 * power;
            } else if (test2 < 0.2 && test2 >= 0) {
                test = sign * 0.1 * power;
            }
            double next = current + test;
            for (int i = 1; i < minorTickCount; i++) {
                double minorV = current + i * (Math.abs(current) - Math.abs(next)) / minorTickCount;
                if (range.contains(minorV)) {
                    ticks.add(new NumberTick(TickType.MINOR, minorV, "",
                            textAnchor, labelAnchor, 0.0));
                }
            }
            current = next;

        }

        //The right central linear part
        current = start3;
        previous = start3;
        while (current <= end3 && start3 <= end3 && (end3 > centralValue && start3 <= rightLinearBoundValue)) {
            if (range.contains(current)) {
                if (current == previous || majorTickNotOverlapping(previous, current, g2, dataArea, edge) && majorTickNotOverlapping(current, start4, g2, dataArea, edge)) {
                    ticks.add(new NumberTick(TickType.MAJOR, current, createTickLabel(current),
                            textAnchor, labelAnchor, 0.0));
                    previous = current;
                } else {
                    ticks.add(new NumberTick(TickType.MAJOR, current, "",
                            textAnchor, labelAnchor, 0.0));
                }
            }
            // add minor ticks (for gridlines)
            double numberOfTickUnits = Math.max(1, Math.round((dataArea.getWidth() * (convertToGenericValue(end3) - convertToGenericValue(start3)) / DEFAULT_MINIMUM_PIXELS_PER_MAJOR_TICK)));
            double test = (end3 - start3) / numberOfTickUnits;
            double power = calculateValue(Math.ceil(calculateLog(Math.abs(test))));
            double test2 = Math.abs(test / power);
            double sign = Math.signum(test);
            if (test2 == 1) {
                test = sign * 1 * power;
            } else if (test2 < 1 && test2 >= 0.5) {
                test = sign * 0.5 * power;
            } else if (test2 < 0.5 && test2 >= 0.2) {
                test = sign * 0.2 * power;
            } else if (test2 < 0.2 && test2 >= 0) {
                test = sign * 0.1 * power;
            }
            double next = current + test;
            for (int i = 1; i < minorTickCount; i++) {
                double minorV = current + i * (Math.abs(next) - Math.abs(current)) / minorTickCount;
                if (range.contains(minorV)) {
                    ticks.add(new NumberTick(TickType.MINOR, minorV, "",
                            textAnchor, labelAnchor, 0.0));
                }
            }
            current = next;

        }

        //The right logarithmic part
        current = start4;
        previous = start4;
        while (current <= end4 && start4 <= end4 && end4 > rightLinearBoundValue) {
            double sign = Math.signum(current);
            double absCurrent = Math.abs(current);
            if (range.contains(current)) {
                if (current == previous || majorTickNotOverlapping(previous, current, g2, dataArea, edge)) {
                    ticks.add(new NumberTick(TickType.MAJOR, current, createTickLabel(current),
                            textAnchor, labelAnchor, 0.0));
                    previous = current;
                } else {
                    ticks.add(new NumberTick(TickType.MAJOR, current, "",
                            textAnchor, labelAnchor, 0.0));
                }
            }
            // add minor ticks (for gridlines)
            double next = sign * Math.pow(this.base, calculateLog(absCurrent)
                    + 1);
            for (int i = 1; i < minorTickCount; i++) {
                double minorV = current + sign * i * ((next) / minorTickCount);
                if (range.contains(minorV)) {
                    ticks.add(new NumberTick(TickType.MINOR, minorV, "",
                            textAnchor, labelAnchor, 0.0));
                }
            }
            current = next;

        }
        return ticks;
    }
}
