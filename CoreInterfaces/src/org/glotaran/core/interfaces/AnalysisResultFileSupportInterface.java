/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.interfaces;

import java.io.File;

/**
 *
 * @author jsg210
 */
public interface AnalysisResultFileSupportInterface {

    /**
     * Get the file type.
     * @param file
     * @return A {@code String} containing the type of the file for loaders.
     */
    public Object getContent(File file);
}
