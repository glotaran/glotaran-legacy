/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.resultdisplayers.common.panels;

import org.ujmp.jama.JamaDenseDoubleMatrix2D;
import org.ujmp.core.Matrix;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.jfreechartcustom.GlotaranDrawingSupplier;
import org.glotaran.jfreechartcustom.GraphPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LinLogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import static java.lang.Math.ceil;
import static java.lang.Math.pow;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.ui.HorizontalAlignment;

/**
 *
 * @author slapten
 */
public class CommonResDispTools {

    private static final int CHART_SIZE = 200;
    private static final int LAYOUT_GAP = 2;
    private static final int REPORT_PANEL_DEFAULT_WIDTH = 900;
    private static final int REPORT_PANEL_DEFAULT_HEIGHT = 810;

    public static void checkPanelSize(JPanel panelToResize, int numSelTraces) {
        int rowNum = (int) ceil((double) numSelTraces / 4);
        if (rowNum > 4) {
            panelToResize.setPreferredSize(new Dimension(REPORT_PANEL_DEFAULT_WIDTH, rowNum * CHART_SIZE + LAYOUT_GAP * (rowNum + 1)));
        }
        GridLayout gl = (GridLayout) panelToResize.getLayout();
        if (numSelTraces / 4 >= gl.getRows()) {
            panelToResize.setLayout(new GridLayout(rowNum, 4, LAYOUT_GAP, LAYOUT_GAP));
        }
    }

    public static void restorePanelSize(JPanel panelToResize) {
        panelToResize.setPreferredSize(new Dimension(REPORT_PANEL_DEFAULT_WIDTH, REPORT_PANEL_DEFAULT_HEIGHT));
        panelToResize.setLayout(new GridLayout(2, 2));
    }

    public static NumberAxis createLinAxis(double[] waves, String name){
        NumberAxis xAxis = new NumberAxis(name);
        if (waves[waves.length - 1] < waves[0]) {
            xAxis.setUpperBound(waves[0]);
            xAxis.setLowerBound(waves[waves.length - 1]);
        } else {
            xAxis.setLowerBound(waves[0]);
            xAxis.setUpperBound(waves[waves.length - 1]);
        }
        xAxis.setAutoRangeIncludesZero(false);
        xAxis.setAutoRange(false);
        return xAxis;
    }
    
    public static GraphPanel makeLinTimeTraceResidChart(XYSeriesCollection trace, XYSeriesCollection residuals, ValueAxis xAxis, String name, boolean multy) {
        return makeLinTimeTraceResidChart(trace,residuals,xAxis,name,multy,false);
    }
    
