/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.resultdisplayers.actions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.glotaran.core.interfaces.AnalysisResultFileSupportInterface;
import org.glotaran.core.interfaces.SupportedXMLFilesInterface;
import org.glotaran.core.main.interfaces.GlobalSpecResultsProviderInterface;
import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;
import org.glotaran.core.models.results.GtaResult;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.CloneableTopComponent;

public final class CompareTimpResultsAction implements ActionListener {

    private final List<DataObject> context;
    private ArrayList<TimpResultDataset> listOfTimpResultDatasets;
    private ArrayList<GtaResult> listOfAnalysisResults;

    public CompareTimpResultsAction(List<DataObject> context) {
        this.context = context;
        listOfTimpResultDatasets = new ArrayList<TimpResultDataset>();
        listOfAnalysisResults = new ArrayList<GtaResult>();
    }

    public void actionPerformed(ActionEvent ev) {
        for (DataObject dataObject : context) {
            if (dataObject instanceof TimpResultDataObject) {
                listOfTimpResultDatasets.add(((TimpResultDataObject) dataObject).getTimpResultDataset());
                listOfAnalysisResults.add(getAnalysisResultsFile(dataObject));
            }
        }
        if (listOfTimpResultDatasets.size() > 1 && listOfAnalysisResults.size() > 1) {
            GlobalSpecResultsProviderInterface globalResultsProvider = Lookup.getDefault().lookup(GlobalSpecResultsProviderInterface.class);
            CloneableTopComponent tc = globalResultsProvider.getCloneableTopComponent(listOfTimpResultDatasets, getUniqueGtaResult(listOfAnalysisResults));
            tc.setDisplayName("Analyses comparison");
            tc.open();
        } else {
            //TODO: display warning
        }
    }

    private GtaResult getAnalysisResultsFile(DataObject dataObject) {
        GtaResult gtaResult = null;
        FileObject folder = dataObject.getPrimaryFile().getParent();
        AnalysisResultFileSupportInterface analysisResultFileSupport = Lookup.getDefault().lookup(AnalysisResultFileSupportInterface.class);
        Collection<? extends SupportedXMLFilesInterface> services =
                Lookup.getDefault().lookupAll(SupportedXMLFilesInterface.class);
        for (SupportedXMLFilesInterface service : services) {
            if (service.getType().equalsIgnoreCase("AnalysisResultDataObject")) {
                for (int i = 0; i < folder.getChildren().length; i++) {
                    try {
                        DataObject test = DataObject.find(folder.getChildren()[i]);
                        if (test.getClass().equals(service.getDataObjectClass())) {
                            gtaResult = (GtaResult) analysisResultFileSupport.getContent(FileUtil.toFile(folder.getChildren()[i]));
                        }
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    if (folder.getChildren()[i].getClass().equals(service.getDataObjectClass())) {
                        gtaResult = (GtaResult) analysisResultFileSupport.getContent(FileUtil.toFile(folder.getChildren()[i]));
                    }
                }
            }
        }
        return gtaResult;
    }

    private GtaResult getUniqueGtaResult(ArrayList<GtaResult> listOfAnalysisResults) {
        GtaResult result = null;
        for (int i = 1; i < listOfAnalysisResults.size(); i++) {
            if (!listOfAnalysisResults.get(i - 1).equals(listOfAnalysisResults.get(i))) {
                return null;
            }
            result = listOfAnalysisResults.get(i);
        }
        return result;
    }
}
