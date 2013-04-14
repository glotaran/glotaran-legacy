/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.gta.GtaSimulationInputRef;
import org.glotaran.core.ui.visualmodelling.palette.PaletteItem;
import org.glotaran.core.ui.visualmodelling.palette.PaletteNode;
import org.glotaran.simfilesupport.spec.SpectralModelDataNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author jsg210
 */
public class SimInputComponentNode extends PropertiesAbstractNode implements Transferable {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/main/resources/doc.png", true);
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(SimInputComponentNode.class, "DatasetComponentNode");
    private SpectralModelDataNode sdn;
    private PropertyChangeListener propListner;
    private Double waveMin = 0.0;
    private Double waveMax = 0.0;
    private Double waveDelta = 0.0;
    private Double timeMin = 0.0;
    private Double timeMax = 1.0;
    private Double timeDelta = 1.0;
    private String[] propDisplayNames = new String[]{"Name", "Min wavelength", "Max wavelength", "Wavelength delta", "Min time", "Max time", "Time detla", "LLTB", "ULTB", "No. of log points"};
    private String[] propNames = new String[]{"Name", "lambdaMin", "lamdaMax", "lamdaDelta", "timeMin", "timeMax", "timeDelta", "lowerLinTimeBound", "upperLinTimeBound", "noLogPoints"};
    private String[] propDescription = new String[]{"Name", "lambdaMin", "lamdaMax", "lamdaDelta", "timeMin", "timeMax", "timeDelta", "lowerLinTimeBound", "upperLinTimeBound", "noLogPoints"};

    public SimInputComponentNode(String name, Children children) {
        super(name, children);
        name = sdn.getDisplayName();
    }

    public SimInputComponentNode(SpectralModelDataNode node, Children children, PropertyChangeListener propListn) {
        super(node.getDisplayName(), children);
        this.sdn = node;
        this.propListner = propListn;
        addPropertyChangeListener(propListn);
    }

    public SimInputComponentNode(SpectralModelDataNode node, Children children, Lookup lookup, PropertyChangeListener propListn) {
        super(node.getDisplayName(), children, lookup);
        this.sdn = node;
        this.propListner = propListn;
        addPropertyChangeListener(propListn);
    }

    @Override
    public String getDisplayName() {
        String name = super.getDisplayName();
        return name;
    }

    public Double getWaveMin() {
        return waveMin;
    }

    public void setWaveMin(Double value) {
        this.waveMin = value;
        sdn.getObject().getSim().setLambdaMin(value);
        sdn.getObject().setModified(true);
    }

    public Double getWaveMax() {
        return waveMax;
    }

    public void setWaveMax(Double value) {
        this.waveMax = value;
        sdn.getObject().getSim().setLambdaMax(value);
        sdn.getObject().setModified(true);
    }

    public Double getTimeMax() {
        return timeMax;
    }

    public void setTimeMax(Double value) {
        this.timeMax = value;
        sdn.getObject().getSim().setTimeMax(value);
        sdn.getObject().setModified(true);
    }

    public Double getTimeMin() {
        return timeMin;
    }

    public void setTimeMin(Double value) {
        this.timeMin = value;
        sdn.getObject().getSim().setTimeMin(value);
        sdn.getObject().setModified(true);
    }

    public Double getTimeDelta() {
        return timeDelta;
    }

    public void setTimeDelta(Double value) {
        this.timeDelta = value;
        sdn.getObject().getSim().setTimeStep(value);
        sdn.getObject().setModified(true);
    }

    public Double getWaveDelta() {
        return waveDelta;
    }

