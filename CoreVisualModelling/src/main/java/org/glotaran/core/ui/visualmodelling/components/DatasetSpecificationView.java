/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.components;

import org.openide.explorer.view.BeanTreeView;

/**
 *
 * @author jsg210
 */
public class DatasetSpecificationView extends BeanTreeView {

//    private ExplorerManager manager;
    public DatasetSpecificationView() {
        super();
        setRootVisible(false);
        //setAllowedDragActions(DnDConstants.ACTION_COPY_OR_MOVE);
        // setAllowedDropActions(DnDConstants.ACTION_COPY_OR_MOVE);
        setDropTarget(true);
    }
}
