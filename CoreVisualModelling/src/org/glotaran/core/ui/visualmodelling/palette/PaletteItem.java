/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.palette;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author jsg210
 */
public class PaletteItem {//implements Transferable {

    private Integer number;
    private String category;
    private String name;
    private Image image;
    private String imageLocation;
//    public static final DataFlavor DATA_FLAVOR = new DataFlavor(PaletteItem.class, "PaletteItem");

    /** Creates a new instance of Instrument */
    public PaletteItem() {
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getImageLocation() {
        return imageLocation;
    }
//
//   public DataFlavor[] getTransferDataFlavors() {
//        return new DataFlavor[]{DATA_FLAVOR};
//    }
//
//    public boolean isDataFlavorSupported(DataFlavor flavor) {
//        return flavor == DATA_FLAVOR;
//    }
//
//    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
//        if (flavor == DATA_FLAVOR) {
//            return this;
//        } else {
//            throw new UnsupportedFlavorException(flavor);
//        }
//    }
}
