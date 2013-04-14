/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author Ralf
 */
public class JRIConnection implements IRConnection {

    final Rengine engine;

    JRIConnection(final Rengine engine) {
        this.engine = engine;
    }

    public void voidEval(final String string) {
        engine.eval(string);
    }

    public IREXP eval(final String string) {
        return new JRIREXP(engine.eval(string));
    }

    public void close() {
        engine.end();
    }

    public boolean isConnected() {
        return engine.waitForR();
    }

    public String getLastError() {
        throw null;
    }

    public void shutdown() {
        engine.end();
    }
}
