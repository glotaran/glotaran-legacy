/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.resultdisplayers.flim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import org.jfree.ui.ExtensionFileFilter;
import java.io.IOException;
import org.ujmp.jama.JamaDenseDoubleMatrix2D;
import org.ujmp.core.Matrix;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.logging.Logger;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.core.resultdisplayers.common.panels.CommonResDispTools;
import org.glotaran.core.resultdisplayers.common.panels.SelectTracesForPlot;
import org.glotaran.jfreechartcustom.GraphPanel;
import org.glotaran.jfreechartcustom.GrayPaintScalePlus;
import org.glotaran.jfreechartcustom.ImageUtilities;
import org.glotaran.jfreechartcustom.IntensImageDataset;
import org.glotaran.jfreechartcustom.RainbowPaintScale;
import org.glotaran.jfreechartcustom.RedGreenPaintScale;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDataImageAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;
import static java.lang.Math.max;
import static java.lang.Math.abs;
import static java.lang.Math.floor;

final public class FlimResultsTopComponent extends TopComponent implements ChartMouseListener {

    private static FlimResultsTopComponent instance;
    /**
     * path to the icon used by the component and its open action
     */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "FlimOutputTopComponent";
    private double maxAveLifetime;
    private double minAveLifetime;
    private double[] aveLifetimes;
    private Matrix normAmpl;
    private XYSeriesCollection tracesCollection, residuals;
    private TimpResultDataset res;
    private int selectedietm;
    private int numberOfComponents;
    private int selImWidth, selImHeight;
    private int[] selImInd;
    private IntensImageDataset intensutyImageDataset;
    private ArrayList<Integer> selectedTimeTraces;
    private JFreeChart subchartTimeTrace;
    private TimpResultDataObject dataObject;

