/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.palette;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.Action;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.nodes.AbstractNode;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;

    
    public class PaletteSupport {
    
    //Name of the folder in the layer.xml file 
    //that is the root of the palette:
    public static final String GTA_PALETTE_FOLDER = "Palette Root";

    private static PaletteController palette = null;
    private static AbstractNode paletteRoot = new AbstractNode(new CategoryChildren());

    //Register the palette for the text/x-java MIME type:
    @MimeRegistration(mimeType = "text/gta+xml", service = PaletteController.class)
    public static PaletteController createPalette() {
//        try {
            if (null == palette) {
                palette = PaletteFactory.createPalette(
                      paletteRoot, //alternatively load from folder
                        new MyActions(),
                        null,
                        new MyDragAndDropHandler()); 
            }
            return palette;
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        return null;
    }

//    public static PaletteController createPalette() {
//            AbstractNode paletteRoot = new AbstractNode(new CategoryChildren());
//            paletteRoot.setName("Palette Root");
//            return PaletteFactory.createPalette(paletteRoot, new MyActions(), null, new MyDnDHandler());
//        }

    private static class MyActions extends PaletteActions {

        @Override
        public Action[] getImportActions() {
            return null;
        }

        @Override
        public Action[] getCustomPaletteActions() {
            return null;
        }

        @Override
        public Action[] getCustomCategoryActions(Lookup lookup) {
            return null;
        }

        @Override
        public Action[] getCustomItemActions(Lookup lookup) {
            return null;
        }

        @Override
        public Action getPreferredAction(Lookup lookup) {
            return null;
        }
    }

    private static class MyDragAndDropHandler extends DragAndDropHandler {
        
        @Override
        public void customize(ExTransferable exTransferable, Lookup lookup) {
            final PaletteItem item = lookup.lookup(PaletteItem.class);
            //final Image image = (Image) node.getIcon(BeanInfo.ICON_COLOR_16x16);
            exTransferable.put(new ExTransferable.Single(new DataFlavor(PaletteItem.class, "PaletteItem")) {

                @Override
                protected PaletteItem getData() throws IOException, UnsupportedFlavorException {
                    return item;
                }
            });
        }
    }
}
