/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.view;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.gta.GtaDatasetContainer;
import org.glotaran.core.models.gta.GtaLayout;
import org.glotaran.core.models.gta.GtaModelReference;
import org.glotaran.core.models.gta.GtaOutput;
import org.glotaran.core.models.gta.GtaSimulationContainer;
import org.glotaran.core.ui.visualmodelling.common.VisualCommonFunctions;
import org.glotaran.core.ui.visualmodelling.components.SingleRootFileSystemView;
import org.glotaran.core.ui.visualmodelling.palette.PaletteItem;
import org.glotaran.core.ui.visualmodelling.palette.PaletteNode;
import org.glotaran.tgmfilesupport.TgmDataNode;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Widget;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author jsg210
 */
public class GlotaranSceneAcceptProvider implements AcceptProvider {

    private GlotaranGraphScene scene;

    public GlotaranSceneAcceptProvider(GlotaranGraphScene scene) {
        this.scene = scene;
    }

    public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
        ConnectorState accept = ConnectorState.REJECT;
        if (transferable.isDataFlavorSupported(TgmDataNode.DATA_FLAVOR)) {
            accept = ConnectorState.ACCEPT;
        } else if (transferable.isDataFlavorSupported(PaletteNode.DATA_FLAVOR)) {
            PaletteItem item = null;
            try {
                item = (PaletteItem) transferable.getTransferData(PaletteNode.DATA_FLAVOR);
            } catch (UnsupportedFlavorException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (item.getCategory().compareTo("Containers") == 0
                    || item.getCategory().compareTo("Output") == 0) {
                accept = ConnectorState.ACCEPT;
            } else {
                accept = ConnectorState.REJECT;
            }
        } else {
            accept = ConnectorState.REJECT;
        }
        return accept;
    }

    public void accept(Widget widget, Point point, Transferable transferable) {
        Widget newWidget = null;

        // A new components is added from the Pallete
        if (transferable.isDataFlavorSupported(PaletteNode.DATA_FLAVOR)) {
            final PaletteItem item = VisualCommonFunctions.getPaletteItemTransferable(transferable);
            if (item.getName().equalsIgnoreCase("Dataset Container")) {
                GtaDatasetContainer newDatasetContainer = new GtaDatasetContainer();
                GtaLayout widlayout = new GtaLayout();
                widlayout.setXposition(point.getX());
                widlayout.setYposition(point.getY());
                newDatasetContainer.setLayout(widlayout);
                newDatasetContainer.setId(String.valueOf(scene.getNewNodeCount()));
                newWidget = scene.addNode(newDatasetContainer);
            } else if (item.getName().equalsIgnoreCase("Output Container")) {
                GtaOutput gtaOutput = new GtaOutput();
                GtaLayout widlayout = new GtaLayout();
                widlayout.setXposition(point.getX());
                widlayout.setYposition(point.getY());
                gtaOutput.setLayout(widlayout);
                gtaOutput.setId(String.valueOf(scene.getNewNodeCount()));
                newWidget = scene.addNode(gtaOutput);
            } else if (item.getName().equalsIgnoreCase("Simulation Input")) {
                GtaSimulationContainer container = new GtaSimulationContainer();
                GtaLayout widlayout = new GtaLayout();
                widlayout.setXposition(point.getX());
                widlayout.setYposition(point.getY());
                container.setLayout(widlayout);
                container.setId(String.valueOf(scene.getNewNodeCount()));
                newWidget = scene.addNode(container);
            } else if (item.getName().equalsIgnoreCase("Model")) {
                //TODO: ask for new file name
                TGProject project = (TGProject) FileOwnerQuery.getOwner(scene.getDobj().getPrimaryFile());
                FileObject modelFolder = project.getModelsFolder(true);
                FileSystemView fsv = new SingleRootFileSystemView(FileUtil.toFile(modelFolder));
                JFileChooser chooser = new JFileChooser(fsv);
                chooser.setDialogTitle("Specify new model name ... ");
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (chooser.showSaveDialog(scene.getView()) == JFileChooser.APPROVE_OPTION) {
                    File newFile = chooser.getSelectedFile();
                    FileObject newTgmFile;
                    try {
                        newTgmFile = modelFolder.createData(newFile.getName(), "xml");
                        VisualCommonFunctions.createNewTgmFile(FileUtil.toFile(newTgmFile),"kin");
                        TgmDataObject dObj = (TgmDataObject) DataObject.find(newTgmFile);
                        TgmDataNode tgmNode = (TgmDataNode) dObj.getNodeDelegate();
                        GtaModelReference newModel = new GtaModelReference();
                        FileObject fo = tgmNode.getObject().getPrimaryFile();
                        //TODO: Use something more reliable than OpenProjects.getDefault().getMainProject()
                        String path = null;
                        try {
                            path = FileUtil.getRelativePath(FileOwnerQuery.getOwner(tgmNode.getObject().getPrimaryFile()).getProjectDirectory(), fo);
                        } catch (Exception e) {
                            CoreErrorMessages.noMainProjectFound();
                            return;
                        }
                        newModel.setPath(path);
                        newModel.setFilename(fo.getName());
                        newModel.setId(String.valueOf(scene.getNewNodeCount()));
                        GtaLayout widlayout = new GtaLayout();
                        widlayout.setXposition(point.getX());
                        widlayout.setYposition(point.getY());
                        newModel.setLayout(widlayout);
                        newWidget = scene.addNode(newModel);


                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

        // A existing dataobject is dragged from within the platform onto the GraphScene
        if (transferable.isDataFlavorSupported(TgmDataNode.DATA_FLAVOR)) {
            try {
                TgmDataNode tgmNode = (TgmDataNode) transferable.getTransferData(TgmDataNode.DATA_FLAVOR);
                GtaModelReference newModel = new GtaModelReference();
                FileObject fo = tgmNode.getObject().getPrimaryFile();
                //TODO: Use something more reliable than OpenProjects.getDefault().getMainProject()
                String path = null;
                try {
                    path = FileUtil.getRelativePath(FileOwnerQuery.getOwner(tgmNode.getObject().getPrimaryFile()).getProjectDirectory(), fo);
                } catch (Exception e) {
                    CoreErrorMessages.noMainProjectFound();
                    return;
                }
                newModel.setPath(path);
                newModel.setFilename(fo.getName());
                newModel.setId(String.valueOf(scene.getNewNodeCount()));
                GtaLayout widlayout = new GtaLayout();
                widlayout.setXposition(point.getX());
                widlayout.setYposition(point.getY());
                newModel.setLayout(widlayout);
                newWidget = scene.addNode(newModel);
            } catch (UnsupportedFlavorException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                CoreErrorMessages.IOException("CustomSceneAcceptProvider");
            }
        }

        if (newWidget != null) {
            newWidget.setPreferredLocation(point);
        }
        scene.validate();
    }
}
