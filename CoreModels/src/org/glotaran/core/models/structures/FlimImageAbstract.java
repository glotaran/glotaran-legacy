package org.glotaran.core.models.structures;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import static java.lang.Math.floor;

/**
 *
 * @author Sergey
 */

public class FlimImageAbstract {
    private int[] data; //array with data - loaded from file
//    private int[] binnedData; //array with binned data - calculated during runtime
    private int[] intmap; //intencity map - calculated during runtime
    private double[] timeSteps; //vector with time steps;
    private int x, y; // sixe of the image in pixels
    private int curvenum; // number of curves x*y
    private short cannelN; //number of channels in time 
    private double cannelW; //time step width 
    private double time; //ful time window
    private int amplmax, amplmin; // max and min of intencitymap - calculated durin runtime
    private int binned;

    /**
     *Default constructor. Create FLIM image with 0 size and 1 time channel 
     */
    public FlimImageAbstract() {
        cannelN = 1;
        x = y = 0;
        time = 0;
        curvenum = 0;
        cannelW = 0;
        amplmax = amplmin = 0;
    }
    
    /**
     * Checks state of the binning for image
     * @return 0 if no binning other wise size of the binning window (now 0 or 1) 
     */
    public int getBinned() {
        return binned;
    }

    /**
     * Sets binning factor for image, now (0 - no binning, 1 - biining 1) 
     * @param binned binning factor
     */
    public void setBinned(int binned) {
        this.binned = binned;
    }
    
    /**
     * Return X size of the image in pixels (number of columns)
     * @return X size of the image in pixels
     */
    public int getX() {
        return x;
    }

    /**
     * Return Y size of the image in pixels (number of Rows) 
     * @return Y size of the image in pixels
     */
    public int getY() {
        return y;
    }

    /**
     * Return number of time traces in the image (X*Y) equal to the number of pixels in the image
     * @return number of pixels in the image
     */
    public int getCurveNum() {
        return curvenum;
    }

    /**
     * Return max of total intensity in the image in intensity image 
     * @return max intensity
     */
    public int getMaxIntens() {
        return amplmax;
    }

    /**
     * Return max of total intensity in the image in intensity image 
     * @return min intensity
     */
    public int getMinIntens() {
        return amplmin;
    }

    /**
     * Return total time window of the image 
     * @return full time window
     */
    public double getTime() {
        return time;
    }

    /**
     * Return number of time channels 
     * @return number of time channels
     */
    public int getCannelN() {
        return cannelN;
    }

    /**
     * Return with of time channel for measurements with homogeneous time axe  
     * @return
     */
    public double getCannelW() {
        return cannelW;
    }

    /**
     * Return whole data matrix 
     * @return raw data matrix
     */
    public int[] getData() {
   
        return data;
//        return binned == 1 ? binnedData : data;
//        if (binned==1) {
//            return binnedData;
//        }
//        else {
//            return data;
//        }
    }

    /**
     * Return intensity map
     * @return intensity matrix 
     */
    public int[] getIntMap() {
        return intmap;
    }

    /**
     * Return time trace from specified pixel take into account binning state
     * @param i rows
     * @param j columns 
     * @return time trace
     */
    public int[] getPixTrace(int i, int j) {
        int[] result = new int[cannelN];
        for (int ind = 0; ind < cannelN; ind++) {
            result[ind] = getDataPoint(i, j, ind);
        }
        return result;//data+(i*x+j)*cannelN;
    }

    /**
     * Return time trace from specified pixel take into account binning state
     * @param i global pixel index 
     * @return time trace
     */
    public int[] getPixTrace(int i) {
        int[] result = new int[cannelN];
        for (int ind = 0; ind < cannelN; ind++) {
            result[ind] = getDataPoint(i * cannelN + ind);
        }
        return result;//data+(i*x+j)*cannelN;
    }

    /**
     * Set time window of image
     * recalculate channelW parameter (for homogeneous time axes)
     * @param t time window 
     */
    public void setTime(double t) {
        time = t;
        cannelW = time / cannelN;
    }

    /**
     * Set number of time channels 
     * recalculate channelW parameter (for homogeneous time axes)
     * @param n number of time steps
     */
    public void setCannelN(short n) {
        cannelN = n;
        cannelW = time / n;
    }

    /**
     * set number of pixels in the image
     * @param n number of pixels 
     */
    public void setCurveNum(int n) {
        curvenum = n;
    }

