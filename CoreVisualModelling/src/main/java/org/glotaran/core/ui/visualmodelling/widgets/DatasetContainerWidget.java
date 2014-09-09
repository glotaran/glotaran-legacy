/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.widgets;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.glotaran.core.models.gta.GtaConnection;
import org.glotaran.core.models.gta.GtaModelDifferences;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.components.DatasetContainerComponent;
import org.glotaran.core.ui.visualmodelling.menu.NodeMenu;
import org.glotaran.core.ui.visualmodelling.view.GlotaranGraphScene;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.filesystems.FileObject;

/**
 *
 * @author slapten
 */
public class DatasetContainerWidget extends Widget {

    private boolean connected = false;
    private List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

    public DatasetContainerWidget(GlotaranGraphScene scene, DatasetContainerComponent component, String name) {
        super(scene);
        setLayout(LayoutFactory.createOverlayLayout());
        setBorder(BorderFactory.createLineBorder());//createRoundedBorder(5, 5, Color.gray, Color.black));//
        getActions().addAction(scene.getConnectAction());
        //TODO implement once reconnect is working
        //getActions().addAction(scene.getReconnectAction());
        getActions().addAction(scene.getSelectAction());
        getActions().addAction(scene.getResizeAction());
        getActions().addAction(scene.getMoveAction());
        setBorder(BorderFactory.createResizeBorder(4));
        LabelWidget label = new LabelWidget(scene, name);
        label.setOpaque(true);
        label.setBackground(Color.LIGHT_GRAY);
        label.getActions().addAction(scene.getConnectAction());
        //TODO implement once reconnect is working
        //label.getActions().addAction(scene.getReconnectAction());
        addChild(0, label);
        ComponentWidget componentWidget = new ComponentWidget(scene, component);
        addChild(1, componentWidget);
        getActions().addAction(ActionFactory.createPopupMenuAction(new NodeMenu(scene)));
        addPropertyChangeListener(component);
    }

    public DatasetContainerComponent getContainerComponent() {
        return (DatasetContainerComponent) ((ComponentWidget) getChildren().get(1)).getComponent();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        if (connected) {
            GlotaranGraphScene scene = (GlotaranGraphScene) getScene();
            for (Object connection : scene.findNodeEdges(scene.findObject(this), false, true)) {
                if ((connection instanceof GtaConnection)
                        && (((GtaConnection) connection).getTargetType().equalsIgnoreCase(EnumTypes.ConnectionTypes.GTADATASETCONTAINER.toString()))) {
                    GtaConnection gtaConnection = (GtaConnection) connection;
                    if (gtaConnection.isActive()) {
                        if (gtaConnection.getModelDifferences() == null) {
                            gtaConnection.setModelDifferences(new GtaModelDifferences());
                        }
                        fire("connectionChange",
                                ((ModelContainerWidget) scene.findWidget(scene.getNodeForID(gtaConnection.getSourceID()))).getModelTgm(),
                                gtaConnection.getModelDifferences());
                    }
                }
            }
        } else {
            fire("connectionChange", null, null);
        }
        this.connected = connected;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    public FileObject getSchemaPath() {
        FileObject path = null;
        path = ((GlotaranGraphScene) getScene()).getDobj().getPrimaryFile();
        return path;
    }

    public void fire(String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }
}
