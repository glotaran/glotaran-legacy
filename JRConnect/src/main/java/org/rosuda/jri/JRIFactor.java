/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import org.rosuda.JRI.RFactor;
import org.rosuda.irconnect.IRFactor;

/**
 *
 * @author Ralf
 */
public class JRIFactor implements IRFactor {

    private final RFactor delegate;

    JRIFactor(final RFactor delegate) {
        this.delegate = delegate;

    }

    public void add(final String v) {
        throw new UnsupportedOperationException();
    }

    public String at(final int i) {
        return delegate.at(i);
    }

    public int size() {
        return delegate.size();
    }
}
