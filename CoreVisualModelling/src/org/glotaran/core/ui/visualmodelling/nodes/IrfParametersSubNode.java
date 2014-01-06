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
public class IrfParametersSubNode extends ParametersSubNode {

    private String[] nodeNames = new String[]{"Position", "Width", "Width2", "Ratio", "test"};

    public IrfParametersSubNode(NonLinearParameter data) {
        super(data);
    }

    @Override
    public String getDisplayName() {
        String name = null;
        for (int i = 0; i < getParentNode().getChildren().getNodesCount(); i++) {
            if (getParentNode().getChildren().getNodes()[i].equals(this)) {
                name = nodeNames[i];
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
