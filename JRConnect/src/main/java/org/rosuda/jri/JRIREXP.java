/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import java.util.Vector;
import org.rosuda.JRI.RBool;
import org.rosuda.irconnect.IRBool;
import org.rosuda.irconnect.IREXP;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.rosuda.irconnect.IRFactor;
import org.rosuda.irconnect.IRList;
import org.rosuda.irconnect.IRMap;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.irconnect.IRVector;
import org.rosuda.irconnect.REXPNames;

/**
 *
 * @author Ralf
 */
public class JRIREXP implements IREXP {

    private REXP delegate;

    JRIREXP(final REXP delegate) {
        this.delegate = delegate;
    }

    public IRList asList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IRMap asMap() {
        return new JRIMap(delegate.asVector());
    }

    public IRVector asVector() {
        return new JRIVector(delegate.asVector());
    }

    public String asString() {
        return delegate.asString();
    }

    public IRBool asBool() {
        if (delegate.getType() == REXP.XT_ARRAY_BOOL_INT) {
            return new JRIBool(new RBool(delegate.asIntArray()[0]));
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getType() {
        if (delegate.getAttribute(dim) != null) {
            return XT_MATRIX;
        }
        if (delegate.getAttribute(dimnames) != null) {
            return XT_MATRIX;
        }
        if (delegate.getType() == XT_VECTOR) {
            final RVector rvect = delegate.asVector();
            //check dim ? it is possible to return a matrix if thats what is wanted!
            final Vector namesVec = rvect.getNames();
            if (namesVec != null && namesVec.size() > 0) {
                return XT_MAP;
            }
        }
        if (delegate.getType() == XT_ARRAY_DOUBLE && delegate.asDoubleArray().length == 1) {
            return XT_DOUBLE;
        }
        if (delegate.getType() == XT_ARRAY_INT && delegate.asIntArray().length == 1) {
            return XT_INT;
        }
        if (delegate.getType() == XT_ARRAY_STR && delegate.asStringArray().length == 1) {
            return XT_STR;
        }
        if (delegate.getType() == REXP.XT_ARRAY_BOOL_INT) {
            if (delegate.asIntArray().length == 1) {
                return XT_BOOL;
            } else {
                return XT_ARRAY_BOOL;
            }
        }
        return delegate.getType();
    }

    public int asInt() {
        if (delegate.getType() == XT_ARRAY_INT) {
            return delegate.asIntArray()[0];
        }
        return delegate.asInt();
    }

    public int[] asIntArray() {
        return delegate.asIntArray();
    }

    public double asDouble() {
        if (delegate.getType() == XT_ARRAY_DOUBLE) {
            return delegate.asDoubleArray()[0];
        }
        return delegate.asDouble();
    }

    public double[] asDoubleArray() {
        return delegate.asDoubleArray();
    }

    public IRFactor asFactor() {
        return new JRIFactor(delegate.asFactor());
    }

    public IREXP getAttribute() {
        return new JRIREXP(delegate.getAttributes());
    }

    public Object getContent() {
        return delegate.getContent();
    }

    public String[] asStringArray() {
        return delegate.asStringArray();
    }

    public int[] dim() {
        final REXP dimREXP = delegate.getAttribute(dim);
        if (dimREXP == null) {
            return null;
        }
        if (dimREXP.getType() == XT_ARRAY_INT) {
            return dimREXP.asIntArray();
        } else if (dimREXP.getType() == XT_INT) {
            return new int[]{dimREXP.asInt()};
        } else {
            throw new IllegalArgumentException("unsupported Type " + REXPNames.xtName(dimREXP.getType()));
        }
    }

    public int length() {
        if (delegate.getType() == XT_ARRAY_BOOL) {
            return 1;
        } else if (delegate.getType() == XT_ARRAY_DOUBLE) {
            return delegate.asDoubleArray().length;
        } else if (delegate.getType() == XT_ARRAY_INT) {
            return delegate.asIntArray().length;
        } else if (delegate.getType() == XT_ARRAY_STR) {
            return delegate.asStringArray().length;
        } else if (delegate.getType() == XT_VECTOR) {
            return delegate.asVector().size();
        }
        throw new IllegalArgumentException("unsupported type " + REXPNames.xtName(delegate.getType()));
    }

    public IREXP asSymbol() {
        return new JRISymbol(delegate);
    }

    public IRBool[] asBoolArray() {
        if (delegate.getType() == REXP.XT_ARRAY_BOOL_INT) {
            final int[] values = delegate.asIntArray();
            final IRBool[] array = new IRBool[values.length];
            for (int i = 0; i < values.length; i++) {
                array[i] = new JRIBool(new RBool(values[i]));
            }
            return array;
        }
        throw new UnsupportedOperationException("Not supports type " + delegate.getType() + " yet.");
    }

    public IRMatrix asMatrix() {
        if (delegate.getType() == XT_ARRAY_DOUBLE) {
            return new JRIDoubleMatrix(delegate);
        } else {
            return new JRIGenericMatrix(delegate);
        }
    }

    public boolean hasAttribute(final String attrName) {
        return (delegate.getAttribute(attrName) != null);
    }

    public IREXP getAttribute(final String attrName) {
        return new JRIREXP(delegate.getAttribute(attrName));
    }
}
