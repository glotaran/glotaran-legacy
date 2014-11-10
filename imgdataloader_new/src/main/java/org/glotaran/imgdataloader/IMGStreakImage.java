/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.imgdataloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.FlimImageAbstract;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author serlap
 */
public class IMGStreakImage implements TGDatasetInterface {

    @Override
    public String getExtention() {
        return "img";
    }

    @Override
    public String getFilterString() {
        return ".img Hamamatsu streack images";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        return "spec";
    }

    @Override
    public boolean Validator(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException {
        String ext = FileUtil.getExtension(file.getName());
        if (ext.equalsIgnoreCase(getExtention())) {
            char symb;
            ImageInputStream f = new FileImageInputStream(new RandomAccessFile(file, "r"));
            f.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            symb = (char) f.readByte();
            if (symb == 'I') {
                symb = (char) f.readByte();
                if (symb == 'M') {
                    f.close();
                    return true;
                }
            }
            f.close();
            return false;
        } else {
            return false;
        }
    }

    @Override
    public DatasetTimp loadFile(File file) throws FileNotFoundException {
        DatasetTimp strImage = new DatasetTimp();
        strImage.setType("spec");
        ImageInputStream f = new FileImageInputStream(new RandomAccessFile(file, "r"));
        f.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        short commentLength = 0;
        short xOffset = 0;
        short yOffset = 0;
        short type = 0;
        String commentString = "";
        try {
            f.seek(2);
            commentLength = f.readShort();
            strImage.setNl(f.readShort());
            strImage.setNt(f.readShort());
            xOffset = f.readShort();
            yOffset = f.readShort();
            type = f.readShort();
            f.seek(64);
            for (int i = 0; i < commentLength; i++) {
                commentString = commentString.concat(String.valueOf((char) f.readByte()));
            }
        } catch (IOException ex) {
            return null;
        }
        int dataLen = strImage.getNl() * strImage.getNt();
        double[] data = new double[dataLen];
        if (type == 2) {
            for (int i = 0; i < strImage.getNt(); i++) {
                for (int j = 0; j < strImage.getNl(); j++) {
                    try {
                        data[j * strImage.getNt() + i] = f.readShort();
                    } catch (IOException ex) {
                        return null;
                    }
                }
            }
        } else {
            if (type == 3) {
                for (int i = 0; i < strImage.getNt(); i++) {
                    for (int j = 0; j < strImage.getNl(); j++) {
                        try {
                            data[j * strImage.getNt() + i] = f.readInt();
                        } catch (IOException ex) {
                            return null;
                        }
                    }
                }
            } else {
                return null;
            }
        }
        strImage.setPsisim(data);

//read X calibration (wavelength)
        int[] calibrationPos;
        calibrationPos = getScalingFile(commentString, "ScalingXScalingFile");
        if ((calibrationPos[0] != 0) && (calibrationPos[1] != 0)) {
            double[] x2 = new double[calibrationPos[1]];
            try {
                f.seek(calibrationPos[0]);
                for (int i = 0; i < calibrationPos[1]; i++) {
                    x2[i] = f.readFloat();
                    strImage.setX2(x2);
                }
            } catch (IOException ex) {
                return null;
            }
        } else {
            double[] x2 = new double[strImage.getNl()];
            for (int i = 0; i < strImage.getNl(); i++) {
                x2[i] = i;
            }
            strImage.setX2(x2);
        }

//read Y calibration (time)
        calibrationPos = getScalingFile(commentString, "ScalingYScalingFile");
        if ((calibrationPos[0] != 0) && (calibrationPos[1] != 0)) {
            double[] x = new double[calibrationPos[1]];
            try {
                f.seek(calibrationPos[0]);
                for (int i = 0; i < calibrationPos[1]; i++) {
                    x[i] = f.readFloat();
                    strImage.setX(x);
                }
            } catch (IOException ex) {
                return null;
            }
        } else {
            double[] x = new double[strImage.getNt()];
            for (int i = 0; i < strImage.getNt(); i++) {
                x[i] = i;
            }
            strImage.setX(x);
        }

//----------------

        strImage.calcRangeInt();
        boolean invertedWaves = strImage.getX2()[0] >= strImage.getX2()[1];
        if (invertedWaves) {
            double[] x2t = new double[strImage.getNl()];
            double[] temp = new double[strImage.getNl() * strImage.getNt()];
            for (int j = 0; j < strImage.getNl(); j++) {
                for (int i = 0; i < strImage.getNt(); i++) {
                    temp[(strImage.getNl() - 1 - j) * strImage.getNt() + i] = strImage.getPsisim()[j * strImage.getNt() + i];
                }
                x2t[strImage.getNl() - j - 1] = strImage.getX2()[j];
            }
            strImage.setX2(x2t);
            strImage.setPsisim(temp);
        }

        return strImage;
    }

    private int[] getScalingFile(String comment, String scalingString) {
        int scalingPos = comment.indexOf(scalingString);
        int scalingFileLength = 0;
        int scalingFile = 0;
        int tempInd = 0;
        if (comment.charAt(scalingPos + 20) == '*') {
            scalingFileLength = 1024;
            comment.charAt(scalingPos + 21);
            comment.substring(scalingPos + 21).indexOf(",");
            tempInd = comment.indexOf(',', scalingPos + 21);
            int tempInd2 = comment.indexOf('[', scalingPos + 21);
            if (tempInd > 0 && tempInd2 > 0) {
                tempInd = tempInd < tempInd2 ? tempInd : tempInd2;
            }
            if (tempInd > 0) {
                scalingFile = Integer.parseInt(comment.substring(scalingPos + 21, tempInd));
            } else {
                scalingFile = Integer.parseInt(comment.substring(scalingPos + 21));
            }
        } else if (comment.charAt(scalingPos + 20) == '+') {
            scalingFileLength = 1280;
            tempInd = comment.indexOf(',', scalingPos + 21);
            scalingFile = Integer.parseInt(comment.substring(scalingPos + 21, tempInd));
        } else if (comment.charAt(scalingPos + 21) == '#') {
            tempInd = comment.indexOf(',', scalingPos + 21);
            scalingFile = Integer.parseInt(comment.substring(scalingPos + 22, tempInd));
            int tempInd2 = comment.indexOf('"', tempInd + 1);
            scalingFileLength = Integer.parseInt(comment.substring(tempInd + 1, tempInd2));
        }
        return new int[]{scalingFile, scalingFileLength};
    }

    private double[] createTimeVec(String comment, int timeLenght) {
        int temp = comment.indexOf("Time Range");
        int timeRange = Integer.parseInt(comment.substring(temp + 12, temp + 13));
        return null;
    }

    private double CalcBinTime(int pos, boolean superpix, boolean lwag, double[] calibration) {
        double rez = 0;
        if (lwag) {
            if (superpix) {
                pos = 2 * pos;
            }
            rez = calibration[0] + pos * calibration[1] + pos * pos * calibration[2] * calibration[2] + pos * pos * pos * calibration[3] * calibration[3] * calibration[3];
            if (superpix) {
                rez = rez * 2;
            }
        }
        return rez;
    }

    private double[] getCalibration(int timeRange, boolean lwag) {
        lwag = true;
        double[] a = new double[4];
        switch (timeRange) {
            case 1:
                if (lwag) {
//			pixtime=0.156;
                    a[0] = 1.5638e-01;
                    a[1] = -1.13812e-05;
                    a[2] = 1.81061e-08;
                    a[3] = -7.50162e-12;
                } else {
                }
                return a; //case1

            case 2:
                if (lwag) {
//			pixtime=0.81;
                    a[0] = 8.0959e-01;
                    a[1] = -11.5975e-04;
                    a[2] = 1.5428e-07;
                    a[3] = 0;

                } else {
                }
                return a; //case2

            case 3:
                if (lwag) {
//			pixtime=1.5;
                    a[0] = 1.52316e-01;
                    a[1] = -4.91418e-04;
                    a[2] = 6.26874e-07;
                    a[3] = -1.68202e-10;

                } else {
                }
                return a; //case3

            case 4:
                if (lwag) {
//			pixtime=2.12;
                    a[0] = 2.12359;
                    a[1] = -9.33621e-04;
                    a[2] = 9.37584e-07;
                    a[3] = -4.22982e-12;

                } else {
                }
                return a; //case4
        } //switch
        return a;
    }

    @Override
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
