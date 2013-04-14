package org.glotaran.core.main.nodes;

import java.awt.Image;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.glotaran.core.main.project.TGProject;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class TGProjectNode extends FilterNode {

    private final ImageIcon ICON = new ImageIcon(ImageUtilities.loadImage("org/glotaran/core/main/resources/Project-icon.png", true));
    private final TGProject project;

    /**
     * Constructor.<br>
     *
     * @param   project The current {@code TGProject}.
     */
    public TGProjectNode(Node node, TGProject project) {
        super(node, new TGProjectNodeFilter(node, project),
                //The projects system wants the project in the Node's lookup.
                //NewAction and friends want the original Node's lookup.
                //Make a merge of both
                //Lookups.singleton(project));

                new ProxyLookup(new Lookup[]{Lookups.singleton(project),
                    node.getLookup()
                }));

        this.project = project;
    }

    @Override
    public Image getIcon(int arg0) {
        return ICON.getImage();
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return ICON.getImage();
    }

    @Override
    public String getDisplayName() {
        ProjectInformation info = project.getLookup().lookup(ProjectInformation.class);
        return info.getDisplayName();
    }

    @Override
    public Action[] getActions(boolean arg0) {
        Action[] nodeActions = new Action[7];
        nodeActions[0] = CommonProjectActions.newFileAction();
        nodeActions[1] = CommonProjectActions.copyProjectAction();
        nodeActions[2] = CommonProjectActions.deleteProjectAction();
        nodeActions[5] = CommonProjectActions.setAsMainProjectAction();
        nodeActions[6] = CommonProjectActions.closeProjectAction();
        return nodeActions;
    }

    public TGProject getProject() {
        return project;
    }
}

class TGProjectNodeFilter extends FilterNode.Children {

    private final TGProject project;

    public TGProjectNodeFilter(Node original, TGProject project) {
        super(original);
        this.project = project;
    }

    @Override
    protected Node[] createNodes(Node node) {
        final String name = node.getName();
        final DataObject dob = node.getLookup().lookup(DataObject.class);
        final FileObject file = dob.getPrimaryFile();
        if (file.equals(project.getDatasetsFolder(true))) {
            return new Node[]{new TGDatasetNode(node)};
        } else if (file.equals(project.getModelsFolder(true))) {
            return new Node[]{new TGModelsNode(node)};
        } else if (file.equals(project.getSchemaFolder(true))) {
            return new Node[]{new TGSchemaNode(node)};
        } else if (file.equals(project.getResultsFolder(true))) {
            return new Node[]{new TGResultsNode(node)};        
        } else if (file.equals(project.getSimModelFolder(true))) {
            return new Node[]{new TGSimModelsNode(node)};
        }
        return new Node[]{};
    }

    public TGProject getProject() {
        return project;
    }
}


