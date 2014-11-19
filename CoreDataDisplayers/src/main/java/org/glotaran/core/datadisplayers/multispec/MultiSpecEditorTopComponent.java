/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.datadisplayers.multispec;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import org.glotaran.core.datadisplayers.common.CommonDataDispTools;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.jfreechartcustom.ColorCodedImageDataset;
import org.glotaran.jfreechartcustom.GraphPanel;
import org.glotaran.jfreechartcustom.GrayPaintScalePlus;
import org.glotaran.jfreechartcustom.HeightMapPanel;
import org.glotaran.jfreechartcustom.ImageCrosshairLabelGenerator;
import org.glotaran.jfreechartcustom.ImageUtilities;
import org.glotaran.jfreechartcustom.IntensImageDataset;
import org.glotaran.jfreechartcustom.LinLogFormat;
import org.glotaran.jfreechartcustom.NonLinearNumberTickUnit;
import org.glotaran.jfreechartcustom.RainbowPaintScale;
import org.glotaran.jfreechartcustom.RedGreenPaintScale;
import org.jfree.chart.ChartFactory;
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.CloneableTopComponent;
import org.ujmp.core.Matrix;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.glotaran.core.datadisplayers.multispec//MultiSpecEditor//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "MultiSpecEditorTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.glotaran.core.datadisplayers.multispec.MultiSpecEditorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MultiSpecEditorAction",
        preferredID = "MultiSpecEditorTopComponent"
)
@Messages({
    "CTL_MultiSpecEditorAction=MultiSpecEditor",
    "CTL_MultiSpecEditorTopComponent=MultiSpecEditor Window",
    "HINT_MultiSpecEditorTopComponent=This is a MultiSpecEditor window"
})
public final class MultiSpecEditorTopComponent extends TopComponent implements ChartChangeListener {
    private final static long serialVersionUID = 1L;
    private static MultiSpecEditorTopComponent instance;
    /** path to the icon used by the component and its open action */
    //    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "MultiSpecEditorTopComponent";
    private TgdDataObject dataObject;
    private TimpDatasetDataObject dataObject2;
    private int MAX_NUMBER_SINGULAR_VALUES = 20;
    private int MAX_NO_TICKS = 6;
    private DatasetTimp data;
    private ColorCodedImageDataset dataset;
    private JFreeChart chartMultiSpec;
    private JFreeChart subchartVerticalCutTrace;
    private JFreeChart subchartHorisontalTrace;
    private JFreeChart subchartTimeTrace;
    private Crosshair crhVerticalCut;
    private Crosshair crhHorisontalCut;
    private Crosshair crhTimeSlice;
    private ChartPanel chartPanelMultiSpec;
    private Range lastXRange;
    private Range lastYRange;
    private Range wholeXRange;
    private Range wholeYRange;
    private Matrix[] svdResult;    
    private JFreeChart leftSVChart;
    private JFreeChart rightSVChart;

    public MultiSpecEditorTopComponent() {
        initComponents();

    }

    public MultiSpecEditorTopComponent(DatasetTimp timpDataFile, TgdDataObject dataObj) {
        initComponents();
        dataObject = dataObj;
        setName(timpDataFile.getDatasetName());
        data = timpDataFile;
        MakeImageChart(MakeXYZDataset());
        updateFileInfo();
        
    }

    public TgdDataObject getDataObject() {
        return dataObject;
    }

