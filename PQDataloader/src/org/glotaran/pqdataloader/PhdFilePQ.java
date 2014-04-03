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
import java.nio.ByteBuffer;
import org.glotaran.pqdataloader.pqstructures.CurveHdr;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Sergey
 */
//@ServiceProvider(service=org.glotaran.core.interfaces.TGDatasetInterface.class)
public class PhdFilePQ implements TGDatasetInterface {

    @Override
    public String getExtention() {
        return "phd";
    }

    @Override
    public String getFilterString() {
        return ".phd PQ PicoHarp FLIM Image";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        return "spec";
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
                    if (binHead1.MeasMode == 0) {
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
    public DatasetTimp loadFile(File file) throws FileNotFoundException {
        DatasetTimp dataset = new DatasetTimp();
        ImageInputStream f = new FileImageInputStream(new RandomAccessFile(file, "r"));
        f.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        TxtHdr header = new TxtHdr();
        BinHdr1 binHead1 = new BinHdr1();
        BinHdr2 binHead2 = new BinHdr2();
        //TTTRHdr ttHeader = new TTTRHdr();
        CurveHdr[] curves;
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

            if (binHead1.MeasMode == 0 && binHead1.BitsPerRecord == 32) {
                curves = new CurveHdr[binHead1.NumberOfCurves];
                int numberOfTRES = 0,numberOfOSC =0, numberOfINT=0;
                int totalChannels = 0, maxChannels = 0;
                for (int i = 0; i < binHead1.NumberOfCurves; i++) {
                    curves[i] = new CurveHdr();
                    curves[i].fread(f);
                    //f.readLong();
                    if (curves[i].SubMode == 0) {//0 means OSC
                        numberOfOSC++;
                        totalChannels += curves[i].Channels;
                        maxChannels = Math.max(curves[i].Channels, maxChannels);
                    }
                    if (curves[i].SubMode == 1) {//1 means INT
                        numberOfINT++;
                        totalChannels += curves[i].Channels;
                        maxChannels = Math.max(curves[i].Channels, maxChannels);
                    }
                    if (curves[i].SubMode == 2) { //2 means TRES
                        numberOfTRES++;
                        totalChannels += curves[i].Channels;
                        maxChannels = Math.max(curves[i].Channels, maxChannels);
                    }
                }

                dataset.setDatasetName(file.getName());
                dataset.setNl(binHead1.NumberOfCurves - 1);
                dataset.setNt(maxChannels);
                dataset.setType("spec");
                //The 0the curve (if measured) is always the IRF in a TRES measurement
                dataset.setPsisim(new double[maxChannels * (binHead1.NumberOfCurves - 1)]); //perhaps change totalChannels to totalBins if data is set to be read in binned
                dataset.setX(new double[dataset.getNt()]); //timepoints
                dataset.setX2(new double[dataset.getNl()]); //wavelengths
                for (int i = 1; i < binHead1.NumberOfCurves; i++) { //skip IRF (curve=0)
                    f.seek(curves[i].DataOffset);
                    for (int j = 0; j < curves[i].Channels; j++) {
                        dataset.getPsisim()[j + (i - 1) * maxChannels] = f.readUnsignedInt();
                        dataset.getX()[j] = curves[i].Resolution * j;
                    }
                    dataset.getX2()[i - 1] = (double) curves[i].P1;

                }

                dataset.calcRangeInt();
            }

        } catch (IOException ex) {
            Logger.getLogger(PhdFilePQ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PhdFilePQ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(PhdFilePQ.class.getName()).log(Level.SEVERE, null, ex);
        }


        return dataset;
    }

    @Override
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
