package org.glotaran.gtafilesupport;

import org.glotaran.gtafilesupport.api.GtaEditorProvider;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.OpenSupport;
import org.openide.util.Lookup;
import org.openide.windows.CloneableTopComponent;

class GtaOpenSupport extends OpenSupport implements OpenCookie {

    public GtaOpenSupport(GtaDataObject.Entry entry) {
        super(entry);
    }

    protected CloneableTopComponent createCloneableTopComponent() {
        GtaDataObject dobj = (GtaDataObject) entry.getDataObject();
        GtaEditorProvider editorProvider = Lookup.getDefault().lookup(GtaEditorProvider.class);
        CloneableTopComponent tc = editorProvider.getCloneableTopComponent(dobj);
        tc.setDisplayName(dobj.getName());
        return tc;
    }
}
