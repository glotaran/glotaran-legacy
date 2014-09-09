package org.rosuda.rengine;

import java.net.SocketException;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RSession;
import org.rosuda.REngine.Rserve.RserveException;
import org.rosuda.irconnect.ARConnection;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.RServerException;

public class REngineRConnection extends ARConnection implements IRConnection {

    protected static Logger logger = Logger.getLogger(REngineRConnection.class.getName());
    final RConnection delegate;

    REngineRConnection(final String host, final int port) {
        try {
            this.delegate = new RConnection(host, port);
        } catch (final RserveException rse) {
            rse.printStackTrace();
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage());
        }
        if (this.delegate == null) {
            throw new IllegalArgumentException("missing required delegate.");
        }
    }

    public void close() {
        delegate.close();
    }

    /**
     *
     * @return
     */
    public RSession detach() {
        RSession session = null;
        try {
            session = delegate.detach();
        } catch (RserveException ex) {
            Logger.getLogger(REngineRConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return session;
    }

    public IREXP eval(final String query) {
        try {
            return new REngineREXP(delegate.eval(query));
        } catch (final RserveException rse) {
            if (rse.getCause() != null && rse.getCause() instanceof SocketException) {
                logger.log(Level.SEVERE, "SocketException on :" + query);
            }
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage() + " on r-command:" + query, rse);
        }
    }

    public String getLastError() {
        return delegate.getLastError();
    }

    public boolean isConnected() {
        return delegate.isConnected();
    }

    public void shutdown() {
        try {
            delegate.shutdown();
        } catch (final RserveException rse) {
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage());
        }
    }

    public void voidEval(final String query) {
        try {
            delegate.voidEval(query);
        } catch (final RserveException rse) {
            if (rse.getCause() != null && rse.getCause() instanceof SocketException) {
                logger.log(Level.SEVERE, "SocketException on :" + query);
            }
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage() + " on r-command:" + query, rse);
        }
    }

    @Override
    protected void login(final String userName, final String userPassword) {
        try {
            delegate.login(userName, userPassword);
        } catch (final RserveException rse) {
            Logger.getLogger(REngineRConnection.class.getName()).log(Level.SEVERE, null, rse);
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage() + " on login user \"" + userName + "\"", rse);
        }
    }
}
