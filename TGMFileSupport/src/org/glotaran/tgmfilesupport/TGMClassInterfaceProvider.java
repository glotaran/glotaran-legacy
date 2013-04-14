/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.tgmfilesupport;

import org.glotaran.core.interfaces.SupportedXMLFilesInterface;

/**
 *
 * @author slapten
 */
public class TGMClassInterfaceProvider implements SupportedXMLFilesInterface {

    public Object getDataObjectClass() {
        return TgmDataObject.class;
    }

    public String getType() {
        return "TgmDataObject";
    }
}
