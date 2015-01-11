package org.glotaran.tgmeditor.panels;

import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.glotaran.core.models.tgm.Double2Matrix;
import org.glotaran.core.models.tgm.IntMatrix;
import org.glotaran.core.models.tgm.KMatrixPanelModel;
import org.glotaran.core.models.tgm.KinPar;
import org.glotaran.core.models.tgm.KinparPanelModel;
import org.glotaran.tgmfilesupport.TgmDataObject;

/**
 *
 * @author  Sergey
 */
public class KMatrixPanelForm extends JPanel implements TableModelListener {

    private TgmDataObject dObj;
    private KMatrixPanelModel kMatrixPanelModel;
    private NumberTableModel model1, model2, modelCLP0,modelClpEq;
    //private SpectralConstraintsTableModel modelClpEq;
    private ParameterTableModel kinparModel, kinscalModel;
    private DefaultTableModel jVec;//, relationsModel;
    private RowHeader rowHeader1, rowHeader2, rowHeaderJVec, rowHeaderCLP0, rowHeaderClpEq, rowHeaderKinpar, rowHeaderKinscal; //rowHeaderRelations
    private int matrixSize = 0;
    
        private static class smallNumberRenderer extends DefaultTableCellRenderer {
    private DecimalFormat dfBig = new DecimalFormat("0.#####");
    private DecimalFormat dfSmall = new DecimalFormat("0.0#E0#");
 
    public smallNumberRenderer() {
      super();      
    }
 
    public void setValue(Object value) {
      if ((value != null) && (value instanceof Number)) {
            if ((Double) value>0.001 || (Double) value==0) {
                value = dfBig.format(value);
            } else {
                value = dfSmall.format(value);
            }
      } 
      super.setValue(value);
    } 
 
 
    }

