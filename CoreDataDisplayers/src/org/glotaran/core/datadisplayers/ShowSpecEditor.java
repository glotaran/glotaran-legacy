/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.datadisplayers;

import java.util.Set;
import org.glotaran.core.datadisplayers.spec.SpecEditorTopCompNew;
import org.glotaran.core.main.interfaces.DatasetLoaderInterface;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author lsp
 */
public class ShowSpecEditor implements DatasetLoaderInterface {

    @Override
    public String getType() {
        return "spec";
    }

    @Override
    public void openDatasetEditor(TgdDataObject dataObj) {
        Set<TopComponent> tset = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent t : tset) {
            if (t instanceof SpecEditorTopCompNew) {
                SpecEditorTopCompNew srtc = (SpecEditorTopCompNew) t;
                if (srtc.getDataObject() != null) {
                    if (srtc.getDataObject().equals(dataObj)) {
                        srtc.requestActive();
                        return;
                    }
                }
            }
        }
        SpecEditorTopCompNew tc = new SpecEditorTopCompNew(dataObj);
        tc.open();
        tc.requestActive();
    }

    @Override
    public void openDatasetEditor(TimpDatasetDataObject dataObj) {
        Set<TopComponent> tset = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent t : tset) {
            if (t instanceof SpecEditorTopCompNew) {
                SpecEditorTopCompNew srtc = (SpecEditorTopCompNew) t;
                if (srtc.getDataObject2() != null) {
                    if (srtc.getDataObject2().equals(dataObj)) {
                        srtc.requestActive();
                        return;
                    }
                }
            }
        }
        SpecEditorTopCompNew tc = new SpecEditorTopCompNew(dataObj);
        tc.open();
        tc.requestActive();
    }
}
