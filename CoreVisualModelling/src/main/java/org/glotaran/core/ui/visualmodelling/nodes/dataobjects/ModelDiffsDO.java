/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes.dataobjects;

import org.glotaran.core.models.gta.GtaModelDiffDO;

/**
 *
 * @author jsg210
 */
public class ModelDiffsDO extends AbstractParameterDO {

    private String what;
    private Integer index;
    private Integer dataset;
    private Double start;

    public ModelDiffsDO() {
        what = null;
        index = null;
        dataset = null;
        start = null;
    }

    public ModelDiffsDO(int ind) {
        what = null;
        index = 0;
        dataset = ind;
        start = 0.0;
    }

    public ModelDiffsDO(GtaModelDiffDO gtaDO) {
        what = gtaDO.getWhat();
        index = gtaDO.getIndex() - 1;
        dataset = gtaDO.getDataset();
        start = gtaDO.getStart();
    }

    public Integer getDataset() {
        return dataset;
    }

    public void setDataset(Integer dataset) {
        this.dataset = dataset;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
        fire("index", null, index);
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
        fire("what", null, what);
    }

    public Double getStart() {
        return start;
    }

    public void setStart(Double value) {
        Double oldStart = start;
        start = value;
        fire("start", oldStart, start);
    }
}
