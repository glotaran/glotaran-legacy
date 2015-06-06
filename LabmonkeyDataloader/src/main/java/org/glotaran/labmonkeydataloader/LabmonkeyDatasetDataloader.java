package org.glotaran.labmonkeydataloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.glotaran.core.interfaces.LabmonkeyDataloaderInterface;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.FlimImageAbstract;

/**
 *
 * @author Glotaran
 */
@org.openide.util.lookup.ServiceProvider(service = LabmonkeyDataloaderInterface.class)
public class LabmonkeyDatasetDataloader implements LabmonkeyDataloaderInterface {

    @Override
    public String getExtention() { //sorry for the typo
        return "~~Datafolder~~";
    }

    @Override
    public String getFilterString() {
        return "~~LabmonkeyDataset~~";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        return "spec";
    }

    @Override
    public boolean Validator(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException {
        // Write your own elaborate validator to see if your LoadFile routine can deal with it
        String ext = getFileExtension(file);
        if (ext.equalsIgnoreCase(getExtention())) {
            return true;
        } else {
//            String loadedString;
//            Scanner sc = new Scanner(file);
//            if (sc.hasNextLine()) { //workaround for binary img file
//                sc.nextLine();
//                sc.nextLine();
//                loadedString = sc.nextLine();
//                if (loadedString.contains("\t")) { //check for tabs instead of spaces
//                    loadedString = loadedString.trim().replaceAll("\t", " ");
//                }
//                if (loadedString.trim().equalsIgnoreCase("Time explicit")
//                        | loadedString.trim().equalsIgnoreCase("Wavelength explicit")
//                        | loadedString.trim().equalsIgnoreCase("FLIM image")) {
//                    loadedString = sc.nextLine();
//                    if (loadedString.trim().contains("NumberOfRecordsPerDatapoint")) {
//                        return true;
//                    } else {
//                        return false;
//                    }
//                } else {
//                    return false;
//                }
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
            return false;
        }
    }

    @Override
    public DatasetTimp loadFile(File file) throws FileNotFoundException {

        Dataset labMonkeyDataset = new org.glotaran.labmonkeydataloader.Dataset(file.getPath());

        DatasetTimp datasetTimp = new DatasetTimp();
        datasetTimp.setNt(labMonkeyDataset.Timepoints.size()); //set number of timepoints in dataset  
        ArrayList<Double> unsortedTimpoints = new ArrayList<>(labMonkeyDataset.Timepoints);        
        Collections.sort(labMonkeyDataset.Timepoints);
        
        int[] indexes = new int[labMonkeyDataset.Timepoints.size()];
        for (int n = 0; n < labMonkeyDataset.Timepoints.size(); n++) {
            indexes[n] = unsortedTimpoints.indexOf(labMonkeyDataset.Timepoints.get(n));
        }
        
        double[] timepoints = new double[labMonkeyDataset.Timepoints.size()];
        for (int i = 0; i < timepoints.length; i++) {
            timepoints[i] = labMonkeyDataset.Timepoints.get(i);                // java 1.5+ style (outboxing)
        }
        datasetTimp.setX(timepoints);

        datasetTimp.setDatasetName(file.getName());
        datasetTimp.setNl(labMonkeyDataset.Wavelengths.size()); //set this number in dataset
        double[] wavelengths = new double[labMonkeyDataset.Wavelengths.size()];
        for (int i = 0; i < wavelengths.length; i++) {
            wavelengths[i] = labMonkeyDataset.Wavelengths.get(i);                // java 1.5+ style (outboxing)
        }
        datasetTimp.setX2(wavelengths);
       
        
        double[] psisim = new double[labMonkeyDataset.Timetrace.size() * labMonkeyDataset.Timetrace.get(0).length];
        for (int j = 0; j < labMonkeyDataset.Timetrace.get(0).length; j++) {
            for (int i = 0; i < labMonkeyDataset.Timetrace.size(); i++) {
                psisim[j * labMonkeyDataset.Timetrace.size() + i] = labMonkeyDataset.Timetrace.get(indexes[i])[j];
            }
        }

        datasetTimp.setPsisim(psisim);
        datasetTimp.calcRangeInt();
        datasetTimp.setType("spec");

        return datasetTimp;
    }

    @Override
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
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

//    @Override
//    public String getName(File file) {
//        String name = "labmonkeyDataFile" + System.currentTimeMillis();
//        String newname = "";        
//        final FileInputStream stream;
//        final Reader reader;
//        final BufferedReader bufferedReader;
//        try {
//            stream = new FileInputStream(file);
//            reader = new InputStreamReader(stream);
//            bufferedReader = new BufferedReader(reader);
//            final Object res = new Yaml().load(bufferedReader);        
//            Map map = (Map) res;
//            newname = (String)map.get("name");
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(LabmonkeyDatasetDataloader.class.getName()).log(Level.SEVERE, null, ex);
//        }                                        
//        return newname.isEmpty() ? name : newname;
//    }
//
//    @Override
//    public String[] getDatasetPaths(String rootDirectoryPath) {
//        return Datafolder.getDatasetPaths(rootDirectoryPath);
//    }
    @Override
    public String getName(File f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] getDatasetPaths(String rootDirectoryPath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
