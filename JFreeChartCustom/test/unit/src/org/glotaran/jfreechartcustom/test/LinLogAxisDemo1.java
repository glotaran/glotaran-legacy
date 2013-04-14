/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.glotaran.jfreechartcustom.test;

/**
 *
 * @author jsg210
 */
/* -----------------
 * LogAxisDemo1.java
 * -----------------
 * (C) Copyright 2006-2009, by Object Refinery Limited.
 */

import java.awt.BasicStroke;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LinLogAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demo showing the use of the {@link LogAxis} class.
 */
public class LinLogAxisDemo1 extends ApplicationFrame {

    private final static long serialVersionUID = 1L;

    /**
     * Creates a new instance of the demo.
     *
     * @param title  the frame title.
     */
    public LinLogAxisDemo1(String title) {
        super(title);
        JPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createScatterPlot(
            "Lin-Log Axis Demo 1",
            "X",
            "Y",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.setDomainGridlineStroke(new BasicStroke(1.0f));
        plot.setRangeGridlineStroke(new BasicStroke(1.0f));
        plot.setDomainMinorGridlinesVisible(true);
        plot.setRangeMinorGridlinesVisible(true);
        plot.setDomainMinorGridlineStroke(new BasicStroke(0.1f));
        plot.setRangeMinorGridlineStroke(new BasicStroke(0.1f));
        //plot.setRenderer(new XYLineAndShapeRenderer());
        LinLogAxis xAxis = new LinLogAxis("X",2,-2, 0.4);
        //LinLogAxis xAxis = new LinLogAxis("X",0,-1, 0.4);
        //LinLogAxis xAxis = new LinLogAxis("X");
        //LogAxis xAxis = new LogAxis("X");
        NumberAxis yAxis = new NumberAxis("Y");
        plot.setDomainAxis(xAxis);
        plot.setRangeAxis(yAxis);
        // make sure the current theme is applied to the axes just added
        ChartUtilities.applyCurrentTheme(chart);
        return chart;
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private static XYDataset createDataset() {
        XYSeries series = new XYSeries("Random Data");
        series.add(1,9);
        series.add(2,10);       // #10
        series.add(3,11);
        series.add(4,12);
        series.add(100,13);
        series.add(1000,14);
        return new XYSeriesCollection(series);
    }

        /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private static XYDataset createLinDataset() {
        XYSeries series = new XYSeries("Random Data");        
        series.add(-1,1);
        series.add(0,2);
        series.add(1,3);       // #10
        series.add(2,4);
        series.add(3,5);
        series.add(4,6);
        series.add(5,7);
        series.add(6,8);    // #1
        series.add(7,9);    // #1
        series.add(8,10);    // #1
        series.add(9,11);    // #1
        series.add(10,12);    // #1
        return new XYSeriesCollection(series);
    }

        private static XYDataset createLinLogDataset2() {
        XYSeries series = new XYSeries("Random Data");
        series.add(-10000,0);    // #1
        series.add(-1000,1);    // #1
        series.add(-100,2);
        series.add(-10,3);
        series.add(-1,4);
        series.add(0,5);       // #5
        series.add(1,6);
        series.add(2,7);
        series.add(3,8); //<-centerValues
        series.add(4,9);
        series.add(5,10);       // #10
        series.add(6,11);
        series.add(10,12);
        series.add(30,13);
        series.add(50,14);
        series.add(75,15);    // #1

        series.add(100,16);    // #1
        series.add(1000,17);    // #1
        series.add(10000,18);    // #1
        return new XYSeriesCollection(series);
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createLinLogDataset2());
        //JFreeChart chart = createChart(createDataset());
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        LinLogAxisDemo1 demo = new LinLogAxisDemo1(
                "JFreeChart: LinLogAxisDemo1.java");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
