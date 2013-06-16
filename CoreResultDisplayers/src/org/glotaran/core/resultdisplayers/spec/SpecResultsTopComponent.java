package org.glotaran.core.resultdisplayers.spec;

import javax.swing.JFileChooser;
import org.jfree.ui.ExtensionFileFilter;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;
import org.glotaran.jfreechartcustom.NonLinearNumberTickUnit;
import org.ujmp.jama.JamaDenseDoubleMatrix2D;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import javax.swing.SwingWorker;
import org.ujmp.core.Matrix;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.core.resultdisplayers.common.panels.CommonResDispTools;
import org.glotaran.core.resultdisplayers.common.panels.SelectTracesForPlot;
import org.glotaran.jfreechartcustom.ColorCodedImageDataset;
import org.glotaran.jfreechartcustom.GraphPanel;
import org.glotaran.jfreechartcustom.HeightMapPanel;
import org.glotaran.jfreechartcustom.ImageCrosshairLabelGenerator;
import org.glotaran.jfreechartcustom.ImageUtilities;
import org.glotaran.jfreechartcustom.RainbowPaintScale;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDataImageAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.ujmp.core.calculation.Calculation.Ret;
import static java.lang.Math.abs;

/**
 * Top component which displays something.
 */
public final class SpecResultsTopComponent extends TopComponent implements ChartChangeListener, ChartMouseListener {

    private static SpecResultsTopComponent instance;
    private final static long serialVersionUID = 1L;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "StreakOutTopComponent";
    private TimpResultDataset res;
    private ChartPanel chpanImage;
    ArrayList<Integer> selectedTimeTraces = new ArrayList<Integer>();
    ArrayList<Integer> selectedWaveTraces = new ArrayList<Integer>();
    private XYSeriesCollection selectedTimeTracesColection = new XYSeriesCollection();
    private XYSeriesCollection selectedTimeResidualsColection = new XYSeriesCollection();
    private XYSeriesCollection selectedWaveTracesColection = new XYSeriesCollection();
    private XYSeriesCollection selectedWaveResidualsColection = new XYSeriesCollection();;
    private Range lastXRange;
    private Range lastYRange;
    private Range wholeXRange;
    private Range wholeYRange;
    private Crosshair crosshair1;
    private Crosshair crosshair2;
    private JFreeChart chartMain;
    private ColorCodedImageDataset dataset;
    private int numberOfComponents;
    private Matrix leftSingVec;
    private Matrix leftSingVecPart;
    private double[] timePart;
    private double[] t0Curve;
    private TimpResultDataObject dataObject;
    private double linPart;
    private Matrix[] svdResult;
    private Matrix residualMatrix, spectraMatrix, concentrationsMatrix;
    private final long MAX_NUMBER_SINGULAR_VALUES = 10;
    private int MAX_NO_TICKS = 6;

    public SpecResultsTopComponent(TimpResultDataObject dataObj) {
        initComponents();
        setToolTipText(NbBundle.getMessage(SpecResultsTopComponent.class, "HINT_StreakOutTopComponent"));
        setName(dataObj.getName());
        this.dataObject = dataObj;
        res = dataObject.getTimpResultDataset();
        res.calcRangeInt();
        residualMatrix = new JamaDenseDoubleMatrix2D((Jama.Matrix) res.getResiduals());
        spectraMatrix = new JamaDenseDoubleMatrix2D((Jama.Matrix) res.getSpectra());
        concentrationsMatrix = new JamaDenseDoubleMatrix2D((Jama.Matrix) res.getConcentrations());
        if (res.getJvec() != null) {
            numberOfComponents = res.getJvec().length / 2;
        } else {
            numberOfComponents = res.getKineticParameters().length / 2;
        }

        ArrayList<String> paramsList = new ArrayList<String>(numberOfComponents);

        for (int i = 0; i < (res.getKineticParameters().length / 2); i++) {
            //TODO: add errors
            paramsList.add("k" + (i + 1) + "="
                    + new Formatter().format("%2.6g",
                    res.getKineticParameters()[i]) //TODO: (un)comment to show/hide errors
                    //+ " (" + new Formatter().format("%2.4g",res.getKineticParameters()[i+1]) + ")"
                    );
            //,res.getKineticParameters()[numberOfComponents + i])
        }

        if (res.getIrfpar() != null) {
            int irfParLength = res.getIrfpar().length / 2;
            for (int i = 0; i < irfParLength; i++) {
                paramsList.add("irf" + (i + 1) + "="
                        + new Formatter().format("%2.6g", res.getIrfpar()[i]) //TODO: (un)comment to show/hide errors
                        //+ " (" + new Formatter().format("%2.4g", res.getIrfpar()[i+1]) +")"
                        );
            }
        }
        paramsList.add("");
        paramsList.add("-- Lifetimes [~s] --");
        double[] eigenValues;
        boolean useKMatrix = false;
        if (res.getEigenvaluesK() != null) {
            eigenValues = res.getEigenvaluesK();
            useKMatrix = true;
        } else {
            eigenValues = res.getKineticParameters();
        }
        DecimalFormat lifeTimesFormat = new DecimalFormat("##0.0##E0");
        String lifeTime;
        for (int i = 0; i < numberOfComponents; i++) {
            if (useKMatrix) {
                lifeTime = lifeTimesFormat.format(1 / -eigenValues[i]);
            } else {
                lifeTime = lifeTimesFormat.format(1 / eigenValues[i]);
            }
            if (lifeTime.contains("E0")) {
                lifeTime = lifeTime.replace("E0", "");
            }
            paramsList.add("tau" + (i + 1) + "=" + lifeTime);
        }
        paramsList.add("-----");
        paramsList.add("RMS =" + (new Formatter().format("%g", res.getRms())).toString());
        jLKineticParameters.setListData(paramsList.toArray());
        jLKineticParameters.setVisibleRowCount(paramsList.size());
        jLKineticParameters.setPreferredSize(jLKineticParameters.getPreferredScrollableViewportSize());
        jLKineticParameters.revalidate();

//first tab
        t0Curve = CommonResDispTools.calculateDispersionTrace(res);
        timePart = res.getX();
        if (spectraMatrix.getRowCount() > numberOfComponents * 2) {
            jTBShowChohSpec.setEnabled(true);
        }
        plotSpectrTrace();
        GraphPanel conc;
        if (numberOfComponents < res.getConcentrations().getColumnDimension()) {
            conc = createLinTimePlot(concentrationsMatrix, res.getX(), true); 
        } else {
            conc = createLinTimePlot(concentrationsMatrix, res.getX());
        }

        jPConcentrations.removeAll();
        jPConcentrations.add(conc);

        SwingWorker<Matrix, Void> worker = new SwingWorker<Matrix, Void>() {

            final ProgressHandle ph = ProgressHandleFactory.createHandle("Performing Singular Value Decomposition on dataset");

            @Override
            protected Matrix doInBackground() throws Exception {
                ph.start();
                Matrix residualMatrix = new JamaDenseDoubleMatrix2D((Jama.Matrix) res.getResiduals());
                svdResult = calculateSVD(residualMatrix);
                leftSingVec = svdResult[0];
                leftSingVecPart = svdResult[0];
                updateSVDPlots(svdResult, res.getX(), res.getX2(), jPRightSingVectors, jPLeftSingVectors, jPSingValues);
                updateSVDPlots(svdResult, res.getX(), res.getX2(), jPRightSingVectorsPart, jPLeftSingVectorsPart, jPSingValuesPart);
                return leftSingVec;
            }

            @Override
            protected void done() {
                makeImageChart();
                jSColum.setMaximum(dataset.GetImageWidth() - 1);
                jSColum.setMinimum(0);
                jSColum.setValue(0);
                jSRow.setMaximum(dataset.GetImageHeigth() - 1);
                jSRow.setMinimum(0);
                jSRow.setValue(0);

                jTFCentrWave.setText(String.valueOf(res.getLamdac()));
                if (res.getParmu() != null) {
                    String parmuStr = "";
                    for (int i = 0; i < res.getParmu().length / 2; i++) {
                        if (i > 0) {
                            parmuStr = parmuStr + ",";
                        }
                        parmuStr += new Formatter().format("%6.1g", res.getParmu()[i]);
                    }
                    jTFCurvParam.setText(parmuStr);
                }
                jPanel8.validate();
                ph.finish();
            }
        };
        worker.execute();


//=====================second tab (can be done in BG)
//calculate dispersion curve


    }

