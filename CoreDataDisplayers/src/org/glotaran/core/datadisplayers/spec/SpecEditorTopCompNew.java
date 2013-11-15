package org.glotaran.core.datadisplayers.spec;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.main.common.*;
import org.glotaran.core.main.nodes.TgdDataChildren;
import org.glotaran.core.main.nodes.TgdDataNode;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.messages.CoreInformationMessages;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.jfreechartcustom.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDataImageAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LinLogAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.util.LinLogFormat;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.doublematrix.impl.DefaultDenseDoubleMatrix2D;

/**
 * Top component which displays something.
 */
final public class SpecEditorTopCompNew extends CloneableTopComponent
        implements ChartChangeListener { //implements ChartMouseListener {

    private final static long serialVersionUID = 1L;
    private static SpecEditorTopCompNew instance;
    /** path to the icon used by the component and its open action */
    //    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "SpecEditorTopComponent";
    private JFreeChart chartMain;
    private JFreeChart subchartTimeTrace;
    private JFreeChart subchartWaveTrace;
    private JFreeChart leftSVChart;
    private JFreeChart rightSVChart;
    private Crosshair crosshair1;
    private Crosshair crosshair2;
    private ChartPanel chpanImage;
    private DatasetTimp data;
    private ColorCodedImageDataset dataset;
    private TgdDataObject dataObject;
    private TimpDatasetDataObject dataObject2;
    private Range lastXRange;
    private Range lastYRange;
    private Range wholeXRange;
    private Range wholeYRange;
    private Matrix[] svdResult;
    private ArrayList<Integer> svdFilter;  
    private TGProject project;
    private int MAX_NUMBER_SINGULAR_VALUES = 20;
    private int MAX_NO_TICKS = 6;

    public SpecEditorTopCompNew() {
        data = new DatasetTimp();
        svdFilter = new ArrayList<Integer>();
        initComponents();
        setName(NbBundle.getMessage(SpecEditorTopCompNew.class, "CTL_StreakLoaderTopComponent"));
        setToolTipText(NbBundle.getMessage(SpecEditorTopCompNew.class, "HINT_StreakLoaderTopComponent"));
    }

    public SpecEditorTopCompNew(TgdDataObject dataObj) {
        File tgdFile;
        dataObject = dataObj;
        svdFilter = new ArrayList<Integer>();
        initComponents();
        setName(dataObject.getTgd().getFilename());

        //try to get the file from local cache
        project = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
        if (dataObject.getTgd().getRelativePath() != null) {
            tgdFile = new File(project.getProjectDirectory().getPath() + File.separator + dataObject.getTgd().getRelativePath());
        } else { //try the orginal location
            tgdFile = new File(dataObject.getTgd().getPath());
        }

        // setName(NbBundle.getMessage(SpecEditorTopCompNew.class, "CTL_StreakLoaderTopComponent"));
        setToolTipText(NbBundle.getMessage(SpecEditorTopCompNew.class, "HINT_StreakLoaderTopComponent"));
        // setIcon(Utilities.loadImage(ICON_PATH, true));
        data = new DatasetTimp();

        //get loaders from lookup
        Collection<? extends TGDatasetInterface> services = Lookup.getDefault().lookupAll(TGDatasetInterface.class);
        for (final TGDatasetInterface service : services) {
            try {
                if (service.Validator(tgdFile)) {
                    data = service.loadFile(tgdFile);
                    if (service.getExtention().equalsIgnoreCase("raw")) {
                        jBConvertToAbs.setEnabled(true);
                    }
                    if (data != null) {
                        if (data.getIntenceIm() != null) {
                            jBTICorrection.setEnabled(true);
                        }
                        MakeImageChart(MakeXYZDataset());
                        updateFileInfo();
                    }
                    break;
                }
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        setActivatedNodes(new Node[]{dataObject.getNodeDelegate()});
    }

    public SpecEditorTopCompNew(TimpDatasetDataObject dataObj) {
        svdFilter = new ArrayList<Integer>();
        dataObject2 = dataObj;
        data = dataObj.getDatasetTimp();
        
        boolean invertedWaves = data.getX2()[0]<data.getX2()[1] ? false : true;
        if (invertedWaves) {
            double[] x2t = new double[data.getNl()];
            double[] temp = new double[data.getNl()*data.getNt()];
            for (int j = 0; j < data.getNl(); j++) {
                for (int i = 0; i < data.getNt(); i++) {
                    temp[(data.getNl() -1 - j) * data.getNt() + i] = data.getPsisim()[j * data.getNt() + i];
                }
                x2t[data.getNl() - j - 1] = data.getX2()[j];
            }
            data.setX2(x2t);
            data.setPsisim(temp);
        }
        initComponents();
        setName(data.getDatasetName());
        setToolTipText(NbBundle.getMessage(SpecEditorTopCompNew.class, "HINT_StreakLoaderTopComponent"));
        MakeImageChart(MakeXYZDataset());
        updateFileInfo();
        
    }

    public TgdDataObject getDataObject() {
        return dataObject;
    }

    public TimpDatasetDataObject getDataObject2() {
        return dataObject2;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jDialog1 = new javax.swing.JDialog();
        inputDatasetName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel11 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jTBZoomX = new javax.swing.JToggleButton();
        jTBZoomY = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jBAverage = new javax.swing.JButton();
        jBResample = new javax.swing.JButton();
        jBSelectData = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jBMakeDataset = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jBSubtractBG = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jBTICorrection = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jBSvdFilter = new javax.swing.JButton();
        jBConvertToAbs = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPSpecImage = new javax.swing.JPanel();
        jSColum = new javax.swing.JSlider();
        jSRow = new javax.swing.JSlider();
        jPXTrace = new javax.swing.JPanel();
        jPYTrace = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTFMaxIntence = new javax.swing.JTextField();
        jTFMinIntence = new javax.swing.JTextField();
        rangeSlider1 = new com.jidesoft.swing.RangeSlider();
        jPanel2 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jTBLinLog = new javax.swing.JToggleButton();
        jTFLinPart = new javax.swing.JTextField();
        jBUpdLinLog = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jLabel3 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jSnumSV = new javax.swing.JSpinner();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jCBShowAllSVD = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jTFtotalNumSV = new javax.swing.JTextField();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jCBAddtoFilter = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPSingValues = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPLeftSingVectors = new javax.swing.JPanel();
        jPRightSingVectors = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTAInfo = new javax.swing.JTextArea();

        inputDatasetName.setText(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.inputDatasetName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jButton3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jButton4.text")); // NOI18N

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                        .addComponent(jButton4))
                    .addComponent(inputDatasetName, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addComponent(inputDatasetName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setAutoscrolls(true);
        setPreferredSize(new java.awt.Dimension(800, 600));
        setLayout(new java.awt.BorderLayout());

        jPanel11.setAlignmentX(0.0F);
        jPanel11.setAlignmentY(0.0F);
        jPanel11.setLayout(new java.awt.GridBagLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jTBZoomX, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jTBZoomX.text")); // NOI18N
        jTBZoomX.setFocusable(false);
        jTBZoomX.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTBZoomX.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTBZoomX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBZoomXActionPerformed(evt);
            }
        });
        jToolBar1.add(jTBZoomX);

        org.openide.awt.Mnemonics.setLocalizedText(jTBZoomY, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jTBZoomY.text")); // NOI18N
        jTBZoomY.setFocusable(false);
        jTBZoomY.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTBZoomY.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTBZoomY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBZoomYActionPerformed(evt);
            }
        });
        jToolBar1.add(jTBZoomY);
        jToolBar1.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(jBAverage, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jBAverage.text")); // NOI18N
        jBAverage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAverageActionPerformed(evt);
            }
        });
        jToolBar1.add(jBAverage);

        org.openide.awt.Mnemonics.setLocalizedText(jBResample, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jBResample.text")); // NOI18N
        jBResample.setFocusable(false);
        jBResample.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBResample.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBResample.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBResampleActionPerformed(evt);
            }
        });
        jToolBar1.add(jBResample);

        org.openide.awt.Mnemonics.setLocalizedText(jBSelectData, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jBSelectData.text")); // NOI18N
        jBSelectData.setFocusable(false);
        jBSelectData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBSelectData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBSelectData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSelectDataActionPerformed(evt);
            }
        });
        jToolBar1.add(jBSelectData);
        jToolBar1.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(jBMakeDataset, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jBMakeDataset.text")); // NOI18N
        jBMakeDataset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBMakeDatasetActionPerformed(evt);
            }
        });
        jToolBar1.add(jBMakeDataset);
        jToolBar1.add(jSeparator3);

        org.openide.awt.Mnemonics.setLocalizedText(jBSubtractBG, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jBSubtractBG.text")); // NOI18N
        jBSubtractBG.setFocusable(false);
        jBSubtractBG.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBSubtractBG.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBSubtractBG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSubtractBGActionPerformed(evt);
            }
        });
        jToolBar1.add(jBSubtractBG);

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jButton2.text")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        org.openide.awt.Mnemonics.setLocalizedText(jBTICorrection, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jBTICorrection.text")); // NOI18N
        jBTICorrection.setEnabled(false);
        jBTICorrection.setFocusable(false);
        jBTICorrection.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBTICorrection.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBTICorrection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBTICorrectionActionPerformed(evt);
            }
        });
        jToolBar1.add(jBTICorrection);
        jToolBar1.add(jSeparator4);

        org.openide.awt.Mnemonics.setLocalizedText(jBSvdFilter, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jBSvdFilter.text")); // NOI18N
        jBSvdFilter.setFocusable(false);
        jBSvdFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBSvdFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBSvdFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSvdFilterActionPerformed(evt);
            }
        });
        jToolBar1.add(jBSvdFilter);

        org.openide.awt.Mnemonics.setLocalizedText(jBConvertToAbs, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jBConvertToAbs.text")); // NOI18N
        jBConvertToAbs.setEnabled(false);
        jBConvertToAbs.setFocusable(false);
        jBConvertToAbs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBConvertToAbs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBConvertToAbs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConvertToAbsActionPerformed(evt);
            }
        });
        jToolBar1.add(jBConvertToAbs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel11.add(jToolBar1, gridBagConstraints);

        jScrollPane4.setBorder(null);
        jScrollPane4.setAlignmentX(0.0F);
        jScrollPane4.setAlignmentY(0.0F);

        jPanel1.setAlignmentX(0.0F);
        jPanel1.setAlignmentY(0.0F);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPSpecImage.setBackground(new java.awt.Color(0, 0, 0));
        jPSpecImage.setMinimumSize(new java.awt.Dimension(100, 100));
        jPSpecImage.setPreferredSize(new java.awt.Dimension(360, 360));
        jPSpecImage.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.6;
        jPanel1.add(jPSpecImage, gridBagConstraints);

        jSColum.setMinimum(1);
        jSColum.setValue(1);
        jSColum.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSColumStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 70);
        jPanel1.add(jSColum, gridBagConstraints);

        jSRow.setMinimum(1);
        jSRow.setOrientation(javax.swing.JSlider.VERTICAL);
        jSRow.setValue(1);
        jSRow.setInverted(true);
        jSRow.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSRowStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jSRow, gridBagConstraints);

        jPXTrace.setBackground(new java.awt.Color(255, 255, 255));
        jPXTrace.setMaximumSize(new java.awt.Dimension(423, 178));
        jPXTrace.setMinimumSize(new java.awt.Dimension(423, 178));
        jPXTrace.setPreferredSize(new java.awt.Dimension(360, 200));
        jPXTrace.setVerifyInputWhenFocusTarget(false);
        jPXTrace.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.4;
        jPanel1.add(jPXTrace, gridBagConstraints);

        jPYTrace.setBackground(new java.awt.Color(255, 255, 255));
        jPYTrace.setMaximumSize(new java.awt.Dimension(211, 356));
        jPYTrace.setMinimumSize(new java.awt.Dimension(211, 356));
        jPYTrace.setPreferredSize(new java.awt.Dimension(200, 360));
        jPYTrace.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.6;
        jPanel1.add(jPYTrace, gridBagConstraints);

        jPanel5.setPreferredSize(new java.awt.Dimension(1, 1));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 362, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(0, -100, 0, 0);
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jPanel10.border.title"))); // NOI18N
        jPanel10.setPreferredSize(new java.awt.Dimension(200, 200));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jButton1.text")); // NOI18N
        jButton1.setIconTextGap(2);
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTFMaxIntence.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMaxIntence.setText(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jTFMaxIntence.text")); // NOI18N

        jTFMinIntence.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMinIntence.setText(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jTFMinIntence.text")); // NOI18N

        rangeSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rangeSlider1StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rangeSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFMaxIntence, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addComponent(jTFMinIntence, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFMinIntence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTFMaxIntence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(rangeSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(87, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.4;
        jPanel1.add(jPanel10, gridBagConstraints);

        jScrollPane4.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.ipady = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel11.add(jScrollPane4, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jPanel11.TabConstraints.tabTitle"), jPanel11); // NOI18N

        jPanel2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel2ComponentShown(evt);
            }
        });

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setMinimumSize(new java.awt.Dimension(400, 24));
        jToolBar2.setPreferredSize(new java.awt.Dimension(400, 24));

        org.openide.awt.Mnemonics.setLocalizedText(jTBLinLog, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jTBLinLog.text")); // NOI18N
        jTBLinLog.setEnabled(false);
        jTBLinLog.setFocusable(false);
        jTBLinLog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTBLinLog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTBLinLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBLinLogActionPerformed(evt);
            }
        });
        jToolBar2.add(jTBLinLog);

        jTFLinPart.setText(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jTFLinPart.text")); // NOI18N
        jTFLinPart.setMaximumSize(new java.awt.Dimension(70, 19));
        jTFLinPart.setMinimumSize(new java.awt.Dimension(10, 19));
        jTFLinPart.setPreferredSize(new java.awt.Dimension(70, 19));
        jToolBar2.add(jTFLinPart);

        org.openide.awt.Mnemonics.setLocalizedText(jBUpdLinLog, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jBUpdLinLog.text")); // NOI18N
        jBUpdLinLog.setEnabled(false);
        jBUpdLinLog.setFocusable(false);
        jBUpdLinLog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBUpdLinLog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBUpdLinLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBUpdLinLogActionPerformed(evt);
            }
        });
        jToolBar2.add(jBUpdLinLog);
        jToolBar2.add(jSeparator5);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jLabel3.text")); // NOI18N
        jToolBar2.add(jLabel3);
        jToolBar2.add(jSeparator7);

        jSnumSV.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), null, null, Integer.valueOf(1)));
        jSnumSV.setMaximumSize(new java.awt.Dimension(70, 20));
        jSnumSV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSnumSVStateChanged(evt);
            }
        });
        jToolBar2.add(jSnumSV);
        jToolBar2.add(jSeparator6);

        jCBShowAllSVD.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCBShowAllSVD, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jCBShowAllSVD.text")); // NOI18N
        jCBShowAllSVD.setToolTipText(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jCBShowAllSVD.toolTipText")); // NOI18N
        jCBShowAllSVD.setFocusable(false);
        jCBShowAllSVD.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jCBShowAllSVD.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCBShowAllSVD.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jCBShowAllSVD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBShowAllSVDActionPerformed(evt);
            }
        });
        jToolBar2.add(jCBShowAllSVD);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jLabel4.text")); // NOI18N
        jToolBar2.add(jLabel4);
        jToolBar2.add(jSeparator8);

        jTFtotalNumSV.setEditable(false);
        jTFtotalNumSV.setText(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jTFtotalNumSV.text")); // NOI18N
        jTFtotalNumSV.setMaximumSize(new java.awt.Dimension(130, 19));
        jTFtotalNumSV.setPreferredSize(new java.awt.Dimension(130, 19));
        jToolBar2.add(jTFtotalNumSV);
        jToolBar2.add(jSeparator10);

        org.openide.awt.Mnemonics.setLocalizedText(jCBAddtoFilter, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jCBAddtoFilter.text")); // NOI18N
        jCBAddtoFilter.setToolTipText(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jCBAddtoFilter.toolTipText")); // NOI18N
        jCBAddtoFilter.setFocusable(false);
        jCBAddtoFilter.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCBAddtoFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jCBAddtoFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBAddtoFilterActionPerformed(evt);
            }
        });
        jToolBar2.add(jCBAddtoFilter);

        jScrollPane1.setBorder(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1004, 691));

        jPanel3.setMinimumSize(new java.awt.Dimension(320, 240));
        jPanel3.setPreferredSize(new java.awt.Dimension(640, 480));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel8.setLayout(new java.awt.GridLayout(0, 1));

        jPanel7.setLayout(new java.awt.GridBagLayout());

        jPSingValues.setBackground(new java.awt.Color(255, 255, 255));
        jPSingValues.setPreferredSize(new java.awt.Dimension(50, 50));
        jPSingValues.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.ipady = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jPSingValues, gridBagConstraints);

        jPanel6.setPreferredSize(new java.awt.Dimension(50, 50));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 281, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(jPanel6, gridBagConstraints);

        jPanel12.setPreferredSize(new java.awt.Dimension(50, 50));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 281, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(jPanel12, gridBagConstraints);

        jPanel8.add(jPanel7);

        jPanel13.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        jPLeftSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPLeftSingVectors.setPreferredSize(new java.awt.Dimension(50, 50));
        jPLeftSingVectors.setLayout(new java.awt.BorderLayout());
        jPanel13.add(jPLeftSingVectors);

        jPRightSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPRightSingVectors.setPreferredSize(new java.awt.Dimension(50, 50));
        jPRightSingVectors.setLayout(new java.awt.BorderLayout());
        jPanel13.add(jPRightSingVectors);

        jPanel8.add(jPanel13);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jPanel8, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jLabel2.text")); // NOI18N
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel3);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 1091, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1091, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jPanel2.TabConstraints.tabTitle_1"), jPanel2); // NOI18N

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jScrollPane3.border.title"))); // NOI18N

        jTAInfo.setColumns(20);
        jTAInfo.setRows(5);
        jTAInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jTAInfo.border.title"))); // NOI18N
        jScrollPane3.setViewportView(jTAInfo);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1071, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(329, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(SpecEditorTopCompNew.class, "SpecEditorTopCompNew.jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        double newMinAmpl, newMaxAmpl;
        try {
            newMinAmpl = Double.parseDouble(jTFMinIntence.getText());
            newMaxAmpl = Double.parseDouble(jTFMaxIntence.getText());
            PaintScale ps = new RainbowPaintScale(newMinAmpl, newMaxAmpl);
//            PaintScale ps = new RedGreenPaintScale(newMinAmpl, newMaxAmpl);

            BufferedImage image = ImageUtilities.createColorCodedImage(this.dataset, ps);
            XYDataImageAnnotation ann = new XYDataImageAnnotation(image, 0, 0,
                    dataset.GetImageWidth(), dataset.GetImageHeigth(), true);

            XYPlot plot = (XYPlot) chartMain.getPlot();
            plot.getRenderer().removeAnnotations();
            plot.getRenderer().addAnnotation(ann, Layer.BACKGROUND);

            ((PaintScaleLegend) chartMain.getSubtitle(0)).setScale(ps);
            ((PaintScaleLegend) chartMain.getSubtitle(0)).getAxis().setRange(newMinAmpl, newMaxAmpl);

        } catch (NumberFormatException ex) {
            NotifyDescriptor errorMessage = new NotifyDescriptor.Exception(
                    new Exception(NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("set_correct_chanNum")));
            DialogDisplayer.getDefault().notify(errorMessage);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jSRowStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSRowStateChanged
        crosshair2.setValue(dataset.GetImageHeigth() - jSRow.getValue());
        int xIndex = jSRow.getValue();
        XYDataset d = ImageUtilities.extractRowFromImageDataset(dataset, xIndex, "Spec");
        subchartWaveTrace.getXYPlot().setDataset(d);
    }//GEN-LAST:event_jSRowStateChanged

    private void jSColumStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSColumStateChanged
        crosshair1.setValue(jSColum.getValue());
        int xIndex = jSColum.getValue();
        XYDataset d = ImageUtilities.extractColumnFromImageDataset(dataset, xIndex, "Spec");
        subchartTimeTrace.getXYPlot().setDataset(d);
    }//GEN-LAST:event_jSColumStateChanged

    private void jBMakeDatasetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBMakeDatasetActionPerformed

        int startX, startY, endX, endY;
        int newWidth, newHeight;
        boolean averaged = true;

        NotifyDescriptor.InputLine datasetNameDialog = new NotifyDescriptor.InputLine(
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("dataset_name"),
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("spec_datasetname"));
        Object res = DialogDisplayer.getDefault().notify(datasetNameDialog);

        if (res.equals(NotifyDescriptor.OK_OPTION)) {
            DatasetTimp newdataset = new DatasetTimp(); //data;
            newdataset.setDatasetName(datasetNameDialog.getInputText());
            newdataset.setType("spec");
            startX = (int) (this.lastXRange.getLowerBound());
            endX = (int) (this.lastXRange.getUpperBound()) - 1;
            startY = (int) (this.wholeYRange.getUpperBound() - this.lastYRange.getUpperBound());
            endY = (int) (this.wholeYRange.getUpperBound() - this.lastYRange.getLowerBound()) - 1;
            newWidth = endX - startX + 1;
            newHeight = endY - startY + 1;

            double[] newvec = new double[newWidth];

            for (int i = 0; i < newWidth; i++) {
                newvec[i] = data.getX2()[i + startX];
            }
            newdataset.setX2(newvec);
            newdataset.setNl(newWidth);

            newvec = new double[newHeight];
            for (int i = 0; i < newHeight; i++) {
                newvec[i] = data.getX()[i + startY];
            }
            newdataset.setX(newvec);
            newdataset.setNt(newHeight);

            newvec = new double[newHeight * newWidth];

            for (int i = 0; i < newWidth; i++) {
                for (int j = 0; j < newHeight; j++) {
                    newvec[(i) * newHeight + j] = data.getPsisim()[(startX + i) * data.getNt() + startY + j];
                }
            }
            newdataset.setPsisim(newvec);
            newdataset.calcRangeInt();
            FileObject cachefolder = null;

            if (dataObject != null) {
                project = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            } else if (dataObject2 != null) {
                project = (TGProject) FileOwnerQuery.getOwner(dataObject2.getPrimaryFile());
            }
            if (project != null) {
                cachefolder = project.getCacheFolder(true);
                if ((dataObject == null) && (dataObject2 != null)) {
                    cachefolder = dataObject2.getFolder().getPrimaryFile();
                    if (dataObject2.getNodeDelegate().getParentNode().getClass().equals(TgdDataNode.class)) {
                        averaged = false;
                    }
                } else {
                    averaged = false;
                    cachefolder = cachefolder.getFileObject(dataObject.getTgd().getCacheFolderName().toString());
                }
                FileObject writeTo;
                try {
                    writeTo = cachefolder.createData(newdataset.getDatasetName(), "timpdataset");
                    ObjectOutputStream stream = new ObjectOutputStream(writeTo.getOutputStream());
                    stream.writeObject(newdataset);
                    stream.close();
                    if (!averaged) {
                        TimpDatasetDataObject dObj = (TimpDatasetDataObject) DataObject.find(writeTo);
                        TgdDataChildren chidrens;
                        if ((dataObject == null) && (dataObject2 != null)) {
                            chidrens = (TgdDataChildren) dataObject2.getNodeDelegate().getParentNode().getChildren();
                        } else {
                            chidrens = (TgdDataChildren) dataObject.getNodeDelegate().getChildren();
                        }
                        chidrens.addObj(dObj);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

            } else {
                NotifyDescriptor errorMessage = new NotifyDescriptor.Exception(
                        new Exception(NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("selMainProj")));
                DialogDisplayer.getDefault().notify(errorMessage);
            }
        }
    }//GEN-LAST:event_jBMakeDatasetActionPerformed

    private void jBResampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBResampleActionPerformed

        int xWin = 0;
        int yWin = 0;
        ResampleDatasetPanel resamplePanel = new ResampleDatasetPanel(false);
        resamplePanel.setInitialNumbers(data.getNl(), data.getNt());
        NotifyDescriptor resampleDatasetDialod = new NotifyDescriptor(
                resamplePanel,
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("resampleDataset"),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(resampleDatasetDialod).equals(NotifyDescriptor.OK_OPTION)) {

            if (resamplePanel.getResampleXState()) {
                xWin = resamplePanel.getResampleXNum();
            }

            if (resamplePanel.getResampleYState()) {
                yWin = resamplePanel.getResampleYNum();
            }

            CommonActionFunctions.resampleDataset(data, false, xWin, yWin);
            MakeImageChart(MakeXYZDataset());
            updateFileInfo();
            this.repaint();
        }

    }//GEN-LAST:event_jBResampleActionPerformed

    private void jBAverageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAverageActionPerformed

        int xWin = 0;
        int yWin = 0;
        ResampleDatasetPanel averagePanel = new ResampleDatasetPanel(true);
        averagePanel.setInitialNumbers(data.getNl(), data.getNt());
        NotifyDescriptor resampleDatasetDialod = new NotifyDescriptor(
                averagePanel,
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("averageDataset"),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.CANCEL_OPTION);

        if (DialogDisplayer.getDefault().notify(resampleDatasetDialod).equals(NotifyDescriptor.OK_OPTION)) {
            if (averagePanel.getResampleXState()) {
                xWin = averagePanel.getResampleXNum();
            }

            if (averagePanel.getResampleYState()) {
                yWin = averagePanel.getResampleYNum();
            }
            jSColum.setValue(0);
            jSRow.setValue(0);

            CommonActionFunctions.resampleDataset(data, true, xWin, yWin);
            MakeImageChart(MakeXYZDataset());
            updateFileInfo();
            this.repaint();
        }


    }//GEN-LAST:event_jBAverageActionPerformed

    private void jTBZoomYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBZoomYActionPerformed
        jTBZoomX.setSelected(false);
        if (jTBZoomY.isSelected()) {
            chpanImage.setDomainZoomable(false);
            chpanImage.setRangeZoomable(true);
        } else {
            chpanImage.setDomainZoomable(true);
            chpanImage.setRangeZoomable(true);
        }
    }//GEN-LAST:event_jTBZoomYActionPerformed

    private void jTBZoomXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBZoomXActionPerformed
        jTBZoomY.setSelected(false);
        if (jTBZoomX.isSelected()) {
            chpanImage.setDomainZoomable(true);
            chpanImage.setRangeZoomable(false);
        } else {
            chpanImage.setDomainZoomable(true);
            chpanImage.setRangeZoomable(true);
        }
    }//GEN-LAST:event_jTBZoomXActionPerformed

    private void jSnumSVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSnumSVStateChanged
        updateSVDVectorsPlot();
    }//GEN-LAST:event_jSnumSVStateChanged

    
    private void jPanel2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel2ComponentShown

        if (svdResult == null) {
            SwingWorker<Matrix[], Void> worker = new SwingWorker<Matrix[], Void>() {
                
                final ProgressHandle ph = ProgressHandleFactory.createHandle("Performing Singular Value Decomposition on dataset");

                @Override
                protected Matrix[] doInBackground() throws Exception {
                    ph.start();
                    return calculateSVD();
                }

                @Override
                protected void done() {
                    createSVDPlots();
                    ph.finish();
                }
            };
            worker.execute();
        }
    }//GEN-LAST:event_jPanel2ComponentShown

    private void jBConvertToAbsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConvertToAbsActionPerformed
        int baseLineNum = 1;
        NotifyDescriptor.InputLine datasetNameDialog = new NotifyDescriptor.InputLine(
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("numberOfBaselines"),
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("numberOfBaselinesStr"));
        Object res = DialogDisplayer.getDefault().notify(datasetNameDialog);

        if (res.equals(NotifyDescriptor.OK_OPTION)) {
            try {
                baseLineNum = Integer.parseInt(datasetNameDialog.getInputText());
            } catch (NumberFormatException e) {
                CoreErrorMessages.numberFormatException();
                return;
            }
            CommonActionFunctions.convertToAbsorption(data, baseLineNum);
            MakeImageChart(MakeXYZDataset());
            updateFileInfo();
            this.repaint();
        }
    }//GEN-LAST:event_jBConvertToAbsActionPerformed

    private void jBSelectDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSelectDataActionPerformed
        SelectDataDialog selectDataDialogPanel = new SelectDataDialog();
        //selectDataDialog.setInitialNumbers(data.getNl(), data.getNt());
        NotifyDescriptor selectDataDialog = new NotifyDescriptor(
                selectDataDialogPanel,
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("selectDataDialogTitle"),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(selectDataDialog).equals(NotifyDescriptor.OK_OPTION)) {
            data = CommonActionFunctions.selectInDataset(data, selectDataDialogPanel.getDim1From(), selectDataDialogPanel.getDim1To(), selectDataDialogPanel.getDim2From(), selectDataDialogPanel.getDim2To());
            jSColum.setValue(0);
            jSRow.setValue(0);
            MakeImageChart(MakeXYZDataset());
            updateFileInfo();
            this.repaint();
        }
    }//GEN-LAST:event_jBSelectDataActionPerformed

    private void jBSubtractBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSubtractBGActionPerformed
        double[] bgSpec;
        double bgConstant = 0;
        BaselineCorrectionDialog baselineCorrectionDialogPanel = new BaselineCorrectionDialog();
        //selectDataDialog.setInitialNumbers(data.getNl(), data.getNt());
        NotifyDescriptor baselineCorrectionDialog = new NotifyDescriptor(
                baselineCorrectionDialogPanel,
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("baselineSubstractionDialogTitle"),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.CANCEL_OPTION);

        if (DialogDisplayer.getDefault().notify(baselineCorrectionDialog).equals(NotifyDescriptor.OK_OPTION)) {
            CommonActionFunctions.baselineCorrection(data, baselineCorrectionDialogPanel.getCorrParameters());
////subtract spect
//            if (baselineCorrectionDialogPanel.getSubtractSpecState()) {
//                bgSpec = new double[data.getNl()];
//                for (int i = 0; i < baselineCorrectionDialogPanel.getNumSpec(); i++) {
//                    for (int j = 0; j < data.getNl(); j++) {
//                        bgSpec[j] += data.getPsisim()[i + j * data.getNt()];
//                    }
//                }
//                for (int j = 0; j < data.getNl(); j++) {
//                    bgSpec[j] /= baselineCorrectionDialogPanel.getNumSpec();
//                }
//
//                for (int i = 0; i < data.getNt(); i++) {
//                    for (int j = 0; j < data.getNl(); j++) {
//                        data.getPsisim()[i + j * data.getNt()] = data.getPsisim()[i + j * data.getNt()] - bgSpec[j];
//                    }
//                }
//            }
////subtract constant
//            if ((baselineCorrectionDialogPanel.getSubtractConstState()) || (baselineCorrectionDialogPanel.getSubtractConstCalcState())) {
//                if (baselineCorrectionDialogPanel.getSubtractConstState()) {
//                    bgConstant = baselineCorrectionDialogPanel.getBGConstant();
//                } else {
//                    if (baselineCorrectionDialogPanel.getSubtractConstCalcState()) {
//                        //calculate constant from data based on the filled numbers and put it to bgConstant
//                        int dim1From, dim1To, dim2From, dim2To;
//                        dim1From = CommonActionFunctions.findTimeIndex(data, baselineCorrectionDialogPanel.getBgRegionDim1()[0]);
//                        dim1To = CommonActionFunctions.findTimeIndex(data, baselineCorrectionDialogPanel.getBgRegionDim1()[1]);
//                        dim2From = CommonActionFunctions.findWaveIndex(data, baselineCorrectionDialogPanel.getBgRegionDim2()[0]);
//                        dim2To = CommonActionFunctions.findWaveIndex(data, baselineCorrectionDialogPanel.getBgRegionDim2()[1]);
//                        double s = 0;
//                        for (int i = dim1From; i < dim1To; i++){
//                            for (int j = dim2From; j < dim2To; j++) {
//                                s += data.getPsisim()[i + j * data.getNt()]; 
//                            } 
//                        }
//                        bgConstant =s/((dim1To-dim1From)*(dim2To-dim2From));      
//                    }
//                }
//                //subtract  bgConstant from the data
//                for (int i = 0; i < data.getNl() * data.getNt(); i++) {
//                    data.getPsisim()[i] -= bgConstant;
//                }
//            }
////subtract time trace 
//            if (baselineCorrectionDialogPanel.getSubtractTimeTraceState()){
//                int indFrom, indTo;
//                indFrom = CommonActionFunctions.findWaveIndex(data, baselineCorrectionDialogPanel.getTimeTrBg()[0]);        
//                indTo = CommonActionFunctions.findWaveIndex(data, baselineCorrectionDialogPanel.getTimeTrBg()[1]);
//                bgSpec = new double[data.getNt()];
//                
//                for (int i = 0; i < data.getNt(); i++) {
//                    for (int j = indFrom; j < indTo; j++) {
//                        bgSpec[i] += data.getPsisim()[i + j * data.getNt()];
//                    }
//                }
//                for (int j = 0; j < data.getNt(); j++) {
//                    bgSpec[j] /= (indTo-indFrom);
//                }
//
//                for (int i = 0; i < data.getNt(); i++) {
//                    for (int j = 0; j < data.getNl(); j++) {
//                        data.getPsisim()[i + j * data.getNt()] = data.getPsisim()[i + j * data.getNt()] - bgSpec[i];
//                    }
//                }
//                
//            }

            data.calcRangeInt();
            MakeImageChart(MakeXYZDataset());
            updateFileInfo();
            this.repaint();
        }
    }//GEN-LAST:event_jBSubtractBGActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        OutlierCorrectionDialog outliersCorrectionDialogPanel = new OutlierCorrectionDialog();
        NotifyDescriptor outliersCorrectionDialog = new NotifyDescriptor(
                outliersCorrectionDialogPanel,
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("outlierDetectionDialogTitle"),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.CANCEL_OPTION);

        if (DialogDisplayer.getDefault().notify(outliersCorrectionDialog).equals(NotifyDescriptor.OK_OPTION)) {
            int size = outliersCorrectionDialogPanel.getWindowSize();
            double fence = outliersCorrectionDialogPanel.getFence();
            int outliercount = CommonActionFunctions.outliersCorrection(data, size, fence);
            MakeImageChart(MakeXYZDataset());
            updateFileInfo();
            this.repaint();

            NotifyDescriptor.Message warningMessage = new NotifyDescriptor.Message(String.valueOf(outliercount) + " "
                    + NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("outliersNumber"),
                    NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(warningMessage);

        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTBLinLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBLinLogActionPerformed
        
        if (jTBLinLog.isSelected()) {
            if (!jTFLinPart.getText().isEmpty()) {
                double linPortion = Double.valueOf(jTFLinPart.getText());
                leftSVChart.getXYPlot().setDomainAxis(
                        new LinLogAxis(leftSVChart.getXYPlot().getDomainAxis().getLabel(), linPortion, -linPortion, 0.2));
                leftSVChart.fireChartChanged();
                jPLeftSingVectors.repaint();
                jBUpdLinLog.setEnabled(true);
            }
        } else {
            leftSVChart.getXYPlot().setDomainAxis(new NumberAxis(leftSVChart.getXYPlot().getDomainAxis().getLabel()));

            jBUpdLinLog.setEnabled(false);

        }
    }//GEN-LAST:event_jTBLinLogActionPerformed

    private void jBUpdLinLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBUpdLinLogActionPerformed
        jTBLinLogActionPerformed(evt);
    }//GEN-LAST:event_jBUpdLinLogActionPerformed

    private void jBTICorrectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBTICorrectionActionPerformed
        CommonActionFunctions.totalIntencityCorrection(data);
        MakeImageChart(MakeXYZDataset());
        updateFileInfo();
        this.repaint();
    }//GEN-LAST:event_jBTICorrectionActionPerformed

    private void rangeSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rangeSlider1StateChanged
        double newMinAmpl, newMaxAmpl;
        Double range = (data.getMaxInt() - data.getMinInt());
        newMinAmpl = data.getMinInt() + range / (rangeSlider1.getMaximum() - rangeSlider1.getMinimum()) * rangeSlider1.getLowValue();
        newMaxAmpl = data.getMinInt() + range / (rangeSlider1.getMaximum() - rangeSlider1.getMinimum()) * rangeSlider1.getHighValue();
        if (newMinAmpl < newMaxAmpl) {
            try {
                PaintScale ps = new RainbowPaintScale(newMinAmpl, newMaxAmpl);
//                PaintScale ps = new RedGreenPaintScale(newMinAmpl, newMaxAmpl);

                BufferedImage image = ImageUtilities.createColorCodedImage(this.dataset, ps);
                XYDataImageAnnotation ann = new XYDataImageAnnotation(image, 0, 0,
                        dataset.GetImageWidth(), dataset.GetImageHeigth(), true);

                XYPlot plot = (XYPlot) chartMain.getPlot();
                plot.getRenderer().removeAnnotations();
                plot.getRenderer().addAnnotation(ann, Layer.BACKGROUND);

                ((PaintScaleLegend) chartMain.getSubtitle(0)).setScale(ps);
                ((PaintScaleLegend) chartMain.getSubtitle(0)).getAxis().setRange(newMinAmpl, newMaxAmpl);

                jTFMinIntence.setText(String.valueOf(newMinAmpl));
                jTFMaxIntence.setText(String.valueOf(newMaxAmpl));

            } catch (NumberFormatException ex) {
                NotifyDescriptor errorMessage = new NotifyDescriptor.Exception(
                        new Exception(NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("set_correct_chanNum")));
                DialogDisplayer.getDefault().notify(errorMessage);
            }
        }

    }//GEN-LAST:event_rangeSlider1StateChanged

    private void jBSvdFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSvdFilterActionPerformed
        
        // check if svdfilter is filled 
        // if not show SVD tab show message asking user to tick what to add to svdfilter and asking to reclick button. 
        if (svdFilter.isEmpty()){
            jTabbedPane1.setSelectedIndex(1);
            CoreInformationMessages.SVDFilterInfo();
            return ;
        }
        
        Collections.sort(svdFilter);
        DefaultDenseDoubleMatrix2D cv = new DefaultDenseDoubleMatrix2D(svdFilter.size(), svdFilter.size());
        
        Matrix lsm = svdResult[0].selectColumns(Ret.NEW, svdFilter);
        Matrix rsm = svdResult[2].selectColumns(Ret.NEW, svdFilter).transpose();
        for (int i = 0; i<svdFilter.size(); i++){
            cv.setAsDouble( svdResult[1].getAsDouble((long)svdFilter.get(i), (long)svdFilter.get(i)), (long)i, (long)i);
        }
        DefaultDenseDoubleMatrix2D newData = (DefaultDenseDoubleMatrix2D) (lsm.mtimes(cv).mtimes(rsm));
        
