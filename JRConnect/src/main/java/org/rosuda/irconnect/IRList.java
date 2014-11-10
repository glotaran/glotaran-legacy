package org.rosuda.irconnect;

/**
 * support the old 0.22 Rserve list interface (not recommended)
 * use IRMap instead!
 * @author Ralf
 * @deprecated
 */
@Deprecated
public interface IRList {

    IREXP getBody();

    IREXP getHead();

    IREXP getTag();

    String[] keys();

    IREXP at(String string);
}
