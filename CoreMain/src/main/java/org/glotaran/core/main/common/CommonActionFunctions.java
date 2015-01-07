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
//import static java.lang.Math.ceil;
import static java.lang.Math.abs;

/**
 * Common functions to work with datasets
 *
 * @author Sergey
 */
public class CommonActionFunctions {

    /**
     * Average several datasets
     *
     * @param datasets list of the datasets (DatasetTimp) to be averaged
     * @return DatasetTimp - averaged dataset
     */
    public static DatasetTimp averageSpecDatasets(ArrayList<DatasetTimp> datasets) {
        DatasetTimp newDataset;
        boolean differentX;
        boolean differentX2;
        for (int i = 1; i < datasets.size(); i++) {
            differentX = Arrays.equals((datasets.get(0).getX()), (datasets.get(i).getX()));
            differentX2 = Arrays.equals((datasets.get(0).getX2()), (datasets.get(i).getX2()));
            if (!(differentX && differentX2)) {
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
        for (int j = 0; j < newDataset.getPsisim().length; j++) {
            for (int i = 1; i < datasets.size(); i++) {
                newDataset.getPsisim()[j] += datasets.get(i).getPsisim()[j];
            }
            newDataset.getPsisim()[j] /= datasets.size();
        }
        newDataset.calcRangeInt();
        newDataset.setType("spec");
        return newDataset;
    }

    /**
     * Resampling or averaging Dataset Resample just samples points (take for ex
     * 1 out of every 4 points for instance) thus reducing the total number of
     * points without affecting the resolution too much, Averaging sums them up
     * and divides by the total number of points
     *
     * @param dataset DatasetTimp: to be averaged
     * @param average boolean: true - average / false - resample
     * @param xWin average/resample for X dimension (waves)
     * @param yWin average/resample for Y dimension (time)
     * @return DatasetTimp : resulted dataset
     */
    public static DatasetTimp resampleDataset(DatasetTimp dataset, boolean average, int xWin, int yWin) {
        int newHeight = dataset.getNt();
        int newWidth = dataset.getNl();
        double[] temp;

        if (xWin != 0) {
// work with x dimension
//            newWidth = (int)ceil((double)dataset.getNl()/xWin);
//because  dataset.getNl() and  xWin - integers > 0 we cn use next code and use integers without casting to double othre wise previous lin eshould be used 
            newWidth = (dataset.getNl() + xWin - 1) / xWin;
            temp = new double[newWidth * newHeight];

            for (int i = 0; i < newHeight; i++) {
                for (int j = 0; j < newWidth - 1; j++) {
                    if (average) {
                        for (int k = 0; k < xWin; k++) {
                            temp[i + j * newHeight] += dataset.getPsisim()[i + (j * xWin + k) * newHeight];
                        }
                        temp[i + j * newHeight] /= xWin;
                    } else {
                        temp[i + j * newHeight] = dataset.getPsisim()[i + (j * xWin) * newHeight];
                    }
                }
                if (average) {
                    for (int k = (newWidth - 1) * xWin; k < dataset.getNl(); k++) {
                        temp[i + (newWidth - 1) * newHeight] += dataset.getPsisim()[i + k * newHeight];
                    }
                    temp[i + (newWidth - 1) * newHeight] /= (dataset.getNl() - (newWidth - 1) * xWin);
                } else {
                    temp[i + (newWidth - 1) * newHeight] = dataset.getPsisim()[i + (dataset.getNl() - 1) * newHeight];
                }
            }
            dataset.setPsisim(temp);
            temp = new double[newWidth];

//update x2 steps
            for (int j = 0; j < newWidth - 1; j++) {
                if (average) {
                    for (int k = 0; k < xWin; k++) {
                        temp[j] += dataset.getX2()[j * xWin + k];
                    }
                    temp[j] /= xWin;
                } else {
                    temp[j] = dataset.getX2()[j * xWin];
                }
            }
            if (average) {
                for (int k = (newWidth - 1) * xWin; k < dataset.getNl(); k++) {
                    temp[newWidth - 1] += dataset.getX2()[k];
                }
                temp[newWidth - 1] /= (dataset.getNl() - (newWidth - 1) * xWin);
            } else {
                temp[newWidth - 1] = dataset.getX2()[dataset.getNl() - 1];
            }
            dataset.setX2(temp);
            dataset.setNl(newWidth);
        }


        if (yWin != 0) {
// work with y dimension      
//            newHeight = (int)ceil((double)dataset.getNt()/yWin);
//because  dataset.getNt() and  yWin - integers > 0 we cn use next code and use integers without casting to double othre wise previous lin eshould be used 
            newHeight = (dataset.getNt() + yWin - 1) / yWin;
            temp = new double[newWidth * newHeight];

            for (int i = 0; i < newWidth; i++) {
                for (int j = 0; j < newHeight - 1; j++) {
                    if (average) {
                        for (int k = 0; k < yWin; k++) {
                            temp[i * newHeight + j] += dataset.getPsisim()[i * dataset.getNt() + j * yWin + k];
                        }
                        temp[i * newHeight + j] /= yWin;
                    } else {
                        temp[i * newHeight + j] = dataset.getPsisim()[i * dataset.getNt() + j * yWin];
                    }
                }
                if (average) {
                    for (int k = (newHeight - 1) * yWin; k < dataset.getNt(); k++) {
                        temp[(i + 1) * newHeight - 1] += dataset.getPsisim()[i * dataset.getNt() + k];
                    }
                    temp[(i + 1) * newHeight - 1] /= (dataset.getNt() - (newHeight - 1) * yWin);
                } else {
                    temp[(i + 1) * newHeight - 1] = dataset.getPsisim()[(i + 1) * dataset.getNt() - 1];
                }
            }
            dataset.setPsisim(temp);
            temp = new double[newHeight];

//update x steps
            for (int j = 0; j < newHeight - 1; j++) {
                if (average) {
                    for (int k = 0; k < yWin; k++) {
                        temp[j] += dataset.getX()[j * yWin + k];
                    }
                    temp[j] /= yWin;
                } else {
                    temp[j] = dataset.getX()[j * yWin];
                }
            }
            if (average) {
                for (int k = (newHeight - 1) * yWin; k < dataset.getNt(); k++) {
                    temp[newHeight - 1] += dataset.getX()[k];
                }
                temp[newHeight - 1] /= (dataset.getNt() - (newHeight - 1) * yWin);
            } else {
                temp[newHeight - 1] = dataset.getX()[dataset.getNt() - 1];
            }
            dataset.setX(temp);
            dataset.setNt(newHeight);
        }

        dataset.calcRangeInt();
        return dataset;
    }

    /**
     * Select part of the dataset
     *
     * @param dataset DatasetTimp source dataset
     * @param minX double start selecting from (in actual values) wavelengths
     * @param maxX double end selecting at (in actual values) wavelengths
     * @param minY double start selecting from (in actual values) time
     * @param maxY double end selecting at (in actual values) time
     * @return DatasetTimp sub-dataset
     */
    public static DatasetTimp selectInDataset(DatasetTimp dataset, double minX, double maxX, double minY, double maxY) {
        double[] temp;
        boolean reversedAxis = dataset.getX2()[0] > dataset.getX2()[1];
        if (!(minX == 0 && maxX == 0)) {
            int indMinX = findWaveIndex(dataset, (reversedAxis && maxX > minX) ? maxX : (!reversedAxis && (minX > maxX) ? maxX : minX), false);
            int indMaxX = findWaveIndex(dataset, (reversedAxis && maxX > minX) ? minX : (!reversedAxis && (minX > maxX) ? minX : maxX), true);
            if (indMinX != -1 && indMaxX != -1) {
                if (!(indMinX == 0 && indMaxX == (dataset.getX2().length - 1))) {
                    temp = new double[dataset.getNt() * (1 + indMaxX - indMinX)];
                    for (int i = 0; i < dataset.getNt(); i++) {
                        for (int j = 0; j < (1 + indMaxX - indMinX); j++) {
                            temp[j * dataset.getNt() + i] = dataset.getPsisim()[(j + indMinX) * dataset.getNt() + i];
                        }
                    }
                    dataset.setPsisim(temp);
                    temp = new double[1 + indMaxX - indMinX];
                    for (int i = 0; i < (1 + indMaxX - indMinX); i++) {
                        temp[i] = dataset.getX2()[i + indMinX];
                    }
                    dataset.setX2(temp);
                    dataset.setNl(1 + indMaxX - indMinX);
                    dataset.calcRangeInt();
                } //else nothing to do
            } // else no valid x range specified
            // TODO: relaunch dialog or issue warning message            
        }

        if (!(minY == 0 && maxY == 0)) {

            int indMinY = findTimeIndex(dataset, minY);
            int indMaxY = findTimeIndex(dataset, maxY);
            if (!(indMinY == 0 && indMaxY == (dataset.getX().length - 1))) {
                temp = new double[dataset.getNl() * (1 + indMaxY - indMinY)];
                for (int j = 0; j < dataset.getNl(); j++) {
                    for (int i = 0; i < (1 + indMaxY - indMinY); i++) {
                        temp[j * (1 + indMaxY - indMinY) + i] = dataset.getPsisim()[j * dataset.getNt() + i + indMinY];
                    }
                }
                dataset.setPsisim(temp);
                temp = new double[1 + indMaxY - indMinY];
                for (int i = 0; i < (1 + indMaxY - indMinY); i++) {
                    temp[i] = dataset.getX()[i + indMinY];
                }
                dataset.setX(temp);
                dataset.setNt(1 + indMaxY - indMinY);
                dataset.calcRangeInt();
            }
        }
        return dataset;
    }

    /**
     * BG Corrections. Corrections done on source dataset.
     *
     * @param dataset DatasetTimp : Source dataset;
     * @param params BaseLineCorrectionParameters : parameters for BG
     * corrections;
     * @return DatasetTimp
     */
    public static DatasetTimp baselineCorrection(DatasetTimp dataset, BaseLineCorrectionParameters params) {
        double[] bgSpec;
        double bgConstant = 0;
        if (params.isSpectralBG()) {
            bgSpec = new double[dataset.getNl()];
            for (int i = 0; i < params.getSelSpecNumber(); i++) {
                for (int j = 0; j < dataset.getNl(); j++) {
                    bgSpec[j] += dataset.getPsisim()[i + j * dataset.getNt()];
                }
            }
            for (int j = 0; j < dataset.getNl(); j++) {
                bgSpec[j] /= params.getSelSpecNumber();
            }

            for (int i = 0; i < dataset.getNt(); i++) {
                for (int j = 0; j < dataset.getNl(); j++) {
                    dataset.getPsisim()[i + j * dataset.getNt()] = dataset.getPsisim()[i + j * dataset.getNt()] - bgSpec[j];
                }
            }
        }
//subtract constant
        if ((params.isConstBG()) || (params.isRegionConstBG())) {
            if (params.isConstBG()) {
                bgConstant = params.getBgConst();
            } else {
                if (params.isRegionConstBG()) {
                    //calculate constant from data based on the filled numbers and put it to bgConstant
                    int dim1From, dim1To, dim2From, dim2To;
                    dim1From = CommonActionFunctions.findTimeIndex(dataset, params.getBgRegConstD1()[0]);
                    dim1To = CommonActionFunctions.findTimeIndex(dataset, params.getBgRegConstD1()[1]);
                    dim2From = CommonActionFunctions.findWaveIndex(dataset, params.getBgRegConstD2()[0], false);
                    dim2To = CommonActionFunctions.findWaveIndex(dataset, params.getBgRegConstD2()[1], true);
                    //TODO: check if specified region (partially) overlaps with dataset
                    double s = 0;
                    for (int i = dim1From; i < (1 + (dim1To - dim1From)); i++) {
                        for (int j = dim2From; j < (1 + (dim2To - dim2From)); j++) {
                            s += dataset.getPsisim()[i + j * dataset.getNt()];
                        }
                    }
                    bgConstant = s / ((1 + dim1To - dim1From) * (1 + dim2To - dim2From));
                }
            }
            //subtract  bgConstant from the dataset
            for (int i = 0; i < dataset.getNl() * dataset.getNt(); i++) {
                dataset.getPsisim()[i] -= bgConstant;
            }
        }
//subtract time trace 
        if (params.isTimetraceBG()) {
            int indFrom, indTo;
            indFrom = CommonActionFunctions.findWaveIndex(dataset, params.getTimeTrBg()[0], false);
            indTo = CommonActionFunctions.findWaveIndex(dataset, params.getTimeTrBg()[1], true);
            bgSpec = new double[dataset.getNt()];

            for (int i = 0; i < dataset.getNt(); i++) {
                for (int j = indFrom; j < indTo; j++) {
                    bgSpec[i] += dataset.getPsisim()[i + j * dataset.getNt()];
                }
            }
            for (int j = 0; j < dataset.getNt(); j++) {
                bgSpec[j] /= (indTo - indFrom);
            }

            for (int i = 0; i < dataset.getNt(); i++) {
                for (int j = 0; j < dataset.getNl(); j++) {
                    dataset.getPsisim()[i + j * dataset.getNt()] = dataset.getPsisim()[i + j * dataset.getNt()] - bgSpec[i];
                }
            }
        }
//todo implement Measured IRF 
//        if (params.isMeasuredBG()) {
//            
//        }




        return dataset;
    }

    /**
     * Correct for outliers. Corrections done on the source dataset
     * @param dataset dataset to work with
     * @param ocParameters parameters for outlier correction
     */
    public static void outliersCorrection(DatasetTimp dataset, OutlierCorrectionParameters ocParameters) {
        if (ocParameters.isIndividualOutlierC()) {
            int size = ocParameters.getWindowSize();
            int fence = ocParameters.getFence();

            int ywindnum = dataset.getNl() / size;
            int xwindnum = dataset.getNt() / size;

            ArrayList<Double> wind = new ArrayList<>();

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

            for (int y = (ywindnum - 1) * size; y < dataset.getNl(); y++) {
                for (int x = (xwindnum - 1) * size; x < dataset.getNt(); x++) {
                    if ((dataset.getPsisim()[y * dataset.getNt() + x] > hiage)
                            || (dataset.getPsisim()[y * dataset.getNt() + x] < lowage)) {

//                        mask[y * data.getNl() + x] = 1;

                        dataset.getPsisim()[y * dataset.getNt() + x] = med;
                        outliercount++;
                    }
                }
            }
            dataset.calcRangeInt();
            ocParameters.setNumberOfIndividualOutliersRemoved(outliercount);
        }
        if (ocParameters.isRegionValueC()) {

            //TODO: make this code abstract into multiple operations: substract, add, set, devide
            //calculate constant from data based on the filled numbers and put it to bgConstant
            int dim1From, dim1To, dim2From, dim2To;    
            dim1From = ocParameters.getOcRegConstD1() == null ? 0 : CommonActionFunctions.findTimeIndex(dataset, ocParameters.getOcRegConstD1()[0]);
            dim1To = ocParameters.getOcRegConstD1() == null ? dataset.getNt(): CommonActionFunctions.findTimeIndex(dataset, ocParameters.getOcRegConstD1()[1]);
            dim2From = ocParameters.getOcRegConstD2() == null ? 0 : CommonActionFunctions.findWaveIndex(dataset, ocParameters.getOcRegConstD2()[0], false);
            dim2To = ocParameters.getOcRegConstD2() == null ? dataset.getNl(): CommonActionFunctions.findWaveIndex(dataset, ocParameters.getOcRegConstD2()[1], true);
            //TODO: check if specified region (partially) overlaps with dataset
            double s = ocParameters.getOcConstValue();
            for (int i = 0; i < (dim1To-dim1From); i++) {
                for (int j = 0; j < (dim2To-dim2From); j++) {
                    if ((i+dim1From) *(j+dim2From) < dataset.getPsisim().length) {
                    dataset.getPsisim()[(i+dim1From) + (j+dim2From) * dataset.getNt()] = s;
                    }
                }
            }
            dataset.calcRangeInt();
        }
    }

    /**
     * Correct for total intensity, source dataset should have vector for
     * corrections, corrections done on source dataset.
     *
     * @param dataset DatasetTimp: source dataset
     * @return DatasetTimp : corrected dataset
     */
    public static DatasetTimp totalIntencityCorrection(DatasetTimp dataset) {
        for (int j = 0; j < dataset.getNl(); j++) {
            for (int i = 0; i < dataset.getNt(); i++) {
                dataset.getPsisim()[j * dataset.getNt() + i] = dataset.getPsisim()[j * dataset.getNt() + i] / (dataset.getIntenceIm()[i] / dataset.getIntenceIm()[0]);
            }
        }
        dataset.calcRangeInt();
        return dataset;
    }

    /**
     * Convert transmission data to absorption, corrections done on source
     * dataset.
     *
     * @param dataset DatasetTimp : source dataset
     * @param baseLineNum int : number of spectra in the beginning of the image
     * to use as baseline.
     * @return DatasetTimp
     */
    public static DatasetTimp convertToAbsorption(DatasetTimp dataset, int baseLineNum) {
        double[] zeroSpec;
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

    /**
     * Export dataset to disc.
     *
     * @param dataset DatasetTimp : dataset to be exported;
     * @param fileName String : destination file
     * @param format String : type of export TIMP, CSV (coma separated with
     * labels), Plain(tab separated with labels)
     */
    public static void exportSpecDatasets(DatasetTimp dataset, String fileName, String format) {
        BufferedWriter f = null;
        if (format.equals("TIMP")) {
            try {
                try {
                    f = new BufferedWriter(new FileWriter(fileName));
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


                    for (int i = 0; i < dataset.getNt(); i++) {
                        if (dataset.getType().equalsIgnoreCase("spec")) {
                            f.append(String.valueOf(dataset.getX()[i]));
                        } else {
                            if (dataset.getType().equalsIgnoreCase("flim")) {
                                int decimalPlace = 4;
                                BigDecimal bd = new BigDecimal(dataset.getX()[i]);
                                bd = bd.setScale(decimalPlace, BigDecimal.ROUND_UP);
                                f.append(String.valueOf(bd.doubleValue()));
                            }
                        }

                        f.append("\t");
                    }
                    f.newLine();
                    for (int i = 0; i < dataset.getNl(); i++) {
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
                } finally {
                    if (f != null) {
                        f.close();
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }

        if (format.equals("CSV")) {
            try {
                try {
                    f = new BufferedWriter(new FileWriter(fileName));
                    f.append("0.0");
                    f.append(",");
                    for (int i = 0; i < dataset.getNt(); i++) {
                        f.append(String.valueOf(dataset.getX()[i]));
                        f.append(",");
                    }
                    for (int i = 0; i < dataset.getNl(); i++) {
                        f.append(String.valueOf(dataset.getX2()[i]));
                        f.append(",");
                        for (int j = 0; j < dataset.getNt(); j++) {
                            f.append(String.valueOf(dataset.getPsisim()[i * dataset.getNt() + j]));
                            f.append(",");
                        }
                        f.newLine();
                    }
                    f.flush();
                } finally {
                    if (f != null) {
                        f.close();
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        if (format.equals("Plain")) {
            try {
                try {
                    f = new BufferedWriter(new FileWriter(fileName));
                    f.append("0.0");
                    f.append("\t");
                    for (int i = 0; i < dataset.getNt(); i++) {
                        f.append(String.valueOf(dataset.getX()[i]));
                        f.append("\t");
                    }
                    for (int i = 0; i < dataset.getNl(); i++) {
                        f.append(String.valueOf(dataset.getX2()[i]));
                        f.append("\t");
                        for (int j = 0; j < dataset.getNt(); j++) {
                            f.append(String.valueOf(dataset.getPsisim()[i * dataset.getNt() + j]));
                            f.append("\t");
                        }
                        f.newLine();
                    }
                    f.flush();

                } finally {
                    if (f != null) {
                        f.close();
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Export the IRF contained in a DatasetTimp object to disc.
     *
     * @param dataset DatasetTimp : dataset of which the IRF should be exported;
     * @param fileName String : destination file
     * @param format String : type of export TIMP, CSV (coma separated with
     * labels), Plain(tab separated with labels)
     */
    public static void exportIRFFromDataset(DatasetTimp dataset, String fileName, String format) {
        BufferedWriter f = null;
        if (format.equals("TIMP")) {
            try {
                try {
                    f = new BufferedWriter(new FileWriter(fileName));
                    f.append(fileName);
                    f.newLine();
                    f.append(dataset.getDatasetName() + "_IRF");
                    f.newLine();
                    if (dataset.getType().equalsIgnoreCase("spec")) {
                        f.append("Wavelength explicit");
                        f.newLine();
                        f.append("Intervalnr ");
                        f.append("  "); //Do not add a tab here!
                        f.append(String.valueOf(1));
                        f.newLine();
                    } else {
//                        if (dataset.getType().equalsIgnoreCase("flim")) {
//                            f.append("FLIM image");
//                            f.newLine();
//                            f.append(String.valueOf(dataset.getOriginalWidth()));
//                            f.append("  "); //Do not add a tab here!
//                            f.append(String.valueOf(dataset.getOriginalHeight()));
//                            f.newLine();
//                            f.append(String.valueOf(dataset.getNt()));
//                            f.newLine();
//                            f.append(String.valueOf(dataset.getNl()));
//                            f.newLine();
//                        }
                    }
                    f.append(String.valueOf(dataset.getMeasuredIRF().length));
                    f.newLine();

                    for (int i = 0; i < dataset.getMeasuredIRF().length; i++) {
                        f.append(String.valueOf(dataset.getMeasuredIRFDomainAxis()[i]));
                        f.append("\t");
                        f.append(String.valueOf(dataset.getMeasuredIRF()[i]));
                        f.newLine();
                    }

                    f.flush();
                } finally {
                    if (f != null) {
                        f.close();
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }

    }

    /**
     * Calculate median
     *
     * @param values ArrayList : source data
     * @return double : medial value
     */
    public static double median(ArrayList values) {
        Collections.sort(values);
        if (values.size() % 2 == 1) {
            return (Double) values.get((values.size() + 1) / 2 - 1);
        } else {
            double lower = (Double) values.get(values.size() / 2 - 1);
            double upper = (Double) values.get(values.size() / 2);
            return (lower + upper) / 2.0;
        }
    }

    /**
     * Calculate quantile
     *
     * @param values ArrayList : source data
     * @param probability double : probability for quantile calculation
     * @return double
     */
    public static double quantile(ArrayList values, double probability) {
        Collections.sort(values);
        double pos;
        pos = probability * (values.size() + 1);
        if (pos == floor(pos)) {
            return (Double) values.get((int) pos - 1);
        } else {
            return probability * ((Double) values.get((int) (floor(pos - 1))) + (Double) values.get((int) (floor(pos))));
        }
    }

    /**
     * Find index for wave Dataset;
     *
     * @param dataset DatasetTimp : source dataset
     * @param wave double : value
     * @param runReverse boolean : indicates whether the function should start
     * at last index rather than 0
     * @return int : index of wave, or -1 if wave is outside of the valid range
     */
    public static int findWaveIndex(DatasetTimp dataset, double wave, boolean runReverse) {
        boolean isReversedAxis = dataset.getX2()[0] > dataset.getX2()[1];
        int index = runReverse ? (dataset.getX2().length - 1) : 0;
        if ((runReverse ^ isReversedAxis) ? (dataset.getX2()[index] < wave) : (dataset.getX2()[index] > wave)) {
            return index;
        } else {
            while ((runReverse ^ isReversedAxis) ? (dataset.getX2()[index] > wave) : (dataset.getX2()[index] < wave)) {
                index = runReverse ? (index - 1) : (index + 1);
                if (index < 0 || index >= dataset.getX2().length) {
                    return -1;
                }
            }
            return index;
        }
    }

    /**
     * Find index for wave Dataset;
     *
     * @param dataset double array : source
     * @param value double : value
     * @param runReverse boolean : indicates whether the function should start
     * at last index rather than 0
     * @return int : index of wave, or -1 if wave is outside of the valid range
     */
    public static int findIndex(double[] dataset, double value, boolean runReverse) {
        boolean isReversedAxis = dataset[0] > dataset[1];
        int index = runReverse ? (dataset.length - 1) : 0;
        if ((runReverse ^ isReversedAxis) ? (dataset[index] < value) : (dataset[index] > value)) {
            return index;
        } else {
            while ((runReverse ^ isReversedAxis) ? (dataset[index] > value) : (dataset[index] < value)) {
                index = runReverse ? (index - 1) : (index + 1);
                if (index < 0 || index >= dataset.length) {
                    return -1;
                }
            }
            return index;
        }
    }

    /**
     * Find index for time step in Dataset;
     *
     * @param dataset DatasetTimp : source dataset
     * @param time double : value
     * @return int : index of time;
     */
    public static int findTimeIndex(DatasetTimp dataset, double time) {
        int index = 0;
        if (time < dataset.getX()[0]) {
            return 0;
        } else {
            while (time >= dataset.getX()[index]) {
                index++;
                if (index >= dataset.getX().length) {
                    return dataset.getX().length - 1;
                }
            }
            return index - 1;
        }
    }
}
