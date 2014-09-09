/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.resultdisplayers.common.panels;

import java.util.ArrayList;

/**
 *
 * @author slapten
 */
public class RelationFrom {

    public Integer indexFrom;
    public ArrayList<RelationTo> scaledDatasets;

    public RelationFrom(Integer index) {
        indexFrom = index;
        scaledDatasets = new ArrayList<RelationTo>();
    }
};
