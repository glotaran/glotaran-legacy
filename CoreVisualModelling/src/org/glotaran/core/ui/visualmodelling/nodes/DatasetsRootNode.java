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
import org.glotaran.core.ui.visualmodelling.components.DatasetContainerComponent;
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
public class DatasetsRootNode extends AbstractNode {

    public DatasetsRootNode(Children children) {
        super(children);
    }

    public DatasetsRootNode(Children children, DatasetContainerComponent comp) {
        super(children, Lookups.singleton(comp));
        addPropertyChangeListener(comp);
    }

    public DatasetContainerComponent getContainerComponent() {
        return getLookup().lookup(DatasetContainerComponent.class);
    }

    private GtaDataset createDatasetRef(TimpDatasetNode tdsNode) {
        FileObject fo = tdsNode.getObject().getPrimaryFile();
        GtaDataset gtaDataset = new GtaDataset();
        FileObject projDir = FileOwnerQuery.getOwner(fo).getProjectDirectory();
        gtaDataset.setPath(FileUtil.getRelativePath(projDir, fo));
        gtaDataset.setFilename(fo.getName());
        return gtaDataset;
    }

    public void updateChildrensProperties() {
        for (Node child : getChildren().getNodes()) {
            ((DatasetComponentNode) child).updatePropSheet();
        }
    }

    @Override
    public PasteType getDropType(final Transferable t, int action, int index) {
        if (t.isDataFlavorSupported(TimpDatasetNode.DATA_FLAVOR)) {
            return new PasteType() {

                @Override
                public Transferable paste() throws IOException {
                    try {
                        TimpDatasetNode timpDataNode = (TimpDatasetNode) t.getTransferData(TimpDatasetNode.DATA_FLAVOR);
                        GtaDataset addedDataset = createDatasetRef(timpDataNode);
                        getChildren().add(new Node[]{
                                    new DatasetComponentNode(
                                    timpDataNode,
                                    new Index.ArrayChildren(),
                                    Lookups.singleton(addedDataset),
                                    getContainerComponent())});
                        firePropertyChange("datasetAdded", null, addedDataset);
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
