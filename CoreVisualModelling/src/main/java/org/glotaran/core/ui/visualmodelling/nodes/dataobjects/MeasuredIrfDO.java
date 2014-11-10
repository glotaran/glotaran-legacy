/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes.dataobjects;

import org.glotaran.core.ui.visualmodelling.common.EnumTypes;

/**
 * @author slapten
 */
public class MeasuredIrfDO extends AbstractParameterDO{

    private String filepath;
    private Double refLifetime;
    private EnumTypes.ConvolutionTypes convolutionTypeProperty;
    

    public MeasuredIrfDO() {
        filepath = "Path to measured irf file";
        convolutionTypeProperty = EnumTypes.ConvolutionTypes.SCATTERCONVOLUTION;
        refLifetime = new Double(0);
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
    public Double getRefLifetime(){
        return refLifetime;
    }
    
    public void setRefLifetime(Double value) {
        Double oldRefLifetime = refLifetime;
        this.refLifetime = value;
//        fire("reflifetime", oldRefLifetime, refLifetime);
    }
    
    public EnumTypes.ConvolutionTypes getConvolutionType(){
        return convolutionTypeProperty;
    }
        
    public final void setConvolutionType(EnumTypes.ConvolutionTypes value){
        EnumTypes.ConvolutionTypes oldVal = convolutionTypeProperty;
        this.convolutionTypeProperty = value;
        fire("convType", oldVal, convolutionTypeProperty);
    } 
}
