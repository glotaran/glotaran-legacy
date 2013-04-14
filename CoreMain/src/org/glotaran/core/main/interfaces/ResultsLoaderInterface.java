/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.interfaces;

import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;

/**
 *
 * @author lsp
 */
public interface ResultsLoaderInterface {

    public String getType();

//    public void openDatasetEditor(TgdDataObject dataObj);
    public void openResultDisplayer(TimpResultDataObject dataObj);
}
