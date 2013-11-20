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
public class CurveHdr extends BaseStructure {
    /* The next is a board specific header */
    public int CurveIndex;                      //Curve Index
    public int TimeOfRecording;                 //uint! (32 bit) TimeOfRecording(i)/24/60/60+25569+693960;
    public byte[] HardwareIdent = new byte[16];  //Hardware Identifier
    public byte[] HardwareVersion = new byte[8]; //Hardware Version
    public int HardwareSerial;                  //HW Serial Number
    public int SyncDivider;                     //Sync divider
    public int CFDZeroCross0;                    //CFD 0 ZeroCross (in mV)
    public int CFDLevel0;                        //CFD 0 Discr (in mV)
    public int CFDZeroCross1;                    //CFD 1 ZeroCross (in mV)
    public int CFDLevel1;                        //CFD 1 Discr (in mV)
    public int Offset;                           //
    public int RoutingChannel;                           //
    public int ExtDevices;                           //
    public int MeasMode;                           //
    public int SubMode;                           //
    public float P1;                     //
    public float P2;                     //
    public float P3;                     //
    public int RangeNo;                           //
    public float Resolution;                     //CFD 1 Discr (in mV) //Resolution (in ns)
    public int Channels;                           //
    public int Tacq;                           //Acquisition Time (ms)
    public int StopAfter;                           //
    public int StopReason;                           //
    public int InpRate0;                           //
    public int InpRate1;                           //
    public int HistCountRate;                           //
    public long IntegralCount; //64 bit signed integer
    public int Reserved;                           //
    public int DataOffset;                           //
    //below is new in format version 2.0
    public int RouterModelCode;
    public int RouterEnabled;
    public int RtChan_InputType;
    public int RtChan_InputLevel;
    public int RtChan_InputEdge;
    public int RtChan_CFDPresent;
    public int RtChan_CFDLevel;
    public int RtChan_CFDZeroCross;    
}
