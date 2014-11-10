/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.analysis.support;

/**
 *
 * @author jsg210
 */
public class AnalysisHelper {

    public AnalysisHelper() {
    }
//    public TimpResultDataset[] runAnalysis(DatasetTimp[] datasets, Tgm[] models, int iterations) {
//
//        TimpResultDataset[] result = null;
//        String[] listOfDatasets;
//        String[] listOfModels;
//        String modelType = null;
//        String optCall = null;
//
//        listOfDatasets = new String[datasets.length];
//        for (int i = 0; i < datasets.length; i++) {
//            DatasetTimp dataset = datasets[i];
//            listOfDatasets[i] = dataset.getDatasetName();
//            sendDataset(dataset, i);
//        }
//
//        listOfModels = new String[models.length];
//        for (int i = 0; i < models.length; i++) {
//            Tgm tgm = models[i];
//            listOfModels[i] = tgm.getDat().getModelName();
//            Boolean correctSend = sendModel(tgm, i);
//            if (correctSend==null || correctSend ==false) {
//                return null;
//            }
//        }
//        //TODO: check for model type
//        modelType = models[0].getDat().getModType();
//        optCall = getOptions(modelType, iterations);
//        modeldiffsCall = "";
//
//        result = fitModel(listOfDatasets, listOfModels, modeldiffsCall, optCall);
//        return result;
//    }
//
//    private TimpResultDataset[] fitModel(String[] listOfDatasets, String[] listOfModels, String modeldiffsCall, String optResult) {
//        TimpResultDataset[] timpResults = null;
//        String cmd = null;
//        cmd = NAME_OF_RESULT_OBJECT + " <- fitModel(";
//        if (listOfDatasets != null) {
//            cmd = cmd.concat("data = list(");
//            for (int i = 0; i < listOfDatasets.length; i++) {
//                if (i > 0) {
//                    cmd = cmd + ",";
//                }
//                cmd = cmd.concat("dataset" + String.valueOf(i + 1));
//            }
//            cmd = cmd.concat(")");
//        }
//
//        if (listOfModels != null) {
//            cmd = cmd.concat(",modspec = list(");
//            for (int i = 0; i < listOfModels.length; i++) {
//                if (i > 1) {
//                    cmd = cmd + ",";
//                }
//                cmd = cmd.concat("model" + String.valueOf(i + 1));
//            }
//            cmd = cmd.concat(")");
//        }
//
//        if (modeldiffsCall != null || modeldiffsCall.isEmpty()) {
//            cmd = cmd.concat(",");
//            cmd = cmd.concat(modeldiffsCall);
//        }
//
//        if (optResult != null) {
//            cmd = cmd.concat(",");
//            cmd = cmd.concat(optResult);
//        }
//
//        cmd = cmd.concat(")");
//        addFitModelCall(cmd);
//        connection.voidEval("try("+cmd+")");
//        //TODO: store this somewhere, possible as private variable
//        if (getBool("exists(\""+NAME_OF_RESULT_OBJECT+"\")")) {
//        if (listOfDatasets != null) {
//            timpResults = new TimpResultDataset[listOfDatasets.length];
//            for (int i = 0; i < listOfDatasets.length; i++) {
//                timpResults[i] = getTimpResultDataset(listOfDatasets[i], i);
//            }
//            connection.voidEval("try(rm(list=ls()))");
//            connection.voidEval("try(gc())");
//        }
//        } else {
//            return null;
//        }
//        return timpResults;
//    }
}
