/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes.dataobjects;

/**
 *
 * @author slapten
 */
public class MeasuredIrfDO {

    private String filepath;

    public MeasuredIrfDO() {
        filepath = "Path to measured irf file";
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
