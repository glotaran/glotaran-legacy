package org.glotaran.dataloader.avg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.FlimImageAbstract;

/**
 *
 * @author lsp
 */
public class AVGImage implements TGDatasetInterface {

    String type;
    String reHash = "(#)";	// Any Single Character 1
    String reWS = "(\\s+)";	// White Space 1
    String reDelay = "(Delay)";	// Word 1
    String reSC = "(:)";	// Any Single Character 2
    String reFloat = "([+-]?\\d*\\.\\d+)(?![-+0-9\\.])";	// Float 1
    Pattern avgTransSpecPattern1 = Pattern.compile(reHash + reWS + reDelay + reSC + reWS + reFloat, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    Pattern yValuesPattern1 = Pattern.compile(reFloat + reWS + reFloat + reWS + reFloat, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    Matcher m;

    @Override
    public String getExtention() {
        return "avg";
    }

    @Override
    public String getFilterString() {
        return ".avg Averaged transient spectra";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        if (type != null) {
            return "spec";
        } else {
            return "error";
        }
    }

    @Override
    public boolean Validator(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException {

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if ((m = avgTransSpecPattern1.matcher(strLine)).find()) {
                    type = "spec";
                    br.close();
                    return true;
                }
            }
            br.close();
            return false;
        } catch (IOException e) {
            System.err.println(e);
            return false;
        }
    }

    @Override
    public DatasetTimp loadFile(File file) throws FileNotFoundException {
        DatasetTimp dataset = new DatasetTimp();
        dataset.setType("spec");
        dataset.setDatasetName(file.getName());
        double maxInt = 0;
        double minInt = 0;
        int nt = 0;
        int nl = 0;
        double[] x;
        double[] x2;
        double[] psisim;
        double[] intim;
        String[] strArray;
        ArrayList<Double> x2Vector = new ArrayList<Double>();
        ArrayList<Double> psisimVector = new ArrayList<Double>();
        ArrayList<Double> psisimErrorVector = new ArrayList<Double>();

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String strLine;
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                if ((m = avgTransSpecPattern1.matcher(strLine)).find()) {
                    strArray = strLine.split(reWS);
                    nt = strArray.length - 2;
                    dataset.setNt(nt);
                    x = new double[nt];
                    for (int i = 2; i < strArray.length; i++) {
                        //Read in time point
                        x[i - 2] = Double.valueOf(strArray[i]);
                    }
                    dataset.setX(x);
                } else if ((m = yValuesPattern1.matcher(strLine)).find()) {
                    strArray = strLine.split(reWS);
                    for (int i = 0; i < 1+((strArray.length - 1) / 2); i++) {
                        if (i == 0) {
                            x2Vector.add(Double.valueOf(strArray[i]));
                        } else {
                            psisimVector.add(Double.valueOf(strArray[2*(i-1)+1]));
                            psisimErrorVector.add(Double.valueOf(strArray[2*(i-1)+2]));
                        }
                    }
                }
            }
            nl = x2Vector.size();
            dataset.setNl(nl);
            x2 = new double[nl];
            psisim = new double[nt * nl];
            maxInt = psisimVector.get(0);
            minInt = psisimVector.get(0);
            for (int j = 0; j < nl; j++) {
                for (int i = 0; i < nt; i++) {
                    psisim[j * nt + i] = psisimVector.get(j * nt + i);
                    if (psisim[j * nt + i] > maxInt) {
                        maxInt = psisim[j * nt + i];
                    }
                    if (psisim[j * nt + i] < minInt) {
                        minInt = psisim[j * nt + i];
                    }
                }
                x2[j] = x2Vector.get(j);
            }
            dataset.setX2(x2);
            dataset.setPsisim(psisim);
            dataset.setMaxInt(maxInt);
            dataset.setMinInt(minInt);
        } catch (IOException e) {
            System.err.println(e);

        }

        return dataset;
    }

    @Override
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
