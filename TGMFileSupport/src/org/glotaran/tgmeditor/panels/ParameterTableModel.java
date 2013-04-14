/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.tgmeditor.panels;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author slapten
 */
public class ParameterTableModel extends DefaultTableModel {//implements TableModelListener {

    private Class[] types = new Class[]{Double.class, Boolean.class, Boolean.class, Double.class, Double.class};

    public ParameterTableModel() {
        super();
        this.setColumnIdentifiers(new Object[]{"Starting value", "Fixed", "Constrained", "Min", "Max"});
    }

    public ParameterTableModel(Object[] ColNames, int i) {
        super(ColNames, i);
    }

    public ParameterTableModel(int i) {
        super(new Object[]{"Starting value", "Fixed", "Constrained", "Min", "Max"}, i);
    }

    public void addRow() {
        super.addRow(new Object[]{new Double(0), new Boolean(false), new Boolean(false), new Double(0), new Double(0)});
    }

    @Override
    public Class getColumnClass(int c) {
        return types[c];
    }
//    @Override
//    public void tableChanged(TableModelEvent event) {
//        //if (jTKinParamTable.isValid()) {
//        setValue(jTKinParamTable, this);
//        endUIChange();
//       // }
//    }
//
}
