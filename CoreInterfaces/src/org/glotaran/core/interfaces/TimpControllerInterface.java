/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.interfaces;

import java.util.ArrayList;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.core.models.tgm.Tgm;

/**
 *
 * @author jsg210
 */
public interface TimpControllerInterface {

    public static final String NAME_OF_RESULT_OBJECT = "gtaFitResult";
    public final static String NAME_OF_DATASET = "gtaDataset";
    public final static String NAME_OF_MODEL = "gtaModel";
    public static final String NAME_OF_SIM_OBJECT = "sim";
    TimpResultDataset[] runAnalysis(DatasetTimp[] datasets, Tgm[] models, int iterations);

    TimpResultDataset[] runAnalysis(DatasetTimp[] datasets, ArrayList<String> initModelCalls, String fitModelCall);

    DatasetTimp[] runSimulation(ArrayList<String> initModelCalls, String simModel);

    void cleanup();

    double getDouble(String cmd);

    double[] getDoubleArray(String cmd);

    String getString(String cmd);

    String[] getStringArray(String cmd);
    
    boolean isConnected();
}
