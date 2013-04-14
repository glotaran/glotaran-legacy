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
    public int RepeatMode;
    public int RepeatsPerCurve;
    public int RepeatTime;
    public int RepeatWaitTime;
    public byte[] ScriptName = new byte[20];
}
