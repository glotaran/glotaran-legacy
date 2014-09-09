package org.rosuda.rengine;

import java.util.Vector;

import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.RList;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRVector;

public class REngineVector extends Vector implements IRVector {

    private static final long serialVersionUID = 8310271150504477455L;
    private final RList delegate;

    REngineVector(final REXPGenericVector delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("cannot create Vector with null delegate!");
        }
        this.delegate = delegate.asList();
        if (this.delegate == null) {
            throw new IllegalArgumentException("cannot create Vector with null list delegate!");
        }
    }

    @Override
    public synchronized Object get(final int idx) {
        final Object rObj = delegate.get(idx);
        return REngineObjectWrapper.wrap(rObj);
    }

    @Override
    public synchronized Object elementAt(final int idx) {
        final Object rObj = delegate.elementAt(idx);
        return REngineObjectWrapper.wrap(rObj);
    }

    public IREXP at(final int idx) {
        final Object rObj = delegate.elementAt(idx);
        return REngineObjectWrapper.wrapAsIREXP(rObj);
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
