package org.glotaran.rawdataloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.FlimImageAbstract;

/**
 *
 * @author lsp
 */
public class RawASCIIImage implements TGDatasetInterface {

    @Override
    public String getExtention() {
        return "raw";
    }

    @Override
    public String getFilterString() {
        return ".raw LOB formated ASCII files";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        return "spec";
    }

    @Override
    public boolean Validator(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException {
        String loadedString;
        Scanner sc = new Scanner(file);
        if (sc.hasNextLine()) { //work around to avoid img files
        loadedString = sc.nextLine();
        loadedString = sc.nextLine();
        if (loadedString.toLowerCase().contains("version")){
            loadedString = sc.nextLine();
            if (loadedString.toLowerCase().contains("nzone")){
                loadedString = sc.nextLine();
                if (loadedString.toLowerCase().contains("full time zone")||(loadedString.toLowerCase().contains("full scale time"))){
                    return true;
                }
            }
        }
        }
        return false;
    }

    @Override
    public DatasetTimp loadFile(File file) throws FileNotFoundException{
        DatasetTimp dataset = new DatasetTimp();
        dataset.setDatasetName(file.getName());
        int nzone = 0;
        double[] timeZones;
        int[] timesonesSteps;
        double[] calibrationX;
        double[] calibrationY;
        int startPixel = 0;
        int pixelWidth = 0;
        int numberColibPoints = 0;
        
        double maxInt = 0;
        double minInt = 0;
        int nt = 0;
        double[] x;
        double[] x2;
        double[] psisim;
        String loadedString;
        Scanner sc = new Scanner(file);
        loadedString = sc.nextLine();
        loadedString = sc.nextLine();
        sc.skip(Pattern.compile(".*:", Pattern.CASE_INSENSITIVE));
        nzone = sc.nextInt();
        timeZones = new double[nzone];
        timesonesSteps = new int[nzone];

        for (int i =  0; i < nzone; i++){
            loadedString = sc.nextLine();
            sc.skip(Pattern.compile(".*:", Pattern.CASE_INSENSITIVE));
            timeZones[i] = Double.parseDouble(sc.next());
        }
        
        loadedString = sc.nextLine();
        sc.skip(Pattern.compile(".*:", Pattern.CASE_INSENSITIVE));
        startPixel = sc.nextInt();
        
        loadedString = sc.nextLine();
        sc.skip(Pattern.compile(".*:", Pattern.CASE_INSENSITIVE));
        pixelWidth = sc.nextInt();
        dataset.setNl(pixelWidth);
        
        loadedString = sc.nextLine();
        sc.skip(Pattern.compile(".*:", Pattern.CASE_INSENSITIVE));
        numberColibPoints = sc.nextInt();
        
        calibrationX = new double[numberColibPoints];
        calibrationY = new double[numberColibPoints];
        
        for (int i =  0; i < numberColibPoints; i++){
            loadedString = sc.nextLine();
            sc.skip(Pattern.compile(".*:", Pattern.CASE_INSENSITIVE));
            calibrationX[i] = Double.parseDouble(sc.next());
            loadedString = sc.nextLine();
            sc.skip(Pattern.compile(".*:", Pattern.CASE_INSENSITIVE));
            calibrationY[i] = Double.parseDouble(sc.next());
        }

        for (int i =  0; i < nzone; i++){
            loadedString = sc.nextLine();
            sc.skip(Pattern.compile(".*:", Pattern.CASE_INSENSITIVE));
            timesonesSteps[i] = sc.nextInt();
            nt += timesonesSteps[i];
        }
        dataset.setNt(nt);

//create vector x
        int k = 0;
        double step = 0;
        x = new double[nt];
        for (int i = 0; i < nzone; i++){
            if (i==0 ){
                step = timeZones[0]/timesonesSteps[0];
                if (k ==0) {
                x[0] = step;
                k++;            
            }
            for (int j = 1; j < timesonesSteps[i]; j++){
                x[k] = x[k-1] + step;
                k++;
            }
            } else {
                step = (timeZones[i]-timeZones[i-1])/timesonesSteps[i];
            for (int j = 0; j < timesonesSteps[i]; j++){
                x[k] = x[k-1] + step;
                k++;
            }
            }
             
//            step = (timeZones[i]-x[k-1])/timesonesSteps[i];
            
        }
        dataset.setX(x);
//create vector x2
        double slope = 0;
        double shift = 0;
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;

        for (int i = 0; i < numberColibPoints; i++){
            sumX += calibrationX[i];
            sumY += calibrationY[i];
            sumXY += calibrationX[i]*calibrationY[i];
            sumX2 += calibrationX[i]*calibrationX[i];
        }

        slope = (numberColibPoints*(sumXY) - sumX*sumY)/(numberColibPoints*sumX2 - sumX*sumX);
        shift = (sumY - slope*sumX)/numberColibPoints;
        x2 = new double[pixelWidth];
        if (slope >= 0){
            for (int i = 0; i < pixelWidth; i++){
                x2[i] = slope*(startPixel+i)+shift;
            }
        } else {
            for (int i = 0; i < pixelWidth; i++){
                x2[pixelWidth-i-1] = slope*(startPixel+i)+shift;
            }
        }

        dataset.setX2(x2);

        loadedString = sc.nextLine();
        loadedString = sc.nextLine();

//read data
        psisim = new double[nt*pixelWidth];

        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < pixelWidth; j++) {
                if (slope >= 0){
                    psisim[i + j * nt] = Double.parseDouble(sc.next());
                } else {
                    psisim[i + (pixelWidth- j -1) * nt] = Double.parseDouble(sc.next());
                }
                if (psisim[i + j * nt] > maxInt) {
                    maxInt = psisim[i + j * nt];
                }
                if (psisim[i + j * nt] < minInt) {
                    minInt = psisim[i + j * nt];
                }
            }
        }

        dataset.setPsisim(psisim);
        dataset.setMaxInt(maxInt);
        dataset.setMinInt(minInt);



        return dataset;
    }

    @Override
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
