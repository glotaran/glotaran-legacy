package org.rosuda.rengine;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;
import org.rosuda.irconnect.IREXP;

public class REngineGenericMatrix extends ARengineMatrix {

    REngineGenericMatrix(final REXP delegate) {
        super(delegate);
        values = new IREXP[rows][columns];
        try {
            final RList list = delegate.asList();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    IREXP value = null;
                    if (list.size() == rows) {
                        final REXP rowElem = list.at(r);
                        if (rowElem.isList()) {
                            final RList rowList = rowElem.asList();
                            if (rowList.size() < columns) {
                                throw new IllegalArgumentException("missing column elements.");
                            }
                            value = new REngineREXP(rowList.at(c));
                        }
                    } else if (list.size() == columns) {
                        final REXP colElem = list.at(c);
                        if (colElem.isList()) {
                            final RList colList = colElem.asList();
                            if (colList.size() < rows) {
                                throw new IllegalArgumentException("missing row elements.");
                            }
                            value = new REngineREXP(colList.at(r));
                        }
                    } else if (list.size() == columns * rows) {
                        int idx = r + c * rows;
                        value = new REngineREXP(list.at(idx));
                    } else {
                        throw new IllegalArgumentException("List size " + list.size() + " does neither match rows(=" + rows + ") nor column(=" + columns + ") length.");
                    }
                    values[r][c] = value;
                }
            }
        } catch (final REXPMismatchException rme) {
            throw new RuntimeException(rme);
        }
    }
}