    /** Creates new form KMatrixPanel */
    public KMatrixPanelForm(TgmDataObject dObj) {
        this.dObj = dObj;
        if (dObj.getTgm().getDat().getKMatrixPanel() == null) {
            dObj.getTgm().getDat().setKMatrixPanel(new KMatrixPanelModel());
        }
        this.kMatrixPanelModel = dObj.getTgm().getDat().getKMatrixPanel();
        rowHeader1 = new RowHeader(30, 30);
        rowHeader2 = new RowHeader(30, 30);
        rowHeaderKinpar = new RowHeader(16, 20);
        rowHeaderKinscal = new RowHeader(16, 20);
//        rowHeaderRelations = new  RowHeader(30, 30);
        rowHeaderJVec = new RowHeader(40, 30);
        rowHeaderClpEq = new RowHeader(60, 30);
        rowHeaderCLP0 = new RowHeader(20, 30);

        initComponents();

        matrixSize = kMatrixPanelModel.getJVector().getVector().size();
        jSNumOfComponents.setModel(new SpinnerNumberModel(matrixSize, 0, null, 1));

//        relationsModel = new DefaultTableModel();

        jVec = new DefaultTableModel();
        jVec.addRow(new Vector());

        model1 = new NumberTableModel(0, 0, Integer.class);
        model2 = new NumberTableModel(0, 0, Integer.class);
        modelCLP0 = new NumberTableModel(0, 0, Double.class);
        modelClpEq = new NumberTableModel(0, 0, Double.class);
        //modelClpEq = new SpectralConstraintsTableModel();
        kinparModel = new ParameterTableModel(0);
        kinscalModel = new ParameterTableModel(0);
//initialization from tgm file sizes of the matrices:
        for (int i = 0; i < matrixSize; i++) {
            model1.addColumn(String.valueOf(i + 1));
            model2.addColumn(String.valueOf(i + 1));
//            relationsModel.addColumn(String.valueOf(i+1));
            jVec.addColumn(String.valueOf(i + 1));
            modelCLP0.addColumn(String.valueOf(i + 1));
            modelClpEq.addColumn(String.valueOf(i + 1));

            jTKMatrix1.getColumnModel().addColumn(new KMatrColumn(i, 30));
            jTKMatrix2.getColumnModel().addColumn(new KMatrColumn(i, 30));
            jTClp0.getColumnModel().addColumn(new KMatrColumn(i, 30));
            jTClpEq.getColumnModel().addColumn(new KMatrColumn(i, 30));

//            jTRelations.getColumnModel().addColumn(new RelationColumn(i));
            jTJVector.getColumnModel().addColumn(new JVectorColumn(i));
        }
//initialization of jVec
        for (int i = 0; i < matrixSize; i++) {
            jVec.setValueAt(new JVectorValueClass(kMatrixPanelModel.getJVector().getVector().get(i),
                    kMatrixPanelModel.getJVector().getFixed().get(i)), 0, i);
        }

//fill in fixed to 0 spectra parametersstar
        modelCLP0.addRow(kMatrixPanelModel.getSpectralContraints().getMin().toArray().clone());
        modelCLP0.addRow(kMatrixPanelModel.getSpectralContraints().getMax().toArray().clone());        

//initialization of kMatr
        for (int i = 0; i < matrixSize; i++) {
            model1.addRow(kMatrixPanelModel.getKMatrix().getData().get(i).getRow().toArray().clone());
            rowHeader1.addRow(String.valueOf(i + 1));
            model2.addRow(kMatrixPanelModel.getKMatrix().getData().get(matrixSize + i).getRow().toArray().clone());
            rowHeader2.addRow(String.valueOf(i + 1));
        }
//initialization of clpeq
        for (int i = 0; i < matrixSize; i++) {
            modelClpEq.addRow(kMatrixPanelModel.getContrainsMatrix().getData().get(i).getMin().toArray().clone());
            modelClpEq.addRow(kMatrixPanelModel.getContrainsMatrix().getData().get(i).getMax().toArray().clone());
            modelClpEq.addRow(kMatrixPanelModel.getContrainsMatrix().getData().get(i).getScal().toArray().clone());
            rowHeaderClpEq.addRow(String.valueOf(i + 1));
        }
//initialisation of kinpar and kinscal
        KinparPanelModel kinparPanelModel = dObj.getTgm().getDat().getKinparPanel();
        for (int i = 0; i < kinparPanelModel.getKinpar().size(); i++) {
            kinparModel.addRow(new Object[]{
                        kinparPanelModel.getKinpar().get(i).getStart(),
                        kinparPanelModel.getKinpar().get(i).isFixed(),
                        kinparPanelModel.getKinpar().get(i).isConstrained(),
                        kinparPanelModel.getKinpar().get(i).getMin(),
                        kinparPanelModel.getKinpar().get(i).getMax()
                    });
            rowHeaderKinpar.addRow(String.valueOf(i + 1));
        }

        for (int i = 0; i < kMatrixPanelModel.getKinScal().size(); i++) {
            kinscalModel.addRow(new Object[]{
                        kMatrixPanelModel.getKinScal().get(i).getStart(),
                        kMatrixPanelModel.getKinScal().get(i).isFixed(),
                        kMatrixPanelModel.getKinScal().get(i).isConstrained(),
                        kMatrixPanelModel.getKinScal().get(i).getMin(),
                        kMatrixPanelModel.getKinScal().get(i).getMax()
                    });
            rowHeaderKinscal.addRow(String.valueOf(i + 1));
        }

//initialization of relations
//        for (int i = 0; i <matrixSize; i++){
//            relationsModel.addRow(new Vector(matrixSize));
//            for (int j = 0; j <matrixSize; j++){
//                if (kMatrixPanelModel.getRelatiostarnsMatrix().getData().size()==matrixSize){
//                    relationsModel.setValueAt(new RelationValueClass(
//                            kMatrixPanelModel.getRelationsMatrix().getData().get(i).getC0().get(j),
//                            kMatrixPanelModel.getRelationsMatrix().getData().get(i).getC1().get(j),
//                            kMatrixPanelModel.getRelationsMatrix().getData().get(i).getC0Fixed().get(j),
//                            kMatrixPanelModel.getRelationsMatrix().getData().get(i).getC1Fixed().get(j)),
//                            i, j);
//                }
//            }
//            rowHeaderRelations.addRow(String.valueOf(i+1));
//        }

//add listeners
        model1.addTableModelListener(this);
        model2.addTableModelListener(this);
//        relationsModel.addTableModelListener(this);
        jVec.addTableModelListener(this);
        modelCLP0.addTableModelListener(this);
        modelClpEq.addTableModelListener(this);
        kinparModel.addTableModelListener(this);
        kinscalModel.addTableModelListener(this);

//add row names
        jTKMatrix1.setModel(model1);
        jTKMatrix2.setModel(model2);
        JScrollPane jscpane = (JScrollPane) jTKMatrix1.getParent().getParent();
        jscpane.setRowHeaderView(rowHeader1);
        jscpane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeader1.getTableHeader());

