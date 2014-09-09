package org.glotaran.core.datadisplayers;

import org.glotaran.core.main.interfaces.DatasetLoaderInterface;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;

/**
 *
 * @author lsp
 */
public class ShowFlimASCIIEditor implements DatasetLoaderInterface {

    public String getType() {
        return "FLIMascii";
    }

    public void openDatasetEditor(TgdDataObject dataObj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void openDatasetEditor(TimpDatasetDataObject dataObj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
