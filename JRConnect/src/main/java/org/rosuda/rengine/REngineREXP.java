package org.rosuda.rengine;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.constants.Const;
import org.rosuda.irconnect.IRBool;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRFactor;
import org.rosuda.irconnect.IRList;
import org.rosuda.irconnect.IRMap;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.irconnect.IRVector;

public class REngineREXP implements IREXP {

    private final REXP delegate;

    public REngineREXP(final REXP delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("missing required delegate.");
        }
        this.delegate = delegate;
    }

    public IRBool asBool() {
        return new REngineRBool((REXPLogical) delegate);
    }

    public double asDouble() {
        try {
            if (delegate.length() < 1) {
                return Double.NaN;
            }
            return delegate.asDouble();
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }
    }

    public double[] asDoubleArray() {
        try {
            return delegate.asDoubles();
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }
    }

    public IRFactor asFactor() {
        try {
            return new REngineRFactor(delegate.asFactor());
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }
    }

    public int asInt() {
        try {
            if (delegate.length() < 1) {
                return Integer.MIN_VALUE;
            }
            return delegate.asInteger();
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }
    }

    public int[] asIntArray() {
        try {
            return delegate.asIntegers();
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }

    }

    public IRList asList() {
        try {
            return new REngineRList(delegate.asList());
        } catch (REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }
    }

    public String asString() {
        try {
            if (delegate.length() < 1) {
                return null;
            }
            return delegate.asString();
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }

    }

    public IRVector asVector() {
        return new REngineVector((REXPGenericVector) delegate);
    }

    public IREXP getAttribute() {
        return null;
    }

    public Object getContent() {
        try {
            return delegate.asBytes();
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }

    }

    public int getType() {
        try {
            if (delegate.isNull()) {
                return IREXP.XT_NULL;
            } else if (delegate.isSymbol()) {
                return IREXP.XT_SYM;
            } else if (delegate.isFactor()) {
                return IREXP.XT_FACTOR;
            } else if (delegate.length() <= 1) {
                if (delegate.isInteger()) {
                    return IREXP.XT_INT;
                } else if (delegate.isNumeric()) {
                    return IREXP.XT_DOUBLE;
                } else if (delegate.isString()) {
                    return IREXP.XT_STR;
                } else if (delegate.isLogical()) {
                    return IREXP.XT_BOOL; // ? XT_LOGICAL
                } else if (delegate.isVector()) {
                    return IREXP.XT_VECTOR;
                }
            } else /*length > 1*/ {
                if (delegate.isInteger()) {
                    return IREXP.XT_ARRAY_INT;
                } else if (delegate.isNumeric()) {
                    if (delegate.dim() != null && delegate.dim().length == 2) {
                        return IREXP.XT_MATRIX;
                    }
                    return IREXP.XT_ARRAY_DOUBLE;
                } else if (delegate.isString()) {
                    return IREXP.XT_ARRAY_STR;
                } else if (delegate.isLogical()) {
                    return IREXP.XT_ARRAY_BOOL; // ? XT_LOGICAL
                } else if (delegate.isVector() && !delegate.isList() && (delegate instanceof REXPGenericVector)) {
                    return IREXP.XT_VECTOR;
                } else if (delegate.dim() != null && delegate.dim().length == 2) {
                    return IREXP.XT_MATRIX;
                } else if (delegate.isList() && delegate.asList().keys() == null) {
                    return IREXP.XT_VECTOR;
                } else if (delegate.isList()) {
                    return IREXP.XT_MAP;
                } else if (delegate.isVector()) {
                    return IREXP.XT_VECTOR;
                }
            }
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }
        System.out.println("unknown type = " + delegate + " isVector?" + delegate.isVector() + " isList?" + delegate.isList());
        try {
            System.out.println("unknown type = " + delegate + " length:" + delegate.length());
        } catch (REXPMismatchException e) {
        }
        return -1;
    }

    public IRMap asMap() {
        try {
            return new REngineRMap(delegate.asList());
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }

    }

    public String[] asStringArray() {
        try {
            return delegate.asStrings();
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }
    }

    public int[] dim() {
        return delegate.dim();
    }

    public int length() {
        try {
            return delegate.length();
        } catch (final REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }
    }

    public IREXP asSymbol() {
        try {
            return new REngineSymbol(delegate.asString());
        } catch (REXPMismatchException e) {
            throw new UnsupportedOperationException();
        }
    }

    public IRBool[] asBoolArray() {
        final REXPLogical logical = (REXPLogical) delegate;
        final IRBool[] boolArray = new IRBool[logical.length()];
        for (int i = 0; i < logical.length(); i++) {
            boolArray[i] = new REngineRBool(logical, i);
        }
        return boolArray;
    }

    public String toString() {
        if (delegate.isString()) {
            try {
                if (delegate.length() == 0) {
                    return delegate.toString();
                }
                if (delegate.length() == 1) {
                    return delegate.asString();
                }
                String[] pars = delegate.asStrings();
                if (pars == null) {
                    return null;
                }
                final StringBuffer buf = new StringBuffer();
                for (int i = 0; i < pars.length; i++) {
                    buf.append(pars[i]);
                    buf.append(Const.LINE_SEPARATOR);
                }
                return buf.toString();
            } catch (final REXPMismatchException e) {
                throw new RuntimeException(e);
            }
        }
        return delegate.toString();
    }

    public IRMatrix asMatrix() {
        if (delegate.isNumeric()) {
            return new REngineDoubleMatrix(delegate);
        } else {
            return new REngineGenericMatrix(delegate);
        }
    }

    public boolean hasAttribute(final String attrName) {
        return delegate.hasAttribute(attrName);
    }

    public IREXP getAttribute(final String attrName) {
        return new REngineREXP(delegate.getAttribute(attrName));
    }
}
