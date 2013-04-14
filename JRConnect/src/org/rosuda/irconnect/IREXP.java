package org.rosuda.irconnect;

public interface IREXP extends IREXPConstants {

    public abstract IRList asList();

    public abstract IRMap asMap();

    public abstract IRVector asVector();

    public abstract String asString();

    public abstract IRBool asBool();

    public abstract int getType();

    public abstract int asInt();

    public abstract int[] asIntArray();

    public abstract double asDouble();

    public abstract double[] asDoubleArray();

    public abstract IRFactor asFactor();

    public abstract IREXP getAttribute();

    public abstract Object getContent();

    public abstract String[] asStringArray();

    public abstract int[] dim();

    public abstract int length();

    public abstract IREXP asSymbol();

    public abstract IRBool[] asBoolArray();

    public abstract IRMatrix asMatrix();

    public abstract boolean hasAttribute(final String attrName);

    public abstract IREXP getAttribute(final String attrName);
}
