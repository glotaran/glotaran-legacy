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
import java.io.File;
import org.glotaran.core.models.gta.GtaConnection;
import org.glotaran.core.models.gta.GtaDataset;
import org.glotaran.core.models.gta.GtaDatasetContainer;
import org.glotaran.core.models.gta.GtaLayout;
import org.glotaran.core.models.gta.GtaModelReference;
import org.glotaran.core.models.gta.GtaOutput;
import org.glotaran.core.models.gta.GtaProjectScheme;
import org.glotaran.core.ui.visualmodelling.components.DatasetContainerComponent;
import org.glotaran.core.ui.visualmodelling.nodes.DatasetComponentNode;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;

/**
 * @author David Kaspar
 */
public class SceneSerializer {

    private static final String VERSION_VALUE = "1.0"; // NOI18N
    private static final String DEFAULT_NAME = "Global and Target Analysis Model"; // NOI18N

    // call in AWT to serialize scene
    public static void serialize(GlotaranGraphScene scene, File file) {
        GtaLayout layout;
        FileObject fo;

        GtaProjectScheme gtaScheme = new GtaProjectScheme();

        gtaScheme.setVersion(VERSION_VALUE);
        gtaScheme.setName(DEFAULT_NAME);

        gtaScheme.setNodeCounter(String.valueOf(scene.getNodeCount()));

        for (Object node : scene.getNodes()) {

            // Iterate over all model containers
            if (node instanceof GtaModelReference) {
                GtaModelReference modelRef = (GtaModelReference) node;
                Widget widget = scene.findWidget(node);
                Point location = widget.getPreferredLocation();
                layout = new GtaLayout();
                layout.setXposition(location.getX());
                layout.setYposition(location.getY());
                layout.setHeight(widget.getBounds().getHeight());
                layout.setWidth(widget.getBounds().getWidth());
                modelRef.setLayout(layout);
                gtaScheme.getModel().add(modelRef);
            }
            // Iterate over all dataset containers
            if (node instanceof GtaDatasetContainer) {
                GtaDatasetContainer gdc = (GtaDatasetContainer) node;
                Widget widget = scene.findWidget(gdc);
                for (Widget testWidget : widget.getChildren()) {
                    if (testWidget instanceof ComponentWidget) {
                        ComponentWidget cw = ((ComponentWidget) testWidget);
                        if (cw.getComponent() instanceof DatasetContainerComponent) {
                            DatasetContainerComponent dcc = (DatasetContainerComponent) cw.getComponent();
                            gdc.getDatasets().clear();
                            for (Node datasetNode : dcc.getExplorerManager().getRootContext().getChildren().getNodes()) {
                                DatasetComponentNode dcn = (DatasetComponentNode) datasetNode;
                                fo = dcn.getTdn().getObject().getPrimaryFile();
                                GtaDataset gtaDataset = new GtaDataset();
                                gtaDataset.setPath(FileUtil.getRelativePath(FileOwnerQuery.getOwner(dcn.getTdn().getObject().getPrimaryFile()).getProjectDirectory(), fo));
                                gtaDataset.setFilename(fo.getName());
                                gdc.getDatasets().add(gtaDataset);
                            }
                            Point location = widget.getPreferredLocation();
                            layout = new GtaLayout();
                            layout.setXposition(location.getX());
                            layout.setYposition(location.getY());
                            layout.setHeight(widget.getBounds().getHeight());
                            layout.setWidth(widget.getBounds().getWidth());
                            gdc.setLayout(layout);
                            //gdc.setId(dcc.getDisplayName());
                            gtaScheme.getDatasetContainer().add(gdc);
                        }
                    }


                }
            }
            if (node instanceof GtaOutput) {
                gtaScheme.getOutput().add((GtaOutput) node);
            }

        }
        for (Object edge : scene.getEdges()) {
            if (edge instanceof GtaConnection) {
                GtaConnection connection = (GtaConnection) edge;
                gtaScheme.getConnection().add(connection);
            }
        }

        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(gtaScheme.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(gtaScheme, file);
        } catch (javax.xml.bind.JAXBException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
    }

    // call in AWT to deserialize scene
    public static void deserialize(GlotaranGraphScene scene, File file) {
        GtaProjectScheme gtaProjectScheme = null;
        if (gtaProjectScheme == null) {
            gtaProjectScheme = new GtaProjectScheme();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(gtaProjectScheme.getClass().getPackage().getName());
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                gtaProjectScheme = (GtaProjectScheme) unmarshaller.unmarshal(file); //NOI18N //replaced: new java.io.File("File path") //Fix this: java.lang.IllegalArgumentException: file parameter must not be null
            } catch (javax.xml.bind.JAXBException ex) {
                // XXXTODO Handle exception
                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            }
            scene.loadScene(gtaProjectScheme);
        }
    }
}
