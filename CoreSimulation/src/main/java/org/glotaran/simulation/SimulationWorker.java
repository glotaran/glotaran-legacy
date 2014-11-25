package org.glotaran.simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import org.glotaran.core.interfaces.TimpControllerInterface;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.gta.GtaModelReference;
import org.glotaran.core.models.gta.GtaOutput;
import org.glotaran.core.models.gta.GtaSimulationContainer;

import org.glotaran.core.models.sim.SpectralModelSpecification;
import org.glotaran.core.models.tgm.Tgm;
import org.glotaran.hdf5interface.Hdf5DatasetTimp;
import org.glotaran.simfilesupport.spec.SpectralModelDataObject;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author jsg210
 */
public class SimulationWorker implements Runnable {

    private DatasetTimp[] results = null;
    private GtaOutput output;
    private GtaModelReference modelReference;
    private GtaSimulationContainer simContainer;
    private FileObject resultsfolder;
    private TGProject project;
    private TimpControllerInterface timpcontroller;
    private ArrayList<String> modelCalls = new ArrayList<String>();
    private String fitModelCall;
    public ProgressHandle ph;

    public SimulationWorker(TGProject currentProject,
            GtaOutput gtaOutput,
            GtaSimulationContainer simContainer,
            GtaModelReference gtaModelReference,
            ProgressHandle progressHandle) {
        this.output = gtaOutput;
        this.modelReference = gtaModelReference;
        this.simContainer = simContainer;
        this.project = currentProject;
        this.ph = progressHandle;
    }

    public void run() {
        try {
            ph.start();
            ph.switchToIndeterminate();
            doSimulation();
            Thread.sleep(0);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void doSimulation() {
        if (project != null) {
           if (output.getOutputPath() == null||output.getOutputPath().isEmpty()) {
                String outputPath = project.getResultsFolder(true).getPath() + File.separator +
                                               "simulation results";
                File outputFolder = new File(outputPath);
                if (outputFolder.exists()) {
                    resultsfolder = FileUtil.toFileObject(outputFolder);
                    if (resultsfolder.getChildren().length > 0) {
                        try {
                            resultsfolder = FileUtil.createFolder(resultsfolder.getParent(),
                                    FileUtil.findFreeFolderName(resultsfolder.getParent(), "simulation results"));
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                } else {
                    try {
                        FileUtil.createFolder(outputFolder);
                        resultsfolder = outputFolder.exists() ? FileUtil.toFileObject(outputFolder) : null;
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
           }
           else{
               String outputPath = project.getResultsFolder(true).getPath() + File.separator +output.getOutputPath();
                File outputFolder = new File(outputPath);
                if (outputFolder.exists()) {
                    resultsfolder = FileUtil.toFileObject(outputFolder);
                    if (resultsfolder.getChildren().length > 0) {
                        try {
                            resultsfolder = FileUtil.createFolder(resultsfolder,
                                    FileUtil.findFreeFolderName(resultsfolder, "simulation results"));
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                } else {
                    try {
                        FileUtil.createFolder(outputFolder);
                        resultsfolder = outputFolder.exists() ? FileUtil.toFileObject(outputFolder) : null;
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
           }
           
                fitModelCall = getFitModelCall(InitSimModel.parseModel(getModel(modelReference), getSimModel(simContainer)));
                timpcontroller = Lookup.getDefault().lookup(TimpControllerInterface.class);
                if (timpcontroller != null) {
                    results = timpcontroller.runSimulation(modelCalls, fitModelCall);
                }
                if (results != null) {
                    writeSimResults(results, modelReference);
                try {
                    writeSummary(resultsfolder, FileUtil.findFreeFileName(resultsfolder, resultsfolder.getName(), "summary"));
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

                }
                else {
                    try {
                        writeSummary(resultsfolder, FileUtil.findFreeFileName(resultsfolder, resultsfolder.getName() + "_errorlog", "summary"));
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                timpcontroller.cleanup();
            }
       // }
    }

    private void writeSummary(FileObject resultsfolder, String freeFilename) throws IOException {
        FileObject writeTo = resultsfolder.createData(freeFilename, "summary");
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(FileUtil.toFile(writeTo)));
        //TODO: Complete the summary here:
        outputWriter.append("Summary");
        outputWriter.newLine();
        outputWriter.newLine();
        outputWriter.append("R Call for the TIMP function \"simndecay_gen\": ");
        outputWriter.newLine();
        outputWriter.write(fitModelCall);
        outputWriter.newLine();
        outputWriter.newLine();

        if (results != null) {

            outputWriter.append("Number of time steps: ");
            outputWriter.append(String.valueOf(results[0].getNt()));
            outputWriter.newLine();
            outputWriter.append("Number of wavelength steps: ");
            outputWriter.append(String.valueOf(results[0].getNl()));
            outputWriter.newLine();
         }
         else {
            outputWriter.newLine();
            outputWriter.append("Error: The simulation did not return valid results.");
            outputWriter.newLine();
            outputWriter.append("Try again with different parameters.");
        }
        outputWriter.close();
    }
    private Tgm getModel(GtaModelReference gtaModelReference) {
        TgmDataObject tgmDO = null;
        Tgm model = null;
        String path = project.getProjectDirectory().getPath() + File.separator + gtaModelReference.getPath();
        try {
            File datasetF = new File(path);
            FileObject datasetFO = FileUtil.createData(datasetF);
            DataObject datasetDO = DataObject.find(datasetFO);
            if (datasetDO != null) {
                tgmDO = (TgmDataObject) datasetDO;
                model = tgmDO.getTgm();
            }
        } catch (DataObjectExistsException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return model;
    }

    private SpectralModelSpecification getSimModel(GtaSimulationContainer simContainerReference) {
        SpectralModelDataObject spmDO = null;
        SpectralModelSpecification model = null;
        String path = project.getProjectDirectory().getPath() + File.separator + simContainerReference.getSimulationInputRef().get(0).getPath();
        try {
            File datasetF = new File(path);
            FileObject datasetFO = FileUtil.createData(datasetF);
            DataObject datasetDO = DataObject.find(datasetFO);
            if (datasetDO != null) {
                spmDO = (SpectralModelDataObject) datasetDO;
                model = spmDO.getSim();
            }
        } catch (DataObjectExistsException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return model;
    }
    private void writeSimResults(DatasetTimp[] results, GtaModelReference modelReference) {
        FileObject resultfolder = project.getResultsFolder(true);
        //cachefolder = cachefolder.getFileObject(dObj).;//.getCacheFolderName().toString()
        FileObject writeTo;
        try {
            String freeFilename = FileUtil.findFreeFileName(resultsfolder, results[0].getDatasetName(), "timpdataset");
            results[0].setDatasetName(freeFilename);
            writeTo = resultsfolder.createData(freeFilename, "timpdataset");
            Hdf5DatasetTimp.save(FileUtil.toFile(writeTo), results[0]);
           }
        catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    private String getFitModelCall(String initModelString) {
        return TimpControllerInterface.NAME_OF_SIM_OBJECT + " <- simndecay_gen(" + initModelString + ")";
    }
}




