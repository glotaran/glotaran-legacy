/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import org.glotaran.core.models.tgm.CohspecPanelModel;
import org.glotaran.core.ui.visualmodelling.common.EnumPropertyEditor;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes.CohSpecTypes;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.NonLinearParameter;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.NonLinearParametersKeys;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author lsp
 */
public final class CohSpecNode extends PropertiesAbstractNode {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/Cohspecpar_16.png", true);
    private EnumTypes.CohSpecTypes cohSpecModelType = EnumTypes.CohSpecTypes.IRF;
//    private CohspecPanelModel cohspecPanel;
    private Boolean cohSpecClp0 = Boolean.FALSE;
    private Boolean cohSpecSeq = Boolean.FALSE;
    private Double cohSpecClpMin = 0.0;
    private Double cohSpecClpMax = 0.0;
    private String[] propertyNames = new String[]{"cohSpecName", "cohSpecModelType", "cohSpecClpZero", "cohSpecClpMin", "cohSpecClpMax", "cohSpecSeqNumber"};
    private String[] propertyDisplayNames = new String[]{"Name", "Model type", "Constrain to 0", "From (min)", "To (max)", "Number of seq starting values "};
    private String[] propertyShortDescription = new String[]{
        "Coherent artifact/scatter component(s)",
        "The type of the time profile for the coherent artifact/scatter components(s). <br>"
        + "Currently only the <b>Irf</b> type is implemented.",
        "Constrains the spectrum of the coherent artifact/scatter component(s) to <b>zero</b> from and to a certain value.",
        "The value <b>from</b> which to contrain the coherent artifact/scatter component(s) to zero",
        "The value <b>to</b> which to contrain the coherent artifact/scatter component(s) to zero",
        "If the coherent artifact model type=\"seq\" or \"mix\" is selected this specifies the number of sequential exponential decays used to model the coherent artifact. This often models oscillating behavior well, where the number of oscillations is the number of parameter starting values. The starting values represent the rate of decay."};
    

    public CohSpecNode(PropertyChangeListener listn) {
        super("CohSpec", new NonLinearParametersKeys(0));
        this.addPropertyChangeListener(listn);
    }

    public CohSpecNode(CohspecPanelModel cohspecPanel, PropertyChangeListener listn) {
        super("CohSpec", new NonLinearParametersKeys(0));
        if (cohspecPanel.isClp0Enabled() != null) {
            setCohSpecClpZero(cohspecPanel.isClp0Enabled());
        }
        if (cohSpecClp0) {
            setCohSpecClpMin(cohspecPanel.getClp0Min());
            setCohSpecClpMax(cohspecPanel.getClp0Max());
        }
        setCohSpecModelType(cohSpecModelType.setFromStr(cohspecPanel.getCohspec().getType()));
        
        if (cohSpecModelType.equals(CohSpecTypes.SEQ)
                || cohSpecModelType.equals(CohSpecTypes.MIXED)) {            
            NonLinearParametersKeys params = (NonLinearParametersKeys) getChildren();
            
            for (int i = 0; i < cohspecPanel.getCohspec().getSeqstart().size(); i++) {
                params.addObj(new NonLinearParameter(cohspecPanel.getCohspec().getSeqstart().get(i), false));
            }
        }
        this.addPropertyChangeListener(listn);
        setShortDescription(propertyShortDescription[0]);
    }

