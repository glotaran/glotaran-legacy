/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.gta.GtaChangesModel;
import org.glotaran.core.models.tgm.Dat;
import org.glotaran.core.models.tgm.Double2Matrix;
import org.glotaran.core.models.tgm.IntMatrix;
import org.glotaran.core.models.tgm.JVector;
import org.glotaran.core.models.tgm.KMatrixPanelModel;
import org.glotaran.core.models.tgm.KinPar;
import org.glotaran.core.models.tgm.KinparPanelModel;
import org.glotaran.core.models.tgm.Tgm;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.ModelDiffsDO;
import org.glotaran.core.ui.visualmodelling.palette.PaletteItem;
import org.glotaran.core.ui.visualmodelling.palette.PaletteNode;
import org.glotaran.tgmeditor.panels.JVectorValueClass;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author slapten
 */
public class ModelDiffsChangeNode extends PropertiesAbstractNode {

    private final Image CHANGE_ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/ChageParam_16.png", true);
    private PropertyChangeListener propListner;
    private int datasetIndex;
    private FileObject parentFolder = null;
    TgmDataObject tgmDO;

    public ModelDiffsChangeNode(String type, PropertyChangeListener listn, int datasetInd) {
        super(type, new Index.ArrayChildren());
        propListner = listn;
        datasetIndex = datasetInd;
        addPropertyChangeListener(WeakListeners.propertyChange(propListner, this));
    }

