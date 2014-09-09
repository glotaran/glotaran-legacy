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
public class TxtHdr extends BaseStructure {

    public byte[] Ident = new byte[16];                 //"PicoHarp 300"
    public byte[] FormatVersion = new byte[6];		//file format version
    public byte[] CreatorName = new byte[18];		//name of creating software
    public byte[] CreatorVersion = new byte[12];	//version of creating software
    public byte[] FileTime = new byte[18];
    public byte[] CRLF = new byte[2];
    public byte[] CommentField = new byte[256]; 
    
}