        JScrollPane jscpane0 = (JScrollPane) jTKMatrix2.getParent().getParent();
        jscpane0.setRowHeaderView(rowHeader2);
        jscpane0.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeader2.getTableHeader());

        jTStartingKinpar.setModel(kinparModel);
        jTStartingKinpar.getColumnModel().getColumn(0).setCellRenderer(new smallNumberRenderer());
        jTStartingKinpar.getColumnModel().getColumn(3).setCellRenderer(new smallNumberRenderer());
        jTStartingKinpar.getColumnModel().getColumn(4).setCellRenderer(new smallNumberRenderer());
        JScrollPane jscpane1 = (JScrollPane) jTStartingKinpar.getParent().getParent();
        jscpane1.setRowHeaderView(rowHeaderKinpar);
        jscpane1.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderKinpar.getTableHeader());

        jTStartingKinscal.setModel(kinscalModel);
        jTStartingKinscal.getColumnModel().getColumn(0).setCellRenderer(new smallNumberRenderer());
        jTStartingKinscal.getColumnModel().getColumn(3).setCellRenderer(new smallNumberRenderer());
        jTStartingKinscal.getColumnModel().getColumn(4).setCellRenderer(new smallNumberRenderer());
        JScrollPane jscpane2 = (JScrollPane) jTStartingKinscal.getParent().getParent();
        jscpane2.setRowHeaderView(rowHeaderKinscal);
        jscpane2.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderKinscal.getTableHeader());

