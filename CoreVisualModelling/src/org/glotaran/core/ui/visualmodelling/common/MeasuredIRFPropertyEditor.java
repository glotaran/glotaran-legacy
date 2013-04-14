
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.common;

import org.glotaran.tgmeditor.panels.MeasuredIrfparPanel;
import org.glotaran.tgmfilesupport.TgmDataObject;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author slapten
 */
public class MeasuredIRFPropertyEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        return "MeasuredIRF";
    }

    @Override
    public void setAsText(String s) {
        if (true) {
            System.out.print("test");
        }
    }

    @Override
    public Component getCustomEditor() {
        return new MeasuredIrfparPanel((TgmDataObject) getValue());
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
}
