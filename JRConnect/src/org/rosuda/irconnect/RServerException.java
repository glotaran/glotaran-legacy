package org.rosuda.irconnect;

/**
 * this class normalizes errors from the connection and throws an UNTAGGED
 * kind of exception (for better error tracing)
 * @author Ralf
 */
public class RServerException extends RuntimeException {

    private static final long serialVersionUID = -350914960632738083L;
    private final String errorDescription;
    private final IRConnection connection;
    private final Throwable cause;
    private String msg;

    public RServerException(final String errorDescription, final String msg) {
        this(null, errorDescription, msg);
    }

    public RServerException(final IRConnection rconnection, final String errorDescription) {
        this(rconnection, errorDescription, null);
    }

    public RServerException(final IRConnection rconnection, final String errorDescription, final String msg) {
        this(rconnection, errorDescription, msg, null);
    }

    public RServerException(final IRConnection rconnection, final String errorDescription, final String msg,
            final Throwable cause) {
        super(cause);
        this.errorDescription = errorDescription;
        this.connection = rconnection;
        this.msg = msg;
        this.cause = cause;
    }

    public final String getRequestErrorDescription() {
        return errorDescription;
    }

    public final IRConnection getConnection() {
        return connection;
    }

    @Override
    public String getMessage() {
        if (msg != null) {
            return msg;
        } else {
            return super.getMessage();
        }
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
