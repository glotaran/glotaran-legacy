/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import org.glotaran.core.models.tgm.IrfparPanelModel;
import org.glotaran.core.ui.visualmodelling.common.EnumPropertyEditor;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.common.MeasuredIRFPropertyEditor;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.IrfParametersKeys;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author slapten
 */
public class IrfParametersNode extends PropertiesAbstractNode {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/IRFpar_16.png", true);
    private EnumTypes.IRFTypes irfTypeProperty = EnumTypes.IRFTypes.GAUSSIAN;
    private Boolean backSweep = false;
    private Double sweepPeriod = null;
    private Integer multiGaussNum = 1;
    IrfparPanelModel irfparPanel;
    private String[] propNames = new String[]{"Name", "Type", "Use backsweep", "Sweep period", "Measured IRF", "Extra gaussians"};

    public IrfParametersNode(PropertyChangeListener listn) {
        super("IRFPar", new IrfParametersKeys(2));
        setIRFType(irfTypeProperty);
        addPropertyChangeListener(listn);
    }

    public IrfParametersNode(TgmDataObject tgmDO, PropertyChangeListener listn) {
        super("IRFPar", new IrfParametersKeys(tgmDO.getTgm().getDat().getIrfparPanel().getIrf(), tgmDO.getTgm().getDat().getIrfparPanel().getFixed()), Lookups.singleton(tgmDO));
        this.irfparPanel = tgmDO.getTgm().getDat().getIrfparPanel();
        if (irfparPanel.isBacksweepEnabled() != null) {
            if (irfparPanel.isBacksweepEnabled()) {
                backSweep = irfparPanel.isBacksweepEnabled();
                sweepPeriod = irfparPanel.getBacksweepPeriod();
            }
        }
        if (irfparPanel.isMirf() != null) {
            if (irfparPanel.isMirf()) {
                setIRFType(EnumTypes.IRFTypes.MEASURED_IRF);
            } else {
                if (irfparPanel.getIrftype() == null) {
                    if (irfparPanel.getIrf().size() == 2) {
                        setIRFType(EnumTypes.IRFTypes.GAUSSIAN);
                    } else {
                        setIRFType(EnumTypes.IRFTypes.DOUBLE_GAUSSIAN);
                    }
                } else {
                    this.irfTypeProperty = irfTypeProperty.setFromStr(irfparPanel.getIrftype());
                    if (irfTypeProperty.equals(EnumTypes.IRFTypes.MULTIPLE_GAUSSIAN)) {
                        multiGaussNum = (irfparPanel.getIrf().size() - 2) / 3;

                    }
                    setIRFType(irfTypeProperty);
                }
            }
        }
        addPropertyChangeListener(listn);
    }

    public Boolean getBackSweep() {
        return backSweep;
    }

