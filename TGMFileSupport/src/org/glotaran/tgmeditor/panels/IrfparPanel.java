package org.glotaran.tgmeditor.panels;

import java.awt.Color;
import java.io.File;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.tgm.IrfparPanelModel;
import org.glotaran.jfreechartcustom.GraphPanel;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;


/*
 * KinIrfparPanel.java
 *
 * Created on August 3, 2008, 11:01 AM
 */
public class IrfparPanel extends SectionInnerPanel {

    private TgmDataObject dObj;
    private IrfparPanelModel irfparPanelModel;
    private IrfparTableModel model;
    private RowHeader rowHeader;
    private Object[] defRow;
    private Object[] newRow;
    private String[] rowNames;
    private JFileChooser fc;
    private int length;
    private int from, till;
    private double maxInt;
    private double[] refArray;
    private ChartPanel chpan;
    private XYSeriesCollection refSerColl;
    private JFreeChart chart;
    private IntervalMarker marker;

    public IrfparPanel(SectionView view, TgmDataObject dObj, IrfparPanelModel irfparPanelModel) {
        super(view);
        this.dObj = dObj;
        this.irfparPanelModel = irfparPanelModel;

        fc = new JFileChooser();
        chpan = null;
        chart = null;
        marker = null;
        maxInt = 0;
        refSerColl = new XYSeriesCollection();

        initComponents();
//=======================IRF
        jSNumOfIrfParameters.setModel(new SpinnerNumberModel(irfparPanelModel.getIrf().size(), 0, 4, 2));
        rowHeader = new RowHeader(20, 50);
        rowNames = new String[]{"Position", "Width", "Width2", "Relation"};
        defRow = new Object[]{new Double(0), new Boolean(false)};
        model = new IrfparTableModel(new Object[]{"Irf parameters", "Fixed"}, 0);
        for (int i = 0; i < irfparPanelModel.getIrf().size(); i++) {
            if (irfparPanelModel.getIrf().get(i) != null) {
                newRow = new Object[]{
                            irfparPanelModel.getIrf().get(i),
                            irfparPanelModel.getFixed().get(i)
                        };
                model.addRow(newRow);
                rowHeader.addRow(rowNames[i]);
            } else {
                model.addRow(defRow);
                rowHeader.addRow(rowNames[i]);
            }
        }
        jTIrfparTable.setModel(model);
        JScrollPane jscpane = (JScrollPane) jTIrfparTable.getParent().getParent();
        jscpane.setRowHeaderView(rowHeader);
        jscpane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeader.getTableHeader());
        if (irfparPanelModel.getDispmufun() != null) {
            if (irfparPanelModel.getDispmufun().compareTo("poly") == 0) {
                jRDispmufun_poly.setSelected(true);
            }
            if (irfparPanelModel.getDispmufun().compareTo("discrete") == 0) {
                jRDispmufun_discrete.setSelected(true);
            }
        } else {
            jRDispmufun_no.setSelected(true);
        }
        jParmuTextfield.setText(irfparPanelModel.getParmu());

        if (irfparPanelModel.getDisptaufun() != null) {
            if (irfparPanelModel.getDisptaufun().compareTo("poly") == 0) {
                jRDisptaufun_poly.setSelected(true);
            }
            if (irfparPanelModel.getDisptaufun().compareTo("discrete") == 0) {
                jRDisptaufun_discrete.setSelected(true);
            }
        } else {
            jRDisptaufun_no.setSelected(true);
        }
        jPartauTextfield.setText(irfparPanelModel.getPartau());
        jTPolyDispersion.setText(String.valueOf(irfparPanelModel.getLamda()));
        jTFLaserPeriod.setText(String.valueOf(irfparPanelModel.getBacksweepPeriod()));
        jCBStreak.setSelected(irfparPanelModel.isBacksweepEnabled());
        jTFLaserPeriod.setEnabled(jCBStreak.isSelected());
        jLabel2.setEnabled(jCBStreak.isSelected());

        if (irfparPanelModel.isParmufixed() != null) {
            jCBParmuFixed.setSelected(irfparPanelModel.isParmufixed());
            jCBParmuFixedShift.setSelected(irfparPanelModel.isParmufixed());

        } else {
            irfparPanelModel.setParmufixed(Boolean.FALSE);
            jCBParmuFixed.setSelected(false);
            jCBParmuFixedShift.setSelected(false);
        }

