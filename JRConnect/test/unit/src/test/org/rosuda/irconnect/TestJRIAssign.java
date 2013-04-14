package test.org.rosuda.irconnect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.rosuda.JRI.REXP;
import org.rosuda.irconnect.IRBool;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IREXPConstants;
import org.rosuda.irconnect.IRFactor;
import org.rosuda.irconnect.IRMap;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.irconnect.IRVector;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.jri.JRIConnectionFactory;

public class TestJRIAssign extends TestCase {

    //static is alas necessary for this test to run on windows machine
    //since closing connection does not enable to connection(s)!
    private static ITwoWayConnection connection;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (connection == null) {
            connection = JRIConnectionFactory.getInstance().createTwoWayConnection(null);
        }
    }

    public static void debugJRIREXPType(final int jriREXPType) {
        for (final Field field : org.rosuda.JRI.REXP.class.getDeclaredFields()) {
            try {
                final REXP rexp = new REXP();
                if (field.isAccessible() && field.getType().isPrimitive() && field.getType() == Integer.TYPE && field.getInt(rexp) == jriREXPType) {

                    System.out.println("jriREXPType " + jriREXPType + " could be " + field.getName());
                }
            } catch (final Exception ex) {
                Logger.getLogger(TestWrappedJRI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void testConnection() {
        assertNotNull("not connected", connection);
    }

    public void testList() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("library()").append(")").toString());

        assertEquals(IREXP.XT_MAP, testREXP.getType());
        final IRMap namedRList = testREXP.asMap();
        final String[] keys = namedRList.keys();
        assertEquals("header", keys[0]);
        assertEquals("results", keys[1]);
        assertEquals("footer", keys[2]);
        final IREXP header = namedRList.at("header");
        assertTrue(header.getType() == IREXP.XT_NULL);
        final IREXP footer = namedRList.at("footer");
        assertTrue(footer.getType() == IREXP.XT_NULL);
        final IREXP results = namedRList.at("results");
        assertNotNull(results.dim());
        assertEquals(3, results.dim()[1]);
        assertTrue(results.getType() == IREXP.XT_MATRIX);
        assertTrue(results.length() > 1);
        final String[] strings = results.asStringArray();
        final int rows = results.dim()[0];


        assertEquals(rows * 3, results.length());

        final List<TestWrappedEngine.Library> libs = new ArrayList<TestWrappedEngine.Library>();
        for (int i = 0; i < rows; i++) {
            final TestWrappedEngine.Library lib = new TestWrappedEngine.Library();
            lib.name = strings[i];
            lib.active = false;// loadedLibraries.contains(lib.name);
            lib.lib = strings[i + rows];
            lib.desc = strings[i + 2 * rows];
            libs.add(lib);
        }
        assertNotNull(libs);
        assertTrue(libs.size() > 0);
    }

    public void testString() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("\"string\"").append(")").toString());
        assertNotNull(testREXP);
        assertEquals(IREXP.XT_STR, testREXP.getType());
        assertEquals("string", testREXP.asString());
    }

    public void testDouble() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("1/4").append(")").toString());

        assertNotNull(testREXP);
        assertEquals(IREXP.XT_DOUBLE, testREXP.getType());
        assertEquals(0.25, testREXP.asDouble(), TestWrappedEngine.EPS);
    }

    public void testInt() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("as.integer(1+4)").append(")").toString());
        assertNotNull(testREXP);
        assertEquals(IREXP.XT_INT, testREXP.getType());

        assertEquals(5, testREXP.asInt());
    }

    public void testBool() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("1 == 1").append(")").toString());
        assertNotNull(testREXP);
        debugJRIREXPType(testREXP.getType());
        assertEquals(IREXP.XT_BOOL, testREXP.getType());
        assertEquals(true, testREXP.asBool().isTRUE());
    }

    public void testSymbol() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("as.symbol(\"x\")").append(")").toString());
        assertNotNull(testREXP);
        assertEquals(IREXP.XT_SYM, testREXP.getType());
        assertEquals("x", testREXP.asSymbol().asString());
    }

    public void testFactor() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("as.factor(c(1,1,1,2,2,1,3,2,3,1))").append(")").toString());
        assertNotNull(testREXP);
        assertEquals(IREXP.XT_FACTOR, testREXP.getType());
        final IRFactor factor = testREXP.asFactor();
        assertNotNull(factor);
        assertEquals(10, factor.size());
        assertEquals("1", factor.at(0));
        assertEquals("1", factor.at(1));
        assertEquals("1", factor.at(2));
        assertEquals("2", factor.at(3));
        assertEquals("2", factor.at(4));
        assertEquals("1", factor.at(5));
        assertEquals("3", factor.at(6));
        assertEquals("2", factor.at(7));
        assertEquals("3", factor.at(8));
        assertEquals("1", factor.at(9));
    }

    public void testStringArray() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("c(\"string\",\"string2\")").append(")").toString());
        assertNotNull(testREXP);
        assertEquals(IREXP.XT_ARRAY_STR, testREXP.getType());
        final String[] sArray = testREXP.asStringArray();
        assertNotNull(sArray);
        assertEquals("string", sArray[0]);
        assertEquals("string2", sArray[1]);
    }

    public void testDoubleArray() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("c(1/4,1/7)").append(")").toString());
        assertNotNull(testREXP);
        assertEquals(IREXP.XT_ARRAY_DOUBLE, testREXP.getType());
        final double[] dArray = testREXP.asDoubleArray();
        assertEquals(0.25, dArray[0], TestWrappedEngine.EPS);
        assertEquals(1.0 / 7.0, dArray[1], TestWrappedEngine.EPS);
    }

    public void testIntArray() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("c(as.integer(1+4),as.integer(1+5))").append(")").toString());
        assertNotNull(testREXP);
        assertEquals(IREXP.XT_ARRAY_INT, testREXP.getType());
        final int[] iArray = testREXP.asIntArray();
        assertEquals(5, iArray[0]);
        assertEquals(6, iArray[1]);
    }

    public void testBoolArray() {
        final IREXP testREXP = connection.eval(new StringBuffer().append(
                "try(").append("c(1 == 1, 1 ==2)").append(")").toString());
        assertNotNull(testREXP);
        assertEquals(IREXP.XT_ARRAY_BOOL, testREXP.getType());
        IRBool[] bArray = testREXP.asBoolArray();
        assertEquals(true, bArray[0].isTRUE());
        assertEquals(true, bArray[1].isFALSE());
    }

    public void testVector() {
        final IREXP testREXP = connection.eval("c(a = as.integer(1), a2 = 2.12, b = 2 == 1, c = as.symbol(\"s\"))");
        assertNotNull(testREXP);
        //TODO distinguish between MAP and VECTOR
        assertEquals(IREXP.XT_MAP, testREXP.getType());
        final IRVector testVector = testREXP.asVector();
        assertNotNull(testVector);
        assertEquals(4, testVector.size());
        final IREXP at0 = testVector.at(0);
        assertNotNull(at0);
        assertEquals(at0.getType(), IREXP.XT_INT);

        final IREXP at1 = testVector.at(1);
        assertNotNull(at1);
        assertEquals(at1.getType(), IREXP.XT_DOUBLE);
    }

    public void testLibraries() {
        final IREXP testREXP =
                connection.eval(
                new StringBuffer().append("try(").append("library()").append(")").toString());
        assertEquals("testREXP is not a List", IREXP.XT_MAP, testREXP.getType());
        final IRMap elementMap = testREXP.asMap();
        //debug names
        assertEquals("elementList.header is not XT_NULL", IREXP.XT_NULL, elementMap.at("header").getType());
        assertEquals("elementList.footer is not a XT_NULL", IREXP.XT_NULL, elementMap.at("footer").getType());
        assertEquals("elementList.keys #0 is not header", "header", elementMap.keys()[0]);
        assertEquals("elementList.keys #1 is not results", "results", elementMap.keys()[1]);
        assertEquals("elementList.keys #2 is not footer", "footer", elementMap.keys()[2]);

        assertEquals("elementList.getBody is not XT_ARRAY_STR", IREXP.XT_MATRIX, elementMap.at("results").getType());
        final IRMatrix listBody = elementMap.at("results").asMatrix();
        //TODO long list
        assertTrue("no of elements is not divisible by 3", listBody.getColumns() % 3 == 0);
    }

    public void testDoubleMatrix() {
        final IREXP testREXP =
                connection.eval(
                new StringBuffer().append("try(").append("summary(lm(dist~speed,data=cars))$coefficients").append(")").toString());
        assertEquals("textREXP is not a matrix", IREXP.XT_MATRIX, testREXP.getType());
        final IRMatrix matrix = testREXP.asMatrix();
        assertNotNull("conversion asMatrx failed!", matrix);
        assertEquals("not 2 rows", 2, matrix.getRows());
        assertEquals("not 4 columns", 4, matrix.getColumns());

        assertEquals("row #1 name wrong", "(Intercept)", matrix.getRowNameAt(0));
        assertEquals("row #2 name wrong", "speed", matrix.getRowNameAt(1));

        assertEquals("column #1 name wrong", "Estimate", matrix.getColumnNameAt(0));
        assertEquals("column #2 name wrong", "Std. Error", matrix.getColumnNameAt(1));
        assertEquals("column #3 name wrong", "t value", matrix.getColumnNameAt(2));
        assertEquals("column #4 name wrong", "Pr(>|t|)", matrix.getColumnNameAt(3));

        //just once the values
        for (int row = 0; row < matrix.getRows(); row++) {
            for (int col = 0; col < matrix.getColumns(); col++) {
                assertEquals("coeff type is wrong @(row=" + row + ",col=" + col + ")", matrix.getValueAt(row, col).getType(), IREXP.XT_DOUBLE);
            }
        }
        assertEquals("coeff value is wrong @(row=0,col=0)", -17.579095, matrix.getValueAt(0, 0).asDouble(), TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=0,col=1)", 6.7584402, matrix.getValueAt(0, 1).asDouble(), TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=0,col=2)", -2.601058, matrix.getValueAt(0, 2).asDouble(), TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=0,col=3)", 1.231882e-02, matrix.getValueAt(0, 3).asDouble(), TestWrappedEngine.EPS);

        assertEquals("coeff value is wrong @(row=1,col=0)", 3.932409, matrix.getValueAt(1, 0).asDouble(), TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=1,col=1)", 0.4155128, matrix.getValueAt(1, 1).asDouble(), TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=1,col=2)", 9.463990, matrix.getValueAt(1, 2).asDouble(), TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=1,col=3)", 1.489836e-12, matrix.getValueAt(1, 3).asDouble(), TestWrappedEngine.EPS);
    }

    public void testGenericMatrix() {
        final IREXP testREXP =
                connection.eval(
                new StringBuffer().append("try(").append("as.matrix(iris)").append(")").toString());
        assertEquals("textREXP is not a matrix", IREXP.XT_MATRIX, testREXP.getType());
        final IRMatrix matrix = testREXP.asMatrix();
        assertNotNull("conversion asMatrix failed!", matrix);

        assertEquals("not 150 rows", 150, matrix.getRows());
        assertEquals("not 5 columns", 5, matrix.getColumns());

        assertEquals("column #1 name wrong", "Sepal.Length", matrix.getColumnNameAt(0));
        assertEquals("column #2 name wrong", "Sepal.Width", matrix.getColumnNameAt(1));
        assertEquals("column #3 name wrong", "Petal.Length", matrix.getColumnNameAt(2));
        assertEquals("column #4 name wrong", "Petal.Width", matrix.getColumnNameAt(3));
        assertEquals("column #5 name wrong", "Species", matrix.getColumnNameAt(4));

        //just once the values
        for (int row = 0; row < matrix.getRows(); row++) {
            for (int col = 0; col < matrix.getColumns(); col++) {
                assertEquals("coeff type is wrong @(row=" + row + ",col=" + col + ")", matrix.getValueAt(row, col).getType(), IREXP.XT_STR);
            }
        }
        //samples !
        //> iris[1:2,]
        //  Sepal.Length Sepal.Width Petal.Length Petal.Width Species
        //1          5.1         3.5          1.4         0.2  setosa
        //2          4.9         3.0          1.4         0.2  setosa
        assertEquals("wrong value @(row=0,col=0)", "5.1", matrix.getValueAt(0, 0).asString());
        assertEquals("wrong value @(row=0,col=1)", "3.5", matrix.getValueAt(0, 1).asString());
        assertEquals("wrong value @(row=0,col=2)", "1.4", matrix.getValueAt(0, 2).asString());
        assertEquals("wrong value @(row=0,col=3)", "0.2", matrix.getValueAt(0, 3).asString());
        assertEquals("wrong value @(row=0,col=4)", "setosa", matrix.getValueAt(0, 4).asString());
        assertEquals("wrong value @(row=1,col=0)", "4.9", matrix.getValueAt(1, 0).asString());
        assertEquals("wrong value @(row=1,col=1)", "3.0", matrix.getValueAt(1, 1).asString());
        assertEquals("wrong value @(row=1,col=2)", "1.4", matrix.getValueAt(1, 2).asString());
        assertEquals("wrong value @(row=1,col=3)", "0.2", matrix.getValueAt(1, 3).asString());
        assertEquals("wrong value @(row=1,col=4)", "setosa", matrix.getValueAt(1, 4).asString());
    }

    public void testAssignString() {
        connection.assign("mystring", "mystring");
        final IREXP myString = connection.eval("mystring");
        assertNotNull(myString);
        assertEquals("wrong type", IREXPConstants.XT_STR, myString.getType());
        String fromR = myString.asString();
        assertEquals("mystring", fromR);
    }

    public void testAssignDoubleArray() {
        connection.assign("mydoubles", new double[]{1.0, 2.0, 3.0});
        final IREXP myDoubles = connection.eval("mydoubles");
        assertNotNull(myDoubles);
        assertEquals("wrong type", IREXPConstants.XT_ARRAY_DOUBLE, myDoubles.getType());
        double[] fromR = myDoubles.asDoubleArray();
        assertEquals("wrong size", 3, fromR.length);
        assertEquals("wrong #1", 1.0, fromR[0]);
        assertEquals("wrong #2", 2.0, fromR[1]);
        assertEquals("wrong #3", 3.0, fromR[2]);
    }

    public void testAssignIntegerArray() {
        connection.assign("myintegers", new int[]{1, 2, 3});
        final IREXP myIntegers = connection.eval("myintegers");
        assertNotNull(myIntegers);
        assertEquals("wrong type", IREXPConstants.XT_ARRAY_INT, myIntegers.getType());
        int[] fromR = myIntegers.asIntArray();
        assertEquals("wrong size", 3, fromR.length);
        assertEquals("wrong #1", 1, fromR[0]);
        assertEquals("wrong #2", 2, fromR[1]);
        assertEquals("wrong #3", 3, fromR[2]);
    }

    public void testAssignStringArray() {
        connection.assign("mystrings", new String[]{"1", "2", "3"});
        final IREXP myStrings = connection.eval("mystrings");
        assertNotNull(myStrings);
        assertEquals("wrong type", IREXPConstants.XT_ARRAY_STR, myStrings.getType());
        String[] fromR = myStrings.asStringArray();
        assertEquals("wrong size", 3, myStrings.length());
        assertEquals("wrong #1", "1", fromR[0]);
        assertEquals("wrong #2", "2", fromR[1]);
        assertEquals("wrong #3", "3", fromR[2]);
    }
}
