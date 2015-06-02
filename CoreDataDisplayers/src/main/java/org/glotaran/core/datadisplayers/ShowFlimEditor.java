/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.datadisplayers;

import java.util.Set;
import org.glotaran.core.datadisplayers.flim.SdtTopComponent;
import org.glotaran.core.main.interfaces.DatasetLoaderInterface;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.models.structures.DatasetTimp;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author lsp
 */
public class ShowFlimEditor implements DatasetLoaderInterface {

    @Override
    public String getType() {
        return "FLIM";
    }


    @Override
    public void openDatasetEditor(TimpDatasetDataObject dataObj) {
        Set<TopComponent> tset = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent t : tset) {
            if (t instanceof SdtTopComponent) {
                SdtTopComponent srtc = (SdtTopComponent) t;
                if (srtc.getTimpDatasetObject() != null) {
                    if (srtc.getTimpDatasetObject().equals(dataObj)) {
                        srtc.requestActive();
                        return;
                    }
                }
            }
        }
        SdtTopComponent tc = new SdtTopComponent(dataObj);
        tc.open();
        tc.requestActive();
    }

    @Override
    public void openDatasetEditor(DatasetTimp data, TgdDataObject dataObj) {
        Set<TopComponent> tset = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent t : tset) {
            if (t instanceof SdtTopComponent) {
                SdtTopComponent srtc = (SdtTopComponent) t;
                if (srtc.getDataObject()!= null) {
                    if (srtc.getDataObject().equals(dataObj)) {
                        srtc.requestActive();
                        return;
                    }
                }
            }
        }
        SdtTopComponent tc = new SdtTopComponent(dataObj);
        tc.open();
        tc.requestActive();
    }
}
