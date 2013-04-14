/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes.actions;

import org.glotaran.core.main.common.CommonActionFunctions;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.glotaran.core.main.common.ExportPanelForm;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.structures.DatasetTimp;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.loaders.DataObject;

public final class ExportTimpDatasetAction implements ActionListener {

    private final DataObject context;
    private DatasetTimp dataset;

    public ExportTimpDatasetAction(DataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (context instanceof TimpDatasetDataObject) {
            dataset = ((TimpDatasetDataObject) context).getDatasetTimp();
        }
        if (dataset!=null) {

            ExportPanelForm exportDialogPanel = new ExportPanelForm();
            NotifyDescriptor exportDataDialog = new NotifyDescriptor(
                    exportDialogPanel,
                    "Export dataset ...",
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.PLAIN_MESSAGE,
                    null,
                    NotifyDescriptor.CANCEL_OPTION);
            Object res2 = DialogDisplayer.getDefault().notify(exportDataDialog);
            if (res2.equals(NotifyDescriptor.OK_OPTION)) {
                if (!exportDialogPanel.getFileName().isEmpty()){
                    CommonActionFunctions.exportSpecDatasets(dataset, exportDialogPanel.getFileName(), exportDialogPanel.getExportType());
                } else {
                    CoreErrorMessages.fileSaveError(null);
                }
            }
        }



    }
    
}
