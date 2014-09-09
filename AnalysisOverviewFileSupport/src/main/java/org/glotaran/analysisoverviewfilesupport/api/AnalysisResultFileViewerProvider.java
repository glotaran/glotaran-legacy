/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.analysisoverviewfilesupport.api;

import org.glotaran.analysisoverviewfilesupport.AnalysisResultDataObject;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author jsg210
 */
public interface AnalysisResultFileViewerProvider {

    public CloneableTopComponent getCloneableTopComponent(AnalysisResultDataObject entry);
}
