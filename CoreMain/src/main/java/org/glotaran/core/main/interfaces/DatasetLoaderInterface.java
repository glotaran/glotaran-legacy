/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.interfaces;

import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.models.structures.DatasetTimp;

/**
 *
 * @author lsp
 */
public interface DatasetLoaderInterface {

    public String getType();
    
    public void openDatasetEditor(DatasetTimp data, TgdDataObject dataObj);

    public void openDatasetEditor(TimpDatasetDataObject dataObj);
}
