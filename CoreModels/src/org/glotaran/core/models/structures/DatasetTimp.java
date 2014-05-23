package org.glotaran.core.models.structures;

import java.io.Serializable;
//import org.glotaran.core.main.interfaces.DatasetTimpInterface;

/**
 *
 * @author Sergey
 */
public final class DatasetTimp implements Serializable {

    private double[] psisim;    //matrix with data stored as vector timetraces in row
    private double maxInt;      //max(psisim)
    private double minInt;      //min(psysim)
    private double[] x;         //vector with timesteps (x-dimension)
    private double[] x2;        //vector with wavelengts (y-dimension)
    private int nt;             //length of x
    private int nl;             //length of x2
    private int orheight;       //original height of flim image
    private int orwidth;        //original width of flimimage
    private double[] intenceIm; //intensity image for flimimage
    private String datasetName; //dataset name
    private String type;        //type: spec/flim/mas
    private String x1label;      //typically timescale (i.e. "Time")
    private String x1unit;      //typically "ps" or "ns"
    private String x2label;      //spectral unit (i.e. "Wavelength (nm)"
    private String x2unit;      //typically spectral unit (i.e. "Wavelength (nm)"
    private String datalabel;    // i.e. "Intensity" "Fluorescence" "Absorption" 
    private String dataunit;    // ("counts", "intensity", "V", "a.u.")
    private double[] measuredIRF; // vector with measured IRF
    private double[] measuredIRFDomainAxis; //(calibrated) time axis
    public static final long serialVersionUID = 1L;

    /**
     *
     */
    public DatasetTimp() {
        psisim = null;
        x = null;
        x2 = null;
        intenceIm = null;
        nt = 1;
        nl = 1;
        orheight = 1;
        orwidth = 1;
        datasetName = "dataset1";
        maxInt = 0;
        minInt = 0;
        type = null;
    }

    /**
     *
     * @param x1
     * @param x21
     * @param nt1
     * @param nl1
     * @param psisim1
     * @param intenceim1
     * @param datasetName1
     * @param type1
     */
    public DatasetTimp(double[] x1, double[] x21, int nt1, int nl1, double[] psisim1,
            double[] intenceim1, String datasetName1, String type1) {
        x = x1;
        x2 = x21;
        nt = nt1;
        nl = nl1;
        psisim = psisim1;
        intenceIm = intenceim1;
        datasetName = datasetName1;
        orheight = 1;
        orwidth = 1;
        type = type1;
        calcRangeInt();
    }

    /**
     *
     * @return
     */
    public int getNt() {
        return nt;
    }

    /**
     * 
     * @return
     */
    public int getNl() {
        return nl;
    }

    /**
     *
     * @return
     */
    public int getOriginalHeight() {
        return orheight;
    }

    /**
     *
     * @return
     */
    public int getOriginalWidth() {
        return orwidth;
    }

    /**
     *
     * @return
     */
    public double[] getPsisim() {
        return psisim;
    }

    /**
     *
     * @return
     */
    public double[] getX() {
        return x;
    }

    /**
     *
     * @return
     */
    public double[] getX2() {
        return x2;
    }

    /**
     *
     * @return
     */
    public double[] getIntenceIm() {
        return intenceIm;
    }

    /**
     *
     * @return
     */
    public double getMaxInt() {
        return maxInt;
    }

    /**
     *
     * @return
     */
    public double getMinInt() {
        return minInt;
    }

    /**
     *
     * @return
     */
    public String getDatasetName() {
        return datasetName;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param ntvalue
     */
    public void setNt(int ntvalue) {
        nt = ntvalue;
    }

    /**
     *
     * @param nlvalue
     */
    public void setNl(int nlvalue) {
        nl = nlvalue;
    }

    /**
     * 
     * @param orheightValue
     */
    public void setOrigHeigh(int orheightValue) {
        orheight = orheightValue;
    }

    /**
     *
     * @param orwidthValue
     */
    public void setOrigWidth(int orwidthValue) {
        orwidth = orwidthValue;
    }

    /**
     *
     * @param psisimValue
     */
    public void setPsisim(double[] psisimValue) {
        psisim = psisimValue;
    }

    /**
     *
     * @param xValue
     */
    public void setX(double[] xValue) {
        x = xValue;
    }

    /**
     *
     * @param x2Value
     */
    public void setX2(double[] x2Value) {
        x2 = x2Value;
    }

    /**
     *
     * @param x2Value
     */
    public void setIntenceIm(double[] x2Value) {
        intenceIm = x2Value;
    }

    /**
     *
     * @param dataName
     */
    public void setDatasetName(String dataName) {
        this.datasetName = dataName;
    }

    /**
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @param maxInt
     */
    public void setMaxInt(double maxInt) {
        this.maxInt = maxInt;
    }

    /**
     *
     * @param minInt
     */
    public void setMinInt(double minInt) {
        this.minInt = minInt;
    }

    public double[] getMeasuredIRF() {
        return measuredIRF;
    }

    public void setMeasuredIRF(double[] measuredIRF) {
        this.measuredIRF = measuredIRF;
    }

    public double[] getMeasuredIRFDomainAxis() {
        return measuredIRFDomainAxis;
    }

    public void setMeasuredIRFDomainAxis(double[] measuredIRFDomainAxis) {
        this.measuredIRFDomainAxis = measuredIRFDomainAxis;
    }

    public String getX1label() {
        return x1label;
    }

    public void setX1label(String x1label) {
        this.x1label = x1label;
    }

    public String getX1unit() {
        return x1unit;
    }

    public void setX1unit(String x1unit) {
        this.x1unit = x1unit;
    }

    public String getX2label() {
        return x2label;
    }

    public void setX2label(String x2label) {
        this.x2label = x2label;
    }

    public String getX2unit() {
        return x2unit;
    }

    public void setX2unit(String x2unit) {
        this.x2unit = x2unit;
    }

    public String getDatalabel() {
        return datalabel;
    }

    public void setDatalabel(String datalabel) {
        this.datalabel = datalabel;
    }

    public String getDataunit() {
        return dataunit;
    }

    public void setDataunit(String dataunit) {
        this.dataunit = dataunit;
    }

    /**
     *
     */
    public void calcRangeInt() {
        maxInt = psisim[0];
        minInt = psisim[0];
        for (int i = 0; i < nl * nt; i++) {
            if (psisim[i] > maxInt) {
                maxInt = psisim[i];
            }
            if (psisim[i] < minInt) {
                minInt = psisim[i];
            }
        }
    }
}