    private SpecResultsTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(SpecResultsTopComponent.class, "HINT_StreakOutTopComponent"));
        setToolTipText(NbBundle.getMessage(SpecResultsTopComponent.class, "HINT_StreakOutTopComponent"));
    }

    public TimpResultDataObject getDataObject() {
        return dataObject;
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
        jPOverviewTab = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jTBLinLog = new javax.swing.JToggleButton();
        jTFLinPart = new javax.swing.JTextField();
        jBUpdLinLog = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jTBShowChohSpec = new javax.swing.JToggleButton();
        jTBNormToMax = new javax.swing.JToggleButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPSingValues = new javax.swing.JPanel();
        jPLeftSingVectors = new javax.swing.JPanel();
        jPRightSingVectors = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLKineticParameters = new javax.swing.JList();
        jPConcentrations = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jPSAS = new javax.swing.JPanel();
        jPDAS = new javax.swing.JPanel();
        jPSASnorm = new javax.swing.JPanel();
        jPDASnorm = new javax.swing.JPanel();
        jPTracesTab = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jTBLinLogTraces = new javax.swing.JToggleButton();
        jTFLinPartTraces = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jBAutoSelectTraces = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton8 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jPanel3 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPSpecImage = new javax.swing.JPanel();
        jSColum = new javax.swing.JSlider();
        jSRow = new javax.swing.JSlider();
        jPanel10 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jCBDispCurveShow = new javax.swing.JCheckBox();
        jTFCentrWave = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFCurvParam = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPSelectedWaveTrace = new javax.swing.JPanel();
        jPSelectedTimeTrace = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jPSingValuesPart = new javax.swing.JPanel();
        jPLeftSingVectorsPart = new javax.swing.JPanel();
        jPRightSingVectorsPart = new javax.swing.JPanel();
        jPSelectedTimTracesTab = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jBClearAllTimeTraces = new javax.swing.JButton();
        jBExportTimeTraces = new javax.swing.JButton();
        jTBOverlayTimeTracess = new javax.swing.JToggleButton();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPSelTimeTrCollection = new javax.swing.JPanel();
        jPSelectedWavTracesTab = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        jBClearAllWavelengthTraces = new javax.swing.JButton();
        jBExportWaveTraces = new javax.swing.JButton();
        jTBOverlayWaveTracess = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jPSelWavTrCollection = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(1000, 1200));
        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(10, 10));
        jTabbedPane1.setOpaque(true);

        jPOverviewTab.setLayout(new java.awt.GridBagLayout());

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jTBLinLog, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTBLinLog.text")); // NOI18N
        jTBLinLog.setFocusable(false);
        jTBLinLog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTBLinLog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTBLinLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBLinLogActionPerformed(evt);
            }
        });
        jToolBar1.add(jTBLinLog);

        jTFLinPart.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFLinPart.setText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTFLinPart.text")); // NOI18N
        jTFLinPart.setMaximumSize(new java.awt.Dimension(70, 19));
        jTFLinPart.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFLinPartFocusLost(evt);
            }
        });
        jToolBar1.add(jTFLinPart);

        org.openide.awt.Mnemonics.setLocalizedText(jBUpdLinLog, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBUpdLinLog.text")); // NOI18N
        jBUpdLinLog.setEnabled(false);
        jBUpdLinLog.setFocusable(false);
        jBUpdLinLog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBUpdLinLog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBUpdLinLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBUpdLinLogActionPerformed(evt);
            }
        });
        jToolBar1.add(jBUpdLinLog);
        jToolBar1.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(jTBShowChohSpec, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTBShowChohSpec.text")); // NOI18N
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

        org.openide.awt.Mnemonics.setLocalizedText(jTBNormToMax, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTBNormToMax.text")); // NOI18N
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
        gridBagConstraints.ipady = -6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPOverviewTab.add(jToolBar1, gridBagConstraints);

        jPanel6.setMinimumSize(new java.awt.Dimension(50, 50));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jPanel8.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 1, 14))); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jPSingValues.setBackground(new java.awt.Color(255, 255, 255));
        jPSingValues.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.26;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(jPSingValues, gridBagConstraints);

        jPLeftSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPLeftSingVectors.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.37;
        jPanel8.add(jPLeftSingVectors, gridBagConstraints);

        jPRightSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPRightSingVectors.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.37;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(jPRightSingVectors, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        jPanel6.add(jPanel8, gridBagConstraints);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        jPanel5.setPreferredSize(new java.awt.Dimension(100, 50));
        jPanel5.setLayout(new java.awt.GridLayout(2, 1));

        jPanel7.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setAutoscrolls(true);

        jLKineticParameters.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jLKineticParameters.border.title"))); // NOI18N
        jLKineticParameters.setPreferredSize(new java.awt.Dimension(100, 50));
        jScrollPane1.setViewportView(jLKineticParameters);

        jPanel7.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel7);

        jPConcentrations.setBackground(new java.awt.Color(255, 255, 255));
        jPConcentrations.setLayout(new java.awt.BorderLayout());
        jPanel5.add(jPConcentrations);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.weighty = 1.0;
        jPanel9.add(jPanel5, gridBagConstraints);

        jPanel19.setPreferredSize(new java.awt.Dimension(250, 150));
        jPanel19.setLayout(new java.awt.GridLayout(2, 2, 2, 2));

        jPSAS.setBackground(new java.awt.Color(255, 255, 255));
        jPSAS.setLayout(new java.awt.BorderLayout());
        jPanel19.add(jPSAS);

        jPDAS.setBackground(new java.awt.Color(255, 255, 255));
        jPDAS.setLayout(new java.awt.BorderLayout());
        jPanel19.add(jPDAS);

        jPSASnorm.setBackground(new java.awt.Color(255, 255, 255));
        jPSASnorm.setLayout(new java.awt.BorderLayout());
        jPanel19.add(jPSASnorm);

        jPDASnorm.setBackground(new java.awt.Color(255, 255, 255));
        jPDASnorm.setLayout(new java.awt.BorderLayout());
        jPanel19.add(jPDASnorm);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.weighty = 1.0;
        jPanel9.add(jPanel19, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        jPanel6.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPOverviewTab.add(jPanel6, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jPOverviewTab.TabConstraints.tabTitle"), jPOverviewTab); // NOI18N

        jPTracesTab.setLayout(new java.awt.GridBagLayout());

        jToolBar2.setRollover(true);
        jToolBar2.setMaximumSize(new java.awt.Dimension(150, 33));
        jToolBar2.setMinimumSize(new java.awt.Dimension(150, 33));
        jToolBar2.setPreferredSize(new java.awt.Dimension(150, 33));

        org.openide.awt.Mnemonics.setLocalizedText(jTBLinLogTraces, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTBLinLogTraces.text")); // NOI18N
        jTBLinLogTraces.setFocusable(false);
        jTBLinLogTraces.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTBLinLogTraces.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTBLinLogTraces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBLinLogTracesActionPerformed(evt);
            }
        });
        jToolBar2.add(jTBLinLogTraces);

        jTFLinPartTraces.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFLinPartTraces.setText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTFLinPartTraces.text")); // NOI18N
        jTFLinPartTraces.setMaximumSize(new java.awt.Dimension(70, 25));
        jTFLinPartTraces.setMinimumSize(new java.awt.Dimension(25, 25));
        jTFLinPartTraces.setPreferredSize(new java.awt.Dimension(50, 25));
        jTFLinPartTraces.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFLinPartTracesFocusLost(evt);
            }
        });
        jToolBar2.add(jTFLinPartTraces);
        jToolBar2.add(jSeparator3);

        jBAutoSelectTraces.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/glotaran/core/resultdisplayers/resources/AutoselectCurves24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jBAutoSelectTraces, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBAutoSelectTraces.text")); // NOI18N
        jBAutoSelectTraces.setToolTipText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBAutoSelectTraces.toolTipText")); // NOI18N
        jBAutoSelectTraces.setFocusable(false);
        jBAutoSelectTraces.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBAutoSelectTraces.setMaximumSize(new java.awt.Dimension(33, 29));
        jBAutoSelectTraces.setMinimumSize(new java.awt.Dimension(33, 29));
        jBAutoSelectTraces.setPreferredSize(new java.awt.Dimension(33, 29));
        jBAutoSelectTraces.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBAutoSelectTraces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAutoSelectTracesActionPerformed(evt);
            }
        });
        jToolBar2.add(jBAutoSelectTraces);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/glotaran/core/resultdisplayers/resources/addTimeTrace24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jButton2.text")); // NOI18N
        jButton2.setToolTipText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jButton2.toolTipText")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMaximumSize(new java.awt.Dimension(33, 29));
        jButton2.setMinimumSize(new java.awt.Dimension(33, 29));
        jButton2.setPreferredSize(new java.awt.Dimension(33, 29));
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/glotaran/core/resultdisplayers/resources/addWaveTrace24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jButton3.text")); // NOI18N
        jButton3.setToolTipText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jButton3.toolTipText")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setMaximumSize(new java.awt.Dimension(33, 29));
        jButton3.setMinimumSize(new java.awt.Dimension(33, 29));
        jButton3.setPreferredSize(new java.awt.Dimension(33, 29));
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton3);
        jToolBar2.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(jButton8, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jButton8.text")); // NOI18N
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton8);
        jToolBar2.add(jSeparator4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPTracesTab.add(jToolBar2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel21.setPreferredSize(new java.awt.Dimension(10, 1));
        jPanel21.setLayout(new java.awt.GridBagLayout());

        jPanel20.setLayout(new java.awt.GridBagLayout());

        jPanel4.setPreferredSize(new java.awt.Dimension(200, 150));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPSpecImage.setBackground(new java.awt.Color(0, 0, 0));
        jPSpecImage.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jPSpecImage, gridBagConstraints);

        jSColum.setValue(0);
        jSColum.setPreferredSize(new java.awt.Dimension(36, 16));
        jSColum.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSColumStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jSColum, gridBagConstraints);

        jSRow.setOrientation(javax.swing.JSlider.VERTICAL);
        jSRow.setValue(0);
        jSRow.setInverted(true);
        jSRow.setPreferredSize(new java.awt.Dimension(36, 16));
        jSRow.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSRowStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jSRow, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel20.add(jPanel4, gridBagConstraints);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jPanel10.border.title"))); // NOI18N
        jPanel10.setPreferredSize(new java.awt.Dimension(200, 94));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jLabel2.text")); // NOI18N

        jCBDispCurveShow.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCBDispCurveShow, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jCBDispCurveShow.text")); // NOI18N
        jCBDispCurveShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBDispCurveShowActionPerformed(evt);
            }
        });

        jTFCentrWave.setEditable(false);
        jTFCentrWave.setText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTFCentrWave.text")); // NOI18N
        jTFCentrWave.setPreferredSize(new java.awt.Dimension(60, 19));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jLabel3.text")); // NOI18N

        jTFCurvParam.setEditable(false);
        jTFCurvParam.setText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTFCurvParam.text")); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBDispCurveShow)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTFCentrWave, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTFCurvParam, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jCBDispCurveShow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFCentrWave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFCurvParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel20.add(jPanel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.45;
        gridBagConstraints.weighty = 1.0;
        jPanel21.add(jPanel20, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jPanel1.border.title"))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPSelectedWaveTrace.setBackground(new java.awt.Color(255, 255, 255));
        jPSelectedWaveTrace.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        jPanel1.add(jPSelectedWaveTrace, gridBagConstraints);

        jPSelectedTimeTrace.setBackground(new java.awt.Color(255, 255, 255));
        jPSelectedTimeTrace.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        jPanel1.add(jPSelectedTimeTrace, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.55;
        gridBagConstraints.weighty = 1.0;
        jPanel21.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        jPanel3.add(jPanel21, gridBagConstraints);

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jPanel17.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 1, 14))); // NOI18N
        jPanel17.setLayout(new java.awt.GridBagLayout());

        jPSingValuesPart.setBackground(new java.awt.Color(255, 255, 255));
        jPSingValuesPart.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.26;
        gridBagConstraints.weighty = 1.0;
        jPanel17.add(jPSingValuesPart, gridBagConstraints);

        jPLeftSingVectorsPart.setBackground(new java.awt.Color(255, 255, 255));
        jPLeftSingVectorsPart.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.37;
        gridBagConstraints.weighty = 1.0;
        jPanel17.add(jPLeftSingVectorsPart, gridBagConstraints);

        jPRightSingVectorsPart.setBackground(new java.awt.Color(255, 255, 255));
        jPRightSingVectorsPart.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.37;
        gridBagConstraints.weighty = 1.0;
        jPanel17.add(jPRightSingVectorsPart, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        jPanel3.add(jPanel17, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPTracesTab.add(jPanel3, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jPTracesTab.TabConstraints.tabTitle"), jPTracesTab); // NOI18N

        jPSelectedTimTracesTab.setPreferredSize(new java.awt.Dimension(950, 1600));
        jPSelectedTimTracesTab.setLayout(new java.awt.GridBagLayout());

        jToolBar3.setRollover(true);
        jToolBar3.setMaximumSize(new java.awt.Dimension(150, 33));
        jToolBar3.setMinimumSize(new java.awt.Dimension(150, 33));
        jToolBar3.setPreferredSize(new java.awt.Dimension(150, 33));

        jBClearAllTimeTraces.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/glotaran/core/resultdisplayers/resources/curvesdelete24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jBClearAllTimeTraces, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBClearAllTimeTraces.text")); // NOI18N
        jBClearAllTimeTraces.setToolTipText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBClearAllTimeTraces.toolTipText")); // NOI18N
        jBClearAllTimeTraces.setFocusable(false);
        jBClearAllTimeTraces.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBClearAllTimeTraces.setIconTextGap(2);
        jBClearAllTimeTraces.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jBClearAllTimeTraces.setMaximumSize(new java.awt.Dimension(33, 29));
        jBClearAllTimeTraces.setMinimumSize(new java.awt.Dimension(33, 29));
        jBClearAllTimeTraces.setPreferredSize(new java.awt.Dimension(33, 29));
        jBClearAllTimeTraces.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBClearAllTimeTraces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBClearAllTimeTracesActionPerformed(evt);
            }
        });
        jToolBar3.add(jBClearAllTimeTraces);

        jBExportTimeTraces.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/glotaran/core/resultdisplayers/resources/exportCurves24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jBExportTimeTraces, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBExportTimeTraces.text")); // NOI18N
        jBExportTimeTraces.setToolTipText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBExportTimeTraces.toolTipText")); // NOI18N
        jBExportTimeTraces.setFocusable(false);
        jBExportTimeTraces.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBExportTimeTraces.setIconTextGap(2);
        jBExportTimeTraces.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jBExportTimeTraces.setMaximumSize(new java.awt.Dimension(33, 29));
        jBExportTimeTraces.setMinimumSize(new java.awt.Dimension(33, 29));
        jBExportTimeTraces.setPreferredSize(new java.awt.Dimension(33, 29));
        jBExportTimeTraces.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBExportTimeTraces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBExportTimeTracesActionPerformed(evt);
            }
        });
        jToolBar3.add(jBExportTimeTraces);
        jBExportTimeTraces.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBExportTimeTraces.AccessibleContext.accessibleDescription")); // NOI18N

        jTBOverlayTimeTracess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/glotaran/core/resultdisplayers/resources/overlay24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jTBOverlayTimeTracess, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTBOverlayTimeTracess.text")); // NOI18N
        jTBOverlayTimeTracess.setToolTipText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTBOverlayTimeTracess.toolTipText")); // NOI18N
        jTBOverlayTimeTracess.setFocusable(false);
        jTBOverlayTimeTracess.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTBOverlayTimeTracess.setIconTextGap(2);
        jTBOverlayTimeTracess.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jTBOverlayTimeTracess.setMaximumSize(new java.awt.Dimension(33, 29));
        jTBOverlayTimeTracess.setMinimumSize(new java.awt.Dimension(33, 29));
        jTBOverlayTimeTracess.setPreferredSize(new java.awt.Dimension(33, 29));
        jTBOverlayTimeTracess.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTBOverlayTimeTracess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBOverlayTimeTracessActionPerformed(evt);
            }
        });
        jToolBar3.add(jTBOverlayTimeTracess);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPSelectedTimTracesTab.add(jToolBar3, gridBagConstraints);

        jPanel18.setPreferredSize(new java.awt.Dimension(800, 1600));
        jPanel18.setLayout(new java.awt.GridBagLayout());

        jScrollPane5.setBorder(null);
        jScrollPane5.setPreferredSize(new java.awt.Dimension(800, 530));

        jPSelTimeTrCollection.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), null));
        jPSelTimeTrCollection.setMinimumSize(new java.awt.Dimension(20, 20));
        jPSelTimeTrCollection.setPreferredSize(new java.awt.Dimension(50, 50));
        jPSelTimeTrCollection.setLayout(new java.awt.GridLayout(2, 2, 2, 2));
        jScrollPane5.setViewportView(jPSelTimeTrCollection);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel18.add(jScrollPane5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPSelectedTimTracesTab.add(jPanel18, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jPSelectedTimTracesTab.TabConstraints.tabTitle"), jPSelectedTimTracesTab); // NOI18N

        jPSelectedWavTracesTab.setLayout(new java.awt.GridBagLayout());

        jToolBar4.setRollover(true);
        jToolBar4.setMaximumSize(new java.awt.Dimension(150, 33));
        jToolBar4.setMinimumSize(new java.awt.Dimension(150, 33));
        jToolBar4.setPreferredSize(new java.awt.Dimension(150, 33));

        jBClearAllWavelengthTraces.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/glotaran/core/resultdisplayers/resources/curvesdelete24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jBClearAllWavelengthTraces, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBClearAllWavelengthTraces.text")); // NOI18N
        jBClearAllWavelengthTraces.setToolTipText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBClearAllWavelengthTraces.toolTipText")); // NOI18N
        jBClearAllWavelengthTraces.setFocusable(false);
        jBClearAllWavelengthTraces.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBClearAllWavelengthTraces.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jBClearAllWavelengthTraces.setMaximumSize(new java.awt.Dimension(33, 29));
        jBClearAllWavelengthTraces.setMinimumSize(new java.awt.Dimension(33, 29));
        jBClearAllWavelengthTraces.setPreferredSize(new java.awt.Dimension(33, 29));
        jBClearAllWavelengthTraces.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBClearAllWavelengthTraces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBClearAllWavelengthTracesActionPerformed(evt);
            }
        });
        jToolBar4.add(jBClearAllWavelengthTraces);

        jBExportWaveTraces.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/glotaran/core/resultdisplayers/resources/exportCurves24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jBExportWaveTraces, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBExportWaveTraces.text")); // NOI18N
        jBExportWaveTraces.setToolTipText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jBExportWaveTraces.toolTipText")); // NOI18N
        jBExportWaveTraces.setFocusable(false);
        jBExportWaveTraces.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBExportWaveTraces.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jBExportWaveTraces.setMaximumSize(new java.awt.Dimension(33, 29));
        jBExportWaveTraces.setMinimumSize(new java.awt.Dimension(33, 29));
        jBExportWaveTraces.setPreferredSize(new java.awt.Dimension(33, 29));
        jBExportWaveTraces.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBExportWaveTraces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBExportWaveTracesActionPerformed(evt);
            }
        });
        jToolBar4.add(jBExportWaveTraces);

        jTBOverlayWaveTracess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/glotaran/core/resultdisplayers/resources/overlay24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jTBOverlayWaveTracess, org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTBOverlayWaveTracess.text")); // NOI18N
        jTBOverlayWaveTracess.setToolTipText(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jTBOverlayWaveTracess.toolTipText")); // NOI18N
        jTBOverlayWaveTracess.setFocusable(false);
        jTBOverlayWaveTracess.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTBOverlayWaveTracess.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jTBOverlayWaveTracess.setMaximumSize(new java.awt.Dimension(33, 29));
        jTBOverlayWaveTracess.setMinimumSize(new java.awt.Dimension(33, 29));
        jTBOverlayWaveTracess.setPreferredSize(new java.awt.Dimension(33, 29));
        jTBOverlayWaveTracess.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTBOverlayWaveTracess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBOverlayWaveTracessActionPerformed(evt);
            }
        });
        jToolBar4.add(jTBOverlayWaveTracess);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPSelectedWavTracesTab.add(jToolBar4, gridBagConstraints);

        jPanel2.setPreferredSize(new java.awt.Dimension(800, 1600));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane8.setBorder(null);
        jScrollPane8.setPreferredSize(new java.awt.Dimension(800, 530));

        jPSelWavTrCollection.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), null));
        jPSelWavTrCollection.setMinimumSize(new java.awt.Dimension(20, 20));
        jPSelWavTrCollection.setPreferredSize(new java.awt.Dimension(50, 50));
        jPSelWavTrCollection.setLayout(new java.awt.GridLayout(2, 2));
        jScrollPane8.setViewportView(jPSelWavTrCollection);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel2.add(jScrollPane8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPSelectedWavTracesTab.add(jPanel2, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(SpecResultsTopComponent.class, "SpecResultsTopComponent.jPSelectedWavTracesTab.TabConstraints.tabTitle"), jPSelectedWavTracesTab); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jSRowStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSRowStateChanged
        crosshair2.setValue(dataset.GetImageHeigth() - jSRow.getValue());
        int xIndex = jSRow.getValue();
        XYSeriesCollection trace = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Trace");
        XYSeries series2 = new XYSeries("Fit");
        XYSeries series3 = new XYSeries("Residuals");

        for (int j = 0; j < res.getX2().length; j++) {
            series1.add(res.getX2()[j], res.getTraces().get(xIndex, j));
            series2.add(res.getX2()[j], res.getFittedTraces().get(xIndex, j));
            series3.add(res.getX2()[j], res.getTraces().get(xIndex, j) - res.getFittedTraces().get(xIndex, j));
        }

        trace.addSeries(series1);
        trace.addSeries(series2);
        NumberAxis xAxis = CommonResDispTools.createLinAxis(res.getX2(), "Wavelength, nm");
        GraphPanel linTime = CommonResDispTools.makeLinTimeTraceResidChart(trace, new XYSeriesCollection(series3), xAxis, null, false);
            jPSelectedWaveTrace.removeAll();
            jPSelectedWaveTrace.add(linTime);
            jPSelectedWaveTrace.validate();

    }//GEN-LAST:event_jSRowStateChanged

    private void jSColumStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSColumStateChanged
        crosshair1.setValue(jSColum.getValue());
        updateTrace(jSColum.getValue());
    }//GEN-LAST:event_jSColumStateChanged

    private void jBAutoSelectTracesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAutoSelectTracesActionPerformed
