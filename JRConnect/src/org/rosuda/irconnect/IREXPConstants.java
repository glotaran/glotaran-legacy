package org.rosuda.irconnect;

public interface IREXPConstants {

    public static final int XT_NULL = 0;
    /** xpression type: integer */
    public static final int XT_INT = 1;
    /** xpression type: double */
    public static final int XT_DOUBLE = 2;
    /** xpression type: String */
    public static final int XT_STR = 3;
    /** xpression type: language construct (currently content is same as list) */
    public static final int XT_LANG = 4;
    /** xpression type: symbol (content is symbol name: String) */
    public static final int XT_SYM = 5;
    /** xpression type: RBool */
    public static final int XT_BOOL = 6;
    /** xpression type: Vector */
    public static final int XT_VECTOR = 16;
    /** xpression type: RList */
    public static final int XT_LIST = 17;
    /** xpression type: closure (there is no java class for that type (yet?). currently the body of the closure is stored in the content part of the REXP. Please note that this may change in the future!) */
    public static final int XT_CLOS = 18;
    /** xpression type: int[] */
    public static final int XT_ARRAY_INT = 32;
    /** xpression type: double[] */
    public static final int XT_ARRAY_DOUBLE = 33;
    /** xpression type: String[] (currently not used, Vector is used instead) */
    public static final int XT_ARRAY_STR = 34;
    /** internal use only! this constant should never appear in a REXP */
    public static final int XT_ARRAY_BOOL_UA = 35;
    /** xpression type: RBool[] */
    public static final int XT_ARRAY_BOOL = 36;
    /** xpression type: unknown; no assumptions can be made about the content */
    public static final int XT_UNKNOWN = 48;
    /** xpression type: RFactor; this XT is internally generated (ergo is does not come from Rsrv.h) to support RFactor class which is built from XT_ARRAY_INT */
    public static final int XT_FACTOR = 127;
    public static final int XT_MAP = -17;
    public static final int XT_MATRIX = -5;
    //key attributes
    public static final String dim = "dim";
    public static final String dimnames = "dimnames";
    public static final String names = "names";
}
