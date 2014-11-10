/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.project;

import org.netbeans.spi.project.ui.PrivilegedTemplates;

/**
 *
 * @author joris
 */
public class TGFileTypes implements PrivilegedTemplates {

    @Override
    public String[] getPrivilegedTemplates() {
        String[] trialTypes = {"Templates/Glotaran/TgmTemplate.xml"
        };

        return trialTypes;
    }
}
