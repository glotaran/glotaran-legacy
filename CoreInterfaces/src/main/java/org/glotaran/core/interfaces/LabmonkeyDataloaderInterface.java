/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.interfaces;

import java.io.File;

/**
 *
 * @author Joris Snellenburg
 */
public interface LabmonkeyDataloaderInterface extends TGDatasetInterface {

    public String getName(File f);
    
    public String[] getDatasetPaths(String rootDirectoryPath);
        
}
