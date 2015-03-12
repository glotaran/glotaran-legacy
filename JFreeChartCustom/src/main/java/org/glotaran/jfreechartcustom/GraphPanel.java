/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.glotaran.core.messages.CoreErrorMessages;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ExtensionFileFilter;
import org.jfree.ui.RectangleInsets;
import org.openide.windows.TopComponent;

/**
 *
 * @author slapten
 */
public class GraphPanel extends ChartPanel {

    private final static long serialVersionUID = 1L;
    private static final String SAVE_ASCII_COMMAND = "SAVE_ASCII";
    private static final String SAVE_SVG_COMMAND = "SAVE_SVG";
    private static final String SAVE_PNG_COMMAND = "SAVE_PNG";
    private static final String OPEN_IN_NEW_WINDOW_COMMAND = "OPEN_IN_NEW_WINDOW";
    private static final String SHOW_ERRORBARS = "SHOW_ERRORBARS";
    private boolean errorBarsEnabled = false;
    private boolean errorBarsVisible = false;
    private Paint[] paintSequence = new Paint[]{Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.magenta, Color.CYAN, Color.YELLOW, Color.ORANGE, Color.PINK, Color.DARK_GRAY};

    public GraphPanel(JFreeChart chart) {
        this(chart, true, false, true, true, true, false);
    }

    public GraphPanel(JFreeChart chart, boolean errBars) {
        this(chart, true, false, true, true, true, errBars);

    }

