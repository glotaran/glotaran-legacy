/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes.actions;

import org.glotaran.core.main.common.CommonActionFunctions;
import org.glotaran.core.main.common.PreprocessingPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.messages.CoreWarningMessages;
import org.glotaran.core.models.structures.DatasetTimp;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

public final class AverageTGDDatasetsAction implements ActionListener {

    private final List<DataObject> context;
    private ArrayList<DatasetTimp> listOfTimpDatasets = new ArrayList<DatasetTimp>();
    private DatasetTimp resDataset;


    public AverageTGDDatasetsAction(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {

        DatasetTimp data = null;
        File tgdFile;
        if (context.size() > 1) {
            TGProject project = (TGProject) FileOwnerQuery.getOwner(context.get(0).getPrimaryFile());
            PreprocessingPane preprocessDialogPanel = new PreprocessingPane(project);
            NotifyDescriptor selectDataDialog = new NotifyDescriptor(
                    preprocessDialogPanel,
                    "Preprocess and average ...",
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.PLAIN_MESSAGE,
                    null,
                    NotifyDescriptor.CANCEL_OPTION);
            Object res2 = DialogDisplayer.getDefault().notify(selectDataDialog);
            if (res2.equals(NotifyDescriptor.OK_OPTION)) {


// after that it can be sent to another thread

                for (DataObject dataObject : context) {
                    if (dataObject instanceof TgdDataObject) {
                        TgdDataObject dataObject2 = (TgdDataObject) dataObject;
                        project = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
                        if (dataObject2.getTgd().getFiletype().equalsIgnoreCase("spec")) {
                            if (dataObject2.getTgd().getRelativePath() != null) {
                                tgdFile = new File(project.getProjectDirectory().getPath() + File.separator + dataObject2.getTgd().getRelativePath());
                            } else { //try the orginal location
                                tgdFile = new File(dataObject2.getTgd().getPath());
                            }
                            Collection<? extends TGDatasetInterface> services = Lookup.getDefault().lookupAll(TGDatasetInterface.class);
                            for (final TGDatasetInterface service : services) {
                                try {
                                    if (service.Validator(tgdFile)) {
                                        data = service.loadFile(tgdFile);
                                    }
                                } catch (FileNotFoundException ex) {
                                    Exceptions.printStackTrace(ex);
                                    return;
                                } catch (IOException ex) {
                                    Exceptions.printStackTrace(ex);
                                    return;
                                } catch (IllegalAccessException ex) {
                                    Exceptions.printStackTrace(ex);
                                    return;
                                } catch (InstantiationException ex) {
                                    Exceptions.printStackTrace(ex);
                                    return;
                                }
                            }
                            listOfTimpDatasets.add(data);
                        }
                    }
                }



                if (preprocessDialogPanel.getTotalIntCorState()) {
                    for (DatasetTimp dataset : listOfTimpDatasets) {
                        CommonActionFunctions.totalIntencityCorrection(dataset);
                    }
                }

                if (preprocessDialogPanel.getBackgroundState()) {
                    //todo implement baseline correction
                }

                if (preprocessDialogPanel.getSelectState()) {
                    for (DatasetTimp dataset : listOfTimpDatasets) {
                        CommonActionFunctions.selectInDataset(dataset,
                                preprocessDialogPanel.getSelectDataPanel().getDim1From(),
                                preprocessDialogPanel.getSelectDataPanel().getDim1To(),
                                preprocessDialogPanel.getSelectDataPanel().getDim2From(),
                                preprocessDialogPanel.getSelectDataPanel().getDim2To());
                    }
                }

                if (preprocessDialogPanel.getResampleState()) {
                    for (DatasetTimp dataset : listOfTimpDatasets) {
                        CommonActionFunctions.resampleDataset(dataset,
                                preprocessDialogPanel.getResampleDatasetPanel().getAverageState(),
                                preprocessDialogPanel.getResampleDatasetPanel().getResampleXState() ? preprocessDialogPanel.getResampleDatasetPanel().getResampleXNum() : 0,
                                preprocessDialogPanel.getResampleDatasetPanel().getResampleYState() ? preprocessDialogPanel.getResampleDatasetPanel().getResampleYNum() : 0);
                    }
                }

                if (preprocessDialogPanel.getOutliersState()) {
                    for (DatasetTimp dataset : listOfTimpDatasets) {
                        CommonActionFunctions.outliersCorrection(dataset,
                                preprocessDialogPanel.getOutlierCorrectionPanel().getWindowSize(),
                                preprocessDialogPanel.getOutlierCorrectionPanel().getFence());
                    }
                }


                resDataset = CommonActionFunctions.averageSpecDatasets(listOfTimpDatasets);
                if (resDataset == null) {
                    CoreErrorMessages.differentSizeDatasetsError();
                    return;
                }

                String datasetsfolder = preprocessDialogPanel.getFileName().getParent();
                FileObject folderObj = FileUtil.toFileObject(new File(datasetsfolder));
                String freeFilename = FileUtil.findFreeFileName(folderObj, preprocessDialogPanel.getFileName().getName(), "timpdataset");
                try {
                    FileObject writeTo = folderObj.createData(freeFilename, "timpdataset");
                    ObjectOutputStream stream = new ObjectOutputStream(writeTo.getOutputStream());
                    stream.writeObject(resDataset);
                    stream.close();

                } catch (IOException ex) {
                    CoreErrorMessages.fileSaveError(freeFilename);
                }

            }
        } else {
            CoreWarningMessages.wrongSelectionWarning();
        }

    }
}
