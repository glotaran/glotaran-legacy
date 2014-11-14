/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.datadisplayers;

import java.util.Set;
import org.glotaran.core.datadisplayers.multispec.MultiSpecEditorTopComponent;
import org.glotaran.core.main.interfaces.DatasetLoaderInterface;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.models.structures.DatasetTimp;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sergey
 */

public class ShowMultiSpecEditor implements DatasetLoaderInterface {

    @Override
    public String getType() {
        return "multispec";
    }

    @Override
    public void openDatasetEditor(DatasetTimp data, TgdDataObject dataObj) {
        Set<TopComponent> tset = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent t : tset) {
            if (t instanceof MultiSpecEditorTopComponent) {
                MultiSpecEditorTopComponent srtc = (MultiSpecEditorTopComponent) t;
                if (srtc.getDataObject() != null) {
                    if (srtc.getDataObject().equals(dataObj)) {
                        srtc.requestActive();
                        return;
                    }
                }
            }
        }
        MultiSpecEditorTopComponent tc = new MultiSpecEditorTopComponent(data,dataObj);
        tc.open();
        tc.requestActive();
 
    }

    @Override
    public void openDatasetEditor(TimpDatasetDataObject dataObj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
