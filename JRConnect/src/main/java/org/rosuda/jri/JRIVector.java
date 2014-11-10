/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import java.util.Vector;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRVector;

/**
 *
 * @author Ralf
 */
public class JRIVector extends Vector implements IRVector {

    private static final long serialVersionUID = 8310271150504477455L;
    private final Vector delegate;

    JRIVector(final Vector delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("cannot create Vector with null delegate!");
        }
        this.delegate = delegate;
        if (this.delegate == null) {
            throw new IllegalArgumentException("cannot create Vector with null list delegate!");
        }
    }

    @Override
    public synchronized Object get(final int idx) {
        final Object rObj = delegate.get(idx);
        return JRIObjectWrapper.wrap(rObj);
    }

    @Override
    public synchronized Object elementAt(final int idx) {
        final Object rObj = delegate.elementAt(idx);
        return JRIObjectWrapper.wrap(rObj);
    }

    public IREXP at(final int idx) {
        final Object rObj = delegate.elementAt(idx);
        return JRIObjectWrapper.wrapAsIREXP(rObj);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public int size() {
        return delegate.size();
    }
}
