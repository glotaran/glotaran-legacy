package org.glotaran.core.ui.visualmodelling.menu;

import org.glotaran.core.ui.visualmodelling.nodes.VisualAbstractNode;
import org.glotaran.core.ui.visualmodelling.view.GlotaranGraphScene;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.models.gta.GtaDatasetContainer;
import org.glotaran.core.ui.visualmodelling.view.SceneSerializer;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author alex
 */
public class SceneMainMenu implements PopupMenuProvider, ActionListener {

    private static final String ADD_NEW_MODEL = "addNewModelAction"; // NOI18N
    private static final String ADD_NEW_DATASET_CONTAINER = "addNewDatasetContainerAction"; // NOI18N
    private static final String SAVE_SCENE = "saveSceneAction"; // NOI18N
    private static final String LOAD_SCENE = "loadSceneAction"; // NOI18N
    private GraphScene scene;
    private JPopupMenu menu;
    private Point point;
    private int nodeCount = 3;

    public SceneMainMenu(GraphScene scene) {
        this.scene = scene;
        menu = new JPopupMenu("Scene Menu");
        JMenuItem item;

//        item = new JMenuItem("Add New Model");
//        item.setActionCommand(ADD_NEW_MODEL);
//        item.addActionListener(this);
//        menu.add(item);

        item = new JMenuItem("Add New Dataset container");
        item.setActionCommand(ADD_NEW_DATASET_CONTAINER);
        item.addActionListener(this);
        menu.add(item);
        menu.addSeparator();

        item = new JMenuItem("Save scene...");
        item.setActionCommand(SAVE_SCENE);
        item.addActionListener(this);
        menu.add(item);

//        item = new JMenuItem ("Load scene...");
//        item.setActionCommand(LOAD_SCENE);
//        item.addActionListener(this);
//        menu.add(item);
    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        this.point = point;
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (ADD_NEW_MODEL.equals(e.getActionCommand())) {
            //           TgmDataNode newNode = null;
            Widget newWidget = null;
            final TGProject proj = (TGProject) OpenProjects.getDefault().getMainProject();
            if (proj != null) {
//                newNode = VisualCommonFunctions.createNewTgmFile(proj.getModelsFolder(true));
//                Action a = findAction("Actions/Project/org-netbeans-modules-project-ui-NewFile.instance");
//                a.actionPerformed(e);
                VisualAbstractNode newNode = new VisualAbstractNode("Model", "Containers", nodeCount++); //TODO: move Mynode and rename
                newWidget = scene.addNode(newNode);
                newWidget.setPreferredLocation(point);
            } else {
                CoreErrorMessages.noMainProjectFound();
            }
        }
        if (ADD_NEW_DATASET_CONTAINER.equals(e.getActionCommand())) {
            Widget newWidget = null;
            GtaDatasetContainer container = new GtaDatasetContainer();
            newWidget = scene.addNode(container);
            scene.validate();
            newWidget.setPreferredLocation(point);
        }
        if (SAVE_SCENE.equals(e.getActionCommand())) {
            save();
        }
        if (LOAD_SCENE.equals(e.getActionCommand())) {
            load();
        }
        scene.validate();
    }

    public Action findAction(String key) {
        FileObject fo = FileUtil.getConfigFile(key);
        if (fo != null && fo.isValid()) {
            try {
                DataObject dob = DataObject.find(fo);
                InstanceCookie ic = dob.getLookup().lookup(InstanceCookie.class);
                if (ic != null) {
                    Object instance = ic.instanceCreate();
                    if (instance instanceof Action) {
                        Action a = (Action) instance;
                        return a;
                    }
                }
            } catch (Exception e) {
                CoreErrorMessages.somethingStrange();
                return null;
            }
        }
        return null;
    }

    private void save() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Scene ...");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showSaveDialog(scene.getView()) == JFileChooser.APPROVE_OPTION) {
            SceneSerializer.serialize((GlotaranGraphScene) scene, chooser.getSelectedFile());
        }
    }

    private void load() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Scene ...");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(scene.getView()) == JFileChooser.APPROVE_OPTION) {
//            for (String edge : new ArrayList<String> (scene.getEdges ()))
//                scene.removeEdge (edge);
//            for (String node : new ArrayList<String> (scene.getNodes ()))
//                scene.removeNode (node);
            SceneSerializer.deserialize((GlotaranGraphScene) scene, chooser.getSelectedFile());
            scene.validate();
        }
    }
}
