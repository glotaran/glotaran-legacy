package org.rosuda.rengine;

import org.rosuda.REngine.REXPFactor;
import org.rosuda.REngine.RFactor;
import org.rosuda.irconnect.IRFactor;

public class REngineRFactor implements IRFactor {

    private final RFactor delegate;

    REngineRFactor(final REXPFactor delegate) {
        this(delegate.asFactor());
    }

    REngineRFactor(final RFactor delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("missing required delegate.");
        }
        this.delegate = delegate;
    }

    public void add(final String attr) {
        throw new UnsupportedOperationException();
    }

    public String at(final int i) {
        return delegate.at(i);
    }

    public int size() {
        return delegate.size();
    }
}
