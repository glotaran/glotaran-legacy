package org.glotaran.analysisoverviewfilesupport;

import org.glotaran.analysisoverviewfilesupport.api.AnalysisResultFileViewerProvider;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.OpenSupport;
import org.openide.util.Lookup;
import org.openide.windows.CloneableTopComponent;

class AnalysisResultFileOpenSupport extends OpenSupport implements OpenCookie {

    public AnalysisResultFileOpenSupport(AnalysisResultDataObject.Entry entry) {
        super(entry);
    }

    protected CloneableTopComponent createCloneableTopComponent() {
        AnalysisResultDataObject dobj = (AnalysisResultDataObject) entry.getDataObject();
        AnalysisResultFileViewerProvider editorProvider = Lookup.getDefault().lookup(AnalysisResultFileViewerProvider.class);
        CloneableTopComponent tc = editorProvider.getCloneableTopComponent(dobj);
        tc.setDisplayName(dobj.getName());
        return tc;
    }
}
