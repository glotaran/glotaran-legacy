package org.glotaran.jfreechartcustom;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.RendererUtilities;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

/**
 * Performs fast rendering of large datasets in nearly constant time.
 */
public class FastXYPlot extends XYPlot {

    private final HashSet<Integer> renderedPixels = new HashSet<Integer>();

    public FastXYPlot(XYDataset dataset, ValueAxis domainAxis, ValueAxis rangeAxis, XYItemRenderer renderer) {
        super(dataset, domainAxis, rangeAxis, renderer);
    }

    /**
     * Determines if the item is to be rendered in the area of the plot where one of the previous
     * items has already been rendered.
     */
    private boolean hasRendered(XYDataset dataset, ValueAxis xAxis, ValueAxis yAxis, RectangleEdge domainEdge,
            RectangleEdge rangeEdge, Rectangle2D dataArea, int series, int item) {
        boolean hasRendered = true;

        int width = (int) dataArea.getWidth();

        double xValue = dataset.getXValue(series, item);
        double yValue = dataset.getYValue(series, item);
        int x = (int) xAxis.valueToJava2D(xValue, dataArea, domainEdge);
        int y = (int) yAxis.valueToJava2D(yValue, dataArea, rangeEdge);

        int itemKey = x + width * y;
        if (!renderedPixels.contains(itemKey)) {
            renderedPixels.add(itemKey);
            hasRendered = false;
        }

        return hasRendered;
    }

    @Override
    public boolean render(Graphics2D g2,
            Rectangle2D dataArea,
            int index,
            PlotRenderingInfo info,
            CrosshairState crosshairState) {


        boolean foundData = false;
        boolean disableOptimization = false;

        XYDataset dataset = getDataset(index);

        if (!DatasetUtilities.isEmptyOrNull(dataset)) {
            foundData = true;
            ValueAxis xAxis = getDomainAxisForDataset(index);
            ValueAxis yAxis = getRangeAxisForDataset(index);
            XYItemRenderer renderer = getRenderer(index);
            if (renderer == null) {
                renderer = getRenderer();
                if (renderer == null) { // no default renderer available
                    return foundData;
                }
            }

            XYItemRendererState state = renderer.initialise(g2, dataArea, this,
                    dataset, info);
            int passCount = renderer.getPassCount();

            if (renderer instanceof XYLineAndShapeRenderer) {
                disableOptimization = ((XYLineAndShapeRenderer) renderer).getDrawSeriesLineAsPath();   // in case of e.g. splines
            }

            RectangleEdge domainEdge = getDomainAxisEdge();
            RectangleEdge rangeEdge = getDomainAxisEdge();

            SeriesRenderingOrder seriesOrder = getSeriesRenderingOrder();
            if (seriesOrder == SeriesRenderingOrder.REVERSE) {
                //render series in reverse order
                for (int pass = 0; pass < passCount; pass++) {
                    renderedPixels.clear();                     // need to clear every pass or else shapes won't be drawn correctly
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = seriesCount - 1; series >= 0; series--) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (lastItem == -1) {
                            continue;
                        }
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtilities.findLiveItems(
                                    dataset, series, xAxis.getLowerBound(),
                                    xAxis.getUpperBound());
                            firstItem = itemBounds[0];
                            lastItem = itemBounds[1];
                        }
                        for (int item = firstItem; item <= lastItem; item++) {
                            if (disableOptimization || !hasRendered(dataset, xAxis, yAxis, domainEdge, rangeEdge, dataArea, series, item)) {
                                renderer.drawItem(g2, state, dataArea, info,
                                        this, xAxis, yAxis, dataset, series, item,
                                        crosshairState, pass);
                            }
                        }
                    }
                }
            } else {
                //render series in forward order
                for (int pass = 0; pass < passCount; pass++) {
                    renderedPixels.clear();                     // need to clear every pass or else shapes won't be drawn correctly
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = 0; series < seriesCount; series++) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtilities.findLiveItems(
                                    dataset, series, xAxis.getLowerBound(),
                                    xAxis.getUpperBound());
                            firstItem = itemBounds[0];
                            lastItem = itemBounds[1];
                        }
                        for (int item = firstItem; item <= lastItem; item++) {
                            if (disableOptimization || !hasRendered(dataset, xAxis, yAxis, domainEdge, rangeEdge, dataArea, series, item)) {
                                renderer.drawItem(g2, state, dataArea, info,
                                        this, xAxis, yAxis, dataset, series, item,
                                        crosshairState, pass);
                            }
                        }
                    }
                }
            }
        }
        return foundData;
    }
}
