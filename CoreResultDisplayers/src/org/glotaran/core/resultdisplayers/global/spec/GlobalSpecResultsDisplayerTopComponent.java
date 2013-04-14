/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.resultdisplayers.global.spec;

import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import static java.lang.Math.abs;
import org.glotaran.core.models.results.GtaResult;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.core.resultdisplayers.common.panels.RelationFrom;
import org.glotaran.core.resultdisplayers.common.panels.RelationTo;
import org.glotaran.jfreechartcustom.GlotaranDrawingSupplier;
import org.glotaran.jfreechartcustom.GlotaranStrokeSupplier;
import org.glotaran.jfreechartcustom.GraphPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.windows.CloneableTopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.glotaran.core.resultdisplayers.global.spec//GlobalSpecResultsDisplayer//EN",
autostore = false)
public final class GlobalSpecResultsDisplayerTopComponent extends CloneableTopComponent {

    private static GlobalSpecResultsDisplayerTopComponent instance;
    private final static long serialVersionUID = 1L;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "GlobalSpecResultsDisplayerTopComponent";
    private List<TimpResultDataset> resultDatasets = null;
    private GtaResult gtaResultObj;
    private ArrayList<RelationFrom> relationGroups = new ArrayList<RelationFrom>();

    public GlobalSpecResultsDisplayerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(GlobalSpecResultsDisplayerTopComponent.class, "CTL_GlobalSpecResultsDisplayerTopComponent"));
        setToolTipText(NbBundle.getMessage(GlobalSpecResultsDisplayerTopComponent.class, "HINT_GlobalSpecResultsDisplayerTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

    }

    public GlobalSpecResultsDisplayerTopComponent(List<TimpResultDataset> results, GtaResult gtaResult) {
        initComponents();
        this.resultDatasets = results;
        this.gtaResultObj = gtaResult;
        setName(NbBundle.getMessage(GlobalSpecResultsDisplayerTopComponent.class, "CTL_GlobalSpecResultsDisplayerTopComponent"));
        setToolTipText(NbBundle.getMessage(GlobalSpecResultsDisplayerTopComponent.class, "HINT_GlobalSpecResultsDisplayerTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        ArrayList<String> datasetsID = new ArrayList<String>();
        boolean selected = false;
        if (gtaResultObj != null) {
            for (int i = 0; i < results.size(); i++) {
                for (int j = 0; j < gtaResult.getDatasets().size(); j++) {
                    if (gtaResult.getDatasets().get(j).getResultFile().getFilename().equals(results.get(i).getDatasetName())) {
                        datasetsID.add(gtaResult.getDatasets().get(j).getId());
                    }
                }
            }
            if (gtaResult.getDatasetRelations() != null) {
//"to" is the dataset whish should be scalled
//"from" is the dataset with weight 1
//"values" linear relations - for now it is only one number;

                for (int j = 0; j < datasetsID.size(); j++) {
                    for (int i = 0; i < gtaResult.getDatasetRelations().size(); i++) {
                        if (gtaResult.getDatasetRelations().get(i).getFrom().equals(datasetsID.get(j))) {
                            if (relationGroups.isEmpty()) {
                                relationGroups.add(new RelationFrom(j));
                                for (int k = 0; k < datasetsID.size(); k++) {
                                    if (gtaResult.getDatasetRelations().get(i).getTo().equals(datasetsID.get(k))) {
                                        relationGroups.get(relationGroups.size() - 1).scaledDatasets.add(new RelationTo(k, gtaResult.getDatasetRelations().get(i).getValues().get(0)));
                                    }
                                }
                            } else {
                                for (int irelgr = 0; irelgr < relationGroups.size(); irelgr++) {
                                    if (relationGroups.get(irelgr).indexFrom == j) {
                                        selected = true;
                                        for (int k = 0; k < datasetsID.size(); k++) {
                                            if (gtaResult.getDatasetRelations().get(i).getTo().equals(datasetsID.get(k))) {
                                                relationGroups.get(irelgr).scaledDatasets.add(new RelationTo(k, gtaResult.getDatasetRelations().get(i).getValues().get(0)));
                                            }
                                        }
                                    }
                                }
                                    if (!selected){
                                        relationGroups.add(new RelationFrom(j));
                                        for (int k = 0; k < datasetsID.size(); k++) {
                                            if (gtaResult.getDatasetRelations().get(i).getTo().equals(datasetsID.get(k))) {
                                                relationGroups.get(relationGroups.size() - 1).scaledDatasets.add(new RelationTo(k, gtaResult.getDatasetRelations().get(i).getValues().get(0)));
                                            }
                                        }
                                    }
                                selected = false;
                            }
                        }
                    }
                }
            }
// relationGroups complex list with groups of all relations
            if (!relationGroups.isEmpty()) {
//for every relations group create tab with comparions spectra coming from "from dataset"
                for (int i = 0; i < relationGroups.size(); i++) {
                    jTabbedPane1.addTab("GroupCompare", new MultiTracesPanel(relationGroups.get(i), results, null));
                }
            } 
        }

        if (relationGroups.isEmpty()){
// if there is no relations create 1 tab with traces and spectra normalized to 1. 
                jTabbedPane1.addTab("TraceCompare", new MultiTracesPanel(null,results, null));
        }
        plotSpectrTraces(relationGroups);

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
        jPSpectraTab = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPSASPlot = new javax.swing.JPanel();
        jPDASPlot = new javax.swing.JPanel();
        jPNormSASPlot = new javax.swing.JPanel();
        jPNormDasPlot = new javax.swing.JPanel();
        jPPlotControls = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jTBShowChohSpec = new javax.swing.JToggleButton();
        jTBNormToMax = new javax.swing.JToggleButton();

        setLayout(new java.awt.BorderLayout());

        jPSpectraTab.setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridLayout(2, 2, 2, 2));

        jPSASPlot.setBackground(new java.awt.Color(255, 255, 255));
        jPSASPlot.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPSASPlot);

        jPDASPlot.setBackground(new java.awt.Color(255, 255, 255));
        jPDASPlot.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPDASPlot);

        jPNormSASPlot.setBackground(new java.awt.Color(255, 255, 255));
        jPNormSASPlot.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPNormSASPlot);

        jPNormDasPlot.setBackground(new java.awt.Color(255, 255, 255));
        jPNormDasPlot.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPNormDasPlot);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPSpectraTab.add(jPanel3, gridBagConstraints);

        javax.swing.GroupLayout jPPlotControlsLayout = new javax.swing.GroupLayout(jPPlotControls);
        jPPlotControls.setLayout(jPPlotControlsLayout);
        jPPlotControlsLayout.setHorizontalGroup(
                jPPlotControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 963, Short.MAX_VALUE));
        jPPlotControlsLayout.setVerticalGroup(
                jPPlotControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 100, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPSpectraTab.add(jPPlotControls, gridBagConstraints);

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jTBShowChohSpec, org.openide.util.NbBundle.getMessage(GlobalSpecResultsDisplayerTopComponent.class, "GlobalSpecResultsDisplayerTopComponent.jTBShowChohSpec.text")); // NOI18N
        jTBShowChohSpec.setEnabled(false);
        jTBShowChohSpec.setFocusable(false);
        jTBShowChohSpec.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTBShowChohSpec.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTBShowChohSpec.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBShowChohSpecActionPerformed(evt);
            }
        });
        jToolBar1.add(jTBShowChohSpec);

        org.openide.awt.Mnemonics.setLocalizedText(jTBNormToMax, org.openide.util.NbBundle.getMessage(GlobalSpecResultsDisplayerTopComponent.class, "GlobalSpecResultsDisplayerTopComponent.jTBNormToMax.text")); // NOI18N
        jTBNormToMax.setFocusable(false);
        jTBNormToMax.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTBNormToMax.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTBNormToMax.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBNormToMaxActionPerformed(evt);
            }
        });
        jToolBar1.add(jTBNormToMax);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(-6, 0, 0, 0);
        jPSpectraTab.add(jToolBar1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(GlobalSpecResultsDisplayerTopComponent.class, "GlobalSpecResultsDisplayerTopComponent.jPSpectraTab.TabConstraints.tabTitle"), jPSpectraTab); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jTBShowChohSpecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBShowChohSpecActionPerformed
        plotSpectrTraces(relationGroups);
        jPSASPlot.validate();
        jPDASPlot.validate();
        jPNormDasPlot.validate();
        jPNormSASPlot.validate();
    }//GEN-LAST:event_jTBShowChohSpecActionPerformed

    private void jTBNormToMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBNormToMaxActionPerformed
        plotSpectrTraces(relationGroups);
        jPSASPlot.validate();
        jPDASPlot.validate();
        jPNormDasPlot.validate();
        jPNormSASPlot.validate();
    }//GEN-LAST:event_jTBNormToMaxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPDASPlot;
    private javax.swing.JPanel jPNormDasPlot;
    private javax.swing.JPanel jPNormSASPlot;
    private javax.swing.JPanel jPPlotControls;
    private javax.swing.JPanel jPSASPlot;
    private javax.swing.JPanel jPSpectraTab;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToggleButton jTBNormToMax;
    private javax.swing.JToggleButton jTBShowChohSpec;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized GlobalSpecResultsDisplayerTopComponent getDefault() {
        if (instance == null) {
            instance = new GlobalSpecResultsDisplayerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the GlobalSpecResultsDisplayerTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized GlobalSpecResultsDisplayerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(GlobalSpecResultsDisplayerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof GlobalSpecResultsDisplayerTopComponent) {
            return (GlobalSpecResultsDisplayerTopComponent) win;
        }
        Logger.getLogger(GlobalSpecResultsDisplayerTopComponent.class.getName()).warning(
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
        
    }

    @Override
    public void componentClosed() {
        
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

    private void plotSpectrTraces(ArrayList<RelationFrom> groups) {
        YIntervalSeriesCollection realSasCollection = new YIntervalSeriesCollection();
        YIntervalSeriesCollection normSasCollection = new YIntervalSeriesCollection();
        XYSeriesCollection realDasCollection = new XYSeriesCollection();
        XYSeriesCollection normDasCollection = new XYSeriesCollection();
        String specName = null;
        boolean errorBars = false;
        int numberOfComponents;
        TimpResultDataset fromDataset;
        YIntervalSeries seria;
        XYSeries dasSeria;
        double maxAmpl = 0;
        double maxDasAmpl = 0;

        for (int g = 0; g < resultDatasets.size(); g++){
            fromDataset = resultDatasets.get(g);
            specName = fromDataset.getJvec() != null ? "SAS" : "EAS";
            errorBars = fromDataset.getSpectraErr() != null ? true : false;
            numberOfComponents = fromDataset.getJvec() != null ? fromDataset.getJvec().length / 2 : fromDataset.getKineticParameters().length / 2;
            if (fromDataset.getSpectra().getRowDimension() > numberOfComponents * 2) {
                jTBShowChohSpec.setEnabled(true);
            }
            int compNumFull = jTBShowChohSpec.isEnabled() ? numberOfComponents + 1 : numberOfComponents;
            int compNum = jTBShowChohSpec.isSelected() ? numberOfComponents + 1 : numberOfComponents;
            maxAmpl = 0;
            maxDasAmpl = 0;
            for (int j = 0; j < compNum; j++) {
                seria = new YIntervalSeries(specName + g + "." + (j + 1));// new XYSeries(specName + (j + 1));
                dasSeria = new XYSeries("DAS" + g + "." + (j + 1));
                maxAmpl = 0;
                maxDasAmpl = 0;
                for (int i = 0; i < fromDataset.getX2().length; i++) {
                    if (fromDataset.getSpectraErr() != null) {
                        seria.add(fromDataset.getX2()[i], fromDataset.getSpectra().get(j, i),
                                fromDataset.getSpectra().get(j, i) - fromDataset.getSpectraErr().get(j, i),
                                fromDataset.getSpectra().get(j, i) + fromDataset.getSpectraErr().get(j, i));
                    } else {
                        seria.add(fromDataset.getX2()[i], fromDataset.getSpectra().get(j, i),
                                fromDataset.getSpectra().get(j, i),
                                fromDataset.getSpectra().get(j, i));
                    }
                    dasSeria.add(fromDataset.getX2()[i], fromDataset.getSpectra().get(j + compNumFull, i));
                    if (jTBNormToMax.isSelected()) {
                        if (maxAmpl < (fromDataset.getSpectra().get(j, i))) {
                            maxAmpl = (fromDataset.getSpectra().get(j, i));
                        }
                        if (fromDataset.getSpectra().getRowDimension() > compNum) {
                            if (maxDasAmpl < (fromDataset.getSpectra().get(j + compNumFull, i))) {
                                maxDasAmpl = (fromDataset.getSpectra().get(j + compNumFull, i));
                            }
                        }
                    } else {
                        if (maxAmpl < abs(fromDataset.getSpectra().get(j, i))) {
                            maxAmpl = abs(fromDataset.getSpectra().get(j, i));
                        }
                        if (fromDataset.getSpectra().getRowDimension() > compNum) {
                            if (maxDasAmpl < abs(fromDataset.getSpectra().get(j + compNumFull, i))) {
                                maxDasAmpl = abs(fromDataset.getSpectra().get(j + compNumFull, i));
                            }
                        }
                    }
                }
                realSasCollection.addSeries(seria);
                if (j < numberOfComponents) {
                    realDasCollection.addSeries(dasSeria);
                }

                seria = new YIntervalSeries("Norm" + specName + g + "." + (j + 1));
                dasSeria = new XYSeries("NormDas" + g + "." + (j + 1));
                for (int i = 0; i < fromDataset.getX2().length; i++) {
                    if (fromDataset.getSpectraErr() != null) {
                        seria.add(fromDataset.getX2()[i], fromDataset.getSpectra().get(j, i) / maxAmpl,
                                fromDataset.getSpectra().get(j, i) / maxAmpl - fromDataset.getSpectraErr().get(j, i) / maxAmpl,
                                fromDataset.getSpectra().get(j, i) / maxAmpl + fromDataset.getSpectraErr().get(j, i) / maxAmpl);
                    } else {
                        seria.add(fromDataset.getX2()[i], fromDataset.getSpectra().get(j, i) / maxAmpl,
                                fromDataset.getSpectra().get(j, i) / maxAmpl,
                                fromDataset.getSpectra().get(j, i) / maxAmpl);
                    }

                    dasSeria.add(fromDataset.getX2()[i], fromDataset.getSpectra().get(j + compNumFull, i) / maxDasAmpl);
                }
                normSasCollection.addSeries(seria);
                if (j < numberOfComponents) {
                    normDasCollection.addSeries(dasSeria);
                }
            }
        }
//create collection of real sas and normalizes all of them to max or abs(max) and creates collection with normSAS

        GraphPanel chpan = createSpecChart(realSasCollection, specName, errorBars, true);
        jPSASPlot.removeAll();
        jPSASPlot.add(chpan);

        chpan = createSpecChart(normSasCollection, "norm" + specName, errorBars, true);
        jPNormSASPlot.removeAll();
        jPNormSASPlot.add(chpan);

        chpan = createSpecChart(realDasCollection, "DAS", false, false);
        jPDASPlot.removeAll();
        jPDASPlot.add(chpan);

        chpan = createSpecChart(normDasCollection, "normDAS", false, false);
        jPNormDasPlot.removeAll();
        jPNormDasPlot.add(chpan);

    }
    
    private GraphPanel createSpecChart(XYDataset traceCollection, String name, boolean errorBars, boolean sas){
        GlotaranDrawingSupplier drawSupl = null;
        GlotaranStrokeSupplier strokeSupl = new GlotaranStrokeSupplier();
        Stroke lineStroke;
        JFreeChart tracechart = ChartFactory.createXYLineChart(
                null,
                "Wavelengths",
                name,
                traceCollection,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        XYPlot plot = tracechart.getXYPlot();
        int k = 0;
        for (int i = 0; i < resultDatasets.size(); i++){
            drawSupl = new GlotaranDrawingSupplier();
            TimpResultDataset fromDataset = resultDatasets.get(i);
            int numberOfComponents = fromDataset.getJvec() != null ? fromDataset.getJvec().length / 2 : fromDataset.getKineticParameters().length / 2;    
            if (fromDataset.getSpectra().getRowDimension() > numberOfComponents * 2) {    
                jTBShowChohSpec.setEnabled(true);    
            }    
            if (sas){
                numberOfComponents = jTBShowChohSpec.isSelected() ? numberOfComponents + 1 : numberOfComponents;
            }
            lineStroke = strokeSupl.getNextStroke();
            for (int j = 0; j < numberOfComponents; j++){
                plot.getRenderer().setSeriesPaint(k,drawSupl.getNextPaint());
                plot.getRenderer().setSeriesStroke(k, lineStroke);
                k++;
            }
            
        }
                
        return new GraphPanel(tracechart, errorBars);

        
    }


}
