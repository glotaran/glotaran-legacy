/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rosuda.constants;

/**
 *
 * @author Ralf
 */
public interface Const {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final String UTF8 = "UTF8";
    public static final String BASE_PATH = new java.io.File("").getAbsolutePath();
    public static final String RESOURCES = "resource";
    public static final String PROPERTIESPATH = RESOURCES + "/";
}