    public void setBackSweep(Boolean backSweep) {
        this.backSweep = backSweep;
        if (backSweep) {
            try {
                Property<Double> sweepPeriodValue = new PropertySupport.Reflection<Double>(this, Double.class, "sweepPeriod");
                sweepPeriodValue.setName(propNames[3]);
                getSheet().get(Sheet.PROPERTIES).put(sweepPeriodValue);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            getSheet().get(Sheet.PROPERTIES).remove(propNames[3]);
        }
        firePropertyChange("SetBackSweep", null, backSweep);
    }

    public Integer getMultiGaussNum() {
        return multiGaussNum;

    }

    public void setMultiGaussNum(Integer newMultiGaussNum) {
        int oldVal = this.multiGaussNum;
        IrfParametersKeys childColection = (IrfParametersKeys) getChildren();
        if (newMultiGaussNum < 0) {
             childColection.removeParams(childColection.getNodesCount());
            this.multiGaussNum = 0;
        } else {
            if (this.multiGaussNum < newMultiGaussNum) {
                childColection.addDefaultObj((newMultiGaussNum - this.multiGaussNum) * 3);
            } else {
                childColection.removeParams((this.multiGaussNum - newMultiGaussNum) * 3);

            }
            this.multiGaussNum = newMultiGaussNum;
        }
        firePropertyChange("multiGausNum", oldVal, this.multiGaussNum);
    }

    public Double getSweepPeriod() {
        return sweepPeriod;
    }

    public void setSweepPeriod(Double sweepPeriod) {
        this.sweepPeriod = sweepPeriod;
        firePropertyChange("SetBackSweepPeriod", null, sweepPeriod);
    }

    @Override
    public String getDisplayName() {
        String name = super.getDisplayName();
        name = name + " (" + irfTypeProperty.toString() + ")";
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
        PropertySupport.Reflection<EnumTypes.IRFTypes> irfType = null;
        Property<String> name = null;
        Property<Boolean> sweep = null;
        try {
            irfType = new PropertySupport.Reflection<EnumTypes.IRFTypes>(this, EnumTypes.IRFTypes.class, "getIRFType", "setIRFType");
            irfType.setPropertyEditorClass(EnumPropertyEditor.class);
            name = new PropertySupport.Reflection<String>(this, String.class, "getDisplayName", null);
            sweep = new PropertySupport.Reflection<Boolean>(this, Boolean.class, "backSweep");

        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        irfType.setName(propNames[1]);
        name.setName(propNames[0]);
        sweep.setName(propNames[2]);

        set.put(name);
        set.put(irfType);
        set.put(sweep);
        sheet.put(set);
        return sheet;
    }

    public final void setIRFType(EnumTypes.IRFTypes irfType) {
        IrfParametersKeys childColection = (IrfParametersKeys) getChildren();
        int currCompNum = childColection.getNodesCount();
        if (irfType.equals(EnumTypes.IRFTypes.GAUSSIAN)) {
            if (irfTypeProperty == EnumTypes.IRFTypes.MEASURED_IRF) {
                childColection.backFromMeasuredIrf();
                childColection = (IrfParametersKeys) getChildren();
                currCompNum = childColection.getNodesCount();
            }
            if (currCompNum < 2) {
                childColection.addDefaultObj(2 - currCompNum);
            } else {
                childColection.removeParams(currCompNum - 2);
            }
            getSheet().get(Sheet.PROPERTIES).remove(propNames[5]);
            addStreackProp();
        }
        if (irfType.equals(EnumTypes.IRFTypes.DOUBLE_GAUSSIAN)) {
            if (irfTypeProperty == EnumTypes.IRFTypes.MEASURED_IRF) {
                childColection.backFromMeasuredIrf();
                childColection = (IrfParametersKeys) getChildren();
                currCompNum = childColection.getNodesCount();
            }
            if (currCompNum < 4) {
                childColection.addDefaultObj(4 - currCompNum);
            } else {
                childColection.removeParams(currCompNum - 4);
            }
            getSheet().get(Sheet.PROPERTIES).remove(propNames[5]);
            addStreackProp();
        }

        if (irfType.equals(EnumTypes.IRFTypes.MULTIPLE_GAUSSIAN)) {
            if (irfTypeProperty == EnumTypes.IRFTypes.MEASURED_IRF) {
                childColection.backFromMeasuredIrf();
                childColection = (IrfParametersKeys) getChildren();
                currCompNum = childColection.getNodesCount();
            }
            if (currCompNum == 2) {
                childColection.addDefaultObj(multiGaussNum * 3);
            }
            if (currCompNum == 4) {
                childColection.removeParams(2);
                childColection.addDefaultObj(multiGaussNum * 3);
            }
            if (backSweep) {
                getSheet().get(Sheet.PROPERTIES).remove(propNames[3]);
            }
            getSheet().get(Sheet.PROPERTIES).remove(propNames[2]);
            addMultipleGaussianProperties();
            addStreackProp();
        }
        if (irfType.equals(EnumTypes.IRFTypes.MEASURED_IRF)) {
            childColection.setMeasuredIrf();
            if (backSweep) {
                getSheet().get(Sheet.PROPERTIES).remove(propNames[3]);
            }
            getSheet().get(Sheet.PROPERTIES).remove(propNames[2]);
            addMeasuredIrfProp();
        }
        irfTypeProperty = irfType;
        fireDisplayNameChange(null, getDisplayName());
        firePropertyChange("setIRFType", null, irfTypeProperty);
    }

    public TgmDataObject getTgmDataObject() {
        return getLookup().lookup(TgmDataObject.class);
    }
    
    public EnumTypes.IRFTypes getIRFType() {
        return irfTypeProperty;
    }

    private void addMultipleGaussianProperties() {
        try {
            Property<Integer> extraGaussNumber = new PropertySupport.Reflection<Integer>(this, Integer.class, "multiGaussNum");
            extraGaussNumber.setName(propNames[5]);
            getSheet().get(Sheet.PROPERTIES).put(extraGaussNumber);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void addStreackProp() {
        try {
            Property<Boolean> sweep = new PropertySupport.Reflection<Boolean>(this, Boolean.class, "backSweep");
            sweep.setName(propNames[2]);
            getSheet().get(Sheet.PROPERTIES).put(sweep);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (backSweep) {
            try {
                Property<Double> sweepPeriodValue = new PropertySupport.Reflection<Double>(this, Double.class, "sweepPeriod");
                sweepPeriodValue.setName(propNames[3]);
                getSheet().get(Sheet.PROPERTIES).put(sweepPeriodValue);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void addMeasuredIrfProp() { //it should not be here, will be moved to lower level. 
        PropertySupport.Reflection measuredIRF;
        try {
            measuredIRF = new PropertySupport.Reflection(this, TgmDataObject.class, "getTgmDataObject", null);
            measuredIRF.setPropertyEditorClass(MeasuredIRFPropertyEditor.class);
            measuredIRF.setName(propNames[4]);
            getSheet().get(Sheet.PROPERTIES).put(measuredIRF);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
//    @Override
//    public void fire(int index, PropertyChangeEvent evt){
//        if ("start".equals(evt.getPropertyName())) {
//            firePropertyChange("start", index, evt.getNewValue());
//        }
//        if ("fixed".equals(evt.getPropertyName())) {
//            firePropertyChange("fixed", index, evt.getNewValue());
//        }
//    }

}
