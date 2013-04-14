package org.glotaran.core.main.nodes;

import java.awt.Image;

import org.glotaran.core.main.nodes.dataobjects.SummaryDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

public class TGResultsNode extends FilterNode {

    private final Image ICON_ROOT = ImageUtilities.loadImage("org/glotaran/core/main/resources/Folder-results-icon-16.png", true);
    private final Image ICON_LEAF = ImageUtilities.loadImage("org/glotaran/core/main/resources/Results-name-16.png", true);

    /**
     * Constructor.
     * @param   node      The {@code Node} describing the folder.
     */
    public TGResultsNode(Node node) {
        super(node, new TGResultsChildrenNode(node));
    }

    @Override
    public Image getIcon(int type) {
        if (this.getParentNode() instanceof TGResultsNode) {
            return ICON_LEAF;
        }
        return ICON_ROOT;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getDisplayName() {
        if (this.getParentNode() instanceof TGResultsNode) {
            return this.getName();
        }
        return NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("results");
    }

    @Override
    public boolean canRename() {
        if (this.getParentNode() instanceof TGResultsNode) {
            return true;
        }
        return false;
    }

    private static class TGResultsChildrenNode extends FilterNode.Children {

        TGResultsChildrenNode(Node node) {
            super(node);
        }

        @Override
        protected Node[] createNodes(Node n) {
            if (n.getLookup().lookup(DataFolder.class) != null) {
                return new Node[]{new TGResultsNode(n)};
            } else {
                if (n.getLookup().lookup(TimpResultDataObject.class) != null) {
                    return new Node[]{n.getLookup().lookup(TimpResultDataObject.class).getNodeDelegate()};
                } else {
                    if (n.getLookup().lookup(SummaryDataObject.class) != null) {
                        return new Node[]{n.getLookup().lookup(SummaryDataObject.class).getNodeDelegate()};
                    }
                }
            }
            return new Node[]{new FilterNode(n)};
        }
    }
}
