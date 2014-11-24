/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.beans.PropertyChangeEvent;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.NonLinearParameter;

/**
 *
 * @author slapten
 */
public class CohspecParametersSubNode extends ParametersSubNode {

    private final String[] nodeNames = new String[]{"Amplitude", "Tgvd", "Tau"};
    private String type = null;

    public CohspecParametersSubNode(NonLinearParameter data, String type) {
        super(data);
        this.type = type;
    }

    @Override
    public String getDisplayName() {
        String name = null;
        int rem;
        int div;
        for (int i = 0; i < getParentNode().getChildren().getNodesCount(); i++) {
            if (getParentNode().getChildren().getNodes()[i].equals(this)) {
                if (type.equals(EnumTypes.CohSpecTypes.XPM.toString())) {
                    if ((i > 2) && (getParentNode().getChildren().getNodesCount() > 3)) {
                        rem = i % 3;
                        div = i / 3;
                        name = nodeNames[rem] + " ".concat(String.valueOf(div + 1));
                    } else {
                        name = nodeNames[i];
                    }
                    if (getDataObj().isFixed()) {
                        name = name + " (f)";
                    }
                } else {
                    name = ((ParametersSubNode) getParentNode().getChildren().getNodes()[i]).getDisplayName();
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
