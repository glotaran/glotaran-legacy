/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.example.dataloader;

import java.io.File;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.FlimImageAbstract;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jsg210
 */
public class ExampleDataloaderTest {
    File testFile;
    
    public ExampleDataloaderTest() {
        testFile = new File(getClass().getResource("dataset.example").getFile());
    }
    
    @BeforeClass
    public static void setUpClass() {	
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getExtention method, of class ExampleDataloader.
     */
    @Test
    public void testGetExtention() {
        System.out.println("getExtention");
        ExampleDataloader instance = new ExampleDataloader();
        String expResult = "example";
        String result = instance.getExtention();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getFilterString method, of class ExampleDataloader.
     */
    @Test
    public void testGetFilterString() {
        System.out.println("getFilterString");
        ExampleDataloader instance = new ExampleDataloader();
        String expResult = ".example (Example datafile)";
        String result = instance.getFilterString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getType method, of class ExampleDataloader.
     */
    @Test
    public void testGetType() throws Exception {
        System.out.println("getType");
        ExampleDataloader instance = new ExampleDataloader();
        String expResult = "spec";
        String result = instance.getType(testFile);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of Validator method, of class ExampleDataloader.
     */
    @Test
    public void testValidator() throws Exception {
        System.out.println("Validator");
        ExampleDataloader instance = new ExampleDataloader();
        boolean expResult = true;
        boolean result = instance.Validator(testFile);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of loadFile method, of class ExampleDataloader.
     */
    @Test
    public void testLoadFile() throws Exception {
        System.out.println("loadFile");
        ExampleDataloader instance = new ExampleDataloader();
        double[] psisim = {310,88.8164870267,25.4463495734,7.2905012154,2.0887635697,360,103.1417268697,29.5505995046,8.4663885082,2.4256609197,410,117.4669667127,33.6548494358,9.642275801,2.7625582696};

        // expResult.setStuff
        DatasetTimp result = instance.loadFile(testFile);        
        assertEquals(3, result.getNl());
        assertEquals(5, result.getNt());
        assertArrayEquals(psisim, result.getPsisim(),0.0);
        // TODO review the generated test code and remove the default call to fail.        
    }

    /**
     * Test of loadFlimFile method, of class ExampleDataloader.
     */
    @Test
    public void testLoadFlimFile() throws Exception {
        System.out.println("loadFlimFile: not tested anymore");        
        assertTrue(true);
    }
    
}