//        jTRelations.setModel(relationsModel);
//        JScrollPane jscpane2 = (JScrollPane) jTRelations.getParent().getParent();
//        jscpane2.setRowHeaderView(rowHeaderRelations);
//        jscpane2.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderRelations.getTableHeader());

        jTClpEq.setModel(modelClpEq);
        JScrollPane jscpane5 = (JScrollPane) jTClpEq.getParent().getParent();        
        jscpane5.setRowHeaderView(rowHeaderClpEq);
        jscpane5.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderClpEq.getTableHeader());

        jTJVector.setModel(jVec);
        JScrollPane jscpane3 = (JScrollPane) jTJVector.getParent().getParent();
        rowHeaderJVec.addRow("1");
        jscpane3.setRowHeaderView(rowHeaderJVec);
        jscpane3.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderJVec.getTableHeader());

        jTClp0.setModel(modelCLP0);
        JScrollPane jscpane4 = (JScrollPane) jTClp0.getParent().getParent();
        rowHeaderCLP0.addRow("low");
        rowHeaderCLP0.addRow("high");
        jscpane4.setRowHeaderView(rowHeaderCLP0);
        jscpane4.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderCLP0.getTableHeader());

    }

    public void setValue(JComponent source, Object value) {

        if (source == jTKMatrix1) {
            int val;
            kMatrixPanelModel.getKMatrix().getData().clear();
            IntMatrix.Data tempData;
            for (int j = 0; j < matrixSize; j++) {
                tempData = new IntMatrix.Data();
                for (int i = 0; i < matrixSize; i++) {
                    if (model1.getValueAt(j, i) == null) {
                        val = 0;
                    } else {
                        val = (Integer) model1.getValueAt(j, i);
                    }
                    tempData.getRow().add(val);

                }
                kMatrixPanelModel.getKMatrix().getData().add(tempData);
            }

            for (int j = 0; j < matrixSize; j++) {
                tempData = new IntMatrix.Data();
                for (int i = 0; i < matrixSize; i++) {
                    if (model2.getValueAt(j, i) == null) {
                        val = 0;
                    } else {
                        val = (Integer) model2.getValueAt(j, i);
                    }
                    tempData.getRow().add(val);

                }
                kMatrixPanelModel.getKMatrix().getData().add(tempData);
            }

        }

        if (source == jTStartingKinpar) {
            KinparPanelModel kinparPanelModel = dObj.getTgm().getDat().getKinparPanel();
            kinparPanelModel.getKinpar().clear();
            for (int i = 0; i < kinparModel.getRowCount(); i++) {
                KinPar kp = new KinPar();
                kp.setStart((Double) kinparModel.getValueAt(i, 0));
                kp.setFixed((Boolean) kinparModel.getValueAt(i, 1));
                kp.setConstrained((Boolean) kinparModel.getValueAt(i, 2));
                kp.setMin((Double) kinparModel.getValueAt(i, 3));
                kp.setMax((Double) kinparModel.getValueAt(i, 4));
                kinparPanelModel.getKinpar().add(kp);
            }
        }

        if (source == jTStartingKinscal) {
            kMatrixPanelModel.getKinScal().clear();
            for (int i = 0; i < kinscalModel.getRowCount(); i++) {
                KinPar kp = new KinPar();
                kp.setStart((Double) kinscalModel.getValueAt(i, 0));
                kp.setFixed((Boolean) kinscalModel.getValueAt(i, 1));
                kp.setConstrained((Boolean) kinscalModel.getValueAt(i, 2));
                kp.setMin((Double) kinscalModel.getValueAt(i, 3));
                kp.setMax((Double) kinscalModel.getValueAt(i, 4));
                kMatrixPanelModel.getKinScal().add(kp);
            }
        }


//      if (source ==jTRealations) {
//            kMatrixPanelModel.getRelationsMatrix().getData().clear();
//            Double2BoolMatrix.Data tempData;
//            RelationValueClass val;
//            for (int i=0; i<matrixSize; i++){
//                tempData = new Double2BoolMatrix.Data();
//                for (int j=0; j<matrixSize; j++){
//                    val = (RelationValueClass) relationsModel.getValueAt(i, j);
//                    if (val==null) {
//                        val = new RelationValueClass();
//                    }
//                        tempData.getC0().add(val.getC0());
//                        tempData.getC1().add(val.getC1());
//                        tempData.getC0Fixed().add(val.isFixedC0());
//                        tempData.getC1Fixed().add(val.isFixedC1());
//
//                }
//                kMatrixPanelModel.getRelationsMatrix().getData().add(tempData);
//            }
//    }
        if (source == jTClpEq) {
            Double2Matrix.Data tempData;
            kMatrixPanelModel.getContrainsMatrix().getData().clear();            
            for (int i = 0; i < matrixSize; i++) {
                tempData = new Double2Matrix.Data();
                if (tempData.getScal()==null) {
                }
                for (int j = 0; j < matrixSize; j++) {
                    tempData.getMin().add((Double) modelClpEq.getValueAt(3 * i, j));
                    tempData.getMax().add((Double) modelClpEq.getValueAt(3 * i + 1, j));
                    tempData.getScal().add((Double) modelClpEq.getValueAt(3 * i + 2, j));
                }
                kMatrixPanelModel.getContrainsMatrix().getData().add(tempData);
            }
        }

        if (source == jTJVector) {
            kMatrixPanelModel.getJVector().getFixed().clear();
            kMatrixPanelModel.getJVector().getVector().clear();
            JVectorValueClass val;
            for (int i = 0; i < matrixSize; i++) {
                val = (JVectorValueClass) jVec.getValueAt(0, i);
                kMatrixPanelModel.getJVector().getVector().add(val.getValue());
                kMatrixPanelModel.getJVector().getFixed().add(val.isFixed());
            }
        }

        if (source == jTClp0) {
            kMatrixPanelModel.getSpectralContraints().getMin().clear();
            kMatrixPanelModel.getSpectralContraints().getMax().clear();
            for (int i = 0; i < matrixSize; i++) {
                kMatrixPanelModel.getSpectralContraints().getMin().add((Double) modelCLP0.getValueAt(0, i));
                kMatrixPanelModel.getSpectralContraints().getMax().add((Double) modelCLP0.getValueAt(1, i));
            }
        }


         dObj.setModified(true);
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        if (event.getSource().equals(model1)) {
            setValue(jTKMatrix1, this);
            int data = getParamNumber(model1);
            int kinparNum = kinparModel.getRowCount();
            if (data > kinparNum) {
                kinparModel.removeTableModelListener(this);
                for (int i = 0; i < data - kinparNum; i++) {
                    kinparModel.addRow();
                    rowHeaderKinpar.addRow(String.valueOf(kinparModel.getRowCount()));
                }
                kinparModel.addTableModelListener(this);
                kinparModel.fireTableStructureChanged();
            } else {
                if (data < kinparNum) {
                    kinparModel.removeTableModelListener(this);
                    for (int i = 0; i < kinparNum - data; i++) {
                        rowHeaderKinpar.removeRow(kinparModel.getRowCount() - 1);
                        kinparModel.removeRow(kinparModel.getRowCount() - 1);
                    }
                    kinparModel.addTableModelListener(this);
                    kinparModel.fireTableStructureChanged();
                }
            }
        } else {
            if (event.getSource().equals(model2)) {
                setValue(jTKMatrix1, this);
                int data = getParamNumber(model2);
                int kinparNum = kinscalModel.getRowCount();
                if (data > kinparNum) {
                    kinscalModel.removeTableModelListener(this);
                    for (int i = 0; i < data - kinparNum; i++) {
                        kinscalModel.addRow();
                        rowHeaderKinscal.addRow(String.valueOf(kinscalModel.getRowCount()));
                    }
                    kinscalModel.addTableModelListener(this);
                    kinscalModel.fireTableStructureChanged();
                }
                if (data < kinparNum) {
                    kinscalModel.removeTableModelListener(this);
                    for (int i = 0; i < kinparNum - data; i++) {
                        rowHeaderKinscal.removeRow(kinscalModel.getRowCount() - 1);
                        kinscalModel.removeRow(kinscalModel.getRowCount() - 1);
                    }
                    kinscalModel.addTableModelListener(this);
                    kinscalModel.fireTableStructureChanged();
                }
//                 setValue(jTRelations, this);
            } else {
                if (event.getSource().equals(jVec)) {
                    setValue(jTJVector, this);
                } else {
                    if (event.getSource().equals(modelCLP0)) {
                        setValue(jTClp0, this);
                    } else {
                        if (event.getSource().equals(modelClpEq)) {
                            setValue(jTClpEq, this);
                        } else {
                            if (event.getSource().equals(kinparModel)) {
                                setValue(jTStartingKinpar, this);
                            } else {
                                if (event.getSource().equals(kinscalModel)) {
                                    setValue(jTStartingKinscal, this);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    class NumberTableModel extends DefaultTableModel {// implements TableModelListener {

        private Class cellClass;

        private NumberTableModel(Class var) {
            super();
            cellClass = var;
        }

        private NumberTableModel(int n, int m, Class var) {
            super(n, m);
            cellClass = var;
        }

        @Override
        public Class getColumnClass(int c) {
            return cellClass;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSNumOfComponents = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTKMatrix1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTKMatrix2 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTClpEq = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTStartingKinpar = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTStartingKinscal = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTJVector = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTClp0 = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(700, 470));

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setPreferredSize(new java.awt.Dimension(220, 30));

        jLabel1.setText("Size of K-matrix");

        jSNumOfComponents.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSNumOfComponentsStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSNumOfComponents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(522, 522, 522))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jSNumOfComponents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setLayout(new java.awt.GridLayout(2, 3));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "K-matrix", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 1, 14))); // NOI18N
        jScrollPane1.setMinimumSize(new java.awt.Dimension(90, 90));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(220, 220));

        jTKMatrix1.setAutoCreateColumnsFromModel(false);
        jTKMatrix1.setToolTipText("Specification of the K-Matrix");
        jTKMatrix1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTKMatrix1.setRowHeight(30);
        jTKMatrix1.getTableHeader().setResizingAllowed(false);
        jTKMatrix1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTKMatrix1);

        jPanel2.add(jScrollPane1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Scaling parameters", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 1, 14))); // NOI18N
        jScrollPane2.setMinimumSize(new java.awt.Dimension(90, 90));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(220, 220));

        jTKMatrix2.setAutoCreateColumnsFromModel(false);
        jTKMatrix2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTKMatrix2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTKMatrix2.setName("test"); // NOI18N
        jTKMatrix2.setRowHeight(30);
        jScrollPane2.setViewportView(jTKMatrix2);

        jPanel2.add(jScrollPane2);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Equality of Spectra", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 1, 14))); // NOI18N
        jScrollPane5.setMinimumSize(new java.awt.Dimension(90, 90));
        jScrollPane5.setPreferredSize(new java.awt.Dimension(220, 220));

        jTClpEq.setAutoCreateColumnsFromModel(false);
        jTClpEq.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTClpEq.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTClpEq.setRowHeight(20);
        jScrollPane5.setViewportView(jTClpEq);

        jPanel2.add(jScrollPane5);

        jScrollPane6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Starting values K-matrix", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 1, 11))); // NOI18N
        jScrollPane6.setMinimumSize(new java.awt.Dimension(90, 90));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(220, 220));

        jTStartingKinpar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Value", "Fixed", "Constrained", "Min", "Max"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTStartingKinpar.setToolTipText("Specification of the K-Matrix");
        jTStartingKinpar.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTStartingKinpar.setColumnSelectionAllowed(true);
        jTStartingKinpar.getTableHeader().setResizingAllowed(false);
        jScrollPane6.setViewportView(jTStartingKinpar);
        jTStartingKinpar.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTStartingKinpar.getColumnModel().getColumn(0).setCellRenderer(new smallNumberRenderer());

        jPanel2.add(jScrollPane6);

        jScrollPane7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Starting values scaling parameters", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 1, 11))); // NOI18N
        jScrollPane7.setMinimumSize(new java.awt.Dimension(90, 90));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(220, 220));

        jTStartingKinscal.setToolTipText("Specification of the K-Matrix");
        jTStartingKinscal.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTStartingKinscal.getTableHeader().setResizingAllowed(false);
        jTStartingKinscal.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(jTStartingKinscal);

        jPanel2.add(jScrollPane7);

        jPanel1.setMinimumSize(new java.awt.Dimension(90, 90));
        jPanel1.setPreferredSize(new java.awt.Dimension(220, 220));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "J-vector", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 1, 14))); // NOI18N
        jScrollPane3.setPreferredSize(new java.awt.Dimension(180, 90));

        jTJVector.setAutoCreateColumnsFromModel(false);
        jTJVector.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTJVector.setRowHeight(40);
        jScrollPane3.setViewportView(jTJVector);

        jPanel1.add(jScrollPane3);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Force spectra to 0", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 1, 14))); // NOI18N
        jScrollPane4.setPreferredSize(new java.awt.Dimension(180, 90));

        jTClp0.setAutoCreateColumnsFromModel(false);
        jTClp0.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Spectrum", "From", "To"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTClp0.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTClp0.setColumnSelectionAllowed(true);
        jTClp0.setRowHeight(20);
        jTClp0.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTClp0);
        jTClp0.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jPanel1.add(jScrollPane4);

        jPanel2.add(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jSNumOfComponentsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSNumOfComponentsStateChanged

        model1.removeTableModelListener(this);
        model2.removeTableModelListener(this);
        //relationsModel.removeTableModelListener(this);
        jVec.removeTableModelListener(this);
        modelCLP0.removeTableModelListener(this);
        modelClpEq.removeTableModelListener(this);
        int oldRowCount = matrixSize;
        if ((Integer) jSNumOfComponents.getValue() > matrixSize) {
            for (int i = 0; i < (Integer) jSNumOfComponents.getValue() - oldRowCount; i++) {
                matrixSize = oldRowCount + i + 1;
                model1.addColumn(String.valueOf(matrixSize));
                model2.addColumn(String.valueOf(matrixSize));
                //        relationsModel.addColumn(String.valueOf(matrixSize));
                jVec.addColumn(String.valueOf(matrixSize));
                modelCLP0.addColumn(String.valueOf(matrixSize));
                modelClpEq.addColumn(String.valueOf(matrixSize));

                jTKMatrix1.getColumnModel().addColumn(new KMatrColumn(matrixSize - 1, 30));
                jTKMatrix2.getColumnModel().addColumn(new KMatrColumn(matrixSize - 1, 30));
                //        jTRelations.getColumnModel().addColumn(new RelationColumn(matrixSize-1));
                jTJVector.getColumnModel().addColumn(new JVectorColumn(matrixSize - 1, 40));
                jTClp0.getColumnModel().addColumn(new KMatrColumn(matrixSize - 1, 40));
                jTClpEq.getColumnModel().addColumn(new KMatrColumn(matrixSize - 1, 40));

                model1.addRow(new Vector(matrixSize));
                model2.addRow(new Vector(matrixSize));
                modelClpEq.addRow(new Vector(matrixSize));
                modelClpEq.addRow(new Vector(matrixSize));
                modelClpEq.addRow(new Vector(matrixSize));
                //        relationsModel.addRow(new Vector(matrixSize));
                //        for (int i = 0; i<matrixSize; i++){
                //            relationsModel.setValueAt(new RelationValueClass(), matrixSize-1, i);
                //            relationsModel.setValueAt(new RelationValueClass(), i, matrixSize-1);
                //        }
                rowHeader1.addRow(String.valueOf(matrixSize));
                rowHeader2.addRow(String.valueOf(matrixSize));
                //        rowHeaderRelations.addRow(String.valueOf(matrixSize));
                rowHeaderClpEq.addRow(String.valueOf(matrixSize));
                jVec.setValueAt(new JVectorValueClass(), 0, matrixSize - 1);
            }

        } else {
            for (int i = 0; i < oldRowCount - (Integer) jSNumOfComponents.getValue(); i++) {
                matrixSize = oldRowCount - i - 1;
                model1.removeRow(matrixSize);
                model2.removeRow(matrixSize);
                modelClpEq.removeRow(modelClpEq.getRowCount() - 1);
                modelClpEq.removeRow(modelClpEq.getRowCount() - 1);
                modelClpEq.removeRow(modelClpEq.getRowCount() - 1);
                //        relationsModel.removeRow(matrixSize);

                rowHeader1.removeRow(matrixSize);
                rowHeader2.removeRow(matrixSize);
                //        rowHeaderRelations.removeRow(matrixSize);
                rowHeaderClpEq.removeRow(matrixSize);
                jTKMatrix1.getColumnModel().removeColumn(jTKMatrix1.getColumnModel().getColumn(matrixSize));
                jTKMatrix2.getColumnModel().removeColumn(jTKMatrix2.getColumnModel().getColumn(matrixSize));
                //        jTRelations.getColumnModel().removeColumn(jTRelations.getColumnModel().getColumn(matrixSize));
                jTJVector.getColumnModel().removeColumn(jTJVector.getColumnModel().getColumn(matrixSize));
                jTClp0.getColumnModel().removeColumn(jTClp0.getColumnModel().getColumn(matrixSize));
                jTClpEq.getColumnModel().removeColumn(jTClpEq.getColumnModel().getColumn(matrixSize));
                model1.setColumnCount(matrixSize);
                model2.setColumnCount(matrixSize);
                //        relationsModel.setColumnCount(matrixSize);
                jVec.setColumnCount(matrixSize);
                modelCLP0.setColumnCount(matrixSize);
                modelClpEq.setColumnCount(matrixSize);
            }
        }
        model1.addTableModelListener(this);
        model2.addTableModelListener(this);
//    relationsModel.addTableModelListener(this);
        jVec.addTableModelListener(this);
        modelCLP0.addTableModelListener(this);
        modelClpEq.addTableModelListener(this);
        model1.fireTableStructureChanged();
        model2.fireTableStructureChanged();
//    relationsModel.fireTableStructureChanged();
        jVec.fireTableStructureChanged();
        modelCLP0.fireTableStructureChanged();
        modelClpEq.fireTableStructureChanged();
//    endUIChange();
    }//GEN-LAST:event_jSNumOfComponentsStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner jSNumOfComponents;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTable jTClp0;
    private javax.swing.JTable jTClpEq;
    private javax.swing.JTable jTJVector;
    private javax.swing.JTable jTKMatrix1;
    private javax.swing.JTable jTKMatrix2;
    private javax.swing.JTable jTStartingKinpar;
    private javax.swing.JTable jTStartingKinscal;
    // End of variables declaration//GEN-END:variables

    private int getParamNumber(NumberTableModel tableModel) {
        int number = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                if (tableModel.getValueAt(i, j) != null) {
                    if ((Integer) tableModel.getValueAt(i, j) > number) {
                        number = (Integer) tableModel.getValueAt(i, j);
                    }
                }
            }
        }
        return number;
    }
}
