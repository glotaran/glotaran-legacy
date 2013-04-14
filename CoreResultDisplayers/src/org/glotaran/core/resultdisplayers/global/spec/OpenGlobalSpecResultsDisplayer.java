/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.resultdisplayers.global.spec;

import java.util.ArrayList;
import org.glotaran.core.main.interfaces.GlobalSpecResultsProviderInterface;
import org.glotaran.core.models.results.GtaResult;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author jsg210
 */
public final class OpenGlobalSpecResultsDisplayer implements GlobalSpecResultsProviderInterface {

    public CloneableTopComponent getCloneableTopComponent(ArrayList<TimpResultDataset> results, GtaResult gtaResult) {
        CloneableTopComponent tc = new GlobalSpecResultsDisplayerTopComponent(results, gtaResult);
        return tc;
    }
}
