/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes.dataobjects;

import java.util.List;
import org.glotaran.core.ui.visualmodelling.nodes.CohspecParametersSubNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author slapten
 * 
 */
public class CohspecParametersKeys extends NonLinearParametersKeys {
    
    private String type = null;

    public CohspecParametersKeys(int paramNum) {
        super(paramNum);     
    }

    public CohspecParametersKeys(List<Double> oscpar, List<Boolean> fixed, String type) {
        super(0);
        this.type = type;
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
            if(oscpar.size() == fixed.size()) {
                addObj(new NonLinearParameter(oscpar.get(i), fixed.get(i)));
            } else {
                addObj(new NonLinearParameter(oscpar.get(i), Boolean.FALSE));
            }
        }
    }    

    @Override
    protected Node[] createNodes(Object key) {
        if (key.getClass().equals(NonLinearParameter.class)) {
            return new Node[]{new CohspecParametersSubNode((NonLinearParameter) key, type)};
        }        
        return new Node[]{new AbstractNode(Children.LEAF)};
    }

}
