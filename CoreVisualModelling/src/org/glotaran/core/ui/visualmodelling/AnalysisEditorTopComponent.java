/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.glotaran.core.main.nodes.TimpDatasetNode;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.models.gta.GtaConnection;
import org.glotaran.core.models.gta.GtaDataset;
import org.glotaran.core.models.gta.GtaDatasetContainer;
import org.glotaran.core.models.gta.GtaModelReference;
import org.glotaran.core.models.gta.GtaOutput;
import org.glotaran.core.models.gta.GtaProjectScheme;
import org.glotaran.core.models.gta.GtaSimulationContainer;
import org.glotaran.core.models.gta.GtaSimulationInputRef;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.components.DatasetContainerComponent;
import org.glotaran.core.ui.visualmodelling.components.ModelContainer;
import org.glotaran.core.ui.visualmodelling.components.OutputPanel;
import org.glotaran.core.ui.visualmodelling.components.SimulationContainerComponent;
import org.glotaran.core.ui.visualmodelling.nodes.DatasetComponentNode;
import org.glotaran.core.ui.visualmodelling.nodes.SimInputComponentNode;
import org.glotaran.core.ui.visualmodelling.palette.PaletteSupport;
import org.glotaran.core.ui.visualmodelling.view.CompartmentalModellingGraphScene;
import org.glotaran.gtafilesupport.GtaDataObject;
import org.glotaran.simfilesupport.spec.SpectralModelDataNode;
import org.glotaran.simfilesupport.spec.SpectralModelDataObject;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.api.visual.action.AcceptProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.windows.CloneableTopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.glotaran.core.ui.visualmodelling//AnalysisEditor//EN",
autostore = false)
public final class AnalysisEditorTopComponent extends CloneableTopComponent implements PropertyChangeListener {