    /**
     * Set X size of the image (number of columns) 
     * @param n number of columns  
     */
    public void setX(int n) {
        x = n;
    }

    /**
     * Set Y size of the image (number of rows) 
     * @param n number of rows
     */
    public void setY(int n) {
        y = n;
    }

    /**
     * Set data matrix
     * @param datnew new data matrix
     */
    public void setData(int[] datnew) {
        data = datnew;
    }

    /**
     * Set Intensity matrix
     * @param intmapnew intensity matrix
     */
    public void setIntMap(int[] intmapnew) {
        intmap = intmapnew;
    }

    /**
     * Calculate time index for given time
     * @param t time 
     * @return time channel index
     */
    public int findCan(double t) {
        return (int) floor(t / cannelW);
    }

    /**
     * Create intensity image 
     * @param mode 1 - integral intensity other wise - amplitude of signal
     */
    public void buildIntMap(int mode) {
// mode = 1 - integral intensity (number of photons in decay) 
// mode !=1 - amplitude of signal (number of photons in max)

        intmap = new int[curvenum];
        int tmp = 0;
        amplmin = 65000;
        amplmax = tmp;
        for (int i = 0; i < curvenum; i++) {
            tmp = this.getDataPoint(i * cannelN);
            for (int j = 1; j < cannelN; j++) {
                if (mode == 1) {
                    tmp += this.getDataPoint(i * cannelN + j);
                } else if (this.getDataPoint(i * cannelN + j) > tmp) {
                    tmp = this.getDataPoint(i * cannelN + j);
                }
            }
            intmap[i] = tmp;
            if (amplmin > tmp) {
                amplmin = tmp;
            }
            if (amplmax < tmp) {
                amplmax = tmp;
            }
        }
    }

    /**
     * Save all time traces in ASCII file 1 time trace per line  
     * @param fileName file name 
     * @throws IOException
     */
    public void saveDataToASCIIFile(String fileName) throws IOException {
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        }
        PrintWriter w = new PrintWriter(f);
        for (int i = 0; i < curvenum; ++i) {
            for (int j = 0; j < cannelN; ++j) {
                int ind = i * cannelN + j;
                w.print(data[ind] + " ");
            }
            w.println();
        }
        w.close();
    }

    /**
     * Save time trace from selected pixel to ASCII file
     * @param i row index   
     * @param j column index
     * @param fileName destination file name
     */
    public void pixTraceToFile(int i, int j, String fileName) {
        
    }

    /**
     * Return value based on the binning factor  
     * @param index global index in data matrix 
     * @return value from selected point
     */
    public int getDataPoint(int index) {
        int i, j, t;
        t = index % cannelN;
        j = ((index - t) / cannelN) % x;
        i = (((index - t) / cannelN) - j) / x;
        
        return getDataPoint(i, j, t);
    }
    
        /**
     * Return value based on the binning factor  
     * @param i row index
     * @param j column index
     * @param t time index
     * @return value from selected point  
     */
    public int getDataPoint(int i, int j, int t) {
        int temp = 0;
        if (binned == 0) {
            return data[(i * x + j) * cannelN + t];
        } else {
            if (i < binned || j < binned || i >= y - binned || j >= x - binned) {
                temp = 0;
            } else {
                for (int m = i - binned; m <= i + binned; m++) {
                    for (int n = j - binned; n <= j + binned; n++) {
                        temp += data[(m * x + n) * cannelN + t];
                    }
                }
            }
            return temp;
        }
    }
    
    /**
     * Set selected point in datamatrix to specified value
     * @param index index in datamatrix
     * @param value value to set
     */
    public void setDataPoint(int index, int value){
        data[index] = value;
    }
    
    
    /**
     * Increment selected value in datamatrix by 1  
     * @param index index in datamatrix
     */
    public void incrementDataPoint(int index){
        data[index]++;
    }

    /**
     * going to be removed
     * @param bin
     */
//    public void makeBinnedImage(int bin) {
//        binnedData = new int[curvenum * cannelN];
//        int temp;
//        for (int i = bin; i < y - bin; i++) {
//            for (int j = bin; j < x - bin; j++) {
//                for (int t = 0; t < cannelN; t++) {
//                    temp = 0;
//                    for (int m = i - bin; m <= i + bin; m++) {
//                        for (int n = j - bin; n <= j + bin; n++) {
//                            temp += data[(m * x + n) * cannelN + t];
//                        }
//                    }
//                    binnedData[(i * x + j) * cannelN + t] = temp;
//                }
//            }
//        }
//    }
    
}
