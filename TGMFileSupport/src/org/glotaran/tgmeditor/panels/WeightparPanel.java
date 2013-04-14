/*
 * KinparPanel.java
 *
 * Created on аўторак, 5, жніўня 2008, 19.49
 */
package org.glotaran.tgmeditor.panels;

import javax.swing.JComponent;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.glotaran.core.models.tgm.WeightPar;
import org.glotaran.core.models.tgm.WeightParPanelModel;
import org.glotaran.tgmfilesupport.TgmDataObject;

/**
 *
 * @author  Sergey
 */
public class WeightparPanel extends SectionInnerPanel {

    private TgmDataObject dObj;
    private WeightParPanelModel weightparPanelModel;
    private WeightparTableModel model;
    private Object[] defRow;
    private Object[] colNames;
    private Object[] newRow;

    /** Creates new form KinparPanel */
    public WeightparPanel(SectionView view, TgmDataObject dObj, WeightParPanelModel weightparPanelModel) {
        super(view);
        this.dObj = dObj;
        this.weightparPanelModel = weightparPanelModel;
        initComponents();

        jSNumOfComponents.setModel(new SpinnerNumberModel(weightparPanelModel.getWeightpar().size(), 0, null, 1));

        defRow = new Object[]{new Double(0), new Double(0), new Double(0), new Double(0), new Double(0)};
        colNames = new Object[]{"Min 1", "Max 1", "Min 2", "Max 2", "Weight"};
        model = new WeightparTableModel(colNames, 0);

        int weightparSize = weightparPanelModel.getWeightpar().size();
        for (int i = 0; i < weightparSize; i++) {
            newRow = new Object[]{
                        weightparPanelModel.getWeightpar().get(i).getMin1(),
                        weightparPanelModel.getWeightpar().get(i).getMax1(),
                        weightparPanelModel.getWeightpar().get(i).getMin2(),
                        weightparPanelModel.getWeightpar().get(i).getMax2(),
                        weightparPanelModel.getWeightpar().get(i).getWeight()
                    };
            model.addRow(newRow);

        }
        jTKinParamTable.setModel(model);
        // Add listerners
        jTKinParamTable.getModel().addTableModelListener(model);
    }

    @Override
    public void setValue(JComponent source, Object value) {

        if (source == jTKinParamTable) {
            int weightparSize = weightparPanelModel.getWeightpar().size();
            if (model.getRowCount() > weightparSize) {
                WeightPar wp = new WeightPar();
                wp.setMin1((Double) model.getValueAt((model.getRowCount() - 1), 0));
                wp.setMax1((Double) model.getValueAt((model.getRowCount() - 1), 1));
                wp.setMin2((Double) model.getValueAt((model.getRowCount() - 1), 2));
                wp.setMax2((Double) model.getValueAt((model.getRowCount() - 1), 3));
                wp.setWeight((Double) model.getValueAt((model.getRowCount() - 1), 4));
                weightparPanelModel.getWeightpar().add(wp);
            } else if (model.getRowCount() < weightparSize) {
                weightparPanelModel.getWeightpar().remove(weightparSize - 1);
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                weightparPanelModel.getWeightpar().get(i).setMin1((Double) model.getValueAt(i, 0));
                weightparPanelModel.getWeightpar().get(i).setMax1((Double) model.getValueAt(i, 1));
                weightparPanelModel.getWeightpar().get(i).setMin2((Double) model.getValueAt(i, 2));
                weightparPanelModel.getWeightpar().get(i).setMax2((Double) model.getValueAt(i, 3));
                weightparPanelModel.getWeightpar().get(i).setWeight((Double) model.getValueAt(i, 4));
            }
        }


        endUIChange();
    }

    @Override
    protected void endUIChange() {// signalUIChange() is deprecated{
         dObj.setModified(true);
    }

    public void linkButtonPressed(Object arg0, String arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JComponent getErrorComponent(String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    class WeightparTableModel extends DefaultTableModel implements TableModelListener {

        private Class[] types = new Class[]{Double.class, Double.class, Double.class, Double.class, Double.class};

        private WeightparTableModel() {
            super();
        }

        private WeightparTableModel(Object[] ColNames, int i) {
            super(ColNames, i);
        }

        @Override
        public Class getColumnClass(int c) {
            return types[c];
        }

        @Override
        public void tableChanged(TableModelEvent event) {
            //if (jTKinParamTable.isValid()) {
            setValue(jTKinParamTable, this);
            endUIChange();
            // }
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

        jLabel1 = new javax.swing.JLabel();
        jSNumOfComponents = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTKinParamTable = new javax.swing.JTable();

        jLabel1.setText("Number of weighting paramters");

        jSNumOfComponents.addChangeListener(new javax.swing.event.ChangeListener() {

            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSNumOfComponentsStateChanged(evt);
            }
        });

        jScrollPane1.setViewportView(jTKinParamTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addGap(13, 13, 13).addComponent(jLabel1).addGap(28, 28, 28).addComponent(jSNumOfComponents, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1).addComponent(jSNumOfComponents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addContainerGap()));
    }// </editor-fold>//GEN-END:initComponents

    private void jSNumOfComponentsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSNumOfComponentsStateChanged
// TODO add your handling code here:]
        if ((Integer) jSNumOfComponents.getValue() > model.getRowCount()) {
            model.addRow(defRow);
        } else {
            model.removeRow(model.getRowCount() - 1);
        }
        endUIChange();
    }//GEN-LAST:event_jSNumOfComponentsStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSpinner jSNumOfComponents;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTKinParamTable;
    // End of variables declaration//GEN-END:variables
}
