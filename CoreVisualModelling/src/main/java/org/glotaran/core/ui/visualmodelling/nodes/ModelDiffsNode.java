/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.glotaran.core.models.gta.GtaChangesModel;
import org.glotaran.core.models.gta.GtaModelDiffDO;
import org.glotaran.core.ui.visualmodelling.components.DatasetContainerComponent;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.ModelDiffsDO;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.ModelDiffsParametersKeys;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author slapten
 */
public class ModelDiffsNode extends PropertiesAbstractNode {

    private final Image FREE_ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/FreeParam_16.png", true);
    private final Image ADD_ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/AddParam_16.png", true);
    private final Image REMOVE_ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/RemoveParam_16.png", true);
    private PropertyChangeListener propListner;
    private int datasetIndex;

    public ModelDiffsNode(String type, PropertyChangeListener listn, int datasetInd) {
        super(type, new ModelDiffsParametersKeys(0));
        propListner = listn;
        datasetIndex = datasetInd;
        addPropertyChangeListener(propListner);
    }

    public ModelDiffsNode(String type, int datasetInd, List<GtaModelDiffDO> diffs, PropertyChangeListener listn) {
        super(type, new ModelDiffsParametersKeys(0));
        propListner = listn;
        datasetIndex = datasetInd + 1;
        ModelDiffsParametersKeys childColection = (ModelDiffsParametersKeys) getChildren();
        for (int i = 0; i < diffs.size(); i++) {
            childColection.addObj(new ModelDiffsDO(diffs.get(i)));
        }
        addPropertyChangeListener(propListner);
    }

    public ModelDiffsNode(String type, int datasetInd, List<GtaChangesModel> changes, DatasetContainerComponent aThis) {
        super(type, new ModelDiffsParametersKeys(0));
    }

    @Override
    public String getDisplayName() {
        String name = super.getDisplayName();
        name = name + " (" + String.valueOf(getChildren().getNodesCount()) + ")";
        return name;
    }

    @Override
    public Image getIcon(int type) {
        if (super.getDisplayName().equals("FreeParameter")) {
            return FREE_ICON;
        }
        if (super.getDisplayName().equals("AddParameter")) {
            return ADD_ICON;
        }
        if (super.getDisplayName().equals("RemoveParameter")) {
            return REMOVE_ICON;
        }
        return super.getIcon(type);
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

        try {
            numberOfComponents = new PropertySupport.Reflection<Integer>(this, Integer.class, "getCompNum", "setCompNum");
            name = new PropertySupport.Reflection<String>(this, String.class, "getDisplayName", null);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        numberOfComponents.setName("Number of components");
        name.setName("Name");
        set.put(name);
        set.put(numberOfComponents);
        sheet.put(set);
        return sheet;
    }

    public Integer getCompNum() {
        return getChildren().getNodesCount();
    }

    public void setCompNum(Integer compNum) {
        ModelDiffsParametersKeys childColection = (ModelDiffsParametersKeys) getChildren();
        int currCompNum = childColection.getNodesCount();
        if (currCompNum < compNum) {
            childColection.addDefaultObj(compNum - currCompNum, datasetIndex, super.getDisplayName());
        } else {
            childColection.removeParams(currCompNum - compNum, datasetIndex, super.getDisplayName());
        }
        fireDisplayNameChange(null, getDisplayName());
        firePropertyChange("Number of components", new Integer(currCompNum), compNum);
    }

    public int getDatasetIndex() {
        return datasetIndex;
    }
}
