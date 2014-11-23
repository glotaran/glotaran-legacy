package org.glotaran.core.datadisplayers.flim;

import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import org.ujmp.core.Matrix;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.TgdDataChildren;
import org.glotaran.core.main.nodes.TgdDataNode;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.jfreechartcustom.FastXYPlot;
import org.glotaran.jfreechartcustom.GraphPanel;
import org.glotaran.jfreechartcustom.GrayPaintScalePlus;
import org.glotaran.jfreechartcustom.IntensImageDataset;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import static java.lang.Math.round;
import java.util.Collection;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import org.glotaran.core.datadisplayers.common.CommonDataDispTools;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.main.common.CommonActionFunctions;
import org.glotaran.core.main.common.ExportPanelForm;
import org.glotaran.core.models.structures.FlimImageAbstract;
import org.glotaran.jfreechartcustom.HeightMapPanel;
import org.glotaran.jfreechartcustom.ImageUtilities;
import org.glotaran.jfreechartcustom.RedGreenPaintScale;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.CloneableTopComponent;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.intmatrix.impl.DefaultDenseIntMatrix2D;
import org.ujmp.core.matrix.Matrix2D;

/**
 * Top component which displays something.
 */
final public class SdtTopComponent extends CloneableTopComponent implements ChartMouseListener {

    private FlimImageAbstract flimImage;
    private JFreeChart chart;
    private ChartPanel chpanIntenceImage;
    private GraphPanel chpanSelectedTrace;
    private JFreeChart leftSVChart;
    private JFreeChart rightSVChart;
    private IntensImageDataset dataset;
    private XYSeriesCollection tracesCollection;
    private int numSelPix;
    private int oldSelPix;
    private TgdDataObject dataObject;
    private TimpDatasetDataObject timpDatasetObject;
    private Matrix[] svdResult;
    private IntervalMarker marker;
    private static SdtTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "SdtTopComponent";
    private TGProject project;
    private int MAX_NUMBER_SINGULAR_VALUES = 20;

    public SdtTopComponent() {
        flimImage = null;
        chart = null;
        chpanIntenceImage = null;
        dataset = null;
        numSelPix = 0;
        tracesCollection = null;
        initComponents();
        setName(NbBundle.getMessage(SdtTopComponent.class, "CTL_SdtTopComponent"));
        setToolTipText(NbBundle.getMessage(SdtTopComponent.class, "HINT_SdtTopComponent"));
    }

