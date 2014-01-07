/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes.dataobjects;

import java.util.ArrayList;
import java.util.List;
import org.glotaran.core.ui.visualmodelling.nodes.IrfMeasuredIrfSubNode;
import org.glotaran.core.ui.visualmodelling.nodes.IrfParametersSubNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author slapten
 */
public class IrfParametersKeys extends NonLinearParametersKeys {

    private List<MeasuredIrfDO> measuredIrf;

    public IrfParametersKeys(int paramNum) {
        super(paramNum);
        measuredIrf = new ArrayList<MeasuredIrfDO>();
        measuredIrf.add(new MeasuredIrfDO());
    }

    public IrfParametersKeys(List<Double> irf, List<Boolean> fixed) {
        super(0);
        measuredIrf = new ArrayList<MeasuredIrfDO>();
        measuredIrf.add(new MeasuredIrfDO());
        int arrSize = 0;
        if (!irf.isEmpty()){
            arrSize = irf.size();
        } else {
            irf.add(0.0);
            irf.add(0.0);
            fixed.add(Boolean.FALSE);
            fixed.add(Boolean.FALSE);
            arrSize = 2;
        }

        for (int i = 0; i < arrSize; i++) {
            addObj(new NonLinearParameter(irf.get(i), fixed.get(i)));
        }
    }

    @Override
    protected Node[] createNodes(Object key) {
        if (key.getClass().equals(NonLinearParameter.class)) {
            return new Node[]{new IrfParametersSubNode((NonLinearParameter) key)};
        }
        if (key.getClass().equals(MeasuredIrfDO.class)){
            return new Node[]{new IrfMeasuredIrfSubNode((MeasuredIrfDO) key)};
        }
        return new Node[]{new AbstractNode(Children.LEAF)};
    }

    public void setMeasuredIrf() {
        setKeys(measuredIrf);
    }

    public void backFromMeasuredIrf() {
        super.backToParams();
    }
}
