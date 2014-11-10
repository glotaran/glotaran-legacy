/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import org.rosuda.JRI.REXP;
import org.rosuda.irconnect.AREXP;

/**
 *
 * @author Ralf
 */
public class JRISymbol extends AREXP {

    private final String name;

    JRISymbol(final REXP delegate) {
        this.name = delegate.asSymbolName();
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
        return XT_SYM;
    }
}
