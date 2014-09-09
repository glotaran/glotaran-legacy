package org.rosuda.rengine;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.RList;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRList;

public class REngineRList implements IRList {

    private final RList delegate;

    REngineRList(final RList delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("missing required delegate");
        }
        this.delegate = delegate;
    }

    public IREXP getBody() {
        final REXP at1 = delegate.at(1);
        if (at1 == null) {
            return null;
        }
        return new REngineREXP(at1);
    }

    public IREXP getHead() {
        final REXP at0 = delegate.at(0);
        if (at0 == null) {
            return null;
        }
        return new REngineREXP(at0);
    }

    public IREXP getTag() {
        final REXP at2 = delegate.at(2);
        if (at2 == null) {
            return null;
        }
        return new REngineREXP(at2);
    }

    public String[] keys() {
        return delegate.keys();
    }

    public IREXP at(final String key) {
        return new REngineREXP(delegate.at(key));
    }
}
