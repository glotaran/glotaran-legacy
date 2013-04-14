/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.glotaran.core.messages.CoreErrorMessages;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.ui.ExtensionFileFilter;
import org.openide.windows.TopComponent;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author slapten
 */
public class HeightMapPanel extends ChartPanel {

    private final static long serialVersionUID = 1L;
    //private static final String SAVE_ASCII_COMMAND = "SAVE_ASCII";
    private static final String SAVE_SVG_COMMAND = "SAVE_SVG";
    private static final String SAVE_PNG_COMMAND = "SAVE_PNG";
    private static final String OPEN_IN_NEW_WINDOW_COMMAND = "OPEN_IN_NEW_WINDOW";
    private static final String TOGGLE_TICK_LABELS = "TOGGLE_TICK_LABELS";
    private static final String INVERT_X_AXIS = "INVERT_X_AXIS";

    public HeightMapPanel(JFreeChart chart) {
        this(chart,  true, //useBuffer
             true,true,true, true, true,false);
    }

    public HeightMapPanel(JFreeChart chart, boolean useBuffer) {
        this(chart, useBuffer,true,true,true, true, true,false);
    }
    
     public HeightMapPanel(JFreeChart chart, boolean useBuffer,
            boolean properties,
            boolean copy,
            boolean save,
            boolean print,
            boolean zoom,
            boolean tooltips) {
        this(chart, 10, 10, 10, 10, 2048, 2048, useBuffer, properties, copy, save, print, zoom, tooltips);
    }

