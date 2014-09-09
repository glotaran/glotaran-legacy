/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.rengine;

import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;
import org.rosuda.irconnect.IJava2RConnection;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;

/**
 *
 * @author Ralf
 */
public class REngineJava2RConnection implements IJava2RConnection {

    private final IRConnection irConnection;
    private final RConnection connection;

    REngineJava2RConnection(final REngineRConnection connection) {
        this.irConnection = connection;
        this.connection = connection.delegate;
    }

    /*unsupported*/
    public void assign(final String name, final byte[] values) {
        try {
            connection.assign(name, values);
        } catch (final REngineException x) {
            throw new RServerException(irConnection,
                    "assign failed",
                    "name<-" + values == null ? null : "byte[" + values.length + "]",
                    x);
        }
    }

    public void assign(final String name, final String[] values) {
        try {
            connection.assign(name, values);
        } catch (final REngineException x) {
            throw new RServerException(irConnection,
                    "assign failed",
                    "name<-" + values == null ? null : "String[" + values.length + "]",
                    x);
        }
    }

    public void assign(final String name, final double[] values) {
        try {
            connection.assign(name, values);
        } catch (final REngineException x) {
            throw new RServerException(irConnection,
                    "assign failed",
                    "name<-" + values == null ? null : "double[" + values.length + "]",
                    x);
        }
    }

    public void assign(final String name, final int[] values) {
        try {
            connection.assign(name, values);
        } catch (final REngineException x) {
            throw new RServerException(irConnection,
                    "assign failed",
                    "name<-" + values == null ? null : "int[" + values.length + "]",
                    x);
        }
    }

    public void assign(final String name, final String value) {
        try {
            connection.assign(name, value);
        } catch (final RserveException x) {
            throw new RServerException(irConnection,
                    "assign failed",
                    "name<-" + value,
                    x);
        }
    }
}
