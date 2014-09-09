package org.rosuda.jri;

import org.rosuda.JRI.REXP;
import org.rosuda.irconnect.AREXP;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.REXPNames;

public class JRIGenericMatrix extends AJRIMatrix {

    JRIGenericMatrix(final REXP delegate) {
        super(delegate);
        //extract values
        values = new IREXP[rows][columns];
        if (delegate.getType() == IREXP.XT_ARRAY_STR) {
            final String[] strvalues = delegate.asStringArray();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    final String value = strvalues[rows * c + r];
                    values[r][c] = new AREXP() {

                        @Override
                        public String asString() {
                            return value;
                        }

                        @Override
                        public int getType() {
                            return XT_STR;
                        }
                    };
                }
            }
        } else {
            throw new IllegalArgumentException("unsupported type : " + delegate.getType() + " = " + REXPNames.xtName(delegate.getType()));
        }
        /*
        if (delegate.getType()==IREXP.XT_VECTOR) {          
        for (int r=0;r<rows;r++) {
        for (int c=0;c<columns;c++) {
        }
        }
        }*/
    }
}
