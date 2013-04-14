/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.common;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import org.glotaran.tgmeditor.panels.KMatrixPanelForm;
import org.glotaran.tgmfilesupport.TgmDataObject;

/**
 *
 * @author slapten
 */
public class KmatrixPropertyEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        return "Kmatrix";
    }

    @Override
    public void setAsText(String s) {
        if (true) {
            System.out.print("test");
        }
    }

    @Override
    public Component getCustomEditor() {
        return new KMatrixPanelForm((TgmDataObject) getValue());
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
}
