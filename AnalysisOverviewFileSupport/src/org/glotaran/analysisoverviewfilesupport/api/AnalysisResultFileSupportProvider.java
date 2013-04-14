/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.analysisoverviewfilesupport.api;

import java.io.File;
import org.glotaran.analysisoverviewfilesupport.AnalysisResultDataObject;
import org.glotaran.core.interfaces.AnalysisResultFileSupportInterface;
import org.glotaran.core.models.results.GtaResult;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author jsg210
 */
public class AnalysisResultFileSupportProvider implements AnalysisResultFileSupportInterface {

    public Object getContent(File file) {
        GtaResult result = null;
        FileObject fo = FileUtil.toFileObject(file);
        DataObject dataObj = null;
        try {
            dataObj = DataObject.find(fo);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (dataObj instanceof AnalysisResultDataObject) {
            result = ((AnalysisResultDataObject) dataObj).getAnalysisResult();
        }
        return result;
    }
}
