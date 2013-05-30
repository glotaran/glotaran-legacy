/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes.actions;

import org.glotaran.core.main.common.CommonActionFunctions;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.messages.CoreWarningMessages;
import org.glotaran.core.models.structures.DatasetTimp;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

public final class AverageTimpDatasetsAction implements ActionListener {

    private final List<DataObject> context;
    private ArrayList<DatasetTimp> listOfTimpDatasets = new ArrayList<DatasetTimp>();
    private DatasetTimp resDataset;
    

    public AverageTimpDatasetsAction(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        TGProject project = null; 
        ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Averaging datasets...");
        if (context.size()>1){
            progressHandle.start();
            for (DataObject dataObject : context) {
                if (dataObject instanceof TimpDatasetDataObject) {
                    if (project==null){
                         project = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
                    }
                    listOfTimpDatasets.add(((TimpDatasetDataObject) dataObject).getDatasetTimp());
                }
            }        
            resDataset = CommonActionFunctions.averageSpecDatasets(listOfTimpDatasets);
            progressHandle.finish();
            if (resDataset == null){
                CoreErrorMessages.differentSizeDatasetsError();
                return;
            }

            NotifyDescriptor.InputLine datasetNameDialog = new NotifyDescriptor.InputLine(
                    NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("dataset_name"),
                    NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("spec_datasetname"));
            Object res = DialogDisplayer.getDefault().notify(datasetNameDialog);

            if (res.equals(NotifyDescriptor.OK_OPTION)) {
                FileObject datasetsfolder = project.getDatasetsFolder(true);
                String filename = datasetNameDialog.getInputText();
                String freeFilename = FileUtil.findFreeFileName(datasetsfolder, filename, "timpdataset");
            try { 
                ObjectOutputStream stream = null;
                try {
                FileObject writeTo = datasetsfolder.createData(freeFilename, "timpdataset");
                stream = new ObjectOutputStream(writeTo.getOutputStream());
                stream.writeObject(resDataset);
                } finally {
                    if (stream !=null){
                        stream.close();
                    }
                }
            } catch (IOException ex) {
                CoreErrorMessages.fileSaveError(freeFilename);
            }

            }
        }
        else {
            CoreWarningMessages.wrongSelectionWarning();
        }
        
    }
}