    public HeightMapPanel(JFreeChart chart,
            int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight,
            boolean useBuffer,
            boolean properties,
            boolean copy,
            boolean save,
            boolean print,
            boolean zoom,
            boolean tooltips) {
        super(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, copy, save, print, zoom, tooltips);
        addCommandsToPopupMenu();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        String command = event.getActionCommand();
        if (command.equals(OPEN_IN_NEW_WINDOW_COMMAND)) {
            try {
                doOpenInSeparateWindow();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(HeightMapPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//        if (command.equals(SAVE_ASCII_COMMAND)) {
//            try {
//                doSaveTracesToAscii();
//            } catch (IOException ex) {
//                CoreErrorMessages.fileSaveError(null);
//            }
//        }

//        if (command.equals(SAVE_SVG_COMMAND)) {
//            try {
//                doSaveChartToSVG();
//            } catch (FileNotFoundException ex) {
//                CoreErrorMessages.fileNotFound();
//            } catch (IOException ex) {
//                CoreErrorMessages.fileSaveError(null);
//            }
//        }

        if (command.equals(SAVE_PNG_COMMAND)) {
            try {
                try {
                    doSaveChartToPNG();
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(HeightMapPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                CoreErrorMessages.fileSaveError(null);
            }
        }

        if (command.equals(TOGGLE_TICK_LABELS)) {
            doToggleTickLabels();
        }
        
        if (command.equals(INVERT_X_AXIS)) {
            doInvertXAxis();
        }

    }

    @Override
    public void doEditChartProperties() {
        super.doEditChartProperties();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isZoomable()){
            super.mouseMoved(e);
        } else {
            super.mouseDragged(e);
        }
    }
    
    
    public boolean isZoomable(){
        return isDomainZoomable() || isRangeZoomable();
    }
    
    private void addCommandsToPopupMenu() {
        JPopupMenu popmenu;
        popmenu = getPopupMenu();
//        JMenuItem saveToASCIIItem = new JMenuItem("Save data to ASCII");
//        saveToASCIIItem.setActionCommand(SAVE_ASCII_COMMAND);
//        saveToASCIIItem.addActionListener(this);
//        popmenu.insert(saveToASCIIItem, 4);

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
        
         JMenuItem toggleTickLabels = new JMenuItem("Toggle tick labels");
            toggleTickLabels.setActionCommand(TOGGLE_TICK_LABELS);
            toggleTickLabels.addActionListener(this);
            popmenu.insert(toggleTickLabels, 1);            
            
            JMenuItem jMInvertXAxis = new JMenuItem("Invert x-axis");
            jMInvertXAxis.setActionCommand(INVERT_X_AXIS);
            jMInvertXAxis.addActionListener(this);
            popmenu.insert(jMInvertXAxis, 2);
            popmenu.insert(new JPopupMenu.Separator(), 3);

     
           
        
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
            BufferedWriter output = new BufferedWriter(new FileWriter(new File(filename)));
            StringBuilder sb = new StringBuilder();

            if (this.getChart().getPlot().getClass().equals(XYPlot.class)) {
                xyList = new ArrayList<XYPlot>();
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
            output.append(sb);
            output.close();
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
        win.setName(((this.getChart().getTitle() != null) ? this.getChart().getTitle().getText() : "Heightmap"));

        JFreeChart chart = (JFreeChart) this.getChart().clone();
        if (this.getChart().getXYPlot() != null) {
            chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
            chart.getXYPlot().getDomainAxis().setInverted(false);
        }
        HeightMapPanel newPanel = new HeightMapPanel(chart, true);

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
            DOMImplementation domImpl =
                    GenericDOMImplementation.getDOMImplementation();
            Document document = domImpl.createDocument(null, "svg", null);

            // Create an instance of the SVG Generator
            SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

            svgGenerator.getGeneratorContext().setPrecision(6);

            // draw the chart in the SVG generator
            getChart().draw(svgGenerator, new Rectangle2D.Double(0, 0, 400, 300), null);

            // Write svg file
            Writer out = new OutputStreamWriter(
                    new FileOutputStream(new File(filename)), "UTF-8");
            svgGenerator.stream(out, true);
        }
    }

    private void setPannable() {
        if (this.getChart().getXYPlot() != null) {
            this.getChart().getXYPlot().setDomainPannable(true);
            this.getChart().getXYPlot().setRangePannable(true);
        }
    }

    private void doToggleTickLabels() {
       this.getChart().getXYPlot().getRangeAxis().setTickLabelsVisible(
               !this.getChart().getXYPlot().getRangeAxis().isTickLabelsVisible());
       this.getChart().getXYPlot().getDomainAxis().setTickLabelsVisible(
               !this.getChart().getXYPlot().getDomainAxis().isTickLabelsVisible());
    }

    private StringBuilder exportXYPlot(StringBuilder output, List<XYPlot> plots) throws IOException {
        ArrayList<StringBuilder> tempStrings = new ArrayList<StringBuilder>();
        for (int index = 0; index < plots.size(); index++) {
            XYPlot plot = plots.get(index);
            AbstractXYDataset data = (AbstractXYDataset) plot.getDataset();
            int tracenum = data.getSeriesCount();            
            if (index == 0) {                
                tempStrings.add(0, new StringBuilder());
                tempStrings.get(0).append("X");
            }
            for (int i = 0; i < tracenum; i++) {
                tempStrings.get(0).append("\t");
                tempStrings.get(0).append(data.getSeriesKey(i).toString());
            }

            for (int j = 0; j < data.getItemCount(0); j++) {
                int line = j+1;
                if (index == 0) {
                tempStrings.add(line, new StringBuilder());
                }
                if (index == 0) {
                    tempStrings.get(line).append(new Formatter().format("%g", data.getXValue(0, j)).toString());
                }
                for (int i = 0; i < tracenum; i++) {
                    tempStrings.get(line).append("\t");
                    tempStrings.get(line).append(new Formatter().format("%g", data.getYValue(i, j)).toString());                 
                }                
            }
        }
        for (int i = 0; i < tempStrings.size(); i++) {
            output.append(tempStrings.get(i)).append("\n");
        }
        return output;
    }

    private void doInvertXAxis() {
       this.getChart().getXYPlot().getDomainAxis().setInverted(!this.getChart().getXYPlot().getDomainAxis().isInverted());
    }
}
