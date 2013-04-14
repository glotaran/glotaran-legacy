package org.rosuda.jri;

import org.rosuda.JRI.REXP;
import org.rosuda.irconnect.AREXP;
import org.rosuda.irconnect.IREXP;

public class JRIDoubleMatrix extends AJRIMatrix {

    JRIDoubleMatrix(final REXP delegate) {
        super(delegate);
        values = new IREXP[rows][columns];
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
    }
}