    public void setWaveDelta(Double value) {
        this.waveDelta = value;
        sdn.getObject().getSim().setLamdaStep(value);
        sdn.getObject().setModified(true);
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
    public Transferable drag() throws IOException {
        return this;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        Property<String> nameProp = null;
        Property<Double> lamdaMinProp = null;
        Property<Double> lamdaMaxProp = null;
        Property<Double> lamdaDeltaProp = null;
        Property<Double> timeMaxProp = null;
        Property<Double> timeMinProp = null;
        Property<Double> timeDeltaProp = null;
        nameProp = new PropertySupport.ReadOnly<String>(propNames[0], String.class, propDisplayNames[0], propDescription[0]) {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return sdn.getDisplayName();
            }
        };
        try {
            //1: lamdaMin
            //2: lamdaMax
            //3: lamdaDelta
            //4: timeMin
            //5: timeMax
            //6: timeDelta
            //7: LLTB (Lower Linear Time Bound)
            //8: ULTB (Upper Linear Time Bound)
            //9: noOfLogPoints (number of sample point in logarithmic domain)
            lamdaMinProp = new PropertySupport.Reflection<Double>(this, Double.class, "waveMin");
            lamdaMinProp.setName(propDisplayNames[1]);
            lamdaMaxProp = new PropertySupport.Reflection<Double>(this, Double.class, "waveMax");
            lamdaMaxProp.setName(propDisplayNames[2]);
            lamdaDeltaProp = new PropertySupport.Reflection<Double>(this, Double.class, "waveDelta");
            lamdaDeltaProp.setName(propDisplayNames[3]);
            timeMaxProp = new PropertySupport.Reflection<Double>(this, Double.class, "timeMax");
            timeMaxProp.setName(propDisplayNames[5]);
            timeMinProp = new PropertySupport.Reflection<Double>(this, Double.class, "timeMin");
            timeMinProp.setName(propDisplayNames[4]);
            timeDeltaProp = new PropertySupport.Reflection<Double>(this, Double.class, "timeDelta");
            timeDeltaProp.setName(propDisplayNames[6]);
            try {
                lamdaMinProp.setValue(sdn.getObject().getSim().getLambdaMin());
                lamdaMaxProp.setValue(sdn.getObject().getSim().getLambdaMax());
                lamdaDeltaProp.setValue(sdn.getObject().getSim().getLamdaStep());
                timeMaxProp.setValue(sdn.getObject().getSim().getTimeMax());
                timeMinProp.setValue(sdn.getObject().getSim().getTimeMin());
                timeDeltaProp.setValue(sdn.getObject().getSim().getTimeStep());
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        set.put(nameProp);
        set.put(lamdaMinProp);
        set.put(lamdaMaxProp);
        set.put(lamdaDeltaProp);
        set.put(timeMaxProp);
        set.put(timeMinProp);
        set.put(timeDeltaProp);
        if (isConnected()) {
            //TODO: read the number of kinetic components from the model and create spectra nodes for these
            //set.put()
        }
        sheet.put(set);
        return sheet;
    }

    public void updatePropSheet() {
        fireNameChange(null, getDisplayName());
        setSheet(createSheet());
    }

    @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        if (t.isDataFlavorSupported(PaletteNode.DATA_FLAVOR)) {
            try {
                final PaletteItem pi = (PaletteItem) t.getTransferData(PaletteNode.DATA_FLAVOR);
                return new PasteType() {

                    @Override
                    public Transferable paste() throws IOException {
                        if (isConnected()) {
                            Boolean present = false;
                            if (pi.getName().equals("FreeParam")) {
                                for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                    if (getChildren().getNodes()[i] instanceof ModelDiffsNode
                                            && (((ModelDiffsNode) getChildren().getNodes()[i])).getType().equalsIgnoreCase("FreeParameter")) {
                                        present = true;
                                    }
                                }
                                if (!present) {
                                    getChildren().add(new Node[]{new ModelDiffsNode("FreeParameter", propListner, getdatasetIndex())});
                                    return null;
                                } else {
                                    CoreErrorMessages.parametersExists("Free Parameter");
                                }
                            }

                            if (pi.getName().equals("AddParam")) {
                                for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                    if (getChildren().getNodes()[i] instanceof ModelDiffsNode
                                            && (((ModelDiffsNode) getChildren().getNodes()[i])).getType().equalsIgnoreCase("AddParameter")) {
                                        present = true;
                                    }
                                }
                                if (!present) {
                                    getChildren().add(new Node[]{new ModelDiffsNode("AddParameter", propListner, getdatasetIndex())});
                                    return null;
                                } else {
                                    CoreErrorMessages.parametersExists("Add Parameter");
                                }
                            }
                            if (pi.getName().equals("RemoveParam")) {
                                for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                    if (getChildren().getNodes()[i] instanceof ModelDiffsNode
                                            && (((ModelDiffsNode) getChildren().getNodes()[i])).getType().equalsIgnoreCase("RemoveParameter")) {
                                        present = true;
                                    }
                                }
                                if (!present) {
                                    getChildren().add(new Node[]{new ModelDiffsNode("RemoveParameter", propListner, getdatasetIndex())});
                                    return null;
                                } else {
                                    CoreErrorMessages.parametersExists("Remove Parameter");
                                }
                            }


                            if (pi.getName().equals("ChangeParam")) {
                                for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                    if (getChildren().getNodes()[i] instanceof ModelDiffsChangeNode) {
                                        present = true;
                                    }
                                }
                                if (!present) {
                                    getChildren().add(new Node[]{new ModelDiffsChangeNode("ChangeParameter", propListner, getdatasetIndex())});
                                    firePropertyChange("ChangeParamAdded", getdatasetIndex(), null);
                                    return null;
                                } else {
                                    CoreErrorMessages.parametersExists("Change Parameter");
                                }
                            }

                        } else {
                            CoreErrorMessages.containerNotConnected();
                        }
                        return null;
                    }
                };
            } catch (UnsupportedFlavorException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        } else {
            return null;
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return (new DataFlavor[]{DATA_FLAVOR});
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor == DATA_FLAVOR);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor == DATA_FLAVOR) {
            return (this);
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public SpectralModelDataNode getDataNode() {
        return sdn;
    }

    @Override
    public void destroy() throws IOException {
        SimulationInputRootNode parNode = (SimulationInputRootNode) getParentNode();
        firePropertyChange("mainNodeDeleted", getdatasetIndex(), getLookup().lookup(GtaSimulationInputRef.class));
        super.destroy();
        parNode.updateChildrensProperties();
    }

    private boolean isConnected() {
        return ((SimulationInputRootNode) getParentNode()).getContainerComponent().isConnected();
    }

    public int getdatasetIndex() {
        for (int i = 0; i < getParentNode().getChildren().getNodesCount(); i++) {
            if (getParentNode().getChildren().getNodes()[i].equals(this)) {
                return i + 1;
            }
        }
        return 1;
    }
}
