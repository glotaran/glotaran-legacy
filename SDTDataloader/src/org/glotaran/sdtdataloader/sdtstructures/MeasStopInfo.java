package org.glotaran.sdtdataloader.sdtstructures;

import org.glotaran.core.models.structures.BaseStructure;

/**
 *
 * @author Sergey
 * information collected when measurement is finished
 *
 */
public class MeasStopInfo extends BaseStructure {

    public char status;         // last SPC_test_state return value ( status )       unsigned short
    public char flags;          // scan clocks bits 2-0( frame, line, pixel),               unsigned short
    //  rates_read - bit 15
    public float stop_time;      // time from start to  - disarm ( simple measurement )
    //    - or to the end of the cycle (for complex measurement )
    public int cur_step;       // current step  ( if multi-step measurement )
    public int cur_cycle;      // current cycle (accumulation cycle in FLOW mode ) -
    //  ( if multi-cycle measurement )
    public int cur_page;       // current measured page
    public float min_sync_rate;  // minimum rates during the measurement
    public float min_cfd_rate;   //   ( -1.0 - not set )
    public float min_tac_rate;
    public float min_adc_rate;
    public float max_sync_rate;  // maximum rates during the measurement
    public float max_cfd_rate;   //   ( -1.0 - not set )
    public float max_tac_rate;
    public float max_adc_rate;
    public int reserved1;
    public float reserved2;
}
