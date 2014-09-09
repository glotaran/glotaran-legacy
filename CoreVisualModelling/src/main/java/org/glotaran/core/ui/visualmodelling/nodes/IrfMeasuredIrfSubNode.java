/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.glotaran.core.ui.visualmodelling.common.EnumPropertyEditor;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes;
import org.glotaran.core.ui.visualmodelling.common.MeasuredIRFPropertyEditor;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.MeasuredIrfDO;
import org.glotaran.core.ui.visualmodelling.nodes.dataobjects.NonLinearParameter;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andras
 */
public class IrfMeasuredIrfSubNode extends PropertiesAbstractNode implements PropertyChangeListener {
    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/Subnode2_16.png", true);
    
    public IrfMeasuredIrfSubNode(MeasuredIrfDO data) {
        super("Measured IRF", Children.LEAF, Lookups.singleton(data));
        data.addPropertyChangeListener(WeakListeners.propertyChange(this, data));
    }

    @Override
    public Image getIcon(int type) {
        return ICON;
    }
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        MeasuredIrfDO obj = getLookup().lookup(MeasuredIrfDO.class);        
        PropertySupport.Reflection<EnumTypes.ConvolutionTypes> convolvType = null;
        Property<String> name = null;
        PropertySupport.Reflection measuredIRF = null;

        
        try {
            name = new PropertySupport.Reflection<String>(this, String.class, "getDisplayName", null);
            
            convolvType = new PropertySupport.Reflection<EnumTypes.ConvolutionTypes>(obj, EnumTypes.ConvolutionTypes.class, "getConvolutionType", "setConvolutionType");
            convolvType.setPropertyEditorClass(EnumPropertyEditor.class);
            
            measuredIRF = new PropertySupport.Reflection(obj, String.class, "getFilepath", null);
            measuredIRF.setPropertyEditorClass(MeasuredIRFPropertyEditor.class);
            
            name.setName("Name");
            measuredIRF.setName("Measured IRF");
            convolvType.setName("Convolution type");
            
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        
        set.put(name);
        set.put(measuredIRF);
        set.put(convolvType);
        sheet.put(set);
        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase("convType")){
            if (((EnumTypes.ConvolutionTypes)evt.getNewValue()).equals(EnumTypes.ConvolutionTypes.REFERENCECONVOLUTION)){
                updateRefLifeTimeProp(true);
            } 
            else {
                updateRefLifeTimeProp(false);
            }   
        }
//        super.propertyChange(evt);
    }
    
    private void updateRefLifeTimeProp(boolean addProp){
        if (addProp){
        MeasuredIrfDO obj = getLookup().lookup(MeasuredIrfDO.class);
        Property<Double> refLifetimeValue;
        try {
            refLifetimeValue = new PropertySupport.Reflection<Double>(obj, Double.class, "refLifetime");
            refLifetimeValue.setName("Reference lifetime");
            getSheet().get(Sheet.PROPERTIES).put(refLifetimeValue);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        }
        else {
            getSheet().get(Sheet.PROPERTIES).remove("Reference lifetime"); 
        }
        
        
    }
    
}
