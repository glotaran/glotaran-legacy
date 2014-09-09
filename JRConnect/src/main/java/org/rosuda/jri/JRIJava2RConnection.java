/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.jri;

import org.rosuda.irconnect.IJava2RConnection;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author Ralf
 */
public class JRIJava2RConnection implements IJava2RConnection {

    private final JRIConnection connection;
    private final Rengine engine;

    JRIJava2RConnection(final IRConnection connection) {
        if (!(connection instanceof JRIConnection)) {
            throw new IllegalArgumentException("wrong type: " + connection);
        }
        this.connection = (JRIConnection) connection;
        this.engine = this.connection.engine;
    }

    /*unsupported*/
    public void assign(final String name, final byte[] bvalues) {
        int[] values = new int[bvalues.length];
        for (int i = 0; i < bvalues.length; i++) {
            values[i] = bvalues[i];
        }
        assign(name, values);
    }

    public void assign(final String name, final String[] values) {
        engine.assign(name, values);
    }

    public void assign(final String name, final double[] values) {
        engine.assign(name, values);
    }

    public void assign(final String name, final int[] values) {
        engine.assign(name, values);
    }

    public void assign(final String name, final String value) {
        engine.assign(name, value);
    }
}
