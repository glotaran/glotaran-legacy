package org.rosuda.irconnect;

/**
 * Helper class to find out how matrix row and columnames should be called
 * @author Ralf
 */
public class MatrixNamesExtractor {

    private static final MatrixNamesExtractor instance = new MatrixNamesExtractor();

    protected MatrixNamesExtractor() {
    }

    public static MatrixNamesExtractor getInstance() {
        return instance;
    }

    public String[] getColNames(final IREXP theMatrix) {
        if (theMatrix.getType() == IREXP.XT_VECTOR) {
            final IRVector namesVec = theMatrix.asVector();
            if (namesVec.size() > 1) {
                final IREXP colNames = namesVec.at(1);
                if (colNames.getType() == IREXP.XT_ARRAY_STR) {
                    return colNames.asStringArray();
                } else if (colNames.getType() == IREXP.XT_NULL) {
                    return null;
                } else {
                    throw new IllegalArgumentException("type " + colNames.getType() + " cannot be converted into colnames");
                }
            }
        }
        throw new IllegalArgumentException("could not convert type " + theMatrix.getType() + " into column names");
    }

    public String[] getRowNames(final IREXP theMatrix) {
        if (theMatrix.getType() == IREXP.XT_VECTOR) {
            final IRVector namesVec = theMatrix.asVector();
            final IREXP rowNames = namesVec.at(0);
            if (rowNames.getType() == IREXP.XT_ARRAY_STR) {
                return rowNames.asStringArray();
            } else if (rowNames.getType() == IREXP.XT_NULL) {
                return null;
            } else {
                throw new IllegalArgumentException("type " + rowNames.getType() + " cannot be converted into rownames");
            }
        }
        throw new IllegalArgumentException("could not convert type " + theMatrix.getType() + " into row names");
    }
}