        if (irfparPanelModel.isPartaufixed() != null) {
            jCBPartauFixed.setSelected(irfparPanelModel.isPartaufixed());
        } else {
            irfparPanelModel.setPartaufixed(Boolean.FALSE);
            jCBPartauFixed.setSelected(false);
        }

//=====================measuredIRF
        if (irfparPanelModel.isMirf() != null) {
            jCBMeasuredIRF.setSelected(irfparPanelModel.isMirf());
        } else {
            jCBMeasuredIRF.setSelected(false);
            irfparPanelModel.setMirf(Boolean.FALSE);
        }
        if (irfparPanelModel.getConvalg() != null) {
            switch (irfparPanelModel.getConvalg()) {
                case 1:
                    jRBScatterConv.setSelected(true);
                    break;
                case 2:
                    jRBScatterConv.setSelected(true);
                    break;
                case 3:
                    jRBReferConv.setSelected(true);
                    break;
                default: {
                    jRBScatterConv.setSelected(true);
                    irfparPanelModel.setConvalg(2);
                    break;
                }
            } //end switch
        } else if (irfparPanelModel.isMirf()) {
            irfparPanelModel.setConvalg(2);
        }
        jTRefLifetime.setText(String.valueOf(irfparPanelModel.getReftau()));
        updateEnabled(irfparPanelModel.isMirf());
        jTFIrfShiftParameter.setText(irfparPanelModel.getParmu());
        if (irfparPanelModel.getMeasuredIrf() != null) {
            if (!irfparPanelModel.getMeasuredIrf().isEmpty()) {
                String[] doubles = irfparPanelModel.getMeasuredIrf().split(",");
                XYSeries refSeria = new XYSeries("Reference");
                refArray = new double[doubles.length];
                for (int i = 0; i < doubles.length; i++) {
                    refArray[i] = Double.parseDouble(doubles[i]);
                    refSeria.add(i, refArray[i]);

                }
                refSerColl.addSeries(refSeria);
                MakeChart(refSerColl);
            }
        }



// Add listerners
        jTIrfparTable.getModel().addTableModelListener(model);
        //Radiobuttons
        addModifier(jRDispmufun_no);
        addModifier(jRDispmufun_poly);
        addModifier(jRDispmufun_discrete);
        addModifier(jRDisptaufun_no);
        addModifier(jRDisptaufun_poly);
        addModifier(jRDisptaufun_discrete);
        addModifier(jCBStreak);
        //checkboxes
        addModifier(jCBParmuFixed);
        addModifier(jCBPartauFixed);
//========meairf=======
        addModifier(jRBScatterConv);
        addModifier(jRBReferConv);
        addModifier(jCBMeasuredIRF);

