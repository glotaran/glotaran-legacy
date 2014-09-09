/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.glotaran.core.ui.visualmodelling.menu;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.glotaran.core.models.gta.GtaConnection;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.nodes.DatasetsRootNode;
import org.glotaran.core.ui.visualmodelling.view.GlotaranGraphScene;
import org.glotaran.core.ui.visualmodelling.widgets.DatasetContainerWidget;

/**
 *
 * @author alex
 */
public class EdgeMenu implements PopupMenuProvider, ActionListener {

    private static final String ADD_REMOVE_CP_ACTION = "addRemoveCPAction"; // NOI18N
//    private static final String DELETE_ALL_CP_ACTION = "deleteAllCPAction"; // NOI18N
    private static final String DELETE_TRANSITION = "deleteTransition"; // NOI18N
    private GlotaranGraphScene scene;
    private JPopupMenu menu;
    private ConnectionWidget edge;
    private Point point;

    public EdgeMenu(GlotaranGraphScene scene) {
        this.scene = scene;
        menu = new JPopupMenu("Transition Menu");
        JMenuItem item;

        item = new JMenuItem("Add/Delete Control Point");
        item.setActionCommand(ADD_REMOVE_CP_ACTION);
        item.addActionListener(this);
        menu.add(item);

        menu.addSeparator();

//        item = new JMenuItem("Delete All Control Points");
//        item.setActionCommand(DELETE_ALL_CP_ACTION);
//        item.addActionListener(this);
//        item.setEnabled(false);
//        menu.add(item);

        item = new JMenuItem("Delete Transition");
        item.setActionCommand(DELETE_TRANSITION);
        item.addActionListener(this);
        menu.add(item);

    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        if (widget instanceof ConnectionWidget) {
            this.edge = (ConnectionWidget) widget;
            this.point = point;
            return menu;
        }
        return null;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ADD_REMOVE_CP_ACTION)) {
            addRemoveControlPoint(point);
        } else if (e.getActionCommand().equals(DELETE_TRANSITION)) {
            removeConnection();
        }
    }

    private void removeConnection() {
        GtaConnection connection = (GtaConnection) scene.findObject(edge);
//        if (connection.getTargetType().equalsIgnoreCase(EnumTypes.ConnectionTypes.GTADATASETCONTAINER.toString())){
//            DatasetsRootNode rootNode = (DatasetsRootNode) ((DatasetContainerWidget)scene.findWidget(scene.getNodeForID(connection.getTargetID()))).getContainerComponent().getExplorerManager().getRootContext();
//            for (int i = 0; i < rootNode.getChildren().getNodesCount(); i++){
//                rootNode.getChildren().getNodes()[i].getChildren().remove(
//                        rootNode.getChildren().getNodes()[i].getChildren().getNodes());
//            }
//        }
        scene.removeEdge((GtaConnection) scene.findObject(edge));
    }

    private void addRemoveControlPoint(Point localLocation) {
        ArrayList<Point> list = new ArrayList<Point>(edge.getControlPoints());
        double createSensitivity = 1.00, deleteSensitivity = 5.00;
        if (!removeControlPoint(localLocation, list, deleteSensitivity)) {
            Point exPoint = null;
            int index = 0;
            for (Point elem : list) {
                if (exPoint != null) {
                    Line2D l2d = new Line2D.Double(exPoint, elem);
                    if (l2d.ptLineDist(localLocation) < createSensitivity) {
                        list.add(index, localLocation);
                        break;
                    }
                }
                exPoint = elem;
                index++;
            }
        }
        edge.setControlPoints(list, false);
    }

    private boolean removeControlPoint(Point point, ArrayList<Point> list, double deleteSensitivity) {
        for (Point elem : list) {
            if (elem.distance(point) < deleteSensitivity) {
                list.remove(elem);
                return true;
            }
        }
        return false;
    }
}
