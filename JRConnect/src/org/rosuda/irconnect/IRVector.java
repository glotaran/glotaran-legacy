package org.rosuda.irconnect;

/**
 * wrapper for Vector with the available access methods
 * @author Ralf
 */
public interface IRVector {

    public IREXP at(final int index);

    public int size();
}
