/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import javax.swing.event.EventListenerList;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYZDataset;
import static java.lang.Math.floor;

/**
 *
 * @author Sergey
 */
public class IntensImageDataset implements XYZDataset {

    private int imageWidth;
    private int imageHeight;
    private double[] intenceImage;
    private transient EventListenerList listenerList;
    private boolean active;

    public IntensImageDataset() {
        this.imageWidth = 1;
        this.imageHeight = 1;
        this.intenceImage = new double[1];
        this.listenerList = new EventListenerList();
        this.active = true;
    }

    public IntensImageDataset(int width, int height, int[] image) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.intenceImage = new double[width * height];
        for (int i = 0; i < width * height; i++) {
            this.intenceImage[i] = image[i];
        }
        this.listenerList = new EventListenerList();
        this.active = true;
    }

    public IntensImageDataset(int width, int height, double[] image) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.intenceImage = image;
        this.listenerList = new EventListenerList();
        this.active = true;
    }

    public int GetImageWidth() {
        return this.imageHeight;
    }

    public int GetImageHeigth() {
        return this.imageWidth;
    }

    public double[] SetIntenceImage() {
        return this.intenceImage;
    }

    public void SetIntenceImage(int[] image) {

        for (int i = 0; i < imageWidth * imageHeight; i++) {
            this.intenceImage[i] = image[i];
        }
        if (this.active) {
            fireDatasetChanged();
        }
    }

    public void SetValue(int x, int y, double value) {
        this.intenceImage[y * this.imageWidth + x] = value;
        if (this.active) {
            fireDatasetChanged();
        }
    }

    public void SetValue(int item, double value) {
        this.intenceImage[item] = value;
        if (this.active) {
            fireDatasetChanged();
        }
    }

    public void SetActive(boolean status) {
        this.active = status;
    }

    public boolean IsActive() {
        return this.active;
    }

    public void Update() {
        this.fireDatasetChanged();
    }

    @Override
    public Number getZ(int series, int item) {
        return new Double(getZValue(series, item));
    }

    @Override
    public double getZValue(int series, int item) {
        return this.intenceImage[item];
    }

    @Override
    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    @Override
    public int getItemCount(int series) {
        return this.imageHeight * this.imageWidth;
    }

    @Override
    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    @Override
    public double getXValue(int series, int item) {
        return item - (floor(item / this.imageWidth) * imageWidth);
    }

    @Override
    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    @Override
    public double getYValue(int series, int item) {
        return floor(item / this.imageWidth);
    }

    @Override
    public int getSeriesCount() {
        return 1;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return (Comparable) "ColorCodedImage";
    }

    @Override
    public int indexOf(Comparable seriesKey) {
        return 0;
    }

    @Override
    public void addChangeListener(DatasetChangeListener listener) {
        this.listenerList.add(DatasetChangeListener.class, listener);
    }

    @Override
    public void removeChangeListener(DatasetChangeListener listener) {
        this.listenerList.remove(DatasetChangeListener.class, listener);
    }

    @Override
    public DatasetGroup getGroup() {
        return null;
    }

    @Override
    public void setGroup(DatasetGroup group) {
        // ignore
    }

    protected void fireDatasetChanged() {
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    protected void notifyListeners(DatasetChangeEvent event) {

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DatasetChangeListener.class) {
                ((DatasetChangeListener) listeners[i + 1]).datasetChanged(
                        event);
            }
        }
    }
}
