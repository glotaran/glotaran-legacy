package test.org.rosuda.irconnect;

import junit.framework.TestCase;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.rengine.REngineConnectionFactory;

/**
 *
 * @author Ralf
 */
public class TestCreateConnection extends TestCase {

    public void testCreateREngineConnection() {
        IRConnection irConnection = new REngineConnectionFactory().createRConnection(null);
        final IRConnection irConnection2 = new REngineConnectionFactory().createRConnection(null);
        final IRConnection irConnection3 = new REngineConnectionFactory().createRConnection(null);
        final IRConnection irConnection4 = new REngineConnectionFactory().createRConnection(null);
        assertNotNull(irConnection);
        assertNotNull(irConnection2);
        assertNotNull(irConnection3);
        assertNotNull(irConnection4);
        irConnection.close();
        irConnection = new REngineConnectionFactory().createRConnection(null);
        assertNotNull(irConnection.eval("rnorm(10^3)").toString());
        assertNotNull(irConnection2.eval("rnorm(10^3)").toString());
        assertNotNull(irConnection3.eval("rnorm(10^3)").toString());
        assertNotNull(irConnection4.eval("rnorm(10^3)").toString());
        irConnection.close();
        irConnection2.close();
        irConnection3.close();
        irConnection4.close();
    }
}