    public FlimResultsTopComponent(TimpResultDataObject dataObj) {
        initComponents();
        setToolTipText(NbBundle.getMessage(FlimResultsTopComponent.class, "HINT_FlimOutputTopComponent"));
        setName(dataObj.getName());
        this.dataObject = dataObj;
        res = dataObject.getTimpResultDataset();
//        res.calcRangeInt();
        numberOfComponents = res.getKineticParameters().length / 2;
        double errTau;
        double tau;
        selectedTimeTraces = new ArrayList<Integer>();

//================tab 1=================
//fil in list of estimate lifetimes and errors
        ArrayList<String> paramsList = new ArrayList<String>();
//        Object[] lifetimes = new Object[res.getKineticParameters().length];
        paramsList.add("-- Lifetimes [~s] --");
        for (int i = 0; i < numberOfComponents; i++) {
            tau = 1 / res.getKineticParameters()[i];
//            lifetimes[2 * i] = "Tau" + (i + 1) + "=" + new Formatter().format("%g", tau) + " ns";
            paramsList.add("Tau" + (i + 1) + "=" + new Formatter().format("%g", tau) + " ns");
            errTau = max(abs(tau - (1 / (res.getKineticParameters()[i] + res.getKineticParameters()[i + numberOfComponents]))),
                    abs(tau - (1 / (res.getKineticParameters()[i] - res.getKineticParameters()[i + numberOfComponents]))));
//            lifetimes[2 * i + 1] = "er_tau" + (i + 1) + "=" + new Formatter().format("%g", errTau) + " ns";
            paramsList.add("er_tau" + (i + 1) + "=" + new Formatter().format("%g", errTau) + " ns");
        }
        paramsList.add("-----");
        paramsList.add("RMS =" + (new Formatter().format("%g", res.getRms())).toString());
        jLEstimatedLifetimes.setListData(paramsList.toArray());
        jLEstimatedLifetimes.setVisibleRowCount(paramsList.size());
        jLEstimatedLifetimes.revalidate();

//find rectangle for selected image
        int firstLine = (int) floor(res.getX2()[0] / res.getOrwidth());
        int lastLine = (int) floor(res.getX2()[res.getX2().length - 1] / res.getOrwidth());
        int firstCol = res.getX2().length - 1;
        int lastCol = 0;
        int tempnum;
        for (int i = 0; i < res.getX2().length; i++) {
            tempnum = (int) (res.getX2()[i] - floor(res.getX2()[i] / res.getOrwidth()) * res.getOrwidth());
            if (tempnum < firstCol) {
                firstCol = tempnum;
            }
            if (tempnum > lastCol) {
                lastCol = tempnum;
            }
        }
        selImWidth = lastCol - firstCol + 1;
        selImHeight = lastLine - firstLine + 1;
        selImInd = new int[res.getX2().length];
        int indX, indY;
        for (int i = 0; i < res.getX2().length; i++) {
            indY = (int) floor(res.getX2()[i] / res.getOrwidth()) - firstLine;
            indX = (int) (res.getX2()[i] - floor(res.getX2()[i] / res.getOrwidth()) * res.getOrwidth()) - firstCol;
            selImInd[i] = selImWidth * indY + indX;
        }

//create intence image with selected pixels
        intensutyImageDataset = new IntensImageDataset(res.getOrwidth(), res.getOrheigh(), res.getIntenceIm());
        for (int i = 0; i < res.getX2().length; i++) {
            intensutyImageDataset.SetValue((int) res.getX2()[i], -1);
        }
        PaintScale ps = new GrayPaintScalePlus(res.getMinInt(), res.getMaxInt(), -1);
        JFreeChart intIm = createScatChart(ImageUtilities.createColorCodedImage(intensutyImageDataset, ps), ps, res.getOrwidth(), res.getOrheigh());
        ChartPanel intImPanel = new ChartPanel(intIm);
        intImPanel.setFillZoomRectangle(true);
        intImPanel.setMouseWheelEnabled(true);
        jPIntenceImage.add(intImPanel);
        jTFMinIntence.setText(String.valueOf(res.getMinInt()));
        jTFMaxIntence.setText(String.valueOf(res.getMaxInt()));

//create lifetime image
        Matrix spectraMatrix = new JamaDenseDoubleMatrix2D(res.getSpectra());
        aveLifetimes = MakeFlimImage(res.getKineticParameters(), spectraMatrix, res.getX2().length);
        IntensImageDataset aveLifetimeDataset = new IntensImageDataset(selImHeight, selImWidth, new double[selImWidth * selImHeight]);
        for (int i = 0; i < res.getX2().length; i++) {
            aveLifetimeDataset.SetValue(selImInd[i], aveLifetimes[i]);
        }
        ps = new RainbowPaintScale(minAveLifetime, maxAveLifetime);
        JFreeChart aveLifetimeChart = createScatChart(ImageUtilities.createColorCodedImage(aveLifetimeDataset, ps), ps, selImWidth, selImHeight);
        ChartPanel aveLifetimePanel = new ChartPanel(aveLifetimeChart);
        aveLifetimePanel.setFillZoomRectangle(true);
        aveLifetimePanel.setMouseWheelEnabled(true);
        jPImage.add(aveLifetimePanel);
        jTFMinLifetime.setText("0");
        jTFMaxLifetime.setText((new Formatter().format("%g", maxAveLifetime)).toString()); //valueOf(maxAveLifetime));

// create and plot histogram of average lifetimes
        jPHist.add(updateHistPanel(aveLifetimes, minAveLifetime, maxAveLifetime, 20));
        jTFChNumHist.setText("20");
//create and plot SVD of the residuals
        calculateSVDResiduals();

//================tab 2=================
        for (int i = 0; i < numberOfComponents; i++) {
            jPComponents.add(new ImageHistPanel(
                    "Normalized amplitude comp" + String.valueOf(i + 1) + " tau="
                    + new Formatter().format("%g", 1 / res.getKineticParameters()[i]) + "ns",
                    normAmpl.toDoubleArray()[i], selImInd,
                    //normAmpl.getArray()[i], selImInd,
                    selImHeight, selImWidth,
                    0.0, 1.0, (ChartMouseListener) this));
        }
        tracesCollection = CommonResDispTools.createFitRawTraceCollection(0, 0, res.getX().length, res);
        residuals = CommonResDispTools.createResidTraceCollection(0, 0, res.getX().length, res);
        ChartPanel chpanSelectedTrace = CommonResDispTools.makeLinTimeTraceResidChart(tracesCollection, residuals, new NumberAxis("Time (ns)"), String.valueOf(res.getX2()[0]), false);
        jPSelectedTrace.add(chpanSelectedTrace);
    }