//create dialog
        SelectTracesForPlot selTracePanel = new SelectTracesForPlot();
        selTracePanel.setMaxNumbers(res.getX2().length, res.getX().length);
        NotifyDescriptor selTracesDialog = new NotifyDescriptor(
                selTracePanel,
                NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("selTracesForReport"),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.OK_OPTION);
//show dialog
        if (DialogDisplayer.getDefault().notify(selTracesDialog).equals(NotifyDescriptor.OK_OPTION)) {
//create time traces collection
            if (selTracePanel.getSelectXState()) {
                int numSelTraces = selTracePanel.getSelectXNum();
                int w = res.getX2().length / numSelTraces;
                int xIndex;
                selectedTimeTraces.clear();
                jPSelTimeTrCollection.removeAll();
                selectedTimeResidualsColection.removeAllSeries();
                selectedTimeTracesColection.removeAllSeries();
                CommonResDispTools.restorePanelSize(jPSelTimeTrCollection);
                CommonResDispTools.checkPanelSize(jPSelTimeTrCollection, numSelTraces);
                NumberAxis xAxis;
                XYSeriesCollection trace;
                XYSeriesCollection resid;
                GraphPanel linTime;
                
                for (int i = 0; i < numSelTraces; i++) {
                    xIndex = i * w;
                    trace= CommonResDispTools.createFitRawTraceCollection(xIndex, 0, res.getX().length, res,t0Curve[xIndex], String.valueOf(res.getX2()[xIndex]));    
                    resid = CommonResDispTools.createResidTraceCollection(xIndex, 0, res.getX().length, res, t0Curve[xIndex], String.valueOf(res.getX2()[xIndex]));
                            
                    selectedTimeTracesColection.addSeries(trace.getSeries(0));
                    selectedTimeTracesColection.addSeries(trace.getSeries(1));
                    selectedTimeResidualsColection.addSeries(resid.getSeries(0));

                    if (!jTBLinLogTraces.isSelected()) {
                        xAxis = CommonResDispTools.createLinAxis(res.getX(), "Time");
                        linTime = CommonResDispTools.makeLinTimeTraceResidChart(trace, resid, xAxis, String.valueOf(res.getX2()[xIndex]), false);
                        jPSelTimeTrCollection.add(linTime);
                    } else {
                        linTime = CommonResDispTools.createLinLogTimeTraceResidChart(trace, resid, String.valueOf(res.getX2()[xIndex]), false, linPart);
                        jPSelTimeTrCollection.add(linTime);
                    }
//Add index ot selected trace into list
                    selectedTimeTraces.add(xIndex);
                }
            }

//create wave traces colection
            if (selTracePanel.getSelectYState()) {
                int numSelTraces = selTracePanel.getSelectYNum();
                int w = res.getX().length / numSelTraces;
                XYSeriesCollection trace, resid;
                int xIndex;
                NumberAxis xAxis;
                ChartPanel chpan;
                selectedWaveTraces.clear();
                jPSelWavTrCollection.removeAll();
                
                selectedWaveTracesColection.removeAllSeries();
                selectedWaveResidualsColection.removeAllSeries();
                
                CommonResDispTools.restorePanelSize(jPSelWavTrCollection);
                CommonResDispTools.checkPanelSize(jPSelWavTrCollection, numSelTraces);

                for (int i = 0; i < numSelTraces; i++) {
//create common X axe for plot
                    xIndex = i * w;
                    xAxis = CommonResDispTools.createLinAxis(res.getX2(), "Wavelength (nm)");                   
                    trace = CommonResDispTools.createFitRawWaveTrCollection(xIndex, 0, res.getX2().length, res, String.valueOf(res.getX()[xIndex]));
                    resid = CommonResDispTools.createResidWaveCollection(xIndex, 0, res.getX2().length, res, String.valueOf(res.getX()[xIndex]));
                    selectedWaveTraces.add(xIndex);
                    selectedWaveTracesColection.addSeries(trace.getSeries(0));
                    selectedWaveTracesColection.addSeries(trace.getSeries(1));
                    selectedWaveResidualsColection.addSeries(resid.getSeries(0));
                    
                    chpan = CommonResDispTools.makeLinTimeTraceResidChart(
                            trace,
                            resid,
                            xAxis,
                            String.valueOf(res.getX()[xIndex]),
                            false);
                    jPSelWavTrCollection.add(chpan);
                }

            }
        }
    }//GEN-LAST:event_jBAutoSelectTracesActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        double portion = Double.valueOf(jTFLinPartTraces.getText());
        if (selectedTimeTraces.isEmpty()) {
        CommonResDispTools.restorePanelSize(jPSelTimeTrCollection);
        }
        selectedTimeTraces.add(jSColum.getValue());
