/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author lsp
 */
public abstract class ImageUtilities {

    /**
     * Returns a dataset containing one series that holds a copy of the (x, z)
     * data from one row (y-index) of the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param row  the row (y) index.
     * @param seriesName  the series name/key (<code>null</code> not permitted).
     *
     * @return The dataset.
     */
    public static XYDataset extractRowFromImageDataset(ColorCodedImageDataset dataset,
            int row, Comparable seriesName) {
        XYSeries series = new XYSeries(seriesName);
        int cols = dataset.GetImageWidth();
        for (int c = 0; c < cols; c++) {
            series.add(dataset.GetWaveValue(c), dataset.getZValue(1, row*cols + c));
        }
        XYSeriesCollection result = new XYSeriesCollection(series);
        return result;
    }

    /**
     * Returns a dataset containing one series that holds a copy of the (y, z)
     * data from one column (x-index) of the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param column  the column (x) index.
     * @param seriesName  the series name (<code>null</code> not permitted).
     *
     * @return The dataset.
     */
    public static XYDataset extractColumnFromImageDataset(
            ColorCodedImageDataset dataset, int column, Comparable seriesName) {
        XYSeries series = new XYSeries(seriesName);
        int rows = dataset.GetImageHeigth();
        int cols = dataset.GetImageWidth();
        for (int r = 0; r < rows; r++) {
            series.add(dataset.GetTimeValue(r), dataset.getZValue(1, column + r * cols));
        }
        XYSeriesCollection result = new XYSeriesCollection(series);
        return result;
    }

    /**
     * Creates an image that displays the values from the specified dataset.
     * 
     * @param dataset the dataset (<code>null</code> not permitted).
     * @param paintScale the paint scale for the z-values (<code>null</code> not permitted).
     * @param invertX invert X dimension
     * @param invertY invert Y dimension
     * @return  A buffered image.
     */
    
    public static BufferedImage createColorCodedImage(ColorCodedImageDataset dataset, 
            PaintScale paintScale, boolean invertX, boolean invertY) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (paintScale == null) {
            throw new IllegalArgumentException("Null 'paintScale' argument.");
        }
        int xCount = dataset.GetImageWidth();
        int yCount = dataset.GetImageHeigth();
        BufferedImage image = new BufferedImage(xCount, yCount,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        int yIndOr;
        int xIndOr; 
        for (int yIndex = 0; yIndex < yCount; yIndex++) {
            for (int xIndex = 0; xIndex < xCount; xIndex++) {
                if (invertY){
                    yIndOr=yCount-yIndex-1;
                } else {
                    yIndOr=yIndex;
                }
                if (invertX){
                    xIndOr=xCount-xIndex-1;
                } else {
                    xIndOr=xIndex;
                }
                
                double z = dataset.getZValue(0, yIndOr * xCount + xIndOr);
                Paint p = paintScale.getPaint(z);
                g2.setPaint(p);
                g2.fillRect(xIndex, yIndex, 1, 1);
            }
        }
        return image;
        
    }
    
    /**
     * Creates an image that displays the values from the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param paintScale  the paint scale for the z-values (<code>null</code>
     *         not permitted).
     *
     * @return A buffered image.
     */
    public static BufferedImage createColorCodedImage(ColorCodedImageDataset dataset,
            PaintScale paintScale) {
        return createColorCodedImage(dataset, paintScale, false, false);
    }

    public static BufferedImage createColorCodedImage(IntensImageDataset dataset,
            PaintScale paintScale) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (paintScale == null) {
            throw new IllegalArgumentException("Null 'paintScale' argument.");
        }
        int xCount = dataset.GetImageWidth();
        int yCount = dataset.GetImageHeigth();
        BufferedImage image = new BufferedImage(xCount, yCount,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        for (int yIndex = 0; yIndex < yCount; yIndex++) {
            for (int xIndex = 0; xIndex < xCount; xIndex++) {
                double z = dataset.getZValue(0, yIndex * xCount + xIndex);
                Paint p = paintScale.getPaint(z);
                g2.setPaint(p);
                g2.fillRect(xIndex, yIndex, 1, 1);
            }
        }
        return image;
    }
}






