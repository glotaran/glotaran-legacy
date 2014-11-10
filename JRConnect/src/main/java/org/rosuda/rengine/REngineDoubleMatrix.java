package org.rosuda.rengine;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.irconnect.AREXP;
import org.rosuda.irconnect.IREXP;

public class REngineDoubleMatrix extends ARengineMatrix {

    REngineDoubleMatrix(final REXP delegate) {
        super(delegate);
        values = new IREXP[rows][columns];
        try {
            final double[][] matrix = delegate.asDoubleMatrix();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    //final int idx = r+c*rows;
                    final double dval = matrix[r][c];
                    values[r][c] = new AREXP() {

                        @Override
                        public double asDouble() {
                            return dval;
                        }

                        @Override
                        public int getType() {
                            return IREXP.XT_DOUBLE;
                        }
                    };
                }
            }
        } catch (final REXPMismatchException rme) {
            throw new RuntimeException(rme);
        }
    }
}
