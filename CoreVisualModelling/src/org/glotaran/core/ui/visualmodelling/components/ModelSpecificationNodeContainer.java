/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.components;

import org.openide.nodes.Index;
import org.openide.nodes.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.logging.Logger;
import org.glotaran.core.ui.visualmodelling.nodes.PropertiesAbstractNode;

/**
 *
 * @author jsg210
 */
public final class ModelSpecificationNodeContainer extends Index.ArrayChildren {

    private static final Logger LOG = Logger.getLogger(ModelSpecificationNodeContainer.class.getName());
    private ArrayList<Node> list = new ArrayList<Node>();

    public ModelSpecificationNodeContainer() {
    }

    @Override
    protected List<Node> initCollection() {
        return list;
    }

    public ListIterator<Node> getRemaining(Node current) {
        Vector<Node> v = new Vector<Node>();
        for (Node n : list.subList(indexOf(current), list.size())) {
            v.add(n.getLookup().lookup(PropertiesAbstractNode.class));
        }
        return (v.listIterator());
    }

    public void add(Node n) {
        add(new Node[]{n});
    }
}
