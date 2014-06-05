/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.common;

/**
 * Class to save parameters for Outlier corrections
 *
 * @author Joris Snellenburg
 */
public class OutlierCorrectionParameters {
    private boolean individualOutlierC = true;
    private boolean regionValueC = false;
    private boolean notANumberValueC = false;
    private int windowSize = 4;
    private int fence = 4;
    private int numberOfIndividualOutliersRemoved = 0;
    private double[] ocRegConstD1 = null;
    private double[] ocRegConstD2 = null;
    private double ocConstValue = 0;

    public int getNumberOfIndividualOutliersRemoved() {
        return numberOfIndividualOutliersRemoved;
    }

    public void setNumberOfIndividualOutliersRemoved(int numberOfIndividualOutliersRemoved) {
        this.numberOfIndividualOutliersRemoved = numberOfIndividualOutliersRemoved;
    }

    public double getOcConstValue() {
        return ocConstValue;
    }

    public void setOcConstValue(double ocConstValue) {
        this.ocConstValue = ocConstValue;
    }

    /**
     * Interval (in actual values) to calculate BG in dimension 1 (time dimension) 
     * @return double[2] [valueFrom, valueTo], returns copy of values stored in the class
     */
    public double[] getOcRegConstD1() {
        return ocRegConstD1.clone();
    }

    /**
     * Set interval to calculate BG in dimension 1 (time dimension) in actual values 
     * @param ind - 0: From 1: To 
     * @param value double
     */
    public void setOcRegConstD1(int ind, double value) {
        if (ocRegConstD1 == null) {
            ocRegConstD1 = new double[2];
        }
        if (ind < 2){
            ocRegConstD1[ind] = value;
        }
    }

    /**
     * Interval (in actual values) to calculate BG in dimension 2 (waves dimension) 
     * @return double[2] [valueFrom, valueTo], returns copy of values stored in the class
     */
    public double[] getOcRegConstD2() {
        return ocRegConstD2.clone();
    }
    
    /**
     * Set interval to calculate BG in dimension 1 (time dimension) in actual values
     * @param ind - 0: From 1: To 
     * @param value double
     */
    public void setOcRegConstD2(int ind, double value) {
        if (ocRegConstD2 == null) {
            ocRegConstD2 = new double[2];
        }
        if (ind < 2){
            ocRegConstD2[ind] = value;
        }
    }

    public boolean isIndividualOutlierC() {
        return individualOutlierC;
    }

    public void setIndividualOutlierC(boolean individualOutlierC) {
        this.individualOutlierC = individualOutlierC;
    }

    public boolean isRegionValueC() {
        return regionValueC;
    }

    public void setRegionValueC(boolean regionValueC) {
        this.regionValueC = regionValueC;
    }

    public boolean isNotANumberValueC() {
        return notANumberValueC;
    }

    public void setNotANumberValueC(boolean notANumberValueC) {
        this.notANumberValueC = notANumberValueC;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getFence() {
        return fence;
    }

    public void setFence(int fence) {
        this.fence = fence;
    }

  
    public void setOcRegConstD1(double[] ocRegConstD1) {
        this.ocRegConstD1 = ocRegConstD1;
    }


    public void setOcRegConstD2(double[] ocRegConstD2) {
        this.ocRegConstD2 = ocRegConstD2;
    }
    
    
}
