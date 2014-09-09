package org.rosuda.irconnect;

/**
 * verbose tranlation of (supported) types
 * @author Ralf
 */
public final class REXPNames {

    public static String xtName(final int xt) {
        if (xt == IREXPConstants.XT_NULL) {
            return "NULL";
        }
        if (xt == IREXPConstants.XT_INT) {
            return "INT";
        }
        if (xt == IREXPConstants.XT_STR) {
            return "STRING";
        }
        if (xt == IREXPConstants.XT_DOUBLE) {
            return "REAL";
        }
        if (xt == IREXPConstants.XT_BOOL) {
            return "BOOL";
        }
        if (xt == IREXPConstants.XT_ARRAY_INT) {
            return "INT*";
        }
        if (xt == IREXPConstants.XT_ARRAY_STR) {
            return "STRING*";
        }
        if (xt == IREXPConstants.XT_ARRAY_DOUBLE) {
            return "REAL*";
        }
        if (xt == IREXPConstants.XT_ARRAY_BOOL) {
            return "BOOL*";
        }
        if (xt == IREXPConstants.XT_SYM) {
            return "SYMBOL";
        }
        if (xt == IREXPConstants.XT_LANG) {
            return "LANG";
        }
        if (xt == IREXPConstants.XT_LIST) {
            return "LIST";
        }
        if (xt == IREXPConstants.XT_CLOS) {
            return "CLOS";
        }
        if (xt == IREXPConstants.XT_VECTOR) {
            return "VECTOR";
        }
        if (xt == IREXPConstants.XT_FACTOR) {
            return "FACTOR";
        }
        if (xt == IREXPConstants.XT_UNKNOWN) {
            return "UNKNOWN";
        }
        if (xt == IREXPConstants.XT_MAP) {
            return "MAP";
        }
        if (xt == IREXPConstants.XT_MATRIX) {
            return "MATRIX";
        }
        return "<unknown " + xt + ">";
    }
}
