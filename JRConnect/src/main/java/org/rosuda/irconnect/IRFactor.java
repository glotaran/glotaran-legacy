package org.rosuda.irconnect;

/**
 * wrapper for R factor
 * @author Ralf
 */
public interface IRFactor {

    void add(java.lang.String v);

    java.lang.String at(int i);

    int size();
}
