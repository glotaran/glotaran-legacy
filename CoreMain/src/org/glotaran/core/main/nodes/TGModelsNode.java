package org.glotaran.core.main.nodes;

import java.awt.Image;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.loaders.DataFolder;
import org.openide.util.NbBundle;

/**
 */
public class TGModelsNode extends FilterNode {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/main/resources/Folder-models-icon-16.png", true);

    /**
     * Constructor.
     * @param Node, map folder
     */
    public TGModelsNode(Node node) {
        super(node, new TGModelsChildrenNode(node));
    }

    @Override
    public Image getIcon(int type) {
        return ICON;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getDisplayName() {
        if (this.getParentNode() instanceof TGModelsNode) {
            return this.getName();
        }
        return NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("models");
    }

    @Override
    public boolean canRename() {
        if (this.getParentNode() instanceof TGModelsNode) {
            return true;
        }
        return false;
    }

    private static class TGModelsChildrenNode extends FilterNode.Children {

        TGModelsChildrenNode(Node node) {
            super(node);
        }

        @Override
        protected Node[] createNodes(Node n) {
//            Collection<? extends SupportedXMLFilesInterface> services =
//                    Lookup.getDefault().lookupAll(SupportedXMLFilesInterface.class);
//            String s = null;
//            for (int i = 0; i < services.size(); i++){
//                SupportedXMLFilesInterface service = (SupportedXMLFilesInterface)services.toArray()[i];
//                if (service.getType().equalsIgnoreCase("TGMDataObject")){
//                    if (n.getLookup().lookup(service.getDataObjectClass().getClass()) != null) {
//                        s = service.getType();
//                        return new Node[]{n};
//                    }
//                }
//            }
            if (n.getLookup().lookup(DataFolder.class) != null) {
                return new Node[]{new TGModelsNode(n)};
            }
            return new Node[]{new FilterNode(n)};
        }
    }
}
