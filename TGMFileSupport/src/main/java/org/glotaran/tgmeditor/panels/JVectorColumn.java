/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.tgmeditor.panels;

import javax.swing.table.TableColumn;

/**
 *
 * @author slapten
 */
public class JVectorColumn extends TableColumn {

    JVectorColumn() {
        super();
        super.width = 30;
        super.setPreferredWidth(30);
        super.setCellEditor(new JVectorCellEditor());
        super.setCellRenderer(new JVectorCellRenderer());
    }

    JVectorColumn(int modelIndex) {
        super(modelIndex, 30);
        headerValue = String.valueOf(modelIndex + 1);
        super.setCellEditor(new JVectorCellEditor());
        super.setCellRenderer(new JVectorCellRenderer());

    }

    JVectorColumn(int modelIndex, int width) {
        super(modelIndex, width);
        headerValue = String.valueOf(modelIndex + 1);
        super.setCellEditor(new JVectorCellEditor());
        super.setCellRenderer(new JVectorCellRenderer());
    }
}
