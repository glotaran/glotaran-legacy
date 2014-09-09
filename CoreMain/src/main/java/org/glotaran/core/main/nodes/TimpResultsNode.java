/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author lsp
 */
public class TimpResultsNode extends DataNode implements Transferable {

    private TimpResultDataObject obj;
    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/main/resources/Result-dataset-16.png", true);
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(TimpResultsNode.class, "TimpResultsNode");

    public TimpResultsNode(TimpResultDataObject obj) {
        super(obj, Children.LEAF);
        this.obj = obj;
    }

    TimpResultsNode(TimpResultDataObject obj, Lookup lookup) {
        super(obj, Children.LEAF, lookup);
        this.obj = obj;
    }

    public TimpResultDataObject getObject() {
        return obj;
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
        return obj.getName();
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
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor == DATA_FLAVOR) {
            return (this);
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public Transferable drag() throws IOException {
        return (Transferable) this;
    }
}
