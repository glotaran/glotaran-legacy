package test.org.rosuda.irconnect;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import junit.framework.TestCase;

public class TestReuseConnection extends TestCase {

    RConnection connection;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        connection = new RConnection();
    }

    public void testDummy() {
    }

    /**
     * this test crashes REngine (now)
     */
    public void testLoadLib() {
        try {
            connection.voidEval("1/3");
        } catch (RserveException e) {
            fail(e.getMessage());
        }
        try {
            //force an error:
            connection.voidEval("library(moonboots)");
        } catch (RserveException e) {
            //fail(e.getMessage());
        }
        //connection seems irreparable:
        try {
            connection.voidEval("1/3");
        } catch (RserveException e) {
            fail(e.getMessage());
        }


    }
}
