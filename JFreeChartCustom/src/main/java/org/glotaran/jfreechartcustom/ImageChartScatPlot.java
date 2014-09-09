/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import java.awt.image.BufferedImage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDataImageAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.Layer;

/**
 *
 * @author lsp
 */
public class ImageChartScatPlot {

    private JFreeChart chart;
    private PaintScale ps;
    double maxInt, minInt;
    int imheigh, imwidth;
    private XYZDataset dataset;
    private BufferedImage image;

    public ImageChartScatPlot() {
    }

    private JFreeChart createScatChart(BufferedImage image, int plotWidth, int plotHeigh) {
        JFreeChart chart_temp = ChartFactory.createScatterPlot(null,
                null, null, new XYSeriesCollection(), PlotOrientation.VERTICAL, false, false,
                false);

        XYDataImageAnnotation ann = new XYDataImageAnnotation(image, 0, 0,
                plotWidth, plotHeigh, true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.getRenderer().addAnnotation(ann, Layer.BACKGROUND);
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        xAxis.setVisible(false);
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);
        yAxis.setVisible(false);
        return chart_temp;

    }
}
