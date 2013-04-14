package test.org.rosuda.irconnect;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class TestEngine extends TestCase {

    RConnection connection;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        connection = new RConnection();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        connection.close();
        try {
            connection.shutdown();
        } catch (final RserveException rse) {
        }
    }

    public void testConnection() {
        assertNotNull("not connected", connection);
    }

    public void testLibraries() throws RserveException, REXPMismatchException {
        final REXP libraryREXP =
                connection.eval(
                new StringBuffer().append("try(").append("library()").append(")").toString());

        final RList namedRList = libraryREXP.asList();
        final String[] keys = namedRList.keys();
        assertEquals("header", keys[0]);
        assertEquals("results", keys[1]);
        assertEquals("footer", keys[2]);
        final REXP header = namedRList.at("header");
        assertTrue(header.isNull());
        final REXP footer = namedRList.at("footer");
        assertTrue(footer.isNull());
        final REXP results = namedRList.at("results");
        assertNotNull(results.dim());
        assertEquals(3, results.dim()[1]);
        assertTrue(results.isString());
        assertTrue(results.length() > 1);
        final String[] strings = results.asStrings();
        assertTrue(results.hasAttribute("dim"));
        final REXP dim = results.getAttribute("dim");
        assertTrue(dim.isInteger());
        assertTrue(dim.length() == 2);
        final int[] dimensions = dim.asIntegers();
        assertEquals(3, dimensions[1]);
        final int rows = dimensions[0];
        assertEquals(rows * 3, results.length());

        final int length = dimensions[0];
        final List<TestWrappedEngine.Library> libs = new ArrayList<TestWrappedEngine.Library>();
        for (int i = 0; i < length; i++) {
            final TestWrappedEngine.Library lib = new TestWrappedEngine.Library();
            lib.name = strings[i];
            lib.active = false;//loadedLibraries.contains(lib.name);
            lib.lib = strings[i + length];
            lib.desc = strings[i + 2 * length];
            libs.add(lib);
        }
    }
}
