/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.view;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import org.glotaran.core.models.gta.GtaDatasetContainer;
import org.glotaran.core.models.gta.GtaModelReference;
import org.glotaran.core.models.gta.GtaOutput;
import org.glotaran.core.models.gta.GtaSimulationContainer;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.AlignWithMoveDecorator;
import org.netbeans.api.visual.action.AlignWithWidgetCollector;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jsg210
 * Adapted from David Kaspar's AlignWithMoveStrategyProvider
 */
public final class GlotaranAlignWithMoveStrategyProvider extends GlotaranAlignWithSupport implements MoveStrategy, MoveProvider {

    private boolean outerBounds;

    public GlotaranAlignWithMoveStrategyProvider(AlignWithWidgetCollector collector, LayerWidget interractionLayer, AlignWithMoveDecorator decorator, boolean outerBounds) {
        super(collector, interractionLayer, decorator);
        this.outerBounds = outerBounds;
    }

    public Point locationSuggested(Widget widget, Point originalLocation, Point suggestedLocation) {
        Point widgetLocation = widget.getLocation();
        Rectangle widgetBounds = outerBounds ? widget.getBounds() : widget.getClientArea();
        Rectangle bounds = widget.convertLocalToScene(widgetBounds);
        bounds.translate((suggestedLocation.x - widgetLocation.x), (suggestedLocation.y - widgetLocation.y));
        Insets insets = widget.getBorder().getInsets();
        if (!outerBounds) {
            suggestedLocation.x += insets.left;
            suggestedLocation.y += insets.top;
        }
        Point point = super.locationSuggested(widget, bounds, widget.getParentWidget().convertLocalToScene(suggestedLocation), true, true, true, true);
        if (!outerBounds) {
            point.x -= insets.left;
            point.y -= insets.top;
        }
        return widget.getParentWidget().convertSceneToLocal(point);
    }

    public void movementStarted(Widget widget) {
        show();
    }

    public void movementFinished(Widget widget) {
        GlotaranGraphScene scene = (GlotaranGraphScene) widget.getScene();
        if (scene.findObject(widget) instanceof GtaModelReference) {
            ((GtaModelReference) scene.findObject(widget)).getLayout().setXposition(widget.getPreferredLocation().getX());
            ((GtaModelReference) scene.findObject(widget)).getLayout().setYposition(widget.getPreferredLocation().getY());
        }
        if (scene.findObject(widget) instanceof GtaDatasetContainer) {
            ((GtaDatasetContainer) scene.findObject(widget)).getLayout().setXposition(widget.getPreferredLocation().getX());
            ((GtaDatasetContainer) scene.findObject(widget)).getLayout().setYposition(widget.getPreferredLocation().getY());
        }
        if (scene.findObject(widget) instanceof GtaOutput) {
            ((GtaOutput) scene.findObject(widget)).getLayout().setXposition(widget.getPreferredLocation().getX());
            ((GtaOutput) scene.findObject(widget)).getLayout().setYposition(widget.getPreferredLocation().getY());
        }
        if (scene.findObject(widget) instanceof GtaSimulationContainer) {
            ((GtaSimulationContainer) scene.findObject(widget)).getLayout().setXposition(widget.getPreferredLocation().getX());
            ((GtaSimulationContainer) scene.findObject(widget)).getLayout().setYposition(widget.getPreferredLocation().getY());
        }
        scene.getDobj().setModified(true);
        hide();
    }

    public Point getOriginalLocation(Widget widget) {
        return ActionFactory.createDefaultMoveProvider().getOriginalLocation(widget);
    }

    public void setNewLocation(Widget widget, Point location) {
        ActionFactory.createDefaultMoveProvider().setNewLocation(widget, location);
    }
}
