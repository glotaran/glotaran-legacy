/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import java.text.MessageFormat;
import java.text.NumberFormat;
import org.jfree.chart.labels.CrosshairLabelGenerator;
import org.jfree.chart.plot.Crosshair;

/**
 *
 * @author lsp
 */
public class ImageCrosshairLabelGenerator implements CrosshairLabelGenerator {
    /*The array with labels*/

    private double[] labels;
    private boolean inverted;
    /** The label format string. */
    private String labelTemplate;
    /** A number formatter for the value. */
    private NumberFormat numberFormat;

    /**
     * Creates a new instance with default attributes.
     */
//    public ImageCrosshairLabelGenerator() {
//        this("{0}", NumberFormat.getNumberInstance());
//    }
    public ImageCrosshairLabelGenerator(double[] val, boolean inv) {
        this("{0}", NumberFormat.getNumberInstance());
        this.labels = new double[val.length];
        this.labels = val;
        this.inverted = inv;
    }

    /**
     * Creates a new instance with the specified attributes.
     *
     * @param labelTemplate  the label template (<code>null</code> not
     *     permitted).
     * @param numberFormat  the number formatter (<code>null</code> not
     *     permitted).
     */
    public ImageCrosshairLabelGenerator(String labelTemplate,
            NumberFormat numberFormat) {
        super();
        if (labelTemplate == null) {
            throw new IllegalArgumentException(
                    "Null 'labelTemplate' argument.");
        }
        if (numberFormat == null) {
            throw new IllegalArgumentException(
                    "Null 'numberFormat' argument.");
        }
        this.labelTemplate = labelTemplate;
        this.numberFormat = numberFormat;
        this.labels = new double[1];
        inverted = true;
    }

    public String getLabelTemplate() {
        return this.labelTemplate;
    }

    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public double[] getLabels() {
        return this.labels;
    }

    public void setLabels(double[] val) {
        this.labels = new double[val.length];
        labels = val;
    }

    /**
     * Returns a string that can be used as the label for a crosshair.
     *
     * @param crosshair  the crosshair (<code>null</code> not permitted).
     *
     * @return The label (possibly <code>null</code>).
     */
    public String generateLabel(Crosshair crosshair) {
        Object[] v;
        if (inverted) {
            v = new Object[]{this.numberFormat.format(
                    //TODO: find a better solution than using Math.max
                        labels[labels.length - (int) Math.max(1, crosshair.getValue())])};
        } else {
            v = new Object[]{this.numberFormat.format(
                        labels[(int) crosshair.getValue()])};
        }

        String result = MessageFormat.format(this.labelTemplate, v);
        return result;
    }
}
