/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.components;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.tgm.Cohspec;
import org.glotaran.core.models.tgm.CohspecPanelModel;
import org.glotaran.core.models.tgm.Oscspec;
import org.glotaran.core.models.tgm.OscspecPanelModel;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.nodes.CohSpecNode;
import org.glotaran.core.ui.visualmodelling.nodes.DispersionModelingNode;
import org.glotaran.core.ui.visualmodelling.nodes.IrfParametersNode;
import org.glotaran.core.ui.visualmodelling.nodes.KineticParametersNode;
import org.glotaran.core.ui.visualmodelling.nodes.KmatrixNode;
import org.glotaran.core.ui.visualmodelling.nodes.OscspecParametersNode;
import org.glotaran.core.ui.visualmodelling.nodes.WeightParametersNode;
import org.glotaran.core.ui.visualmodelling.palette.PaletteItem;
import org.glotaran.core.ui.visualmodelling.palette.PaletteNode;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author jsg210
 */
public class ModelSpecificationView extends TreeTableView {

    private static final long serialVersionUID = 1L;

    public ModelSpecificationView() {
        setRootVisible(false);
        setDropTarget();
    }

    private void setDropTarget() {
        DropTarget dt = new DropTarget(this, new DropTargetAdapter() {

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                if (dtde.getTransferable().isDataFlavorSupported(PaletteNode.DATA_FLAVOR)) {
                    PaletteItem item = null;
                    try {
                        item = (PaletteItem) dtde.getTransferable().getTransferData(PaletteNode.DATA_FLAVOR);
                    } catch (UnsupportedFlavorException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    if (item.getCategory().compareTo("Modelling") != 0) {
                        dtde.rejectDrag();
                    }
                } else {
                    dtde.rejectDrag();
                }
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                PropertyChangeListener listn = null;
                for (PropertyChangeListener propertyChangeListener : getPropertyChangeListeners()) {
                    if (propertyChangeListener instanceof ModelContainer) {
                        listn = propertyChangeListener;
                    }
                }
                PaletteItem item = null;
                try {
                    item = (PaletteItem) dtde.getTransferable().getTransferData(PaletteNode.DATA_FLAVOR);
                    Children nodes = ExplorerManager.find(getParent()).getRootContext().getChildren();
                    boolean present = false;
//================ kinetic parameter node creation ===================
                    if (item.getName().equalsIgnoreCase("Kinetic Parameters")) {
                        for (int i = 0; i < nodes.getNodesCount(); i++) {
                            if (nodes.getNodes()[i] instanceof KineticParametersNode) {
                                present = true;
                            }
                        }
                        if (!present) {
                            nodes.add(new Node[]{new KineticParametersNode(listn)});
                        } else {
                            CoreErrorMessages.parametersExists("Kinetic parameters ");
                        }
                    } else //================ irf parameter node creation ===================
                    if (item.getName().equalsIgnoreCase("IRF Parameters")) {
                        for (int i = 0; i < nodes.getNodesCount(); i++) {
                            if (nodes.getNodes()[i] instanceof IrfParametersNode) {
                                present = true;
                            }
                        }
                        if (!present) {
                            TgmDataObject obj = ExplorerManager.find(getParent()).getRootContext().getLookup().lookup(TgmDataObject.class);
                            if (obj != null) {
                                nodes.add(new Node[]{new IrfParametersNode(obj, listn)});
                                obj.setModified(true);
                            } else {
                                nodes.add(new Node[]{new IrfParametersNode(listn)});
                            }
                        } else {
                            CoreErrorMessages.parametersExists("IRF Parameters ");
                        }
                    } else //================ disp parameter node creation ===================
                    if (item.getName().equalsIgnoreCase("Dispersion")) {
                        int paramNumb = 0;
                        EnumTypes.DispersionTypes type = null;
                        for (int i = 0; i < nodes.getNodesCount(); i++) {
                            if (nodes.getNodes()[i] instanceof DispersionModelingNode) {
                                paramNumb++;
                                type = ((DispersionModelingNode) nodes.getNodes()[i]).getDisptype();
                                ((DispersionModelingNode) nodes.getNodes()[i]).setSingle(false);
                                ((DispersionModelingNode) nodes.getNodes()[i]).recreateSheet();
                            }
                        }
                        if (paramNumb < 2) {
                            if (type != null) {
                                type = (type.equals(EnumTypes.DispersionTypes.PARMU))
                                        ? EnumTypes.DispersionTypes.PARTAU
                                        : EnumTypes.DispersionTypes.PARMU;
                                nodes.add(new Node[]{new DispersionModelingNode(type, false, listn)});
                            } else {
                                nodes.add(new Node[]{new DispersionModelingNode(EnumTypes.DispersionTypes.PARMU, true, listn)});
                            }
                        } else {
                            CoreErrorMessages.parametersExists("2 Dispersion parameters ");
                        }
                    } else //================ cohspec parameter node creation ===================
                    if (item.getName().equalsIgnoreCase("Cohspec Parameters")) {
                        for (int i = 0; i < nodes.getNodesCount(); i++) {
                            if (nodes.getNodes()[i] instanceof CohSpecNode) {
                                present = true;
                            }
                        }
                        if (!present) {
                            nodes.add(new Node[]{new CohSpecNode(listn)});
                            if (nodes.getNodeAt(nodes.getNodesCount() - 1) instanceof CohSpecNode) {
                                ((CohSpecNode) nodes.getNodeAt(nodes.getNodesCount() - 1)).initialize();
                            }
                        } else {
                            CoreErrorMessages.parametersExists("CohSpec parameters ");
                        }
                    } else //================ oscspec parameter node creation ===================
                    if (item.getName().equalsIgnoreCase("Oscspec Parameters")) {
                        for (int i = 0; i < nodes.getNodesCount(); i++) {
                            if (nodes.getNodes()[i] instanceof OscspecParametersNode) {
                                present = true;
                            }
                        }
                        if (!present) {
                            TgmDataObject obj = ExplorerManager.find(getParent()).getRootContext().getLookup().lookup(TgmDataObject.class);                            
                            if (obj != null) {
                                if(obj.getTgm().getDat().getOscspecPanel()==null) {
                                    obj.getTgm().getDat().setOscspecPanel(new OscspecPanelModel());
                                    obj.getTgm().getDat().getOscspecPanel().setOscspec(new Oscspec());
                                    obj.getTgm().getDat().getOscspecPanel().getOscspec().setSet(Boolean.TRUE);
                                }
                                obj.getTgm().getDat().getOscspecPanel().getOscspec().setSet(Boolean.TRUE);                                
                                nodes.add(new Node[]{new OscspecParametersNode(obj, listn)});
                                obj.setModified(true);
                            } else {
                                nodes.add(new Node[]{new OscspecParametersNode(listn)});
                            }
                            
                        } else {
                            CoreErrorMessages.parametersExists("Oscspec Parameters ");
                        }
                    } else //================ weight parameter node creation ===================
                    if (item.getName().equalsIgnoreCase("Weight Parameters")) {
                        for (int i = 0; i < nodes.getNodesCount(); i++) {
                            if (nodes.getNodes()[i] instanceof WeightParametersNode) {
                                present = true;
                            }
                        }
                        if (!present) {
                            nodes.add(new Node[]{new WeightParametersNode(listn)});
                        } else {
                            CoreErrorMessages.parametersExists("Weight parameters ");
                        }
                    } else //================ Kmatrix parameter node creation ===================
                    if (item.getName().equalsIgnoreCase("KMatrix")) {
                        for (int i = 0; i < nodes.getNodesCount(); i++) {
                            if (nodes.getNodes()[i] instanceof KmatrixNode) {
                                present = true;
                            }
                        }
                        if (!present) {
                            TgmDataObject obj = ExplorerManager.find(getParent()).getRootContext().getLookup().lookup(TgmDataObject.class);
                            if (obj != null) {
                                nodes.add(new Node[]{new KmatrixNode(obj, listn)});
                                obj.setModified(true);
                            } else {
                                nodes.add(new Node[]{new KmatrixNode(listn)});
                            }
                            firePropertyChange("addedKMatrixNode", obj, null);
                        } else {
                            CoreErrorMessages.parametersExists("Kmatrix ");
                        }

                    } else {
//================ rest node creation ===================
                        ExplorerManager.find(getParent()).getRootContext().getChildren().add(new Node[]{new AbstractNode(Children.LEAF)});
                    }

                } catch (UnsupportedFlavorException | IOException e) {

                }
            }
        });

        setDropTarget(dt);
    }
}
