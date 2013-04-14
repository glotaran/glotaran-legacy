/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import org.glotaran.core.models.tgm.IrfparPanelModel;
import org.glotaran.core.ui.visualmodelling.common.EnumPropertyEditor;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes.DispersionTypes;
import org.glotaran.core.ui.visualmodelling.common.VisualCommonFunctions;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.NonLinearParameter;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.NonLinearParametersKeys;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author slapten
 * @author jsnel

 */
public class DispersionModelingNode extends PropertiesAbstractNode {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/Dispersion_16.png", true);
    private EnumTypes.DispersionTypes disptype = EnumTypes.DispersionTypes.PARMU;
    private Double centralWave;
    private boolean single = true;

    public DispersionModelingNode(PropertyChangeListener listn) {
        super("Dispersion", new NonLinearParametersKeys(0));
        this.addPropertyChangeListener(listn);
    }

    public DispersionModelingNode(IrfparPanelModel irfparPanel, DispersionTypes dispersionTypes, PropertyChangeListener listn) {
        super("Dispersion", new NonLinearParametersKeys(0));
        NonLinearParametersKeys params = (NonLinearParametersKeys) getChildren();
        ArrayList<Double> paramList = new ArrayList<Double>();
        Boolean fixed = false;
        ArrayList<Boolean> paramFixedList = new ArrayList<Boolean>();
        if (dispersionTypes.equals(EnumTypes.DispersionTypes.PARMU)) {
            if (irfparPanel.getParmulist()!=null && !irfparPanel.getParmulist().isEmpty()) {
                paramList = (ArrayList<Double>) irfparPanel.getParmulist();
            } else {
                paramList = VisualCommonFunctions.strToParams(irfparPanel.getParmu());
            }
            if (irfparPanel.getParmufixedlist()!=null && !irfparPanel.getParmufixedlist().isEmpty()) {
                paramFixedList = (ArrayList<Boolean>) irfparPanel.getParmufixedlist();
            } else {
                fixed = irfparPanel.isParmufixed();
            }
        }
         if (dispersionTypes.equals(EnumTypes.DispersionTypes.PARTAU)) {
            if (irfparPanel.getPartaulist()!=null && !irfparPanel.getPartaulist().isEmpty()) {
                paramList = (ArrayList<Double>) irfparPanel.getPartaulist();
            } else {
                paramList = VisualCommonFunctions.strToParams(irfparPanel.getPartau());
            }
            if (irfparPanel.getPartaufixedlist()!=null && !irfparPanel.getPartaufixedlist().isEmpty()) {
                paramFixedList = (ArrayList<Boolean>) irfparPanel.getPartaufixedlist();
            } else {
                fixed = irfparPanel.isPartaufixed();
            }
        } 
        for (int i = 0; i < paramList.size(); i++) {

            if(fixed!=null && !fixed && !paramFixedList.isEmpty()) {
            params.addObj(new NonLinearParameter(paramList.get(i), paramFixedList.get(i)));
            } else {
            params.addObj(new NonLinearParameter(paramList.get(i), fixed));
            }
        }
        setDisptype(dispersionTypes);
        setCentralWave(irfparPanel.getLamda());
        this.addPropertyChangeListener(listn);
    }

    public DispersionModelingNode(DispersionTypes dispersionType, boolean single, PropertyChangeListener listn) {
        super("Dispersion", new NonLinearParametersKeys(0));
        this.setDisptype(dispersionType);
        this.single = single;
        this.addPropertyChangeListener(listn);
    }

    @Override
    public void destroy() throws IOException {
        firePropertyChange("mainNodeDeleted", disptype, null);
        super.destroyNode();
    }

    @Override
    public String getDisplayName() {
        String name = super.getDisplayName();
        name = name + " (" + disptype + ")";
        return name;
    }

    @Override
    public Image getIcon(int type) {
        return ICON;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        Property<Integer> numberOfComponents = null;
        Property<String> name = null;
        Property<Double> lambdaC = null;
        PropertySupport.Reflection<EnumTypes.DispersionTypes> dispType = null;
        try {
            dispType = (single)
                    ? new PropertySupport.Reflection<EnumTypes.DispersionTypes>(this, EnumTypes.DispersionTypes.class, "disptype")
                    : new PropertySupport.Reflection<EnumTypes.DispersionTypes>(this, EnumTypes.DispersionTypes.class, "getDisptype", null);
            dispType.setPropertyEditorClass(EnumPropertyEditor.class);
            numberOfComponents = new PropertySupport.Reflection<Integer>(this, Integer.class, "getCompNum", "setCompNum");
            name = new PropertySupport.Reflection<String>(this, String.class, "getDisplayName", null);
            lambdaC = new PropertySupport.Reflection<Double>(this, Double.class, "centralWave");
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        numberOfComponents.setName("Order of polinomial");
        name.setName("Name");
        dispType.setName("Dispersion type");
        lambdaC.setName("Central wave");
        set.put(name);
        set.put(dispType);
        set.put(numberOfComponents);
        set.put(lambdaC);
        sheet.put(set);
        return sheet;
    }

    public void recreateSheet() {
        setSheet(createSheet());
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public Integer getCompNum() {
        return getChildren().getNodesCount();
    }

    public void setCompNum(Integer compNum) {     
        NonLinearParametersKeys childColection = (NonLinearParametersKeys) getChildren();
        int currCompNum = childColection.getNodesCount();
        if (currCompNum < compNum) {
            childColection.addDefaultObj(compNum - currCompNum);
        } else {
            childColection.removeParams(currCompNum - compNum);
        }
        fireDisplayNameChange(null, getDisplayName());
        
        firePropertyChange("Number of components", new Integer(currCompNum), compNum);
    }

    public DispersionTypes getDisptype() {
        return disptype;
    }

    public void setDisptype(DispersionTypes disptype) {
        firePropertyChange("setDisptype", this.disptype, disptype);
        this.disptype = disptype;
        fireDisplayNameChange(null, getDisplayName());
    }

    public Double getCentralWave() {
        return centralWave;
    }

    public void setCentralWave(Double centralWave) {
        this.centralWave = centralWave;
        firePropertyChange("setCentralWave", this.disptype, centralWave);
    }

    @Override
    public void fire(int index, PropertyChangeEvent evt) {
        if ("start".equals(evt.getPropertyName())) {
            firePropertyChange("start", disptype, evt.getNewValue());
        }
        if ("fixed".equals(evt.getPropertyName())) {
            firePropertyChange("fixed", disptype, evt.getNewValue());
        }
        if ("delete".equals(evt.getPropertyName())) {
            firePropertyChange("delete", disptype, evt.getNewValue());
        }
    }
}
