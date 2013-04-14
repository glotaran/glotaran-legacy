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
import javax.swing.JComponent;
import org.glotaran.core.ui.visualmodelling.components.OutputPanel;
import org.glotaran.core.ui.visualmodelling.menu.NodeMenu;
import org.glotaran.core.ui.visualmodelling.view.GlotaranGraphScene;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author slapten
 */
public class OutputWidget extends Widget {

    private boolean connected = false;
    private List listeners = Collections.synchronizedList(new LinkedList());

    public OutputWidget(GlotaranGraphScene scene, JComponent component, String name) {
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
        //addPropertyChangeListener(component);
    }

    public OutputPanel getContainerComponent() {
        return (OutputPanel) ((ComponentWidget) getChildren().get(1)).getComponent();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        //TODO: implementation code here
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    public void fire(String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }
}
