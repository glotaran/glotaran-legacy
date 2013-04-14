package org.glotaran.core.main.project;

import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.TGProjectNode;
import org.netbeans.spi.project.ui.LogicalViewProvider;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;

public class TGLogicalView implements LogicalViewProvider {

    private final TGProject project;

    public TGLogicalView(TGProject project) {
        this.project = project;
    }

    @Override
    public Node createLogicalView() {

        DataObject obj = null;
        try {
            obj = DataObject.find(project.getProjectDirectory());
        } catch (DataObjectNotFoundException ex) {
            CoreErrorMessages.projectFolderException();
        }

        if (obj != null) {
            Node root = obj.getNodeDelegate();
            return new TGProjectNode(root, project);
        } else {
            return Node.EMPTY;
        }

    }

    /**
     * @see org.netbeans.spi.project.ui.LogicalViewProvider#findPath(Node,Object)
     */
    @Override
    public Node findPath(Node root, Object target) {
        return null;
    }
}
