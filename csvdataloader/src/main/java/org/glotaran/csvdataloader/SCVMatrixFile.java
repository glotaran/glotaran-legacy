/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.csvdataloader;

import org.openide.util.NbBundle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glotaran.core.models.structures.DatasetTimp;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.FileFormat;
import org.ujmp.core.exceptions.MatrixException;
import static java.lang.Math.floor;
import org.glotaran.core.interfaces.GlotaranDataloaderInterface;

/**
 *
 * @author Owner
 */
public class SCVMatrixFile implements GlotaranDataloaderInterface {

    private String filetype = "spec";

    @Override
    public ArrayList<String> getExtensions() {
        ArrayList<String> supportedExtensions = new ArrayList<>();
        supportedExtensions.add("csv");
        supportedExtensions.add("txt");
        supportedExtensions.add("dat");
        return supportedExtensions;
    }

    @Override
    public String getExtention() {
        return getExtensions().get(0);
    }

    @Override
    public String getFilterString() {
        return ".csv;.txt;.dat delimited separated matrix file";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        return filetype;
    }

    @Override
    public boolean Validator(File file) {
        String ext = FileUtil.getExtension(file.getName());
        if (getExtensions().contains(ext.toLowerCase())) {
            try {
                //MatrixFactory.importFromFile(FileFormat.CSV, file);
                return true;
            } catch (MatrixException ex) { //| IOException
                return false;
            }
        }
        return false;
    }

