package org.glotaran.core.ui.visualmodelling.filesupport;

import org.glotaran.core.ui.visualmodelling.AnalysisEditorTopComponent;
import org.glotaran.gtafilesupport.GtaDataObject;
import org.glotaran.core.ui.visualmodelling.VisualModellingTopComponent;
import org.glotaran.gtafilesupport.api.GtaEditorProvider;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;

public final class OpenGtaEditor implements GtaEditorProvider {

    @Override
    public CloneableTopComponent getCloneableTopComponent(GtaDataObject dataObject) {
        VisualModellingTopComponent tc = new VisualModellingTopComponent(dataObject);
        return (CloneableTopComponent) tc;
    }
    
//    @Override
//    public CloneableTopComponent getCloneableTopComponent(GtaDataObject dataObject) {
//       
//        // Try out the new editor here!
//        TopComponent tc2 = new AnalysisEditorTopComponent(dataObject);
//        tc2.setDisplayName(dataObject.getName());
//        return (CloneableTopComponent) tc2;
//    }
}
