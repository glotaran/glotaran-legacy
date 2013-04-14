/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.csvdataloader;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.FileFormat;
import org.ujmp.core.exceptions.MatrixException;
import org.ujmp.core.stringmatrix.impl.CSVMatrix;
import org.ujmp.gui.MatrixGUIObject;
import org.ujmp.gui.panels.MatrixTableEditorPanel;
import static java.lang.Math.floor;

/**
 *
 * @author Owner
 */
public class SCVMatrixFile implements TGDatasetInterface {

    @Override
    public String getExtention() {
        return "csv";
    }

    @Override
    public String getFilterString() {
        return ".csv tab space or comma separated matrix";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        return "spec";
    }

    @Override
    public boolean Validator(File file) {
        String ext = FileUtil.getExtension(file.getName());
        if (ext.equalsIgnoreCase("csv")) {
            try {
                MatrixFactory.importFromFile(FileFormat.CSV, file);
                return true;
            } catch (MatrixException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }
        return false;
    }

    @Override
    public org.glotaran.core.models.structures.DatasetTimp loadFile(File file) throws FileNotFoundException {
        DatasetTimp dataset = null;
        Matrix dataMatrix;
        CSVLoaderDialog editorPanel;
        try {
            dataMatrix = MatrixFactory.importFromFile(FileFormat.CSV, file);
            editorPanel = new CSVLoaderDialog(new MatrixTableEditorPanel(new MatrixGUIObject(dataMatrix)));
            NotifyDescriptor cellEditor = new NotifyDescriptor(
                editorPanel,
                "table",
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(cellEditor).equals(NotifyDescriptor.OK_OPTION)) {
                if ((editorPanel.getSkipRows()>0)||(editorPanel.getSkipColums()>0)){
                    dataMatrix = dataMatrix.subMatrix(Ret.NEW, editorPanel.getSkipRows(), editorPanel.getSkipRows(), dataMatrix.getRowCount()-1, dataMatrix.getColumnCount()-1);    
                }
                dataset = new DatasetTimp();
                dataset.setDatasetName(file.getName());
                if (editorPanel.isSpectraInRows()) {
                    dataMatrix = dataMatrix.transpose();
                }
                if (editorPanel.isLabelsInColums()) {
                    if (editorPanel.isLabelsInRows()) {
                        dataset.setNl((int) dataMatrix.getRowCount() - 1);
                        dataset.setNt((int) dataMatrix.getColumnCount() - 1);
                        dataset.setX2(new double[dataset.getNl()]);
                        dataset.setX(new double[dataset.getNt()]);
                        for (int i = 0; i < dataset.getNt(); i++) {
                            dataset.getX()[i] = dataMatrix.getAsDouble(0, i + 1);
                        }
                        for (int i = 0; i < dataset.getNl(); i++) {
                            dataset.getX2()[i] = dataMatrix.getAsDouble(i + 1, 0);
                        }
                        dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
                        for (int j = 0; j < dataset.getNl(); j++) {
                            for (int i = 0; i < dataset.getNt(); i++) {
                                dataset.getPsisim()[j * dataset.getNt() + i] = dataMatrix.getAsDouble(j + 1, i + 1);
                            }
                        }
                    } else { //labels in colums but not in rows
                        dataset.setNl((int) dataMatrix.getRowCount() - 1);
                        dataset.setNt((int) dataMatrix.getColumnCount());
                        dataset.setX2(new double[dataset.getNl()]);
                        dataset.setX(new double[dataset.getNt()]);
                        for (int i = 0; i < dataset.getNt(); i++) {
                            dataset.getX()[i] = dataMatrix.getAsDouble(0, i);
                        }
                        for (int i = 0; i < dataset.getNl(); i++) {
                            dataset.getX2()[i] = i;
                        }
                        dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
                        for (int j = 0; j < dataset.getNl(); j++) {
                            for (int i = 0; i < dataset.getNt(); i++) {
                                dataset.getPsisim()[j * dataset.getNt() + i] = dataMatrix.getAsDouble(j + 1, i);
                            }
                        }
                    }
                } else { //labels in rows but not in columns
                    if (editorPanel.isLabelsInRows()) {
                        dataset.setNl((int) dataMatrix.getRowCount());
                        dataset.setNt((int) dataMatrix.getColumnCount() - 1);
                        dataset.setX2(new double[dataset.getNl()]);
                        dataset.setX(new double[dataset.getNt()]);
                        for (int i = 0; i < dataset.getNt(); i++) {
                            dataset.getX()[i] = i;
                        }
                        for (int i = 0; i < dataset.getNl(); i++) {
                            dataset.getX2()[i] = dataMatrix.getAsDouble(i, 0);
                        }
                        dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
                        for (int j = 0; j < dataset.getNl(); j++) {
                            for (int i = 0; i < dataset.getNt(); i++) {
                                dataset.getPsisim()[j * dataset.getNt() + i] = dataMatrix.getAsDouble(j, i + 1);
                            }
                        }
                    } else {
                        dataset.setNl((int) dataMatrix.getRowCount());
                        dataset.setNt((int) dataMatrix.getColumnCount());
                        dataset.setX2(new double[dataset.getNl()]);
                        dataset.setX(new double[dataset.getNt()]);
                        for (int i = 0; i < dataset.getNt(); i++) {
                            dataset.getX()[i] = i;
                        }
                        for (int i = 0; i < dataset.getNl(); i++) {
                            dataset.getX2()[i] = i;
                        }
                        dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
                        for (int j = 0; j < dataset.getNl(); j++) {
                            for (int i = 0; i < dataset.getNt(); i++) {
                                dataset.getPsisim()[j * dataset.getNt() + i] = dataMatrix.getAsDouble(j, i);
                            }
                        }
                    }
                }
                
                if (editorPanel.isWaveCalbrationEnabled()){
                    ArrayList<Double> calibration = new ArrayList<Double>();
                    if (!editorPanel.getFilename().isEmpty()){
                        Scanner sc = new Scanner(new File(editorPanel.getFilename()));
                        while (sc.hasNext()){
                            calibration.add(Double.parseDouble(sc.next()));
                        }
                        
                        if (calibration.size() != dataset.getNl()) {
                            NotifyDescriptor.Message infoMessage = new NotifyDescriptor.Message(
                                    NbBundle.getBundle("org/glotaran/csvdataloader/Bundle").getString("wrongCalibrationSize"),
                                    NotifyDescriptor.INFORMATION_MESSAGE);
                            DialogDisplayer.getDefault().notify(infoMessage);
                            return dataset;
                        }
                                
                        int splitIndex = 0;
                        int overlapStartindex=0; 
                        int i = 1;   
                        double splitValue = 0;
                        
                        if (calibration.get(1) > calibration.get(0)) {
                            while ((i < calibration.size()) &&(calibration.get(i) > calibration.get(i - 1))) {
                                i++;
                            }
                            if (i < calibration.size() - 1) {
                                splitIndex = i;
                                splitValue = calibration.get(i);
                                i--;
                                while (splitValue < calibration.get(i)) {
                                    i--;
                                }
                                overlapStartindex = (int) (i + floor((splitIndex - i) * 0.5));
                                splitIndex = (int) (splitIndex + floor((splitIndex - i) * 0.5));
                            }  
                            dataset.setPsisim(updateDatamatrix(dataset, overlapStartindex, splitIndex));
                            dataset.setNl((dataset.getNl() - splitIndex + overlapStartindex));
                        } 
                        else {
                            while ((calibration.get(i) < calibration.get(i - 1)) && (i < calibration.size())) {
                                i++;
                            }
                            if (i < calibration.size() - 1) {
                                splitIndex = i;
                                splitValue = calibration.get(i);
                                i--;
                                while (splitValue > calibration.get(i)) {
                                    i--;
                                }
                                overlapStartindex = (int) (i + floor((splitIndex - i) * 0.25));
                                splitIndex = (int) (splitIndex + floor((splitIndex - i) * 0.25));
                                dataset.setPsisim(updateDatamatrix(dataset, overlapStartindex, splitIndex));
                                dataset.setNl((dataset.getNl() - splitIndex + overlapStartindex));
                            }
                        }
                        dataset.setX2(updateWaves(calibration, overlapStartindex, splitIndex));
                    }
                }
            }
        } catch (MatrixException ex) {
            Logger.getLogger(SCVMatrixFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SCVMatrixFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataset.calcRangeInt();
        return dataset;
    }

    @Override
    public org.glotaran.core.models.structures.FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private double[] updateDatamatrix(DatasetTimp dataset, int overlapStartindex, int splitIndex) {
        int newNl = (dataset.getNl()-splitIndex+overlapStartindex);
        double[] newPsiSim = new double[dataset.getNt()*newNl]; 
        double scale = 0;
        double temp;
        ArrayList<Double> scalevector = new ArrayList<Double>(); 
        //calculate ratio of timetracess in overlapStartindex and splitIndex   
        for (int j = 1; j < dataset.getNt(); j++) {
            if ((dataset.getPsisim()[(overlapStartindex) * dataset.getNt() + j] != 0) && (dataset.getPsisim()[(splitIndex) * dataset.getNt() + j] != 0)) {
                temp = dataset.getPsisim()[(overlapStartindex) * dataset.getNt() + j] / dataset.getPsisim()[(splitIndex) * dataset.getNt() + j];
                if (temp > 0) {
                    scalevector.add(temp);
                }
            }
        }

        if (scalevector.size()>0){
            Collections.sort(scalevector); 
            if (scalevector.size() % 2 == 1){
                scale = scalevector.get((scalevector.size()-1)/2);
            } 
            else {
                scale = (scalevector.get((scalevector.size())/2-1)+scalevector.get((scalevector.size())/2))/2;
            }
        }

        for (int i = 0; i < dataset.getNt(); i++){
            for (int j = 0; j < overlapStartindex; j++){
                newPsiSim[j*dataset.getNt()+i] = dataset.getPsisim()[j*dataset.getNt()+i];
            }
            for (int j = 0; j < newNl - overlapStartindex; j++){
                newPsiSim[(j+overlapStartindex)*dataset.getNt()+i] = dataset.getPsisim()[(j+splitIndex)*dataset.getNt()+i]*scale;
            }
        } 
        return newPsiSim;
    }
    
    
    private double[] updateWaves(ArrayList<Double> calibration, int overlapStartindex, int splitIndex) {
        double[] newX2 = new double[calibration.size() - splitIndex + overlapStartindex];
        for (int j = 0; j < overlapStartindex; j++) {
            newX2[j] = calibration.get(j);
        }
        for (int j = 0; j < calibration.size() - splitIndex; j++) {
            newX2[j + overlapStartindex] = calibration.get(j + splitIndex);
        }
        return newX2;
    }
    
    
}