    public SdtTopComponent(TgdDataObject dataObj) {
        dataObject = dataObj;
        initComponents();
        setName(dataObj.getName());

        File tgdFile;
        dataObject = dataObj;
        initComponents();
        setName(dataObject.getTgd().getFilename());

        //try to get the file from local cache
        project = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
        if (dataObject.getTgd().getRelativePath() != null) {
            tgdFile = new File(project.getProjectDirectory().getPath() + File.separator + dataObject.getTgd().getRelativePath());
        } else { //try the orginal location
            tgdFile = new File(dataObject.getTgd().getPath());
            if (!tgdFile.exists()) {
                tgdFile = new File(dataObject.getTgd().getPath() + File.separator + dataObject.getTgd().getFilename());
            }
        }

        setToolTipText(NbBundle.getMessage(SdtTopComponent.class, "HINT_SdtTopComponent"));
        
        flimImage = new FlimImageAbstract();
        
        Collection<? extends TGDatasetInterface> services = Lookup.getDefault().lookupAll(TGDatasetInterface.class);
        for (final TGDatasetInterface service : services) {
            try {
                if (service.Validator(tgdFile)) {
                    flimImage = service.loadFlimFile(tgdFile);
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
        
        if (flimImage != null) {
//            flimImage.makeBinnedImage(1);
            flimImage.buildIntMap(1);
            MakeIntImageChart(MakeXYZDataset());
            MakeTracesChart(PlotFirstTrace(0), false);
            chpanIntenceImage = new HeightMapPanel(chart, true);
            chpanIntenceImage.setMouseZoomable(false);
            chpanIntenceImage.addChartMouseListener((ChartMouseListener) this);
            jPIntensImage.add(chpanIntenceImage);
            jLNumSelPix.setText(Integer.toString(numSelPix));
            jTFMaxIntence.setText(Integer.toString(flimImage.getMaxIntens()));
            jLChNumm.setText(Integer.toString(flimImage.getCannelN()));
            jLHeigth.setText(Integer.toString(flimImage.getY()));
            jLWidth.setText(Integer.toString(flimImage.getX()));
            jLChWidth.setText(Double.toString(flimImage.getCannelW()).substring(0, 7));
            rangeSlider1.setHighValue(flimImage.getMaxIntens());
            rangeSlider1.setLowValue(flimImage.getMinIntens());
            jTFMinIntence.setText(Integer.toString(flimImage.getMinIntens()));
            sumSelectedPixels();
        }
    }

    public SdtTopComponent(TimpDatasetDataObject dataObj) {
        this.timpDatasetObject = dataObj;
        String filename=new String();
        DatasetTimp tempData;
        TgdDataNode tddNode = (TgdDataNode) timpDatasetObject.getNodeDelegate().getParentNode();
        dataObject = (TgdDataObject) tddNode.getDataObject();
        project = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
        initComponents();
        setName(dataObject.getTgd().getFilename());
        if(dataObject.getTgd().getRelativePath()!= null){
            filename = project.getProjectDirectory().getPath().concat(File.separator).concat(dataObject.getTgd().getRelativePath());
        
        }
        else{
         filename = dataObject.getTgd().getPath();
        }
//        filename = filename.concat("/").concat(dataObject.getTgd().getFilename());
        setToolTipText(NbBundle.getMessage(SdtTopComponent.class, "HINT_SdtTopComponent"));
        
        flimImage = new FlimImageAbstract();
        File tgdFile = new File(filename);
        
        Collection<? extends TGDatasetInterface> services = Lookup.getDefault().lookupAll(TGDatasetInterface.class);
        for (final TGDatasetInterface service : services) {
            try {
                if (service.Validator(tgdFile)) {
                    flimImage = service.loadFlimFile(tgdFile);
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
        
//        flimImage.makeBinnedImage(1);
        tempData = dataObj.getDatasetTimp();
        flimImage.buildIntMap(1);
        if (tempData.getMaxInt() > flimImage.getMaxIntens()) {
            flimImage.setBinned(1);
            jTButBin.setSelected(true);
            flimImage.buildIntMap(1);
        }

        MakeXYZDataset();
        for (int i = 0; i < tempData.getNl(); i++) {
            dataset.SetValue((int) tempData.getX2()[i], -1);
        }
        numSelPix = tempData.getNl();
        jLNumSelPix.setText(Integer.toString(numSelPix));
        MakeIntImageChart(dataset);
        MakeTracesChart(PlotFirstTrace((int) tempData.getX2()[0]), false);
        chpanIntenceImage = new HeightMapPanel(chart, true);
        chpanIntenceImage.setMouseZoomable(false);
        chpanIntenceImage.addChartMouseListener((ChartMouseListener) this);
        jPIntensImage.add(chpanIntenceImage);
        jLNumSelPix.setText(Integer.toString(numSelPix));
        jTFMaxIntence.setText(Integer.toString(flimImage.getMaxIntens()));
        jLChNumm.setText(Integer.toString(flimImage.getCannelN()));
        jLHeigth.setText(Integer.toString(flimImage.getY()));
        jLWidth.setText(Integer.toString(flimImage.getX()));
        jLChWidth.setText(Double.toString(flimImage.getCannelW()).substring(0, 7));
        rangeSlider1.setHighValue(flimImage.getMaxIntens());
        rangeSlider1.setLowValue(flimImage.getMinIntens());
        jTFMinIntence.setText(Integer.toString(flimImage.getMinIntens()));
        sumSelectedPixels();
    }

    public TgdDataObject getDataObject() {
        return dataObject;
    }

    public TimpDatasetDataObject getTimpDatasetObject() {
        return timpDatasetObject;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTPFlimTabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jBMakeDataset = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jBSaveIvoFile = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jTButBin = new javax.swing.JToggleButton();
        jTButAmpl = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton1 = new javax.swing.JButton();
        jPIntensImage = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLChNumm = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLChWidth = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLWidth = new javax.swing.JLabel();
        jLHeigth = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLNumSelPix = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTFPortion = new javax.swing.JTextField();
        jBSelect = new javax.swing.JButton();
        jBUnselect = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTFMaxIntence = new javax.swing.JTextField();
        jTFMinIntence = new javax.swing.JTextField();
        rangeSlider1 = new com.jidesoft.swing.RangeSlider();
        jPanel4 = new javax.swing.JPanel();
        jPSelectedTrace = new javax.swing.JPanel();
        jPSumTrace = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        rangeSlider2 = new com.jidesoft.swing.RangeSlider();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel7 = new javax.swing.JPanel();
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

        setPreferredSize(new java.awt.Dimension(900, 800));

        jTPFlimTabs.setPreferredSize(new java.awt.Dimension(900, 800));

        jPanel2.setPreferredSize(new java.awt.Dimension(500, 500));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jBMakeDataset, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jBMakeDataset.text")); // NOI18N
        jBMakeDataset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBMakeDatasetActionPerformed(evt);
            }
        });
        jToolBar1.add(jBMakeDataset);
        jToolBar1.add(jSeparator4);

        org.openide.awt.Mnemonics.setLocalizedText(jBSaveIvoFile, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jBSaveIvoFile.text")); // NOI18N
        jBSaveIvoFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveIvoFileActionPerformed(evt);
            }
        });
        jToolBar1.add(jBSaveIvoFile);
        jToolBar1.add(jSeparator3);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getBundle(SdtTopComponent.class).getString("SdtTopComponent.jLabel1.text")); // NOI18N
        jToolBar1.add(jLabel1);
        jToolBar1.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(jTButBin, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jTButBin.text")); // NOI18N
        jTButBin.setFocusable(false);
        jTButBin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTButBin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTButBin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTButBinActionPerformed(evt);
            }
        });
        jToolBar1.add(jTButBin);

        org.openide.awt.Mnemonics.setLocalizedText(jTButAmpl, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jTButAmpl.text")); // NOI18N
        jTButAmpl.setFocusable(false);
        jTButAmpl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jTButAmpl.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jTButAmpl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTButAmplActionPerformed(evt);
            }
        });
        jToolBar1.add(jTButAmpl);
        jToolBar1.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(jToolBar1, gridBagConstraints);

        jPIntensImage.setBackground(new java.awt.Color(0, 0, 0));
        jPIntensImage.setMaximumSize(new java.awt.Dimension(500, 400));
        jPIntensImage.setMinimumSize(new java.awt.Dimension(500, 400));
        jPIntensImage.setPreferredSize(new java.awt.Dimension(500, 400));
        jPIntensImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPIntensImageMouseReleased(evt);
            }
        });
        jPIntensImage.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 0);
        jPanel2.add(jPIntensImage, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getBundle(SdtTopComponent.class).getString("SdtTopComponent.jPanel3.border.title"))); // NOI18N
        jPanel3.setMaximumSize(new java.awt.Dimension(460, 85));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 6, 0);
        jPanel3.add(jLabel6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLChNumm, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLChNumm.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 6, 0);
        jPanel3.add(jLChNumm, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 6, 0);
        jPanel3.add(jLabel9, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLChWidth, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLChWidth.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 6, 0);
        jPanel3.add(jLChWidth, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 6, 0);
        jPanel3.add(jLabel8, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 6, 0);
        jPanel3.add(jLabel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLWidth, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLWidth.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 6, 0);
        jPanel3.add(jLWidth, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLHeigth, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLHeigth.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 6, 0);
        jPanel3.add(jLHeigth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        jPanel2.add(jPanel3, gridBagConstraints);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jPanel5.border.title"))); // NOI18N
        jPanel5.setMaximumSize(new java.awt.Dimension(418, 105));
        jPanel5.setMinimumSize(new java.awt.Dimension(418, 105));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel5.add(jLabel3, gridBagConstraints);

        jLNumSelPix.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLNumSelPix, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLNumSelPix.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel5.add(jLNumSelPix, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel5.add(jLabel2, gridBagConstraints);

        jTFPortion.setText(org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jTFPortion.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 0, 0);
        jPanel5.add(jTFPortion, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jBSelect, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jBSelect.text")); // NOI18N
        jBSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSelectActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        jPanel5.add(jBSelect, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jBUnselect, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jBUnselect.text")); // NOI18N
        jBUnselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBUnselectActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        jPanel5.add(jBUnselect, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        jPanel2.add(jPanel5, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jPanel6.border.title"))); // NOI18N
        jPanel6.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel12.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 0, 6, 6);
        jPanel6.add(jLabel12, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel13.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 6, 0);
        jPanel6.add(jLabel13, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jButton2.text")); // NOI18N
        jButton2.setIconTextGap(2);
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 6, 0);
        jPanel6.add(jButton2, gridBagConstraints);

        jTFMaxIntence.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMaxIntence.setText(org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jTFMaxIntence.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 6, 0);
        jPanel6.add(jTFMaxIntence, gridBagConstraints);

        jTFMinIntence.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTFMinIntence.setText(org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jTFMinIntence.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 6, 20);
        jPanel6.add(jTFMinIntence, gridBagConstraints);

        rangeSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rangeSlider1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.ipadx = 229;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel6.add(rangeSlider1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 0);
        jPanel2.add(jPanel6, gridBagConstraints);

        jPanel4.setMinimumSize(new java.awt.Dimension(500, 500));
        jPanel4.setPreferredSize(new java.awt.Dimension(500, 600));
        jPanel4.setRequestFocusEnabled(false);
        jPanel4.setVerifyInputWhenFocusTarget(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPSelectedTrace.setBackground(new java.awt.Color(255, 255, 255));
        jPSelectedTrace.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPSelectedTrace.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jPSelectedTrace.setPreferredSize(new java.awt.Dimension(500, 600));
        jPSelectedTrace.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        jPanel4.add(jPSelectedTrace, gridBagConstraints);

        jPSumTrace.setBackground(new java.awt.Color(255, 255, 255));
        jPSumTrace.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPSumTrace.setPreferredSize(new java.awt.Dimension(500, 600));
        jPSumTrace.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        jPanel4.add(jPSumTrace, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jPanel1.border.title"))); // NOI18N
        jPanel1.setMinimumSize(new java.awt.Dimension(600, 49));
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 49));
        jPanel1.setLayout(new java.awt.BorderLayout());

        rangeSlider2.setValue(30);
        rangeSlider2.setHighValue(60);
        rangeSlider2.setMinimumSize(new java.awt.Dimension(500, 25));
        rangeSlider2.setPreferredSize(new java.awt.Dimension(500, 25));
        rangeSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rangeSlider2StateChanged(evt);
            }
        });
        jPanel1.add(rangeSlider2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel2.add(jPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(filler2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(filler3, gridBagConstraints);

        jTPFlimTabs.addTab(org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel7.setPreferredSize(new java.awt.Dimension(500, 500));
        jPanel7.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel7ComponentShown(evt);
            }
        });

        jToolBar3.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel10.text")); // NOI18N
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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel11.text")); // NOI18N
        jToolBar3.add(jLabel11);

        jTFtotalNumSV.setEditable(false);
        jTFtotalNumSV.setText(org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jTFtotalNumSV.text")); // NOI18N
        jTFtotalNumSV.setMaximumSize(new java.awt.Dimension(100, 20));
        jTFtotalNumSV.setMinimumSize(new java.awt.Dimension(45, 20));
        jTFtotalNumSV.setPreferredSize(new java.awt.Dimension(55, 20));
        jToolBar3.add(jTFtotalNumSV);
        jToolBar3.add(jSeparator6);

        jPanel10.setPreferredSize(new java.awt.Dimension(500, 500));
        jPanel10.setLayout(new java.awt.GridBagLayout());

        jPSingValues.setBackground(new java.awt.Color(255, 255, 255));
        jPSingValues.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel10.add(jPSingValues, gridBagConstraints);

        jPLeftSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPLeftSingVectors.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel10.add(jPLeftSingVectors, gridBagConstraints);

        jPRightSingVectors.setBackground(new java.awt.Color(255, 255, 255));
        jPRightSingVectors.setLayout(new java.awt.GridLayout(2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 2.0;
        jPanel10.add(jPRightSingVectors, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jLabel5.text")); // NOI18N
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel10.add(jLabel5, gridBagConstraints);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 1121, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE))
        );

        jTPFlimTabs.addTab(org.openide.util.NbBundle.getMessage(SdtTopComponent.class, "SdtTopComponent.jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTPFlimTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 1126, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTPFlimTabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
///save dataset in timpformat
   private DatasetTimp createTimpDataset(String name, FlimImageAbstract v_flimImage,int numberSelPixels,IntensImageDataset v_dataset ) {                                              
            DatasetTimp timpDat = new DatasetTimp();
            timpDat.setType("flim");
            timpDat.setDatasetName(name);

            timpDat.setIntenceIm(intToDoubleArray(v_flimImage.getIntMap()));
            int k = 0;

            timpDat.setPsisim(new double[v_flimImage.getCannelN() * numberSelPixels]);
            timpDat.setX2(new double[numberSelPixels]);
            timpDat.setNl(numberSelPixels);
            timpDat.setNt(v_flimImage.getCannelN());
            timpDat.setOrigHeigh(v_flimImage.getY());
            timpDat.setOrigWidth(v_flimImage.getX());

            for (int i = 0; i < v_flimImage.getCurveNum(); i++) {
                if (v_dataset.getZValue(1, i) == -1) {
                    for (int j = 0; j < v_flimImage.getCannelN(); j++) {
                        timpDat.getPsisim()[k * v_flimImage.getCannelN() + j] = v_flimImage.getDataPoint(i * flimImage.getCannelN() + j);
                    }
                    timpDat.getX2()[k] = i;
                    k++;
                }
            }
            timpDat.setX(new double[v_flimImage.getCannelN()]);
            for (int i = 0; i < v_flimImage.getCannelN(); i++) {
                timpDat.getX()[i] = i * v_flimImage.getCannelW();
            }

            timpDat.setMaxInt(v_flimImage.getMaxIntens());
            timpDat.setMinInt(v_flimImage.getMinIntens());

       
       return timpDat;
   }                                             


    
    private void jBMakeDatasetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBMakeDatasetActionPerformed
        NotifyDescriptor.InputLine datasetNameDialog = new NotifyDescriptor.InputLine(
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("dataset_name"),
                NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("spec_datasetname"));
        Object res = DialogDisplayer.getDefault().notify(datasetNameDialog);

        if (res.equals(NotifyDescriptor.OK_OPTION)) {
            DatasetTimp timpDat = createTimpDataset(datasetNameDialog.getInputText(), flimImage, numSelPix, dataset);
   timpDat.getMaxInt();
//create serfile
            FileObject cachefolder = null;
            final TGProject proj = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            if (proj != null) {
                cachefolder = proj.getCacheFolder(true);
                cachefolder = cachefolder.getFileObject(dataObject.getTgd().getCacheFolderName().toString());
                FileObject writeTo;
                try {
                    writeTo = cachefolder.createData(timpDat.getDatasetName(), "timpdataset");
                    ObjectOutputStream stream = new ObjectOutputStream(writeTo.getOutputStream());
                    stream.writeObject(timpDat);
                    stream.close();
                    TimpDatasetDataObject dObj = (TimpDatasetDataObject) DataObject.find(writeTo);
                    TgdDataChildren chidrens = (TgdDataChildren) dataObject.getNodeDelegate().getChildren();
                    chidrens.addObj(dObj);
                } catch (IOException ex) {
                    CoreErrorMessages.IOException("(create new .timpdataset file)");
                }

            } else {
                CoreErrorMessages.noMainProjectFound();
            }
        }
    }//GEN-LAST:event_jBMakeDatasetActionPerformed

    private void jBSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSelectActionPerformed
        double portion = 0.7;
        dataset.SetActive(false);

        try {
            portion = Double.parseDouble(jTFPortion.getText());
        } catch (NumberFormatException ex) {
            NotifyDescriptor errorMessage = new NotifyDescriptor.Exception(
                    new Exception(NbBundle.getBundle("org/glotaran/core/datadisplayers/Bundle").getString("wrongPortion")));
        }

        for (int i = 0; i < flimImage.getCurveNum(); i++) {
            if (dataset.getZValue(1, i) > portion * flimImage.getMaxIntens()) {
                dataset.SetValue(i, -1);
                numSelPix++;
            }
        }
        jLNumSelPix.setText(Integer.toString(numSelPix));
        sumSelectedPixels();
        dataset.SetActive(true);
        dataset.Update();
    }//GEN-LAST:event_jBSelectActionPerformed

    private void jBUnselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBUnselectActionPerformed
        dataset.SetIntenceImage(flimImage.getIntMap().clone());
        numSelPix = 0;
        jLNumSelPix.setText(Integer.toString(numSelPix));
        sumSelectedPixels();
    }//GEN-LAST:event_jBUnselectActionPerformed

    private void jSnumSVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSnumSVStateChanged
        updateSVDPlots();
    }//GEN-LAST:event_jSnumSVStateChanged

    private void jTButBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTButBinActionPerformed
        updateXYZDataset();
    }//GEN-LAST:event_jTButBinActionPerformed

    private void jTButAmplActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTButAmplActionPerformed
        updateXYZDataset();
    }//GEN-LAST:event_jTButAmplActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        double newMinAmpl, newMaxAmpl;
        try {
            newMinAmpl = Double.parseDouble(jTFMinIntence.getText());
            newMaxAmpl = Double.parseDouble(jTFMaxIntence.getText());
            updateIntenceImageChart(newMinAmpl, newMaxAmpl);
        } catch (NumberFormatException ex) {
            NotifyDescriptor errorMessage = new NotifyDescriptor.Exception(
                    new Exception(NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("set_correct_chanNum")));
            DialogDisplayer.getDefault().notify(errorMessage);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jPanel7ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel7ComponentShown
        
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
    }//GEN-LAST:event_jPanel7ComponentShown

    private void updateSVDPlots() {
        XYSeriesCollection lSVCollection = new XYSeriesCollection();
        XYSeries seria;
        for (int j = 0; j < (Integer) jSnumSV.getValue(); j++) {
            seria = new XYSeries("LSV" + j + 1);
            for (int i = 0; i < flimImage.getCannelN(); i++) {
                seria.add(i * flimImage.getCannelW(), svdResult[0].getAsDouble((long) i, j));
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
                tempRsingVec = new double[flimImage.getX() * flimImage.getY()];
                for (int j = 0; j < flimImage.getX() * flimImage.getY(); j++) {
                    tempValue = svdResult[2].getAsDouble(j, i);
                    tempRsingVec[j] = tempValue;
                    minVal = minVal > tempValue ? tempValue : minVal;
                    maxVal = maxVal < tempValue ? tempValue : maxVal;
                }
                IntensImageDataset rSingVec = new IntensImageDataset(flimImage.getX(), flimImage.getY(), tempRsingVec);
                PaintScale ps = new RedGreenPaintScale(minVal, maxVal);
                JFreeChart rSingVect = CommonDataDispTools.createScatChart(ImageUtilities.createColorCodedImage(rSingVec, ps), ps, flimImage.getX(), flimImage.getY());
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
        for (int i = 0; i < flimImage.getCannelN(); i++) {
            seria.add(i * flimImage.getCannelW(), svdResult[0].getAsDouble((long) i, 0));
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
        tempRsingVec = new double[flimImage.getX() * flimImage.getY()];
        double tempValue;
        for (int i = 0; i < flimImage.getX() * flimImage.getY(); i++) {
            tempValue =  svdResult[2].getAsDouble(i, 0);
            tempRsingVec[i] =tempValue;
            minVal = minVal > tempValue ? tempValue : minVal;
            maxVal = maxVal < tempValue ? tempValue : maxVal;
        }

        IntensImageDataset rSingVec = new IntensImageDataset(flimImage.getX(), flimImage.getY(), tempRsingVec);
        PaintScale ps = new RedGreenPaintScale(minVal, maxVal);
        JFreeChart rSingVect = CommonDataDispTools.createScatChart(ImageUtilities.createColorCodedImage(rSingVec, ps), ps, flimImage.getX(), flimImage.getY());
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
    
    private void jBSaveIvoFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveIvoFileActionPerformed
        
            ExportPanelForm exportDialogPanel = new ExportPanelForm();
            NotifyDescriptor exportDataDialog = new NotifyDescriptor(
                    exportDialogPanel,
                    "Export dataset ...",
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.PLAIN_MESSAGE,
                    null,
                    NotifyDescriptor.CANCEL_OPTION);
            Object res2 = DialogDisplayer.getDefault().notify(exportDataDialog);
            if (res2.equals(NotifyDescriptor.OK_OPTION)) {
                if (!exportDialogPanel.getFileName().isEmpty()){

                    DatasetTimp timpDat = createTimpDataset(exportDialogPanel.getFileName(), flimImage, numSelPix, dataset);
                    CommonActionFunctions.exportSpecDatasets(timpDat, exportDialogPanel.getFileName(), exportDialogPanel.getExportType());
            } else {
                    CoreErrorMessages.fileSaveError(null);
                }
            }
    }//GEN-LAST:event_jBSaveIvoFileActionPerformed

    private void rangeSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rangeSlider1StateChanged
        
        int newMinAmpl, newMaxAmpl;
        Integer range = (flimImage.getMaxIntens() - flimImage.getMinIntens());
        newMinAmpl = flimImage.getMinIntens() + range / (rangeSlider1.getMaximum() - rangeSlider1.getMinimum()) * rangeSlider1.getLowValue();
        newMaxAmpl = flimImage.getMinIntens() + range / (rangeSlider1.getMaximum() - rangeSlider1.getMinimum()) * rangeSlider1.getHighValue();
        if (newMinAmpl < newMaxAmpl) {
            try {
                updateIntenceImageChart(newMinAmpl, newMaxAmpl);

                jTFMinIntence.setText(String.valueOf(newMinAmpl));
                jTFMaxIntence.setText(String.valueOf(newMaxAmpl));

            } catch (NumberFormatException ex) {
                NotifyDescriptor errorMessage = new NotifyDescriptor.Exception(
                        new Exception(NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("set_correct_chanNum")));
                DialogDisplayer.getDefault().notify(errorMessage);
            }
        }
    }//GEN-LAST:event_rangeSlider1StateChanged

    private void rangeSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rangeSlider2StateChanged
        marker.setStartValue(flimImage.getCannelW()*rangeSlider2.getLowValue());
        marker.setEndValue(flimImage.getCannelW()*rangeSlider2.getHighValue());
    }//GEN-LAST:event_rangeSlider2StateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        int newChN = rangeSlider2.getHighValue()- rangeSlider2.getLowValue();
        if (newChN < 2){
            CoreErrorMessages.notEnoughTimesteps();
            return;
        }
        double newTime = newChN*flimImage.getCannelW();
        
        int[] newData = new int[newChN*flimImage.getCurveNum()];
        for (int i = 0; i < flimImage.getCurveNum(); i++){
            for (int j = 0; j < newChN; j++){
                newData[i*newChN+j] = flimImage.getDataPoint(i*flimImage.getCannelN()+rangeSlider2.getLowValue()+j);
            }
        }
        
        flimImage.setData(newData.clone());
        flimImage.setTime(newTime);
        flimImage.setCannelN((short)newChN);
//        flimImage.makeBinnedImage(1);
        updateXYZDataset(); 
        
        sumSelectedPixels();
        jPanel2.revalidate();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jPIntensImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPIntensImageMouseReleased
        // TODO add your handling code here:         
            sumSelectedPixels();        
    }//GEN-LAST:event_jPIntensImageMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jBMakeDataset;
    private javax.swing.JButton jBSaveIvoFile;
    private javax.swing.JButton jBSelect;
    private javax.swing.JButton jBUnselect;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLChNumm;
    private javax.swing.JLabel jLChWidth;
    private javax.swing.JLabel jLHeigth;
    private javax.swing.JLabel jLNumSelPix;
    private javax.swing.JLabel jLWidth;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPIntensImage;
    private javax.swing.JPanel jPLeftSingVectors;
    private javax.swing.JPanel jPRightSingVectors;
    private javax.swing.JPanel jPSelectedTrace;
    private javax.swing.JPanel jPSingValues;
    private javax.swing.JPanel jPSumTrace;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JSpinner jSnumSV;
    private javax.swing.JToggleButton jTButAmpl;
    private javax.swing.JToggleButton jTButBin;
    private javax.swing.JTextField jTFMaxIntence;
    private javax.swing.JTextField jTFMinIntence;
    private javax.swing.JTextField jTFPortion;
    private javax.swing.JTextField jTFtotalNumSV;
    private javax.swing.JTabbedPane jTPFlimTabs;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private com.jidesoft.swing.RangeSlider rangeSlider1;
    private com.jidesoft.swing.RangeSlider rangeSlider2;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized SdtTopComponent getDefault() {
        if (instance == null) {
            instance = new SdtTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the SdtTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized SdtTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(SdtTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof SdtTopComponent) {
            return (SdtTopComponent) win;
        }
        Logger.getLogger(SdtTopComponent.class.getName()).warning(
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

    private Matrix[] calculateSVD() {
        DefaultDenseIntMatrix2D newMatrix = new DefaultDenseIntMatrix2D(flimImage.getCannelN(),flimImage.getX()*flimImage.getY());
        for (int i = 0; i < flimImage.getCannelN(); i ++){
            for (int j = 0; j < flimImage.getX()*flimImage.getY(); j++){
                newMatrix.setInt(flimImage.getDataPoint(j*flimImage.getCannelN()+i),i,j);   
            }
        }
        //Matrix newMatrix = MatrixFactory.importFromArray(flimImage.getData());
       // newMatrix = newMatrix.reshape(Ret.NEW, flimImage.getCannelN(),flimImage.getX()*flimImage.getY());
        svdResult = newMatrix.svd();
        return svdResult;
        
        
    }

    private void sumSelectedPixels() {
        JFreeChart tracechart;
        XYSeriesCollection trace = new XYSeriesCollection();
        XYSeries seria = new XYSeries("Trace");
        double[] sumTrace = new double[flimImage.getCannelN()];

        for (int i = 0; i < sumTrace.length; i++) {
            sumTrace[i] = 0;
        }

        for (int i = 0; i < flimImage.getCurveNum(); i++) {
            if (dataset.getZValue(1, i) == -1) {
                for (int j = 0; j < flimImage.getCannelN(); j++) {
                    sumTrace[j] += flimImage.getDataPoint(i * flimImage.getCannelN() + j);
                }
            }
        }
        for (int i = 0; i < flimImage.getCannelN(); i++) {
            seria.add(flimImage.getCannelW() * i, sumTrace[i]);
        }
        trace.addSeries(seria);
        tracechart = ChartFactory.createXYLineChart(
                "Sum Trace",
                "Time (ns)",
                "Number of counts",
                trace,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        tracechart.getXYPlot().getDomainAxis().setUpperBound(flimImage.getTime());
        marker = new IntervalMarker(rangeSlider2.getLowValue(), rangeSlider2.getHighValue());
        marker.setPaint(new Color(222, 222, 255, 128));
        tracechart.getXYPlot().addDomainMarker(marker);
        GraphPanel chpanSumTrace = new GraphPanel(tracechart);
        rangeSlider2.setMaximum(flimImage.getCannelN() - 1);
        rangeSlider2.setHighValue(flimImage.getCannelN() - 1);
        rangeSlider2.setMinimum(0);
        rangeSlider2.setLowValue(0);
        
        jPSumTrace.removeAll();
        jPSumTrace.setLayout(new BorderLayout());
        jPSumTrace.add(chpanSumTrace);              
        jPanel2.revalidate();
        
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return SdtTopComponent.getDefault();
        }
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        selectPixel(event.getTrigger().getX(), event.getTrigger().getY());
        sumSelectedPixels();
    }
    
    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        if (event.getTrigger().getID() == MouseEvent.MOUSE_DRAGGED){
            selectPixel(event.getTrigger().getX(), event.getTrigger().getY());
        }
       
    }
    

    private void selectPixel(int mouseX, int mouseY){
                
        Point2D p = this.chpanIntenceImage.translateScreenToJava2D(new Point(mouseX, mouseY));
        XYPlot plot = (XYPlot) this.chart.getPlot();
        ChartRenderingInfo info = this.chpanIntenceImage.getChartRenderingInfo();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();

        ValueAxis domainAxis = plot.getDomainAxis();
        RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
        ValueAxis rangeAxis = plot.getRangeAxis();
        RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();

        int chartX = (int) round(domainAxis.java2DToValue(p.getX(), dataArea, domainAxisEdge));
        int chartY = (int) round(rangeAxis.java2DToValue(p.getY(), dataArea, rangeAxisEdge));
        
        if (oldSelPix != chartY * flimImage.getX() + chartX) {
            if ((chartX < flimImage.getX()) && (chartY < flimImage.getY()) && (chartX >= 0) && (chartY >= 0)) {
                if (dataset.getZValue(1, chartY * flimImage.getX() + chartX) == -1) {
                    dataset.SetValue(chartY * flimImage.getX() + chartX, flimImage.getIntMap()[chartY * flimImage.getX() + chartX]);
                    numSelPix--;
                    jLNumSelPix.setText(Integer.toString(numSelPix));
                } else {
                    dataset.SetValue(chartX, chartY, -1);
                    numSelPix++;
                    jLNumSelPix.setText(Integer.toString(numSelPix));
                }
                UpdateSelectedTrace(chartY * flimImage.getX() + chartX);
            }
            oldSelPix = chartY * flimImage.getX() + chartX;
        }
    }
    
    private XYZDataset MakeXYZDataset() {
        dataset = new IntensImageDataset(flimImage.getX(), flimImage.getY(), flimImage.getIntMap().clone());
        return dataset;
    }

    private void updateXYZDataset(){
        
        if (jTButBin.isSelected()) {
            flimImage.setBinned(1);
        } else {
            flimImage.setBinned(0);
        }

        if (jTButAmpl.isSelected()) {
            flimImage.buildIntMap(0);
        } else {
            flimImage.buildIntMap(1);
        }
        
        int[] tempSelectedPixels = new int[flimImage.getCurveNum()];
        for (int i = 0; i < flimImage.getCurveNum(); i++) {
            if (dataset.getZValue(1, i) == -1) {
                tempSelectedPixels[i] = -1;
            }
        }
        dataset.SetIntenceImage(flimImage.getIntMap().clone());
        for (int i = 0; i < flimImage.getCurveNum(); i++) {
            if (tempSelectedPixels[i] == -1) {
                dataset.SetValue(i, -1);
            }
        }
        jTFMaxIntence.setText(String.valueOf(flimImage.getMaxIntens()));
        jTFMaxIntence.setText("0");
        updateIntenceImageChart(0, flimImage.getMaxIntens());
    }
            
    private XYSeriesCollection PlotFirstTrace(int index) {
        tracesCollection = new XYSeriesCollection();
        XYSeries seria = new XYSeries("Trace");
        for (int j = 0; j < flimImage.getCannelN(); j++) {
            seria.add(j * flimImage.getCannelW(), flimImage.getDataPoint(index * flimImage.getCannelN() + j));
        }
        tracesCollection.addSeries(seria);
        return tracesCollection;
    }

    private void UpdateSelectedTrace(int item) {
        tracesCollection.getSeries(0).clear();
        for (int j = 0; j < flimImage.getCannelN(); j++) {
            tracesCollection.getSeries(0).add(j * flimImage.getCannelW(), flimImage.getDataPoint(item * flimImage.getCannelN() + j));
        }
    }

    private void updateIntenceImageChart(double low, double higth) {
        PaintScale scale = new GrayPaintScalePlus(low, higth, -1);
        XYBlockRenderer rend = (XYBlockRenderer) chart.getXYPlot().getRenderer();
        rend.setPaintScale(scale);
        updateColorBar(scale);
    }

    private void updateColorBar(PaintScale scale) {
        NumberAxis scaleAxis = new NumberAxis();
        scaleAxis.setAxisLinePaint(Color.black);
        scaleAxis.setTickMarkPaint(Color.black);
        scaleAxis.setUpperBound(flimImage.getMaxIntens());
        scaleAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 9));
        PaintScaleLegend legend = new PaintScaleLegend(scale, scaleAxis);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        legend.setMargin(new RectangleInsets(5, 5, 5, 5));
        legend.setStripWidth(15);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(chart.getBackgroundPaint());
        chart.clearSubtitles();
        chart.addSubtitle(legend);
    }

    private void MakeTracesChart(XYSeriesCollection dat, boolean compleateSet) {
        JFreeChart tracechart;
        tracechart = ChartFactory.createXYLineChart(
                "Selected trace",
                "Time (ns)",
                "Number of counts",
                dat,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        tracechart.getXYPlot().getDomainAxis().setUpperBound(flimImage.getTime());
        tracechart.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
//        tracechart.getXYPlot().setDomainZeroBaselineVisible(true);
        if (compleateSet) {
            for (int i = 0; i < flimImage.getCurveNum(); i++) {
                tracechart.getXYPlot().getRenderer().setSeriesVisible(i, false);
            }
        }
        chpanSelectedTrace = new GraphPanel(tracechart);
        chpanSelectedTrace.setSize(jPSelectedTrace.getMaximumSize());
        jPSelectedTrace.removeAll();
        jPSelectedTrace.add(chpanSelectedTrace);
        jPSelectedTrace.repaint();
    }

    private void MakeIntImageChart(XYZDataset dataset) {
        NumberAxis xAxis = new NumberAxis("X");
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        xAxis.setVisible(false);
        NumberAxis yAxis = new NumberAxis("Y");
        yAxis.setAutoRangeIncludesZero(false);
        yAxis.setInverted(true);
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setVisible(false);

        XYBlockRenderer renderer = new XYBlockRenderer();
        PaintScale scale = new GrayPaintScalePlus(0, flimImage.getMaxIntens(), -1);
        renderer.setPaintScale(scale);
        FastXYPlot plot = new FastXYPlot(dataset, xAxis, yAxis, renderer);
        chart = new JFreeChart(plot);
        chart.setAntiAlias(false);
        chart.removeLegend();
        updateColorBar(scale);
    }

    public double[] intToDoubleArray(int[] numbers) {
        double[] newNumbers = new double[numbers.length];
        for (int index = 0; index < numbers.length; index++) {
            newNumbers[index] = (double) numbers[index];
        }
        return newNumbers;
    }
}