//        try {    
//            File fil = new File("lsm.txt");
//            lsm.exportToFile(FileFormat.TXT, fil, lsm);
//            fil = new File("rsm.txt");
//            rsm.exportToFile(FileFormat.TXT, fil, rsm);
//            fil = new File("cv.txt");
//            cv.exportToFile(FileFormat.TXT, fil, cv); 
//            fil = new File("newdata.txt");
//            newData.exportToFile(FileFormat.TXT, fil, newData);
//        } catch (MatrixException ex) {
//            Exceptions.printStackTrace(ex);
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
        
        data.setPsisim(newData.getColumnMajorDoubleArray1D());
        
        MakeImageChart(MakeXYZDataset());
        updateFileInfo();
        svdResult=null;
        svdFilter.clear();
        this.repaint();
    }//GEN-LAST:event_jBSvdFilterActionPerformed

    private void jCBShowAllSVDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBShowAllSVDActionPerformed
        updateSVDVectorsPlot();
    }//GEN-LAST:event_jCBShowAllSVDActionPerformed

    private void jCBAddtoFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBAddtoFilterActionPerformed
        if(jCBAddtoFilter.isSelected()){
            svdFilter.add((Integer)jSnumSV.getModel().getValue()-1);
        } else {
            for (int i = 0; i < svdFilter.size(); i++ ) {
                if ((svdFilter.get(i)+1) == jSnumSV.getModel().getValue()) {
                    svdFilter.remove(i);
                }
            }
        }
        
    }//GEN-LAST:event_jCBAddtoFilterActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField inputDatasetName;
    private javax.swing.JButton jBAverage;
    private javax.swing.JButton jBConvertToAbs;
    private javax.swing.JButton jBMakeDataset;
    private javax.swing.JButton jBResample;
    private javax.swing.JButton jBSelectData;
    private javax.swing.JButton jBSubtractBG;
    private javax.swing.JButton jBSvdFilter;
    private javax.swing.JButton jBTICorrection;
    private javax.swing.JButton jBUpdLinLog;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCBAddtoFilter;
    private javax.swing.JCheckBox jCBShowAllSVD;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPLeftSingVectors;
    private javax.swing.JPanel jPRightSingVectors;
    private javax.swing.JPanel jPSingValues;
    private javax.swing.JPanel jPSpecImage;
    private javax.swing.JPanel jPXTrace;
    private javax.swing.JPanel jPYTrace;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSlider jSColum;
    private javax.swing.JSlider jSRow;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JSpinner jSnumSV;
    private javax.swing.JTextArea jTAInfo;
    private javax.swing.JToggleButton jTBLinLog;
    private javax.swing.JToggleButton jTBZoomX;
    private javax.swing.JToggleButton jTBZoomY;
    private javax.swing.JTextField jTFLinPart;
    private javax.swing.JTextField jTFMaxIntence;
    private javax.swing.JTextField jTFMinIntence;
    private javax.swing.JTextField jTFtotalNumSV;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private com.jidesoft.swing.RangeSlider rangeSlider1;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized SpecEditorTopCompNew getDefault() {
        if (instance == null) {
            instance = new SpecEditorTopCompNew();
        }
        return instance;
    }

    /**
     * Obtain the StreakLoaderTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized SpecEditorTopCompNew findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(SpecEditorTopCompNew.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof SpecEditorTopCompNew) {
            return (SpecEditorTopCompNew) win;
        }
        Logger.getLogger(SpecEditorTopCompNew.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return CloneableTopComponent.PERSISTENCE_NEVER;
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

    private Matrix[] calculateSVD() {
        Matrix newMatrix = MatrixFactory.importFromArray(data.getPsisim());
        newMatrix = newMatrix.reshape(Ret.NEW, data.getNt(), data.getNl());
        //Matrix newMatrix = MatrixFactory.dense(ValueType.BYTE, data.getNt(),data.getNl());
        //newMatrix.fill(Ret.LINK, data.getPsisim());

//        controller = Lookup.getDefault().lookup(TimpControllerInterface.class);
//        if (controller != null) {
//            //svdResult = controller.doSingularValueDecomposition(newMatrix);
//        } else {
//            //JAMA implementation is relatively slow, in the future should use UJMP.
//            svdResult = newMatrix.svd();
//        }
        svdResult = newMatrix.svd();

        return svdResult;
    }
    
    private void updateSVDVectorsPlot(){
        int n = (Integer) jSnumSV.getModel().getValue();
        XYSeriesCollection lSVCollection = new XYSeriesCollection();
        XYSeries seria;
        int startFrom = jCBShowAllSVD.isSelected()? 0 : n<1 ? 0 : n-1;

        for (int j = startFrom; j < n; j++) {
            seria = new XYSeries("LSV1" + j + 1);
            for (int i = 0; i < data.getX().length; i++) {
                seria.add(data.getX()[i], svdResult[0].getAsDouble((long) i, (long) j));
            }
            lSVCollection.addSeries(seria);
        }
        leftSVChart.getXYPlot().setDataset(lSVCollection);

        XYSeriesCollection rSVCollection = new XYSeriesCollection();
        for (int j = startFrom; j < n; j++) {
            seria = new XYSeries("RSV" + (j + 1));
            for (int i = 0; i < data.getX2().length; i++) {
                seria.add(data.getX2()[i], svdResult[2].getAsDouble((long) i, (long) j));
            }
            rSVCollection.addSeries(seria);
        }

        rightSVChart.getXYPlot().setDataset(rSVCollection);
        
        boolean included = false;
        for (int i = 0; i < svdFilter.size(); i++) {
            if ((svdFilter.get(i)+1) == (Integer)jSnumSV.getModel().getValue()) {
                    jCBAddtoFilter.setSelected(true);    
                    included = true;
            }    
        }
        if (!included){
            jCBAddtoFilter.setSelected(false);           
        }
    }

    private void createSVDPlots() {
        if (svdResult!=null) {
        int maxSpinnerNumberModel = (int) Math.min(MAX_NUMBER_SINGULAR_VALUES, svdResult[1].getRowCount());
        jTFtotalNumSV.setText("Max " + maxSpinnerNumberModel + " of  " + String.valueOf(svdResult[1].getRowCount()));
        jSnumSV.setModel(new SpinnerNumberModel((int)1, (int)0, maxSpinnerNumberModel, (int)1));
        //creare collection with first 2 LSV
        XYSeriesCollection lSVCollection = new XYSeriesCollection();
        XYSeries seria;
        seria = new XYSeries("LSV1");
        for (int i = 0; i < data.getX().length; i++) {
            seria.add(data.getX()[i], svdResult[0].getAsDouble((long) i, 0l));
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
        leftSVChart.getXYPlot().getDomainAxis().setUpperBound(data.getX()[data.getX().length - 1]);
        leftSVChart.getXYPlot().getDomainAxis().setAutoRange(false);
        GraphPanel chpan = new GraphPanel(leftSVChart);
        //add chart with 2 LSV to JPannel
        jPLeftSingVectors.removeAll();
        jPLeftSingVectors.add(chpan);

        //creare collection with first 2 RSV
        XYSeriesCollection rSVCollection = new XYSeriesCollection();
        seria = new XYSeries("RSV1");
        for (int i = 0; i < data.getX2().length; i++) {
            seria.add(data.getX2()[i], svdResult[2].getAsDouble((long) i, 0l));
        }
        rSVCollection.addSeries(seria);

        //creare chart for 2 RSV
        rightSVChart = ChartFactory.createXYLineChart(
                "Right singular vectors",
                "Wavelength (nm)",
                null,
                rSVCollection,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        
        rightSVChart.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
        chpan = new GraphPanel(rightSVChart);
        //add chart with 2 RSV to JPannel
        jPRightSingVectors.removeAll();
        jPRightSingVectors.add(chpan);

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
//        long index = svdResult[1].getRowCount() - 1;
//        while (svdResult[1].getAsDouble(index, 0l) <= 0) {
//            index--;
//        }
//        logAxe.setRange(svdResult[1].getAsDouble(514l, 0l), svdResult[1].getAsDouble(0l, 0l));
        //  logAxe.setLowerBound(svdResult.getSingularValues()[svdResult.getSingularValues().length-2]);
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
        } else {
            CoreErrorMessages.noSVDCalculated();
            //Matrix newMatrix = MatrixFactory.importFromArray(data.getPsisim());
            //newMatrix = newMatrix.reshape(Ret.NEW, data.getNt(), data.getNl());
            //newMatrix = newMatrix.deleteRowsWithMissingValues(Ret.NEW, 10);
            //newMatrix = newMatrix.deleteColumnsWithMissingValues(Ret.NEW);
            //newMatrix.showGUI();
        }
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return SpecEditorTopCompNew.getDefault();
        }
    }

    private ColorCodedImageDataset MakeXYZDataset() {
        dataset = new ColorCodedImageDataset(data.getNl(), data.getNt(),
                data.getPsisim(), data.getX2(), data.getX(), true);
        return dataset;
    }

    @Override
    public void chartChanged(ChartChangeEvent event) {
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
            XYPlot plot2 = (XYPlot) this.subchartWaveTrace.getPlot();
            lowInd = (int) (this.lastXRange.getLowerBound());
            upInd = (int) (this.lastXRange.getUpperBound() - 1);
            plot2.getDomainAxis().setRange(new Range(data.getX2()[lowInd],data.getX2()[upInd]));
            jSColum.setMinimum(lowInd);
            jSColum.setMaximum(upInd);

        }

        if (!plot.getRangeAxis().getRange().equals(this.lastYRange)) {
            this.lastYRange = plot.getRangeAxis().getRange();
            XYPlot plot1 = (XYPlot) this.subchartTimeTrace.getPlot();
            lowInd = (int) (this.wholeYRange.getUpperBound() - this.lastYRange.getUpperBound());
            upInd = (int) (this.wholeYRange.getUpperBound() - this.lastYRange.getLowerBound() - 1);
            plot1.getDomainAxis().setRange(new Range(data.getX()[lowInd], data.getX()[upInd]));
            plot1.getRangeAxis().setAutoRange(true);
            jSRow.setMinimum(lowInd);
            jSRow.setMaximum(upInd);
        }
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
//        PaintScale ps = new RedGreenPaintScale(dataMin, dataMax);
        this.chartMain = createChart(new XYSeriesCollection());
        this.chartMain.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);

        XYPlot tempPlot = (XYPlot) this.chartMain.getPlot();
        this.wholeXRange = tempPlot.getDomainAxis().getRange();
        this.wholeYRange = tempPlot.getRangeAxis().getRange();
        //added
        DecimalFormat formatter = new DecimalFormat("##0E0");
        NumberAxis xAxis = new NumberAxis("Wavelength (nm) → ");
//        xAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
//        xAxis.setLowerMargin(0.0);
//        xAxis.setUpperMargin(0.0);
//        xAxis.setAxisLinePaint(Color.white);
//        xAxis.setTickMarkPaint(Color.white);
//        xAxis.setRange(res.getX()2[0], res.getX2()[res.getX2().length-1]);
        int numberOfTicks = Math.min(data.getX2().length,MAX_NO_TICKS);
        NonLinearNumberTickUnit xTickUnit = new NonLinearNumberTickUnit(data.getX2().length/numberOfTicks, formatter,data.getX2());
        xAxis.setTickUnit(xTickUnit);
        xAxis.setTickLabelsVisible(true);
        tempPlot.setDomainAxis(xAxis);
        
        NumberAxis yAxis = new NumberAxis("← Time (ps)");
//        yAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
//        yAxis.setLowerMargin(0.0);
//        yAxis.setUpperMargin(0.0);
//        yAxis.setAxisLinePaint(Color.white);
//        yAxis.setTickMarkPaint(Color.white);
//        yAxis.setRange(res.getX()[0], res.getX()[res.getX().length-1]);
        formatter = new DecimalFormat("##0.#E0");
        numberOfTicks = Math.min(data.getX().length,MAX_NO_TICKS);
         NonLinearNumberTickUnit yTickUnit = new NonLinearNumberTickUnit(data.getX().length/numberOfTicks, formatter,data.getX(),true);
        yAxis.setTickUnit(yTickUnit);
        yAxis.setTickLabelsVisible(true);
        tempPlot.setRangeAxis(yAxis);
        chpanImage = new HeightMapPanel(chartMain,true);
        chpanImage.setFillZoomRectangle(true);
        chpanImage.setMouseWheelEnabled(true);
        chpanImage.setZoomFillPaint(new Color(68, 68, 78, 63));
        jPSpecImage.removeAll();
//        chpanImage.setSize(jPSpecImage.getMaximumSize());
        jPSpecImage.setLayout(new BorderLayout());

        ImageCrosshairLabelGenerator crossLabGen1 = new ImageCrosshairLabelGenerator(data.getX2(), false);
        ImageCrosshairLabelGenerator crossLabGen2 = new ImageCrosshairLabelGenerator(data.getX(), true);

        CrosshairOverlay overlay = new CrosshairOverlay();
        crosshair1 = new Crosshair(0.0);
        crosshair1.setPaint(Color.red);
        crosshair2 = new Crosshair(data.getNt());
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

        jPSpecImage.add(chpanImage);
        //TODO: auto scale the JSlider jSColum to the size of the chart
        //chpanImage.getChartRenderingInfo().getChartArea().getWidth();
        //jSColum.setBounds(jSColum.getBounds().x, jSColum.getBounds().y,(int)chpanImage.getChartRenderingInfo().getChartArea().getBounds().width,jSColum.getHeight());

        this.chartMain.addChangeListener((ChartChangeListener) this);

        XYSeriesCollection dataset1 = new XYSeriesCollection();
        subchartTimeTrace = ChartFactory.createXYLineChart(
                null,
                null,
                null,
                dataset1,
                PlotOrientation.HORIZONTAL,
                false,
                false,
                false);
        subchartTimeTrace.getXYPlot().getDomainAxis().setUpperBound(data.getX()[data.getX().length - 1]);
        subchartTimeTrace.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
////        tracechart.getXYPlot().setDomainZeroBaselineVisible(true);
        GraphPanel chpan = new GraphPanel(subchartTimeTrace);
//        chpan.setSize(jPYTrace.getMaximumSize());
        jPYTrace.removeAll();
        jPYTrace.setLayout(new BorderLayout());
        chpan.setMinimumDrawHeight(0);
        chpan.setMinimumDrawWidth(0);
        jPYTrace.add(chpan);

        XYPlot plot1 = (XYPlot) subchartTimeTrace.getPlot();
        plot1.getDomainAxis().setLowerMargin(0.0);
        plot1.getDomainAxis().setUpperMargin(0.0);
        plot1.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        plot1.getDomainAxis().setInverted(true);

        XYSeriesCollection dataset2 = new XYSeriesCollection();
        subchartWaveTrace = ChartFactory.createXYLineChart(
                null,
                null,
                null,
                dataset2,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        if (data.getX2()[data.getX2().length - 1] < data.getX2()[0]) {
            subchartWaveTrace.getXYPlot().getDomainAxis().setUpperBound(data.getX2()[0]);
            subchartWaveTrace.getXYPlot().getDomainAxis().setInverted(true);
        } else {
            subchartWaveTrace.getXYPlot().getDomainAxis().setUpperBound(data.getX2()[data.getX2().length - 1]);
        }

        XYPlot plot2 = (XYPlot) subchartWaveTrace.getPlot();
        plot2.getDomainAxis().setLowerMargin(0.0);
        plot2.getDomainAxis().setUpperMargin(0.0);
        plot2.getDomainAxis().setAutoRange(true);
        //      plot2.getDomainAxis().resizeRange(100);
        plot2.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
        plot2.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        GraphPanel subchart2Panel = new GraphPanel(subchartWaveTrace);
//        subchart2Panel.setSize(jPXTrace.getMaximumSize());
        jPXTrace.removeAll();
        jPXTrace.setLayout(new BorderLayout());
        subchart2Panel.setMinimumDrawHeight(0);
        subchart2Panel.setMinimumDrawWidth(0);
        jPXTrace.add(subchart2Panel);

        jSColum.setValueIsAdjusting(true);
        jSColum.setMaximum(dataset.GetImageWidth() - 1);
        jSColum.setMinimum(0);
        jSColum.setValue(0);
        jSColum.setValueIsAdjusting(false);

        jSRow.setValueIsAdjusting(true);
        jSRow.setMaximum(dataset.GetImageHeigth() - 1);
        jSRow.setMinimum(0);
        jSRow.setValue(0);
        jSRow.setValueIsAdjusting(false);

        NumberAxis scaleAxis = new NumberAxis();
        scaleAxis.setAxisLinePaint(Color.black);
        scaleAxis.setTickMarkPaint(Color.black);
        scaleAxis.setRange(data.getMinInt(), data.getMaxInt());
        scaleAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
        PaintScaleLegend legend = new PaintScaleLegend(ps, scaleAxis);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        //        legend.setAxisOffset(5.0);
        legend.setMargin(new RectangleInsets(5, 5, 5, 5));
        //        legend.setFrame(new BlockBorder(Color.red));
        //        legend.setPadding(new RectangleInsets(5, 5, 5, 5));
        legend.setStripWidth(15);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(chartMain.getBackgroundPaint());
        chartMain.addSubtitle(legend);

        this.chartMain.addChangeListener((ChartChangeListener) this);

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
        tempString = "Time step: " + String.valueOf(data.getX()[1] - data.getX()[0]) + "\n";
        jTAInfo.append(tempString);

        tempString = "Wave window: " + String.valueOf(data.getX2()[data.getNl() - 1] - data.getX2()[0]) + "\n";
        jTAInfo.append(tempString);
        tempString = "Nuber of wave steps: " + String.valueOf(data.getNl()) + "\n";
        jTAInfo.append(tempString);
        tempString = "Wave step: " + String.valueOf(data.getX2()[1] - data.getX2()[0]) + "\n";
        jTAInfo.append(tempString);

        jTFMaxIntence.setText(String.valueOf(data.getMaxInt()));
        jTFMinIntence.setText(String.valueOf(data.getMinInt()));
        rangeSlider1.setMinimum(0);
        rangeSlider1.setMaximum(99);
        rangeSlider1.setLowValue(0);
        rangeSlider1.setHighValue(99);
        rangeSlider1.setRangeDraggable(true);

        rangeSlider1.setPaintLabels(true);
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
        rangeSlider1.setLabelTable(labels);
        rangeSlider1.setPaintTicks(true);
    }
}
