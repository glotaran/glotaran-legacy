/**
 * conveniance implementation that throws UnsportedException on all calls
 * but can be easily adopted for IREXP types without the need to implement each
 * method
 */
package org.rosuda.irconnect;

public abstract class AREXP implements IREXP {

    public IRBool asBool() {
        throw new UnsupportedOperationException();
    }

    public double asDouble() {
        throw new UnsupportedOperationException();
    }

    public double[] asDoubleArray() {
        throw new UnsupportedOperationException();
    }

    public IRFactor asFactor() {
        throw new UnsupportedOperationException();
    }

    public int asInt() {
        throw new UnsupportedOperationException();
    }

    public int[] asIntArray() {
        throw new UnsupportedOperationException();
    }

    public IRMap asMap() {
        throw new UnsupportedOperationException();
    }

    public IRList asList() {
        throw new UnsupportedOperationException();
    }

    public String asString() {
        throw new UnsupportedOperationException();
    }

    public IRVector asVector() {
        throw new UnsupportedOperationException();
    }

    public IREXP getAttribute() {
        throw new UnsupportedOperationException();
    }

    public String[] asStringArray() {
        throw new UnsupportedOperationException();
    }

    public int[] dim() {
        throw new UnsupportedOperationException();
    }

    public int length() {
        throw new UnsupportedOperationException();
    }

    public Object getContent() {
        throw new UnsupportedOperationException();
    }

    public int getType() {
        throw new UnsupportedOperationException();
    }

    public IREXP asSymbol() {
        throw new UnsupportedOperationException();
    }

    public IRBool[] asBoolArray() {
        throw new UnsupportedOperationException();
    }

    public IRMatrix asMatrix() {
        throw new UnsupportedOperationException();
    }

    public boolean hasAttribute(final String attrName) {
        throw new UnsupportedOperationException();
    }

    public IREXP getAttribute(final String attrName) {
        throw new UnsupportedOperationException();
    }
}
