/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.analysis;

import java.util.List;
import org.glotaran.core.models.tgm.IrfparPanelModel;
import org.glotaran.core.models.tgm.KinparPanelModel;
import org.glotaran.core.models.tgm.KMatrixPanelModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Formatter;
import org.glotaran.analysis.support.InitModel;
import org.glotaran.core.interfaces.TimpControllerInterface;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.gta.GtaChangesModel;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.core.models.gta.GtaDataset;
import org.glotaran.core.models.gta.GtaDatasetContainer;
import org.glotaran.core.models.gta.GtaLinkCLP;
import org.glotaran.core.models.gta.GtaModelDiffContainer;
import org.glotaran.core.models.gta.GtaModelDiffDO;
import org.glotaran.core.models.gta.GtaModelDifferences;
import org.glotaran.core.models.gta.GtaModelReference;
import org.glotaran.core.models.gta.GtaOutput;
import org.glotaran.core.models.results.Dataset;
import org.glotaran.core.models.results.DatasetRelation;
import org.glotaran.core.models.results.GtaResult;
import org.glotaran.core.models.results.NlsProgress;
import org.glotaran.core.models.results.OutputFile;
import org.glotaran.core.models.results.Summary;
import org.glotaran.core.models.tgm.Tgm;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import static java.lang.Math.floor;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.glotaran.hdf5interface.Hdf5TimpResultDataset;

/**
 *
 * @author jsg210
 */
public class AnalysisWorker implements Runnable {

    private int NO_OF_ITERATIONS = 0;
    private DatasetTimp[] datasets;
    private TimpResultDataset[] results = null;
    private GtaOutput output;
    private GtaDatasetContainer datasetContainer;
    private GtaModelReference modelReference;
    private GtaModelDifferences modelDifferences;
    private FileObject resultsfolder;
    private FileObject writeTo;
    private TGProject project;
    private TimpControllerInterface timpcontroller;
    private ArrayList<String> modelCalls = new ArrayList<String>();
    private String fitModelCall;
    private int numIterations;
    private ArrayList<Double[]> relationsList = new ArrayList<Double[]>();
    public ProgressHandle ph;
    private Tgm tgm;

    public AnalysisWorker(TGProject currentProject, GtaOutput gtaOutput, GtaDatasetContainer gtaDatasetContainer, GtaModelReference gtaModelReference, GtaModelDifferences gtaModelDifferences, ProgressHandle progressHandle) {
        this.output = gtaOutput;
        this.datasetContainer = gtaDatasetContainer;
        this.modelReference = gtaModelReference;
        this.modelDifferences = gtaModelDifferences;
        this.project = currentProject;
        this.ph = progressHandle;
    }

    public AnalysisWorker(TGProject currentProject, GtaOutput gtaOutput, GtaDatasetContainer gtaDatasetContainer, GtaModelReference gtaModelReference, ProgressHandle progressHandle) {
        this.output = gtaOutput;
        this.datasetContainer = gtaDatasetContainer;
        this.modelReference = gtaModelReference;
        this.modelDifferences = null;
        this.project = currentProject;
        this.ph = progressHandle;
    }

