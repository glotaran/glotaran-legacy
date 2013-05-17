/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.Action;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;

/**
 *
 * @author lsp
 */
public class TimpDatasetNode extends DataNode implements Transferable {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/main/resources/doc.png", true);
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(TimpDatasetNode.class, "TimpDatasetNode");

    public TimpDatasetNode(TimpDatasetDataObject obj) {
        super(obj, Children.LEAF);
    }

    public TimpDatasetNode(TimpDatasetDataObject obj, Children children) {
        super(obj, children);
    }

//    @Override
//    public Action[] getActions(boolean context) {
//        Action showTimpDatasetAction = FileUtil.getConfigObject("Actions/Build/org-glotaran-core-main-nodes-actions-ShowTimpDataset.instance", Action.class);
//        return new Action[]{showTimpDatasetAction};
//        
//    }

    
    public TimpDatasetDataObject getObject() {
        return getLookup().lookup(TimpDatasetDataObject.class);
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
    public String getDisplayName() {
        return getObject().getName();
    }

    @Override
    public Transferable drag() {
        return (Transferable) (this);
    }

    @Override
    public boolean canCopy() {
        return true;
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return (new DataFlavor[]{DATA_FLAVOR});
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor == DATA_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor == DATA_FLAVOR) {
            return (this);
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