    private static AnalysisEditorTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "AnalysisEditorTopComponent";
    private final InstanceContent content = new InstanceContent();
    private boolean connected;
    private DatasetContainerComponent datasetContainer;
    private ModelContainer modelContainer;
    private SimulationContainerComponent simulationContainer;
    private OutputPanel outputPanel;
    private List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());
    private TgmDataObject tgmDObj;
    private TimpDatasetDataObject tdobj;

    public AnalysisEditorTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(AnalysisEditorTopComponent.class, "CTL_AnalysisEditorTopComponent"));
        setToolTipText(NbBundle.getMessage(AnalysisEditorTopComponent.class, "HINT_AnalysisEditorTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        modelContainer = new ModelContainer();
        datasetContainer = new DatasetContainerComponent();
        outputPanel = new OutputPanel();
        jPanel4.add(modelContainer);
        jPanel5.add(datasetContainer);
        jPanel3.add(outputPanel);
        content.add(PaletteSupport.createPalette());
        associateLookup(new AbstractLookup(content));
    }

    public AnalysisEditorTopComponent(GtaDataObject dobj) {
        initComponents();
        setName(NbBundle.getMessage(AnalysisEditorTopComponent.class, "CTL_AnalysisEditorTopComponent"));
        setToolTipText(NbBundle.getMessage(AnalysisEditorTopComponent.class, "HINT_AnalysisEditorTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        content.add(dobj);
        content.add(PaletteSupport.createPalette());
        addPropertyChangeListener(instance);
        associateLookup(new AbstractLookup(content));

        FileObject resultsFolder = ((TGProject) FileOwnerQuery.getOwner(dobj.getPrimaryFile())).getResultsFolder(true);

        //simulationContainer = new SimulationContainerComponent();
        GtaOutput gtaOutput;
        if (dobj.getProgectScheme().getOutput() != null && dobj.getProgectScheme().getOutput().size() > 0) {
            gtaOutput = dobj.getProgectScheme().getOutput().get(0);
        } else {
            gtaOutput = new GtaOutput();
        }
        outputPanel = new OutputPanel(gtaOutput, resultsFolder, this);

        GtaDatasetContainer gtaDatasetContainer;
        if (dobj.getProgectScheme().getDatasetContainer() != null && dobj.getProgectScheme().getDatasetContainer().size() > 0) {
            gtaDatasetContainer = dobj.getProgectScheme().getDatasetContainer().get(0);
        } else {
            gtaDatasetContainer = new GtaDatasetContainer();
        }
        datasetContainer = new DatasetContainerComponent(gtaDatasetContainer, this);

        if (gtaDatasetContainer.getDatasets() != null) {
            for (GtaDataset dataset : gtaDatasetContainer.getDatasets()) {
                try {
                    File fl = new File(FileOwnerQuery.getOwner(dobj.getPrimaryFile()).getProjectDirectory().getPath() + File.separator + dataset.getPath());
                    FileObject fo = FileUtil.createData(fl);
                    DataObject dObj = DataObject.find(fo);
                    if (dObj != null) {
                        tdobj = (TimpDatasetDataObject) dObj;

                        datasetContainer.getExplorerManager().getRootContext().getChildren().add(new Node[]{
                                    new DatasetComponentNode(
                                    (TimpDatasetNode) tdobj.getNodeDelegate(),
                                    new Index.ArrayChildren(),
                                    Lookups.singleton(dataset),
                                    datasetContainer)});

                    }

                } catch (DataObjectExistsException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

            }
        }

        GtaModelReference gtaModelReference;
        if (dobj.getProgectScheme().getModel() != null && dobj.getProgectScheme().getModel().size() > 0) {
            gtaModelReference = dobj.getProgectScheme().getModel().get(0);
        } else {
            gtaModelReference = new GtaModelReference();
        }
        if (gtaModelReference.getPath() != null) {
            try {

                File fl = new File(FileOwnerQuery.getOwner(dobj.getPrimaryFile()).getProjectDirectory().getPath() + File.separator + gtaModelReference.getPath());
                FileObject test = FileUtil.createData(fl);
                DataObject dObj = DataObject.find(test);
                if (dObj != null) {
                    tgmDObj = (TgmDataObject) dObj;
                }
                //fo = new TgmDataObject(FileUtil.toFileObject(fl), null);

            } catch (DataObjectExistsException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            modelContainer = new ModelContainer(tgmDObj);
        }

        //GtaConnection connection;
        if (dobj.getProgectScheme().getConnection() != null) {
            for (GtaConnection connection : dobj.getProgectScheme().getConnection()) {
                if (connection.isActive()) {
                    if (connection.getSourceType() == EnumTypes.ConnectionTypes.GTADATASETCONTAINER.toString()) {
                        if (connection.getTargetType() == EnumTypes.ConnectionTypes.GTAMODELREFERENCE.toString()) {
                        }
                    }
                } else {
                    dobj.getProgectScheme().getConnection().remove(connection);
                }
            }
//                connection = dobj.getProgectScheme().getConnection().get(0);
        } else {
//                connection = new GtaConnection();
        }

//            if (connection.isActive()) {
//               if(connection.getSourceType()==EnumTypes.ConnectionTypes.GTADATASETCONTAINER.toString()) {
//               if(connection.getTargetType()==EnumTypes.ConnectionTypes.GTAMODELREFERENCE.toString()) {
//               }
//               }
//                Object sourceNode = getNodeForID(connection.getSourceID());
//                Object targetNode = getNodeForID(connection.getTargetID());
//               
//                if (findWidget(targetNode) instanceof DatasetContainerWidget) {
//                    ((DatasetContainerWidget) findWidget(targetNode)).setConnected(true);
//                    ((DatasetContainerWidget) findWidget(targetNode)).getContainerComponent().setConnectedModel(
//                            ((ModelContainerWidget) findWidget(sourceNode)).getModelTgm());
//                }
//                if (findWidget(targetNode) instanceof SimulationInputContainerWidget) {
//                    ((SimulationInputContainerWidget) findWidget(targetNode)).setConnected(true);
//                    ((SimulationInputContainerWidget) findWidget(targetNode)).getContainerComponent().setConnectedModel(
//                            ((ModelContainerWidget) findWidget(sourceNode)).getModelTgm());
//                }
        validate();


        //loadScene(dobj.getProgectScheme());
        if(modelContainer!=null) {
        jPanel4.add(modelContainer);
        } else {
            jPanel4.add(new JLabel("Please drag existing or new model here"));
        }
        jPanel4.setDropTarget(new DropTarget());
        jPanel5.add(datasetContainer);
        jPanel3.add(outputPanel);
        validate();

        CompartmentalModellingGraphScene scene = new CompartmentalModellingGraphScene(tgmDObj);
        JComponent myView = scene.createView();
        jScrollPane1.setViewportView(myView);
        jPanel8.add(scene.createSatelliteView(), BorderLayout.WEST);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public DataObject getDataObject() {
        return getLookup().lookup(DataObject.class);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase("modelChanged")) {
            getLookup().lookup(DataObject.class).setModified(true);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(AnalysisEditorTopComponent.class, "AnalysisEditorTopComponent.jPanel4.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP)); // NOI18N
        jPanel4.setLayout(new java.awt.BorderLayout());
        jPanel2.add(jPanel4);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(AnalysisEditorTopComponent.class, "AnalysisEditorTopComponent.jPanel5.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP)); // NOI18N
        jPanel5.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel6.add(jPanel5, gridBagConstraints);

        jPanel2.add(jPanel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 240;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(AnalysisEditorTopComponent.class, "AnalysisEditorTopComponent.jPanel3.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP)); // NOI18N
        jPanel3.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jPanel3, gridBagConstraints);

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jToggleButton1, org.openide.util.NbBundle.getMessage(AnalysisEditorTopComponent.class, "AnalysisEditorTopComponent.jToggleButton1.text")); // NOI18N
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleButton1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jToolBar1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(AnalysisEditorTopComponent.class, "AnalysisEditorTopComponent.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel8.setLayout(new java.awt.BorderLayout());
        jPanel8.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(AnalysisEditorTopComponent.class, "AnalysisEditorTopComponent.jPanel8.TabConstraints.tabTitle"), jPanel8); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        if (jToggleButton1.isSelected()) {
            jPanel5.removeAll();
            jPanel5.add(simulationContainer);
        } else {
            jPanel5.removeAll();
            jPanel5.add(datasetContainer);
        }
        jPanel5.validate();
        validate();
    }//GEN-LAST:event_jToggleButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized AnalysisEditorTopComponent getDefault() {
        if (instance == null) {
            instance = new AnalysisEditorTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the AnalysisEditorTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized AnalysisEditorTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(AnalysisEditorTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof AnalysisEditorTopComponent) {
            return (AnalysisEditorTopComponent) win;
        }
        Logger.getLogger(AnalysisEditorTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public DatasetContainerComponent getContainerComponent() {
        return datasetContainer;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    public FileObject getSchemaPath() {
        FileObject path = null;
        path = getLookup().lookup(DataObject.class).getPrimaryFile();
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
