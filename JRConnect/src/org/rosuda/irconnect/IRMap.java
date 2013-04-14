package org.rosuda.irconnect;

/**
 * getter map and replaces the old IRList
 * @author Ralf
 */
public interface IRMap {

    public abstract String[] keys();

    public abstract IREXP at(final String string);
}
