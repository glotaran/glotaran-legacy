/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.irconnect;

/**
 *
 * @author Ralf
 */
public interface IJava2RConnection {

    public abstract void assign(final String name, final String[] values);

    public abstract void assign(final String name, final double[] values);

    public abstract void assign(final String name, final int[] values);

    public abstract void assign(final String name, final String value);
}
