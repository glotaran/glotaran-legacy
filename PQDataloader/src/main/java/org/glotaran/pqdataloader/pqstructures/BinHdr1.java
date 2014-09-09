/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.pqdataloader.pqstructures;

import org.glotaran.core.models.structures.BaseStructure;

/**
 *
 * @author Owner
 */
public class BinHdr1 extends BaseStructure {
/* The following is binary file header information  beginning*/
    public int NumberOfCurves;                       // NumberOfCurves (int32)
    public int BitsPerRecord;                // BitsPerHistoBin (int32)
    public int RoutingChannels;              // RoutingChannels (int32)
    public int NumberOfBoards;               // NumberOfBoards (int32)
    public int ActiveCurve;                  // ActiveCurve (int32)
    public int MeasMode;                     // MeasurementMode (int32)
    public int SubMode;                      // SubMode (int32)
    public int RangeNo;                      // RangeNo (int32)
    public int Offset;                       // Offset (int32)
    public int Tacq;                         // Tacq (int32) Acquisition Time in ms
    public int StopAt;                       // StopAt (int32)
    public int StopOnOvfl;                   // StopOnOvfl (int32) Stop on overflow
    public int Restart;                       // Restart (int32)
    public int DispLinLog;                   // DispLinLog (int32)
    public int DispTimeFrom;		      // DispTimeAxisFrom (int32) Time axis from in ns
    public int DispTimeTo;                   // DispTimeAxisTo (int32) Time axis to in ns
    public int DispCountsFrom;               // DispCountAxisFrom (int32) Count axis from
    public int DispCountsTo;                 // DispCountAxisTo (int32) Count axis to
    
}
