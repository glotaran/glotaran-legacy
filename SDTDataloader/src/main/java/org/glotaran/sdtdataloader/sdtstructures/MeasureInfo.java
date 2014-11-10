package org.glotaran.sdtdataloader.sdtstructures;

import org.glotaran.core.models.structures.BaseStructure;

/**
 *
 * @author Sergey
 */
public class MeasureInfo extends BaseStructure {

    public byte[] time = new byte[9];        /* time of creation */

    public byte[] date = new byte[11];       /* date of creation */

    public byte[] mod_ser_no = new byte[16]; /* serial number of the module */

    public short meas_mode;
    public float cfd_ll;
    public float cfd_lh;
    public float cfd_zc;
    public float cfd_hf;
    public float syn_zc;
    public short syn_fd;
    public float syn_hf;
    public float tac_r;
    public short tac_g;
    public float tac_of;
    public float tac_ll;
    public float tac_lh;
    public short adc_re;
    public short eal_de;
    public short ncx;
    public short ncy;
    public char page;   //unsigned short
    public float col_t;
    public float rep_t;
    public short stopt;
    public byte overfl;
    public short use_motor;
    public char steps; //unsigned short
    public float offset;
    public short dither;
    public short incr;
    public short mem_bank;
    public byte[] mod_type = new byte[16];   /* module type */

    public float syn_th;
    public short dead_time_comp;
    public short polarity_l;
    public short polarity_f;
    public short polarity_p;
    public short linediv;      // line predivider = 2 ** ( linediv)
    public short accumulate;
    public int flbck_y;
    public int flbck_x;
    public int bord_u;
    public int bord_l;
    public float pix_time;
    public short pix_clk;
    public short trigger;
    public int scan_x;
    public int scan_y;
    public int scan_rx;
    public int scan_ry;
    public short fifo_typ;
    public int epx_div;
    public char mod_type_code; //unsigned short
    public char mod_fpga_ver;    // unsigned short  new in v.8.4
    public float overflow_corr_factor;
    public int adc_zoom;
    public int cycles;        //  cycles ( accumulation cycles in FLOW mode )
    public MeasStopInfo StopInfo = new MeasStopInfo();
    public MeasFCSInfo FCSInfo = new MeasFCSInfo();   // valid only for FIFO meas
    public int image_x;       // 5 subsequent fields valid only for Camera mode
    public int image_y;       //
    public int image_rx;
    public int image_ry;
    public short xy_gain;       // gain for XY ADCs ( SPC930 )
    public short master_clock;  // use or not  Master Clock (SPC140 multi-module )
    public short adc_de;        // ADC sample delay ( SPC-930 )
    public short det_type;      // detector type ( SPC-930 in camera mode )
    public short x_axis;        // X axis representation ( SPC-930 )
    public MeasHISTInfo HISTInfo = new MeasHISTInfo(); // extension of FCSInfo, valid only for FIFO meas
}

