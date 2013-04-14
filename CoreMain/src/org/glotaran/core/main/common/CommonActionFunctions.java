/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.glotaran.core.main.common;

import java.math.BigDecimal;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.glotaran.core.models.structures.DatasetTimp;
import org.openide.util.Exceptions;
import static java.lang.Math.floor;
import static java.lang.Math.ceil;
import static java.lang.Math.abs;

/**
 *
 * @author lsp
 */
public class CommonActionFunctions {

    public static DatasetTimp averageSpecDatasets(ArrayList<DatasetTimp> datasets){
        DatasetTimp newDataset = null;
        boolean differentX = false;
        boolean differentX2 = false;
        for (int i = 1; i < datasets.size(); i++){
            differentX = Arrays.equals((datasets.get(0).getX()),(datasets.get(i).getX()));
            differentX2 = Arrays.equals((datasets.get(0).getX2()),(datasets.get(i).getX2()));
            if (!(differentX&&differentX2)){
                return null;
            }
        }
        newDataset = new DatasetTimp();
        newDataset.setDatasetName("AveragedDataset");
        newDataset.setNl(datasets.get(0).getNl());
        newDataset.setNt(datasets.get(0).getNt());
        newDataset.setType(datasets.get(0).getType());
        newDataset.setX(datasets.get(0).getX().clone());
        newDataset.setX2(datasets.get(0).getX2().clone());
        newDataset.setPsisim(datasets.get(0).getPsisim().clone());
        for (int j = 0; j < newDataset.getPsisim().length; j++){
            for (int i = 1; i < datasets.size(); i++){
                newDataset.getPsisim()[j]+=datasets.get(i).getPsisim()[j];
            }
            newDataset.getPsisim()[j]/=datasets.size();
        }
        newDataset.calcRangeInt();
        newDataset.setType("spec");
        return newDataset;
    }
    
    public static DatasetTimp resampleDataset(DatasetTimp dataset, boolean average, int xWin, int yWin){
        int newHeight = dataset.getNt();
        int newWidth = dataset.getNl();
        double[] temp = null;

        if (xWin!=0){
// work with x dimension
            newWidth = (int)ceil(dataset.getNl()/xWin);
            temp = new double[newWidth * newHeight];

            for (int i = 0; i < newHeight; i++) {
                for (int j = 0; j < newWidth-1; j++) {
                    if (average){
                        for (int k = 0; k < xWin; k++){
                            temp[i + j * newHeight]+= dataset.getPsisim()[i + (j*xWin+k) * newHeight];
                        }
                        temp[i + j * newHeight]/=xWin;
                    } else {
                        temp[i + j * newHeight] = dataset.getPsisim()[i + (j * xWin) * newHeight];
                    }
                }
                if (average){
                    for (int k = (newWidth-1)*xWin; k < dataset.getNl(); k++){
                        temp[i + (newWidth-1) * newHeight]+= dataset.getPsisim()[i + k * newHeight];
                    }
                        temp[i + (newWidth-1) * newHeight]/=(dataset.getNl()-(newWidth-1)*xWin);
                } else {
                        temp[i + (newWidth-1) * newHeight] = dataset.getPsisim()[i + (dataset.getNl()-1) * newHeight];
                }
            }
            dataset.setPsisim(temp);
            temp = new double[newWidth];

//update x2 steps
            for (int j = 0; j < newWidth-1; j++) {
                if (average){
                    for (int k = 0; k < xWin; k++){
                        temp[j]+= dataset.getX2()[j * xWin + k];
                    }
                    temp[j]/= xWin;
                } else {
                    temp[j] = dataset.getX2()[j * xWin];    
                }
            }
            if (average){
                for (int k = (newWidth-1)*xWin; k < dataset.getNl(); k++ ){
                    temp[newWidth-1] +=dataset.getX2()[k];
                }
                temp[newWidth-1]/=(dataset.getNl()-(newWidth-1)*xWin);
            } else {
                temp[newWidth-1] = dataset.getX2()[dataset.getNl()-1];
            }
            dataset.setX2(temp);
            dataset.setNl(newWidth);
        }


        if (yWin!=0){
// work with y dimension
            newHeight = (int)ceil(dataset.getNt()/yWin);
            temp = new double[newWidth * newHeight];

            for (int i = 0; i < newWidth; i++) {
                for (int j = 0; j < newHeight-1; j++) {
                    if (average){
                        for (int k = 0; k < yWin; k++){
                            temp[i*newHeight + j]+= dataset.getPsisim()[i * dataset.getNt() + j*yWin + k];
                        }
                        temp[i*newHeight + j]/=yWin;
                    } else {
                        temp[i*newHeight + j] = dataset.getPsisim()[i * dataset.getNt() + j*yWin];
                    }
                }
                if (average){
                    for (int k = (newHeight-1)*yWin; k < dataset.getNt(); k++){
                        temp[(i+1)*newHeight-1]+= dataset.getPsisim()[i*dataset.getNt() + k];
                    }
                        temp[(i+1)*newHeight-1]/=(dataset.getNt()-(newHeight-1)*yWin);
                } else {
                        temp[(i+1)*newHeight-1] = dataset.getPsisim()[(i+1)*dataset.getNt() -1];
                }
            }
            dataset.setPsisim(temp);
            temp = new double[newHeight];

//update x steps
            for (int j = 0; j < newHeight-1; j++) {
                if (average){
                    for (int k = 0; k < yWin; k++){
                        temp[j]+= dataset.getX()[j * yWin + k];
                    }
                    temp[j]/= yWin;
                } else {
                    temp[j] = dataset.getX()[j * yWin];
                }
            }
            if (average){
                for (int k = (newHeight-1)*yWin; k < dataset.getNt(); k++ ){
                    temp[newHeight-1] +=dataset.getX()[k];
                }
                temp[newHeight-1]/=(dataset.getNt()-(newHeight-1)*yWin);
            } else {
                temp[newHeight-1] = dataset.getX()[dataset.getNt()-1];
            }
            dataset.setX(temp);
            dataset.setNt(newHeight);
        }
        
        dataset.calcRangeInt();
        return dataset;
    }

