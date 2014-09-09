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
public class KMatrColumn extends TableColumn {

    KMatrColumn() {
        super();
        super.width = 30;
        super.setPreferredWidth(30);

    }

    KMatrColumn(int modelIndex) {
        super(modelIndex, 30);
        headerValue = String.valueOf(modelIndex + 1);
    }

    KMatrColumn(int modelIndex, int width) {
        super(modelIndex, width);
        headerValue = String.valueOf(modelIndex + 1);
    }
}
