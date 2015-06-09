package org.glotaran.labmonkeydataloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.glotaran.core.interfaces.LabmonkeyDataloaderInterface;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.FlimImageAbstract;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Glotaran
 */
@org.openide.util.lookup.ServiceProvider(service = TGDatasetInterface.class)
public class LabmonkeyFolderDataloader implements LabmonkeyDataloaderInterface {

    @Override
    public String getExtention() { //sorry for the typo
        return "yaml";
    }

    @Override
    public String getFilterString() {
        return ".yaml (Labmonkey script)";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        // Check if the type of the file is supported (one extension may hold diferent types of data,
        // think time-gated spectra or time-traces recorded per wavelength)
        return "meta";
    }

    @Override
    public boolean Validator(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException {
        // Write your own elaborate validator to see if your LoadFile routine can deal with it
        String ext = getFileExtension(file);
        if (ext.equalsIgnoreCase(getExtention())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public DatasetTimp loadFile(File file) throws FileNotFoundException {
        // This method should not be called in this context.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        // This method should not be called in this context.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    @Override
    public String getName(File file) {
        String name = "labmonkeyDataFile" + System.currentTimeMillis();
        String newname = "";
        final FileInputStream stream;
        final Reader reader;
        final BufferedReader bufferedReader;
        try {
            stream = new FileInputStream(file);
            reader = new InputStreamReader(stream);
            bufferedReader = new BufferedReader(reader);
            final Object res = new Yaml().load(bufferedReader);
            Map map = (Map) res;
            newname = (String) map.get("name");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LabmonkeyFolderDataloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newname.isEmpty() ? name : newname;
    }

    @Override
    public String[] getDatasetPaths(String rootDirectoryPath) {
        return Datafolder.getDatasetPaths(rootDirectoryPath);
    }

}
