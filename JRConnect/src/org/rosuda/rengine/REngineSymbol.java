package org.rosuda.rengine;

import org.rosuda.irconnect.AREXP;
import org.rosuda.irconnect.IREXP;

public class REngineSymbol extends AREXP implements IREXP {

    private final String name;

    public REngineSymbol(final String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }

    public String[] asStrings() {
        return new String[]{name};
    }

    @Override
    public int getType() {
        return IREXP.XT_SYM;
    }
}
