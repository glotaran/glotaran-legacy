/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.WeightParameter;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author slapten
 */
public class WeighParametersSubNode extends PropertiesAbstractNode implements PropertyChangeListener {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/Subnode_16.png", true);
//    private NonLinearParameter dataObj;

    public WeighParametersSubNode(WeightParameter data) {
        super("Weight parameter", Children.LEAF, Lookups.singleton(data));
        data.addPropertyChangeListener(WeakListeners.propertyChange(this, data));
    }

    @Override
    public Image getIcon(int type) {
        return ICON;
    }

    public WeightParameter getDataObj() {
        return getLookup().lookup(WeightParameter.class);
    }
//
//    @Override
//    public String getDisplayName() {
////        return new Formatter().format("%g",getLookup().lookup(NonLinearParameter.class).getStart()).toString();
//    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        WeightParameter obj = getLookup().lookup(WeightParameter.class);

        Property min1Value = null;
        Property min2Value = null;
        Property max1Value = null;
        Property max2Value = null;
        Property weightValue = null;

        try {
            min1Value = new PropertySupport.Reflection(obj, Double.class, "min1");
            min2Value = new PropertySupport.Reflection(obj, Double.class, "min2");
            max1Value = new PropertySupport.Reflection(obj, Double.class, "max1");
            max2Value = new PropertySupport.Reflection(obj, Double.class, "max2");
            weightValue = new PropertySupport.Reflection(obj, Double.class, "weight");
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        min1Value.setName("Min1 value");
        min2Value.setName("Min2 value");
        max1Value.setName("Max1 value");
        max2Value.setName("Max2 value");
        weightValue.setName("Weight value");

        set.put(min1Value);
        set.put(max1Value);
        set.put(min2Value);
        set.put(max2Value);
        set.put(weightValue);
        sheet.put(set);
        return sheet;
    }

    @Override
    public void destroy() throws IOException {
        WeightParametersNode parent = (WeightParametersNode) getParentNode();
        int ind = 0;
        for (int i = 0; i < parent.getChildren().getNodes().length; i++) {
            if (this.equals(parent.getChildren().getNodes()[i])) {
                ind = i;
            }
        }
        parent.fire(ind, new PropertyChangeEvent(this, "delete", null, ind));
        super.destroy();
        parent.updateName();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        int ind = 0;
        for (int i = 0; i < getParentNode().getChildren().getNodes().length; i++) {
            if (this.equals(getParentNode().getChildren().getNodes()[i])) {
                ind = i;
            }
        }
        ((PropertiesAbstractNode) this.getParentNode()).fire(ind, evt);
        this.fireDisplayNameChange(null, getDisplayName());
    }
}
