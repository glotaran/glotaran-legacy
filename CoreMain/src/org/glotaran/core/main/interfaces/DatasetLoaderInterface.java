/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.interfaces;

import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;

/**
 *
 * @author lsp
 */
public interface DatasetLoaderInterface {

    public String getType();

    public void openDatasetEditor(TgdDataObject dataObj);

    public void openDatasetEditor(TimpDatasetDataObject dataObj);
}
