package org.glotaran.sdtdataloader;

import org.glotaran.core.models.structures.FlimImageAbstract;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.sdtdataloader.sdtstructures.BHFileBlockHeader;
import org.glotaran.sdtdataloader.sdtstructures.FileHeader;
import org.glotaran.sdtdataloader.sdtstructures.MeasureInfo;
import static java.lang.Math.sqrt;
import org.openide.util.Exceptions;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Sergey
 */
public class FlimImageBH implements TGDatasetInterface {

//    private int[] data;
//    private int[] binnedData;
//    private int[] intmap;
//    private int x, y;
//    private int curvenum;
//    private short cannelN;
//    private short increm;
//    private double cannelW;
//    private double time;
//    private short measmod;
//    private int amplmax, amplmin;
//    private int binned;

//    public int getBinned() {
//        return binned;
//    }
//
//    public void setBinned(int binned) {
//        this.binned = binned;
//    }
//
    public FlimImageBH() {

    }

//    public FlimImage(File file) throws IOException, IllegalAccessException, InstantiationException {
//        ImageInputStream f = new FileImageInputStream(new RandomAccessFile(file, "r"));
//        f.setByteOrder(ByteOrder.LITTLE_ENDIAN);
//        FileHeader header = new FileHeader();
//        MeasureInfo measinf = new MeasureInfo();
//        BHFileBlockHeader blhead = new BHFileBlockHeader();
//
//        header.fread(f);
////        System.out.println(f.getStreamPosition());
//        f.seek(header.meas_desc_block_offs);
////        System.out.println("meas "+f.getStreamPosition());
//        measinf.fread(f);
////        System.out.println("blhead "+f.getStreamPosition());
//        f.seek(header.data_block_offs);
//        blhead.fread(f);
////        System.out.println("a blhead "+f.getStreamPosition());
//        f.seek(blhead.data_offs);
//
//        long size = blhead.block_length / 2;
//
//        setData(new int[(int) size]);
//        char[] tmpData = new char[(int) size];
//
////        System.out.println("data"+f.getStreamPosition());
//        f.readFully(tmpData, 0, (int) size);
//
//        f.close();
//
//        setCurveNum((int) blhead.block_length / measinf.adc_re / 2);
//        setX((int) sqrt(getCurveNum()));
//        setY((int) sqrt(getCurveNum()));
//        setCannelN(measinf.adc_re);
//        setTime(measinf.tac_r / measinf.tac_g * 1e9);
//        increm = measinf.incr;
//
//        for (int i = 0; i < size; ++i) {
//            getData()[i] = (int) tmpData[i] / increm;
//        }
//    }

//    public int getX() {
//        return x;
//    }
//
//    public int getY() {
//        return y;
//    }
//
//    public int getCurveNum() {
//        return curvenum;
//    }
//
//    public int getMaxIntens() {
//        return amplmax;
//    }
//
//    public int getMinIntens() {
//        return amplmin;
//    }
//
//    public double getTime() {
//        return time;
//    }
//
//    public int getCannelN() {
//        return cannelN;
//    }
//
//    public double getCannelW() {
//        return cannelW;
//    }
//
//    public int[] getData() {
//        return binned == 1 ? binnedData : data;
////        if (binned==1) {
////            return binnedData;
////        }
////        else {
////            return data;
////        }
//    }
//
//    public int[] getIntMap() {
//        return intmap;
//    }
//
//    public int[] getPixTrace(int i, int j) {
//        int[] result = new int[cannelN];
//        for (int ind = 0; ind < cannelN; ind++) {
//            result[ind] = data[(i * x + j) * cannelN + ind];
//        }
//        return result;//data+(i*x+j)*cannelN;
//    }
//
//    public int[] getPixTrace(int i) {
//        int[] result = new int[cannelN];
//        for (int ind = 0; ind < cannelN; ind++) {
//            result[ind] = data[i * cannelN + ind];
//        }
//        return result;//data+(i*x+j)*cannelN;
//    }
//
//    public void setTime(float t) {
//        time = t;
//        cannelW = time / cannelN;
//    }
//
//    public void setCannelN(short n) {
//        cannelN = n;
//        cannelW = time / n;
//    }
//
//    public void setCurveNum(int n) {
//        curvenum = n;
//    }
//
//    public void setX(int n) {
//        x = n;
//    }
//
//    public void setY(int n) {
//        y = n;
//    }
//
//    public void setIncrem(short n) {
//        increm = n;
//    }
//
//    public void setData(int[] datnew) {
//        data = datnew;
//    }
//
//    public void setIntMap(int[] intmapnew) {
//        intmap = intmapnew;
//    }
//
//    public int findCan(double t) {
//        return (int) floor(t / cannelW);
//    }
//
//    public void buildIntMap(int mode) {
//// mode = 1 - integral intensyty (number of photons in decay) 
//// mode !=1 - amplitude of sygnal (number of photons in max)
//
//        intmap = new int[curvenum];
//        int tmp;
//        amplmin = 65000;
//        amplmax = tmp = 0;
//        for (int i = 0; i < curvenum; i++) {
//            tmp = this.getDataPoint(i * cannelN);
//            for (int j = 1; j < cannelN; j++) {
//                if (mode == 1) {
//                    tmp += this.getDataPoint(i * cannelN + j);
//                } else if (this.getDataPoint(i * cannelN + j) > tmp) {
//                    tmp = this.getDataPoint(i * cannelN + j);
//                }
//            }
//            intmap[i] = tmp;
//            if (amplmin > tmp) {
//                amplmin = tmp;
//            }
//            if (amplmax < tmp) {
//                amplmax = tmp;
//            }
//        }
//    }
//
//    public void saveDataToASCIIFile(String fileName) throws IOException {
//        File f = new File(fileName);
//        if (f.exists()) {
//            f.delete();
//        }
//        PrintWriter w = new PrintWriter(f);
//        for (int i = 0; i < curvenum; ++i) {
//            for (int j = 0; j < cannelN; ++j) {
//                int ind = i * cannelN + j;
//                w.print(data[ind] + " ");
//            }
//            w.println();
//        }
//        w.close();
//    }
//
//    public void pixTraceToFile(int i, int j, String fileName) {
//    }
//
//    public int getDataPoint(int index) {
//        if (binned == 1) {
//            return binnedData[index];
//        } else {
//            return data[index];
//        }
//    }
//
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

