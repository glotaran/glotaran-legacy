/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import org.jfree.chart.axis.StandardTickUnitSource;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;

/**
 *
 * @author lsp
 */
public class VectorTickUnitSource implements TickUnitSource {

    private double[] AxeLabels;

    public TickUnit getLargerTickUnit(TickUnit arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TickUnit getCeilingTickUnit(TickUnit arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TickUnit getCeilingTickUnit(double arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        return (obj instanceof StandardTickUnitSource);
    }
}
