package org.glotaran.core.interfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.FlimImageAbstract;

/**
 * this interfays should be implemente d by all possible filetypes
 */
public interface TGDatasetInterface {

    /**
     * get type - extention of the suported files.
     * @return A {String} containing the extention.
     */
    public String getExtention();    

    /**
     * @return true if str = sdt.
     */
    public String getFilterString();

    /**
     * Get the file type.
     * @param file
     * @return A {@code String} containing the type of the file for loaders.
     * @throws java.io.FileNotFoundException
     */
    public String getType(File file) throws FileNotFoundException;

    public boolean Validator(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException;

    public DatasetTimp loadFile(File file) throws FileNotFoundException;
    
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException;
}
