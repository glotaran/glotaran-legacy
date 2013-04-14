/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.analysisoverviewfilesupport;

import org.glotaran.core.interfaces.SupportedXMLFilesInterface;

/**
 *
 * @author slapten
 */
public class AnalysisResultClassInterfaceProvider implements SupportedXMLFilesInterface {

    public Object getDataObjectClass() {
        return AnalysisResultDataObject.class;
    }

    public String getType() {
        return "AnalysisResultDataObject";
    }
}
