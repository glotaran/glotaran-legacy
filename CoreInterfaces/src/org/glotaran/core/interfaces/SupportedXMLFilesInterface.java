/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.interfaces;

/**
 *
 * @author slapten
 */
public interface SupportedXMLFilesInterface {

//return dataobject for a given node
    public Object getDataObjectClass();

//return a type of the file for a given node
    public String getType();
}