    public ModelDiffsChangeNode(String type, int datasetInd, GtaChangesModel gtaChangesModel, FileObject schemaFolder, PropertyChangeListener listn) {
        super(type, new Index.ArrayChildren());
        propListner = listn;
        datasetIndex = datasetInd + 1;
        parentFolder = schemaFolder;
        if (parentFolder == null && getParentNode() != null) {
            parentFolder = ((DatasetsRootNode) getParentNode().getParentNode()).getContainerComponent().getSchemaFolder();
        }
        FileObject projDir = FileOwnerQuery.getOwner(parentFolder).getProjectDirectory();
        FileObject tgmFO = FileUtil.toFileObject(new File(projDir.getPath() + File.separator + gtaChangesModel.getPath() + File.separator + gtaChangesModel.getFilename()));
        if (tgmFO != null) {
            try {
                tgmDO = ((TgmDataObject) DataObject.find(tgmFO));
            } catch (DataObjectNotFoundException ex ) {
                CoreErrorMessages.fileLoadException("changes TGM");
            } catch (java.lang.ClassCastException ex) { //at least catch error or file won't open
                // optionally delete empty file, but check if empty first
                // then remove it from Schema file as well
                //tgmFO.delete(); 
                
            }
        }
        if (tgmDO != null) {
            updateChangesNodes(tgmDO);
        }
        addPropertyChangeListener(WeakListeners.propertyChange(propListner, this));
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>(); //super.getActions(context);
        actions.add(SystemAction.get(DeleteAction.class));
        actions.add(SystemAction.get(PropertiesAction.class));
        actions.add(new ImportKMatrixAction());
        return actions.toArray(new Action[actions.size()]);
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

    @Override
    public Image getIcon(int type) {
        return CHANGE_ICON;
    }

    @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        if (t.isDataFlavorSupported(PaletteNode.DATA_FLAVOR)) {
            try {
                final PaletteItem pi = (PaletteItem) t.getTransferData(PaletteNode.DATA_FLAVOR);
                return new PasteType() {

                    @Override
                    public Transferable paste() throws IOException {
                        boolean present = false;
                        if (pi.getName().equalsIgnoreCase("Kinetic Parameters")) {
                            for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                if (getChildren().getNodes()[i] instanceof KineticParametersNode) {
                                    present = true;
                                }
                            }
                            if (!present) {
                                getChildren().add(new Node[]{new KineticParametersNode(propListner)});
                            } else {
                                CoreErrorMessages.parametersExists("Kinetic parameters ");
                            }
                        }
                        present = false;
//================ irf parameter node creation ===================
                        if (pi.getName().equalsIgnoreCase("IRF Parameters")) {
                            for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                if (getChildren().getNodes()[i] instanceof IrfParametersNode) {
                                    present = true;
                                }
                            }
                            if (!present) {
                                if (tgmDO != null) {
                                    getChildren().add(new Node[]{new IrfParametersNode(tgmDO, propListner)});
                                }
                            } else {
                                CoreErrorMessages.parametersExists("IRF Parameters ");
                            }
                        }
//================ disp parameter node creation ===================
                        if (pi.getName().equalsIgnoreCase("Dispersion")) {
                            int paramNumb = 0;
                            EnumTypes.DispersionTypes type = null;
                            for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                if (getChildren().getNodes()[i] instanceof DispersionModelingNode) {
                                    paramNumb++;
                                    type = ((DispersionModelingNode) getChildren().getNodes()[i]).getDisptype();
                                    ((DispersionModelingNode) getChildren().getNodes()[i]).setSingle(false);
                                    ((DispersionModelingNode) getChildren().getNodes()[i]).recreateSheet();
                                }
                            }
                            if (paramNumb < 2) {
                                if (type != null) {
                                    type = (type.equals(EnumTypes.DispersionTypes.PARMU))
                                            ? EnumTypes.DispersionTypes.PARTAU
                                            : EnumTypes.DispersionTypes.PARMU;
                                    getChildren().add(new Node[]{new DispersionModelingNode(type, false, propListner)});
                                } else {
                                    getChildren().add(new Node[]{new DispersionModelingNode(EnumTypes.DispersionTypes.PARMU, true, propListner)});
                                }
                            } else {
                                CoreErrorMessages.parametersExists("2 Dispersion parameters ");
                            }
                        }
//================ cohspec parameter node creation ===================
                        if (pi.getName().equalsIgnoreCase("Cohspec Parameters")) {
                            for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                if (getChildren().getNodes()[i] instanceof CohSpecNode) {
                                    present = true;
                                }
                            }
                            if (!present) {
                                getChildren().add(new Node[]{new CohSpecNode(propListner)});
                            } else {
                                CoreErrorMessages.parametersExists("CohSpec parameters ");
                            }
                        }
//================ weight parameter node creation ===================
                        if (pi.getName().equalsIgnoreCase("Weight Parameters")) {
                            for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                if (getChildren().getNodes()[i] instanceof WeightParametersNode) {
                                    present = true;
                                }
                            }
                            if (!present) {
                                getChildren().add(new Node[]{new WeightParametersNode(propListner)});
                            } else {
                                CoreErrorMessages.parametersExists("Weight parameters ");
                            }
                        }
//================ Kmatrix parameter node creation ===================
                        if (pi.getName().equalsIgnoreCase("KMatrix")) {
                            for (int i = 0; i < getChildren().getNodesCount(); i++) {
                                if (getChildren().getNodes()[i] instanceof KmatrixNode) {
                                    present = true;
                                }
                            }
                            if (!present) {
                                if (tgmDO != null) {
                                    getChildren().add(new Node[]{new KmatrixNode(tgmDO, propListner)});
                                }
                            } else {
                                CoreErrorMessages.parametersExists("Kmatrix ");
                            }
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

    private void updateChangesNodes(TgmDataObject tgmDO) {

        Dat tgmDat = tgmDO.getTgm().getDat();

        if (tgmDat.getKMatrixPanel() != null) {
            if (!tgmDat.getKMatrixPanel().getJVector().getVector().isEmpty()) {

                getChildren().add(
                        new Node[]{new KmatrixNode(tgmDO, propListner)});
            }
        } else {
            if (tgmDat.getKinparPanel() != null) {
                if (!tgmDat.getKinparPanel().getKinpar().isEmpty()) {
                    getChildren().add(
                            new Node[]{new KineticParametersNode(tgmDat.getKinparPanel(), propListner)});
                }
            }
        }

        if (tgmDat.getIrfparPanel() != null) {
            if (!tgmDat.getIrfparPanel().getIrf().isEmpty()
                    && (!tgmDat.getIrfparPanel().isMirf())) {
                getChildren().add(
                        new Node[]{new IrfParametersNode(tgmDO, propListner)});
            }

            if (tgmDat.getIrfparPanel().getParmu() != null) {
                if (tgmDat.getIrfparPanel().getParmu().length() != 0) {
                    getChildren().add(
                            new Node[]{new DispersionModelingNode(tgmDat.getIrfparPanel(), EnumTypes.DispersionTypes.PARMU, propListner)});
                }
            }

            if (tgmDat.getIrfparPanel().getPartau() != null) {
                if (tgmDat.getIrfparPanel().getPartau().length() != 0) {
                    getChildren().add(
                            new Node[]{new DispersionModelingNode(tgmDat.getIrfparPanel(), EnumTypes.DispersionTypes.PARTAU, propListner)});
                }
            }
        }

        if (tgmDat.getWeightParPanel() != null) {
            if (tgmDat.getWeightParPanel().getWeightpar() != null) {
                if (!tgmDat.getWeightParPanel().getWeightpar().isEmpty()) {
                    getChildren().add(
                            new Node[]{new WeightParametersNode(tgmDat.getWeightParPanel(), propListner)});
                }
            }
        }

        if (tgmDat.getCohspecPanel() != null) {
            if (tgmDat.getCohspecPanel().getCohspec() != null) {
                if (tgmDat.getCohspecPanel().getCohspec().isSet()) {
                    getChildren().add(
                            new Node[]{new CohSpecNode(tgmDat.getCohspecPanel(), propListner)});
                }
            }
        }

    }

    private class ImportKMatrixAction extends AbstractAction {

        private KMatrixPanelModel importedKMModel, kMatrixPanelModel;
        private int matrixSize = 0;

        public ImportKMatrixAction() {
            putValue(NAME, "Import Kmatrix from connected model");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            importedKMModel = getConnectedModel().getDat().getKMatrixPanel();
            matrixSize = importedKMModel.getJVector().getVector().size();

            Node[] children = getChildren().getNodes();
            for (Node node : children) {
                if (node instanceof KmatrixNode) {
                    kMatrixPanelModel = ((KmatrixNode) node).getKmatrix().getTgm().getDat().getKMatrixPanel();

                    int val;
                    kMatrixPanelModel.getKMatrix().getData().clear();
                    IntMatrix.Data tempData;
                    for (int j = 0; j < matrixSize; j++) {
                        tempData = new IntMatrix.Data();
                        for (int i = 0; i < matrixSize; i++) {
                            if (importedKMModel.getKMatrix().getData().get(j).getRow().get(i) == null) {
                                val = 0;
                            } else {
                                val = (Integer) importedKMModel.getKMatrix().getData().get(j).getRow().get(i);
                            }
                            tempData.getRow().add(val);
                        }
                        kMatrixPanelModel.getKMatrix().getData().add(tempData);
                    }

                    for (int j = 0; j < matrixSize; j++) {
                        tempData = new IntMatrix.Data();
                        for (int i = 0; i < matrixSize; i++) {
                            if (importedKMModel.getKMatrix().getData().get(j + matrixSize).getRow().get(i) == null) {
                                val = 0;
                            } else {
                                val = (Integer) importedKMModel.getKMatrix().getData().get(j + matrixSize).getRow().get(i);
                            }
                            tempData.getRow().add(val);

                        }
                        kMatrixPanelModel.getKMatrix().getData().add(tempData);
                    }
                    KinparPanelModel importedKPModel = getConnectedModel().getDat().getKinparPanel();
                    KinparPanelModel kinparPanelModel = ((KmatrixNode) node).getKmatrix().getTgm().getDat().getKinparPanel();
                    kinparPanelModel.getKinpar().clear();
                    for (int i = 0; i < importedKPModel.getKinpar().size(); i++) {
                        KinPar kp = new KinPar();
                        kp.setStart((Double) importedKPModel.getKinpar().get(i).getStart());
                        kp.setFixed((Boolean) importedKPModel.getKinpar().get(i).isFixed());
                        kp.setConstrained((Boolean) importedKPModel.getKinpar().get(i).isConstrained());
                        kp.setMin((Double) importedKPModel.getKinpar().get(i).getMin());
                        kp.setMax((Double) importedKPModel.getKinpar().get(i).getMax());
                        kinparPanelModel.getKinpar().add(kp);
                    }


                    kMatrixPanelModel.getKinScal().clear();
                    for (int i = 0; i < importedKMModel.getKinScal().size(); i++) {
                        KinPar kp = new KinPar();
                        kp.setStart((Double) importedKMModel.getKinScal().get(i).getStart());
                        kp.setFixed((Boolean) importedKMModel.getKinScal().get(i).isFixed());
                        kp.setConstrained((Boolean) importedKMModel.getKinScal().get(i).isConstrained());
                        kp.setMin((Double) importedKMModel.getKinScal().get(i).getMin());
                        kp.setMax((Double) importedKMModel.getKinScal().get(i).getMax());
                        kMatrixPanelModel.getKinScal().add(kp);
                    }

                    Double2Matrix.Data tempD2M;
                    kMatrixPanelModel.getContrainsMatrix().getData().clear();
                    for (int i = 0; i < matrixSize; i++) {
                        tempD2M = new Double2Matrix.Data();
                        if (tempD2M.getScal() == null) {
                        }
                        for (int j = 0; j < matrixSize; j++) {
                            int numel = importedKMModel.getContrainsMatrix().getData().get(i).getMin().size();
                            for (int n = 0; n < numel; n++) {
                                tempD2M.getMin().add((Double) importedKMModel.getContrainsMatrix().getData().get(i).getMin().get(n));
                                tempD2M.getMax().add((Double) importedKMModel.getContrainsMatrix().getData().get(i).getMax().get(n));
                                if (!tempD2M.getScal().isEmpty()) {
                                tempD2M.getScal().add((Double) importedKMModel.getContrainsMatrix().getData().get(i).getScal().get(n));
                                }
                            }
                        }
                        kMatrixPanelModel.getContrainsMatrix().getData().add(tempD2M);
                    }

                    kMatrixPanelModel.getJVector().getFixed().clear();
                    kMatrixPanelModel.getJVector().getVector().clear();
                    for (int i = 0; i < matrixSize; i++) {
                        kMatrixPanelModel.getJVector().getVector().add(importedKMModel.getJVector().getVector().get(i));
                        kMatrixPanelModel.getJVector().getFixed().add(importedKMModel.getJVector().getFixed().get(i));
                    }

                    kMatrixPanelModel.getSpectralContraints().getMin().clear();
                    kMatrixPanelModel.getSpectralContraints().getMax().clear();
                    for (int i = 0; i < matrixSize; i++) {
                        kMatrixPanelModel.getSpectralContraints().getMin().add((Double) importedKMModel.getSpectralContraints().getMin().get(i));
                        kMatrixPanelModel.getSpectralContraints().getMax().add((Double) importedKMModel.getSpectralContraints().getMax().get(i));
                    }

                    tgmDO.setModified(true);

                }
            }
        }
        //APIObject obj = getLookup().lookup (APIObject.class);
        //JOptionPane.showMessageDialog(null, "Hello from " + obj);
    }
}
