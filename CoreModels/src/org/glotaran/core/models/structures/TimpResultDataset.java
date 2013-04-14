package org.glotaran.core.models.structures;

/**
 *
 * @author Sergey
 */
//import org.glotaran.core.main.interfaces.TimpResultInterface;
import Jama.Matrix;
import java.io.Serializable;

public class TimpResultDataset implements Serializable {

    private String datasetName;
    private String type;            //type of the dataset (flim, spec, mass)
    private double[] kineticParameters;     //Kinetic parameters for kinmodel
    private double[] spectralParameters;    //spectral parameters for specmodel
    private double[] irfpar;        //irf model parameters
    private double[] parmu;         //irf location modeling
    private double[] partau;        //irf Width modeling
    private double[] jvec;          //jvector
    private double[] specdisppar;   //time modeling of the spec parammeters
    private double[] prel;          //parameters for relations
    private double[] kinscal;       //branching scaling factors
    private double[] clpequ;        //equality of the spectra coeficcient
    private Matrix concentrations;  //matrix with concentrations
    private Matrix spectra;         //matrix with CLP
    private Matrix spectraErr;      //errors of the CLP
    private Matrix residuals;       //matrix with residuals
    private Matrix traces;          //matrix with raw data
    private Matrix fittedTraces;    //matrix with fitted data
    private double[] x;             //vector with timesteps (x-dimension)
    private double[] x2;            //vector with wavelengts (y-dimension)
    private double[] intenceIm;     //intensity image FLIM
    private int orheigh;            //original hight of flimimage
    private int orwidth;            //original widht of flimimage
    private double maxInt;          //max of intencity (for plotting code)
    private double minInt;          //min of intencity (for plotting code)
    private double lamdac;          //usually this is the center wavelength
    private double rms;             //final rms
    private double[] eigenvaluesK;  //hold the vector of eigenvalues of the
                                    //kinetic transfer matrix if it was used
    public static final long serialVersionUID = 1L;

    public double[] getClpequ() {
        return clpequ;
    }

    public void setClpequ(double[] clpequ) {
        this.clpequ = clpequ;
    }

    public double[] getKinscal() {
        return kinscal;
    }

    public void setKinscal(double[] kinscal) {
        this.kinscal = kinscal;
    }

    public double[] getPartau() {
        return partau;
    }

    public void setPartau(double[] partau) {
        this.partau = partau;
    }

    public double[] getPrel() {
        return prel;
    }

    public void setPrel(double[] prel) {
        this.prel = prel;
    }

    public double getRms() {
        return rms;
    }

    public void setRms(double rms) {
        this.rms = rms;
    }

    public Matrix getSpectraErr() {
        return spectraErr;
    }

    public void setSpectraErr(Matrix spectraErr) {
        this.spectraErr = spectraErr;
    }

    public void setDatasetName(String datasetNameValue) {
        datasetName = datasetNameValue;
    }

    public void setKineticParameters(double[] kineticParametersValue) {
        kineticParameters = kineticParametersValue;
    }

    public void setSpectralParameters(double[] spectralParametersValue) {
        spectralParameters = spectralParametersValue;
    }

    public void setConcentrations(Matrix concentrationsValue) {
        concentrations = concentrationsValue;
    }

    public void setSpectra(Matrix spectraValue) {
        spectra = spectraValue;
    }

    public void setX(double[] xValue) {
        x = xValue;
    }

    public void setX2(double[] x2Value) {
        x2 = x2Value;
    }

    public void setResiduals(Matrix residualsValue) {
        residuals = residualsValue;
    }

    public void setTraces(Matrix tracesValue) {
        traces = tracesValue;
    }

    public void setFittedTraces(Matrix fittedTracesValue) {
        fittedTraces = fittedTracesValue;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIntenceIm(double[] intenceIm) {
        this.intenceIm = intenceIm;
    }

    public void setOrheigh(int orheigh) {
        this.orheigh = orheigh;
    }

    public void setOrwidth(int orwidth) {
        this.orwidth = orwidth;
    }

    public void setMaxInt(double maxInt) {
        this.maxInt = maxInt;
    }

    public void setMinInt(double minInt) {
        this.minInt = minInt;
    }

    public void setIrfpar(double[] irfpar) {
        this.irfpar = irfpar;
    }

    public void setJvec(double[] jvec) {
        this.jvec = jvec;
    }

    public void setLamdac(double lamdac) {
        this.lamdac = lamdac;
    }

    public void setParmu(double[] parmu) {
        this.parmu = parmu;
    }

    public void setSpecdisppar(double[] specdisppar) {
        this.specdisppar = specdisppar;
    }

    public double[] getIntenceIm() {
        return intenceIm;
    }

    public int getOrheigh() {
        return orheigh;
    }

    public int getOrwidth() {
        return orwidth;
    }

    public String getType() {
        return type;
    }

    public double getMaxInt() {
        return maxInt;
    }

    public double getMinInt() {
        return minInt;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public double[] getKineticParameters() {
        return kineticParameters;
    }

    public double[] getSpectralParameters() {
        return spectralParameters;
    }

    public Matrix getConcentrations() {
        return concentrations;
    }

    public Matrix getSpectra() {
        return spectra;
    }

    public double[] getX() {
        return x;
    }

    public double[] getX2() {
        return x2;
    }

    public Matrix getResiduals() {
        return residuals;
    }

    public Matrix getTraces() {
        return traces;
    }

    public Matrix getFittedTraces() {
        return fittedTraces;
    }

    public double[] getSpecdisppar() {
        return specdisppar;
    }

    public double[] getParmu() {
        return parmu;
    }

    public double getLamdac() {
        return lamdac;
    }

    public double[] getJvec() {
        return jvec;
    }

    public double[] getIrfpar() {
        return irfpar;
    }

    public double[] getEigenvaluesK() {
        return eigenvaluesK;
    }

    public void setEigenvaluesK(double[] eigenvaluesK) {
        this.eigenvaluesK = eigenvaluesK;
    }

    public TimpResultDataset() {
        datasetName = "Dataset1";
        orheigh = 64;      //original hight of flimimage
        orwidth = 64;      //original widht of flimimage
        intenceIm = null; //intensity image FLIM
        kineticParameters = new double[]{10, 3, .5};
        spectralParameters = null;
        concentrations = Matrix.random(15, 3);
        spectra = Matrix.random(3, 10);
        x = new double[]{0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1, 1.1, 1.2, 1.3, 1.4};
        x2 = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
//        x2 = new double[]{6,7,12,13,14,15,18,19,21,22};
        residuals = Matrix.random(15, 10);
        traces = Matrix.random(15, 10);
        fittedTraces = Matrix.random(15, 10);
        irfpar = new double[]{0.2, 0.05};
        parmu = new double[]{1.1, 0.9};
        jvec = null;
        specdisppar = null;
        lamdac = 4;
        eigenvaluesK = null;
        calcRangeInt();
    }

    public final void calcRangeInt() {
        maxInt = 0;
        minInt = 0;
        double[] temp = traces.getColumnPackedCopy();
        for (int i = 0; i < x2.length * x.length; i++) {
            if (temp[i] > maxInt) {
                maxInt = temp[i];
            }
            if (temp[i] < minInt) {
                minInt = temp[i];
            }
        }
    }
}
