/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.tgmeditor.panels;

/**
 *
 * @author slapten
 */
public class RelationValueClass {

    private Double c0;
    private Double c1;
    private Boolean fixedC0;
    private Boolean fixedC1;

    RelationValueClass() {
        c0 = new Double(Double.NaN);
        c1 = new Double(Double.NaN);
        fixedC0 = new Boolean(false);
        fixedC1 = new Boolean(false);
    }

    RelationValueClass(double c0val, double c1val, boolean fixC0, boolean fixC1) {
        c0 = new Double(c0val);
        c1 = new Double(c1val);
        fixedC0 = new Boolean(fixC0);
        fixedC1 = new Boolean(fixC1);
    }

    public boolean isFixedC0() {
        return fixedC0;
    }

    public void setFixedC0(boolean fixedC0) {
        this.fixedC0 = fixedC0;
    }

    public boolean isFixedC1() {
        return fixedC1;
    }

    public void setFixedC1(boolean fixedC1) {
        this.fixedC1 = fixedC1;
    }

    public double getC0() {
        return c0;
    }

    public void setC0(double c0) {
        this.c0 = c0;
    }

    public double getC1() {
        return c1;
    }

    public void setC1(double c1) {
        this.c1 = c1;
    }
}
