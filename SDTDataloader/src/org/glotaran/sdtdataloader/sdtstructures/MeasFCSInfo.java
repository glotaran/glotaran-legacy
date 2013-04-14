package org.glotaran.sdtdataloader.sdtstructures;

import org.glotaran.core.models.structures.BaseStructure;

/**
 *
 * @author Sergey
 * information collected when FIFO measurement is finished
 * 
 */
public class MeasFCSInfo extends BaseStructure {

    public short chan;               // routing channel number   unsigned
    public char fcs_decay_calc;     // bit 0 = 1 - decay curve calculated   unsigned short
    // bit 1 = 1 - fcs   curve calculated
    // bit 2 = 1 - FIDA  curve calculated
    // bit 3 = 1 - FILDA curve calculated
    // bit 4 = 1 - MCS curve calculated
    public long mt_resol;           // macro time clock in 0.1 ns units    unsigned int
    public float cortime;            // correlation time [ms]
    public int calc_photons;       //  no of photons    unsigned
    public int fcs_points;         // no of FCS values
    public float end_time;           // macro time of the last photon
    public short overruns;           // no of Fifo overruns   unsigned
    //   when > 0  fcs curve & end_time are not valid
    public char fcs_type;   // 0 - linear FCS with log binning ( 100 bins/log )   unsigned short
    // when bit 15 = 1 ( 0x8000 ) - Multi-Tau FCS
    //           where bits 14-0 = ktau parameter
    public char cross_chan;         // cross FCS routing channel number    unsigned short
    //   when chan = cross_chan and mod == cross_mod - Auto FCS
    //        otherwise - Cross FCS
    public char mod;                // module number    unsigned short
    public char cross_mod;          // cross FCS module number    unsigned short
    public long cross_mt_resol;     // macro time clock of cross FCS module in 0.1 ns units    unsigned int
}