    public static DatasetTimp selectInDataset(DatasetTimp dataset, double minX, double maxX, double minY, double maxY){
        double[] temp = null;
        if (minX!=0&&maxX!=0){
            int indMixX=findWaveIndex(dataset, minX);
            int indMaxX=findWaveIndex(dataset, maxX);
            temp = new double[dataset.getNt()*(indMaxX-indMixX)] ;
            for(int i = 0; i <dataset.getNt(); i++){
                for(int j = 0; j < (indMaxX-indMixX); j++){
                    temp[j*dataset.getNt()+i] = dataset.getPsisim()[(j+indMixX)*dataset.getNt()+i];
                }
            }
            dataset.setPsisim(temp);
            temp = new double[indMaxX-indMixX];
            for (int i = 0; i<indMaxX-indMixX ; i++){
                temp[i] = dataset.getX2()[i+indMixX];
            }
            dataset.setX2(temp);
            dataset.setNl(indMaxX-indMixX);
        }
        
        if (minY!=0&&maxY!=0){
            int indMixY=findTimeIndex(dataset, minY);
            int indMaxY=findTimeIndex(dataset, maxY);
            temp = new double[dataset.getNl()*(indMaxY-indMixY)];
            for (int j = 0; j < dataset.getNl(); j++){
                for (int i = 0; i<(indMaxY-indMixY); i++){
                    temp[j*(indMaxY-indMixY)+i] = dataset.getPsisim()[j*dataset.getNt()+i+indMixY];
                }
            }
            dataset.setPsisim(temp);
            temp = new double[indMaxY-indMixY];
            for (int i = 0; i<indMaxY-indMixY ; i++){
                temp[i] = dataset.getX()[i+indMixY];
            }
            dataset.setX(temp);
            dataset.setNt(indMaxY-indMixY);
        }
        dataset.calcRangeInt();
        return dataset;
    }

    public static DatasetTimp baselineCorrection(DatasetTimp dataset){
        

        return null;
    }

