/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private int[] binnedData; //array with binned data - calculated during runtime
    private int[] intmap; //intencity map - calculated during runtime
    private int x, y; // sixe of the image in pixels
    private int curvenum; // number of curves x*y
    private short cannelN; //number of channels in time 
    private double cannelW; //time step width 
    private double time; //ful time window
    private int amplmax, amplmin; // max and min of intencitymap - calculated durin runtime
    private int binned;

    public FlimImageAbstract() {
        cannelN = 1;
        x = y = 0;
        time = 0;
        curvenum = 0;
        cannelW = 0;
        amplmax = amplmin = 0;
    }
    
    public int getBinned() {
        return binned;
    }

    public void setBinned(int binned) {
        this.binned = binned;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCurveNum() {
        return curvenum;
    }

    public int getMaxIntens() {
        return amplmax;
    }

    public int getMinIntens() {
        return amplmin;
    }

    public double getTime() {
        return time;
    }

    public int getCannelN() {
        return cannelN;
    }

    public double getCannelW() {
        return cannelW;
    }

    public int[] getData() {
        return binned == 1 ? binnedData : data;
//        if (binned==1) {
//            return binnedData;
//        }
//        else {
//            return data;
//        }
    }

    public int[] getIntMap() {
        return intmap;
    }

    public int[] getPixTrace(int i, int j) {
        int[] result = new int[cannelN];
        for (int ind = 0; ind < cannelN; ind++) {
            result[ind] = data[(i * x + j) * cannelN + ind];
        }
        return result;//data+(i*x+j)*cannelN;
    }

    public int[] getPixTrace(int i) {
        int[] result = new int[cannelN];
        for (int ind = 0; ind < cannelN; ind++) {
            result[ind] = data[i * cannelN + ind];
        }
        return result;//data+(i*x+j)*cannelN;
    }

    public void setTime(double t) {
        time = t;
        cannelW = time / cannelN;
    }

    public void setCannelN(short n) {
        cannelN = n;
        cannelW = time / n;
    }

    public void setCurveNum(int n) {
        curvenum = n;
    }

    public void setX(int n) {
        x = n;
    }

    public void setY(int n) {
        y = n;
    }

    public void setData(int[] datnew) {
        data = datnew;
    }

    public void setIntMap(int[] intmapnew) {
        intmap = intmapnew;
    }

    public int findCan(double t) {
        return (int) floor(t / cannelW);
    }

    public void buildIntMap(int mode) {
// mode = 1 - integral intensyty (number of photons in decay) 
// mode !=1 - amplitude of sygnal (number of photons in max)

        intmap = new int[curvenum];
        int tmp;
        amplmin = 65000;
        amplmax = tmp = 0;
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

    public void pixTraceToFile(int i, int j, String fileName) {
    }

    public int getDataPoint(int index) {
        if (binned == 1) {
            return binnedData[index];
        } else {
            return data[index];
        }
    }

    public void makeBinnedImage(int bin) {
        binnedData = new int[curvenum * cannelN];
        int temp;
        for (int i = bin; i < y - bin; i++) {
            for (int j = bin; j < x - bin; j++) {
                for (int t = 0; t < cannelN; t++) {
                    temp = 0;
                    for (int m = i - bin; m <= i + bin; m++) {
                        for (int n = j - bin; n <= j + bin; n++) {
                            temp += data[(m * x + n) * cannelN + t];
                        }
                    }
                    binnedData[(i * x + j) * cannelN + t] = temp;
                }
            }
        }
    }
    
}