    @Override
    public String getDisplayName() {
        String name = super.getDisplayName();
        if (getCompNum() != 0) { // and if seq == true
            name = name + " (" + getCompNum() + " " + cohSpecModelType + ")";
        } else {
            name = name + " (" + cohSpecModelType + ")";
        }
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

    public void initialize() {
        //firePropertyChange("cohSpecModelType", null, cohSpecModelType);
        firePropertyChange(propertyNames[1], null, cohSpecModelType);
    }

    public CohSpecTypes getCohSpecModelType() {
        return cohSpecModelType;
    }

    public void setCohSpecModelType(CohSpecTypes cohSpecModelType) {
        this.cohSpecModelType = cohSpecModelType;
        Property<Integer> numberOfComponents = null;
        if (cohSpecModelType.equals(CohSpecTypes.SEQ)
                || cohSpecModelType.equals(CohSpecTypes.MIXED)) {
            try {
                numberOfComponents = new PropertySupport.Reflection<Integer>(this, Integer.class, "getCompNum", "setCompNum");
//Add properties for cohspec = seq
                numberOfComponents.setName(propertyNames[5]);
                numberOfComponents.setDisplayName(propertyDisplayNames[5]);
                numberOfComponents.setShortDescription(propertyShortDescription[5]);
                getSheet().get(Sheet.PROPERTIES).put(numberOfComponents);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            getSheet().get(Sheet.PROPERTIES).remove(propertyNames[5]);
            setCompNum(0);
        }

        fireDisplayNameChange(null, getDisplayName());
        //firePropertyChange("cohSpecModelType", null, cohSpecModelType);
        firePropertyChange(propertyNames[1], null, cohSpecModelType);
    }

    public Boolean getCohSpecClpZero() {
        return cohSpecClp0;
    }

    public void setCohSpecClpZero(Boolean cohSpecClp0) {
        this.cohSpecClp0 = cohSpecClp0;
        if (cohSpecClp0) {
            try {
                Property<Double> clpminimum = new PropertySupport.Reflection<Double>(this, Double.class, propertyNames[3]);
                Property<Double> clpmaximum = new PropertySupport.Reflection<Double>(this, Double.class, propertyNames[4]);
                clpminimum.setName(propertyNames[3]);
                clpminimum.setDisplayName(propertyDisplayNames[3]);
                clpminimum.setShortDescription(propertyShortDescription[3]);
                clpmaximum.setName(propertyNames[4]);
                clpmaximum.setDisplayName(propertyDisplayNames[4]);
                clpmaximum.setShortDescription(propertyShortDescription[4]);
                getSheet().get(Sheet.PROPERTIES).put(clpminimum);
                getSheet().get(Sheet.PROPERTIES).put(clpmaximum);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            getSheet().get(Sheet.PROPERTIES).remove(propertyNames[3]);
            getSheet().get(Sheet.PROPERTIES).remove(propertyNames[4]);
        }
        firePropertyChange(propertyNames[2], null, cohSpecClp0);
    }

    public Double getCohSpecClpMin() {
        return cohSpecClpMin;
    }

    public void setCohSpecClpMin(Double clpMin) {
        this.cohSpecClpMin = clpMin;
        firePropertyChange(propertyNames[3], null, clpMin);
    }

    public Double getCohSpecClpMax() {
        return cohSpecClpMax;
    }

    public void setCohSpecClpMax(Double clpMax) {
        this.cohSpecClpMax = clpMax;
        fireDisplayNameChange(null, getDisplayName());
        firePropertyChange(propertyNames[4], null, clpMax);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        Property<String> cohSpecNameProperty = null;
        PropertySupport.Reflection<EnumTypes.CohSpecTypes> cohSpecModelTypeProperty = null;
        Property<Boolean> cohSpecClp0Property = null;

        try {
            cohSpecNameProperty = new PropertySupport.Reflection<String>(this, String.class, "getDisplayName", null);
            cohSpecModelTypeProperty = new PropertySupport.Reflection<EnumTypes.CohSpecTypes>(this, EnumTypes.CohSpecTypes.class, propertyNames[1]);
            cohSpecModelTypeProperty.setPropertyEditorClass(EnumPropertyEditor.class); //EnumPropertyEditor.class;
            cohSpecClp0Property = new PropertySupport.Reflection<Boolean>(this, Boolean.class, propertyNames[2]);

        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        //CohSpec Name
        cohSpecNameProperty.setName(propertyNames[0]);
        cohSpecNameProperty.setDisplayName(propertyDisplayNames[0]);
        cohSpecNameProperty.setShortDescription(propertyShortDescription[0]);
        //CohSpec Model
        cohSpecModelTypeProperty.setName(propertyNames[1]);
        cohSpecModelTypeProperty.setDisplayName(propertyDisplayNames[1]);
        cohSpecModelTypeProperty.setShortDescription(propertyShortDescription[1]);
        //CohSpec Zero Constraint
        cohSpecClp0Property.setName(propertyNames[2]);
        cohSpecClp0Property.setDisplayName(propertyDisplayNames[2]);
        cohSpecClp0Property.setShortDescription(propertyShortDescription[2]);

        //Add all CohSpec properties to the set
        set.put(cohSpecNameProperty);
        set.put(cohSpecModelTypeProperty);
        set.put(cohSpecClp0Property);
        //Finally add the set to the Property Sheet
        sheet.put(set);

        return sheet;
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

    @Override
    public void fire(int index, PropertyChangeEvent evt) {
        if ("start".equals(evt.getPropertyName())) {
            firePropertyChange("start", propertyNames[1], evt.getNewValue());
        }
        if ("fixed".equals(evt.getPropertyName())) {
            firePropertyChange("fixed", propertyNames[1], evt.getNewValue());
        }
        if ("delete".equals(evt.getPropertyName())) {
            firePropertyChange("delete", propertyNames[1], evt.getNewValue());
        }
    }
}
