/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.components;

/**
 *
 * @author jsg210
 */
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.datatransfer.PasteType;

public class DummyChildFactory extends ChildFactory<String> {

    ArrayList<String> names = new ArrayList<String>();

    public DummyChildFactory() {
    }

    @Override
    protected boolean createKeys(List<String> list) {
        for (String name : names) {
            list.add(name);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(String name) {
        Node node = new AbstractNode(Children.LEAF) {

            @Override
            public PasteType getDropType(Transferable t, int arg1, int arg2) {
                final Node node = NodeTransfer.node(t, arg1);
                return new PasteType() {

                    @Override
                    public Transferable paste() throws IOException {
                        names.add(node.getDisplayName());
                        refresh(true);
                        return null;
                    }
                };
            }
        };

        node.setDisplayName(name);
        return node;
    }
}
