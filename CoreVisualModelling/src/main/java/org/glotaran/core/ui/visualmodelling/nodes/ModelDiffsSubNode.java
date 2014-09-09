/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Formatter;
import org.glotaran.core.models.tgm.Tgm;
import org.glotaran.core.ui.visualmodelling.common.VisualCommonFunctions;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.ModelDiffsDO;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
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
public class ModelDiffsSubNode extends PropertiesAbstractNode implements PropertyChangeListener {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/DiffsSubnode_16.png", true);
    private String[] parameters = null;
    private int[] paramInd = null;
    private String[] paramValues = null;
    private int[] paramValInd = null;
    private Integer selectedType = new Integer(0);
    private String[] paramNames = new String[]{"kinpar", "irfpar", "parmu", "partau"};

    public ModelDiffsSubNode(ModelDiffsDO data) {
        super("parameter", Children.LEAF, Lookups.singleton(data));
        data.addPropertyChangeListener(WeakListeners.propertyChange(this, data));
    }

    @Override
    public Image getIcon(int type) {
        return ICON;
    }

    public ModelDiffsDO getDataObj() {
        return getLookup().lookup(ModelDiffsDO.class);
    }

    private Tgm getConnectedModel() {
        Node node = this;
        while (!node.getClass().equals(DatasetsRootNode.class)) {
            node = node.getParentNode();
        }
        DatasetsRootNode rootNode = (DatasetsRootNode) node;
        return rootNode.getContainerComponent().getConnectedModel();
    }