//create jfreechart collections with selected time trace     
        XYSeriesCollection trace = CommonResDispTools.createFitRawTraceCollection(jSColum.getValue(), 0, res.getX().length, res, t0Curve[jSColum.getValue()], String.valueOf(res.getX2()[jSColum.getValue()]));
        XYSeriesCollection resid = CommonResDispTools.createResidTraceCollection(jSColum.getValue(), 0, res.getX().length, res, t0Curve[jSColum.getValue()], String.valueOf(res.getX2()[jSColum.getValue()]));
            
// add selected trace to collection             
            selectedTimeTracesColection.addSeries(trace.getSeries(0));
            selectedTimeTracesColection.addSeries(trace.getSeries(1));
// add residuals trace to collection
            selectedTimeResidualsColection.addSeries(resid.getSeries(0));

        if (jTBOverlayTimeTracess.isSelected()) {
            
        } 
        else {
            if (jTBLinLogTraces.isSelected()) {
                ChartPanel linLogTime = CommonResDispTools.createLinLogTimeTraceResidChart(trace, resid, String.valueOf(res.getX2()[jSColum.getValue()]), false, portion);
                linLogTime.getChart().setTitle(String.valueOf(res.getX2()[jSColum.getValue()]));
                jPSelTimeTrCollection.add(linLogTime);
            } else {
                NumberAxis xAxis = CommonResDispTools.createLinAxis(res.getX(), "Time ~s");
                ChartPanel linTime = CommonResDispTools.makeLinTimeTraceResidChart(trace, resid, xAxis, String.valueOf(res.getX2()[jSColum.getValue()]), false);
                jPSelTimeTrCollection.add(linTime);
            }
            CommonResDispTools.checkPanelSize(jPSelTimeTrCollection, selectedTimeTraces.size());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (selectedWaveTraces.isEmpty()) {
        CommonResDispTools.restorePanelSize(jPSelWavTrCollection);
        }
        selectedWaveTraces.add(jSRow.getValue());
//create jfreechart collections with selected time trace         
        XYSeriesCollection trace = CommonResDispTools.createFitRawWaveTrCollection(jSRow.getValue(), 0, res.getX2().length, res, String.valueOf(res.getX()[jSRow.getValue()]));
        XYSeriesCollection resid = CommonResDispTools.createResidWaveCollection(jSRow.getValue(), 0, res.getX2().length, res, String.valueOf(res.getX()[jSRow.getValue()]));
// add selected trace to collection    
        selectedWaveTracesColection.addSeries(trace.getSeries(0));    
        selectedWaveTracesColection.addSeries(trace.getSeries(1));
// add residuals trace to collection   
        selectedWaveResidualsColection.addSeries(resid.getSeries(0));
        
        if (jTBOverlayWaveTracess.isSelected()){
            
        } else {
            NumberAxis xAxis = CommonResDispTools.createLinAxis(res.getX2(), "Wavelenth (nm)");
            ChartPanel chpan = CommonResDispTools.makeLinTimeTraceResidChart(trace, resid, xAxis, String.valueOf(res.getX2()[jSColum.getValue()]), false);        
            jPSelWavTrCollection.add(chpan);
            CommonResDispTools.checkPanelSize(jPSelWavTrCollection, selectedWaveTraces.size());
        }
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jBClearAllWavelengthTracesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBClearAllWavelengthTracesActionPerformed
        jPSelWavTrCollection.removeAll();
        CommonResDispTools.restorePanelSize(jPSelWavTrCollection);
        jPSelWavTrCollection.repaint();
        selectedWaveTraces.clear();
        selectedWaveResidualsColection.removeAllSeries();
        selectedWaveTracesColection.removeAllSeries();

    }//GEN-LAST:event_jBClearAllWavelengthTracesActionPerformed

    private void jBClearAllTimeTracesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBClearAllTimeTracesActionPerformed
        jPSelTimeTrCollection.removeAll();
        CommonResDispTools.restorePanelSize(jPSelTimeTrCollection);
        jPSelTimeTrCollection.repaint();
        selectedTimeTraces.clear();
        selectedTimeResidualsColection.removeAllSeries();
        selectedTimeTracesColection.removeAllSeries();
    }//GEN-LAST:event_jBClearAllTimeTracesActionPerformed

    private void jCBDispCurveShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBDispCurveShowActionPerformed
        showDispCurve();
    }//GEN-LAST:event_jCBDispCurveShowActionPerformed

    private void jTBLinLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBLinLogActionPerformed

        if (jTBLinLog.isSelected()) {
            linPart = Double.valueOf(jTFLinPart.getText());
            jTFLinPartTraces.setText(jTFLinPart.getText());
            updateLinLog(true);
        } else {
            updateLinLog(false);
        }
    }//GEN-LAST:event_jTBLinLogActionPerformed

    private void updateLinLog(boolean selected) {
        if (selected) {
            try {
                jTBLinLogTraces.setSelected(true);
                jTBLinLog.setSelected(true);
                updateTrace(jSColum.getValue());
                updateLinLogPlotSumary();
                jPRightSingVectorsPart.validate();
                jPSingValuesPart.validate();
            } catch (Exception e) {
                CoreErrorMessages.updLinLogException();
                jTBLinLog.setSelected(false);
                jTBLinLogTraces.setSelected(false);
            }

        } else {
            jTBLinLog.setSelected(false);
            jTBLinLogTraces.setSelected(false);
            updateTrace(jSColum.getValue());
            ChartPanel conc;
            if (numberOfComponents < res.getConcentrations().getColumnDimension()) {
                conc = createLinTimePlot(concentrationsMatrix, res.getX(), true);
            } else {
                conc = createLinTimePlot(concentrationsMatrix, res.getX());
            }
            ChartPanel lsv = createLinTimePlot(leftSingVec, res.getX(), 2);
            lsv.getChart().setTitle("Left singular vectors");
            lsv.getChart().getTitle().setFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getFontName(), JFreeChart.DEFAULT_TITLE_FONT.getStyle(), 12));
            jPConcentrations.removeAll();
            jPConcentrations.add(conc);
            jPConcentrations.validate();
            jPLeftSingVectors.removeAll();
            jPLeftSingVectors.add(lsv);
            jPLeftSingVectors.validate();
            jBUpdLinLog.setEnabled(false);

        }
    }

    private void jBUpdLinLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBUpdLinLogActionPerformed
        try {
            linPart = Double.valueOf(jTFLinPart.getText());
            jTFLinPartTraces.setText(jTFLinPart.getText());
            updateTrace(jSColum.getValue());
            updateLinLogPlotSumary();
        } catch (Exception e) {
            CoreErrorMessages.updLinLogException();
        }
    }//GEN-LAST:event_jBUpdLinLogActionPerformed

    private void jTBLinLogTracesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBLinLogTracesActionPerformed
        if (jTBLinLogTraces.isSelected()) {
            linPart = Double.valueOf(jTFLinPartTraces.getText());
            jTFLinPart.setText(jTFLinPartTraces.getText());
            updateLinLog(true);
        } else {
            updateLinLog(false);
        }
    }//GEN-LAST:event_jTBLinLogTracesActionPerformed

    private void jTBShowChohSpecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBShowChohSpecActionPerformed
        plotSpectrTrace();
        jPSAS.validate();
        jPDAS.validate();
        jPDASnorm.validate();
        jPSASnorm.validate();
    }//GEN-LAST:event_jTBShowChohSpecActionPerformed

    private void jTBNormToMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBNormToMaxActionPerformed
        plotSpectrTrace();
        jPSAS.validate();
        jPDAS.validate();
        jPDASnorm.validate();
        jPSASnorm.validate();
    }//GEN-LAST:event_jTBNormToMaxActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        Range yRange = chartMain.getXYPlot().getRangeAxis().getRange();
        Range xRange = chartMain.getXYPlot().getDomainAxis().getRange();

        Matrix tempResidMatrix = residualMatrix.subMatrix(Ret.NEW, res.getX().length - (int) yRange.getUpperBound(),
                (int) xRange.getLowerBound(),
                res.getX().length - (int) yRange.getLowerBound() - 1,
                (int) xRange.getUpperBound() - 1);

        int newHeigh = (int) yRange.getUpperBound() - (int) yRange.getLowerBound();
        int newWidth = (int) xRange.getUpperBound() - (int) xRange.getLowerBound();
        double[] tempX = new double[newHeigh];
        double[] tempX2 = new double[newWidth];
        for (int i = 0; i < newWidth; i++) {
            tempX2[i] = res.getX2()[i + (int) xRange.getLowerBound()];
        }
        for (int i = 0; i < newHeigh; i++) {
            tempX[i] = res.getX()[i + res.getX().length - (int) yRange.getUpperBound()];
        }
        timePart = tempX;
        Matrix[] tempSVDResult = calculateSVD(tempResidMatrix);
        leftSingVecPart = tempSVDResult[0];
        updateSVDPlots(tempSVDResult, tempX, tempX2, jPRightSingVectorsPart, jPLeftSingVectorsPart, jPSingValuesPart);
        updateTrace(jSColum.getValue());
        jPRightSingVectorsPart.validate();
        jPSingValuesPart.validate();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jTFLinPartFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFLinPartFocusLost
        try {
            // String textValue = the string from your JTextField
            linPart = Double.parseDouble(jTFLinPart.getText());
            jTFLinPartTraces.setText(jTFLinPart.getText());
            if (jTBLinLogTraces.isSelected()) {
                updateTrace(jSColum.getValue());
                updateLinLogPlotSumary();
            }
            // if the code gets to here, it was recognizable as a double             
        } catch (NumberFormatException e) {
            // if the code gets to here, it was NOT recognizable as a double
        }
    }//GEN-LAST:event_jTFLinPartFocusLost

    private void jTFLinPartTracesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFLinPartTracesFocusLost
        try {
            // String textValue = the string from your JTextField
            linPart = Double.parseDouble(jTFLinPartTraces.getText());
            jTFLinPart.setText(jTFLinPartTraces.getText());
            if (jTBLinLogTraces.isSelected()) {
                updateTrace(jSColum.getValue());
                updateLinLogPlotSumary();
            }
        } catch (NumberFormatException e) {
            // if the code gets to here, it was NOT recognizable as a double
        }
    }//GEN-LAST:event_jTFLinPartTracesFocusLost

    private void jBExportTimeTracesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportTimeTracesActionPerformed
        // Exports all current traces on the tab in the following format
        // TAB  Wav1    Res1    Wav2    Res2
        // t0   y01     r01     y02     r02
        // t0   y11     r11     y12     r12
        
        Jama.Matrix fittedTracesMat;
        Jama.Matrix residualTracesMat;
        BufferedWriter output = null;
        
        JFileChooser fileChooser = new JFileChooser();
        //TODO: implement fileChooser.setCurrentDirectory(this.getDefaultDirectoryForSaveAs());
        ExtensionFileFilter filter = new ExtensionFileFilter("Comma separated file (*.csv)", ".csv");
        fileChooser.addChoosableFileFilter(filter);
        int option = fileChooser.showSaveDialog(this);
        
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
                if (!filename.endsWith(".csv")) {
                    filename += ".csv";
                }                    

        try {
            output = new BufferedWriter(new FileWriter(new File(filename)));
            StringBuilder sb = new StringBuilder();
            fittedTracesMat = res.getFittedTraces();
            residualTracesMat = res.getResiduals();
            double[] wavenumbers = res.getX2();
            
            sb.append("FIT,");
            for (int index = 0; index < selectedTimeTraces.size(); index++) {
                sb.append(wavenumbers[selectedTimeTraces.get(index)]);
                sb.append(",    ");
                if (index < selectedTimeTraces.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
            
            double[] timepoints = res.getX();            

            for (int i = 0; i < fittedTracesMat.getRowDimension(); i++) {
                sb.append(timepoints[i]);
                sb.append(",");
                for (int index = 0; index < selectedTimeTraces.size(); index++) {                                        
                    sb.append(fittedTracesMat.get(i, selectedTimeTraces.get(index)));
                    sb.append(",");
                    sb.append(residualTracesMat.get(i, selectedTimeTraces.get(index)));
                    if (index < selectedTimeTraces.size() - 1) {
                        sb.append(",");
                    }
                }
                sb.append("\n");
            }
            output.append(sb);
            output.close();

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        }

    }//GEN-LAST:event_jBExportTimeTracesActionPerformed

    private void jBExportWaveTracesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportWaveTracesActionPerformed
        // export jPSelWavTrCollection
                // Exports all current traces on the tab in the following format
        // TAB   Time1    Res1    Time2   Res2
        // Wav1   y01     r01     y02     r02
        // Wav2   y11     r11     y12     r12
        
        Jama.Matrix fittedTracesMat;
        Jama.Matrix residualTracesMat;
        BufferedWriter output = null;
        
        JFileChooser fileChooser = new JFileChooser();
        //TODO: implement fileChooser.setCurrentDirectory(this.getDefaultDirectoryForSaveAs());
        ExtensionFileFilter filter = new ExtensionFileFilter("Comma separated file (*.csv)", ".csv");
        fileChooser.addChoosableFileFilter(filter);
        int option = fileChooser.showSaveDialog(this);
        
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
                if (!filename.endsWith(".csv")) {
                    filename += ".csv";
                }                    

        try {
            output = new BufferedWriter(new FileWriter(new File(filename)));
            StringBuilder sb = new StringBuilder();
            fittedTracesMat = res.getFittedTraces();
            residualTracesMat = res.getResiduals();
            double[] timepoints = res.getX();           
            
            sb.append("FIT,");
            for (int index = 0; index < selectedWaveTraces.size(); index++) {
                sb.append(timepoints[selectedWaveTraces.get(index)]);
                sb.append(",    ");
                if (index < selectedWaveTraces.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
            double[] wavenumbers = res.getX2();
                        

            for (int i = 0; i < fittedTracesMat.getRowDimension(); i++) {
                sb.append(wavenumbers[i]);
                sb.append(",");
                for (int index = 0; index < selectedWaveTraces.size(); index++) {                                        
                    sb.append(fittedTracesMat.get(selectedWaveTraces.get(index),i));
                    sb.append(",");
                    sb.append(residualTracesMat.get(selectedWaveTraces.get(index),i));
                    if (index < selectedWaveTraces.size() - 1) {
                        sb.append(",");
                    }
                }
                sb.append("\n");
            }
            output.append(sb);
            output.close();

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        }
    }//GEN-LAST:event_jBExportWaveTracesActionPerformed

    private void jTBOverlayWaveTracessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBOverlayWaveTracessActionPerformed
        jPSelWavTrCollection.removeAll();
        if (jTBOverlayWaveTracess.isSelected()) {
             CommonResDispTools.restorePanelSize(jPSelWavTrCollection);
            NumberAxis xAxis = CommonResDispTools.createLinAxis(res.getX2(), "Wavelenth (nm)");
            jPSelWavTrCollection.setLayout(new BorderLayout());
            ChartPanel chpan = CommonResDispTools.makeLinTimeTraceResidChart(selectedWaveTracesColection, selectedWaveResidualsColection, xAxis, "Selected spectra", true, true);
            jPSelWavTrCollection.add(chpan);
        } else {
            CommonResDispTools.restorePanelSize(jPSelWavTrCollection);
            NumberAxis xAxis;
            XYSeriesCollection trace;
            XYSeriesCollection resid;
            ChartPanel chpan;
            for (int i = 0; i < selectedWaveTraces.size(); i++) {
                xAxis = CommonResDispTools.createLinAxis(res.getX2(), "Wavelenth (nm)");
                resid = new XYSeriesCollection(selectedWaveResidualsColection.getSeries(i));
                trace = new XYSeriesCollection(selectedWaveTracesColection.getSeries(2*i));
                trace.addSeries(selectedWaveTracesColection.getSeries(2 * i + 1));
                chpan = CommonResDispTools.makeLinTimeTraceResidChart(trace, resid, xAxis, String.valueOf(res.getX2()[i]), false);
                jPSelWavTrCollection.add(chpan);
                
            }
            CommonResDispTools.checkPanelSize(jPSelWavTrCollection, selectedWaveTraces.size());
        }
        
        jPSelWavTrCollection.repaint();
        jPSelWavTrCollection.updateUI();
    }//GEN-LAST:event_jTBOverlayWaveTracessActionPerformed

    private void jTBOverlayTimeTracessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBOverlayTimeTracessActionPerformed
        jPSelTimeTrCollection.removeAll();
        ChartPanel chpan;
        if (jTBOverlayTimeTracess.isSelected()) {
            CommonResDispTools.restorePanelSize(jPSelTimeTrCollection);
            NumberAxis xAxis = CommonResDispTools.createLinAxis(res.getX(), "Time ~s");
            jPSelTimeTrCollection.setLayout(new BorderLayout());
            if (jTBLinLogTraces.isSelected()){
                double portion = Double.valueOf(jTFLinPartTraces.getText());
                chpan = CommonResDispTools.createLinLogTimeTraceResidChart(selectedTimeTracesColection, selectedTimeResidualsColection, "Selected time traces", true, portion, true);
            }
            else {
                chpan = CommonResDispTools.makeLinTimeTraceResidChart(selectedTimeTracesColection, selectedTimeResidualsColection, xAxis, "Selected time traces", true, true);
            }
            jPSelTimeTrCollection.add(chpan);
        } else {
            CommonResDispTools.restorePanelSize(jPSelTimeTrCollection);
            NumberAxis xAxis;
            XYSeriesCollection trace;
            XYSeriesCollection resid;
            for (int i = 0; i < selectedTimeTraces.size(); i++) {
                xAxis = CommonResDispTools.createLinAxis(res.getX(), "Time ~s");
                resid = new XYSeriesCollection(selectedTimeResidualsColection.getSeries(i));
                trace = new XYSeriesCollection(selectedTimeTracesColection.getSeries(2 * i));
                trace.addSeries(selectedTimeTracesColection.getSeries(2 * i + 1));
                if (jTBLinLogTraces.isSelected()) {
                    double portion = Double.valueOf(jTFLinPartTraces.getText());
                    chpan = CommonResDispTools.createLinLogTimeTraceResidChart(trace, resid, String.valueOf(res.getX()[i]), false, portion);
                } else {
                    chpan = CommonResDispTools.makeLinTimeTraceResidChart(trace, resid, xAxis, String.valueOf(res.getX()[i]), false);
                }
                jPSelTimeTrCollection.add(chpan);

            }
            CommonResDispTools.checkPanelSize(jPSelTimeTrCollection, selectedTimeTraces.size());
        }
        jPSelTimeTrCollection.repaint();
        jPSelTimeTrCollection.updateUI();
    }//GEN-LAST:event_jTBOverlayTimeTracessActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBAutoSelectTraces;
    private javax.swing.JButton jBClearAllTimeTraces;
    private javax.swing.JButton jBClearAllWavelengthTraces;
    private javax.swing.JButton jBExportTimeTraces;
    private javax.swing.JButton jBExportWaveTraces;
    private javax.swing.JButton jBUpdLinLog;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCBDispCurveShow;
    private javax.swing.JList jLKineticParameters;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPConcentrations;
    private javax.swing.JPanel jPDAS;
    private javax.swing.JPanel jPDASnorm;
    private javax.swing.JPanel jPLeftSingVectors;
    private javax.swing.JPanel jPLeftSingVectorsPart;
    private javax.swing.JPanel jPOverviewTab;
    private javax.swing.JPanel jPRightSingVectors;
    private javax.swing.JPanel jPRightSingVectorsPart;
    private javax.swing.JPanel jPSAS;
    private javax.swing.JPanel jPSASnorm;
    private javax.swing.JPanel jPSelTimeTrCollection;
    private javax.swing.JPanel jPSelWavTrCollection;
    private javax.swing.JPanel jPSelectedTimTracesTab;
    private javax.swing.JPanel jPSelectedTimeTrace;
    private javax.swing.JPanel jPSelectedWavTracesTab;
    private javax.swing.JPanel jPSelectedWaveTrace;
    private javax.swing.JPanel jPSingValues;
    private javax.swing.JPanel jPSingValuesPart;
    private javax.swing.JPanel jPSpecImage;
    private javax.swing.JPanel jPTracesTab;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSlider jSColum;
    private javax.swing.JSlider jSRow;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToggleButton jTBLinLog;
    private javax.swing.JToggleButton jTBLinLogTraces;
    private javax.swing.JToggleButton jTBNormToMax;
    private javax.swing.JToggleButton jTBOverlayTimeTracess;
    private javax.swing.JToggleButton jTBOverlayWaveTracess;
    private javax.swing.JToggleButton jTBShowChohSpec;
    private javax.swing.JTextField jTFCentrWave;
    private javax.swing.JTextField jTFCurvParam;
    private javax.swing.JTextField jTFLinPart;
    private javax.swing.JTextField jTFLinPartTraces;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized SpecResultsTopComponent getDefault() {
        if (instance == null) {
            instance = new SpecResultsTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the StreakOutTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized SpecResultsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(SpecResultsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof SpecResultsTopComponent) {
            return (SpecResultsTopComponent) win;
        }
        Logger.getLogger(SpecResultsTopComponent.class.getName()).warning(
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

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public void chartChanged(ChartChangeEvent arg0) {
        XYPlot plot = this.chartMain.getXYPlot();
        double lowBound = plot.getDomainAxis().getRange().getLowerBound();
        double upBound = plot.getDomainAxis().getRange().getUpperBound();
        boolean recreate = false;
        int lowInd, upInd;

        if (lowBound < wholeXRange.getLowerBound()) {
            lowBound = wholeXRange.getLowerBound();
            recreate = true;
        }
        if (upBound > wholeXRange.getUpperBound()) {
            upBound = wholeXRange.getUpperBound();
            recreate = true;
        }
        if (recreate) {
            plot.getDomainAxis().setRange(new Range(lowBound, upBound));
//            this.chartMain.getPlot().getDomainAxis().setRange(new Range(lowBound, upBound));
        }
        recreate = false;
        lowBound = plot.getRangeAxis().getRange().getLowerBound();
        upBound = plot.getRangeAxis().getRange().getUpperBound();
        if (lowBound < wholeYRange.getLowerBound()) {
            lowBound = wholeYRange.getLowerBound();
            recreate = true;
        }
        if (upBound > wholeYRange.getUpperBound()) {
            upBound = wholeYRange.getUpperBound();
            recreate = true;
        }
        if (recreate) {
            plot.getRangeAxis().setRange(new Range(lowBound, upBound));
//            this.chartMain.getPlot().getDomainAxis().setRange(new Range(lowBound, upBound));
        }

//============begin of the pice of code
/*code below will make imageplote behave like in datasetdisplayers
        but it still gives null pointers i guess because of the differenbut
        type of plot for trace (xyplot and combine plot)
        if you want you can try to debug it or we can leave it for later
         */
//        if (!plot.getDomainAxis().getRange().equals(this.lastXRange)) {
//            this.lastXRange = plot.getDomainAxis().getRange();
//            XYPlot plot2 = (XYPlot) this.subchartWaveTrace.getPlot();
//            lowInd = (int) (this.lastXRange.getLowerBound());
//            upInd = (int) (this.lastXRange.getUpperBound() - 1);
//            plot2.getDomainAxis().setRange(new Range(res.getX2()[lowInd],res.getX2()[upInd]));
//            jSColum.setMinimum(lowInd);
//            jSColum.setMaximum(upInd);
//
//        }
//
//        if (!plot.getRangeAxis().getRange().equals(this.lastYRange)) {
//            this.lastYRange = plot.getRangeAxis().getRange();
//            XYPlot plot1 = (XYPlot) this.subchartTimeTrace.getPlot();
//            lowInd = (int) (this.wholeYRange.getUpperBound() - this.lastYRange.getUpperBound());
//            upInd = (int) (this.wholeYRange.getUpperBound() - this.lastYRange.getLowerBound() - 1);
//            plot1.getDomainAxis().setRange(new Range(res.getX()[lowInd], res.getX()[upInd]));
//            jSRow.setMinimum(lowInd);
//            jSRow.setMaximum(upInd);
//        }
//========end of the pice code
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return SpecResultsTopComponent.getDefault();
        }
    }
    
    private GraphPanel createLinLogTimePlot(double timeZero, double linearBoundValue, Matrix data, double[] timesteps) {
        return createLinLogTimePlot(timeZero, linearBoundValue, data, timesteps, false);
    }

    private GraphPanel createLinLogTimePlot(double timeZero, double linearBoundValue, Matrix data, double[] timesteps, boolean conc) {
        XYSeries seria;
        int numberOfTraces = (int) (conc ? data.getColumnCount() : Math.min(data.getColumnCount(), 2));

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (int j = 0; j < numberOfTraces; j++) {
            seria = new XYSeries("Comp" + (j + 1));
            for (int i = 0; i < timesteps.length; i++) {
                seria.add(timesteps[i] - timeZero, data.getAsDouble(i, j));
            }
            xySeriesCollection.addSeries(seria);
        }
        return CommonResDispTools.makeLinLogTimeTraceChart(xySeriesCollection, null, false, linearBoundValue);
    }

    private GraphPanel createLinTimePlot(Matrix data, double[] timesteps) {
        return createLinTimePlot(data, timesteps, false, 0);
    }

    private GraphPanel createLinTimePlot(Matrix data, double[] timesteps, boolean conc) {
        return createLinTimePlot(data, timesteps, conc, 0);
    }

    private GraphPanel createLinTimePlot(Matrix data, double[] timesteps, int comps) {
        return createLinTimePlot(data, timesteps, false, comps);
    }

    private GraphPanel createLinTimePlot(Matrix dataRaw, double[] timesteps, boolean conc, int comps) {
        Matrix data = dataRaw;
        if (conc) {
            double maxComps = 0;
            double maxCoh = 0;
            for (int i = 0; i < data.getColumnCount() - 1; i++) {
                for (int j = 0; j < data.getRowCount(); j++) {
                    if (maxComps < data.getAsDouble(j, i)) {
                        maxComps = data.getAsDouble(j, i);
                    }
                }
            }
            for (int j = 0; j < data.getRowCount(); j++) {
                if (maxCoh < data.getAsDouble(j, data.getColumnCount() - 1)) {
                    maxCoh = data.getAsDouble(j, data.getColumnCount() - 1);
                }
            }
            for (int j = 0; j < data.getRowCount(); j++) {
                data.setAsDouble(dataRaw.getAsDouble(j, data.getColumnCount() - 1) / maxCoh * maxComps, j, data.getColumnCount() - 1);
            }
        }
        int numberOfTraces;
        if (comps > 0) {
            numberOfTraces = (int) Math.min(data.getColumnCount(), comps);
        } else {
            numberOfTraces = (int) data.getColumnCount();
        }
        XYSeriesCollection concCollection = new XYSeriesCollection();
        XYSeries seria;
        for (int j = 0; j < numberOfTraces; j++) {
            seria = new XYSeries("Conc" + (j + 1));
            for (int i = 0; i < timesteps.length; i++) {
                seria.add(timesteps[i], data.getAsDouble(i, j));
            }
            concCollection.addSeries(seria);
        }
        return CommonResDispTools.createGraphPanel(concCollection, null, "Time", false);
    }

    private void plotSpectrTrace() {
        String specName = res.getJvec() != null ? "SAS" : "EAS";
        boolean errorBars = res.getSpectraErr() != null ? true : false;
        int compNumFull = jTBShowChohSpec.isEnabled() ? numberOfComponents + 1 : numberOfComponents;
        int compNum = jTBShowChohSpec.isSelected() ? numberOfComponents + 1 : numberOfComponents;
        double maxAmpl;
        double maxDasAmpl;

        YIntervalSeriesCollection realSasCollection = new YIntervalSeriesCollection();
        YIntervalSeriesCollection normSasCollection = new YIntervalSeriesCollection();
        XYSeriesCollection realDasCollection = new XYSeriesCollection();
        XYSeriesCollection normDasCollection = new XYSeriesCollection();
        YIntervalSeries seria;
        XYSeries dasSeria;

//create collection of real sas and normalizes all of them to max or abs(max) and creates collection with normSAS
        for (int j = 0; j < compNum; j++) {
            seria = new YIntervalSeries(specName + (j + 1));// new XYSeries(specName + (j + 1));
            dasSeria = new XYSeries("DAS" + (j + 1));
            maxAmpl = 0;
            maxDasAmpl = 0;
            for (int i = 0; i < res.getX2().length; i++) {
                if (res.getSpectraErr() != null) {
                    seria.add(res.getX2()[i], res.getSpectra().get(j, i),
                            res.getSpectra().get(j, i) - res.getSpectraErr().get(j, i),
                            res.getSpectra().get(j, i) + res.getSpectraErr().get(j, i));
                } else {
                    seria.add(res.getX2()[i], res.getSpectra().get(j, i),
                            res.getSpectra().get(j, i),
                            res.getSpectra().get(j, i));
                }
                dasSeria.add(res.getX2()[i], res.getSpectra().get(j + compNumFull, i));
                if (jTBNormToMax.isSelected()) {
                    if (maxAmpl < (res.getSpectra().get(j, i))) {
                        maxAmpl = (res.getSpectra().get(j, i));
                    }
                    if (res.getSpectra().getRowDimension() > compNum) {
                        if (maxDasAmpl < (res.getSpectra().get(j + compNumFull, i))) {
                            maxDasAmpl = (res.getSpectra().get(j + compNumFull, i));
                        }
                    }
                } else {
                    if (maxAmpl < abs(res.getSpectra().get(j, i))) {
                        maxAmpl = abs(res.getSpectra().get(j, i));
                    }
                    if (res.getSpectra().getRowDimension() > compNum) {
                        if (maxDasAmpl < abs(res.getSpectra().get(j + compNumFull, i))) {
                            maxDasAmpl = abs(res.getSpectra().get(j + compNumFull, i));
                        }
                    }
                }
            }
            realSasCollection.addSeries(seria);
            if (j < numberOfComponents) {
                realDasCollection.addSeries(dasSeria);
            }

            seria = new YIntervalSeries("Norm" + specName + (j + 1));
            dasSeria = new XYSeries("NormDas" + (j + 1));
            for (int i = 0; i < res.getX2().length; i++) {
                if (res.getSpectraErr() != null) {
                    if (maxAmpl > 0) {
                        seria.add(res.getX2()[i], res.getSpectra().get(j, i) / maxAmpl,
                                res.getSpectra().get(j, i) / maxAmpl - res.getSpectraErr().get(j, i) / maxAmpl,
                                res.getSpectra().get(j, i) / maxAmpl + res.getSpectraErr().get(j, i) / maxAmpl);
                    } else {
                        seria.add(res.getX2()[i], 0, 0, 0);
                    }
                } else {
                    if (maxAmpl > 0) {
                        seria.add(res.getX2()[i], res.getSpectra().get(j, i) / maxAmpl,
                                res.getSpectra().get(j, i) / maxAmpl,
                                res.getSpectra().get(j, i) / maxAmpl);
                    } else {
                        seria.add(res.getX2()[i], 0, 0, 0);
                    }
                }

                dasSeria.add(res.getX2()[i], res.getSpectra().get(j + compNumFull, i) / maxDasAmpl);
            }
            normSasCollection.addSeries(seria);
            if (j < numberOfComponents) {
                normDasCollection.addSeries(dasSeria);
            }
        }

        GraphPanel chpan = CommonResDispTools.createGraphPanel(realSasCollection, specName, "Wavelength (nm)", errorBars);
        jPSAS.removeAll();
        jPSAS.add(chpan);

        chpan = CommonResDispTools.createGraphPanel(normSasCollection, "norm" + specName, "Wavelength (nm)", errorBars);
        jPSASnorm.removeAll();
        jPSASnorm.add(chpan);

        chpan = CommonResDispTools.createGraphPanel(realDasCollection, "DAS", "Wavelength (nm)", false);
        jPDAS.removeAll();
        jPDAS.add(chpan);

        chpan = CommonResDispTools.createGraphPanel(normDasCollection, "normDAS", "Wavelength (nm)", false);
        jPDASnorm.removeAll();
        jPDASnorm.add(chpan);

    }

    private JFreeChart createChart(XYDataset dataset1) {

        JFreeChart chart_temp = ChartFactory.createScatterPlot(null,
                null, null, dataset1, PlotOrientation.VERTICAL, false, false,
                false);

        PaintScale ps = new RainbowPaintScale(res.getMinInt(), res.getMaxInt());
        BufferedImage image = ImageUtilities.createColorCodedImage(this.dataset, ps);

        XYDataImageAnnotation ann = new XYDataImageAnnotation(image, 0, 0,
                dataset.GetImageWidth(), dataset.GetImageHeigth(), true);
        XYPlot plot = (XYPlot) chart_temp.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.getRenderer().addAnnotation(ann, Layer.BACKGROUND);
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        xAxis.setVisible(false);
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);
        yAxis.setVisible(false);
        return chart_temp;
    }

    private void makeImageChart() {
        dataset = new ColorCodedImageDataset(res.getX2().length, res.getX().length,
                res.getTraces().getRowPackedCopy(), res.getX2(), res.getX(), false);
        PaintScale ps = new RainbowPaintScale(res.getMinInt(), res.getMaxInt());
        this.chartMain = createChart(new XYSeriesCollection());
        this.chartMain.addChangeListener((ChartChangeListener) this);
        XYPlot tempPlot = (XYPlot) this.chartMain.getPlot();
        this.wholeXRange = tempPlot.getDomainAxis().getRange();
        this.wholeYRange = tempPlot.getRangeAxis().getRange();

        //added
        NumberAxis xAxis = new NumberAxis("Wavelength (nm) → ");
//        xAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
//        xAxis.setLowerMargin(0.0);
//        xAxis.setUpperMargin(0.0);
//        xAxis.setAxisLinePaint(Color.white);
//        xAxis.setTickMarkPaint(Color.white);
//        xAxis.setRange(res.getX()2[0], res.getX2()[res.getX2().length-1]);
        int numberOfTicks = Math.min(res.getX2().length, MAX_NO_TICKS);
        DecimalFormat formatter = new DecimalFormat("##0E0");
        NonLinearNumberTickUnit xTickUnit = new NonLinearNumberTickUnit(res.getX2().length / numberOfTicks, formatter, res.getX2());
        xAxis.setTickUnit(xTickUnit);
        xAxis.setTickLabelsVisible(false);
        tempPlot.setDomainAxis(xAxis);
        NumberAxis yAxis = new NumberAxis("← Time (ps)");
//        yAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
//        yAxis.setLowerMargin(0.0);
//        yAxis.setUpperMargin(0.0);
//        yAxis.setAxisLinePaint(Color.white);
//        yAxis.setTickMarkPaint(Color.white);
//        yAxis.setRange(res.getX()[0], res.getX()[res.getX().length-1]);
        formatter = new DecimalFormat("##0.#E0");
        numberOfTicks = Math.min(res.getX().length, MAX_NO_TICKS);
        NonLinearNumberTickUnit yTickUnit = new NonLinearNumberTickUnit(res.getX().length / numberOfTicks, formatter, res.getX(), true);
        yAxis.setTickUnit(yTickUnit);
        yAxis.setTickLabelsVisible(false);
        tempPlot.setRangeAxis(yAxis);

        tempPlot.setInsets(new RectangleInsets(0, 0, 0, 0));
        chpanImage = new HeightMapPanel(chartMain, true);
        chpanImage.setFillZoomRectangle(true);
        chpanImage.setMouseWheelEnabled(true);
        chpanImage.setZoomFillPaint(new Color(68, 68, 78, 63));
        ImageCrosshairLabelGenerator crossLabGen1 = new ImageCrosshairLabelGenerator(res.getX2(), false);
        ImageCrosshairLabelGenerator crossLabGen2 = new ImageCrosshairLabelGenerator(res.getX(), true);

        CrosshairOverlay overlay = new CrosshairOverlay();
        crosshair1 = new Crosshair(0.0);
        crosshair1.setPaint(Color.red);
        crosshair2 = new Crosshair(0.0);
        crosshair2.setPaint(Color.GRAY);
        overlay.addDomainCrosshair(crosshair1);
        overlay.addRangeCrosshair(crosshair2);
        chpanImage.addOverlay(overlay);
        crosshair1.setLabelGenerator(crossLabGen1);
        crosshair1.setLabelVisible(true);
        crosshair1.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        crosshair1.setLabelBackgroundPaint(new Color(255, 255, 0, 100));
        crosshair2.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        crosshair2.setLabelGenerator(crossLabGen2);
        crosshair2.setLabelVisible(true);
        crosshair2.setLabelBackgroundPaint(new Color(255, 255, 0, 100));

        NumberAxis scaleAxis = new NumberAxis();
        scaleAxis.setAxisLinePaint(Color.black);
        scaleAxis.setTickMarkPaint(Color.black);
        scaleAxis.setRange(res.getMinInt(), res.getMaxInt());
        scaleAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 9));
        PaintScaleLegend legend = new PaintScaleLegend(ps, scaleAxis);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        legend.setMargin(new RectangleInsets(5, 5, 5, 5));
        legend.setStripWidth(15);
        legend.setPosition(RectangleEdge.RIGHT);
        chartMain.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
        legend.setBackgroundPaint(chartMain.getBackgroundPaint());
        chartMain.addSubtitle(legend);
        jPSpecImage.add(chpanImage);
        showDispCurve();
    }

    private Matrix[] calculateSVD(Matrix matrix) {
        return matrix.svd();
    }

    private void updateSVDPlots(Matrix[] svdResult, double[] x, double[] x2, JPanel jPRSV, JPanel jPLSV, JPanel jPSV) {
//do SVD

        long maxSVNumber = Math.min(MAX_NUMBER_SINGULAR_VALUES, svdResult[1].getRowCount());
        int n = 2;
//creare collection with first 2 LSV
        XYSeriesCollection lSVCollection = new XYSeriesCollection();
        XYSeries seria;
        for (int j = 0; j < n; j++) {
            seria = new XYSeries("LSV" + (j + 1));
            for (int i = 0; i < x.length; i++) {
                seria.add(x[i], svdResult[0].getAsDouble((long) i, (long) j));
            }
            lSVCollection.addSeries(seria);
        }

//creare collection with first 2 RSV
        XYSeriesCollection rSVCollection = new XYSeriesCollection();
        for (int j = 0; j < n; j++) {
            seria = new XYSeries("RSV" + (j + 1));
            for (int i = 0; i < x2.length; i++) {
                seria.add(x2[i], svdResult[2].getAsDouble((long) i, (long) j));
            }
            rSVCollection.addSeries(seria);
        }

//creare collection with singular values
        XYSeriesCollection sVCollection = new XYSeriesCollection();
        seria = new XYSeries("SV");
        for (int i = 0; i < maxSVNumber; i++) {
            seria.add(i + 1, svdResult[1].getAsDouble((long) i, (long) i));
        }
        sVCollection.addSeries(seria);

        createSVDPlots(jPRSV, rSVCollection, jPLSV, lSVCollection, jPSV, sVCollection);
    }

    private void createSVDPlots(JPanel jPRSV, XYSeriesCollection colRSV, JPanel jPLSV, XYSeriesCollection colLSV, JPanel jPSV, XYSeriesCollection colSV) {
//creare chart for 2 LSV
        JFreeChart tracechart = ChartFactory.createXYLineChart(
                "Left singular vectors",
                "Time",
                null,
                colLSV,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        tracechart.getTitle().setFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getFontName(), JFreeChart.DEFAULT_TITLE_FONT.getStyle(), 12));
        tracechart.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
        tracechart.getXYPlot().getDomainAxis().setAutoRange(false);
        tracechart.getXYPlot().getDomainAxis().setUpperMargin(0.0);
        tracechart.getXYPlot().getDomainAxis().setLowerMargin(0.0);
        tracechart.getXYPlot().setRangeZeroBaselineVisible(true);
        GraphPanel chpan = new GraphPanel(tracechart, false);
