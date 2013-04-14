/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.irconnect;

/**
 *
 * @author Ralf
 */
public abstract class ARConnection implements IRConnection {

    protected abstract void login(final String userName, final String userPassword);
}