    public TimpDatasetDataObject getDataObject2() {
        return dataObject2;
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
        jpDataPannel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jpDataPanelInner = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jpTimeeTrace = new javax.swing.JPanel();
        jSVerticalCut = new javax.swing.JSlider();
        jpHorisontalCut = new javax.swing.JPanel();
        jsTimeSlice = new javax.swing.JSlider();
        jpMultiSpecImage = new javax.swing.JPanel();
        jsHorisontalCut = new javax.swing.JSlider();
        jpVerticalCut = new javax.swing.JPanel();
        jSPInfoPane = new javax.swing.JScrollPane();
        jTAInfo = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTFMinIntence = new javax.swing.JTextField();
        rangeSlider = new com.jidesoft.swing.RangeSlider();
        jTFMaxIntence = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jtbIntegrateMap = new javax.swing.JToggleButton();
        jcbColorScale = new javax.swing.JComboBox();
        jpSVDResults = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jLabel10 = new javax.swing.JLabel();
        jSnumSV = new javax.swing.JSpinner();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jLabel11 = new javax.swing.JLabel();
        jTFtotalNumSV = new javax.swing.JTextField();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jPanel10 = new javax.swing.JPanel();
        jPSingValues = new javax.swing.JPanel();
        jPLeftSingVectors = new javax.swing.JPanel();
        jPRightSingVectors = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(500, 300));
        setPreferredSize(new java.awt.Dimension(1000, 600));
        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(500, 300));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(1000, 600));

        jpDataPannel.setMinimumSize(new java.awt.Dimension(500, 300));
        jpDataPannel.setPreferredSize(new java.awt.Dimension(1000, 600));
        jpDataPannel.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(1000, 640));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1000, 640));

        jpDataPanelInner.setMinimumSize(new java.awt.Dimension(1000, 570));
        jpDataPanelInner.setPreferredSize(new java.awt.Dimension(1000, 570));
        jpDataPanelInner.setLayout(new java.awt.GridBagLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(960, 280));
        jPanel1.setPreferredSize(new java.awt.Dimension(960, 280));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jpTimeeTrace.setBackground(new java.awt.Color(255, 255, 204));
        jpTimeeTrace.setMinimumSize(new java.awt.Dimension(573, 250));
        jpTimeeTrace.setName(""); // NOI18N
        jpTimeeTrace.setPreferredSize(new java.awt.Dimension(573, 200));

        javax.swing.GroupLayout jpTimeeTraceLayout = new javax.swing.GroupLayout(jpTimeeTrace);
        jpTimeeTrace.setLayout(jpTimeeTraceLayout);
        jpTimeeTraceLayout.setHorizontalGroup(
            jpTimeeTraceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 573, Short.MAX_VALUE)
        );
        jpTimeeTraceLayout.setVerticalGroup(
            jpTimeeTraceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 23, 0, 0);
        jPanel1.add(jpTimeeTrace, gridBagConstraints);

        jSVerticalCut.setValue(0);
        jSVerticalCut.setMinimumSize(new java.awt.Dimension(450, 23));
        jSVerticalCut.setPreferredSize(new java.awt.Dimension(450, 23));
        jSVerticalCut.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSVerticalCutStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jSVerticalCut, gridBagConstraints);

        jpHorisontalCut.setBackground(new java.awt.Color(255, 255, 255));
        jpHorisontalCut.setMinimumSize(new java.awt.Dimension(450, 250));
        jpHorisontalCut.setPreferredSize(new java.awt.Dimension(450, 200));

        javax.swing.GroupLayout jpHorisontalCutLayout = new javax.swing.GroupLayout(jpHorisontalCut);
        jpHorisontalCut.setLayout(jpHorisontalCutLayout);
        jpHorisontalCutLayout.setHorizontalGroup(
            jpHorisontalCutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );
        jpHorisontalCutLayout.setVerticalGroup(
            jpHorisontalCutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jpHorisontalCut, gridBagConstraints);

        jsTimeSlice.setValue(0);
        jsTimeSlice.setMinimumSize(new java.awt.Dimension(573, 23));
        jsTimeSlice.setPreferredSize(new java.awt.Dimension(450, 23));
        jsTimeSlice.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsTimeSliceStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 23, 0, 0);
        jPanel1.add(jsTimeSlice, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jpDataPanelInner.add(jPanel1, gridBagConstraints);

        jpMultiSpecImage.setBackground(new java.awt.Color(0, 0, 0));
        jpMultiSpecImage.setMinimumSize(new java.awt.Dimension(450, 400));
        jpMultiSpecImage.setPreferredSize(new java.awt.Dimension(450, 400));

        javax.swing.GroupLayout jpMultiSpecImageLayout = new javax.swing.GroupLayout(jpMultiSpecImage);
        jpMultiSpecImage.setLayout(jpMultiSpecImageLayout);
        jpMultiSpecImageLayout.setHorizontalGroup(
            jpMultiSpecImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );
        jpMultiSpecImageLayout.setVerticalGroup(
            jpMultiSpecImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jpDataPanelInner.add(jpMultiSpecImage, gridBagConstraints);

        jsHorisontalCut.setOrientation(javax.swing.JSlider.VERTICAL);
        jsHorisontalCut.setValue(0);
        jsHorisontalCut.setPreferredSize(new java.awt.Dimension(23, 400));
        jsHorisontalCut.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsHorisontalCutStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jpDataPanelInner.add(jsHorisontalCut, gridBagConstraints);

        jpVerticalCut.setBackground(new java.awt.Color(255, 255, 255));
        jpVerticalCut.setPreferredSize(new java.awt.Dimension(300, 400));
        jpVerticalCut.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jpDataPanelInner.add(jpVerticalCut, gridBagConstraints);

        jSPInfoPane.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jSPInfoPane.border.title"))); // NOI18N
        jSPInfoPane.setMinimumSize(new java.awt.Dimension(260, 220));
        jSPInfoPane.setPreferredSize(new java.awt.Dimension(260, 270));

        jTAInfo.setColumns(20);
        jTAInfo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTAInfo.setRows(5);
        jTAInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jTAInfo.border.title"))); // NOI18N
        jTAInfo.setMinimumSize(new java.awt.Dimension(200, 400));
        jTAInfo.setPreferredSize(new java.awt.Dimension(248, 250));
        jSPInfoPane.setViewportView(jTAInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jpDataPanelInner.add(jSPInfoPane, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(250, 100));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel1, gridBagConstraints);

        jTFMinIntence.setText(org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jTFMinIntence.text")); // NOI18N
        jTFMinIntence.setMaximumSize(new java.awt.Dimension(50, 20));
        jTFMinIntence.setMinimumSize(new java.awt.Dimension(50, 20));
        jTFMinIntence.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel2.add(jTFMinIntence, gridBagConstraints);

        rangeSlider.setMaximumSize(new java.awt.Dimension(200, 43));
        rangeSlider.setMinimumSize(new java.awt.Dimension(50, 43));
        rangeSlider.setPreferredSize(new java.awt.Dimension(50, 43));
        rangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rangeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(rangeSlider, gridBagConstraints);

        jTFMaxIntence.setText(org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jTFMaxIntence.text")); // NOI18N
        jTFMaxIntence.setMaximumSize(new java.awt.Dimension(60, 20));
        jTFMaxIntence.setMinimumSize(new java.awt.Dimension(50, 20));
        jTFMaxIntence.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel2.add(jTFMaxIntence, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jButton1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jpDataPanelInner.add(jPanel2, gridBagConstraints);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jpDataPanelInner.add(jPanel4, gridBagConstraints);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jpDataPanelInner.add(jPanel5, gridBagConstraints);

        jScrollPane1.setViewportView(jpDataPanelInner);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jpDataPannel.add(jScrollPane1, gridBagConstraints);

        jToolBar1.setRollover(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(200, 23));
        jToolBar1.setMinimumSize(new java.awt.Dimension(200, 23));
        jToolBar1.setPreferredSize(new java.awt.Dimension(200, 23));

        org.openide.awt.Mnemonics.setLocalizedText(jtbIntegrateMap, org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jtbIntegrateMap.text")); // NOI18N
        jtbIntegrateMap.setFocusable(false);
        jtbIntegrateMap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbIntegrateMap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbIntegrateMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbIntegrateMapActionPerformed(evt);
            }
        });
        jToolBar1.add(jtbIntegrateMap);

        jcbColorScale.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rainbow Scale", "RedGrin Scale", "Gray Scale" }));
        jcbColorScale.setMaximumSize(new java.awt.Dimension(100, 20));
        jcbColorScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbColorScaleActionPerformed(evt);
            }
        });
        jToolBar1.add(jcbColorScale);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jpDataPannel.add(jToolBar1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jpDataPannel.TabConstraints.tabTitle"), jpDataPannel); // NOI18N

        jpSVDResults.setMinimumSize(new java.awt.Dimension(500, 300));
        jpSVDResults.setPreferredSize(new java.awt.Dimension(1000, 600));
        jpSVDResults.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jpSVDResultsComponentShown(evt);
            }
        });
        jpSVDResults.setLayout(new java.awt.GridBagLayout());

        jToolBar3.setRollover(true);
        jToolBar3.setMinimumSize(new java.awt.Dimension(438, 23));
        jToolBar3.setPreferredSize(new java.awt.Dimension(460, 23));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jLabel10.text")); // NOI18N
        jToolBar3.add(jLabel10);

        jSnumSV.setMaximumSize(new java.awt.Dimension(45, 20));
        jSnumSV.setMinimumSize(new java.awt.Dimension(45, 20));
        jSnumSV.setPreferredSize(new java.awt.Dimension(45, 20));
        jSnumSV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSnumSVStateChanged(evt);
            }
        });
        jToolBar3.add(jSnumSV);
        jToolBar3.add(jSeparator5);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jLabel11.text")); // NOI18N
        jToolBar3.add(jLabel11);

        jTFtotalNumSV.setEditable(false);
        jTFtotalNumSV.setText(org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jTFtotalNumSV.text")); // NOI18N
        jTFtotalNumSV.setMaximumSize(new java.awt.Dimension(100, 20));
        jTFtotalNumSV.setMinimumSize(new java.awt.Dimension(45, 20));
        jTFtotalNumSV.setPreferredSize(new java.awt.Dimension(55, 20));
        jToolBar3.add(jTFtotalNumSV);
        jToolBar3.add(jSeparator6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jpSVDResults.add(jToolBar3, gridBagConstraints);

        jPanel10.setMinimumSize(new java.awt.Dimension(1000, 570));
        jPanel10.setPreferredSize(new java.awt.Dimension(500, 500));
        jPanel10.setLayout(new java.awt.GridBagLayout());

        jPSingValues.setBackground(new java.awt.Color(255, 255, 255));
        jPSingValues.setMinimumSize(new java.awt.Dimension(50, 50));
        jPSingValues.setPreferredSize(new java.awt.Dimension(100, 100));
        jPSingValues.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel10.add(jPSingValues, gridBagConstraints);

        jPLeftSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPLeftSingVectors.setMinimumSize(new java.awt.Dimension(100, 100));
        jPLeftSingVectors.setPreferredSize(new java.awt.Dimension(100, 100));
        jPLeftSingVectors.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel10.add(jPLeftSingVectors, gridBagConstraints);

        jPRightSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPRightSingVectors.setMinimumSize(new java.awt.Dimension(100, 100));
        jPRightSingVectors.setPreferredSize(new java.awt.Dimension(100, 100));
        jPRightSingVectors.setLayout(new java.awt.GridLayout(2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel10.add(jPRightSingVectors, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(MultiSpecEditorTopComponent.class, "MultiSpecEditorTopComponent.jLabel5.text")); // NOI18N
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel10.add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jpSVDResults.add(jPanel10, gridBagConstraints);

        jTabbedPane1.addTab("SVD", jpSVDResults);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jSnumSVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSnumSVStateChanged
//        updateSVDPlots();
    }//GEN-LAST:event_jSnumSVStateChanged

    private void jpSVDResultsComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jpSVDResultsComponentShown

//        if (svdResult == null) {
//            SwingWorker<Matrix[], Void> worker = new SwingWorker<Matrix[], Void>() {
//
//                final ProgressHandle ph = ProgressHandleFactory.createHandle("Performing Singular Value Decomposition on dataset");
//
//                @Override
//                protected Matrix[] doInBackground() throws Exception {
//                    ph.start();
//                    return calculateSVD();
//                }
//
//                @Override
//                protected void done() {
//                    createSVDPlots();
//                    ph.finish();
//                }
//            };
//            worker.execute();
//        }
    }//GEN-LAST:event_jpSVDResultsComponentShown

    private void rangeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rangeSliderStateChanged
        double newMinAmpl, newMaxAmpl;
        Double range = (data.getMaxInt() - data.getMinInt());
        newMinAmpl = data.getMinInt() + range / (rangeSlider.getMaximum() - rangeSlider.getMinimum()) * rangeSlider.getLowValue();
        newMaxAmpl = data.getMinInt() + range / (rangeSlider.getMaximum() - rangeSlider.getMinimum()) * rangeSlider.getHighValue();
        if (newMinAmpl < newMaxAmpl) {
            try {
                updateImagePlot(newMinAmpl, newMaxAmpl);

                jTFMinIntence.setText(String.valueOf(newMinAmpl));
                jTFMaxIntence.setText(String.valueOf(newMaxAmpl));

            } catch (NumberFormatException ex) {
                CoreErrorMessages.numberFormatException();
            }
        }
    }//GEN-LAST:event_rangeSliderStateChanged

    private void jsHorisontalCutStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsHorisontalCutStateChanged

//        crhHorisontalCut.setValue(data.getOriginalHeight() - jsHorisontalCut.getValue() - 1);
        crhHorisontalCut.setValue(jsHorisontalCut.getValue());
        int xIndex = jsHorisontalCut.getValue();
        XYDataset d = ImageUtilities.extractRowFromImageDataset(dataset, xIndex, "Spec");
        subchartHorisontalTrace.getXYPlot().setDataset(d);
        subchartTimeTrace.getXYPlot().setDataset(extractTimeTraceFromData(xIndex, jSVerticalCut.getValue(), "timetrace"));
    }//GEN-LAST:event_jsHorisontalCutStateChanged

    private void jSVerticalCutStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSVerticalCutStateChanged
        int xIndex = jSVerticalCut.getValue();
        crhVerticalCut.setValue(xIndex);
        XYDataset d = ImageUtilities.extractColumnFromImageDataset(dataset, xIndex, "Spec");
        subchartVerticalCutTrace.getXYPlot().setDataset(d);
        subchartTimeTrace.getXYPlot().setDataset(extractTimeTraceFromData(jsHorisontalCut.getValue(), xIndex, "timetrace"));
    }//GEN-LAST:event_jSVerticalCutStateChanged

    private void jsTimeSliceStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsTimeSliceStateChanged
        crhTimeSlice.setValue(data.getX()[jsTimeSlice.getValue()]);
        int xIndex = jsTimeSlice.getValue();
        for (int j = 0; j < data.getOriginalHeight(); j++) {
            for (int i = 0; i < data.getOriginalWidth(); i++) {
                data.getIntenceIm()[j * data.getOriginalWidth() + i] = data.getPsisim()[(j * data.getOriginalWidth() + i) * data.getNt() + xIndex];
            }
        }
        MakeXYZDataset();    
        updateImagePlot(data.getMinInt(), data.getMaxInt());
    }//GEN-LAST:event_jsTimeSliceStateChanged

    private void jtbIntegrateMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbIntegrateMapActionPerformed
        jsTimeSlice.setEnabled(!jtbIntegrateMap.isSelected());
        if (jtbIntegrateMap.isSelected()) {
            data.buildIntMap(1);
        } else {
            data.buildIntMap(0);
        }
        MakeXYZDataset();
        updateImagePlot(data.getMinInt(), data.getMaxInt());
      
        XYDataset d = ImageUtilities.extractColumnFromImageDataset(dataset, jSVerticalCut.getValue(), "Spec");
        subchartVerticalCutTrace.getXYPlot().setDataset(d);
        d = ImageUtilities.extractRowFromImageDataset(dataset, jsHorisontalCut.getValue(), "Spec");
        subchartHorisontalTrace.getXYPlot().setDataset(d);
        

    }//GEN-LAST:event_jtbIntegrateMapActionPerformed

    private void jcbColorScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbColorScaleActionPerformed
        updateImagePlot(data.getMinInt(), data.getMaxInt());
        
    }//GEN-LAST:event_jcbColorScaleActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPLeftSingVectors;
    private javax.swing.JPanel jPRightSingVectors;
    private javax.swing.JPanel jPSingValues;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jSPInfoPane;
    private javax.swing.JSlider jSVerticalCut;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JSpinner jSnumSV;
    private javax.swing.JTextArea jTAInfo;
    private javax.swing.JTextField jTFMaxIntence;
    private javax.swing.JTextField jTFMinIntence;
    private javax.swing.JTextField jTFtotalNumSV;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JComboBox jcbColorScale;
    private javax.swing.JPanel jpDataPanelInner;
    private javax.swing.JPanel jpDataPannel;
    private javax.swing.JPanel jpHorisontalCut;
    private javax.swing.JPanel jpMultiSpecImage;
    private javax.swing.JPanel jpSVDResults;
    private javax.swing.JPanel jpTimeeTrace;
    private javax.swing.JPanel jpVerticalCut;
    private javax.swing.JSlider jsHorisontalCut;
    private javax.swing.JSlider jsTimeSlice;
    private javax.swing.JToggleButton jtbIntegrateMap;
    private com.jidesoft.swing.RangeSlider rangeSlider;
    // End of variables declaration//GEN-END:variables
     
    @Override
    public int getPersistenceType() {
        return CloneableTopComponent.PERSISTENCE_NEVER;
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

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    private JFreeChart createChart(XYDataset dataset1) {
        JFreeChart chart_temp = ChartFactory.createScatterPlot(null,
                null, null, dataset1, PlotOrientation.VERTICAL, false, false,
                false);

        double range = Math.abs(data.getMaxInt() - data.getMinInt());
        double dataMin, dataMax;
        if (range == 0.0) {
            dataMin = data.getMinInt() - 0.1;
            dataMax = data.getMaxInt() + 0.1;
        } else {
            dataMin = data.getMinInt();
            dataMax = data.getMaxInt();
        }
        PaintScale ps = new RainbowPaintScale(dataMin, dataMax);
//        PaintScale ps = new RedGreenPaintScale(dataMin, dataMax);
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
    
    private void MakeImageChart(ColorCodedImageDataset dataset) {
        double range = Math.abs(data.getMaxInt() - data.getMinInt());
        double dataMin, dataMax;
        if (range == 0.0) {
            dataMin = data.getMinInt() - 0.1;
            dataMax = data.getMaxInt() + 0.1;
        } else {
            dataMin = data.getMinInt();
            dataMax = data.getMaxInt();
        }
        PaintScale ps = new RainbowPaintScale(dataMin, dataMax);
        this.chartMultiSpec = createChart(new XYSeriesCollection());
        this.chartMultiSpec.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);

        XYPlot tempPlot = (XYPlot) this.chartMultiSpec.getPlot();
        this.wholeXRange = tempPlot.getDomainAxis().getRange();
        this.wholeYRange = tempPlot.getRangeAxis().getRange();
        
        //added axes labeles on the image
//        DecimalFormat formatter = new DecimalFormat("##0E0");
//        NumberAxis xAxis = new NumberAxis("Wavelength → ");
//
//        
//          double[] x2values = data.getIntenceImY();
//        double x2range = Math.abs(x2values[0] - x2values[x2values.length - 1]);
//        if (x2range<=0) {
//            for (int i = 0; i < x2values.length; i++) {
//                x2values[i]=i;
//            }
//        }
//        
//        int numberOfTicks = Math.min(data.getIntenceImY().length,MAX_NO_TICKS);
//        NonLinearNumberTickUnit xTickUnit = new NonLinearNumberTickUnit(x2values.length/numberOfTicks, formatter,x2values);
//        xAxis.setTickUnit(xTickUnit);
//        xAxis.setTickLabelsVisible(true);
//        tempPlot.setDomainAxis(xAxis);
//        
//        NumberAxis yAxis = new NumberAxis("← Wavelength");
//
//        formatter = new DecimalFormat("##0.#E0");
//        numberOfTicks = Math.min(data.getX3().length,MAX_NO_TICKS);
//         NonLinearNumberTickUnit yTickUnit = new NonLinearNumberTickUnit(data.getX3().length/numberOfTicks, formatter,data.getX3(),false);
//        yAxis.setTickUnit(yTickUnit);
//        yAxis.setTickLabelsVisible(true);
//        tempPlot.setRangeAxis(yAxis);
        
        chartPanelMultiSpec = new HeightMapPanel(chartMultiSpec,true);
        chartPanelMultiSpec.setFillZoomRectangle(true);
        chartPanelMultiSpec.setMouseWheelEnabled(true);
        chartPanelMultiSpec.setZoomFillPaint(new Color(68, 68, 78, 63));
        jpMultiSpecImage.removeAll();
//        chpanImage.setSize(jPSpecImage.getMaximumSize());
        jpMultiSpecImage.setLayout(new BorderLayout());

        ImageCrosshairLabelGenerator crossLabGenVerticalCut = new ImageCrosshairLabelGenerator(data.getIntenceImY(), false);
        ImageCrosshairLabelGenerator crossLabGenHorisontalCut = new ImageCrosshairLabelGenerator(data.getIntenceImX(), false);
        ImageCrosshairLabelGenerator crossLabGenTimeSlise = new ImageCrosshairLabelGenerator(data.getX(), false);
       
        crhVerticalCut = createCroshair(Color.red, crossLabGenVerticalCut);
        crhHorisontalCut = createCroshair(Color.gray, crossLabGenHorisontalCut);
        crhTimeSlice = createCroshair(Color.red, null);
        
        CrosshairOverlay overlay = new CrosshairOverlay();
        overlay.addDomainCrosshair(crhVerticalCut);
        overlay.addRangeCrosshair(crhHorisontalCut);
        chartPanelMultiSpec.addOverlay(overlay);
        jpMultiSpecImage.add(chartPanelMultiSpec);
        //TODO: auto scale the JSlider jSColum to the size of the chart
        //chpanImage.getChartRenderingInfo().getChartArea().getWidth();
        //jSColum.setBounds(jSColum.getBounds().x, jSColum.getBounds().y,(int)chpanImage.getChartRenderingInfo().getChartArea().getBounds().width,jSColum.getHeight());

        chartMultiSpec.addChangeListener((ChartChangeListener) this);
        
        subchartVerticalCutTrace = createXYPlot(PlotOrientation.HORIZONTAL,AxisLocation.BOTTOM_OR_RIGHT, data.getIntenceImX(),jpVerticalCut, false, null);
        subchartHorisontalTrace = createXYPlot(PlotOrientation.VERTICAL,AxisLocation.BOTTOM_OR_RIGHT, data.getIntenceImY(),jpHorisontalCut, false, null);
        
        CrosshairOverlay overlayTime = new CrosshairOverlay();
        overlayTime.addDomainCrosshair(crhTimeSlice);
        
        subchartTimeTrace = createXYPlot(PlotOrientation.VERTICAL,AxisLocation.BOTTOM_OR_LEFT, data.getX(),jpTimeeTrace, false, overlayTime);
        
        NumberAxis scaleAxis = new NumberAxis();
        scaleAxis.setAxisLinePaint(Color.black);
        scaleAxis.setTickMarkPaint(Color.black);
        scaleAxis.setRange(data.getMinInt(), data.getMaxInt());
        scaleAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
        PaintScaleLegend legend = new PaintScaleLegend(ps, scaleAxis);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        legend.setMargin(new RectangleInsets(5, 5, 5, 5));
        legend.setStripWidth(15);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(chartMultiSpec.getBackgroundPaint());
        chartMultiSpec.addSubtitle(legend);

        this.chartMultiSpec.addChangeListener((ChartChangeListener) this);
        jSVerticalCut.setValueIsAdjusting(true);
        jSVerticalCut.setMaximum(dataset.GetImageWidth() - 1);
        jSVerticalCut.setMinimum(0);
//        jSVerticalCut.setValue(0);
        jSVerticalCut.setValueIsAdjusting(false);

        jsHorisontalCut.setValueIsAdjusting(true);
        jsHorisontalCut.setMaximum(dataset.GetImageHeigth() - 1);
        jsHorisontalCut.setMinimum(0);
        jsHorisontalCut.setValueIsAdjusting(false);
        
        jsTimeSlice.setValueIsAdjusting(true);
        jsTimeSlice.setMaximum(data.getNt()-1);
        jsTimeSlice.setMinimum(0);
        jsTimeSlice.setValueIsAdjusting(false);
        
    }
    
    private Crosshair createCroshair(Color crhColor, ImageCrosshairLabelGenerator lebelGenereator){
        Crosshair crosshair = new Crosshair(0.0);
        crosshair.setPaint(crhColor);
        if (lebelGenereator!=null){ 
            crosshair.setLabelGenerator(lebelGenereator);
        }
        crosshair.setLabelVisible(true);
        crosshair.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        crosshair.setLabelBackgroundPaint(new Color(255, 255, 0, 100));
        return crosshair;
    }
    
    private JFreeChart createXYPlot(PlotOrientation orient, AxisLocation axLoc, double[] axisValues, JPanel panel, boolean domInvert, CrosshairOverlay overlay){
        JFreeChart subchart;
        XYSeriesCollection chartDataset = new XYSeriesCollection();
        subchart = ChartFactory.createXYLineChart(
                null,
                null,
                null,
                chartDataset,
                orient,
                false,
                false,
                false);
        if (axisValues[axisValues.length - 1] < axisValues[0]) {
            subchart.getXYPlot().getDomainAxis().setUpperBound(axisValues[0]);
            subchart.getXYPlot().getDomainAxis().setInverted(domInvert);
        } else {
            subchart.getXYPlot().getDomainAxis().setUpperBound(axisValues[axisValues.length - 1]);
        }

        XYPlot plot = (XYPlot) subchart.getPlot();
        plot.getDomainAxis().setLowerMargin(0.0);
        plot.getDomainAxis().setUpperMargin(0.0);
        plot.getDomainAxis().setAutoRange(true);
        plot.setDomainAxisLocation(axLoc);
        plot.setRangeAxisLocation(axLoc);
        plot.getDomainAxis().setInverted(domInvert);

        
        GraphPanel subchartPanel = new GraphPanel(subchart);
        if (overlay != null){
            subchartPanel.addOverlay(overlay);
        }
        panel.removeAll();
        panel.setLayout(new BorderLayout());
        subchartPanel.setMinimumDrawHeight(0);
        subchartPanel.setMinimumDrawWidth(0);
        panel.add(subchartPanel);
        return subchart;
        
    }
    
    protected void updateFileInfo() {
        String tempString;
        jTAInfo.removeAll();
        tempString = "File name: " + data.getDatasetName() + "\n";
        jTAInfo.append(tempString);
        tempString = "Time window: " + String.valueOf(data.getX()[data.getNt() - 1] - data.getX()[0]) + "\n";
        jTAInfo.append(tempString);
        tempString = "Nuber of time steps: " + String.valueOf(data.getNt()) + "\n";
        jTAInfo.append(tempString);
        tempString = "Image window: " + String.valueOf(data.getIntenceImY()[data.getOriginalWidth() - 1] - data.getIntenceImY()[0]) + " x " + String.valueOf(data.getIntenceImX()[data.getOriginalHeight()- 1] - data.getIntenceImX()[0]) + "\n";
        jTAInfo.append(tempString);
        tempString = "Image size: " + String.valueOf(data.getOriginalWidth()) + " x " + String.valueOf(data.getOriginalHeight()) + "\n";
        jTAInfo.append(tempString);

        jTFMaxIntence.setText(String.valueOf(data.getMaxInt()));
        jTFMinIntence.setText(String.valueOf(data.getMinInt()));
        rangeSlider.setMinimum(0);
        rangeSlider.setMaximum(99);
        rangeSlider.setLowValue(0);
        rangeSlider.setHighValue(99);
        rangeSlider.setRangeDraggable(true);

        rangeSlider.setPaintLabels(true);
        Hashtable labels = new Hashtable();
        double iLabel;
        LinLogFormat twoPlaces = new LinLogFormat();
        twoPlaces.setMinLinearDisplayValue(0.01);
        twoPlaces.setMaxLinearDisplayValue(1000);
        twoPlaces.setSignificandFormat(new DecimalFormat("0.##"));
        twoPlaces.setExponentFormat(new DecimalFormat("0.#"));
        for (int i = 0; i <= 5; i++) {
            iLabel = (data.getMinInt() + (data.getMaxInt() - data.getMinInt()) / 5 * (i));
            labels.put(new Integer(i * 20), new JLabel(twoPlaces.format(iLabel)));
        }
        rangeSlider.setLabelTable(labels);
        rangeSlider.setPaintTicks(true);
    }
    
    private ColorCodedImageDataset MakeXYZDataset() {
        dataset = new ColorCodedImageDataset(data.getOriginalWidth(),data.getOriginalHeight(),
                data.getIntenceIm(), data.getIntenceImY(), data.getIntenceImX(), false);
        return dataset;
    }
    
    public XYDataset extractTimeTraceFromData(int row, int col, Comparable seriesName) {
        XYSeries series = new XYSeries(seriesName);
        for (int i = 0; i < data.getNt(); i++) {
            series.add(data.getX()[i] ,data.getPsisim()[(row*data.getOriginalWidth()+col)*data.getNt()+i]);
        }
        XYSeriesCollection result = new XYSeriesCollection(series);
        return result;
    }
    
    private void updateImagePlot(double minAmp, double maxAmp) {
        PaintScale ps;
        switch (jcbColorScale.getSelectedIndex()){
            case 0: {
                ps = new RainbowPaintScale(minAmp, maxAmp);
                break;
            }
            case 1: {
                ps = new RedGreenPaintScale(data.getMinInt(), data.getMaxInt());
                break;
            }
            case 2: {
                ps = new GrayPaintScale();
                break;
            }
            default: {
                ps = new RainbowPaintScale(minAmp, maxAmp);
                break;
            }
        }
         
                             
//        PaintScale ps = new RedGreenPaintScale(data.getMinInt(), data.getMaxInt());

        BufferedImage image = ImageUtilities.createColorCodedImage(dataset, ps,false,true);
        XYDataImageAnnotation ann = new XYDataImageAnnotation(image, 0, 0,
                dataset.GetImageWidth(), dataset.GetImageHeigth(), true);

        XYPlot plot = (XYPlot) chartMultiSpec.getPlot();
        plot.getRenderer().removeAnnotations();
        plot.getRenderer().addAnnotation(ann, Layer.BACKGROUND);

        ((PaintScaleLegend) chartMultiSpec.getSubtitle(0)).setScale(ps);
        ((PaintScaleLegend) chartMultiSpec.getSubtitle(0)).getAxis().setRange(minAmp, maxAmp);

        
        
    }
    
    private void updateSVDPlots() {
        XYSeriesCollection lSVCollection = new XYSeriesCollection();
        XYSeries seria;
        for (int j = 0; j < (Integer) jSnumSV.getValue(); j++) {
            seria = new XYSeries("LSV" + j + 1);
            for (int i = 0; i < data.getNt(); i++) {
                seria.add(data.getX()[i], svdResult[0].getAsDouble((long) i, j));
            }
            lSVCollection.addSeries(seria);
        }
        leftSVChart.getXYPlot().setDataset(lSVCollection);
        
        double[] tempRsingVec = null;
        double minVal = 0;
        double maxVal = 0;
        
        if (jPRightSingVectors.getComponentCount()<(Integer)jSnumSV.getValue()){
            double tempValue;
            for (int i = jPRightSingVectors.getComponentCount(); i < (Integer) jSnumSV.getValue(); i++) {
                tempRsingVec = new double[data.getNl()];
                for (int j = 0; j < data.getNl(); j++) {
                    tempValue = svdResult[2].getAsDouble(j, i);
                    tempRsingVec[j] = tempValue;
                    minVal = minVal > tempValue ? tempValue : minVal;
                    maxVal = maxVal < tempValue ? tempValue : maxVal;
                }
                IntensImageDataset rSingVec = new IntensImageDataset(data.getOriginalWidth(), data.getOriginalHeight(), tempRsingVec);
                PaintScale ps = new RedGreenPaintScale(minVal, maxVal);
                JFreeChart rSingVect = CommonDataDispTools.createScatChart(ImageUtilities.createColorCodedImage(rSingVec, ps), ps, data.getOriginalWidth(), data.getOriginalHeight());
//            rSingVect.setTitle("R Singular vector " + String.valueOf(j + 1));
                //rSingVect.getTitle().setFont(new Font(tracechart.getTitle().getFont().getFontName(), Font.PLAIN, 12));
                ChartPanel rSingVectPanel = new ChartPanel(rSingVect);
                rSingVectPanel.setFillZoomRectangle(true);
                rSingVectPanel.setMouseWheelEnabled(true);
                jPRightSingVectors.add(rSingVectPanel);
            }
            
        } 
        else {
            for (int i = jPRightSingVectors.getComponentCount()-1; i >= (Integer) jSnumSV.getValue(); i--) {
                jPRightSingVectors.remove(i);
                jPRightSingVectors.repaint();                
            }
        }  
    jPRightSingVectors.validate();
    }
    
    private void createSVDPlots() {

        int maxSpinnerNumberModel = Math.min(MAX_NUMBER_SINGULAR_VALUES, (int) svdResult[1].getRowCount());
        jTFtotalNumSV.setText("Max " + maxSpinnerNumberModel + " of  " + String.valueOf(svdResult[1].getRowCount()));
        jSnumSV.setModel(new SpinnerNumberModel((int) 1, (int) 0, maxSpinnerNumberModel, (int) 1));

        //creare collection with first 2 LSV

        XYSeriesCollection lSVCollection = new XYSeriesCollection();
        XYSeries seria;
        seria = new XYSeries("LSV1");
        for (int i = 0; i < data.getNt(); i++) {
            seria.add(data.getX()[i], svdResult[0].getAsDouble((long) i, 0));
        }
        lSVCollection.addSeries(seria);



        //creare chart for 2 LSV
        leftSVChart = ChartFactory.createXYLineChart(
                "Left singular vectors",
                "Time (~s)",
                null,
                lSVCollection,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        //leftSVChart.getTitle().setFont(new Font(leftSVChart.getTitle().getFont().getFontName(), Font.PLAIN, 12));
        leftSVChart.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);        
        GraphPanel chpan = new GraphPanel(leftSVChart);
        jPLeftSingVectors.removeAll();
        jPLeftSingVectors.add(chpan);

        //creare collection with first RSV
               
        double[] tempRsingVec = null;
        double minVal = 0;
        double maxVal = 0;

//            seria = new XYSeries("RSV" + (j + 1));
        tempRsingVec = new double[data.getNl()];
        double tempValue;
        for (int i = 0; i < data.getNl(); i++) {
            tempValue =  svdResult[2].getAsDouble(i, 0);
            tempRsingVec[i] =tempValue;
            minVal = minVal > tempValue ? tempValue : minVal;
            maxVal = maxVal < tempValue ? tempValue : maxVal;
        }

        IntensImageDataset rSingVec = new IntensImageDataset(data.getOriginalWidth(), data.getOriginalHeight(), tempRsingVec);
        PaintScale ps = new RedGreenPaintScale(minVal, maxVal);
        JFreeChart rSingVect = CommonDataDispTools.createScatChart(ImageUtilities.createColorCodedImage(rSingVec, ps), ps, data.getOriginalWidth(), data.getOriginalHeight());
//            rSingVect.setTitle("R Singular vector " + String.valueOf(j + 1));
        //rSingVect.getTitle().setFont(new Font(tracechart.getTitle().getFont().getFontName(), Font.PLAIN, 12));
        ChartPanel rSingVectPanel = new ChartPanel(rSingVect);
        rSingVectPanel.setFillZoomRectangle(true);
        rSingVectPanel.setMouseWheelEnabled(true);

        jPRightSingVectors.removeAll();
        jPRightSingVectors.add(rSingVectPanel);


//creare collection with singular values
        XYSeriesCollection sVCollection = new XYSeriesCollection();
        seria = new XYSeries("SV");
        for (int i = 0; i < maxSpinnerNumberModel; i++) {
            seria.add(i + 1, svdResult[1].getAsDouble((long) i, (long) i));
        }
        sVCollection.addSeries(seria);


        //create chart for singular values
        JFreeChart tracechart = ChartFactory.createXYLineChart(
                "Screeplot",
                "Singular Value index (n)",
                null,
                sVCollection,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        LogAxis logAxe = new LogAxis("Log(SVn)");
        final NumberAxis domainAxis = (NumberAxis) tracechart.getXYPlot().getDomainAxis();
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        tracechart.getXYPlot().setRangeAxis(logAxe);
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
        //add chart with 2 RSV to JPannel
        jPSingValues.removeAll();
        jPSingValues.add(chpan);
    }
    

    @Override
    public void chartChanged(ChartChangeEvent cce) {
        XYPlot plot = this.chartMultiSpec.getXYPlot();
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

        if (!plot.getDomainAxis().getRange().equals(this.lastXRange)) {
            this.lastXRange = plot.getDomainAxis().getRange();
            XYPlot plot2 = (XYPlot) this.subchartHorisontalTrace.getPlot();
            lowInd = (int) (this.lastXRange.getLowerBound());
            upInd = (int) (this.lastXRange.getUpperBound() - 1);
            double lowIndValue = data.getIntenceImY()[lowInd];
            double upIndValue = data.getIntenceImY()[upInd];
            Range domainAxisRange = lowIndValue > upIndValue ? (new Range(upIndValue, lowIndValue)) : (new Range(lowIndValue, upIndValue));
            plot2.getDomainAxis().setRange(domainAxisRange);
            jSVerticalCut.setMinimum(lowInd);
            jSVerticalCut.setMaximum(upInd);

        }

        if (!plot.getRangeAxis().getRange().equals(this.lastYRange)) {
            this.lastYRange = plot.getRangeAxis().getRange();
            XYPlot plot1 = (XYPlot) this.subchartVerticalCutTrace.getPlot();
            lowInd = (int) (this.wholeYRange.getUpperBound() - this.lastYRange.getUpperBound());
            upInd = (int) (this.wholeYRange.getUpperBound() - this.lastYRange.getLowerBound() - 1);
            plot1.getDomainAxis().setRange(new Range(data.getIntenceImX()[lowInd], data.getIntenceImX()[upInd]));
            plot1.getRangeAxis().setAutoRange(true);
            jsHorisontalCut.setMinimum(lowInd);
            jsHorisontalCut.setMaximum(upInd);
        }

    }

    
}