//add chart with 2 LSV to JPannel
        jPLSV.removeAll();
        jPLSV.add(chpan);

//creare chart for 2 RSV
        tracechart = ChartFactory.createXYLineChart(
                "Right singular vectors",
                "Wavelength (nm)",
                null,
                colRSV,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        tracechart.getTitle().setFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getFontName(), JFreeChart.DEFAULT_TITLE_FONT.getStyle(), 12));
        tracechart.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
        tracechart.getXYPlot().getDomainAxis().setUpperMargin(0.0);
        tracechart.getXYPlot().getDomainAxis().setLowerMargin(0.0);
        tracechart.getXYPlot().getDomainAxis().setAutoRange(false);
        tracechart.getXYPlot().setRangeZeroBaselineVisible(true);
        chpan = new GraphPanel(tracechart, false);
//add chart with 2 RSV to JPannel
        jPRSV.removeAll();
        jPRSV.add(chpan);

//creare chart for 2 RSV
        tracechart = ChartFactory.createXYLineChart(
                "Screeplot",
                "Singular Value Index (n)",
                null,
                colSV,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        tracechart.getXYPlot().setRangeAxis(new LogAxis("Log(SVn)"));
//        int index = colSV.getSeries(0).getItemCount() - 1;
//        while (colSV.getSeries(0).getDataItem(index).getYValue() <= 0) {
//            index--;
//        }
//        tracechart.getXYPlot().getRangeAxis().setRange(colSV.getSeries(0).getDataItem(Math.min(index, 10)).getYValue(), colSV.getSeries(0).getDataItem(0).getYValue());
//        tracechart.getXYPlot().getDomainAxis().setRange(colSV.getSeries(0).getDataItem(0).getXValue(), colSV.getSeries(0).getDataItem(Math.min(index, 10)).getXValue());
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) tracechart.getXYPlot().getRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setBaseFillPaint(Color.white);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesOutlineStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));

        tracechart.getTitle().setFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getFontName(), JFreeChart.DEFAULT_TITLE_FONT.getStyle(), 12));
        tracechart.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);

        chpan = new GraphPanel(tracechart, false);
