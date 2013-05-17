/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.common;

/**
 * Class to save parameters for BackGround corrections
 * @author sergey
 */
public class BaseLineCorrectionParameters {
    private boolean spectralBG = true;
    private boolean timetraceBG = false;
    private boolean constBG = false;
    private boolean regionConstBG = false;
    private boolean measuredBG = false;
    private int selSpecNumber = 1;
    private double bgConst = 0;
    private double[] timeTrBg = null; 
    private double[] bgRegConstD1 = null;
    private double[] bgRegConstD2 = null;
    private String fileName = null;

    /**
     * True if there is number of spectra to calculate BG 
     * @return boolean
     */
    public boolean isSpectralBG() {
        return spectralBG;
    }

    /**
     * Set true if there is parameters for corrections in spectral domain
     * @param spectralBG
     */
    public void setSpectralBG(boolean spectralBG) {
        this.spectralBG = spectralBG;
    }
    
    /**
     * Constant BG that need to be subtracted. Default = 0
     * @return double
     */
    public double getBgConst() {
        return bgConst;
    }

    /**
     * Set constant BG that need to be subtracted. Default = 0
     * @param bgConst - constant BG
     */
    public void setBgConst(double bgConst) {
        this.bgConst = bgConst;
    }

    /**
     * Interval (in actual values) to calculate BG in dimension 1 (time dimension) 
     * @return double[2] [valueFrom, valueTo], returns copy of values stored in the class
     */
    public double[] getBgRegConstD1() {
        return bgRegConstD1.clone();
    }

//    public void setBgRegConstD1(double[] bgRegConstD1) {
//        this.bgRegConstD1 = bgRegConstD1;
//    }
    
    /**
     * Set interval to calculate BG in dimension 1 (time dimension) in actual values 
     * @param ind - 0: From 1: To 
     * @param value double
     */
    public void setBgRegConstD1(int ind, double value) {
        if (bgRegConstD1==null){
            bgRegConstD1 = new double[2];
        }
        if (ind < 2){
            bgRegConstD1[ind] = value;
        }
    }

    /**
     * Interval (in actual values) to calculate BG in dimension 2 (waves dimension) 
     * @return double[2] [valueFrom, valueTo], returns copy of values stored in the class
     */
    public double[] getBgRegConstD2() {
        return bgRegConstD2.clone();
    }

//    public void setBgRegConstD2(double[] bgRegConstD2) {
//        this.bgRegConstD2 = bgRegConstD2;
//    }
    
    /**
     * Set interval to calculate BG in dimension 1 (time dimension) in actual values
     * @param ind - 0: From 1: To 
     * @param value double
     */
    public void setBgRegConstD2(int ind, double value) {
        if (bgRegConstD2==null){
            bgRegConstD2 = new double[2];
        }
        if (ind < 2){
            bgRegConstD2[ind] = value;
        }
    }

    /**
     * True if constant BG is specified
     * @return boolean
     */
    public boolean isConstBG() {
        return constBG;
    }

    /**
     * Set true if constant BG is specified
     * @param constBG 
     */
    public void setConstBG(boolean constBG) {
        this.constBG = constBG;
    }

    /**
     * Name of file with measured BG
     * @return filename String
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set full path and file name for file with measured BG
     * @param fileName full path
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * True if file with measured BG is specified
     * @return boolean
     */
    public boolean isMeasuredBG() {
        return measuredBG;
    }

    /**
     * Set true if file with measured BG is specified
     * @param measuredBG
     */
    public void setMeasuredBG(boolean measuredBG) {
        this.measuredBG = measuredBG;
    }

    /**
     * True if region for calculating BG was set (dim1 and dim2 parameters)
     * @return boolean
     */
    public boolean isRegionConstBG() {
        return regionConstBG;
    }

    /**
     * Set true if region for calculating BG was set (dim1 and dim2 parameters)
     * @param regionConstBG
     */
    public void setRegionConstBG(boolean regionConstBG) {
        this.regionConstBG = regionConstBG;
    }

    /**
     * Number of the spectra in the beginning of the image to use to calculate BG 
     * @return
     */
    public int getSelSpecNumber() {
        return selSpecNumber;
    }

    /**
     * Set number of the spectra in the beginning of the image to use to calculate BG 
     * @param selSpecNumber
     */
    public void setSelSpecNumber(int selSpecNumber) {
        this.selSpecNumber = selSpecNumber;
    }

    /**
     * Region to calculate time trace to be subtracted as BG
     * @return double[2] [waveFrom, WaveTo]
     */
    public double[] getTimeTrBg() {
        return timeTrBg.clone();
    }

    /**
     * Set region (waves from to) to calculate time trace to be subtracted as BG
     * @param ind - 0: From 1: To 
     * @param value double
     */
//    public void setTimeTrBg(double[] timeTrBg) {
//        this.timeTrBg = timeTrBg;
//    }
//    
    public void setTimeTrBg(int ind, double value) {
    if (timeTrBg==null){
            timeTrBg = new double[2];
        }
        if (ind < 2){
            timeTrBg[ind] = value;
        }
     }

    /**
     * True if time trace should be subtracted as BG 
     * @return boolean
     */
    public boolean isTimetraceBG() {
        return timetraceBG;
    }

    /**
     * Set true if time trace should be subtracted as BG 
     * @param timetraceBG
     */
    public void setTimetraceBG(boolean timetraceBG) {
        this.timetraceBG = timetraceBG;
    }
    
    
}
