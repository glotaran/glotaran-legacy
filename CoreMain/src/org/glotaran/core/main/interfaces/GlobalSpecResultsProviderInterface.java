/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.interfaces;

import java.util.ArrayList;
import org.glotaran.core.models.results.GtaResult;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author jsg210
 */
public interface GlobalSpecResultsProviderInterface {

    public CloneableTopComponent getCloneableTopComponent(ArrayList<TimpResultDataset> timpResultDatasets, GtaResult gtaResult);
}
