/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes.dataobjects;

import org.glotaran.core.models.tgm.KinPar;

/**
 *
 * @author lsp
 */
public class NonLinearParameter extends AbstractParameterDO {

    private Double start;
    private Boolean fixed;
    private Boolean constrained;
    private Double maximum;
    private Double minimum;

    public NonLinearParameter() {
        start = new Double(0.0);
        fixed = false;
        constrained = false;
        maximum = new Double(0.0);
        minimum = new Double(0.0);

    }

    public NonLinearParameter(Double irfPar, Boolean fixedPar) {
        start = irfPar;
        fixed = fixedPar;
        constrained = false;
        maximum = new Double(0.0);
        minimum = new Double(0.0);

    }

    public NonLinearParameter(KinPar param) {
        start = param.getStart();
        fixed = param.isFixed();
        constrained = param.isConstrained();
        maximum = param.getMax();
        minimum = param.getMin();
    }

    public Double getStart() {
        return start;
    }

    public void setStart(Double value) {
        Double oldStart = start;
        this.start = value;
        fire("start", oldStart, start);
    }

    public Boolean isFixed() {
        return fixed;
    }

    public void setFixed(Boolean value) {
        Boolean oldFixed = fixed;
        this.fixed = value;
        fire("fixed", oldFixed, fixed);
    }

    public Boolean isConstrained() {
        return constrained;
    }

    public void setConstrained(Boolean value) {
        this.constrained = value;
    }

    public Double getMaximum() {
        return maximum;
    }

    public void setMaximum(Double value) {
        this.maximum = value;
    }

    public Double getMinimum() {
        return minimum;
    }

    public void setMinimum(Double value) {
        this.minimum = value;
    }
}
