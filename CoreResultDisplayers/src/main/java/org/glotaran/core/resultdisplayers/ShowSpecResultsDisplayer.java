/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.resultdisplayers;

import java.util.Set;
import org.glotaran.core.main.interfaces.ResultsLoaderInterface;
import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;
import org.glotaran.core.resultdisplayers.spec.SpecResultsTopComponent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sergey
 */
public class ShowSpecResultsDisplayer implements ResultsLoaderInterface {

    public String getType() {
        return "spec";
    }

    public void openResultDisplayer(TimpResultDataObject dataObj) {
        Set<TopComponent> tset = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent t : tset) {
            if (t instanceof SpecResultsTopComponent) {
                SpecResultsTopComponent srtc = (SpecResultsTopComponent) t;
                if (srtc.getDataObject().equals(dataObj)) {
                    srtc.requestActive();
                    return;
                }
            }
        }
        SpecResultsTopComponent tc = new SpecResultsTopComponent(dataObj);
        tc.open();
        tc.requestActive();
    }
}
