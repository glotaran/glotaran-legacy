/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.pqdataloader.pqstructures;

import org.glotaran.core.models.structures.BaseStructure;

/**
 *
 * @author sergey
 */
public class BoardHdr extends BaseStructure {
    /* The next is a board specific header */
    public byte[] HardwareIdent = new byte[16];
    public byte[] HardwareVersion = new byte[8];
    public int HardwareSerial;
    public int SyncDivider;
    public int CFDZeroCross0;
    public int CFDLevel0;
    public int CFDZeroCross1;
    public int CFDLevel1;
    public float Resolution;
//below is new in format version 2.0
    public int RouterModelCode;
    public int RouterEnabled;
    public int RtChan1_InputType;
    public int RtChan1_InputLevel;
    public int RtChan1_InputEdge;
    public int RtChan1_CFDPresent;
    public int RtChan1_CFDLevel;
    public int RtChan1_CFDZeroCross;
    public int RtChan2_InputType;
    public int RtChan2_InputLevel;
    public int RtChan2_InputEdge;
    public int RtChan2_CFDPresent;
    public int RtChan2_CFDLevel;
    public int RtChan2_CFDZeroCross;
    public int RtChan3_InputType;
    public int RtChan3_InputLevel;
    public int RtChan3_InputEdge;
    public int RtChan3_CFDPresent;
    public int RtChan3_CFDLevel;
    public int RtChan3_CFDZeroCross;
    public int RtChan4_InputType;
    public int RtChan4_InputLevel;
    public int RtChan4_InputEdge;
    public int RtChan4_CFDPresent;
    public int RtChan4_CFDLevel;
    public int RtChan4_CFDZeroCross;
}
