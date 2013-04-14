/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.tgmeditor.panels;

/**
 *
 * @author slapten
 */
public class JVectorValueClass {

    private double value;
    private boolean fixed;

    JVectorValueClass() {
        value = 0;
        fixed = true;
    }

    JVectorValueClass(double val, boolean fix) {
        value = val;
        fixed = fix;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
