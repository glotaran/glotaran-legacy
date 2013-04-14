/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import org.rosuda.JRI.RBool;
import org.rosuda.irconnect.IRBool;

/**
 *
 * @author Ralf
 */
public class JRIBool implements IRBool {

    private RBool boolValue;

    JRIBool(final RBool bool) {
        this.boolValue = bool;
    }

    public boolean isFALSE() {
        return boolValue.isFALSE();
    }

    public boolean isNA() {
        return boolValue.isNA();
    }

    public boolean isTRUE() {
        return boolValue.isTRUE();
    }
}