    public static GraphPanel makeLinTimeTraceResidChart(XYSeriesCollection trace, XYSeriesCollection residuals, ValueAxis xAxis, String name, boolean multy, boolean showLegend) {
        GlotaranDrawingSupplier drawSuplTrace = multy ? new GlotaranDrawingSupplier(multy) : new GlotaranDrawingSupplier();
        GlotaranDrawingSupplier drawSuplResid = new GlotaranDrawingSupplier();
        JFreeChart subchartResiduals = ChartFactory.createXYLineChart(
                null,
                null,
                null,
                residuals,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        JFreeChart subchartTrace = ChartFactory.createXYLineChart(
                null,
                null,
                null,
                trace,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        XYPlot plot1_1 = subchartTrace.getXYPlot();
        plot1_1.getDomainAxis().setLowerMargin(0.0);
        plot1_1.getDomainAxis().setUpperMargin(0.0);
        plot1_1.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        plot1_1.getDomainAxis().setInverted(true);
        plot1_1.setRangeZeroBaselineVisible(true);
        plot1_1.getRangeAxis().setAutoRange(false);
        for (int i = 0; i < trace.getSeriesCount(); i++) {
            plot1_1.getRenderer().setSeriesPaint(i, drawSuplTrace.getNextPaint());
            plot1_1.getRenderer().setSeriesStroke(i, drawSuplTrace.getNextStroke());
        }
        XYPlot plot1_2 = subchartResiduals.getXYPlot();
        plot1_2.getDomainAxis().setLowerMargin(0.0);
        plot1_2.getDomainAxis().setUpperMargin(0.0);
        plot1_2.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        plot1_2.getDomainAxis().setInverted(true);
        plot1_2.setRangeZeroBaselineVisible(true);
        plot1_2.getRangeAxis().setAutoRange(false);
        for (int i = 0; i < residuals.getSeriesCount(); i++) {
            plot1_2.getRenderer().setSeriesPaint(i, drawSuplResid.getNextPaint());
        }
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(xAxis);
        plot.setGap(5.0);
        plot.add(plot1_1, 3);
        plot.add(plot1_2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.getDomainAxis().setLowerMargin(0.0);
        plot.getDomainAxis().setUpperMargin(0.0);
        Font titleFont = new Font(JFreeChart.DEFAULT_TITLE_FONT.getFontName(), JFreeChart.DEFAULT_TITLE_FONT.getStyle(), 12);
        JFreeChart tracechart = new JFreeChart(name, titleFont, plot, showLegend);
        if (showLegend){
            LegendTitle legend;
            legend = new LegendTitle(plot1_2);
            legend.setPosition(RectangleEdge.RIGHT);
            legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
            tracechart.addSubtitle(legend);
        }
        GraphPanel chpan = new GraphPanel(tracechart, false);
        chpan.setMinimumDrawHeight(0);
        chpan.setMinimumDrawWidth(0);
        return chpan;
    }

    public static GraphPanel createLinLogTimeTraceResidChart(XYSeriesCollection trace, XYSeriesCollection resid, String name, boolean multy, double linearBoundValue) {
        return createLinLogTimeTraceResidChart(trace, resid, name, multy, linearBoundValue, false);
    }
    
public static GraphPanel createLinLogTimeTraceResidChart(XYSeriesCollection trace, XYSeriesCollection resid, String name, boolean multy, double linearBoundValue, boolean showLegend) {
        NumberAxis yAxis = new NumberAxis();
        yAxis.setVisible(false);
        LinLogAxis xAxis = new LinLogAxis("Time",linearBoundValue,-linearBoundValue,0.4);
        GraphPanel linTime = CommonResDispTools.makeLinTimeTraceResidChart(trace, resid, xAxis, name, multy, showLegend);
        ((XYPlot)linTime.getChart().getPlot()).setDomainAxis(xAxis);
        
        return linTime;
    }

//    public static GraphPanel makeLinLogTimeTraceChart(XYSeriesCollection traceLin, XYSeriesCollection traceLog, String name, boolean multy) {
//        NumberAxis yAxis = new NumberAxis();
//        yAxis.setVisible(false);
//        GlotaranDrawingSupplier drawSuplLin = new GlotaranDrawingSupplier();
//        GlotaranDrawingSupplier drawSuplLog = new GlotaranDrawingSupplier();
//
//        JFreeChart linePart, logPart;
//
//        linePart = ChartFactory.createXYLineChart(
//                null,
//                "Time",
//                null,
//                traceLin,
//                PlotOrientation.VERTICAL,
//                false,
//                false,
//                false);
//        logPart = ChartFactory.createXYLineChart(
//                null,
//                "log(Time)",
//                null,
//                traceLog,
//                PlotOrientation.VERTICAL,
//                false,
//                false,
//                false);
//
//        XYPlot linPlot = linePart.getXYPlot();
//        linPlot.getDomainAxis().setLowerMargin(0.0);
//        linPlot.getDomainAxis().setUpperMargin(0.0);
//        linPlot.setRangeZeroBaselineVisible(true);
//
//        XYPlot logPlot = logPart.getXYPlot();
//
//        logPlot.setDomainAxis(new LogAxis("log(Time)"));
//        logPlot.getDomainAxis().setLabelFont(linPlot.getDomainAxis().getLabelFont());
//        logPlot.getDomainAxis().setLowerMargin(0.0);
//        logPlot.getDomainAxis().setUpperMargin(0.0);
//        logPlot.setRangeZeroBaselineVisible(true);
//        for (int i = 0; i < traceLog.getSeriesCount(); i++) {
//            linPlot.getRenderer().setSeriesPaint(i, drawSuplLin.getNextPaint());
//            logPlot.getRenderer().setSeriesPaint(i, drawSuplLog.getNextPaint());
//        }
//
//        CombinedRangeXYPlot plot = new CombinedRangeXYPlot(yAxis);
//        plot.setGap(-7.5);
//        plot.add(linPlot);
//        plot.add(logPlot);
//
//        JFreeChart tracechart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
//        LegendTitle legend = new LegendTitle(linPlot);
//        legend.setPosition(RectangleEdge.BOTTOM);
//        tracechart.removeLegend();
//        legend.setVisible(false);
//        tracechart.addLegend(legend);
//        return new GraphPanel(tracechart, false);
//    }

    public static GraphPanel makeLinLogTimeTraceChart(XYSeriesCollection trace,String name, boolean multy, double linearBoundValue) {
        NumberAxis yAxis = new NumberAxis();
        yAxis.setVisible(false);
        GlotaranDrawingSupplier drawSuplLin = new GlotaranDrawingSupplier();

        JFreeChart chart = ChartFactory.createXYLineChart(
                null,
                "Time",
                null,
                trace,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        LinLogAxis xAxis = new LinLogAxis("Time", linearBoundValue, -linearBoundValue, 0.4);
        chart.getXYPlot().setDomainAxis(xAxis);

        XYPlot plot = chart.getXYPlot();
//        plot.getDomainAxis().setLowerMargin(0.0);
//        plot.getDomainAxis().setUpperMargin(0.0);
        plot.setRangeZeroBaselineVisible(true);

        for (int i = 0; i < trace.getSeriesCount(); i++) {
            plot.getRenderer().setSeriesPaint(i, drawSuplLin.getNextPaint());

        }

        JFreeChart tracechart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        LegendTitle legend = new LegendTitle(plot);
        legend.setPosition(RectangleEdge.BOTTOM);
        tracechart.removeLegend();
        legend.setVisible(false);
        tracechart.addLegend(legend);
        return new GraphPanel(tracechart, true);
    }

    public static XYSeriesCollection createFitRawTraceCollection(int xIndex, int startInd, int stopInd, TimpResultDataset data) {
        return createFitRawTraceCollection(xIndex, startInd, stopInd, data, 0, "");
    }

    public static XYSeriesCollection createFitRawTraceCollection(int xIndex, int startInd, int stopInd, TimpResultDataset data, double t0, String name) {
        return createFitRawTraceCollection(xIndex, startInd, stopInd, data, t0, name, 1);
    }

    public static XYSeriesCollection createFitRawTraceCollection(int xIndex, int startInd, int stopInd, TimpResultDataset data, double t0, String name, double scaleVal) {

        XYSeriesCollection trace = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Trace" + name);
        XYSeries series2 = new XYSeries("Fit" + name);
        Matrix tracesMatrix = new JamaDenseDoubleMatrix2D((Jama.Matrix)data.getTraces());
        Matrix fittedTracesMatrix = new JamaDenseDoubleMatrix2D((Jama.Matrix)data.getFittedTraces());

        for (int j = startInd; j < stopInd; j++) {
            series1.add(data.getX()[j] - t0, tracesMatrix.getAsDouble(j, xIndex) / scaleVal);
            series2.add(data.getX()[j] - t0, fittedTracesMatrix.getAsDouble(j, xIndex) / scaleVal);
        }
        trace.addSeries(series1);
        trace.addSeries(series2);
        return trace;
    }
    
    public static XYSeriesCollection createFitRawWaveTrCollection(int yIndex, int startInd, int stopInd, TimpResultDataset data, String name){
        return createFitRawWaveTrCollection(yIndex, startInd, stopInd, data, name, 1.0);
    }
    
    public static XYSeriesCollection createFitRawWaveTrCollection(int yIndex, int startInd, int stopInd, TimpResultDataset data, String name, double scaleVal) {
        XYSeriesCollection trace = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Trace" + name);
        XYSeries series2 = new XYSeries("Fit" + name);
        
        Matrix tracesMatrix = new JamaDenseDoubleMatrix2D((Jama.Matrix)data.getTraces());
        Matrix fittedTracesMatrix = new JamaDenseDoubleMatrix2D((Jama.Matrix)data.getFittedTraces());
        
        for (int i = startInd; i < stopInd; i++ ){
            series1.add(data.getX2()[i], tracesMatrix.getAsDouble(yIndex, i) / scaleVal);
            series2.add(data.getX2()[i], fittedTracesMatrix.getAsDouble(yIndex, i) / scaleVal);
        }   
        trace.addSeries(series1);
        trace.addSeries(series2);
        return trace;
    }
    
    public static XYSeriesCollection createResidWaveCollection(int xIndex, int startInd, int stopInd, TimpResultDataset data, String name) {
        return createResidWaveCollection(xIndex, startInd, stopInd, data, name, 1.0);
    }
    
    public static XYSeriesCollection createResidWaveCollection(int xIndex, int startInd, int stopInd, TimpResultDataset data, String name, double scaleVal) {
        XYSeries series3 = new XYSeries(name);
        for (int j = startInd; j < stopInd; j++) {
            series3.add(data.getX2()[j], (data.getTraces().get(xIndex, j) - data.getFittedTraces().get(xIndex, j)) / scaleVal);
        }
        return new XYSeriesCollection(series3);
    }
    
    public static XYSeriesCollection createResidTraceCollection(int xIndex, int startInd, int stopInd, TimpResultDataset data) {
        return createResidTraceCollection(xIndex, startInd, stopInd, data, 0, "");
    }

    public static XYSeriesCollection createResidTraceCollection(int xIndex, int startInd, int stopInd, TimpResultDataset data, double t0, String name) {
        return createResidTraceCollection(xIndex, startInd, stopInd, data, t0, name, 1);
    }

    public static XYSeriesCollection createResidTraceCollection(int xIndex, int startInd, int stopInd, TimpResultDataset data, double t0, String name, double scaleVal) {
        XYSeries series3 = new XYSeries(name);
        for (int j = startInd; j < stopInd; j++) {
            series3.add(data.getX()[j] - t0, (data.getTraces().get(j, xIndex) - data.getFittedTraces().get(j, xIndex)) / scaleVal);
        }
        return new XYSeriesCollection(series3);
    }

    public static GraphPanel createGraphPanel(XYDataset traceCollection, String name, String axeName, boolean errorBars) {
        JFreeChart tracechart = ChartFactory.createXYLineChart(
                null,
                axeName,
                name,
                traceCollection,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);
        return new GraphPanel(tracechart, errorBars);
    }

    public static double[] calculateDispersionTrace(TimpResultDataset res) {
        double centrW = res.getLamdac();
        double[] param = res.getParmu() != null ? res.getParmu() : new double[]{0};
        double timeZero = res.getIrfpar() != null ? res.getIrfpar()[0] : 0;
        int order = param.length / 2;
        double[] t0Curve = new double[res.getX2().length];
        double point = 0;
        int k = 0;
        for (int i = 0; i < res.getX2().length; i++) {
            point = 0;
            for (int j = 0; j < order; j++) {
                point = point + param[j] * pow((res.getX2()[i] - centrW) / 100, j + 1);
            }
            k = 0;
            point = point + timeZero;
            t0Curve[i] = point;
        }
        return t0Curve;
    }

    public static int findIndexForWave(double wave, double thresh, TimpResultDataset res) {
        int index = 0;
        if (res.getX2()[0] < res.getX2()[1]) {
            //wavelengths
            if (wave < res.getX2()[0] - thresh) {
                return -1;
            } else {
                while (wave > res.getX2()[index] + thresh) {
                    index++;
                    if(index >= res.getX2().length) {
                        return -1;
                    }
                }
                return index;
            }
        } else {
            //wavenambers
            if (wave > res.getX2()[0] + thresh) {
                return -1;
            } else {
                while (wave < res.getX2()[index] - thresh) {
                    index++;
                    if(index >= res.getX2().length) {
                        return -1;
                    }
                }
                return index;
            }
        }
    }
}
