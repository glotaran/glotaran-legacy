package org.rosuda.rengine;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.RFactor;
import org.rosuda.REngine.RList;
import org.rosuda.irconnect.AREXP;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRVector;

public class REngineObjectWrapper {

    static Object wrap(final Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof RList) {
            return new REngineRMap((RList) obj);
        } else if (obj instanceof REXP) {
            return new REngineREXP((REXP) obj);
        } else if (obj instanceof REXPLogical) {
            return new REngineRBool((REXPLogical) obj);
        } else if (obj instanceof RFactor) {
            return new REngineRFactor((RFactor) obj);
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
            return new REngineREXP((REXP) obj);
        }
        throw new IllegalArgumentException("cannot wrap " + obj + " into IREXP");
    }
}
