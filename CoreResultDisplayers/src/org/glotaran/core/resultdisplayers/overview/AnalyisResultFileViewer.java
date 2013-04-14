package org.glotaran.core.resultdisplayers.overview;


import org.glotaran.analysisoverviewfilesupport.AnalysisResultDataObject;
import org.glotaran.analysisoverviewfilesupport.api.AnalysisResultFileViewerProvider;
import org.openide.windows.CloneableTopComponent;

public final class AnalyisResultFileViewer implements AnalysisResultFileViewerProvider {

    @Override
    public CloneableTopComponent getCloneableTopComponent(AnalysisResultDataObject dataObject) {
        AnalysisResultFileViewerTopComponent tc = new AnalysisResultFileViewerTopComponent(dataObject);
        tc.setDisplayName(dataObject.getName());
        return tc;
    }
}