    public TimpResultDataObject getDataObject() {
        return dataObject;
    }

    private FlimResultsTopComponent() {
        maxAveLifetime = 0;
        res = new TimpResultDataset();
        normAmpl = null;
        initComponents();
        setName(NbBundle.getMessage(FlimResultsTopComponent.class, "CTL_FlimOutputTopComponent"));
        setToolTipText(NbBundle.getMessage(FlimResultsTopComponent.class, "HINT_FlimOutputTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLEstimatedLifetimes = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPRightSingVectors = new javax.swing.JPanel();
        jPSingValues = new javax.swing.JPanel();
        jPLeftSingVectors = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPIntenceImage = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTFMaxIntence = new javax.swing.JTextField();
        jTFMinIntence = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jPImage = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTFMaxLifetime = new javax.swing.JTextField();
        jTFMinLifetime = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jPHist = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jTFChNumHist = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPSelectedTrace = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPComponents = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jButton3 = new javax.swing.JButton();
        jBAutoSelTraces = new javax.swing.JButton();
        jPSelTimeTrCollectionTop = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jButton6 = new javax.swing.JButton();
        jBExportTimeTraces = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPSelTimeTrCollection = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jToolBar1.setRollover(true);
        jToolBar1.setPreferredSize(new java.awt.Dimension(500, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jToolBar1, gridBagConstraints);

        jPanel2.setPreferredSize(new java.awt.Dimension(1200, 597));

        jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.PAGE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel5.text")); // NOI18N
        jPanel12.add(jLabel5);

        jScrollPane2.setViewportView(jLEstimatedLifetimes);

        jPanel12.add(jScrollPane2);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel9.setLayout(new java.awt.BorderLayout());

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel4.text")); // NOI18N
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel9.add(jLabel4, java.awt.BorderLayout.CENTER);

        jPRightSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPRightSingVectors.setLayout(new java.awt.GridLayout(1, 0));

        jPSingValues.setBackground(new java.awt.Color(255, 255, 255));
        jPSingValues.setLayout(new java.awt.BorderLayout());

        jPLeftSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPLeftSingVectors.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPLeftSingVectors, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPSingValues, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPRightSingVectors, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPLeftSingVectors, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
            .addComponent(jPSingValues, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
            .addComponent(jPRightSingVectors, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 1009, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel6.text")); // NOI18N

        jPIntenceImage.setBackground(new java.awt.Color(0, 0, 0));
        jPIntenceImage.setMaximumSize(new java.awt.Dimension(100, 100));
        jPIntenceImage.setMinimumSize(new java.awt.Dimension(450, 350));
        jPIntenceImage.setLayout(new java.awt.BorderLayout());

        jPanel4.setPreferredSize(new java.awt.Dimension(293, 25));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jButton1.text")); // NOI18N
        jButton1.setIconTextGap(2);
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTFMaxIntence.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMaxIntence.setText(org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jTFMaxIntence.text")); // NOI18N

        jTFMinIntence.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMinIntence.setText(org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jTFMinIntence.text")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(11, 11, 11)
                .addComponent(jTFMinIntence, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFMaxIntence, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel2)
                .addComponent(jTFMinIntence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jTFMaxIntence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel7)
                .addComponent(jButton1))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, 0, 450, Short.MAX_VALUE)
            .addComponent(jPIntenceImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPIntenceImage, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPImage.setBackground(new java.awt.Color(0, 0, 0));
        jPImage.setMaximumSize(new java.awt.Dimension(450, 350));
        jPImage.setMinimumSize(new java.awt.Dimension(450, 350));
        jPImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPImageMouseClicked(evt);
            }
        });
        jPImage.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jButton2.text")); // NOI18N
        jButton2.setIconTextGap(2);
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTFMaxLifetime.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMaxLifetime.setText(org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jTFMaxLifetime.text")); // NOI18N

        jTFMinLifetime.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMinLifetime.setText(org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jTFMinLifetime.text")); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(11, 11, 11)
                .addComponent(jTFMinLifetime, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFMaxLifetime, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel8)
                .addComponent(jTFMinLifetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jTFMaxLifetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel9)
                .addComponent(jButton2))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPImage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPImage, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPHist.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPHist.setLayout(new java.awt.BorderLayout());

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jLabel15.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton5, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jButton5.text")); // NOI18N
        jButton5.setIconTextGap(2);
        jButton5.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTFChNumHist.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFChNumHist.setText(org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jTFChNumHist.text")); // NOI18N

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(jTFChNumHist, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addGap(21, 21, 21))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel15)
                .addComponent(jTFChNumHist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton5))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
            .addComponent(jPHist, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPHist, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(187, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, 0, 297, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, 0, 297, Short.MAX_VALUE)
                        .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPSelectedTrace.setBackground(new java.awt.Color(255, 255, 255));
        jPSelectedTrace.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPSelectedTrace.setLayout(new java.awt.BorderLayout());

        jPComponents.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPComponents.setPreferredSize(new java.awt.Dimension(500, 4));
        jPComponents.setLayout(new java.awt.GridLayout(3, 1));
        jScrollPane3.setViewportView(jPComponents);

        jToolBar2.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jButton3.text")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton3);

        org.openide.awt.Mnemonics.setLocalizedText(jBAutoSelTraces, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jBAutoSelTraces.text")); // NOI18N
        jBAutoSelTraces.setFocusable(false);
        jBAutoSelTraces.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBAutoSelTraces.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBAutoSelTraces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAutoSelTracesActionPerformed(evt);
            }
        });
        jToolBar2.add(jBAutoSelTraces);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPSelectedTrace, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 1068, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPSelectedTrace, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(315, 315, 315))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPSelTimeTrCollectionTop.setPreferredSize(new java.awt.Dimension(900, 1000));
        jPSelTimeTrCollectionTop.setLayout(new java.awt.GridBagLayout());

        jToolBar3.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jButton6, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jButton6.text")); // NOI18N
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar3.add(jButton6);

        org.openide.awt.Mnemonics.setLocalizedText(jBExportTimeTraces, org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jBExportTimeTraces.text")); // NOI18N
        jBExportTimeTraces.setFocusable(false);
        jBExportTimeTraces.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBExportTimeTraces.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBExportTimeTraces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBExportTimeTracesActionPerformed(evt);
            }
        });
        jToolBar3.add(jBExportTimeTraces);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = -6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPSelTimeTrCollectionTop.add(jToolBar3, gridBagConstraints);

