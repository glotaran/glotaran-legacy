/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import java.util.logging.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.irconnect.MatrixNamesExtractor;

/**
 *
 * @author Ralf
 */
public abstract class AJRIMatrix implements IRMatrix {

    final REXP delegate;
    final int columns;
    final int rows;
    final String[] columnNames;
    final String[] rowNames;
    IREXP[][] values;

    AJRIMatrix(final REXP delegate) {
        this(delegate, null);
    }

    AJRIMatrix(final REXP delegate, final IREXP[][] values) {
        this.delegate = delegate;
        final IREXP dim = new JRIREXP(delegate.getAttribute(IREXP.dim));
        int tmpRows = 0;
        int tmpCols = 0;

        if (dim != null) {
            if (dim.getType() == IREXP.XT_ARRAY_INT) {
                final int[] dims = dim.asIntArray();
                tmpRows = dims[0];
                tmpCols = dims[1];
            } else {
                Logger.getLogger(AJRIMatrix.class.getName()).severe("could not convert " + dim.getType() + " into dim");
            }
        }
        this.rows = tmpRows;
        this.columns = tmpCols;
        final IREXP dimnames = new JRIREXP(delegate.getAttribute(IREXP.dimnames));
        if (dimnames != null && dimnames.getType() == REXP.XT_VECTOR) {
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
