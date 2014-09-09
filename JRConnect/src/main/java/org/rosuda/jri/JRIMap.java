/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import org.rosuda.JRI.RVector;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRMap;

/**
 *
 * @author Ralf
 */
public class JRIMap implements IRMap {

    private final RVector vector;
    private final String[] names;

    JRIMap(final RVector rvect) {
        this.vector = rvect;
        names = new String[rvect.getNames().size()];
        int i = 0;
        for (final Object name : rvect.getNames()) {
            names[i++] = name.toString();
        }
    }

    public String[] keys() {
        return names;
    }

    public IREXP at(final String key) {
        return new JRIREXP(vector.at(key));
    }
}
