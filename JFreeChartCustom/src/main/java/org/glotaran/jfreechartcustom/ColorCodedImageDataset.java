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
public class ColorCodedImageDataset implements XYZDataset {

    private int imageWidth;
    private int imageHeight;
    private double[] colCodedImage;
    private double[] timesteps;
    private double[] wavelengths;
    private transient EventListenerList listenerList;
    private boolean active;

    public ColorCodedImageDataset() {
        this.imageWidth = 1;
        this.imageHeight = 1;
        this.colCodedImage = new double[1];
        this.timesteps = new double[1];
        this.wavelengths = new double[1];
        this.listenerList = new EventListenerList();
        this.active = true;
    }

    public ColorCodedImageDataset(int width, int height, double[] image, double[] x, double[] x2, boolean timpDataset) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.listenerList = new EventListenerList();
        this.active = true;
        this.colCodedImage = new double[width * height];
        this.timesteps = new double[height];
        this.wavelengths = new double[width];
        this.timesteps = x2;
        this.wavelengths = x;
        if (timpDataset) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    this.colCodedImage[i * width + j] = image[j * height + i];
                }
            }
        } else {
            this.colCodedImage = image;
        }
    }

    public double GetWaveValue(int index) {
        return this.wavelengths[index];
    }

    public double GetTimeValue(int index) {
        return this.timesteps[index];
    }

    public int GetImageWidth() {
        return this.imageWidth;
    }

    public int GetImageHeigth() {
        return this.imageHeight;
    }

    public double[] GetImage() {
        return this.colCodedImage;
    }

    protected void SetImage(double[] image) {
        this.colCodedImage = image;
        if (this.active) {
            fireDatasetChanged();
        }
    }

    public void SetValue(int x, int y, double value) {
        this.colCodedImage[y * this.imageWidth + x] = value;
        if (this.active) {
            fireDatasetChanged();
        }
    }

    public void SetValue(int item, double value) {
        this.colCodedImage[item] = value;
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

    public Number getZ(int series, int item) {
        return new Double(getZValue(series, item));
    }

    public double getZValue(int series, int item) {
        return this.colCodedImage[item];
    }

    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    public int getItemCount(int series) {
        return this.imageHeight * this.imageWidth;
    }

    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    public double getXValue(int series, int item) {
        return item - (floor(item / this.imageWidth) * imageWidth);
    }

    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    public double getYValue(int series, int item) {
        return floor(item / this.imageWidth);
    }

    public int getSeriesCount() {
        return 1;
    }

    public Comparable getSeriesKey(int series) {
        return (Comparable) "ColorCodedImage";
    }

    public int indexOf(Comparable seriesKey) {
        return 0;
    }

    public void addChangeListener(DatasetChangeListener listener) {
        this.listenerList.add(DatasetChangeListener.class, listener);
    }

    public void removeChangeListener(DatasetChangeListener listener) {
        this.listenerList.remove(DatasetChangeListener.class, listener);
    }

    public DatasetGroup getGroup() {
        return null;
    }

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
