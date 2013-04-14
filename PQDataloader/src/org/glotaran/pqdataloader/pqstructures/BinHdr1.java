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
    public int Curves;
    public int BitsPerRecord;
    public int RoutingChannels;
    public int NumberOfBoards;
    public int ActiveCurve;
    public int MeasMode;
    public int SubMode;
    public int RangeNo;
    public int Offset;
    public int Tacq;				// in ms
    public int StopAt;
    public int StopOnOvfl;
    public int Restart;
    public int DispLinLog;
    public int DispTimeFrom;		// 1ns steps
    public int DispTimeTo;
    public int DispCountsFrom;
    public int DispCountsTo;
    
}