        jPanel13.setLayout(new java.awt.BorderLayout());

        jPSelTimeTrCollection.setLayout(new java.awt.GridLayout(2, 2));
        jScrollPane4.setViewportView(jPSelTimeTrCollection);

        jPanel13.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPSelTimeTrCollectionTop.add(jPanel13, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(FlimResultsTopComponent.class, "FlimResultsTopComponent.jPSelTimeTrCollectionTop.TabConstraints.tabTitle"), jPSelTimeTrCollectionTop); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jPImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPImageMouseClicked
    }//GEN-LAST:event_jPImageMouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int newNumChHish;
        try {
            newNumChHish = Integer.parseInt(jTFChNumHist.getText());
            if (newNumChHish < 1) {
                CoreErrorMessages.selCorrChNum();
                return;
            }
            jPHist.removeAll();
            ChartPanel chpanHist = updateHistPanel(aveLifetimes, minAveLifetime, maxAveLifetime, newNumChHish);
            chpanHist.setSize(jPHist.getSize());
            jPHist.add(chpanHist);
            jPHist.repaint();
        } catch (NumberFormatException ex) {
            CoreErrorMessages.selCorrChNum();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        double newMinAmpl, newMaxAmpl;
        try {
            newMinAmpl = Double.parseDouble(jTFMinIntence.getText());
            newMaxAmpl = Double.parseDouble(jTFMaxIntence.getText());
            PaintScale ps = new GrayPaintScalePlus(newMinAmpl, newMaxAmpl, -1);
            JFreeChart intIm = createScatChart(ImageUtilities.createColorCodedImage(intensutyImageDataset, ps), ps, res.getOrwidth(), res.getOrheigh());
            ChartPanel intImPanel = new ChartPanel(intIm);
            intImPanel.setFillZoomRectangle(true);
            intImPanel.setMouseWheelEnabled(true);
            jPIntenceImage.removeAll();
            intImPanel.setSize(jPIntenceImage.getSize());
            jPIntenceImage.add(intImPanel);
            jPIntenceImage.repaint();

        } catch (NumberFormatException ex) {
            CoreErrorMessages.selCorrChNum();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        double newMinLifetime, newMaxLifetime;
        try {
            newMinLifetime = Double.parseDouble(jTFMinLifetime.getText());
            newMaxLifetime = Double.parseDouble(jTFMaxLifetime.getText());

            IntensImageDataset aveLifetimeDataset = new IntensImageDataset(selImHeight, selImWidth, new double[selImWidth * selImHeight]);
            for (int i = 0; i < res.getX2().length; i++) {
                aveLifetimeDataset.SetValue(selImInd[i], aveLifetimes[i]);
            }
            PaintScale ps = new RainbowPaintScale(newMinLifetime, newMaxLifetime);
            JFreeChart aveLifetimeChart = createScatChart(ImageUtilities.createColorCodedImage(aveLifetimeDataset, ps), ps, selImWidth, selImHeight);
            ChartPanel aveLifetimePanel = new ChartPanel(aveLifetimeChart);
            aveLifetimePanel.setFillZoomRectangle(true);
            aveLifetimePanel.setMouseWheelEnabled(true);
            jPImage.removeAll();
            aveLifetimePanel.setSize(jPImage.getSize());
            jPImage.add(aveLifetimePanel);
            jPImage.repaint();
        } catch (NumberFormatException ex) {
            CoreErrorMessages.selCorrChNum();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        selectedTimeTraces.add(selectedietm);
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot();
        ChartPanel chpan = CommonResDispTools.makeLinTimeTraceResidChart(
                CommonResDispTools.createFitRawTraceCollection(selectedietm, 0, res.getX().length, res),
                CommonResDispTools.createResidTraceCollection(selectedietm, 0, res.getX().length, res),
                new NumberAxis("Time (ns)"),
                String.valueOf(res.getX2()[selectedietm]),
                false);
        chpan.setMinimumDrawHeight(0);
        chpan.setMinimumDrawWidth(0);
        jPSelTimeTrCollection.add(chpan);
        plot.setOrientation(PlotOrientation.VERTICAL);

        CommonResDispTools.checkPanelSize(jPSelTimeTrCollection, selectedTimeTraces.size());

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jBAutoSelTracesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAutoSelTracesActionPerformed
        //create dialog
        SelectTracesForPlot selTracePanel = new SelectTracesForPlot();
        selTracePanel.setMaxNumbers(res.getX2().length, res.getX().length);
        selTracePanel.setEnabledYDimension(false);

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
                int xIndex = 0;
                selectedTimeTraces.clear();
                jPSelTimeTrCollection.removeAll();
                CommonResDispTools.restorePanelSize(jPSelTimeTrCollection);
                CommonResDispTools.checkPanelSize(jPSelTimeTrCollection, numSelTraces);
                NumberAxis xAxis;
                for (int i = 0; i < numSelTraces; i++) {
                    xIndex = i * w;
                    //Add index ot selected trace into listselectedTimeTraces.add(xIndex);

                    //create ChartPanel using xydatasets from above
                    ChartPanel chpan = CommonResDispTools.makeLinTimeTraceResidChart(
                            CommonResDispTools.createFitRawTraceCollection(xIndex, 0, res.getX().length, res),
                            CommonResDispTools.createResidTraceCollection(xIndex, 0, res.getX().length, res),
                            new NumberAxis("Time (ns)"),
                            String.valueOf(res.getX2()[xIndex]),
                            false);
//                //add chartpanel
                    jPSelTimeTrCollection.add(chpan);
                    selectedTimeTraces.add(xIndex);
                }
            }
        }
    }//GEN-LAST:event_jBAutoSelTracesActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jPSelTimeTrCollection.removeAll();
        CommonResDispTools.restorePanelSize(jPSelTimeTrCollection);
        jPSelTimeTrCollection.repaint();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jBExportTimeTracesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportTimeTracesActionPerformed
        // TODO add your handling code here:
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBAutoSelTraces;
    private javax.swing.JButton jBExportTimeTraces;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JList jLEstimatedLifetimes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPComponents;
    private javax.swing.JPanel jPHist;
    private javax.swing.JPanel jPImage;
    private javax.swing.JPanel jPIntenceImage;
    private javax.swing.JPanel jPLeftSingVectors;
    private javax.swing.JPanel jPRightSingVectors;
    private javax.swing.JPanel jPSelTimeTrCollection;
    private javax.swing.JPanel jPSelTimeTrCollectionTop;
    private javax.swing.JPanel jPSelectedTrace;
    private javax.swing.JPanel jPSingValues;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTFChNumHist;
    private javax.swing.JTextField jTFMaxIntence;
    private javax.swing.JTextField jTFMaxLifetime;
    private javax.swing.JTextField jTFMinIntence;
    private javax.swing.JTextField jTFMinLifetime;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link findInstance}.
     */
    public static synchronized FlimResultsTopComponent getDefault() {
        if (instance == null) {
            instance = new FlimResultsTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the FlimOutputTopComponent instance. Never call
     * {@link #getDefault} directly!
     */
    public static synchronized FlimResultsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(FlimResultsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof FlimResultsTopComponent) {
            return (FlimResultsTopComponent) win;
        }
        Logger.getLogger(FlimResultsTopComponent.class.getName()).warning(
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

    /**
     * replaces this in object stream
     */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return FlimResultsTopComponent.getDefault();
        }
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        int index = 0;
        JFreeChart firedChart = event.getChart();
        int mouseX = event.getTrigger().getX();
        int mouseY = event.getTrigger().getY();
        ChartPanel firedPanel = (ChartPanel) event.getTrigger().getSource();
        Point2D p = firedPanel.translateScreenToJava2D(new Point(mouseX, mouseY));
        XYPlot plot = (XYPlot) firedChart.getPlot();
        ChartRenderingInfo info = firedPanel.getChartRenderingInfo();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
        ValueAxis domainAxis = plot.getDomainAxis();
        RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
        ValueAxis rangeAxis = plot.getRangeAxis();
        RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
        int chartX = (int) floor(domainAxis.java2DToValue(p.getX(), dataArea, domainAxisEdge));
        int chartY = (int) floor(rangeAxis.java2DToValue(p.getY(), dataArea, rangeAxisEdge));

        int ind = -1;
        if ((chartX < selImWidth) && (chartY < selImHeight)) {
            index = chartY * selImWidth + chartX;
            if (index > -1) {
                for (int i = 0; i < selImInd.length; i++) {
                    if (index == selImInd[i]) {
                        ind = i;

                    }
                }
                selectedietm = ind;
                if (ind != -1) {
                    tracesCollection = CommonResDispTools.createFitRawTraceCollection(ind, 0, res.getX().length, res);
                    residuals = CommonResDispTools.createResidTraceCollection(ind, 0, res.getX().length, res);
                    ChartPanel chpanSelectedTrace = CommonResDispTools.makeLinTimeTraceResidChart(tracesCollection, residuals, new NumberAxis("Time (ns)"), String.valueOf(res.getX2()[ind]), false);
                    jPSelectedTrace.removeAll();
                    jPSelectedTrace.add(chpanSelectedTrace);
                    jPSelectedTrace.validate();
                }
            }
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
//         System.out.println("ChartMouseMoved");
    }

    private ChartPanel updateHistPanel(double[] data, double minVal, double maxVal, int numPockets) {
        HistogramDataset datasetHist = new HistogramDataset();
        datasetHist.addSeries("seria1", data, numPockets, minAveLifetime, maxAveLifetime);
        JFreeChart charthist = ChartFactory.createHistogram(
                null,
                null,
                null,
                datasetHist,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
        XYPlot plot = (XYPlot) charthist.getPlot();
        plot.setForegroundAlpha(0.85f);
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        return new GraphPanel(charthist);
    }

    private double[] MakeFlimImage(double[] kinpar, Matrix amplitudes, int numOfSelPix) {
        minAveLifetime = Double.MAX_VALUE;
        double[] aveLifeTimes = new double[numOfSelPix];
        normAmpl = MatrixFactory.dense(ValueType.DOUBLE, numberOfComponents, numOfSelPix);
        //normAmpl = new Matrix(numberOfComponents, numOfSelPix);
        double sumOfConc;
        int offset = numberOfComponents < res.getConcentrations().getColumnDimension() ? numberOfComponents + 1 : numberOfComponents;

        for (int i = 0; i < numOfSelPix; i++) {
            aveLifeTimes[i] = 0;
            sumOfConc = 0;
            for (int k = 0; k < numberOfComponents; k++) {
                sumOfConc = sumOfConc + amplitudes.getAsDouble(k + offset, i);
            }
            for (int j = 0; j < numberOfComponents; j++) {
                aveLifeTimes[i] = aveLifeTimes[i] + 1 / kinpar[j] * amplitudes.getAsDouble(j + offset, i) / sumOfConc;
                normAmpl.setAsDouble(amplitudes.getAsDouble(j + offset, i) / sumOfConc, j, i);
            }
            if (maxAveLifetime < aveLifeTimes[i]) {
                maxAveLifetime = aveLifeTimes[i];
            }
            if (minAveLifetime > aveLifeTimes[i]) {
                minAveLifetime = aveLifeTimes[i];
            }
        }
        return aveLifeTimes;
    }

    private void calculateSVDResiduals() {
        int n = 2;
//do SVD

        Matrix[] svdResult = null;
        Matrix residualMatrix = new JamaDenseDoubleMatrix2D(res.getResiduals());
        svdResult = residualMatrix.svd();

//creare collection with first 2 LSV
        XYSeriesCollection lSVCollection = new XYSeriesCollection();
        XYSeries seria;
        for (int j = 0; j < n; j++) {
            seria = new XYSeries("LSV" + String.valueOf(j));
            for (int i = 0; i < res.getX().length; i++) {
                seria.add(res.getX()[i], svdResult[0].getAsDouble((long) i, j));
            }
            lSVCollection.addSeries(seria);
        }

//creare chart for 2 LSV
        JFreeChart tracechart = ChartFactory.createXYLineChart(
                "Left singular vectors",
                "Time (ns)",
                null,
                lSVCollection,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        //tracechart.getTitle().setFont(new Font(tracechart.getTitle().getFont().getFontName(), Font.PLAIN, 12));
        tracechart.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
//        tracechart.getXYPlot().getDomainAxis().setUpperBound(res.getX()[res.getX().length - 1]);
//        tracechart.getXYPlot().getDomainAxis().setAutoRange(true);
        ChartPanel chpan = new GraphPanel(tracechart);
//add chart with 2 LSV to JPannel
        jPLeftSingVectors.removeAll();
        jPLeftSingVectors.add(chpan);

//create images with first 2 RSV
        double[] tempRsingVec = null;
        double minVal = 0;
        double maxVal = 0;
        for (int j = 0; j < n; j++) {
//            seria = new XYSeries("RSV" + (j + 1));
            tempRsingVec = new double[selImWidth * selImHeight];
            minVal = svdResult[2].getMinValue();
            maxVal = svdResult[2].getMaxValue();
            for (int i = 0; i < res.getX2().length; i++) {
                tempRsingVec[selImInd[i]] = svdResult[2].getAsDouble(i, j);
            }

            IntensImageDataset rSingVec = new IntensImageDataset(selImHeight, selImWidth, tempRsingVec);
            PaintScale ps = new RedGreenPaintScale(minVal, maxVal);
            JFreeChart rSingVect = createScatChart(ImageUtilities.createColorCodedImage(rSingVec, ps), ps, selImWidth, selImHeight);
            rSingVect.setTitle("R Singular vector " + String.valueOf(j + 1));
            //rSingVect.getTitle().setFont(new Font(tracechart.getTitle().getFont().getFontName(), Font.PLAIN, 12));
            ChartPanel rSingVectPanel = new ChartPanel(rSingVect);
            rSingVectPanel.setFillZoomRectangle(true);
            rSingVectPanel.setMouseWheelEnabled(true);
            jPRightSingVectors.add(rSingVectPanel);
        }

//creare collection with singular values
        XYSeriesCollection sVCollection = new XYSeriesCollection();

        seria = new XYSeries("SV");
        for (int i = 0; i < svdResult[1].getRowCount(); i++) {
            seria.add(i + 1, svdResult[1].getAsDouble((long) i, (long) i));
        }
        sVCollection.addSeries(seria);

//creare chart for singular values
        tracechart = ChartFactory.createXYLineChart(
                "Screeplot",
                "Singular Value index (n)",
                null,
                sVCollection,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        tracechart.getXYPlot().setRangeAxis(new LogAxis("Log(SVn)"));
        final NumberAxis domainAxis = (NumberAxis) tracechart.getXYPlot().getDomainAxis();
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        //tracechart.getTitle().setFont(new Font(tracechart.getTitle().getFont().getFontName(), Font.PLAIN, 12));
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) tracechart.getXYPlot().getRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setBaseFillPaint(Color.white);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesOutlineStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));

        //tracechart.getTitle().setFont(new Font(tracechart.getTitle().getFont().getFontName(), Font.PLAIN, 12));
        tracechart.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);

        chpan = new GraphPanel(tracechart);
//add chart with singularvalues to JPannel
        jPSingValues.removeAll();
        jPSingValues.add(chpan);
    }

    private JFreeChart createScatChart(BufferedImage image, PaintScale ps, int plotWidth, int plotHeigh) {
        JFreeChart chart_temp = ChartFactory.createScatterPlot(null,
                null, null, new XYSeriesCollection(), PlotOrientation.VERTICAL, false, false,
                false);
        chart_temp.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
        XYDataImageAnnotation ann = new XYDataImageAnnotation(image, 0, 0,
                plotWidth, plotHeigh, true);
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

        NumberAxis scaleAxis = new NumberAxis();
        scaleAxis.setAxisLinePaint(Color.black);
        scaleAxis.setTickMarkPaint(Color.black);
        scaleAxis.setRange(ps.getLowerBound(), ps.getUpperBound());
        scaleAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 9));
        PaintScaleLegend legend = new PaintScaleLegend(ps, scaleAxis);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        legend.setMargin(new RectangleInsets(5, 5, 5, 5));
        legend.setStripWidth(10);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(chart_temp.getBackgroundPaint());
        chart_temp.addSubtitle(legend);

        return chart_temp;
    }
}