    public GraphPanel(JFreeChart chart,
            boolean properties,
            boolean save,
            boolean print,
            boolean zoom,
            boolean tooltips,
            boolean errBars) {
        super(chart, properties, save, print, zoom, tooltips);
        errorBarsEnabled = errBars;
        addCommandsToPopupMenu();
        updateAppearance();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        String command = event.getActionCommand();
        if (command.equals(OPEN_IN_NEW_WINDOW_COMMAND)) {
            try {
                doOpenInSeparateWindow();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(GraphPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (command.equals(SAVE_ASCII_COMMAND)) {
            try {
                doSaveTracesToAscii();
            } catch (IOException ex) {
                CoreErrorMessages.fileSaveError(null);
            }
        }

        if (command.equals(SAVE_SVG_COMMAND)) {
            try {
                doSaveChartToSVG();
            } catch (FileNotFoundException ex) {
                CoreErrorMessages.fileNotFound();
            } catch (IOException ex) {
                CoreErrorMessages.fileSaveError(null);
            }
        }

        if (command.equals(SAVE_PNG_COMMAND)) {
            try {
                try {
                    doSaveChartToPNG();
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(GraphPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                CoreErrorMessages.fileSaveError(null);
            }
        }

        if (command.equals(SHOW_ERRORBARS)) {
            doShowErrorBars();
        }

    }

    @Override
    public void doEditChartProperties() {
        super.doEditChartProperties();
    }

    private void addCommandsToPopupMenu() {
        JPopupMenu popmenu;
        popmenu = getPopupMenu();
        JMenuItem saveToASCIIItem = new JMenuItem("Save data to ASCII");
        saveToASCIIItem.setActionCommand(SAVE_ASCII_COMMAND);
        saveToASCIIItem.addActionListener(this);
        popmenu.insert(saveToASCIIItem, 4);

        JMenuItem saveToPNGItem = new JMenuItem("Render chart to PNG");
        saveToPNGItem.setActionCommand(SAVE_PNG_COMMAND);
        saveToPNGItem.addActionListener(this);
        popmenu.insert(saveToPNGItem, 5);

        JMenuItem saveToSVGItem = new JMenuItem("Render chart to SVG");
        saveToSVGItem.setActionCommand(SAVE_SVG_COMMAND);
        saveToSVGItem.addActionListener(this);
        popmenu.insert(saveToSVGItem, 6);

        JMenuItem openInSepWindItem = new JMenuItem("Open in new window");
        openInSepWindItem.setActionCommand(OPEN_IN_NEW_WINDOW_COMMAND);
        openInSepWindItem.addActionListener(this);
        popmenu.insert(openInSepWindItem, 0);

        if (errorBarsEnabled == true) {
            JMenuItem showErrorBars = new JMenuItem("Show error bars");
            showErrorBars.setActionCommand(SHOW_ERRORBARS);
            showErrorBars.addActionListener(this);
            showErrorBars.setEnabled(errorBarsEnabled);
            popmenu.insert(showErrorBars, 1);
            popmenu.insert(new JPopupMenu.Separator(), 2);
        }
    }

    private void updateAppearance() {
        this.setFillZoomRectangle(true);
        this.setMouseWheelEnabled(true);
        this.setZoomFillPaint(new Color(68, 68, 78, 63));
        if (this.getChart().getLegend() != null) {
            this.getChart().getLegend().setVisible(false);
        }
        this.getChart().setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
        if (this.getChart().getXYPlot() != null) {
            XYPlot plot = this.getChart().getXYPlot();
            plot.setRangeZeroBaselineVisible(true);
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);
            plot.setAxisOffset(RectangleInsets.ZERO_INSETS);
            if (plot.getDomainAxis() != null) {
                plot.getDomainAxis().setLowerMargin(0.0);
                plot.getDomainAxis().setUpperMargin(0.0);
            }
            plot.setDrawingSupplier(new GlotaranDrawingSupplier());
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
            plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);
            errorBarsVisible = plot.getRenderer() instanceof XYErrorRenderer;
        }
        setPannable();
    }

    private void doSaveTracesToAscii() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(this.getDefaultDirectoryForSaveAs());
        ExtensionFileFilter filter = new ExtensionFileFilter("Tab Separated ASCII files", ".ascii");
        fileChooser.addChoosableFileFilter(filter);
        int option = fileChooser.showSaveDialog(this);
        ArrayList<XYPlot> xyList;
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (isEnforceFileExtensions()) {
                if (!filename.endsWith(".ascii")) {
                    filename = filename + ".ascii";
                }
            }
//write all tracess that presented in chart, in colums, using domain axe from first seria in colums
            
            
            StringBuilder sb = new StringBuilder();

            if (this.getChart().getPlot().getClass().equals(XYPlot.class)) {
                xyList = new ArrayList<>();
                XYPlot plot = (XYPlot) this.getChart().getPlot();
                if (plot!=null) {
                    xyList.add(plot);
                }
                sb = exportXYPlot(sb, xyList);
            } else if (this.getChart().getPlot().getClass().equals(CombinedDomainXYPlot.class)) {
                @SuppressWarnings("unchecked")
                List<XYPlot> plots = ((CombinedDomainXYPlot) this.getChart().getXYPlot()).getSubplots();
                sb = exportXYPlot(sb, plots);
            }
            try (BufferedWriter output = new BufferedWriter(new FileWriter(new File(filename)))) {
                output.append(sb);
            }
        }
    }

    private void doOpenInSeparateWindow() throws CloneNotSupportedException {
        TopComponent win = new TopComponent() {

            private final static long serialVersionUID = 1L;

            @Override
            public int getPersistenceType() {
                return TopComponent.PERSISTENCE_NEVER;

            }
        };
        win.setLayout(new BorderLayout());
        win.setName(((this.getChart().getTitle() != null) ? this.getChart().getTitle().getText() : "Single graph"));

        JFreeChart chart = (JFreeChart) this.getChart().clone();
        if (this.getChart().getXYPlot() != null) {
            chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
            chart.getXYPlot().getDomainAxis().setInverted(false);
        }
        GraphPanel newPanel = new GraphPanel(chart, errorBarsEnabled);

        win.add(newPanel);
        win.open();
        win.requestActive();
    }

    private void doSaveChartToPNG() throws CloneNotSupportedException, IOException {
        doSaveAs();
    }

    private void doSaveChartToSVG() throws FileNotFoundException, IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(this.getDefaultDirectoryForSaveAs());
        ExtensionFileFilter filter = new ExtensionFileFilter("SVG graphics files", ".svg");
        fileChooser.addChoosableFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (isEnforceFileExtensions()) {
                if (!filename.endsWith(".svg")) {
                    filename = filename + ".svg";
                }
            }
            // Create an instance of the SVG Generator
            SVGGraphics2D svgGenerator = new SVGGraphics2D(400,300);
            

            // draw the chart in the SVG generator
            getChart().draw(svgGenerator, new Rectangle2D.Double(0, 0, 400, 300), null);
            try (BufferedWriter output = new BufferedWriter(new FileWriter(new File(filename)))) {
                output.append(svgGenerator.getSVGDocument());
                output.close();
//            Files.write(Paths.get(filename), svgGenerator.getSVGDocument().getBytes(),StandardOpenOption.CREATE);
            }
            
        }
    }

    private void setPannable() {
        if (this.getChart().getXYPlot() != null) {
            this.getChart().getXYPlot().setDomainPannable(true);
            this.getChart().getXYPlot().setRangePannable(true);
        }
    }

    private void doShowErrorBars() {
        if (!errorBarsVisible) {
            XYErrorRenderer renderer = new XYErrorRenderer();
            renderer.setBaseLinesVisible(true);
            renderer.setBaseShapesVisible(false);

            this.getChart().setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
            XYPlot plot = new XYPlot(this.getChart().getXYPlot().getDataset(),
                    this.getChart().getXYPlot().getDomainAxis(),
                    this.getChart().getXYPlot().getRangeAxis(), renderer);
            plot.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
            JFreeChart chart = new JFreeChart(plot);
            this.setChart(chart);
        } else {
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setBaseLinesVisible(true);
            renderer.setBaseShapesVisible(false);
            XYPlot plot = new XYPlot(this.getChart().getXYPlot().getDataset(),
                    this.getChart().getXYPlot().getDomainAxis(),
                    this.getChart().getXYPlot().getRangeAxis(), renderer);
//            plot.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
            JFreeChart chart = new JFreeChart(plot);
            this.setChart(chart);
        }
        updateAppearance();
    }

    private StringBuilder exportXYPlot(StringBuilder output, List<XYPlot> plots) throws IOException {
        ArrayList<StringBuilder> tempStrings = new ArrayList<StringBuilder>();
        for (int index = 0; index < plots.size(); index++) {
            XYPlot plot = plots.get(index);
            AbstractXYDataset data = (AbstractXYDataset) plot.getDataset();
            int tracenum = data.getSeriesCount();            
            if (index == 0) {                
                tempStrings.add(0, new StringBuilder());
//                tempStrings.get(0).append("X").append(String.valueOf(index));
            }
            for (int i = 0; i < tracenum; i++) {
                if ((i!=0)||(index!=0)){
                    tempStrings.get(0).append("\t");
                }
                tempStrings.get(0).append("X").append(data.getSeriesKey(i).toString());
                tempStrings.get(0).append("\t");
                tempStrings.get(0).append(data.getSeriesKey(i).toString());
                if (errorBarsVisible) {
                    tempStrings.get(0).append("\t");
                    tempStrings.get(0).append(data.getSeriesKey(i).toString()).append("_Err-Hi");
                    tempStrings.get(0).append("\t");
                    tempStrings.get(0).append(data.getSeriesKey(i).toString()).append("_Err-Low");
                }
            }

            for (int j = 0; j < data.getItemCount(0); j++) {
                int line = j+1;
                if (index == 0) {
                tempStrings.add(line, new StringBuilder());
                }
//                if (index == 0) {
//                    tempStrings.get(line).append(new Formatter().format("%g", data.getXValue(0, j)).toString());
//                }
                for (int i = 0; i < tracenum; i++) {
                    if ((i!=0)||(index!=0)){
                        tempStrings.get(line).append("\t");
                    }
                    tempStrings.get(line).append(new Formatter().format("%g", data.getXValue(i, j)).toString());
                    tempStrings.get(line).append("\t");
                    tempStrings.get(line).append(new Formatter().format("%g", data.getYValue(i, j)).toString());
                    if (errorBarsVisible) {
                        tempStrings.get(line).append("\t");
                        tempStrings.get(line).append(new Formatter().format("%g", ((YIntervalSeriesCollection) data).getStartYValue(i, j)).toString());
                        tempStrings.get(line).append("\t");
                        tempStrings.get(line).append(new Formatter().format("%g", ((YIntervalSeriesCollection) data).getEndYValue(i, j)).toString());
                    }
                }                
            }
        }
        for (int i = 0; i < tempStrings.size(); i++) {
            output.append(tempStrings.get(i)).append("\n");
        }
        return output;
    }
}
