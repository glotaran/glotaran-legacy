package org.rosuda.rengine;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.RList;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRMap;

public class REngineRMap implements IRMap {

    private final RList delegate;

    REngineRMap(final RList delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("missing required delegate.");
        }
        this.delegate = delegate;
    }

    public String[] keys() {
        return delegate.keys();
    }

    public IREXP at(final String key) {
        final REXP rexpat = delegate.at(key);
        if (rexpat == null) {
            return null;
        }
        return new REngineREXP(delegate.at(key));
    }
}
