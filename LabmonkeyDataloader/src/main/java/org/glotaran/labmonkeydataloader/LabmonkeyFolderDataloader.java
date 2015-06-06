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
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Glotaran
 */
@org.openide.util.lookup.ServiceProvider(service=TGDatasetInterface.class)
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
//        String loadedString;
//        Scanner sc = new Scanner(file);
//        //TODO: implement your own file type parser
//        try {
//            sc.nextLine(); //Read over header lines
//            sc.nextLine(); //Read over header lines
//            loadedString = sc.nextLine(); //Read file descripter
//            if (loadedString.contains("\t")) { //check for tabs instead of spaces
//                loadedString = loadedString.trim().replaceAll("\t", " ");
//            }
//            if (loadedString.trim().equalsIgnoreCase("Time explicit") | loadedString.trim().equalsIgnoreCase("Wavelength explicit")) {
//                return "spec";
//            } else {
//                //TODO: check for other file types, currently the only other supprted types are "flim" and "multispec"
//                return "error";
//            }
//        } catch (Exception e) {
//            //TODO: handle the exception nicely
//            return "error";
//        }
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
        // Program this to either load the average 
        // this example reads in a data file of type=spec in wavelength explicit format 
        // and stores it in the DatasetTimp class
        
        // call Datafolder routine to load files, and create datasets, then open first dataset

        DatasetTimp dataset = new DatasetTimp();

        dataset.setDatasetName(file.getName());

        double maxInt = 0;
        double minInt = 0;
        int nt = 0; //number of timepoints
        int nl = 0; //number of wavelengths
        double[] x; //labels for time
        double[] x2; //labels for wavelength
        double[] psisim;
        double[] intim;
        ArrayList<Double> x2Vector = new ArrayList<>();
        ArrayList<Double> psisimVector = new ArrayList<>();
        ArrayList<Double> intFluoVector = new ArrayList<>();
        ArrayList<Double> row = new ArrayList<>();

        String loadedString;
        Scanner sc = new Scanner(file);
        sc.nextLine();
        sc.nextLine();
        loadedString = sc.nextLine();
        if (loadedString.contains("\t")) { //check for tabs instead of spaces
            loadedString = loadedString.trim().replaceAll("\t", " ");
        }

        if (loadedString.trim().equalsIgnoreCase("Wavelength explicit")) {
            sc.skip(Pattern.compile(" *NumberOfRecordsPerDatapoint", 2));
            nl = sc.nextInt(); //read number of wavelengths
            dataset.setNl(nl); //set this number in dataset
            x2 = new double[nl]; //initialize the array with wavelengths
            for (int i = 0; i < nl; i++) { //read in the next nl numbers as wavelength labels
                x2[i] = Double.parseDouble(sc.next());
            }
            boolean invertedWaves = false;
            if (x2.length > 1) {
                invertedWaves = x2[0] >= x2[1];
                if (invertedWaves) {
                    double[] x2t = new double[nl];
                    for (int i = 0; i < nl; i++) {
                        x2t[nl - i - 1] = x2[i];
                    }
                    dataset.setX2(x2t);
                } else {
                    dataset.setX2(x2);
                }
            } else {
                dataset.setX2(x2);
            }
            while (sc.hasNext() && (!sc.hasNext(Pattern.compile("#*", Pattern.CASE_INSENSITIVE)))) {
                x2Vector.add(Double.parseDouble(sc.next()));
                for (int i = 0; i < nl; i++) {
                    psisimVector.add(Double.parseDouble(sc.next()));
                }
            }
            nt = x2Vector.size();
            dataset.setNt(nt); //set number of timepoints in dataset
            x = new double[nt];
            psisim = new double[nt * nl];
            for (int j = 0; j < nt; j++) {
                for (int i = 0; i < nl; i++) {
                    if (invertedWaves) {
                        psisim[(nl - i - 1) * nt + j] = psisimVector.get(j * nl + i);
                    } else {
                        psisim[i * nt + j] = psisimVector.get(j * nl + i);
                    }
                    if (psisim[i * nt + j] > maxInt) {
                        maxInt = psisim[i * nt + j];
                    }
                    if (psisim[i * nt + j] < minInt) {
                        minInt = psisim[i * nt + j];
                    }
                }
                x[j] = x2Vector.get(j);
            }
            dataset.setX(x);
            dataset.setPsisim(psisim);
            dataset.setMaxInt(maxInt);
            dataset.setMinInt(minInt);
            dataset.setType("spec");
        } 
    

    return dataset ;
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
        return name.substring(lastIndexOf+1);
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
            newname = (String)map.get("name");
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
