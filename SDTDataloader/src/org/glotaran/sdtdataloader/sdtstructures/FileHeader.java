package org.glotaran.sdtdataloader.sdtstructures;

import org.glotaran.core.models.structures.BaseStructure;

/*
import java.io.IOException;
import java.lang.reflect.Field;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Sergey
 */

public class FileHeader extends BaseStructure {

    public short revision;  // software revision number  (lower 4 bits >= 10(decimal))
    public long info_offs; // offset of the info part which contains general text
    // information (Title, date, time, contents etc.)
    public short info_length;  // length of the info part
    public long setup_offs;   // offset of the setup text data
    // (system parameters, display parameters, trace parameters etc.)
    public short setup_length;  // length of the setup data
    public long data_block_offs;   // offset of the first data block
    public short no_of_data_blocks; // no_of_data_blocks valid only when in 0 .. 0x7ffe range,
    // if equal to 0x7fff  the  field 'reserved1' contains
    // valid no_of_data_blocks
    public long data_block_length;     // length of the longest block in the file
    public long meas_desc_block_offs;  // offset to 1st. measurement description block
    //   (system parameters connected to data blocks)
    public short no_of_meas_desc_blocks;  // number of measurement description blocks
    public short meas_desc_block_length;  // length of the measurement description blocks
    //unsigned
    public char header_valid;   // valid: 0x5555, not valid: 0x1111
    //unsigned
    public long reserved1;      // reserved1 now contains no_of_data_blocks
    //unsigned
    public char reserved2;
    //unsigned
    public char chksum;            // checksum of file header
}



