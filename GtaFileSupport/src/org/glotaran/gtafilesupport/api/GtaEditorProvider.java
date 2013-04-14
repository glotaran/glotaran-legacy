/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.gtafilesupport.api;

import org.glotaran.gtafilesupport.GtaDataObject;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author jsg210
 */
public interface GtaEditorProvider {

    public CloneableTopComponent getCloneableTopComponent(GtaDataObject entry);
}
