/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.glotaran.core.main.nodes.TimpDatasetNode;
import org.glotaran.core.models.gta.GtaDataset;
import org.glotaran.core.models.gta.GtaSimulationInputRef;
import org.glotaran.core.ui.visualmodelling.components.DatasetContainerComponent;
import org.glotaran.core.ui.visualmodelling.components.SimulationContainerComponent;
import org.glotaran.simfilesupport.spec.SpectralModelDataNode;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author slapten
 */
public class SimulationInputRootNode extends AbstractNode {

    public SimulationInputRootNode(Children children) {
        super(children);
    }

    public SimulationInputRootNode(Children children, SimulationContainerComponent comp) {
        super(children, Lookups.singleton(comp));
        addPropertyChangeListener(comp);
    }

    public SimulationContainerComponent getContainerComponent() {
        return getLookup().lookup(SimulationContainerComponent.class);
    }

    private GtaSimulationInputRef createSimulationInputRef(SpectralModelDataNode tdsNode) {
        FileObject fo = tdsNode.getObject().getPrimaryFile();
        GtaSimulationInputRef gtaSimulationInputRef = new GtaSimulationInputRef();
        gtaSimulationInputRef.setPath(FileUtil.getRelativePath(FileOwnerQuery.getOwner(tdsNode.getObject().getPrimaryFile()).getProjectDirectory(), fo));
        gtaSimulationInputRef.setFilename(fo.getName());
        return gtaSimulationInputRef;
    }

    public void updateChildrensProperties() {
        for (Node child : getChildren().getNodes()) {
            ((SimInputComponentNode) child).updatePropSheet();
        }
    }

    @Override
    public PasteType getDropType(final Transferable t, int action, int index) {
        if (t.isDataFlavorSupported(SpectralModelDataNode.DATA_FLAVOR)) {
            return new PasteType() {

                @Override
                public Transferable paste() throws IOException {
                    try {
                        SpectralModelDataNode spectralModelDataNode = (SpectralModelDataNode) t.getTransferData(SpectralModelDataNode.DATA_FLAVOR);
                        GtaSimulationInputRef addedRef = createSimulationInputRef(spectralModelDataNode);
                        getChildren().add(new Node[]{
                                    new SimInputComponentNode(
                                    spectralModelDataNode,
                                    new Index.ArrayChildren(),
                                    Lookups.singleton(spectralModelDataNode.getObject()),
                                    getContainerComponent())});
                        firePropertyChange("simulationInputAdded", null, addedRef);
                        updateChildrensProperties();
                    } catch (UnsupportedFlavorException exption) {
                        Exceptions.printStackTrace(exption);
                    }
                    return null;
                }
            };
        } else {
            return null;
        }

    }
}