        // Textfields:
        addModifier(jParmuTextfield);
        addModifier(jPartauTextfield);
        addModifier(jTPolyDispersion);
        addModifier(jTFLaserPeriod);
//========meairf=======
        addModifier(jTRefLifetime);
        addModifier(jTFIrfShiftParameter);
        addModifier(jCBParmuFixedShift);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSNumOfIrfParameters = new javax.swing.JSpinner();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTIrfparTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jParmuTextfield = new javax.swing.JTextField();
        jRDispmufun_no = new javax.swing.JRadioButton();
        jRDispmufun_poly = new javax.swing.JRadioButton();
        jRDispmufun_discrete = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jCBParmuFixed = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jRDisptaufun_no = new javax.swing.JRadioButton();
        jRDisptaufun_poly = new javax.swing.JRadioButton();
        jRDisptaufun_discrete = new javax.swing.JRadioButton();
        jPartauTextfield = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jCBPartauFixed = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jTPolyDispersion = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jCBStreak = new javax.swing.JCheckBox();
        jTFLaserPeriod = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jCBMeasuredIRF = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        tFRefFilename = new javax.swing.JTextField();
        Bloadref = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jCBEstimateBG = new javax.swing.JCheckBox();
        jSFrom = new javax.swing.JSpinner();
        jSTill = new javax.swing.JSpinner();
        jTFBGvalue = new javax.swing.JTextField();
        jBSubtrBG = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jBCalculateBG = new javax.swing.JButton();
        jCBNegToZer = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jRBScatterConv = new javax.swing.JRadioButton();
        jRBReferConv = new javax.swing.JRadioButton();
        jTRefLifetime = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jTFIrfShiftParameter = new javax.swing.JTextField();
        jCBParmuFixedShift = new javax.swing.JCheckBox();

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel6.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel6.addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentHidden(java.awt.event.ComponentEvent evt) {
                jPanel6ComponentHidden(evt);
            }

            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel6ComponentShown(evt);
            }
        });

        jLabel4.setText("Number of IRF parameters");

        jSNumOfIrfParameters.addChangeListener(new javax.swing.event.ChangeListener() {

            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSNumOfIrfParametersStateChanged(evt);
            }
        });

        jTIrfparTable.setRowHeight(20);
        jScrollPane3.setViewportView(jTIrfparTable);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters to model IRF dispersion"));

        jLabel3.setText("model dispersion of IRF location?");

        jParmuTextfield.setColumns(10);
        jParmuTextfield.setToolTipText("parmu: *** Object of class \"list\" of starting values for the dispersion model for the IRF location\n[Enter as R string]");

        buttonGroup1.add(jRDispmufun_no);
        jRDispmufun_no.setSelected(true);
        jRDispmufun_no.setText("no");
        jRDispmufun_no.setToolTipText("disptaufun: *** Object of class \"character\" describing the functional form of the disper-\n    sion of the IRF width parameter; if equal to \"discrete\" then the IRF width is parame-\n    terized per element of x2 and partau should have the same length as x2. defaults to a\n    polynomial description\n");

        buttonGroup1.add(jRDispmufun_poly);
        jRDispmufun_poly.setText("\"poly\" (as polynomial function)");
        jRDispmufun_poly.setToolTipText("disptaufun: *** Object of class \"character\" describing the functional form of the disper-\n    sion of the IRF width parameter; if equal to \"discrete\" then the IRF width is parame-\n    terized per element of x2 and partau should have the same length as x2. defaults to a\n    polynomial description\n");

        buttonGroup1.add(jRDispmufun_discrete);
        jRDispmufun_discrete.setText("\"discrete\" (with one parameter per-wavelength)");
        jRDispmufun_discrete.setToolTipText("disptaufun: *** Object of class \"character\" describing the functional form of the disper-\n    sion of the IRF width parameter; if equal to \"discrete\" then the IRF width is parame-\n    terized per element of x2 and partau should have the same length as x2. defaults to a\n    polynomial description\n");

        jLabel5.setText("parameters for dispersion of IRF location");

        jLabel8.setText("(comma separated numbers)");

        jCBParmuFixed.setText("Fix");
        jCBParmuFixed.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBParmuFixedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jRDispmufun_poly).addComponent(jRDispmufun_discrete).addComponent(jRDispmufun_no).addComponent(jLabel3).addComponent(jLabel8).addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(jParmuTextfield, javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE).addComponent(jCBParmuFixed))).addContainerGap()));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(jLabel3).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jRDispmufun_no).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jRDispmufun_poly).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jRDispmufun_discrete).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel5).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jLabel8).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jParmuTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCBParmuFixed)).addContainerGap()));

        jLabel6.setText("model dispersion of IRF width?");

        buttonGroup2.add(jRDisptaufun_no);
        jRDisptaufun_no.setSelected(true);
        jRDisptaufun_no.setText("no");

        buttonGroup2.add(jRDisptaufun_poly);
        jRDisptaufun_poly.setText("\"poly\" (as polynomial function)");

        buttonGroup2.add(jRDisptaufun_discrete);
        jRDisptaufun_discrete.setText("\"discrete\" (with one parameter per-wavelength)");

        jPartauTextfield.setColumns(10);
        jPartauTextfield.setToolTipText("partau: *** Object of class \"vector\" of starting values for the dispersion model for the IRF FWHM\n[Enter as R string]\n");

        jLabel7.setText("parameters for dispersion of IRF width");

        jLabel9.setText("(comma separated numbers)");

        jCBPartauFixed.setText("Fix");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(jRDisptaufun_poly).addComponent(jRDisptaufun_no).addComponent(jLabel6).addComponent(jRDisptaufun_discrete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPartauTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel9)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jCBPartauFixed))).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(jLabel6).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jRDisptaufun_no).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jRDisptaufun_poly).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jRDisptaufun_discrete).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel7).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jLabel9).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPartauTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()).addGroup(jPanel1Layout.createSequentialGroup().addGap(18, 18, 18).addComponent(jCBPartauFixed).addContainerGap()))));

        jLabel1.setText("Center wavelength for polynomial dispersion");

        jTPolyDispersion.setPreferredSize(new java.awt.Dimension(4, 18));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTPolyDispersion, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1).addComponent(jTPolyDispersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(41, 41, 41)));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters for a streak images analysis"));

        jCBStreak.setText("Include backsweep into model");
        jCBStreak.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBStreakActionPerformed(evt);
            }
        });

        jTFLaserPeriod.setEnabled(false);

        jLabel2.setText("Period of the laser pulses:");
        jLabel2.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jCBStreak).addGroup(jPanel4Layout.createSequentialGroup().addGap(21, 21, 21).addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTFLaserPeriod, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))).addContainerGap()));
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addComponent(jCBStreak).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2).addComponent(jTFLaserPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))));

        jCBMeasuredIRF.setText("Measured IRF");
        jCBMeasuredIRF.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMeasuredIRFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap().addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCBMeasuredIRF).addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(jPanel6Layout.createSequentialGroup().addComponent(jLabel4).addGap(25, 25, 25).addComponent(jSNumOfIrfParameters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap().addComponent(jCBMeasuredIRF).addGap(24, 24, 24).addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jSNumOfIrfParameters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel4)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(199, 199, 199)));

        jTabbedPane1.addTab("IRF Settings", jPanel6);

        jPanel7.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel7.addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentHidden(java.awt.event.ComponentEvent evt) {
                jPanel7ComponentHidden(evt);
            }

            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel7ComponentShown(evt);
            }
        });

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel8.setPreferredSize(new java.awt.Dimension(679, 413));

        jLabel11.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.jLabel2.text")); // NOI18N

        tFRefFilename.setEditable(false);
        tFRefFilename.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.tFRefFilename.text")); // NOI18N

        Bloadref.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.Bloadref.text")); // NOI18N
        Bloadref.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BloadrefActionPerformed(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanel9ComponentResized(evt);
            }
        });
        jPanel9.setLayout(new java.awt.BorderLayout());

        jCBEstimateBG.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.jCBEstimateBG.text")); // NOI18N
        jCBEstimateBG.setEnabled(false);
        jCBEstimateBG.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBEstimateBGActionPerformed(evt);
            }
        });

        jSFrom.setEnabled(false);
        jSFrom.addChangeListener(new javax.swing.event.ChangeListener() {

            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSFromStateChanged(evt);
            }
        });

        jSTill.setEnabled(false);
        jSTill.addChangeListener(new javax.swing.event.ChangeListener() {

            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSTillStateChanged(evt);
            }
        });

        jTFBGvalue.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.jTFBGvalue.text")); // NOI18N
        jTFBGvalue.setEnabled(false);

        jBSubtrBG.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.jBSubtrBG.text")); // NOI18N
        jBSubtrBG.setEnabled(false);
        jBSubtrBG.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSubtrBGActionPerformed(evt);
            }
        });

        jLabel12.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.jLabel1.text")); // NOI18N
        jLabel12.setEnabled(false);

        jLabel13.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.jLabel3.text")); // NOI18N
        jLabel13.setEnabled(false);

        jLabel14.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.jLabel4.text")); // NOI18N
        jLabel14.setEnabled(false);

        jBCalculateBG.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.jBCalculateBG.text")); // NOI18N
        jBCalculateBG.setEnabled(false);
        jBCalculateBG.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCalculateBGActionPerformed(evt);
            }
        });

        jCBNegToZer.setSelected(true);
        jCBNegToZer.setText(org.openide.util.NbBundle.getMessage(IrfparPanel.class, "MeasuredIrfTopComponent.jCBNegToZer.text")); // NOI18N
        jCBNegToZer.setEnabled(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup().addContainerGap().addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup().addComponent(jCBEstimateBG, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel12).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jSFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel13).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jSTill, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jBCalculateBG).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel14).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTFBGvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup().addComponent(jLabel11).addGap(10, 10, 10).addComponent(tFRefFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup().addComponent(Bloadref, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 272, Short.MAX_VALUE).addComponent(jCBNegToZer).addGap(18, 18, 18).addComponent(jBSubtrBG))).addContainerGap()));
        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel8Layout.createSequentialGroup().addContainerGap().addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel11).addComponent(tFRefFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(Bloadref).addComponent(jBSubtrBG).addComponent(jCBNegToZer)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel14).addComponent(jTFBGvalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jBCalculateBG).addComponent(jLabel12).addComponent(jSFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel13).addComponent(jSTill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCBEstimateBG)).addContainerGap()));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel5.setEnabled(false);

        buttonGroup3.add(jRBScatterConv);
        jRBScatterConv.setText("Scatter Convolution");
        jRBScatterConv.setEnabled(false);

        buttonGroup3.add(jRBReferConv);
        jRBReferConv.setText("Reference convolution");
        jRBReferConv.setEnabled(false);

        jTRefLifetime.setEnabled(false);

        jLabel10.setText("Reference Lifetime (ns)");
        jLabel10.setEnabled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jRBReferConv).addGroup(jPanel5Layout.createSequentialGroup().addGap(21, 21, 21).addComponent(jLabel10).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTRefLifetime, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(jRBScatterConv)).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addComponent(jRBReferConv).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel10).addComponent(jTRefLifetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jRBScatterConv).addContainerGap(10, Short.MAX_VALUE)));

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setText("Measured IRF Shift Parameter");

        jCBParmuFixedShift.setText("Fix");
        jCBParmuFixedShift.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBParmuFixedShiftActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
                jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel10Layout.createSequentialGroup().addContainerGap().addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel15).addGroup(jPanel10Layout.createSequentialGroup().addComponent(jTFIrfShiftParameter, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jCBParmuFixedShift))).addContainerGap(110, Short.MAX_VALUE)));
        jPanel10Layout.setVerticalGroup(
                jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel10Layout.createSequentialGroup().addContainerGap().addComponent(jLabel15).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jTFIrfShiftParameter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCBParmuFixedShift)).addContainerGap(37, Short.MAX_VALUE)));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addContainerGap().addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()).addGroup(jPanel7Layout.createSequentialGroup().addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(1031, 1031, 1031)))));
        jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup().addContainerGap().addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGap(452, 452, 452)));

        jTabbedPane1.addTab("Measured IRF", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 832, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 558, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    private void jSNumOfIrfParametersStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSNumOfIrfParametersStateChanged
// TODO add your handling code here:]
        if ((Integer) jSNumOfIrfParameters.getValue() > model.getRowCount()) {
            model.addRow(defRow);
            model.addRow(defRow);
            rowHeader.addRow(rowNames[(Integer) jSNumOfIrfParameters.getValue() - 2]);
            rowHeader.addRow(rowNames[(Integer) jSNumOfIrfParameters.getValue() - 1]);
        } else {
            model.removeRow(model.getRowCount() - 1);
            model.removeRow(model.getRowCount() - 1);
            rowHeader.removeRow(rowHeader.getRowCount() - 1);
            rowHeader.removeRow(rowHeader.getRowCount() - 1);

        }
//    jTIrfparTable.setModel(model);
        setValue(jTIrfparTable, this);
    }//GEN-LAST:event_jSNumOfIrfParametersStateChanged

    private void jCBStreakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBStreakActionPerformed
        jTFLaserPeriod.setEnabled(jCBStreak.isSelected());
        jLabel2.setEnabled(jCBStreak.isSelected());

    }//GEN-LAST:event_jCBStreakActionPerformed

    private void jCBMeasuredIRFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMeasuredIRFActionPerformed
        updateEnabled(jCBMeasuredIRF.isSelected());

    }//GEN-LAST:event_jCBMeasuredIRFActionPerformed

    private void BloadrefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BloadrefActionPerformed
        int returnVal = fc.showOpenDialog(this);
        Vector refVector = new Vector();
        File file = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            tFRefFilename.setText(fc.getName(file));

            try {
                Scanner sc = new Scanner(file);
                while (sc.hasNext()) {
                    refVector.addElement(sc.nextFloat());
                }
            } catch (Exception e) {
                CoreErrorMessages.fileLoadException("IRF");
            }
            int i = 0;
            float num;
            XYSeries refSeria = new XYSeries("Reference");
            length = refVector.size();
            refArray = new double[length];
            for (Enumeration e = refVector.elements(); e.hasMoreElements();) {
                Float temp = (Float) e.nextElement();
                num = (float) temp;
                refSeria.add(i, num);
                if (num > maxInt) {
                    maxInt = num;
                }
                refArray[i] = num;
                i++;
            }
            if (refSerColl.getSeries().size() > 0) {
                refSerColl.removeAllSeries();
            }
            refSerColl.addSeries(refSeria);
            MakeChart(refSerColl);
            from = 0;
            till = length;
            marker = new IntervalMarker(from, till);
            marker.setPaint(new Color(222, 222, 255, 128));

            jBSubtrBG.setEnabled(true);
            jLabel4.setEnabled(true);
            jCBEstimateBG.setEnabled(true);
            jTFBGvalue.setEnabled(true);
            jCBNegToZer.setEnabled(true);

            jSFrom.setModel(new SpinnerNumberModel(from, 0, length, 1));
            jSTill.setModel(new SpinnerNumberModel(till, 0, length, 1));
            //        System.out.println(refVector);
            setValue(jPanel9, refArray);
        }
    }//GEN-LAST:event_BloadrefActionPerformed

    private void jPanel9ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel9ComponentResized
        if (chpan != null) {
            chpan.setSize(jPanel2.getSize());
        }
    }//GEN-LAST:event_jPanel9ComponentResized

    private void jCBEstimateBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBEstimateBGActionPerformed
        jTFBGvalue.setEditable(!jTFBGvalue.isEditable());
        jLabel1.setEnabled(!jLabel1.isEnabled());
        jLabel3.setEnabled(!jLabel3.isEnabled());
        jSFrom.setEnabled(!jSFrom.isEnabled());
        jSTill.setEnabled(!jSTill.isEnabled());
        jBCalculateBG.setEnabled(!jBCalculateBG.isEnabled());
        if (jCBEstimateBG.isSelected()) {
            chart.getXYPlot().addDomainMarker(marker);
        } else {
            chart.getXYPlot().clearDomainMarkers(0);
        }
    }//GEN-LAST:event_jCBEstimateBGActionPerformed

    private void jSFromStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSFromStateChanged
        Integer fr = (Integer) jSFrom.getValue();
        Integer tl = (Integer) jSTill.getValue();
        from = (int) fr;
        till = (int) tl;
        if (from >= till) {
            jSTill.setValue(jSFrom.getValue());
            till = from;
        }
        marker.setStartValue(from);
        marker.setEndValue(till);
    }//GEN-LAST:event_jSFromStateChanged

    private void jSTillStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSTillStateChanged
        Integer fr = (Integer) jSFrom.getValue();
        Integer tl = (Integer) jSTill.getValue();
        from = (int) fr;
        till = (int) tl;

        if (from >= till) {
            jSFrom.setValue(jSTill.getValue());
            from = till;
        }
        marker.setStartValue(from);
        marker.setEndValue(till);
    }//GEN-LAST:event_jSTillStateChanged

    private void jBSubtrBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSubtrBGActionPerformed
        float val = 0;
        try {
            val = Float.parseFloat(jTFBGvalue.getText());
        } catch (NumberFormatException ex) {
            CoreErrorMessages.numberFormatException();
        }
        if (refSerColl.getSeries().size() > 0) {
            refSerColl.getSeries().clear();
            for (int i = 0; i < length; i++) {
                refArray[i] -= val;
                if (jCBNegToZer.isSelected() && (refArray[i] < 0)) {
                    refArray[i] = 0;
                }
                refSerColl.getSeries(0).add(i, refArray[i]);
            }
            setValue(jPanel9, refArray);
        }
    }//GEN-LAST:event_jBSubtrBGActionPerformed

    private void jBCalculateBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCalculateBGActionPerformed
        float sum = 0;
        for (int i = from; i < till; i++) {
            sum += refArray[i];
        }
        Float val = sum / (till - from);
        jTFBGvalue.setText(val.toString());
    }//GEN-LAST:event_jBCalculateBGActionPerformed

    private void jPanel6ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel6ComponentShown
        endUIChange();
    }//GEN-LAST:event_jPanel6ComponentShown

    private void jPanel7ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel7ComponentShown
        endUIChange();
    }//GEN-LAST:event_jPanel7ComponentShown

    private void jPanel6ComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel6ComponentHidden
        endUIChange();
    }//GEN-LAST:event_jPanel6ComponentHidden

    private void jPanel7ComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel7ComponentHidden
        endUIChange();
    }//GEN-LAST:event_jPanel7ComponentHidden

    private void jCBParmuFixedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBParmuFixedActionPerformed
    }//GEN-LAST:event_jCBParmuFixedActionPerformed

    private void jCBParmuFixedShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBParmuFixedShiftActionPerformed
    }//GEN-LAST:event_jCBParmuFixedShiftActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Bloadref;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton jBCalculateBG;
    private javax.swing.JButton jBSubtrBG;
    private javax.swing.JCheckBox jCBEstimateBG;
    private javax.swing.JCheckBox jCBMeasuredIRF;
    private javax.swing.JCheckBox jCBNegToZer;
    private javax.swing.JCheckBox jCBParmuFixed;
    private javax.swing.JCheckBox jCBParmuFixedShift;
    private javax.swing.JCheckBox jCBPartauFixed;
    private javax.swing.JCheckBox jCBStreak;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTextField jParmuTextfield;
    private javax.swing.JTextField jPartauTextfield;
    private javax.swing.JRadioButton jRBReferConv;
    private javax.swing.JRadioButton jRBScatterConv;
    private javax.swing.JRadioButton jRDispmufun_discrete;
    private javax.swing.JRadioButton jRDispmufun_no;
    private javax.swing.JRadioButton jRDispmufun_poly;
    private javax.swing.JRadioButton jRDisptaufun_discrete;
    private javax.swing.JRadioButton jRDisptaufun_no;
    private javax.swing.JRadioButton jRDisptaufun_poly;
    private javax.swing.JSpinner jSFrom;
    private javax.swing.JSpinner jSNumOfIrfParameters;
    private javax.swing.JSpinner jSTill;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTFBGvalue;
    private javax.swing.JTextField jTFIrfShiftParameter;
    private javax.swing.JTextField jTFLaserPeriod;
    private javax.swing.JTable jTIrfparTable;
    private javax.swing.JTextField jTPolyDispersion;
    private javax.swing.JTextField jTRefLifetime;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField tFRefFilename;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setValue(JComponent source, Object value) {
        if (source == jTIrfparTable) {
            irfparPanelModel.getIrf().clear();
            irfparPanelModel.getFixed().clear();

            for (int i = 0; i < model.getRowCount(); i++) {
                irfparPanelModel.getIrf().add((Double) model.getValueAt(i, 0));
                irfparPanelModel.getFixed().add((Boolean) model.getValueAt(i, 1));
            }
        }

        if (source == jRDispmufun_no) {
            irfparPanelModel.setDispmufun(null);
        }

        if (source == jRDisptaufun_no) {
            irfparPanelModel.setDisptaufun(null);
        }

        if (source == jRDispmufun_poly) {
            irfparPanelModel.setDispmufun("poly");
        }
        if (source == jRDisptaufun_poly) {
            irfparPanelModel.setDisptaufun("poly");
        }
        if (source == jRDispmufun_discrete) {
            irfparPanelModel.setDispmufun("discrete");
        }
        if (source == jRDisptaufun_discrete) {
            irfparPanelModel.setDisptaufun("discrete");
        }
        if (source == jParmuTextfield) {
            irfparPanelModel.setParmu((String) value);
        }
        if (source == jPartauTextfield) {
            irfparPanelModel.setPartau((String) value);
        }
        if (source == jCBParmuFixed) {
            irfparPanelModel.setParmufixed((Boolean) value);
        }
        if (source == jCBParmuFixedShift) {
            irfparPanelModel.setParmufixed((Boolean) value);
        }
        if (source == jCBPartauFixed) {
            irfparPanelModel.setPartaufixed((Boolean) value);
        }

        if (source == jTPolyDispersion) {
            String test = (String) value;
            if (!test.isEmpty()) {
                irfparPanelModel.setLamda(Double.valueOf((String) value));
            } else {
                irfparPanelModel.setLamda(null);
                jTPolyDispersion.setText("");
            }
        }
        if (source == jCBStreak) {
            irfparPanelModel.setBacksweepEnabled(jCBStreak.isSelected());
        }

        if (source == jTFLaserPeriod) {
            String newValue = (String) value;
            if (!newValue.isEmpty()) {
                irfparPanelModel.setBacksweepPeriod(Double.valueOf((String) value));
            } else {
                irfparPanelModel.setBacksweepPeriod(null);
            }
        }

        if (source == jCBMeasuredIRF) {
            irfparPanelModel.setMirf(jCBMeasuredIRF.isSelected());
        }
        if (source == jRBScatterConv) {
            if ((Boolean) value) {
                irfparPanelModel.setConvalg(2);
            }
        }
        if (source == jRBReferConv) {
            if ((Boolean) value) {
                irfparPanelModel.setConvalg(3);
            }
        }
        if (source == jTRefLifetime) {
            String newValue = (String) value;
            if (!newValue.isEmpty()) {
                irfparPanelModel.setReftau(Double.valueOf((String) value));
            } else {
                irfparPanelModel.setReftau(null);
            }
        }
        if (source == jPanel9) { //measured IRF Changed
            StringBuilder result = new StringBuilder(String.valueOf(refArray[0]));
            for (int i = 1; i < refArray.length; i++) {
                result.append(",").append(refArray[i]);
            }
            irfparPanelModel.setMeasuredIrf(result.toString());
        }
        if (source == jTFIrfShiftParameter) {
            irfparPanelModel.setParmu((String) value);
        }
    }

    @Override
    protected void endUIChange() {// signalUIChange() is deprecated{
         dObj.setModified(true);
    }

    @Override
    public void linkButtonPressed(Object arg0, String arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JComponent getErrorComponent(String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void updateEnabled(boolean selected) {
        jPanel5.setEnabled(!selected);
        for (int i = 0; i < jPanel5.getComponents().length; i++) {
            jPanel5.getComponents()[i].setEnabled(selected);
        }

        for (int i = 0; i < jPanel1.getComponents().length; i++) {
            jPanel1.getComponents()[i].setEnabled(!selected);
        }

        for (int i = 0; i < jPanel2.getComponents().length; i++) {
            jPanel2.getComponents()[i].setEnabled(!selected);
        }

        jLabel1.setEnabled(!selected);
        jTPolyDispersion.setEnabled(!selected);
        jTIrfparTable.setEnabled(!selected);
        jScrollPane3.setEnabled(!selected);
        jLabel4.setEnabled(!selected);
        jSNumOfIrfParameters.setEnabled(!selected);
        jCBStreak.setEnabled(!selected);
        jTFLaserPeriod.setEnabled((!selected) & (jCBStreak.isSelected()));

    }

    class IrfparTableModel extends DefaultTableModel implements TableModelListener {

        private Class[] types = new Class[]{Double.class, Boolean.class};

        private IrfparTableModel() {
            super();
        }

        private IrfparTableModel(Object[] ColNames, int i) {
            super(ColNames, i);
        }

        @Override
        public Class getColumnClass(int c) {
            return types[c];
        }

        @Override
        public void tableChanged(TableModelEvent event) {
            setValue(jTIrfparTable, this);
            jTIrfparTable.repaint();
        }
    }

    private void MakeChart(XYDataset dat) {
        chart = ChartFactory.createXYLineChart(
                "Measured IRF",
                "Channel Number",
                "Number of counts",
                dat,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
//        chart.getXYPlot().getDomainAxis().setUpperBound(length);
        chart.getXYPlot().getDomainAxis().setUpperMargin(0);
        chart.getXYPlot().getDomainAxis().setLowerMargin(0);
        chart.getXYPlot().setDomainZeroBaselineVisible(true);
        chpan = new GraphPanel(chart);
        jPanel9.removeAll();
        jPanel9.add(chpan);
        jPanel9.validate();
    }
}
