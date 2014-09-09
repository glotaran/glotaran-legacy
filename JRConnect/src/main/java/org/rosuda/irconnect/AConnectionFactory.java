/*
 * This is a conveniance implementation that extracts
 * <strong>host</strong> and <strong>port</strong> from the properties file
 *
 */
package org.rosuda.irconnect;

import java.util.Properties;
import org.rosuda.irconnect.proxy.RConnectionProxy;

/**
 *
 * @author Ralf
 */
public abstract class AConnectionFactory implements IConnectionFactory {

    private static IConnectionFactory instance;

    public static IConnectionFactory getInstance() {
        return instance;
    }

    protected AConnectionFactory() {
        instance = this;
    }

    @Override
    public IRConnection createRConnection(final Properties configuration) {
        return createARConnection(configuration);
    }

    public ITwoWayConnection createTwoWayConnection(final Properties configuration) {
        final IRConnection connection = createARConnection(configuration);
        return RConnectionProxy.createProxy(connection, handleCreateTransfer(connection));
    }

    private final IRConnection createARConnection(final Properties configuration) {
        if (configuration == null) {
            return handleCreateConnection(default_host, default_port);
        }
        String host = default_host;
        int port = default_port;
        if (configuration.containsKey(IConnectionFactory.HOST)) {
            host = configuration.getProperty(IConnectionFactory.HOST);
        }
        if (configuration.containsKey(IConnectionFactory.PORT)) {
            port = Integer.parseInt(configuration.getProperty(IConnectionFactory.PORT));
        }
        final ARConnection connection = handleCreateConnection(host, port);
        if (configuration.containsKey(IConnectionFactory.USER) && configuration.containsKey(IConnectionFactory.PASSWORD)) {
            final String user = configuration.getProperty(IConnectionFactory.USER);
            final String password = configuration.getProperty(IConnectionFactory.PASSWORD);
            connection.login(user, password);
        }
        return connection;
    }

    protected abstract ARConnection handleCreateConnection(final String host, final int port);

    protected abstract IJava2RConnection handleCreateTransfer(final IRConnection con);
}