    private boolean checkFilterRow(int i, CSVLoaderDialog editorPanel, Matrix unfilteredDataMatrix) {
        if (editorPanel.isLabelsInRows()) {
            if (unfilteredDataMatrix.getColumnCount() > 1) {
                if (!Double.isNaN(unfilteredDataMatrix.getAsDouble(i, 1))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public org.glotaran.core.models.structures.DatasetTimp loadFile(File file) throws FileNotFoundException {
        DatasetTimp dataset = null;
        Matrix rawDataMatrix = null;
        Matrix unfilteredDataMatrix = null;
        Matrix dataMatrix = null;
        CSVLoaderDialog editorPanel;
        try {
            editorPanel = new CSVLoaderDialog(file);
            NotifyDescriptor cellEditor = new NotifyDescriptor(
                    editorPanel,
                    "table",
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.PLAIN_MESSAGE,
                    null,
                    NotifyDescriptor.CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(cellEditor).equals(NotifyDescriptor.OK_OPTION)) {
                if (editorPanel.getDtaMatrix() == null) {
//                    dataMatrix = MatrixFactory.importFromFile(FileFormat.CSV, file);
                    rawDataMatrix = MatrixFactory.importFromFile(FileFormat.CSV, file, editorPanel.getDelimiterString());
                    unfilteredDataMatrix = rawDataMatrix.subMatrix(Ret.LINK, editorPanel.getSkipRows(), editorPanel.getSkipColums(), rawDataMatrix.getRowCount() - 1, rawDataMatrix.getColumnCount() - 1);
                } else {
                    unfilteredDataMatrix = editorPanel.getDtaMatrix();
                }

                if (editorPanel.getAutoSkip()) {
                    ArrayList<Integer> rowsToFilter = new ArrayList<>();
                    for (int i = 0; i < unfilteredDataMatrix.getRowCount(); i++) {
                        //System.out.println(unfilteredDataMatrix.getAsDouble(i, 0));
                        if (Double.isNaN(unfilteredDataMatrix.getAsDouble(i, 0))) {
                            if (checkFilterRow(i, editorPanel, unfilteredDataMatrix)) {
                                rowsToFilter.add(i);
                            }
                        } else {
                            break;
                        }
                    }
                    for (int i = 0; i < unfilteredDataMatrix.getRowCount(); i++) {
                        //System.out.println(unfilteredDataMatrix.getAsDouble((unfilteredDataMatrix.getRowCount() - 1 - i), 0));
                        if (Double.isNaN(unfilteredDataMatrix.getAsDouble((unfilteredDataMatrix.getRowCount() - 1 - i), 0))) {
                            if (checkFilterRow((int) (unfilteredDataMatrix.getRowCount() - 1 - i), editorPanel, unfilteredDataMatrix)) {
                                rowsToFilter.add((int) (unfilteredDataMatrix.getRowCount() - 1 - i));
                            }
                        } else {
                            break;
                        }
                    }
                    dataMatrix = unfilteredDataMatrix.deleteRows(Ret.LINK, rowsToFilter);
                } else {
                    dataMatrix = unfilteredDataMatrix;
                }

                dataset = new DatasetTimp();
                if (editorPanel.isSingleMatrix()) {
//load single matrix from file  
                    filetype = "spec";
                    dataset.setType("spec");
                    dataset.setDatasetName(file.getName());

                    if (editorPanel.isSpectraInRows()) {
                        dataMatrix = dataMatrix.transpose();
                    }
//                    if (editorPanel.isLabelsInColums()) {
//                        if (editorPanel.isLabelsInRows()) {
                    dataset.setNl((int) (dataMatrix.getRowCount() - (editorPanel.isLabelsInColums() ? 1 : 0)));
                    dataset.setNt((int) (dataMatrix.getColumnCount() - (editorPanel.isLabelsInRows() ? 1 : 0)));
                    dataset.setX2(new double[dataset.getNl()]);
                    dataset.setX(new double[dataset.getNt()]);
                    for (int i = 0; i < dataset.getNt(); i++) {
                        dataset.getX()[i] = editorPanel.isLabelsInColums() ? dataMatrix.getAsDouble(0, i + (editorPanel.isLabelsInRows() ? 1 : 0)) : i;
                    }
                    for (int i = 0; i < dataset.getNl(); i++) {
                        dataset.getX2()[i] = editorPanel.isLabelsInRows() ? dataMatrix.getAsDouble(i + (editorPanel.isLabelsInColums() ? 1 : 0), 0) : i;
                    }
                    dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
                    for (int j = 0; j < dataset.getNl(); j++) {
                        for (int i = 0; i < dataset.getNt(); i++) {
                            dataset.getPsisim()[j * dataset.getNt() + i] = dataMatrix.getAsDouble(j + (editorPanel.isLabelsInColums() ? 1 : 0), i + (editorPanel.isLabelsInRows() ? 1 : 0));
                        }
                    }
//                        } else { //labels in colums but not in rows
//                            dataset.setNl((int) dataMatrix.getRowCount() - 1);
//                            dataset.setNt((int) dataMatrix.getColumnCount());
//                            dataset.setX2(new double[dataset.getNl()]);
//                            dataset.setX(new double[dataset.getNt()]);
//                            for (int i = 0; i < dataset.getNt(); i++) {
//                                dataset.getX()[i] = dataMatrix.getAsDouble(0, i);
//                            }
//                            for (int i = 0; i < dataset.getNl(); i++) {
//                                dataset.getX2()[i] = i;
//                            }
//                            dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
//                            for (int j = 0; j < dataset.getNl(); j++) {
//                                for (int i = 0; i < dataset.getNt(); i++) {
//                                    dataset.getPsisim()[j * dataset.getNt() + i] = dataMatrix.getAsDouble(j + 1, i);
//                                }
//                            }
//                        }
//                    } else { //labels in rows but not in columns
//                        if (editorPanel.isLabelsInRows()) {
//                            dataset.setNl((int) dataMatrix.getRowCount());
//                            dataset.setNt((int) dataMatrix.getColumnCount() - 1);
//                            dataset.setX2(new double[dataset.getNl()]);
//                            dataset.setX(new double[dataset.getNt()]);
//                            for (int i = 0; i < dataset.getNt(); i++) {
//                                dataset.getX()[i] = i;
//                            }
//                            for (int i = 0; i < dataset.getNl(); i++) {
//                                dataset.getX2()[i] = dataMatrix.getAsDouble(i, 0);
//                            }
//                            dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
//                            for (int j = 0; j < dataset.getNl(); j++) {
//                                for (int i = 0; i < dataset.getNt(); i++) {
//                                    dataset.getPsisim()[j * dataset.getNt() + i] = dataMatrix.getAsDouble(j, i + 1);
//                                }
//                            }
//                        } else {
//                            dataset.setNl((int) dataMatrix.getRowCount());
//                            dataset.setNt((int) dataMatrix.getColumnCount());
//                            dataset.setX2(new double[dataset.getNl()]);
//                            dataset.setX(new double[dataset.getNt()]);
//                            for (int i = 0; i < dataset.getNt(); i++) {
//                                dataset.getX()[i] = i;
//                            }
//                            for (int i = 0; i < dataset.getNl(); i++) {
//                                dataset.getX2()[i] = i;
//                            }
//                            dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
//                            for (int j = 0; j < dataset.getNl(); j++) {
//                                for (int i = 0; i < dataset.getNt(); i++) {
//                                    dataset.getPsisim()[j * dataset.getNt() + i] = dataMatrix.getAsDouble(j, i);
//                                }
//                            }
//                        }
//                    }

                    if (editorPanel.isWaveCalbrationEnabled()) {
                        ArrayList<Double> calibration = new ArrayList<Double>();
                        if (!editorPanel.getFilename().isEmpty()) {
                            Scanner sc = new Scanner(new File(editorPanel.getFilename()));
                            while (sc.hasNext()) {
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
                            int overlapStartindex = 0;
                            int i = 1;
                            double splitValue = 0;

                            if (calibration.get(1) > calibration.get(0)) {
                                while ((i < calibration.size()) && (calibration.get(i) > calibration.get(i - 1))) {
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
                            } else {
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
                    dataset.calcRangeInt();
                }

                if (editorPanel.isLifetimeDensityMap()) {
//load and generate data from lifetimedensitymap                    
                    // set number of reconstructed timepoints
                    filetype = "spec";
                    dataset.setType("spec");
                    int numberOfTimepoints = editorPanel.getTimepoints();
                    double fromValue = editorPanel.getFrom();
                    double toValue = editorPanel.getTo();
                    double logFromValue = editorPanel.getLogFrom();
                    double linFracValue = editorPanel.getLinearFraction();
                    // set number of recorded wavelengths
                    dataset.setNl((int) dataMatrix.getRowCount() - 1);
                    dataset.setX2(new double[dataset.getNl()]);
                    double excitationWavelength = dataMatrix.getAsDouble(0, 0);
                    double[] exponents = new double[((int) dataMatrix.getColumnCount() - 1)];
                    for (int i = 0; i < exponents.length; i++) {
                        exponents[i] = dataMatrix.getAsDouble(0, i + 1);
                    }
                    for (int i = 0; i < dataset.getNl(); i++) {
                        dataset.getX2()[i] = dataMatrix.getAsDouble(i + 1, 0);
                    }
                    // set number of timepoint based on desired time resolution

                    dataset.setNt(numberOfTimepoints + 1);
                    dataset.setX(new double[dataset.getNt()]);
                    if (editorPanel.isLinLogEnabeled()) {
                        int linpoints, logpoints;
                        if (logFromValue > fromValue) {
                            linFracValue = Math.abs(linFracValue) < 1 ? Math.abs(linFracValue) : 1;
                            linpoints = (int) Math.floor(Math.abs(numberOfTimepoints * linFracValue));
                            logpoints = numberOfTimepoints - linpoints;
                            //double linstep = (logFromValue-fromValue)/(Math.floor(numberOfTimepoints*linFracValue))
                            for (int i = 0; i < linpoints; i++) {
                                dataset.getX()[i] = (logFromValue - fromValue) / linpoints * i;
                            }
                            for (int i = 0; i <= logpoints; i++) {
                                dataset.getX()[i + linpoints] = Math.exp(Math.log(logFromValue) + (Math.log(toValue) - Math.log(logFromValue)) / logpoints * i);
                            }
                        } else {
                            for (int i = 0; i <= numberOfTimepoints; i++) {
                                dataset.getX()[i] = Math.exp(Math.log(fromValue) + (Math.log(toValue) - Math.log(fromValue)) / numberOfTimepoints * i);
                            }
                        }
                    } else {

                        for (int i = 0; i <= numberOfTimepoints; i++) {
                            dataset.getX()[i] = (toValue - fromValue) / numberOfTimepoints * i;
                        }
                    }

                    //for every wavelength (row) calculate, for every timepoint
                    //pre_exponential_amplitudes[k]*Exp[-1/Exp[exponential_amplitude[k]] * t]
                    double[] timepoints = dataset.getX();
                    dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
                    for (int j = 0; j < dataset.getNl(); j++) {
                        for (int i = 0; i < dataset.getNt(); i++) {
                            for (int k = 0; k < exponents.length; k++) {
                                double mult = (k == (exponents.length - 1)) ? editorPanel.getLastLifetimeMult() : 1;
                                dataset.getPsisim()[j * dataset.getNt() + i] = dataset.getPsisim()[j * dataset.getNt() + i]
                                        + dataMatrix.getAsDouble(j + 1, k + 1) * Math.exp(-1 / (mult * Math.pow(10, exponents[k])) * timepoints[i]);
                            }

                        }
                    }

                }
            }

            if (editorPanel.isTimeGatedMattrix()) {
                filetype = "multispec";
//load data from timegated matrises assuming each matrix have labels and timesteps set in the corner
                long ySize = 1;
                long zSize = 1;
                long xSize = dataMatrix.getColumnCount();
                int index;
                while (ySize < dataMatrix.getRowCount()) {
                    index = 1;
                    while (index < xSize && dataMatrix.getAsDouble(ySize, index) == dataMatrix.getAsDouble(0, index)) {
                        index++;
                    }
                    if (index == xSize) {
                        break;
                    }
                    ySize++;
                }
                if (ySize < dataMatrix.getRowCount()) {
                    zSize = dataMatrix.getRowCount() / ySize;
                }
//                dataMatrix=dataMatrix.reshape(Ret.NEW, ySize, zSize, xSize);

                dataset.setDatasetName(file.getName());
                dataset.setNt((int) zSize);
                dataset.setNl((int) ((int) (xSize - 1) * (ySize - 1)));
                dataset.setOrigHeigh((int) (ySize - 1));
                dataset.setOrigWidth((int) (xSize - 1));
                dataset.setX2(new double[dataset.getNl()]);
                dataset.setIntenceImX(new double[dataset.getOriginalHeight()]);
                dataset.setIntenceImY(new double[dataset.getOriginalWidth()]);
                dataset.setX(new double[dataset.getNt()]);
                dataset.setType("multispec");

                for (int i = 0; i < dataset.getNt(); i++) {
                    dataset.getX()[i] = dataMatrix.getAsDouble(i * ySize, 0);
                }
                for (int i = 0; i < dataset.getOriginalWidth(); i++) {
                    dataset.getIntenceImY()[i] = dataMatrix.getAsDouble(0, i + 1);
                }

                for (int i = 0; i < dataset.getOriginalHeight(); i++) {
                    dataset.getIntenceImX()[i] = dataMatrix.getAsDouble(i + 1, 0);
                }

                dataset.setPsisim(new double[dataset.getNl() * dataset.getNt()]);
                int counter = 0;
                for (int j = 0; j < dataset.getOriginalHeight(); j++) {
                    for (int i = 0; i < dataset.getOriginalWidth(); i++) {
                        for (int k = 0; k < dataset.getNt(); k++) {
                            dataset.getPsisim()[(j * dataset.getOriginalWidth() + i) * dataset.getNt() + k] = dataMatrix.getAsDouble(1 + j + k * ySize, i + 1);
                        }
                        dataset.getX2()[j * dataset.getOriginalWidth() + i] = counter;
                        counter++;
                    }
                }
                dataset.buildIntMap(0);

            }

        } catch (MatrixException ex) {
            Logger.getLogger(SCVMatrixFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SCVMatrixFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataset;
    }

    @Override
    public org.glotaran.core.models.structures.FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private double[] updateDatamatrix(DatasetTimp dataset, int overlapStartindex, int splitIndex) {
        int newNl = (dataset.getNl() - splitIndex + overlapStartindex);
        double[] newPsiSim = new double[dataset.getNt() * newNl];
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

        if (scalevector.size() > 0) {
            Collections.sort(scalevector);
            if (scalevector.size() % 2 == 1) {
                scale = scalevector.get((scalevector.size() - 1) / 2);
            } else {
                scale = (scalevector.get((scalevector.size()) / 2 - 1) + scalevector.get((scalevector.size()) / 2)) / 2;
            }
        }

        for (int i = 0; i < dataset.getNt(); i++) {
            for (int j = 0; j < overlapStartindex; j++) {
                newPsiSim[j * dataset.getNt() + i] = dataset.getPsisim()[j * dataset.getNt() + i];
            }
            for (int j = 0; j < newNl - overlapStartindex; j++) {
                newPsiSim[(j + overlapStartindex) * dataset.getNt() + i] = dataset.getPsisim()[(j + splitIndex) * dataset.getNt() + i] * scale;
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