    @Override
    public String getExtention() {
        return "sdt";
    }

    @Override
    public String getType(File file) {
        return "FLIM";
    }

    @Override
    public String getFilterString() {
        return ".sdt Becker & Hickl FLIM Image";
    }

    @Override
    public boolean Validator(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException {
        ImageInputStream f = new FileImageInputStream(new RandomAccessFile(file, "r"));
        f.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        FileHeader header = new FileHeader();
//        MeasureInfo measinf = new MeasureInfo();
        header.fread(f);

        if (header.header_valid == 0X5555) {
            return true;
        } else {
//            Confirmation msg = new NotifyDescriptor.Confirmation(
//                    NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("headerNotValid"),
//                    NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("err"),
//                    NotifyDescriptor.ERROR_MESSAGE);
//            DialogDisplayer.getDefault().notify(msg);
            return false;
        }
    }

    @Override
    public DatasetTimp loadFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        FlimImageAbstract image = new FlimImageAbstract();
        ImageInputStream f = new FileImageInputStream(new RandomAccessFile(file, "r"));
        f.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        FileHeader header = new FileHeader();
        MeasureInfo measinf = new MeasureInfo();
        BHFileBlockHeader blhead = new BHFileBlockHeader();
        try {
            header.fread(f);
//        System.out.println(f.getStreamPosition());
            f.seek(header.meas_desc_block_offs);
//        System.out.println("meas "+f.getStreamPosition());
            measinf.fread(f);
//        System.out.println("blhead "+f.getStreamPosition());
            f.seek(header.data_block_offs);
            blhead.fread(f);
//        System.out.println("a blhead "+f.getStreamPosition());
            f.seek(blhead.data_offs);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        long size = blhead.block_length / 2;
        image.setData(new int[(int) size]);
        char[] tmpData = new char[(int) size];
        try {
//        System.out.println("data"+f.getStreamPosition());
            f.readFully(tmpData, 0, (int) size);
            f.close();

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        image.setCurveNum((int) blhead.block_length / measinf.adc_re / 2);
        image.setX((int) sqrt(image.getCurveNum()));
        image.setY((int) sqrt(image.getCurveNum()));
        image.setCannelN(measinf.adc_re);
        image.setTime(measinf.tac_r / measinf.tac_g * 1e9);
        int increm = measinf.incr;

        for (int i = 0; i < size; ++i) {
            image.setDataPoint(i, (int)tmpData[i]/increm); //[i] = (int) tmpData[i] / increm;
        }
        return image;
    }
   
}
