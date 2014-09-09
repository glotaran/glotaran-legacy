/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import javax.swing.Icon;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.main.common.CommonActionFunctions;
import org.glotaran.core.main.common.ExportPanelForm;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.structures.DatasetTimp;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.NotificationDisplayer;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "org.glotaran.core.main.nodes.actions.ExportIRF")
@ActionRegistration(
        displayName = "#CTL_ExportIRF")
@ActionReferences({
    @ActionReference(path = "Menu/Export", position = 140),
    @ActionReference(path = "Loaders/text/tgd+xml/Actions", position = 250),
    @ActionReference(path = "Loaders/text/x-timpdataset/Actions", position = 250),
    @ActionReference(path = "Shortcuts", name = "D-S-I")
})
@Messages("CTL_ExportIRF=Export IRF")
public final class ExportIRF implements ActionListener {

    private final DataObject context;
    private TgdDataObject datafile;
    private TGProject project;

    public ExportIRF(DataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        DatasetTimp dataset = null;
        File tgdFile;
        if (context instanceof TgdDataObject) {

            datafile = (TgdDataObject) context;
            project = (TGProject) FileOwnerQuery.getOwner(datafile.getPrimaryFile());
            if (datafile.getTgd().getFiletype().equalsIgnoreCase("spec")) {
                if (datafile.getTgd().getRelativePath() != null) {
                    tgdFile = new File(project.getProjectDirectory().getPath() + File.separator + datafile.getTgd().getRelativePath());
                } else { //try the orginal location
                    tgdFile = new File(datafile.getTgd().getPath());
                }
                Collection<? extends TGDatasetInterface> services = Lookup.getDefault().lookupAll(TGDatasetInterface.class);
                for (final TGDatasetInterface service : services) {
                    try {
                        if (service.Validator(tgdFile)) {
                            dataset = service.loadFile(tgdFile);
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
            }
        } else if (context instanceof TimpDatasetDataObject) {
            dataset = ((TimpDatasetDataObject) context).getDatasetTimp();
        }

        if (dataset != null) {
            if (dataset.getMeasuredIRF() != null) {
                ExportPanelForm exportDialogPanel = new ExportPanelForm((TGProject) FileOwnerQuery.getOwner(datafile.getPrimaryFile()));
                NotifyDescriptor exportDataDialog = new NotifyDescriptor(
                        exportDialogPanel,
                        "Export IRF ...",
                        NotifyDescriptor.OK_CANCEL_OPTION,
                        NotifyDescriptor.PLAIN_MESSAGE,
                        null,
                        NotifyDescriptor.CANCEL_OPTION);
                Object res2 = DialogDisplayer.getDefault().notify(exportDataDialog);
                if (res2.equals(NotifyDescriptor.OK_OPTION)) {
                    if (!exportDialogPanel.getFileName().isEmpty()) {

                        CommonActionFunctions.exportIRFFromDataset(dataset, exportDialogPanel.getFileName(), exportDialogPanel.getExportType());


                    } else {
                        CoreErrorMessages.fileSaveError(null);
                    }
                }
            } else {
                NotificationDisplayer.getDefault().notify("Error exporting IRF",
                        (Icon) context.getNodeDelegate().getIcon(0),
                        "The selected dataset does not contain an IRF to export.",
                        this);
            }
        }

    }
}
