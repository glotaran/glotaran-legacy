/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes.dataobjects;

import java.util.ArrayList;
import java.util.List;
import org.glotaran.core.ui.visualmodelling.nodes.IrfMeasuredIrfSubNode;
import org.glotaran.core.ui.visualmodelling.nodes.IrfParametersSubNode;
import org.glotaran.core.ui.visualmodelling.nodes.OscspecParametersNode;
import org.glotaran.core.ui.visualmodelling.nodes.OscspecParametersSubNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author slapten
 */
public class OscParametersKeys extends NonLinearParametersKeys {

    public OscParametersKeys(int paramNum) {
        super(paramNum);     
    }

    public OscParametersKeys(List<Double> oscpar, List<Boolean> fixed) {
        super(0);      
        int arrSize = 0;
        if (!oscpar.isEmpty()){
            arrSize = oscpar.size();
        } else {
            oscpar.add(0.0);
            oscpar.add(0.0);
            oscpar.add(0.0);
            fixed.add(Boolean.TRUE);
            fixed.add(Boolean.FALSE);
            fixed.add(Boolean.FALSE);
            arrSize = 3;
        }

        for (int i = 0; i < arrSize; i++) {
            addObj(new NonLinearParameter(oscpar.get(i), fixed.get(i)));
        }
    }

    @Override
    protected Node[] createNodes(Object key) {
        if (key.getClass().equals(NonLinearParameter.class)) {
            return new Node[]{new OscspecParametersSubNode((NonLinearParameter) key)};
        }        
        return new Node[]{new AbstractNode(Children.LEAF)};
    }

}
