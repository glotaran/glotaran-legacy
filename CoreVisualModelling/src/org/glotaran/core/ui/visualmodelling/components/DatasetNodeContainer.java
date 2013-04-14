/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.components;

import org.openide.nodes.Index;
import org.openide.nodes.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author jsg210
 */
public final class DatasetNodeContainer extends Index.ArrayChildren {

    private static final Logger LOG = Logger.getLogger(DatasetNodeContainer.class.getName());
    private ArrayList<Node> list = new ArrayList<Node>();

    public DatasetNodeContainer() {
    }

    @Override
    protected List<Node> initCollection() {
        return list;
    }
}
