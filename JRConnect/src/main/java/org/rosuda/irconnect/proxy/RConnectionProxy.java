/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.irconnect.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IJava2RConnection;
import org.rosuda.irconnect.ITwoWayConnection;

/**
 *
 * @author Ralf
 */
public class RConnectionProxy implements InvocationHandler {

    private final static Class[] interfaces = new Class[]{IRConnection.class, IJava2RConnection.class, ITwoWayConnection.class};
    private final Object[] delegates;
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;

    static {

        try {
            hashCodeMethod = Object.class.getMethod("hashCode", null);
            equalsMethod = Object.class.getMethod("equals", new Class[]{Object.class});
            toStringMethod = Object.class.getMethod("toString", null);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public static ITwoWayConnection createProxy(final IRConnection irconnection, final IJava2RConnection irtransfer) {
        return (ITwoWayConnection) Proxy.newProxyInstance(irconnection.getClass().getClassLoader(),
                interfaces,
                new RConnectionProxy(irconnection, irtransfer));
    }

    private RConnectionProxy(final IRConnection irconnection, final IJava2RConnection irtransfer) {
        delegates = new Object[]{irconnection, irtransfer};
    }

    public Object invoke(final Object proxy, final Method m, final Object[] args)
            throws Throwable {
        Class declaringClass = m.getDeclaringClass();

        if (declaringClass == Object.class) {
            if (m.equals(hashCodeMethod)) {
                return proxyHashCode(proxy);
            } else if (m.equals(equalsMethod)) {
                return proxyEquals(proxy, args[0]);
            } else if (m.equals(toStringMethod)) {
                return proxyToString(proxy);
            } else {
                throw new InternalError(
                        "unexpected Object method dispatched: " + m);
            }
        } else {
            for (int i = 0; i < interfaces.length; i++) {
                if (declaringClass.isAssignableFrom(interfaces[i])) {
                    try {
                        return m.invoke(delegates[i], args);
                    } catch (final InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            }

            throw new UnsupportedOperationException("cannot execute method " + m);
        }
    }

    protected Integer proxyHashCode(Object proxy) {
        return new Integer(System.identityHashCode(proxy));
    }

    protected Boolean proxyEquals(Object proxy, Object other) {
        return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
    }

    protected String proxyToString(Object proxy) {
        return proxy.getClass().getName() + '@'
                + Integer.toHexString(proxy.hashCode());
    }
}
