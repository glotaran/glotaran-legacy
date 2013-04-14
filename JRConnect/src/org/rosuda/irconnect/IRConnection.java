package org.rosuda.irconnect;

/**
 * this IRConnection handles R connection 
 * and delegates all calls to implementing wrapper classes.
 * @author Ralf
 */
public interface IRConnection {

    /**
     * evaluate R-Command string without return parameter
     * @param string
     */
    public abstract void voidEval(String string);

    /**
     * evaluate R-Command string without return parameter
     * @param string
     */
    public abstract IREXP eval(String string);

    /**
     * tries to close the current connection
     */
    public abstract void close();

    /**
     * check if the connection is still available or broken (false)
     * @return
     */
    public abstract boolean isConnected();

    /**
     *
     * @return an error code from R if available
     */
    public abstract String getLastError();

    /**
     * the hard version of close - used to terminate the server of the connection
     */
    public abstract void shutdown();
}
