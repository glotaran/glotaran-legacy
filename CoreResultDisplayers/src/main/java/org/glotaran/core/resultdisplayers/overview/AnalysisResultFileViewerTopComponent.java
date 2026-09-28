/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.resultdisplayers.overview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.glotaran.analysisoverviewfilesupport.AnalysisResultDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;
import org.glotaran.core.models.gta.GtaDataset;
import org.glotaran.core.models.results.Dataset;
import org.glotaran.core.models.results.GtaResult;
import org.glotaran.core.models.results.NlsProgress;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.jfreechartcustom.GraphPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.glotaran.jfreechartcustom.LinLogAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.StandardTickUnitSource;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.windows.CloneableTopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.glotaran.support.analysis.ui//AnalysisResultFileViewer//EN",
        autostore = false)
public final class AnalysisResultFileViewerTopComponent extends CloneableTopComponent {

    private static AnalysisResultFileViewerTopComponent instance;
    /**
     * path to the icon used by the component and its open action
     */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "AnalysisResultFileViewerTopComponent";

    public AnalysisResultFileViewerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(AnalysisResultFileViewerTopComponent.class, "CTL_AnalysisResultFileViewerTopComponent"));
        setToolTipText(NbBundle.getMessage(AnalysisResultFileViewerTopComponent.class, "HINT_AnalysisResultFileViewerTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

    }

    public AnalysisResultFileViewerTopComponent(AnalysisResultDataObject dObj) {
        initComponents();
        setName(NbBundle.getMessage(AnalysisResultFileViewerTopComponent.class, "CTL_AnalysisResultFileViewerTopComponent"));
        setToolTipText(NbBundle.getMessage(AnalysisResultFileViewerTopComponent.class, "HINT_AnalysisResultFileViewerTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        double[][] values;
        TimpResultDataObject timpResObj;
        if (dObj.getAnalysisResult() != null) {
            GtaResult res = dObj.getAnalysisResult();
            if (res != null) {
                values = parseNlsProgress(res);
                if (values != null) {
                    if (values.length > 0) {
                        plotProgression(values);
                        jTextArea1.append("Number of iterations: " + String.valueOf(values[0].length - 1));
                    } else {
                        jPanel1.removeAll();
                        jPanel1.add(new JLabel("This file does not contain any progression data."));
                        jPanel1.add(new JLabel("Please rerun the analysis with more iterations."));
//                        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.PAGE_AXIS));
                        jTextArea1.append("Number of iterations: 0");
                    }
                }
                else {
                    jPanel1.add(new JLabel("No fit progression Data available"));
                }
            } else {
                jPanel1.add(new JLabel("Result could to be parsed."));
            }
            jTextArea2.append("\n# R Call for the TIMP function \"initModel\": \n");
            jTextArea2.append(res.getSummary().getInitModelCall());
            jTextArea2.append("\n# R Call for the TIMP function \"fitModel\": \n");
            jTextArea2.append(res.getSummary().getFitModelCall());

            for (int i = 0; i < res.getDatasets().size(); i++) {
                String summary = null;
                try {
                    FileObject test = getResultsFileObject(res.getDatasets().get(i), dObj);
                    if (test != null) {
                        DataObject dataObj = DataObject.find(test);
                        if (dataObj != null) {
                            timpResObj = (TimpResultDataObject) dataObj;
                            summary = getSummaryOfResults(timpResObj.getTimpResultDataset(), i);
                        }
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                if (summary != null) {
                    jTextArea1.append(summary);
                } else {
                    jTextArea1.append("\n !!! Result file could not be found !!! \n");
                }
                jTextArea1.append("\n");
            }

            if (res.getDatasetRelations() != null) {
                String relationsOut = "";
                for (int i = 0; i < res.getDatasetRelations().size(); i++) {
                    relationsOut = "Dataset " + res.getDatasetRelations().get(i).getFrom()
                            + " is scaled to dataset " + res.getDatasetRelations().get(i).getTo() + " with coefficient(s): ";
                    for (int j = 0; j < res.getDatasetRelations().get(i).getValues().size(); j++) {
                        relationsOut += String.valueOf(res.getDatasetRelations().get(i).getValues().get(j)) + " ";
                    }
                    jTextArea1.append(relationsOut);
                    jTextArea1.append("\n");
                }
            }

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(80, 100));
        jPanel1.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.5;
        add(jPanel1, gridBagConstraints);

        jPanel3.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jTextArea2.setColumns(10);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setMinimumSize(new java.awt.Dimension(0, 0));
        jTextArea2.setPreferredSize(new java.awt.Dimension(200, 100));
        jScrollPane2.setViewportView(jTextArea2);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        add(jPanel3, gridBagConstraints);

        jPanel2.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jTextArea1.setColumns(10);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(50);
        jTextArea1.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPane1.setViewportView(jTextArea1);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.5;
        add(jPanel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link #findInstance}.
     */
    public static synchronized AnalysisResultFileViewerTopComponent getDefault() {
        if (instance == null) {
            instance = new AnalysisResultFileViewerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the AnalysisResultFileViewerTopComponent instance. Never call
     * {@link #getDefault} directly!
     */
    public static synchronized AnalysisResultFileViewerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(AnalysisResultFileViewerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof AnalysisResultFileViewerTopComponent) {
            return (AnalysisResultFileViewerTopComponent) win;
        }
        Logger.getLogger(AnalysisResultFileViewerTopComponent.class.getName()).warning(
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

    private double[][] parseNlsProgress(GtaResult res) {
        ArrayList<ArrayList<Double>> allValues;
        allValues = new ArrayList<>();
        double[][] rssValues;
        if (res.getNlsprogress() != null) {
            if (!res.getNlsprogress().isEmpty()) {
                for (Iterator<NlsProgress> it = res.getNlsprogress().iterator(); it.hasNext();) {
                    ArrayList<Double> values = new ArrayList<Double>();
                    NlsProgress nlsProgress = it.next();
                    String test = nlsProgress.getRss();
                    test = test.replaceAll("[\\(\\)\\:\\=par]", " ");
                    Scanner tokenize = new Scanner(test);
                    while (tokenize.hasNext()) {
                        try {
                            values.add(Double.parseDouble(tokenize.next()));
                        } catch (NumberFormatException ex) {
                            // tokenize.next();
                            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.INFO, null, ex); //NOI18N
                        }
                    }
                    allValues.add(values);
                }
                int numberOfIterations = allValues.size();
                int numberOfNlsProgressValues = allValues.get(0).size();

                rssValues = new double[numberOfNlsProgressValues][numberOfIterations];
                for (int j = 0; j < numberOfIterations; j++) {
                    for (int i = 0; i < numberOfNlsProgressValues; i++) {
                        rssValues[i][j] = allValues.get(j).get(i);
                    }
                }
                return rssValues;
            }
            return null;
        } else {
            return null;
        }
    }

    private void plotProgression(double[][] points) {
        //creare collection with singular values
        XYSeriesCollection colSV = new XYSeriesCollection();
        XYSeries seria = new XYSeries("rss");
        for (int i = 0; i < points[0].length; i++) {
            seria.add(i, Math.abs(points[0][i]));
        }
        colSV.addSeries(seria);

        JFreeChart rssChart = ChartFactory.createXYLineChart(
                "Residual Sum of Squares progression",
                "Number of iterations",
                "Log(RSS)",
                colSV,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        XYPlot plot = rssChart.getXYPlot();
        NumberAxis domainAxis = new NumberAxis("Iteration");
        LogAxis rangeAxis = new LogAxis("Log(RSS)");
        rangeAxis.setRange(colSV.getSeries(0).getMinY(), colSV.getSeries(0).getMaxY());
        rangeAxis.configure();
        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(rangeAxis);
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) rssChart.getXYPlot().getRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setBaseFillPaint(Color.white);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesOutlineStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));

        rssChart.getTitle().setFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getFontName(), JFreeChart.DEFAULT_TITLE_FONT.getStyle(), 12));
        rssChart.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);

        GraphPanel chartPanel = new GraphPanel(rssChart, false);
        jPanel1.removeAll();
        jPanel1.add(chartPanel);
    }

    private String getSummaryOfResults(TimpResultDataset timpResultDataset, int index) {
        StringBuilder outputString = new StringBuilder();
        if (timpResultDataset != null) {
            if (index == 0) {
                outputString.append("\nFinal residual standard error: ");
                outputString.append((new Formatter().format("%g", timpResultDataset.getRms())).toString());
            }
            outputString.append("\n\n");
            String tmpString = "### Results for " + timpResultDataset.getDatasetName() + " ###";
            outputString.append(tmpString);

            String[] slots = {"getKineticParameters", "getSpectralParameters", "getIrfpar", "getCoh", "getOscpar", "getSpecdisppar", "getParmu", "getPartau", "getKinscal", "getPrel", "getJvec"};
            String[] slotsName = {"Kinetic parameters", "Spectral parameters", "Irf parameters", "Cohspec parameters", "Oscspec parameters", "Specdisppar", "Parmu", "Partau", "Kinscal", "Prel", "J vector"};
            double[] params = null;

            for (int k = 0; k < slots.length; k++) {
                try {
                    try {
                        //params = (double[]) results[i].getClass().getMethod(slots[k], new Class[]{results[i].getClass().getClass()}).invoke(results[i], new Object[]{results});
                        params = (double[]) timpResultDataset.getClass().getMethod(slots[k], null).invoke(timpResultDataset, null);
                        if (params != null) {

                            outputString.append("\n\n Estimated " + slotsName[k] + ": ");

                            for (int j = 0; j < params.length / 2; j++) {
                                if (j > 0) {
                                    outputString.append(", ");
                                }
                                outputString.append((new Formatter().format("%g", params[j])).toString());
                            }

                            outputString.append("\n Standard errors: ");
                            for (int j = 0; j < params.length / 2; j++) {
                                if (j > 0) {
                                    outputString.append(", ");
                                }
                                outputString.append((new Formatter().format("%g",
                                        params[j + params.length / 2])).toString());
                            }

                        }

                    } catch (IllegalAccessException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalArgumentException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } catch (NoSuchMethodException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (SecurityException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            outputString.append("\n\n");
            for (int i = 0; i < tmpString.length(); i++) {
                outputString.append("#");
            }
            outputString.append("\n\n");
        } else {
            outputString.append("Error: The analysis did not return valid results.");
            outputString.append("Try again with different parameters.");
        }
        return outputString.toString();

    }

    private FileObject getResultsFileObject(Dataset resDataset, AnalysisResultDataObject dObj) {
        FileObject resultsFileObject = null;
        String projectFolder = FileOwnerQuery.getOwner(dObj.getPrimaryFile()).getProjectDirectory().getPath();
        String initialResultsFolder = resDataset.getResultFile().getPath();
        String currentResultsFolder = dObj.getPrimaryFile().getParent().getPath();
        String initialResultsFile = resDataset.getResultFile().getFilename();

        FileObject test = FileUtil.toFileObject(new File(projectFolder + File.separator + initialResultsFolder));
        if (test == null) {
            test = FileUtil.toFileObject(new File(currentResultsFolder));
        }
        resultsFileObject = test.getFileObject(initialResultsFile, "timpres");
        return resultsFileObject;
    }
}
