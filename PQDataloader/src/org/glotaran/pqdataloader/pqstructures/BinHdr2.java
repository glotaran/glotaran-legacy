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
public class BinHdr2 extends BaseStructure {
/* The following is binary file header information  end*/
    public int RepeatMode;                  //RepeatMode (int32)
    public int RepeatsPerCurve;             // RepeatsPerCurve (int32)
    public int RepeatTime;                  // RepeatTime (int32)
    public int RepeatWaitTime;              // RepeatTime (int32)
    public byte[] ScriptName = new byte[20]; // ScriptName (char[20])
}
