/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import org.glotaran.core.models.tgm.OscspecPanelModel;
import org.glotaran.core.ui.visualmodelling.common.EnumPropertyEditor;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.OscParametersKeys;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author jsnel
 */
public class OscspecParametersNode extends PropertiesAbstractNode {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/IRFpar_16.png", true);
    private EnumTypes.OscSpecTypes cohspecTypeProperty = EnumTypes.OscSpecTypes.HARMONIC;
    OscspecPanelModel oscspecPanelModel;
    private Integer oscNumber = 1;
    private String[] propNames = new String[]{"Name", "Type", "Number"};
    Property<Integer> oscNumberProp = null;

    public OscspecParametersNode(PropertyChangeListener listn) {
        super("Oscpar", new OscParametersKeys(3));        
        setOscSpecType(cohspecTypeProperty);
        addPropertyChangeListener(listn);
        
    }

    public OscspecParametersNode(TgmDataObject tgmDO, PropertyChangeListener listn) {
        super("Oscpar", new OscParametersKeys(tgmDO.getTgm().getDat().getOscspecPanel().getOscpar(), tgmDO.getTgm().getDat().getOscspecPanel().getOscspec().getFixed()), Lookups.singleton(tgmDO));
        this.oscspecPanelModel = tgmDO.getTgm().getDat().getOscspecPanel();         
        if (oscspecPanelModel.getOscspec().getType() == null) {
            setOscSpecType(EnumTypes.OscSpecTypes.HARMONIC);
            oscNumber = 1;
        } else {
            this.cohspecTypeProperty = cohspecTypeProperty.setFromStr(oscspecPanelModel.getOscspec().getType());
            if (cohspecTypeProperty.equals(EnumTypes.OscSpecTypes.HARMONIC)) {
                oscNumber = (oscspecPanelModel.getOscpar().size()) / 3;
            }
            setOscSpecType(cohspecTypeProperty);
        }
        addPropertyChangeListener(listn);
       
    }

    public Integer getOscNumberProp() {
        return oscNumber;

    }

    public void setOscNumberProp(Integer newOscNumber) {
        int oldVal = this.oscNumber;
        OscParametersKeys childColection = (OscParametersKeys) getChildren();

        if (newOscNumber < 0) {
            childColection.removeParams(childColection.getNodesCount());
            this.oscNumber = 0;
        } else {
            if (this.oscNumber < newOscNumber) {
                childColection.addDefaultObj((newOscNumber - this.oscNumber) * 3);
            } else {
                childColection.removeParams((this.oscNumber - newOscNumber) * 3);
            }
            this.oscNumber = newOscNumber;
        }
        
        firePropertyChange("oscNumber", oldVal, this.oscNumber);
    }

    @Override
    public String getDisplayName() {
        String name = super.getDisplayName();
        name = name + " (" + cohspecTypeProperty.toString() + ")";
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
        PropertySupport.Reflection<EnumTypes.OscSpecTypes> oscspecType = null;
        Property<String> name = null;

        try {
            oscspecType = new PropertySupport.Reflection<>(this, EnumTypes.OscSpecTypes.class, "getOscSpecType", "setOscSpecType");
            oscspecType.setPropertyEditorClass(EnumPropertyEditor.class);
            name = new PropertySupport.Reflection<>(this, String.class, "getDisplayName", null);
            oscNumberProp = new PropertySupport.Reflection<>(this, Integer.class, "oscNumberProp");
            oscspecType.setName(propNames[1]);
            name.setName(propNames[0]);
            oscNumberProp.setName(propNames[2]);

        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }

        set.put(name);
        set.put(oscspecType);
        set.put(oscNumberProp);
        sheet.put(set);
        return sheet;
    }

    public final void setOscSpecType(EnumTypes.OscSpecTypes oscspecType) {
        OscParametersKeys childColection = (OscParametersKeys) getChildren();
        int currCompNum = childColection.getNodesCount();
        if (oscspecType.equals(EnumTypes.OscSpecTypes.HARMONIC)) {
        }

        cohspecTypeProperty = oscspecType;
        fireDisplayNameChange(null, getDisplayName());
        firePropertyChange("setOscSpecType", null, cohspecTypeProperty);
    }

//    public TgmDataObject getTgmDataObject() {
//        return getLookup().lookup(TgmDataObject.class);
//    }
    public EnumTypes.OscSpecTypes getOscSpecType() {
        return cohspecTypeProperty;
    }

}
