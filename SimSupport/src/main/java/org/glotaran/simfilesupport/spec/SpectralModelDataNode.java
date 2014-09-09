/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.simfilesupport.spec;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author jsg210
 */
public class SpectralModelDataNode extends DataNode implements Transferable {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/main/resources/doc.png", true);
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(SpectralModelDataNode.class, "SpectralModelDataNode");

    public SpectralModelDataNode(SpectralModelDataObject obj) {
        super(obj, Children.LEAF);
    }

    public SpectralModelDataNode(SpectralModelDataObject obj, Children children, Lookup lookup) {
        super(obj, children, lookup);
    }

    public SpectralModelDataObject getObject() {
        return getLookup().lookup(SpectralModelDataObject.class);
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
}
