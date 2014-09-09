/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.rengine;

import org.rosuda.REngine.REXP;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.irconnect.MatrixNamesExtractor;

/**
 *
 * @author Ralf
 */
public abstract class ARengineMatrix implements IRMatrix {

    final REXP delegate;
    final int columns;
    final int rows;
    final String[] columnNames;
    final String[] rowNames;
    IREXP[][] values;

    ARengineMatrix(final REXP delegate) {
        this(delegate, null);
    }

    ARengineMatrix(final REXP delegate, final IREXP[][] values) {
        this.delegate = delegate;
        columns = delegate.dim()[1];
        rows = delegate.dim()[0];
        final IREXP dimnames = new REngineREXP(delegate._attr().asList().at(1));
        if (dimnames != null && dimnames.getType() == IREXP.XT_VECTOR) {
            this.columnNames = MatrixNamesExtractor.getInstance().getColNames(dimnames);
            this.rowNames = MatrixNamesExtractor.getInstance().getRowNames(dimnames);
        } else {
            this.columnNames = null;
            this.rowNames = null;
        }
        this.values = values;
    }

    public final int getColumns() {
        return columns;
    }

    public final int getRows() {
        return rows;
    }

    public final String getColumnNameAt(final int row) {
        if (columnNames != null && columnNames.length > row) {
            return columnNames[row];
        }
        return null;
    }

    public final String getRowNameAt(final int column) {
        if (rowNames != null && rowNames.length > column) {
            return rowNames[column];
        }
        return null;
    }

    public IREXP getValueAt(final int row, final int col) {
        return values[row][col];
    }
}
