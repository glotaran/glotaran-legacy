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
public class RelationColumn extends TableColumn {

    RelationColumn() {
        super();
        super.width = 55;
        super.setPreferredWidth(55);
        super.setCellEditor(new RelationCellEditor());
        super.setCellRenderer(new RelationCellRenderer());
    }

    RelationColumn(int modelIndex) {
        super(modelIndex, 55);
        headerValue = String.valueOf(modelIndex + 1);
        super.setCellEditor(new RelationCellEditor());
        super.setCellRenderer(new RelationCellRenderer());

    }

    RelationColumn(int modelIndex, int width) {
        super(modelIndex, width);
        headerValue = String.valueOf(modelIndex + 1);
        super.setCellEditor(new RelationCellEditor());
        super.setCellRenderer(new RelationCellRenderer());
    }
}
