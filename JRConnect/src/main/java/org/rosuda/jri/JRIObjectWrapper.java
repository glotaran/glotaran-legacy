/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import org.rosuda.JRI.RBool;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RFactor;
import org.rosuda.JRI.RVector;
import org.rosuda.irconnect.AREXP;
import org.rosuda.irconnect.IREXP;

/**
 *
 * @author Ralf
 */
public class JRIObjectWrapper {

    static Object wrap(final Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof RVector) {
            return new JRIMap((RVector) obj);
        } else if (obj instanceof REXP) {
            return new JRIREXP((REXP) obj);
        } else if (obj instanceof RBool) {
            return new JRIBool((RBool) obj);
        } else if (obj instanceof RFactor) {
            return new JRIFactor((RFactor) obj);
        }
        return obj;
    }

    static IREXP wrapAsIREXP(final Object obj) {
        if (obj == null) {
            return new AREXP() {

                @Override
                public int getType() {
                    return XT_NULL;
                }
            };
        } else if (obj instanceof REXP) {
            return new JRIREXP((REXP) obj);
        }
        throw new IllegalArgumentException("cannot wrap " + obj + " into IREXP");
    }
}
