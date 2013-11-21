/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.pqdataloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.FlimImageAbstract;
import org.glotaran.pqdataloader.pqstructures.BinHdr1;
import org.glotaran.pqdataloader.pqstructures.BinHdr2;
import org.glotaran.pqdataloader.pqstructures.BoardHdr;
import org.glotaran.pqdataloader.pqstructures.CurveMapping;
import org.glotaran.pqdataloader.pqstructures.ParamStruct;
import org.glotaran.pqdataloader.pqstructures.TTTRHdr;
import org.glotaran.pqdataloader.pqstructures.TxtHdr;
import static java.lang.Math.floor;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Sergey
 */
//@ServiceProvider(service=org.glotaran.core.interfaces.TGDatasetInterface.class)
public class FlimImagePQ implements TGDatasetInterface {

    @Override
    public String getExtention() {
        return "pt3";
    }

    @Override
    public String getFilterString() {
        return ".pt3 PQ PicoHarp FLIM Image";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        return "FLIM";
    }

    @Override
    public boolean Validator(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException {
        String ext = FileUtil.getExtension(file.getName());
        if (ext.equalsIgnoreCase(getExtention())) {
            ImageInputStream f = new FileImageInputStream(new RandomAccessFile(file, "r"));
            f.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            TxtHdr header = new TxtHdr();
            BinHdr1 binHead1 = new BinHdr1();
            BinHdr2 binHead2 = new BinHdr2();
            TTTRHdr ttHeader = new TTTRHdr();
            BoardHdr[] boards;
            CurveMapping[] dispCurves = new CurveMapping[8];
            ParamStruct[] params = new ParamStruct[3];
            header.fread(f);



            String temp = new String(header.Ident);

            if (temp.trim().equalsIgnoreCase("PicoHarp 300")) {
                temp = new String(header.FormatVersion);
                if (temp.trim().equalsIgnoreCase("2.0")) {
                    binHead1.fread(f);
                    if (binHead1.MeasMode == 3) {
                        for (int i = 0; i < 8; i++) {
                            dispCurves[i] = new CurveMapping();
                            dispCurves[i].fread(f);
                        }
                        for (int i = 0; i < 3; i++) {
                            params[i] = new ParamStruct();
                            params[i].fread(f);
                        }
                        binHead2.fread(f);
//for now number of boards fixed to 1 by PQ            
                        boards = new BoardHdr[binHead1.NumberOfBoards];
                        for (int i = 0; i < binHead1.NumberOfBoards; i++) {
                            boards[i] = new BoardHdr();
                            boards[i].fread(f);
                        }
                        ttHeader.fread(f);
                        if (ttHeader.ImgHdrSize > 0) {
                            return true;
                        } else {
//                        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
//                                NbBundle.getBundle("org/glotaran/pqdataloader/Bundle").getString("noImage"),
//                                NotifyDescriptor.ERROR_MESSAGE);
//                        DialogDisplayer.getDefault().notify(errorMessage);
                            return false;
                        }

                    } else {
//                    NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
//                                NbBundle.getBundle("org/glotaran/pqdataloader/Bundle").getString("wrongMeasMode"),
//                                NotifyDescriptor.ERROR_MESSAGE);    
//                    DialogDisplayer.getDefault().notify(errorMessage);
                        return false;
                    }
                } else {
//                NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
//                                NbBundle.getBundle("org/glotaran/pqdataloader/Bundle").getString("wrongFileVersion"),
//                                NotifyDescriptor.ERROR_MESSAGE);    
//                DialogDisplayer.getDefault().notify(errorMessage);
                    return false;
                }
            } else {
//            NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
//                                NbBundle.getBundle("org/glotaran/pqdataloader/Bundle").getString("wrongFileType"),
//                                NotifyDescriptor.ERROR_MESSAGE);    
//                DialogDisplayer.getDefault().notify(errorMessage);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        FlimImageAbstract flimImage = new FlimImageAbstract();
        ImageInputStream f = new FileImageInputStream(new RandomAccessFile(file, "r"));
        f.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        TxtHdr header = new TxtHdr();
        BinHdr1 binHead1 = new BinHdr1();
        BinHdr2 binHead2 = new BinHdr2();
        TTTRHdr ttHeader = new TTTRHdr();
        BoardHdr[] boards;
        CurveMapping[] dispCurves = new CurveMapping[8];
        ParamStruct[] params = new ParamStruct[3];
        double syncPeriod = 0;
        try {
            header.fread(f);
            binHead1.fread(f);

            for (int i = 0; i < 8; i++) {
                dispCurves[i] = new CurveMapping();
                dispCurves[i].fread(f);
            }
            for (int i = 0; i < 3; i++) {
                params[i] = new ParamStruct();
                params[i].fread(f);
            }

            binHead2.fread(f);
//for now number of boards fixed to 1 by PQ            
            boards = new BoardHdr[binHead1.NumberOfBoards];
            for (int i = 0; i < binHead1.NumberOfBoards; i++) {
                boards[i] = new BoardHdr();
                boards[i].fread(f);
            }
            ttHeader.fread(f);
            long temp;
            long[] imageHead = new long[ttHeader.ImgHdrSize];
//image related info (still has to be verified what is there, number 6 and 7 looks like is an image size)            
            for (int i = 0; i < ttHeader.ImgHdrSize; i++) {
                imageHead[i] = f.readUnsignedInt();
            }
            flimImage.setX((int) imageHead[6]);
            flimImage.setY((int) imageHead[7]);
            flimImage.setCurveNum((int) imageHead[6] * (int) imageHead[7]);
            flimImage.setCannelN((short) 4096);
            flimImage.setTime(4096 * boards[0].Resolution);
            flimImage.setData(new int[flimImage.getCurveNum() * 4096]);


            syncPeriod = 1E9 / ttHeader.CntRate0;
//loading photon events            
            long tmpCh, tmpdTime, tmpSnk;
            long oflCount = 0;
            int marker = 0;
            int lineInd = -1;
            int rowInd = -1;
            long lineStartTime = 0, lineEndTime = 0, lineTime;

            long photStart = f.getStreamPosition();
//get line scan time
            boolean lineMarker = false;
            while (!lineMarker) {
                temp = f.readUnsignedInt();
                tmpCh = (temp >>> 28) & 0xf; //4 bits
                tmpdTime = (temp >>> 16) & 0xfff; //12 bits
                tmpSnk = temp & 0xffff; //16 bits

                if ((tmpCh == 15) && (tmpdTime == 0)) {
                    oflCount++;
                    continue;
                }

                if ((tmpCh == 15) && (tmpdTime > 0)) {
                    //its a marker    
                    //start of the line
                    if (tmpdTime == 1) {
                        // calculate start of the line time
                        lineStartTime = (oflCount * 65536) + tmpSnk;
                        lineMarker = true;
                    }
                }
            }
            lineMarker = false;
            while (!lineMarker) {
                temp = f.readUnsignedInt();
                tmpCh = (temp >>> 28) & 0xf; //4 bits
                tmpdTime = (temp >>> 16) & 0xfff; //12 bits
                tmpSnk = temp & 0xffff; //16 bits

                if ((tmpCh == 15) && (tmpdTime == 0)) {
                    oflCount++;
                    continue;
                }

                if ((tmpCh == 15) && (tmpdTime > 0)) {
                    //its a marker    
                    //start of the line
                    if (tmpdTime == 2) {
                        // calculate start of the line time
                        lineEndTime = (oflCount * 65536) + tmpSnk;
                        lineMarker = true;
                    }
                }
            }
            lineTime = lineEndTime - lineStartTime;

            f.seek(photStart);
            lineMarker = false;
            for (long i = 0; i < ttHeader.Records; i++) {

                temp = f.readUnsignedInt();
//                temp = 2975487644l;                
//                tmpCh = temp & 0xf0000000;
//                tmpStSt = temp & 0xfff0000;
//                tmpSnk = temp & 0xffff;

                tmpCh = (temp >>> 28) & 0xf; //4 bits
                tmpdTime = (temp >>> 16) & 0xfff; //12 bits
                tmpSnk = temp & 0xffff; //16 bits

                if ((tmpCh == 15) && (tmpdTime == 0)) {
                    //owerflow marker
                    oflCount++;
                    continue;
                }

                if ((tmpCh == 15) && (tmpdTime > 0)) {
                    //its a marker    
                    //start of the line
                    if (tmpdTime == 1) {
                        if ((lineInd == -1) || (lineInd == flimImage.getY())) {
                            lineInd = 0;
                        }
                        // calculate start of the line time
                        lineStartTime = (oflCount * 65536) + tmpSnk;
                        lineMarker = true;
                    }
                    //end of the line 
                    if (tmpdTime == 2) {
                        lineInd++;
                        lineMarker = false;
                    }
                    //end of frame
                    if (tmpdTime == 6) {
                        lineInd = -1;
                        lineMarker = false;
                    }
                    continue;
                }

                // photon analysis
                if ((lineInd != -1) && lineMarker) {
                    // obtain row index
                    rowInd = (int) floor((((float) ((oflCount * 65536) + tmpSnk - lineStartTime) / lineTime)) * flimImage.getX());
                    //add photon in the histogram
                    flimImage.incrementDataPoint((lineInd * flimImage.getX() + rowInd) * flimImage.getCannelN() + (int) tmpdTime); //;getData()[(lineInd*flimImage.getX()+rowInd)*flimImage.getCannelN()+(int)tmpdTime]++;
                }





            }
            //f.seek(f.getStreamPosition()+ttHeader.ImgHdrSize);


        } catch (IOException ex) {
            Logger.getLogger(FlimImagePQ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FlimImagePQ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(FlimImagePQ.class.getName()).log(Level.SEVERE, null, ex);
        }


        return flimImage;
    }

    @Override
    public DatasetTimp loadFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
