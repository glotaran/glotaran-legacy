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

import java.awt.Point;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.gta.GtaConnection;
import org.glotaran.core.models.gta.GtaDatasetContainer;
import org.glotaran.core.models.gta.GtaModelReference;
import org.glotaran.core.models.gta.GtaOutput;
import org.glotaran.core.models.gta.GtaProjectScheme;
import org.glotaran.core.models.gta.GtaSimulationContainer;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.widgets.DatasetContainerWidget;
import org.glotaran.core.ui.visualmodelling.widgets.SimulationInputContainerWidget;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author alex
 */
public class GlotaranConnectProvider implements ConnectProvider {

    private Object source = null;
    private Object target = null;
    int edgeCounter = 0;
    private GlotaranGraphScene scene;

    public GlotaranConnectProvider(GlotaranGraphScene scene) {
        this.scene = scene;
    }

    public boolean isSourceWidget(Widget sourceWidget) {
        Object object = scene.findObject(sourceWidget);
        source = scene.isNode(object) ? object : null;
        return source != null;
    }

    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        target = scene.findObject(targetWidget);
        if (scene.isNode(target)) {
            if (target instanceof GtaDatasetContainer && source instanceof GtaModelReference) {
                return ConnectorState.ACCEPT;
            } else if (target instanceof GtaOutput && source instanceof GtaDatasetContainer) {
                return ConnectorState.ACCEPT;
            } else if (target instanceof GtaSimulationContainer && source instanceof GtaModelReference) {
                return ConnectorState.ACCEPT;
            } else if (target instanceof GtaOutput && source instanceof GtaSimulationContainer) {
                return ConnectorState.ACCEPT;
            } else {
                return ConnectorState.REJECT_AND_STOP;
            }
        }
        return target != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
    }

    public boolean hasCustomTargetWidgetResolver(Scene scene) {
        return false;
    }

    public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {
        return null;
    }

    public void createConnection(Widget sourceWidget, Widget targetWidget) {
        GtaProjectScheme gtaProjectScheme = scene.getDobj().getProgectScheme();
        GtaConnection connection = null;

        if (scene.findEdgesBetween(source, target).isEmpty()) {
            Object sourceObject = scene.findObject(sourceWidget);
            Object targetObject = scene.findObject(targetWidget);
            // Check if connection already exists in file.
            if (sourceObject instanceof GtaModelReference && targetObject instanceof GtaDatasetContainer) {
                String sourceId = ((GtaModelReference) sourceObject).getId();
                String targetId = ((GtaDatasetContainer) targetObject).getId();
                connection = getExistingConnection(gtaProjectScheme, sourceId, targetId);
                if (connection != null) {
                    connection.setActive(true);
                    scene.addEdge(connection);
                    scene.setEdgeSource(connection, source);
                    scene.setEdgeTarget(connection, target);
                    scene.validate();
                    if (targetWidget.getParentWidget() instanceof DatasetContainerWidget) {
                        ((DatasetContainerWidget) targetWidget.getParentWidget()).setConnected(true);
                    }
                }
            }
            if (connection == null) {
                connection = new GtaConnection();
                source = scene.findObject(sourceWidget);
                if (source instanceof GtaModelReference) {
                    connection.setSourceID(((GtaModelReference) source).getId());
                    connection.setSourceType(EnumTypes.ConnectionTypes.GTAMODELREFERENCE.toString());
                } else if (source instanceof GtaDatasetContainer) {
                    connection.setSourceID(((GtaDatasetContainer) source).getId());
                    connection.setSourceType(EnumTypes.ConnectionTypes.GTADATASETCONTAINER.toString());
                }
                else if (source instanceof GtaSimulationContainer) {
                    connection.setSourceID(((GtaSimulationContainer) source).getId());
                    connection.setSourceType(EnumTypes.ConnectionTypes.GTASIMULATIONCONTAINER.toString());
                }
                target = scene.findObject(targetWidget);
                if (target instanceof GtaDatasetContainer) {
                    connection.setTargetID(((GtaDatasetContainer) target).getId());
                    connection.setTargetType(EnumTypes.ConnectionTypes.GTADATASETCONTAINER.toString());
                } else if (target instanceof GtaOutput) {
                    connection.setTargetID(((GtaOutput) target).getId());
                    connection.setTargetType(EnumTypes.ConnectionTypes.GTAOUTPUT.toString());
                } else if (target instanceof GtaSimulationContainer) {
                    connection.setTargetID(((GtaSimulationContainer) target).getId());
                    connection.setTargetType(EnumTypes.ConnectionTypes.GTASIMULATIONCONTAINER.toString());
                }

                if (connection.getSourceID() != null && connection.getTargetID() != null) {
                    connection.setId(String.valueOf(scene.getNewEdgeCount()));
                    connection.setName("Connection " + scene.getEdgeCount());
                    connection.setActive(true);
                    scene.addEdge(connection);
                    scene.setEdgeSource(connection, source);
                    scene.setEdgeTarget(connection, target);
                    scene.validate();
                    if (targetWidget.getParentWidget() instanceof DatasetContainerWidget) {
                        ((DatasetContainerWidget) targetWidget.getParentWidget()).setConnected(true);
                    } else if (targetWidget.getParentWidget() instanceof SimulationInputContainerWidget) {
                        ((SimulationInputContainerWidget) targetWidget.getParentWidget()).setConnected(true);
                    }
                }
            }
        }
    }

    private GtaConnection getExistingConnection(GtaProjectScheme gtaProjectScheme, String sourceId, String targetId) {
        GtaConnection connection = null;


        for (GtaConnection testConnection : gtaProjectScheme.getConnection()) {
            if (testConnection.getSourceID() != null && testConnection.getTargetID() != null) {
                if (testConnection.getSourceID().equalsIgnoreCase(sourceId) && testConnection.getTargetID().equalsIgnoreCase(targetId)) {
                    connection = testConnection;


                }
            }
        }
        return connection;

    }
}
