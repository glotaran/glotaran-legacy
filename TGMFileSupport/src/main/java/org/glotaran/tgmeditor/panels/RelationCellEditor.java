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
public class RelationCellEditor extends AbstractCellEditor implements TableCellEditor {

    JComponent relCell = new RelationCellPanel();

    @Override
    public Object getCellEditorValue() {
        RelationValueClass cellVal = new RelationValueClass(
                ((RelationCellPanel) relCell).getC0Value(),
                ((RelationCellPanel) relCell).getC1Value(),
                ((RelationCellPanel) relCell).isC0Fixed(),
                ((RelationCellPanel) relCell).isC1Fixed());
        return cellVal;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // 'value' is value contained in the cell located at (rowIndex, vColIndex)
        if (isSelected) {
            // cell (and perhaps other cells) are selected
        }
        // Configure the component with the specified value
        if (value != null) {
            ((RelationCellPanel) relCell).setC0Fixed(((RelationValueClass) value).isFixedC0());
            ((RelationCellPanel) relCell).setC1Fixed(((RelationValueClass) value).isFixedC1());
            ((RelationCellPanel) relCell).setC0Value(((RelationValueClass) value).getC0());
            ((RelationCellPanel) relCell).setC1Value(((RelationValueClass) value).getC1());

        } else {
            relCell = new RelationCellPanel();
        }
        // Return the configured component
        return relCell;
    }

    @Override
    public boolean stopCellEditing() {
        RelationValueClass vlue = (RelationValueClass) getCellEditorValue();
        try {
            vlue.getC0();
            vlue.getC1();
            return super.stopCellEditing();
        } catch (Exception e) {
            return false;
        }

//TODO check if the values are correct        
//        String s = (String)getCellEditorValue();
//
//        if (!isValid(s)) {
//            // Should display an error message at this point
//            return false;
//        }

    }
}

class RelationCellRenderer extends RelationCellPanel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int rowIndex, int vColIndex) {

        if (value != null) {
            RelationValueClass val = (RelationValueClass) value;
            setC0Value(val.getC0());
            setC1Value(val.getC1());
            setC0Fixed(val.isFixedC0());
            setC1Fixed(val.isFixedC1());
        } else {
            setC0Value(0);
            setC1Value(0);
            setC0Fixed(false);
            setC1Fixed(false);
        }
        return this;
    }
}

