package org.glotaran.core.main.nodes;

import java.awt.Image;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.loaders.DataFolder;
import org.openide.util.NbBundle;

/**
 */
public class TGSimModelsNode extends FilterNode {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/main/resources/Folder-simulations-icon-16.png", true);

    /**
     * Constructor.
     * @param Node, map folder
     */
    public TGSimModelsNode(Node node) {
        super(node, new TGSimModelsChildrenNode(node));
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
        if (this.getParentNode() instanceof TGSimModelsNode) {
            return this.getName();
        }
        return NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("simmodels");
    }

    @Override
    public boolean canRename() {
        if (this.getParentNode() instanceof TGSimModelsNode) {
            return true;
        }
        return false;
    }

    private static class TGSimModelsChildrenNode extends FilterNode.Children {

        TGSimModelsChildrenNode(Node node) {
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
                return new Node[]{new TGSimModelsNode(n)};
            }
            return new Node[]{new FilterNode(n)};
        }
    }
}