    public static int outliersCorrection(DatasetTimp dataset, int size, double fence) {
        int ywindnum = dataset.getNl() / size;
        int xwindnum = dataset.getNt() / size;

        ArrayList<Double> wind = new ArrayList<Double>();

        int outliercount = 0;
        double med;
        double lowage, hiage;
        double interv;
// mask containes all positions where outliers detected
//            short[] mask = new short[data.getNl() * data.getNt()];
//first region - compleate windows
        for (int n = 0; n < ywindnum - 1; n++) {
            for (int m = 0; m < xwindnum - 1; m++) {
                wind.clear();
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        wind.add(dataset.getPsisim()[(n * size + x) * dataset.getNt() + m * size + y]);
                    }
                }
                med = median(wind);
                interv = abs(quantile(wind, 0.75) - quantile(wind, 0.25));

                if (interv == 0) {
                    interv = 1;
                }
                lowage = med - fence * interv;
                hiage = med + fence * interv;

                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        if ((dataset.getPsisim()[(n * size + x) * dataset.getNt() + m * size + y] > hiage)
                                || ((dataset.getPsisim()[(n * size + x) * dataset.getNt() + m * size + y] < lowage))) {
//                                mask[(n * size + x) * data.getNl() + m * size + y] = 1;
                            dataset.getPsisim()[(n * size + x) * dataset.getNt() + m * size + y] = med;
                            outliercount++;
                        }
                    }
                }
            }
//second region windows with smaller width - left side of the image (if present)
            //               wind = new ArrayList<Double>((data.getNl() - (xwindnum - 1) * size) * size);
            wind.clear();
            for (int y = 0; y < size; y++) {
                for (int x = (xwindnum - 1) * size; x < dataset.getNt(); x++) {
                    wind.add(dataset.getPsisim()[(n * size + y) * dataset.getNt() + x]);
                }
            }

            med = median(wind);
            interv = abs(quantile(wind, 0.75) - quantile(wind, 0.25));
            if (interv == 0) {
                interv = 1;
            }
            lowage = med - fence * interv;
            hiage = med + fence * interv;
            for (int y = 0; y < size; y++) {
                for (int x = (xwindnum - 1) * size; x < dataset.getNt(); x++) {
                    if ((dataset.getPsisim()[(n * size + y) * dataset.getNt() + x] > hiage)
                            || (dataset.getPsisim()[(n * size + y) * dataset.getNt() + x] < lowage)) {

//                            mask[(n * size + y) * data.getNl() + x] = 1;
                        dataset.getPsisim()[(n * size + y) * dataset.getNt() + x] = med;
                        outliercount++;
                    }
                }
            }
        }
//third region windows with smaller height - bottom part of the image (if present)
//            wind = new ArrayList<Double>((data.getNt() - (ywindnum - 1) * size) * size);
        for (int m = 0; m < xwindnum - 1; m++) {
            wind.clear();
            for (int y = (ywindnum - 1) * size; y < dataset.getNl(); y++) {
                for (int x = 0; x < size; x++) {
                    wind.add(dataset.getPsisim()[y * dataset.getNt() + m * size + x]);
                }
            }
            med = median(wind);
            interv = abs(quantile(wind, 0.75) - quantile(wind, 0.25));
            if (interv == 0) {
                interv = 1;
            }

            lowage = med - fence * interv;
            hiage = med + fence * interv;

            for (int y = (ywindnum - 1) * size; y < dataset.getNl(); y++) {
                for (int x = 0; x < size; x++) {
                    if ((dataset.getPsisim()[y * dataset.getNt() + m * size + x] > hiage)
                            || (dataset.getPsisim()[y * dataset.getNt() + m * size + x] < lowage)) {
//                            mask[y * data.getNl() + m * size + x] = 1;
                        dataset.getPsisim()[y * dataset.getNt() + m * size + x] = med;
                        outliercount++;
                    }
                }
            }
        }
