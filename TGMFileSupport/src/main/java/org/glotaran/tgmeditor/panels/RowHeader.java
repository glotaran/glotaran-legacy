/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.tgmeditor.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author slapten
 */
public class RowHeader extends JTable {

    private DefaultTableModel dataTableModel;

    RowHeader() {
        super(new DefaultTableModel(0, 1));
//        this.rowHeader= new JTable(rowHeaderModel);
//        rowHeader.setIntercellSpacing(new Dimension(0, 0));

        setDefaultRenderer(Object.class, new RowHeaderRenderer());
        JTableHeader corner = getTableHeader();
        corner.setReorderingAllowed(false);
        corner.setResizingAllowed(false);
        LookAndFeel.installColorsAndFont(this,
                "TableHeader.background",
                "TableHeader.foreground",
                "TableHeader.font");
        setIntercellSpacing(new Dimension(0, 0));
        dataTableModel = (DefaultTableModel) dataModel;
    }

    RowHeader(int heigh, int width) {
        super(new DefaultTableModel(0, 1));
        setIntercellSpacing(new Dimension(0, 0));
        Dimension d = getPreferredScrollableViewportSize();
        d.width = width;
        setPreferredScrollableViewportSize(d);
        setRowHeight(heigh);
        setDefaultRenderer(Object.class, new RowHeaderRenderer());
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        LookAndFeel.installColorsAndFont(this,
                "TableHeader.background",
                "TableHeader.foreground",
                "TableHeader.font");
        dataTableModel = (DefaultTableModel) dataModel;
    }

    public void addRow(String name) {
        dataTableModel.addRow(new Object[]{name});
//        dataTableModel.fireTableRowsInserted(dataTableModel.getRowCount(), dataTableModel.getRowCount());
    }

    public void removeRow(int index) {
        dataTableModel.removeRow(index);
    }

    class RowHeaderRenderer extends DefaultTableCellRenderer implements ListCellRenderer {

        protected Border noFocBorder, focusBorder;

        public RowHeaderRenderer() {
            setOpaque(true);
            setBorder(noFocBorder);
        }

        @Override
        public void updateUI() {
            super.updateUI();
            Border cell = UIManager.getBorder("TableHeader.cellBorder");
            Border focus = UIManager.getBorder("Table.focusCellHighlightBorder");
            focusBorder = new BorderUIResource.CompoundBorderUIResource(cell, focus);
            Insets i = focus.getBorderInsets(this);
            noFocBorder = new BorderUIResource.CompoundBorderUIResource(cell, BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right));
            /* Alternatively, if focus shouldn't be supported:
            focusBorder = noFocusBorder = cell;
             */
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean selected, boolean focused) {
            if (list != null) {
                if (selected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }

                setFont(list.getFont());

                setEnabled(list.isEnabled());
            } else {
                setBackground(UIManager.getColor("TableHeader.background"));
                setForeground(UIManager.getColor("TableHeader.foreground"));
                setFont(UIManager.getFont("TableHeader.font"));
                setEnabled(true);
            }

            if (focused) {
                setBorder(focusBorder);
            } else {
                setBorder(noFocusBorder);
            }

            setValue(value);

            return this;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int column) {
            if (table != null) {
                if (selected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }

                setFont(table.getFont());

                setEnabled(table.isEnabled());
            } else {
                setBackground(UIManager.getColor("TableHeader.background"));
                setForeground(UIManager.getColor("TableHeader.foreground"));
                setFont(UIManager.getFont("TableHeader.font"));
                setEnabled(true);
            }

            if (focused) {
                setBorder(focusBorder);
            } else {
                setBorder(noFocusBorder);
            }

            setValue(value);

            return this;
        }
    }
}
