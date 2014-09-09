package org.glotaran.sdtdataloader.sdtstructures;

import org.glotaran.core.models.structures.BaseStructure;

/**
 *
 * @author Sergey
 * extension of MeasFCSInfo for other histograms ( FIDA, FILDA, MCS ) 
 */
public class MeasHISTInfo extends BaseStructure {

    public float fida_time;          // interval time [ms] for FIDA histogram
    public float filda_time;         // interval time [ms] for FILDA histogram
    public int fida_points;        // no of FIDA values
    public int filda_points;       // no of FILDA values
    public float mcs_time;            // interval time [ms] for MCS histogram
    public int mcs_points;          // no of MCS values
}
