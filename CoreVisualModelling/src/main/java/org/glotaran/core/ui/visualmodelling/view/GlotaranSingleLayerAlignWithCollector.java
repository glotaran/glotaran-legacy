/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.view;

/**
 *
 * @author jsg210
 */
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.action.AlignWithWidgetCollector;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author jsg210
 * Adapted from David Kaspar's SingleLayerAlignWithCollector
 */
public final class GlotaranSingleLayerAlignWithCollector implements AlignWithWidgetCollector {

    private LayerWidget collectionLayer;
    private boolean outerBounds;

    public GlotaranSingleLayerAlignWithCollector(LayerWidget collectionLayer, boolean outerBounds) {
        this.collectionLayer = collectionLayer;
        this.outerBounds = outerBounds;
    }

    public java.util.List<Rectangle> getRegions(Widget movingWidget) {
        java.util.List<Widget> children = collectionLayer.getChildren();
        ArrayList<Rectangle> regions = new ArrayList<Rectangle>(children.size());
        for (Widget widget : children) {
            if (widget != movingWidget) {
                regions.add(widget.convertLocalToScene(outerBounds ? widget.getBounds() : widget.getClientArea()));
            }
        }
        return regions;
    }
}
