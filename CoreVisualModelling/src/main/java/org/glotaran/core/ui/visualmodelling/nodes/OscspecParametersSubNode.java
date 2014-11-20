/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.beans.PropertyChangeEvent;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.NonLinearParameter;

/**
 *
 * @author slapten
 */
public class OscspecParametersSubNode extends ParametersSubNode {

    private final String[] nodeNames = new String[]{"Phase shift","Frequency (\u03C9)","Damping (\u03b3)"};

    public OscspecParametersSubNode(NonLinearParameter data) {
        super(data);
    }

    @Override
    public String getDisplayName() {
        String name = null;
        int rem;
        int div;
        for (int i = 0; i < getParentNode().getChildren().getNodesCount(); i++) {
            if (getParentNode().getChildren().getNodes()[i].equals(this)) {
                if ((i>2)&&(getParentNode().getChildren().getNodesCount()>4)){
                    rem = i%3;
                    div = i/3;
                    name = nodeNames[rem]+" ".concat(String.valueOf(div+1));
                } else {
                name = nodeNames[i];
                }
                if (getDataObj().isFixed()) {
                    name = name + " (f)";
                }
                return name;
            }
        }
        return "error";
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
    }

}