    private void getParamsFromModel() {
        ArrayList<String> tempPar = new ArrayList<String>();
        Tgm model = getConnectedModel();

        if (!model.getDat().getKinparPanel().getKinpar().isEmpty()) {
            tempPar.add(paramNames[0]);
        }
        if (!model.getDat().getIrfparPanel().getIrf().isEmpty()) {
            tempPar.add(paramNames[1]);
        }
        if (model.getDat().getIrfparPanel().getParmu()!=null && !model.getDat().getIrfparPanel().getParmu().isEmpty()) {
            tempPar.add(paramNames[2]);
        }
        if (model.getDat().getIrfparPanel().getPartau()!=null && !model.getDat().getIrfparPanel().getPartau().isEmpty()) {
            tempPar.add(paramNames[3]);
        }
        parameters = new String[tempPar.size()];
        paramInd = new int[tempPar.size()];
        for (int i = 0; i < tempPar.size(); i++) {
            parameters[i] = tempPar.get(i);
            paramInd[i] = i;
        }
        if (getDataObj().getWhat() != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].equalsIgnoreCase(getDataObj().getWhat())) {
                    selectedType = i;
                }
            }
        } else {
            getDataObj().setWhat(parameters[0]);
        }
    }

    private void getParamList() {
        Tgm model = getConnectedModel();
        if (parameters[selectedType].equalsIgnoreCase(paramNames[0])) {
            paramValues = new String[model.getDat().getKinparPanel().getKinpar().size()];
            paramValInd = new int[model.getDat().getKinparPanel().getKinpar().size()];
            for (int i = 0; i < model.getDat().getKinparPanel().getKinpar().size(); i++) {
                paramValInd[i] = i;
                paramValues[i] = "k" + (i + 1) + " ("
                        + new Formatter().format("%g", model.getDat().getKinparPanel().getKinpar().get(i).getStart()).toString() + ")";
            }
        }
        if (parameters[selectedType].equalsIgnoreCase(paramNames[1])) {
            paramValues = new String[model.getDat().getIrfparPanel().getIrf().size()];
            paramValInd = new int[model.getDat().getIrfparPanel().getIrf().size()];
            for (int i = 0; i < model.getDat().getIrfparPanel().getIrf().size(); i++) {
                paramValInd[i] = i;
                paramValues[i] = "irf" + (i + 1) + " ("
                        + new Formatter().format("%g", model.getDat().getIrfparPanel().getIrf().get(i)).toString() + ")";
            }
        }

        if (parameters[selectedType].equalsIgnoreCase(paramNames[2])) {
            ArrayList<Double> parMu = VisualCommonFunctions.strToParams(model.getDat().getIrfparPanel().getParmu());
            paramValues = new String[parMu.size()];
            paramValInd = new int[parMu.size()];
            for (int i = 0; i < parMu.size(); i++) {
                paramValInd[i] = i;
                paramValues[i] = "parmu" + (i + 1) + " ("
                        + new Formatter().format("%g", parMu.get(i)).toString() + ")";
            }
        }
        if (parameters[selectedType].equalsIgnoreCase(paramNames[3])) {
            ArrayList<Double> parTau = VisualCommonFunctions.strToParams(model.getDat().getIrfparPanel().getPartau());
            paramValues = new String[parTau.size()];
            paramValInd = new int[parTau.size()];
            for (int i = 0; i < parTau.size(); i++) {
                paramValInd[i] = i;
                paramValues[i] = "partau" + (i + 1) + " ("
                        + new Formatter().format("%g", parTau.get(i)).toString() + ")";
            }
        }

    }

    private Property<Integer> createFreParProperty() {
        getParamList();
        Property<Integer> paramIndex = new Property<Integer>(Integer.class) {

            @Override
            public boolean canRead() {
                return true;
            }

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                int index = 0;
                if (getDataObj().getIndex()!=null) {
                    index = getDataObj().getIndex()>=0 ? getDataObj().getIndex() : 0;
                }
                return index;
            }

            @Override
            public boolean canWrite() {
                return true;
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                getDataObj().setIndex(val);
            }
        };
        paramIndex.setValue("intValues", paramValInd);
        paramIndex.setValue("stringKeys", paramValues);
        paramIndex.setName("ParamValue");
        return paramIndex;
    }

    private Property<Integer> createFreParTypeProperty() {
        getParamsFromModel();
        Property<Integer> freeParm = new Property<Integer>(Integer.class) {

            @Override
            public boolean canRead() {
                return true;
            }

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return selectedType;
            }

            @Override
            public boolean canWrite() {
                return true;
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                selectedType = val;
                getDataObj().setWhat(paramNames[selectedType]);
                updateFreeParProperty();
                fireDisplayNameChange(null, getDisplayName());
            }
        };
        freeParm.setValue("intValues", paramInd);
        freeParm.setValue("stringKeys", parameters);
        freeParm.setName("ParamType");
        return freeParm;
    }

    private void updateFreeParProperty() {
        getDataObj().setIndex(0);
        getSheet().get(Sheet.PROPERTIES).remove("ParamValue");
        getSheet().get(Sheet.PROPERTIES).put(createFreParProperty());
    }

    @Override
    public String getDisplayName() {
        if (getDataObj().getWhat() != null) {
            return getDataObj().getWhat();
        }
        return paramNames[selectedType];
//        return new Formatter().format("%g",getLookup().lookup(ModelDiffsDO.class).getStart()).toString();
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        ModelDiffsDO obj = getDataObj();
        Property<String> modelDiffType = null;
        Property<Double> startingValue = null;
        try {
            modelDiffType = new PropertySupport.ReadOnly<String>("Type", String.class, "Type", "Type of the modeldifference") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return ((ModelDiffsNode) getParentNode()).getType();
                }
            };
            startingValue = new PropertySupport.Reflection<Double>(obj, Double.class, "start");
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        startingValue.setName("Starting value");

        set.put(modelDiffType);
        set.put(startingValue);
        set.put(createFreParTypeProperty());
        set.put(createFreParProperty());
        sheet.put(set);
        getDataObj().setWhat(paramNames[selectedType]);
        getDataObj().setIndex(getDataObj().getIndex());
        return sheet;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        int ind = 0;
        for (int i = 0; i < getParentNode().getChildren().getNodes().length; i++) {
            if (this.equals(getParentNode().getChildren().getNodes()[i])) {
                ind = i;
            }
        }
        ((PropertiesAbstractNode) this.getParentNode()).fire(ind, evt);

    }

    @Override
    public void destroy() throws IOException {
        propertyChange(new PropertyChangeEvent(this, "delete", null, null));
        PropertiesAbstractNode parent = (PropertiesAbstractNode) getParentNode();
        super.destroy();
        parent.updateName();
    }
}