    @Override
    public void run() {
        try {
            ph.start();
            ph.switchToIndeterminate();
            doAnalysis();
            Thread.sleep(0);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void doAnalysis() {
        if (project != null) {
            if (output.getIterations() != null) {
                if (!output.getIterations().isEmpty()) {
                    numIterations = Integer.parseInt(output.getIterations());
                }
            } else {
                numIterations = NO_OF_ITERATIONS;
            }
            if (output.getOutputPath() != null && !output.getOutputPath().isEmpty()) {
                String[] nlsprogressResult = null;

                datasets = getDatasets(datasetContainer);
                modelCalls.add(getModelCall(modelReference, 0));

                fitModelCall = getFitModelCall(datasets, modelCalls, modelDifferences, output, numIterations);

                if (isValidAnalysis(datasets, modelReference)) {
                    timpcontroller = Lookup.getDefault().lookup(TimpControllerInterface.class);
                    if (timpcontroller.isConnected()) {
                        results = timpcontroller.runAnalysis(datasets, modelCalls, fitModelCall);
                        nlsprogressResult = timpcontroller.getStringArray(TimpControllerInterface.NAME_OF_RESULT_OBJECT + "$nlsprogress");

                        String outputPath = project.getResultsFolder(true).getPath() + File.separator + output.getOutputPath();
                        File outputFolder = new File(outputPath);
                        if (outputFolder.exists()) {
                            resultsfolder = FileUtil.toFileObject(outputFolder);
                            if (resultsfolder.getChildren().length > 0) {
                                try {
                                    resultsfolder = FileUtil.createFolder(resultsfolder.getParent(), FileUtil.findFreeFolderName(resultsfolder.getParent(), resultsfolder.getName()));
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

                        if (results != null) {
                            writeResults(results, modelReference, nlsprogressResult);

                        } else {
                            try {
                                writeSummary(resultsfolder, FileUtil.findFreeFileName(resultsfolder, resultsfolder.getName() + "_errorlog", "summary"));
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        timpcontroller.cleanup();

                    } else {
                        CoreErrorMessages.noRServeFoundException();
                    }
                } else {
                    CoreErrorMessages.notAValidModelException();
                    return;
                }

            } else {
                // Error message, output path is null
                CoreErrorMessages.noOutputPathSpecified();
            }
        }
    }

    private DatasetTimp[] getDatasets(GtaDatasetContainer gtaDatasetContainer) {
        TimpDatasetDataObject timpDatasetDO = null;
        int numberOfDatasets = gtaDatasetContainer.getDatasets().size();
        datasets = new DatasetTimp[numberOfDatasets];
        for (int i = 0; i < numberOfDatasets; i++) {
            GtaDataset gtaDataset = gtaDatasetContainer.getDatasets().get(i);
            String path = project.getProjectDirectory().getPath() + File.separator + gtaDataset.getPath();
            try {
                File datasetF = new File(path);
                FileObject datasetFO = FileUtil.createData(datasetF);
                DataObject datasetDO = DataObject.find(datasetFO);
                if (datasetDO != null) {
                    timpDatasetDO = (TimpDatasetDataObject) datasetDO;
                    datasets[i] = timpDatasetDO.getDatasetTimp();
                }
            } catch (DataObjectExistsException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return datasets;
    }

    private Tgm getModel(String filename, String relativeFilePath) {
        TgmDataObject tgmDO;
        Tgm model = null;
        String newPath = project.getProjectDirectory().getPath() + File.separator + relativeFilePath + File.separator + filename;
        try {
            File datasetF = new File(newPath);
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

    private Tgm getModel(GtaModelReference gtaModelReference) {
        TgmDataObject tgmDO = null;
        Tgm model = null;
        String path = project.getProjectDirectory().getPath() + File.separator + gtaModelReference.getPath();
//        String path = "C:/Sergey/Analysis_glotaran/kerr/models/etoh-3.xml";

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

    private ArrayList<String> getModelCalls(ArrayList<GtaModelReference> modelReferences) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < modelReferences.size(); i++) {
            GtaModelReference ref = modelReferences.get(i);
            result.add(getModelCall(ref, i));
        }
        return result;
    }

    private String getModelCall(GtaModelReference modelReference, int modelIndex) {
        String result;
        tgm = getModel(modelReference);
        String modelCall = InitModel.parseModel(tgm);
        result = TimpControllerInterface.NAME_OF_MODEL + (modelIndex + 1) + " <- " + modelCall;
        return result;
    }

    private String getFitModelCall(DatasetTimp[] datasets, ArrayList<String> modelCalls, GtaModelDifferences modelDifferences, GtaOutput gtaOutput, int numIterations) {
        String result = "";

        ArrayList<String> listOfDatasets = new ArrayList<String>();
        for (int i = 0; i < datasets.length; i++) {
            listOfDatasets.add(TimpControllerInterface.NAME_OF_DATASET + (i + 1));
        }

        ArrayList<String> listOfModels = new ArrayList<String>();
        for (int i = 0; i < modelCalls.size(); i++) {
            listOfModels.add(TimpControllerInterface.NAME_OF_MODEL + (i + 1));
        }
        result = TimpControllerInterface.NAME_OF_RESULT_OBJECT + " <- fitModel(";

        if (listOfDatasets != null) {
            result = result.concat("data = list(");
            for (int i = 0; i < listOfDatasets.size(); i++) {
                if (i > 0) {
                    result = result + ",";
                }
                result = result.concat(listOfDatasets.get(i));
            }
            result = result.concat(")");
        }

        if (listOfModels != null) {
            result = result.concat(",modspec = list(");
            for (int i = 0; i < listOfModels.size(); i++) {
                if (i > 0) {
                    result = result + ",";
                }
                result = result.concat(listOfModels.get(i));
            }
            result = result.concat(")");
        }

        String modeldiffsCall = getModelDifferences(modelDifferences);
        if (!modeldiffsCall.isEmpty()) {
            result = result.concat(",");
            result = result.concat(modeldiffsCall);
        }

        String optResult = getOptResult(getModelType(modelCalls), gtaOutput, numIterations);
        if (!optResult.isEmpty()) {
            result = result.concat(",");
            result = result.concat(optResult);
        }
        result = result.concat(", lprogress = TRUE");
        result = result.concat(")");
        return result;
    }

    private String getOptResult(String modelType, GtaOutput gtaOutput, int iterations) {
        String result = "opt = " + modelType + "opt("
                + "iter = " + String.valueOf(iterations);
        if (tgm.getDat().getKinparPanel().isNnls() != null) {
            String arg = tgm.getDat().getKinparPanel().isNnls() ? "TRUE" : "FALSE";
            result = result + ", nnls = " + arg;
        }
        if (gtaOutput.isGenerateErrorBars() != null) {
            if (gtaOutput.isGenerateErrorBars()) {
                if (modelType.equalsIgnoreCase("kin")) {
                    result = result + ", stderrclp = TRUE, kinspecerr = TRUE";
                } else {
                    result = result + ", stderrclp = TRUE";
                }
            } else {
                if (modelType.equalsIgnoreCase("kin")) {
                    result = result + ", stderrclp = FALSE, kinspecerr = FALSE";
                } else {
                    result = result + ", stderrclp = FALSE";
                }
            }
        } else {
            result = result + ", stderrclp = FALSE";
        }
        result = result + ", plot = FALSE)";
        return result;
    }

    public String getModelDifferences(GtaModelDifferences modelDifferences) {
        String result = "";
        String tempString = "";
        if (modelDifferences != null) {
            if (modelDifferences.getThreshold() != null) {
                if (modelDifferences.getThreshold() != 0) {
                    result = result + "thresh = " + String.valueOf(modelDifferences.getThreshold());
                }
            }

            tempString = getModelDiffsLinkCLP(modelDifferences.getLinkCLP());
            if (!tempString.isEmpty()) {
                if (!result.isEmpty()) {
                    result = result + ", ";
                }
                result = result + tempString;
            }
            tempString = "";

            tempString = getModelDiffsFree(modelDifferences.getDifferences());
            if (!tempString.isEmpty()) {
                if (!result.isEmpty()) {
                    result = result + ", ";
                }
                result = result + tempString;
            }
            tempString = "";

            tempString = getModelDiffsDScal(modelDifferences);
            if (!tempString.isEmpty()) {
                if (!result.isEmpty()) {
                    result = result + ", ";
                }
                result = result + tempString;
            }
            tempString = "";

            //Fill in the "change" parameter
            tempString = getModelDiffsChange(modelDifferences);
            if (!tempString.isEmpty()) {
                if (!result.isEmpty()) {
                    result = result + ", ";
                }
                result = result + tempString;
            }
            tempString = "";
        }
        if (!result.isEmpty()) {
            result = "modeldiffs = list(" + result + ")";
        }
        return result;
    }

    private String getModelDiffsFree(java.util.List<GtaModelDiffContainer> diffContainers) {
        String result = "";
        //Fill in the "free" parameter
        for (GtaModelDiffContainer diffContainer : diffContainers) {
            if (diffContainer != null) {
                for (int i = 0; i < diffContainer.getFree().size(); i++) {
                    GtaModelDiffDO modelDiffDO = diffContainer.getFree().get(i);
                    if (modelDiffDO != null) {
                        if (!result.isEmpty()) {
                            result = result + ",";
                        }
                        if (modelDiffDO.getWhat().equalsIgnoreCase("parmu")
                                || modelDiffDO.getWhat().equalsIgnoreCase("partau")) {
                            result = result
                                    + "list(what = \"" + modelDiffDO.getWhat() + "\","
                                    + "ind = c(1," + modelDiffDO.getIndex() + "),"
                                    + "dataset = " + modelDiffDO.getDataset() + ","
                                    + "start = " + modelDiffDO.getStart() + ")";
                        } else {
                            result = result
                                    + "list(what = \"" + modelDiffDO.getWhat() + "\","
                                    + "ind = " + modelDiffDO.getIndex() + ","
                                    + "dataset = " + modelDiffDO.getDataset() + ","
                                    + "start = " + modelDiffDO.getStart() + ")";
                        }
                    }
                }
            }
        }
        if (!result.isEmpty()) {
            result = "free = list(" + result + ")";
        }
        return result;
    }

    private String getModelDiffsLinkCLP(java.util.List<GtaLinkCLP> gtaLinkCLPList) {
        String result = "";
        if (gtaLinkCLPList != null) {
            int total = gtaLinkCLPList.size();
            String[] listOfCLP = new String[total];
            for (int i = 0; i < listOfCLP.length; i++) {
                listOfCLP[i] = "";
            }
            for (int i = 0; i < gtaLinkCLPList.size(); i++) {
                int num = gtaLinkCLPList.get(i).getGroupNumber() - 1;
                if (listOfCLP[num].isEmpty()) {
                    listOfCLP[num] = listOfCLP[num] + String.valueOf(i + 1);
                } else {
                    listOfCLP[num] = listOfCLP[num] + "," + String.valueOf(i + 1);
                }
            }
            String clp = "";
            for (int i = 0; i < listOfCLP.length; i++) {
                if (!listOfCLP[i].isEmpty()) {
                    if (!clp.isEmpty()) {
                        clp = clp + ',';
                    }
                    clp = clp + "c(" + listOfCLP[i] + ")";
                }

            }
            if (!clp.isEmpty()) {
                result = result + "linkclp = list(" + clp + ")";
            }
        }
        return result;
    }

    private boolean isFreeParam(String what, int index) {
        boolean isFree = false;
        for (GtaModelDiffContainer diffContainer : modelDifferences.getDifferences()) {
            if (diffContainer != null) {
                for (int i = 0; i < diffContainer.getFree().size(); i++) {
                    GtaModelDiffDO modelDiffDO = diffContainer.getFree().get(i);
                    isFree = (modelDiffDO.getWhat().compareToIgnoreCase(what) == 0
                            && modelDiffDO.getIndex() == index);
                    if (isFree) {
                        return isFree;
                    }

                }
            }
        }
        return isFree;
    }

    private String get_modeldiff_fixed(Tgm tgm) {
        String fixedStr = null;
        KinparPanelModel kinparPanelModel = tgm.getDat().getKinparPanel();
        int count = 0;
        for (int i = 0; i < kinparPanelModel.getKinpar().size(); i++) {
            if (kinparPanelModel.getKinpar().get(i).isFixed()
                    && !isFreeParam("kinpar", i + 1)) {
                if (count > 0) {
                    fixedStr = fixedStr + ",";
                } else {
                    fixedStr = " kinpar=c(";
                }
                fixedStr = fixedStr + String.valueOf(i + 1);
                count++;
            }
        }
        if (count > 0) {
            fixedStr = fixedStr + ")";
        }

        count = 0;

        KMatrixPanelModel kmatPanel = tgm.getDat().getKMatrixPanel();
        for (int i = 0; i < kmatPanel.getKinScal().size(); i++) {
            if (kmatPanel.getKinScal().get(i).isFixed()
                    && !isFreeParam("kinscal", i + 1)) {
                if (count > 0) {
                    fixedStr = fixedStr + ",";
                } else {
                    if (fixedStr != null) {
                        fixedStr = fixedStr + ", kinscal=c(";
                    } else {
                        fixedStr = " kinscal=c(";
                    }
                }
                fixedStr = fixedStr + String.valueOf(i + 1);
                count++;
            }
        }
        if (count > 0) {
            fixedStr = fixedStr + ")";
        }

        count = 0;
        IrfparPanelModel irfPanel = tgm.getDat().getIrfparPanel();
        for (int i = 0; i < irfPanel.getFixed().size(); i++) {
            if (irfPanel.getFixed().get(i)
                    && !isFreeParam("irfpar", i + 1)) {
                if (count > 0) {
                    fixedStr = fixedStr + ",";
                } else {
                    if (fixedStr != null) {
                        fixedStr = fixedStr + ", irfpar=c(";
                    } else {
                        fixedStr = " irfpar=c(";
                    }
                }
                fixedStr = fixedStr + String.valueOf(i + 1);
                count++;
            }
        }
        if (count > 0) {
            fixedStr = fixedStr + ")";
        }

        if (irfPanel.getParmufixedlist() != null && !irfPanel.getParmufixedlist().isEmpty()) {
            count = 0;
            for (int i = 0; i < irfPanel.getParmufixedlist().size(); i++) {
                if (irfPanel.getParmufixedlist().get(i)
                        && !isFreeParam("parmu", i + 1)) {
                    if (count > 0) {
                        fixedStr = fixedStr + ",";
                    } else {
                        if (fixedStr != null) {
                            fixedStr = fixedStr + ", parmu=c(";
                        } else {
                            fixedStr = " parmu=c(";
                        }
                    }
                    fixedStr = fixedStr + String.valueOf(i + 1);
                    count++;
                }
            }
            if (count > 0) {
                fixedStr = fixedStr + ")";
            }
        } else {

            if (irfPanel.getParmu() != null) {
                if (irfPanel.isParmufixed() != null && irfPanel.isParmufixed()) {
                    String[] doubles = irfPanel.getParmu().split(",");

                    if (fixedStr != null) {
                        fixedStr = fixedStr + ", parmu=c(";
                    } else {
                        fixedStr = " parmu=c(";
                    }
                    fixedStr = fixedStr + "1:" + String.valueOf(doubles.length);
                    fixedStr = fixedStr + ")";
                }
            }
        }

        if (irfPanel.getPartaufixedlist() != null && !irfPanel.getPartaufixedlist().isEmpty()) {
            count = 0;
            for (int i = 0; i < irfPanel.getPartaufixedlist().size(); i++) {
                if (irfPanel.getPartaufixedlist().get(i)
                        && !isFreeParam("partau", i + 1)) {
                    if (count > 0) {
                        fixedStr = fixedStr + ",";
                    } else {
                        if (fixedStr != null) {
                            fixedStr = fixedStr + ", partau=c(";
                        } else {
                            fixedStr = " partau=c(";
                        }
                    }
                    fixedStr = fixedStr + String.valueOf(i + 1);
                    count++;
                }
            }
            if (count > 0) {
                fixedStr = fixedStr + ")";
            }
        } else {

            if (irfPanel.getPartau() != null) {
                if (irfPanel.isPartaufixed()) {
                    if (fixedStr != null) {
                        fixedStr = fixedStr + ", partau=c(";
                    } else {
                        fixedStr = " partau=c(";
                    }

                    String[] doubles = irfPanel.getPartau().split(",");
                    fixedStr = fixedStr + "1:" + String.valueOf(doubles.length);
                    fixedStr = fixedStr + ")";
                }
            }
        }

        count = 0;
        KMatrixPanelModel kMatrix = tgm.getDat().getKMatrixPanel();
        for (int i = 0; i < kMatrix.getJVector().getFixed().size(); i++) {
            if (kMatrix.getJVector().getFixed().get(i)) {
                if (count > 0) {
                    fixedStr = fixedStr + ",";
                } else {
                    if (fixedStr != null) {
                        fixedStr = fixedStr + ", jvec=c(";
                    } else {
                        fixedStr = " jvec=c(";
                    }
                }
                fixedStr = fixedStr + String.valueOf(i + 1);
                count++;
            }
        }
        if (count > 0) {
            fixedStr = fixedStr + ")";
        }
        // TODO: add additional paramters for fixed here:

        // This closes the "fixed" argument

        return fixedStr;
    }

    private String getModelDiffsChange(GtaModelDifferences modelDifferences) {
        String result = "";
        String fileName, pathName;
        String tempStr;
        Tgm changesModel;
        Integer datasetIndex;
        ArrayList<String> baseArgs = new ArrayList<>();
        ArrayList<String> changedArgs = new ArrayList<>();
        List<GtaModelDiffContainer> diffContainers = modelDifferences.getDifferences();

        //Fill in the "change" parameter
        //First modify the 'fixed' variable
        for (GtaModelDiffContainer diffContainer : diffContainers) {
            if (diffContainer != null) {
                if (diffContainer.getFree().size() > 0) {
                    GtaModelDiffDO modelDiffDO = diffContainer.getFree().get(0);
                    String changesFixed = get_modeldiff_fixed(getModel(modelReference));
                    if (changesFixed != null) {
                        if (!result.isEmpty()) {
                            result = result + ",";
                        }
                        //TODO: clean up workaround
                        if (modelDifferences.getDscal().get(modelDiffDO.getDataset() - 1).isFixed() != null) {
                            if (modelDifferences.getDscal().get(modelDiffDO.getDataset() - 1).isFixed()) {
                                changesFixed = changesFixed + ", drel = 1";
                            }
                        }
                        changesFixed = "list (" + changesFixed + ")";
                        result = result + "list(what=\"fixed\", spec = " + changesFixed + ", dataset = " + modelDiffDO.getDataset() + ")";
                    }
                }

            }
        }



        for (GtaModelDiffContainer diffContainer : diffContainers) {
            baseArgs.clear();
            changedArgs.clear();

            if (diffContainer != null) {
                GtaChangesModel changes = diffContainer.getChanges();
                if (changes != null) {
                    fileName = changes.getFilename();
                    pathName = changes.getPath();
                    datasetIndex = changes.getDataset();
                    changesModel = getModel(fileName, pathName);

                    if (changesModel != null) {
                        Tgm baseModel = getModel(modelReference);

                        String baseModelCall = InitModel.get_kmatrix(baseModel);
                        if (baseModelCall.indexOf("kmat") != -1) {
                            if (baseModelCall.indexOf("jvec") != -1) {
                                tempStr = baseModelCall.substring(baseModelCall.indexOf("kmat"), baseModelCall.indexOf("jvec"));
                                baseArgs.add(tempStr.substring(0, tempStr.lastIndexOf(",")));
                            } else {
                                tempStr = baseModelCall.substring(baseModelCall.indexOf("kmat"));
                                baseArgs.add(tempStr.substring(0, tempStr.lastIndexOf(",")));
                            }
                        }

                        if (baseModelCall.indexOf("jvec") != -1) {
                            if (baseModelCall.indexOf("kinscal") != -1) {
                                tempStr = baseModelCall.substring(baseModelCall.indexOf("jvec"), baseModelCall.indexOf("kinscal"));
                                baseArgs.add(tempStr.substring(0, tempStr.lastIndexOf(",")));
                            } else {
                                tempStr = baseModelCall.substring(baseModelCall.indexOf("jvec"));
                                baseArgs.add(tempStr);
                            }
                        }

                        if (baseModelCall.indexOf("kinscal") != -1) {
                            tempStr = baseModelCall.substring(baseModelCall.indexOf("kinscal"));
                            baseArgs.add(tempStr);
                        }


                        String changedModelCall = InitModel.get_kmatrix(changesModel);
                        if (changedModelCall.indexOf("kmat") != -1) {
                            if (changedModelCall.indexOf("jvec") != -1) {
                                tempStr = changedModelCall.substring(changedModelCall.indexOf("kmat"), changedModelCall.indexOf("jvec"));
                                changedArgs.add(tempStr.substring(0, tempStr.lastIndexOf(",")));
                            } else {
                                tempStr = changedModelCall.substring(changedModelCall.indexOf("kmat"));
                                changedArgs.add(tempStr.substring(0, tempStr.lastIndexOf(",")));
                            }
                        }

                        if (changedModelCall.indexOf("jvec") != -1) {
                            if (changedModelCall.indexOf("kinscal") != -1) {
                                tempStr = changedModelCall.substring(changedModelCall.indexOf("jvec"), changedModelCall.indexOf("kinscal"));
                                changedArgs.add(tempStr.substring(0, tempStr.lastIndexOf(",")));
                            } else {
                                tempStr = changedModelCall.substring(changedModelCall.indexOf("jvec"));
                                changedArgs.add(tempStr);
                            }
                        }

                        if (changedModelCall.indexOf("kinscal") != -1) {
                            tempStr = changedModelCall.substring(changedModelCall.indexOf("kinscal"));
                            changedArgs.add(tempStr);
                        }

                        baseModelCall = InitModel.get_kinpar(baseModel);
                        if (baseModelCall.indexOf("kinpar") != -1) {
                            baseArgs.add(baseModelCall);
                        }

                        changedModelCall = InitModel.get_kinpar(changesModel);
                        if (changedModelCall.indexOf("kinpar") != -1) {
                            changedArgs.add(changedModelCall);
                        }

                        String what = null;
                        for (int i = 0; i < changedArgs.size(); i++) {
                            if (changedArgs.get(i).compareTo(baseArgs.get(i)) != 0) {
                                if (!result.isEmpty()) {
                                    result = result + ",";
                                }
                                what = changedArgs.get(i).substring(0, changedArgs.get(i).indexOf("=")).trim();
                                result = result
                                        + "list(what = \"" + what + "\","
                                        + "dataset = " + datasetIndex + ","
                                        + "spec = " + changedArgs.get(i).substring(1 + changedArgs.get(i).indexOf("=")) + ")";
                            }

                        }


                    }

                }
            }
        }

        if (result.isEmpty()) {
            String changesFixed = "";
            if (modelDifferences.getDscal().size() > 1) {
                for (int i = 0; i < modelDifferences.getDscal().size(); i++) {
                    //int dataset = modelDifferences.getDscal().get(i).getToDataset();
                    if (modelDifferences.getDscal().get(i).isFixed() != null) {
                        if (modelDifferences.getDscal().get(i).isFixed()) {
                            changesFixed += "";
                        }
                    }


                }
            }
        }

        if (!result.isEmpty()) {
            result = "change = list(" + result + ")";
        }
        return result;
    }

//    private String getModelDiffsChange(GtaModelDiffContainer diffContainer) {
//        String result = "";
//
//        TgmDataObject tgmDO;
//        Tgm changesModel = null;
//        if (diffContainer != null) {
//            if (diffContainer.getChanges() != null) {
//                GtaChangesModel changes = diffContainer.getChanges();
//                String fileName = changes.getFilename();
//                String pathName = changes.getPath();
//                changesModel = getModel(fileName, pathName);
//            }
//
//            if(changesModel!=null) {
//                if(changesModel.getDat().getKMatrixPanel()!=null) {
//                    //Todo: finish parsing code
//                    //change = list(list(what="kmat", dataset=3, spec=delK),
//		//list(what="kmat", dataset=4, spec=delK),
//		//list(what="prelspec", dataset=2,spec=list(list(what1="kinpar",
//			//what2="kinpar",ind1=6,ind2=1, start=c(0.1,0))))
//                String test = InitModel.get_kmatrix(changesModel);
//                String test2 = test.substring(7);
//                }
//
//            }
//
//        }
//
//        return result;
//    }
    private String getModelDiffsDScal(GtaModelDifferences modelDiffs) {
        String result = "";
        if (modelDiffs.getThreshold() < 0) {
            return result;
        }
        int datasetNum = modelDiffs.getLinkCLP().size();

        if (datasetNum > 1) {

            int groups[] = new int[datasetNum];
            for (int i = 0; i < datasetNum; i++) {
                groups[modelDiffs.getLinkCLP().get(i).getGroupNumber() - 1] += 1;
            }

            String tempString = "";
            for (int i = 0; i < datasetNum; i++) {
                if (groups[modelDiffs.getLinkCLP().get(i).getGroupNumber() - 1] > 1) {
                    //index of first dataset in the group
                    Integer fromInd = modelDiffs.getDscal().get(i).getToDataset();
                    Integer toInd = i;
                    Double toValue = modelDiffs.getDscal().get(i).getValue();
                    if (toInd != null && toValue != null && fromInd != null) {
                        if ((toInd + 1) != fromInd) {
                            if (modelDiffs.getLinkCLP().get(toInd).getGroupNumber() == modelDiffs.getLinkCLP().get(fromInd - 1).getGroupNumber()) {
                                if (!tempString.isEmpty()) {
                                    tempString = tempString + ", ";
                                }
                                relationsList.add(new Double[3]);
                                relationsList.get(relationsList.size() - 1)[0] = (double) toInd + 1;
                                relationsList.get(relationsList.size() - 1)[1] = (double) fromInd;
                                relationsList.get(relationsList.size() - 1)[2] = toValue;
                                tempString = tempString + "list(to = " + String.valueOf(toInd + 1)
                                        + ", from = " + String.valueOf(fromInd)
                                        + ", value = " + toValue
                                        + ")";

                            }
                        }
                    }
                }
            }

            if (!tempString.isEmpty()) {
                result = "dscal = list(" + tempString + ")";
            }
        }
        return result;
    }

    private String getModelType(ArrayList<String> modelCalls) {
        String result = "";
        for (String string : modelCalls) {
            if (string.contains("mod_type = \"kin\"")) {
                result = "kin";
            } else if (string.contains("mod_type = \"spec\"")) {
                result = "spec";
            } else if (string.contains("mod_type = \"mass\"")) {
                result = "mass";
            } else {
                result = "kin";
            }
            if (!result.isEmpty()) {
                return result;
            }
        }
        return result;
    }

    private boolean isValidAnalysis(DatasetTimp[] datasets, GtaModelReference gtaModelReference) {
        Tgm tgm = getModel(gtaModelReference);
        String feedback = null;

        boolean run = true;
        int counter = 0;
        for (DatasetTimp dataset : datasets) {
            counter++;
            if (dataset == null) {
                run = false;
            }
            int numberOfNaNs = checkDatasetForNaNs(dataset);
            if (numberOfNaNs > 0) {
                run = false;
                CoreErrorMessages.datasetContainsNaNs(counter,numberOfNaNs);
            }
        }

        if (tgm.getDat().getIrfparPanel().isMirf() == null || !tgm.getDat().getIrfparPanel().isMirf()) {
            if ((tgm.getDat().getIrfparPanel().getParmu() != null && !tgm.getDat().getIrfparPanel().getParmu().isEmpty())
                    || (tgm.getDat().getIrfparPanel().getPartau() != null && !tgm.getDat().getIrfparPanel().getPartau().isEmpty())) {
                Double testLamda = tgm.getDat().getIrfparPanel().getLamda();
                if (testLamda == null || testLamda.isNaN()) {
                    run = false;
                    feedback = "Parmu or Partau specified but no center wavelength was specified.";
                }
            }
        }

        if (tgm.getDat().getIrfparPanel().isBacksweepEnabled() != null && tgm.getDat().getIrfparPanel().isBacksweepEnabled()) {
            if (tgm.getDat().getIrfparPanel().getBacksweepPeriod() == null) {
                run = false;
                feedback = "Backsweep modelling specified but no sweep period specified.";
            }
        }

        if (feedback != null) {
            NotifyDescriptor errorMessage = new NotifyDescriptor.Message(
                    new Exception("Invalid Model: \n"
                    + "" + feedback + "\n"
                    + "Analysis was not started."));
            DialogDisplayer.getDefault().notifyLater(errorMessage);
        }
        return run;
    }

    private void writeSummary(FileObject resultsfolder, String freeFilename) throws IOException {
        writeTo = resultsfolder.createData(freeFilename, "summary");
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(FileUtil.toFile(writeTo)));
        //TODO: Complete the summary here:
        outputWriter.append("Summary");
        outputWriter.newLine();
//        outputWriter.append("Used dataset(s): ");
//        for (int j = 0; j < datasets.length; j++) {
//            DatasetTimp dataset = datasets[j];
//            if (j > 0) {
//                outputWriter.append(", ");
//            }
//            outputWriter.append(dataset.getDatasetName());
//        }
//        outputWriter.newLine();
//        outputWriter.newLine();
//        outputWriter.append("Used model(s): ");
//        for (int j = 0; j < models.length; j++) {
//            if (j > 0) {
//                outputWriter.append(", ");
//            }
//            outputWriter.append(models[j].getDat().getModelName());
//        }
//        outputWriter.newLine();
//        outputWriter.newLine();

        outputWriter.append("Number of iterations: ");
        outputWriter.append(String.valueOf(numIterations));
        outputWriter.newLine();
        outputWriter.newLine();

        outputWriter.append("# R Call for the TIMP function \"initModel\": ");
        outputWriter.newLine();
        ArrayList<String> list = modelCalls;
        for (String string : list) {
            outputWriter.append(string);
            outputWriter.newLine();
        }
        outputWriter.newLine();
        outputWriter.append("# R Call for the TIMP function \"fitModel\": ");
        outputWriter.newLine();
        outputWriter.write(fitModelCall);
        outputWriter.newLine();
        outputWriter.newLine();

        if (results != null) {

            outputWriter.append("Final residual standard error: ");
            outputWriter.append((new Formatter().format("%g", results[0].getRms())).toString());
            outputWriter.newLine();
            outputWriter.newLine();

            String[] slots = {"getKineticParameters", "getSpectralParameters", "getIrfpar", "getCoh","getOscpar","getSpecdisppar", "getParmu", "getPartau", "getKinscal", "getPrel", "getJvec"};
            String[] slotsName = {"Kinetic parameters", "Spectral parameters", "Irf parameters", "Cohspec parameters", "Oscspec parameters", "Specdisppar", "Parmu", "Partau", "Kinscal", "Prel", "J vector"};
            double[] params = null;

            for (int k = 0; k < slots.length; k++) {
                try {
                    try {
                        for (int i = 0; i < results.length; i++) {
                            //TODO: verify the next line
                            //params = (double[]) results[i].getClass().getMethod(slots[k], new Class[]{results[i].getClass().getClass()}).invoke(results[i], new Object[]{results});
                            params = (double[]) results[i].getClass().getMethod(slots[k], null).invoke(results[i], null);
                            if (params != null) {
                                if (i == 0) {
                                    outputWriter.newLine();
                                    outputWriter.append("Estimated " + slotsName[k] + ": ");
                                }
                                outputWriter.append("Dataset" + (i + 1) + ": ");
                                for (int j = 0; j < params.length / 2; j++) {
                                    if (j > 0) {
                                        outputWriter.append(", ");
                                    }
                                    outputWriter.append((new Formatter().format("%g", params[j])).toString());
                                }
                                outputWriter.newLine();
                                outputWriter.append("Standard errors: ");
                                for (int j = 0; j < params.length / 2; j++) {
                                    if (j > 0) {
                                        outputWriter.append(", ");
                                    }
                                    outputWriter.append((new Formatter().format("%g",
                                            params[j + params.length / 2])).toString());
                                }
                                outputWriter.newLine();
                            }
                        }
                    } catch (IllegalAccessException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalArgumentException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } catch (NoSuchMethodException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (SecurityException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } else {
            outputWriter.newLine();
            outputWriter.append("Error: The analysis did not return valid results.");
            outputWriter.newLine();
            outputWriter.append("Try again with different parameters.");
        }
        outputWriter.close();
    }

    private void writeResultsXml(GtaResult resultsObject) throws IOException {

        String newAnResFileName = FileUtil.findFreeFileName(resultsfolder, resultsfolder.getName() + "_" + modelReference.getFilename() + "_overview", "xml");
        try {
            FileObject newAnResFile = resultsfolder.createData(newAnResFileName, "xml");
            resultsObject.setSummary(new Summary());
            resultsObject.getSummary().setFitModelCall(fitModelCall);
            //TODO resolve problem with multiple modelcalls
            resultsObject.getSummary().setInitModelCall(modelCalls.get(0));

            for (int i = 0; i < relationsList.size(); i++) {
                resultsObject.getDatasetRelations().add(new DatasetRelation());
                resultsObject.getDatasetRelations().get(i).setTo(String.valueOf((int) floor(relationsList.get(i)[0])));
                resultsObject.getDatasetRelations().get(i).setFrom(String.valueOf((int) floor(relationsList.get(i)[1])));
                //TODO do this in a different way
                //resultsObject.getDatasetRelations().get(i).getValues().add(relationsList.get(i)[2]);
                String cmd = TimpControllerInterface.NAME_OF_RESULT_OBJECT + "$currTheta[[" + (int) floor(relationsList.get(i)[0]) + "]]@drel";
                resultsObject.getDatasetRelations().get(i).getValues().add(timpcontroller.getDouble(cmd));
            }

            createAnalysisResultsFile(resultsObject, FileUtil.toFile(newAnResFile));
        } catch (Exception e) {
            if (e.getMessage().matches("Permission denied")) {
                CoreErrorMessages.folderNotWritable(resultsfolder.getPath());
            }
        }

    }

    private void createAnalysisResultsFile(GtaResult resultsObject, File file) {
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(resultsObject.getClass().getPackage().getName());
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(resultsObject, file);

        } catch (JAXBException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
    }

    private void writeResults(TimpResultDataset[] results, GtaModelReference modelReference, String[] nlsprogressResult) {
        Tgm model = getModel(modelReference);
        GtaResult newResultsObject = new GtaResult();
        String freeResultsFilename = FileUtil.findFreeFileName(resultsfolder, resultsfolder.getName(), "summary");
        try {
            writeSummary(resultsfolder, freeResultsFilename);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (nlsprogressResult != null) {
            for (int i = 0; i < nlsprogressResult.length; i++) {
                NlsProgress progress = new NlsProgress();
                progress.setRss(nlsprogressResult[i]);
                newResultsObject.getNlsprogress().add(progress);
            }
        }

        for (int i = 0; i < results.length; i++) {
            TimpResultDataset timpResultDataset = results[i];
            timpResultDataset.setType(datasets[i].getType());

            newResultsObject.getDatasets().add(new Dataset());
            newResultsObject.getDatasets().get(i).setDatasetFile(new OutputFile());
            newResultsObject.getDatasets().get(i).getDatasetFile().setFilename(datasetContainer.getDatasets().get(i).getFilename());
            newResultsObject.getDatasets().get(i).getDatasetFile().setPath(datasetContainer.getDatasets().get(i).getPath());
            newResultsObject.getDatasets().get(i).getDatasetFile().setFiletype(datasets[i].getType());
            newResultsObject.getDatasets().get(i).setId(String.valueOf(i + 1));

            if (model.getDat().getIrfparPanel().getLamda() != null) {
                timpResultDataset.setLamdac(model.getDat().getIrfparPanel().getLamda());
            }

            if (datasets[i].getType().equalsIgnoreCase("flim")) {
                timpResultDataset.setOrheigh(datasets[i].getOriginalHeight());
                timpResultDataset.setOrwidth(datasets[i].getOriginalWidth());
                timpResultDataset.setIntenceIm(datasets[i].getIntenceIm().clone());
                timpResultDataset.setMaxInt(datasets[i].getMaxInt());
                timpResultDataset.setMinInt(datasets[i].getMinInt());
                timpResultDataset.setX(datasets[i].getX().clone());
                timpResultDataset.setX2(datasets[i].getX2().clone());
            }

            try {
                String freeFilename = FileUtil.findFreeFileName(resultsfolder, resultsfolder.getName() + "_d" + (i + 1) + "_" + timpResultDataset.getDatasetName(), "timpres");
                timpResultDataset.setDatasetName(freeFilename);
                writeTo = resultsfolder.createData(freeFilename, "timpres");
                ObjectOutputStream stream = new ObjectOutputStream(writeTo.getOutputStream());
                stream.writeObject(timpResultDataset);
                stream.close();
                
                String hdfFile = writeTo.getPath();
                hdfFile = hdfFile.substring(0, hdfFile.lastIndexOf('.')) + ".h5";
                File file = new File(hdfFile);
                Hdf5TimpResultDataset.save(file, timpResultDataset);

                newResultsObject.getDatasets().get(i).setResultFile(new OutputFile());
                newResultsObject.getDatasets().get(i).getResultFile().setFilename(freeFilename);
                newResultsObject.getDatasets().get(i).getResultFile().setPath(FileUtil.getRelativePath(project.getProjectDirectory(), resultsfolder));

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
        try {
            writeResultsXml(newResultsObject);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private int checkDatasetForNaNs(DatasetTimp dataset) {
        int numberOfNaNs = 0;
        for (int i = 0; i < dataset.getPsisim().length; i++) {
            if(Double.isNaN(dataset.getPsisim()[i])) {
                numberOfNaNs+=1;
            }
            
        }
        return numberOfNaNs;
    }
}
