package org.glotaran.core.resultdisplayers;

import java.util.Set;
import org.glotaran.core.main.interfaces.ResultsLoaderInterface;
import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;
import org.glotaran.core.resultdisplayers.flim.FlimResultsTopComponent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sergey
 */
public class ShowFlimResultsDisplayer implements ResultsLoaderInterface {

    public String getType() {
        return "flim";
    }

    public void openResultDisplayer(TimpResultDataObject dataObj) {
        Set<TopComponent> tset = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent t : tset) {
            if (t instanceof FlimResultsTopComponent) {
                FlimResultsTopComponent citc = (FlimResultsTopComponent) t;
                if (citc.getDataObject().equals(dataObj)) {
                    citc.requestActive();
                    return;
                }
            }
        }
        FlimResultsTopComponent tc = new FlimResultsTopComponent(dataObj);
        tc.open();
        tc.requestActive();
    }
}
