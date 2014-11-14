package org.glotaran.core.datadisplayers;

import org.glotaran.core.main.interfaces.DatasetLoaderInterface;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.models.structures.DatasetTimp;

/**
 *
 * @author lsp
 */
public class ShowFlimASCIIEditor implements DatasetLoaderInterface {

    public String getType() {
        return "FLIMascii";
    }

    public void openDatasetEditor(TimpDatasetDataObject dataObj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void openDatasetEditor(DatasetTimp data, TgdDataObject dataObj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
