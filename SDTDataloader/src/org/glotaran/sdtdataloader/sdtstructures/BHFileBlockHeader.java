package org.glotaran.sdtdataloader.sdtstructures;

import org.glotaran.core.models.structures.BaseStructure;

/**
 *
 * @author Sergey
 */
public class BHFileBlockHeader extends BaseStructure {

    public short block_no;              // number of the block in the file
    // valid only  when in 0 .. 0x7ffe range, otherwise use lblock_no field
    // obsolete now, lblock_no contains full block no information
    public long data_offs;             // offset of the data block from the beginning of the file
    public long next_block_offs;       // offset to the data block header of the next data block
    public short block_type;            // see block_type defines below        unsigned
    public short meas_desc_block_no;    // Number of the measurement description block 
    //    corresponding to this data block
    public long lblock_no;             // long block_no - see remarks below      unsigned long
    public long block_length;          // reserved2 now contains block( set ) length    unsigned long
}