//add chart with 2 RSV to JPannel
        jPSV.removeAll();
        jPSV.add(chpan);
    }

    private XYDataset createDispersionCurve() {
        XYSeries curve = new XYSeries("dispersion");
        int k = 0;
        for (int i = 0; i < res.getX2().length; i++) {
            while (t0Curve[i] > res.getX()[k]) {
                k++;
            }
            curve.add(i, res.getX().length - k);
            k = 0;
        }
        XYDataset curveDataset = new XYSeriesCollection(curve);
        return curveDataset;
    }

    private void updateLinLogPlotSumary() {
        double linPortion = linPart;
        ChartPanel conc = createLinLogTimePlot(t0Curve[0], linPortion, concentrationsMatrix, res.getX(), true);
        ChartPanel lsv = createLinLogTimePlot(t0Curve[0], linPortion, leftSingVec, res.getX());
        lsv.getChart().setTitle("Left singular vectors");
        lsv.getChart().getTitle().setFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getFontName(), JFreeChart.DEFAULT_TITLE_FONT.getStyle(), 12));

        jPConcentrations.removeAll();
        conc.setSize(jPConcentrations.getSize());
        jPConcentrations.add(conc);
        jPConcentrations.repaint();

        jPLeftSingVectors.removeAll();
        lsv.setSize(jPLeftSingVectors.getSize());
        jPLeftSingVectors.add(lsv);
        jPLeftSingVectors.repaint();
        jBUpdLinLog.setEnabled(true);

    }

    private void updateTrace(int xIndex) {
        XYSeriesCollection trace = CommonResDispTools.createFitRawTraceCollection(xIndex, 0, res.getX().length, res);
        XYSeriesCollection resid = CommonResDispTools.createResidTraceCollection(xIndex, 0, res.getX().length, res);
        if (!jTBLinLogTraces.isSelected()) {
            NumberAxis xAxis = CommonResDispTools.createLinAxis(res.getX(), "time");
            GraphPanel linTime = CommonResDispTools.makeLinTimeTraceResidChart(trace, resid, xAxis, null, false);
            jPSelectedTimeTrace.removeAll();
            jPSelectedTimeTrace.add(linTime);
            jPSelectedTimeTrace.validate();

            if (leftSingVecPart != null) {
                GraphPanel lsv = createLinTimePlot(leftSingVecPart, timePart, 2);
                lsv.getChart().setTitle("Left singular vectors");
                lsv.getChart().getTitle().setFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getFontName(), JFreeChart.DEFAULT_TITLE_FONT.getStyle(), 12));
                jPLeftSingVectorsPart.removeAll();
                jPLeftSingVectorsPart.add(lsv);
                jPLeftSingVectorsPart.validate();
            }
        } else {
            GraphPanel linLogTime = CommonResDispTools.createLinLogTimeTraceResidChart(trace, resid, String.valueOf(res.getX2()[xIndex]), false, linPart);
            jPSelectedTimeTrace.removeAll();
            jPSelectedTimeTrace.add(linLogTime);
            jPSelectedTimeTrace.validate();
            if (leftSingVecPart != null) {
                double linPortion = linPart;
                GraphPanel lsv = createLinLogTimePlot(t0Curve[0], linPortion, leftSingVecPart, timePart);
                lsv.getChart().setTitle("Left singular vectors");
                lsv.getChart().getTitle().setFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getFontName(), JFreeChart.DEFAULT_TITLE_FONT.getStyle(), 12));
                jPLeftSingVectorsPart.removeAll();
                jPLeftSingVectorsPart.add(lsv);
                jPLeftSingVectorsPart.validate();
            }
        }
    }

    private void showDispCurve() {
        if (jCBDispCurveShow.isSelected()) {
            XYDataset dispCurve = createDispersionCurve();
            XYLineAndShapeRenderer rendererDisp = new XYLineAndShapeRenderer();

            rendererDisp.setSeriesShapesVisible(0, false);
            rendererDisp.setSeriesPaint(0, Color.BLACK);

            chartMain.getXYPlot().setDataset(1, dispCurve);
            chartMain.getXYPlot().setRenderer(1, (XYItemRenderer) rendererDisp);
            chartMain.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        } else {
            chartMain.getXYPlot().setDataset(1, null);
            chartMain.getXYPlot().setRenderer(1, null);
        }
    }
}
