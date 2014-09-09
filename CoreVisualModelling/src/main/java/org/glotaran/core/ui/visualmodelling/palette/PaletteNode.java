/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.palette;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

public class PaletteNode extends AbstractNode implements Transferable {

    /** Creates a new instance of InstrumentNode */
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(PaletteNode.class, "VisualPaletteNode");
    private PaletteItem paletteItem;

    public PaletteNode(PaletteItem key) {
        super(Children.LEAF, Lookups.fixed(new Object[]{key}));
        this.paletteItem = key;
        setIconBaseWithExtension(key.getImageLocation());
    }

    public DataFlavor[] getTransferDataFlavors() {
        return (new DataFlavor[]{DATA_FLAVOR});
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor == DATA_FLAVOR);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor == DATA_FLAVOR) {
            return (paletteItem);
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public PaletteItem getPaletteItem() {
        return paletteItem;
    }

    @Override
    public Transferable drag() throws IOException {
        return this;
    }

    @Override
    public boolean canCopy() {
        return true;
    }

    @Override
    public Action getPreferredAction() {
        return super.getPreferredAction();
    }
}
