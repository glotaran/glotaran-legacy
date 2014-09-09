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
public class TTTRHdr extends BaseStructure {
    public int ExtDevices;
    public int Reserved1;
    public int Reserved2;
    public int CntRate0;
    public int CntRate1;
    public int StopAfter;
    public int StopReason;
    public int Records;
    public int ImgHdrSize;
}
