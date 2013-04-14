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
package org.glotaran.core.ui.visualmodelling.view;

import java.awt.*;
import org.glotaran.core.messages.CoreErrorMessages;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.glotaran.core.models.gta.GtaConnection;
import org.glotaran.core.models.gta.GtaDatasetContainer;
import org.glotaran.core.models.gta.GtaModelReference;
import org.glotaran.core.ui.visualmodelling.widgets.DatasetContainerWidget;

/**
 *
 * @author alex
 */
public class GlotaranReconnectProvider implements ReconnectProvider {

    private GtaConnection edge;
    private Object originalNode;
    private Object replacementNode;
    private GlotaranGraphScene scene;

    public GlotaranReconnectProvider(GlotaranGraphScene scene) {
        this.scene = scene;
    }

    public void reconnectingStarted(ConnectionWidget connectionWidget, boolean reconnectingSource) {
        if (!reconnectingSource) {
            Widget widget = scene.findWidget(scene.getNodeForID(((GtaConnection) scene.findObject(connectionWidget)).getTargetID()));
            if (widget instanceof DatasetContainerWidget) {
                ((DatasetContainerWidget) widget).setConnected(false);
                ((DatasetContainerWidget) widget).getContainerComponent().setConnectedModel(null);
            }
        }
    }

    public void reconnectingFinished(ConnectionWidget connectionWidget, boolean reconnectingSource) {
    }

    public boolean isSourceReconnectable(ConnectionWidget connectionWidget) {
        Object object = scene.findObject(connectionWidget);
        edge = scene.isEdge(object) ? (GtaConnection) object : null;
        originalNode = edge != null ? scene.getEdgeSource(edge) : null;
        return originalNode != null;
    }

    public boolean isTargetReconnectable(ConnectionWidget connectionWidget) {
        Object object = scene.findObject(connectionWidget);
        edge = scene.isEdge(object) ? (GtaConnection) object : null;
        originalNode = edge != null ? scene.getEdgeTarget(edge) : null;
        return originalNode != null;
    }

    public ConnectorState isReplacementWidget(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
        Object object = scene.findObject(replacementWidget);
        replacementNode = scene.isNode(object) ? object : null;
        if (reconnectingSource) {
            return (object != null && object instanceof GtaModelReference)
                    ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
        } else {
            return (object != null && object instanceof GtaDatasetContainer)
                    ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
        }
//        return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
    }

    public boolean hasCustomReplacementWidgetResolver(Scene scene) {
        return false;
    }

    public Widget resolveReplacementWidget(Scene scene, Point sceneLocation) {
        return null;
    }

    public void reconnect(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
        if (replacementWidget == null) {
            scene.removeEdge(edge);
        } else if (reconnectingSource) {
            GtaConnection connection = (GtaConnection) scene.findObject(connectionWidget);
            connection.setSourceID(((GtaModelReference) replacementNode).getId());
            scene.setEdgeSource(edge, replacementNode);
            scene.getDobj().setModified(true);
        } else {
            if (replacementWidget.getParentWidget() instanceof DatasetContainerWidget) {
                if (!((DatasetContainerWidget) replacementWidget.getParentWidget()).isConnected()) {
                    ((DatasetContainerWidget) replacementWidget.getParentWidget()).setConnected(true);
                    GtaConnection connection = (GtaConnection) scene.findObject(connectionWidget);
                    connection.setTargetID(((GtaDatasetContainer) replacementNode).getId());
                    scene.setEdgeTarget(edge, replacementNode);
                    scene.getDobj().setModified(true);
                    //TODO finish implementing
                } else {
                    ((DatasetContainerWidget) scene.findWidget(scene.getNodeForID(edge.getTargetID()))).setConnected(true);
                    CoreErrorMessages.containerConnected("", "");
                }
            }
        }
        scene.validate();
    }
}
