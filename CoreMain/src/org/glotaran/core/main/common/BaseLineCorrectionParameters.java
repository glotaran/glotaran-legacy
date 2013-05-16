/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.common;

/**
 *
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

    public boolean isSpectralBG() {
        return spectralBG;
    }

    public void setSpectralBG(boolean spectralBG) {
        this.spectralBG = spectralBG;
    }
    
    public double getBgConst() {
        return bgConst;
    }

    public void setBgConst(double bgConst) {
        this.bgConst = bgConst;
    }

    public double[] getBgRegConstD1() {
        return bgRegConstD1.clone();
    }

//    public void setBgRegConstD1(double[] bgRegConstD1) {
//        this.bgRegConstD1 = bgRegConstD1;
//    }
    
    public void setBgRegConstD1(int ind, double value) {
        if (bgRegConstD1==null){
            bgRegConstD1 = new double[2];
        }
        if (ind < 2){
            bgRegConstD1[ind] = value;
        }
    }

    public double[] getBgRegConstD2() {
        return bgRegConstD2.clone();
    }

//    public void setBgRegConstD2(double[] bgRegConstD2) {
//        this.bgRegConstD2 = bgRegConstD2;
//    }
    
    public void setBgRegConstD2(int ind, double value) {
        if (bgRegConstD2==null){
            bgRegConstD2 = new double[2];
        }
        if (ind < 2){
            bgRegConstD2[ind] = value;
        }
    }

    public boolean isConstBG() {
        return constBG;
    }

    public void setConstBG(boolean constBG) {
        this.constBG = constBG;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isMeasuredBG() {
        return measuredBG;
    }

    public void setMeasuredBG(boolean measuredBG) {
        this.measuredBG = measuredBG;
    }

    public boolean isRegionConstBG() {
        return regionConstBG;
    }

    public void setRegionConstBG(boolean regionConstBG) {
        this.regionConstBG = regionConstBG;
    }

    public int getSelSpecNumber() {
        return selSpecNumber;
    }

    public void setSelSpecNumber(int selSpecNumber) {
        this.selSpecNumber = selSpecNumber;
    }

    public double[] getTimeTrBg() {
        return timeTrBg;
    }

    public void setTimeTrBg(double[] timeTrBg) {
        this.timeTrBg = timeTrBg;
    }

    public boolean isTimetraceBG() {
        return timetraceBG;
    }

    public void setTimetraceBG(boolean timetraceBG) {
        this.timetraceBG = timetraceBG;
    }
    
    
}
