/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Formatter;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.NonLinearParameter;
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
public class ParametersSubNode extends PropertiesAbstractNode implements PropertyChangeListener {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/Subnode_16.png", true);
//    private NonLinearParameter dataObj;

    public ParametersSubNode(NonLinearParameter data) {
        super("parameter", Children.LEAF, Lookups.singleton(data));
        data.addPropertyChangeListener(WeakListeners.propertyChange(this, data));
    }

    @Override
    public Image getIcon(int type) {
        return ICON;
    }

    public NonLinearParameter getDataObj() {
        return getLookup().lookup(NonLinearParameter.class);
    }

    @Override
    public String getDisplayName() {
        String name = new Formatter().format("%g", getLookup().lookup(NonLinearParameter.class).getStart()).toString();
        if (getLookup().lookup(NonLinearParameter.class).isFixed() == null) {
            getLookup().lookup(NonLinearParameter.class).setFixed(false);
        }
        if (getLookup().lookup(NonLinearParameter.class).isFixed()) {
            name = name + " (f)";
        }
        return name;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        NonLinearParameter obj = getLookup().lookup(NonLinearParameter.class);

        Property<Double> startingValue = null;
        Property<Boolean> fixedValue = null;
        Property<Boolean> constrainedValue = null;
        Property<Double> constrainedMin = null;
        Property<Double> constrainedMax = null;

        try {
            startingValue = new PropertySupport.Reflection<Double>(obj, Double.class, "start");
            fixedValue = new PropertySupport.Reflection<Boolean>(obj, Boolean.class, "isFixed", "setFixed");
            constrainedValue = new PropertySupport.Reflection<Boolean>(obj, Boolean.class, "isConstrained", "setConstrained");
            constrainedMin = new PropertySupport.Reflection<Double>(obj, Double.class, "minimum");
            constrainedMax = new PropertySupport.Reflection<Double>(obj, Double.class, "maximum");
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        startingValue.setName("Starting value");
        fixedValue.setName("Value fixed");
        constrainedValue.setName("Value constrained");
        constrainedMin.setName("Minimal value");
        constrainedMax.setName("Maximal value");

        set.put(startingValue);
        set.put(fixedValue);
        set.put(constrainedValue);
        set.put(constrainedMin);
        set.put(constrainedMax);
        sheet.put(set);
        return sheet;
    }

    @Override
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

    @Override
    public void destroy() throws IOException {
        PropertiesAbstractNode parent = (PropertiesAbstractNode) getParentNode();
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
}
