package org.glotaran.tgmeditor.panels;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author slapten
 */
public class JVectorCellEditor extends AbstractCellEditor implements TableCellEditor {

    JComponent jVecCell = new JVectorCellPanel();

    @Override
    public Object getCellEditorValue() {
        JVectorValueClass cellVal = new JVectorValueClass(((JVectorCellPanel) jVecCell).getValueNumber(), ((JVectorCellPanel) jVecCell).isValueFixed());
        return cellVal;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // 'value' is value contained in the cell located at (rowIndex, vColIndex)
        if (isSelected) {
            // cell (and perhaps other cells) are selected
        }
        // Configure the component with the specified value
        ((JVectorCellPanel) jVecCell).setValueFixed(((JVectorValueClass) value).isFixed());
        ((JVectorCellPanel) jVecCell).setValueNumber(((JVectorValueClass) value).getValue());
        // Return the configured component
        return jVecCell;
    }

    @Override
    public boolean stopCellEditing() {
//TODO check if the values are correct        
//        String s = (String)getCellEditorValue();
//
//        if (!isValid(s)) {
//            // Should display an error message at this point
//            return false;
//        }
        return super.stopCellEditing();
    }
}

class JVectorCellRenderer extends JVectorCellPanel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int rowIndex, int vColIndex) {

        if (value != null) {
            JVectorValueClass val = (JVectorValueClass) value;
            setValueNumber(val.getValue());
            setValueFixed(val.isFixed());
        } else {
            setValueNumber(0);
            setValueFixed(false);
        }
        return this;
    }
}

