/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes;

import java.awt.Image;
import java.io.IOException;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.project.TGProject;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.loaders.DataNode;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

public class TgdDataNode extends DataNode {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/main/resources/Tgd-dataset-icon-16.png", true);
    //private static Image BADGE_BAD = ImageUtilities.loadImage("org/glotaran/core/main/resources/error_badge.png");
    private TgdDataObject obj;

    public TgdDataNode(TgdDataObject obj) {
        super(obj, new TgdDataChildren(obj));
        this.obj = obj;

    }

    public TgdDataNode(TgdDataObject obj, Lookup lookup) {
        super(obj, new TgdDataChildren(obj), lookup);
        this.obj = obj;
    }

    @Override
    public Image getIcon(int type) {
        //DataFolder root = DataFolder.findFolder(Repository.getDefault().getDefaultFileSystem().getRoot());
        //Image ICON = root.getNodeDelegate().getIcon(type);
        //return ImageUtilities.mergeImages(ICON, BADGE_BAD, 7, 7);
        return ICON;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public void destroy() throws IOException {
        TGProject project = findProject();
        if (this.getChildren().getNodesCount() > 0) {
            for (int i = 0; i < this.getChildren().getNodesCount(); i++) {
                this.getChildren().getNodes()[i].destroy();
            }
        }
        project.getCacheFolder(false).getFileObject(obj.getTgd().getCacheFolderName()).delete();
        super.destroy();
    }

    public TGProject findProject() {
        return (TGProject) FileOwnerQuery.getOwner(obj.getPrimaryFile());
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        Property<String> propertyDisplayName = null;
        Property<Double> timeWindow = null;
        Property<Integer> numberOfTimeSteps = null;
        Property<Double> timeStep = null;
        Property<Double> waveWindow = null;
        Property<Integer> numberOfWaveSteps = null;
        Property<Double> waveStep = null;

        try {
            //Properties for first dimension (time, ...)
            timeWindow = new PropertySupport.Reflection<Double>(this, Double.class, "getTimeWindow", null);
            numberOfTimeSteps = new PropertySupport.Reflection<Integer>(this, Integer.class, "getNumberOfTimeSteps", null);
            timeStep= new PropertySupport.Reflection<Double>(this, Double.class, "getTimeStep", null);

            //Properties for second dimension (wavelength, ...)
            waveWindow = new PropertySupport.Reflection<Double>(this, Double.class, "getWaveWindow", null);
            numberOfWaveSteps = new PropertySupport.Reflection<Integer>(this, Integer.class, "getNumberOfWaveSteps", null);
            waveStep = new PropertySupport.Reflection<Double>(this, Double.class, "getWaveStep", null);

            propertyDisplayName = new PropertySupport.Reflection<String>(this, String.class, "getPropertyDisplayName", null);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        propertyDisplayName.setName("Name");
        timeWindow.setName("Time window");
        numberOfTimeSteps.setName("Time steps");
        timeStep.setName("Time step size");
        waveWindow.setName("Wavelengths window");
        numberOfWaveSteps.setName("Wavelength steps");
        waveStep.setName("Wavelength step size");

        set.put(propertyDisplayName);
        set.put(timeWindow);
        set.put(numberOfTimeSteps);
        set.put(timeStep);
        set.put(waveWindow);
        set.put(numberOfWaveSteps);
        set.put(waveStep);
        sheet.put(set);
        return sheet;
    }

    public Integer getNumberOfTimeSteps() {
        return 1;
    }

    public Integer getNumberOfWaveSteps() {
        return 2;
    }

    public Double getTimeStep() {
        return 3.0;
    }

    public Double getTimeWindow() {
        return 4.0;
    }

    public Double getWaveStep() {
        return 5.0;
    }

    public Double getWaveWindow() {
        return 6.0;
    }

    public String getPropertyDisplayName() {
        return "Name";
    }


}