//forth region windows with smaller width and height - left sfottom angle of the image (if present)
        wind.clear();
        for (int y = (ywindnum - 1) * size; y < dataset.getNl(); y++) {
            for (int x = (xwindnum - 1) * size; x < dataset.getNt(); x++) {
                wind.add(dataset.getPsisim()[y * dataset.getNt() + x]);
            }
        }
        med = median(wind);
        interv = abs(quantile(wind, 0.75) - quantile(wind, 0.25));

        if (interv == 0) {
            interv = 1;
        }
        lowage = med - fence * interv;
        hiage = med + fence * interv;

        for (int y = (ywindnum - 1) * size; y < dataset.getNt(); y++) {
            for (int x = (xwindnum - 1) * size; x < dataset.getNl(); x++) {
                if ((dataset.getPsisim()[y * dataset.getNt() + x] > hiage)
                        || (dataset.getPsisim()[y * dataset.getNt() + x] < lowage)) {

//                        mask[y * data.getNl() + x] = 1;

                    dataset.getPsisim()[y * dataset.getNt() + x] = med;
                    outliercount++;
                }
            }
        }
        dataset.calcRangeInt();

        return outliercount;
    }

    public static DatasetTimp totalIntencityCorrection(DatasetTimp dataset){
        for (int j = 0; j < dataset.getNl(); j++) {
            for (int i = 0; i < dataset.getNt(); i++) {
                dataset.getPsisim()[j * dataset.getNt() + i] = dataset.getPsisim()[j * dataset.getNt() + i] / (dataset.getIntenceIm()[i]/dataset.getIntenceIm()[0]);
            }
        }
        dataset.calcRangeInt();
        return dataset;
    }

    public static DatasetTimp convertToAbsorption(DatasetTimp dataset, int baseLineNum) {
        double[] zeroSpec = null;
        zeroSpec = new double[dataset.getNl()];
        for (int j = 0; j < dataset.getNl(); j++) {
            zeroSpec[j] = dataset.getPsisim()[j * dataset.getNt()];
        }
        for (int i = 1; i < baseLineNum; i++) {
            for (int j = 0; j < dataset.getNl(); j++) {
                zeroSpec[j] += dataset.getPsisim()[i + j * dataset.getNt()];
            }
        }
        for (int j = 0; j < dataset.getNl(); j++) {
            zeroSpec[j] /= baseLineNum;
        }

        for (int i = 0; i < dataset.getNt(); i++) {
            for (int j = 0; j < dataset.getNl(); j++) {
                dataset.getPsisim()[i + j * dataset.getNt()] = -Math.log10(dataset.getPsisim()[i + j * dataset.getNt()] / zeroSpec[j]);
            }
        }
        dataset.calcRangeInt();
        return dataset;
    }

    public static void exportSpecDatasets(DatasetTimp dataset, String fileName, String format){
        if(format.equals("TIMP")) {

            try {
                BufferedWriter f = new BufferedWriter(new FileWriter(fileName));
                f.append(fileName);
                f.newLine();
                f.append(dataset.getDatasetName());
                f.newLine();
                if (dataset.getType().equalsIgnoreCase("spec")) {
                    f.append("Time explicit");
                    f.newLine();
                    f.append("Intervalnr ");
                    f.append("  "); //Do not add a tab here!
                    f.append(String.valueOf(dataset.getNt()));
                    f.newLine();
                } else {
                    if (dataset.getType().equalsIgnoreCase("flim")) {
                        f.append("FLIM image");
                        f.newLine();
                        f.append(String.valueOf(dataset.getOriginalWidth()));
                        f.append("  "); //Do not add a tab here!
                        f.append(String.valueOf(dataset.getOriginalHeight()));
                        f.newLine();
                        f.append(String.valueOf(dataset.getNt()));
                        f.newLine();
                        f.append(String.valueOf(dataset.getNl()));
                        f.newLine();
                    }
                }

                
                for (int i = 0; i< dataset.getNt(); i++){
                    if (dataset.getType().equalsIgnoreCase("spec")) {
                        f.append(String.valueOf(dataset.getX()[i]));
                    }
                    else {
                       if (dataset.getType().equalsIgnoreCase("flim")) {
                             int decimalPlace = 4;
                             BigDecimal bd = new BigDecimal(dataset.getX()[i]);
                             bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
                             f.append(String.valueOf(bd.doubleValue()));
                       } 
                    }

                    f.append("\t");
                }
                f.newLine();
                for (int i=0; i < dataset.getNl(); i++){
                    f.append(String.valueOf(dataset.getX2()[i]));
                    f.append("\t");
                    for (int j = 0; j < dataset.getNt(); j++) {
                        f.append(String.valueOf(dataset.getPsisim()[i * dataset.getNt() + j]));
                        f.append("\t");
                    }
                    f.newLine();
                }
                if (dataset.getType().equalsIgnoreCase("flim")) {
                    f.append("Intensity image");
                    f.newLine();
                    for (int i = 0; i < dataset.getIntenceIm().length; i++) {
                        f.append(String.valueOf(dataset.getIntenceIm()[i]));
                        f.append("\t");
                    }
                    f.newLine();
                }
                f.flush();
                f.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }

        if(format.equals("CSV")) {
                    try {
                BufferedWriter f = new BufferedWriter(new FileWriter(fileName));
                f.append("0.0");
                f.append(",");
                for (int i = 0; i< dataset.getNt(); i++){
                    f.append(String.valueOf(dataset.getX()[i]));
                    f.append(",");
                }
                for (int i=0; i < dataset.getNl(); i++){
                    f.append(String.valueOf(dataset.getX2()[i]));
                    f.append(",");
                    for (int j = 0; j < dataset.getNt(); j++) {
                        f.append(String.valueOf(dataset.getPsisim()[i*dataset.getNt()+j]));
                        f.append(",");
                    }
                    f.newLine();
                }
                f.flush();
                f.close();

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        if(format.equals("Plain")) {
            try {
                BufferedWriter f = new BufferedWriter(new FileWriter(fileName));
                f.append("0.0");    
                f.append("\t");
                for (int i = 0; i< dataset.getNt(); i++){
                    f.append(String.valueOf(dataset.getX()[i]));
                    f.append("\t");
                }
                for (int i=0; i < dataset.getNl(); i++){
                    f.append(String.valueOf(dataset.getX2()[i]));
                    f.append("\t");
                    for (int j = 0; j < dataset.getNt(); j++) {
                        f.append(String.valueOf(dataset.getPsisim()[i*dataset.getNt()+j]));
                        f.append("\t");
                    }
                    f.newLine();
                }
                f.flush();
                f.close();

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

        public static double median(ArrayList values){
        Collections.sort(values);
        if (values.size() % 2 == 1) {
            return (Double)values.get((values.size() + 1) / 2 - 1);
        } else {
            double lower = (Double)values.get(values.size() / 2 - 1);
            double upper = (Double)values.get(values.size() / 2);
            return (lower + upper) / 2.0;
        }
    }

    public static double quantile(ArrayList values, double probability){
        Collections.sort(values);
        double pos;
        pos = probability * (values.size() + 1);
        if (pos == floor(pos)){
            return (Double)values.get((int)pos - 1);
        }
        else{
            return probability*((Double)values.get((int)(floor(pos - 1))) + (Double)values.get((int)(floor(pos))));
        }
    }

    public static int findWaveIndex(DatasetTimp dataset, double wave) {
        int index = 0;
        if (dataset.getX2()[0] < dataset.getX2()[1]) {
            //wavelengths
            if (wave < dataset.getX2()[0]) {
                return 0;
            } else {
                while (wave > dataset.getX2()[index]) {
                    index++;
                    if(index >= dataset.getX2().length) {
                        return dataset.getX2().length-1;
                    }
                }
                return index-1;
            }
        } else {
            //wavenambers
            if (wave > dataset.getX2()[0]) {
                return 0;
            } else {
                while (wave < dataset.getX2()[index]) {
                    index++;
                    if(index >= dataset.getX2().length) {
                        return dataset.getX2().length-1;
                    }
                }
                return index-1;
            }
        }
    }

    public static int findTimeIndex(DatasetTimp dataset, double time) {
        int index = 0;
        if (time < dataset.getX()[0]) {
            return 0;
        } else {
            while (time > dataset.getX()[index]) {
                index++;
                if (index >= dataset.getX().length) {
                    return dataset.getX().length - 1;
                }
            }
            return index-1;
        }
    }
}
