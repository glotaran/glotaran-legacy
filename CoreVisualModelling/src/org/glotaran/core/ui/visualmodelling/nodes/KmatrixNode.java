/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import org.glotaran.core.ui.visualmodelling.common.KmatrixPropertyEditor;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author slapten
 */
public class KmatrixNode extends PropertiesAbstractNode {

    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/Kmatr_16.png", true);
    private TgmDataObject model;
    Boolean positiveKinpar = false;
    Boolean nnls = false;

    public KmatrixNode(PropertyChangeListener listn) {
        super("Kmatrix", Children.LEAF);
        this.addPropertyChangeListener(listn);
    }

    public KmatrixNode(TgmDataObject tgmDO, PropertyChangeListener listn) {
        super("Kmatrix", Children.LEAF, Lookups.singleton(tgmDO));
        this.model = tgmDO;
        positiveKinpar = model.getTgm().getDat().getKinparPanel().isPositivepar();
        nnls = (model.getTgm().getDat().getKinparPanel().isNnls() == null) ? false : model.getTgm().getDat().getKinparPanel().isNnls();
        this.addPropertyChangeListener(listn);
    }

    @Override
    public Image getIcon(int type) {
        return ICON;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        PropertySupport.Reflection kMatrix = null;
        Property posKin = null;
        Property nnlsProp = null;
        try {
            kMatrix = new PropertySupport.Reflection(this, TgmDataObject.class, "getKmatrix", null);
            kMatrix.setPropertyEditorClass(KmatrixPropertyEditor.class);
            posKin = new PropertySupport.Reflection(this, Boolean.class, "positiveKinpar");
            nnlsProp = new PropertySupport.Reflection(this, Boolean.class, "nnlsProp");
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        kMatrix.setName("Kmatrix");
        posKin.setName("Positive rates");
        nnlsProp.setName("Non-negative Least Squares");
        set.put(kMatrix);
        set.put(posKin);
        sheet.put(set);
        set.put(nnlsProp);
        return sheet;
    }

    public TgmDataObject getKmatrix() {
        return getLookup().lookup(TgmDataObject.class);
    }

    public Boolean getPositiveKinpar() {
        return positiveKinpar;
    }

    public void setPositiveKinpar(Boolean positiveKinpar) {
        this.positiveKinpar = positiveKinpar;
        firePropertyChange("Positive rates", null, positiveKinpar);
    }

    public Boolean getNnlsProp() {
        return nnls;
    }

    public void setNnlsProp(Boolean nnls) {
        this.nnls = nnls;
        firePropertyChange("NNLS", null, nnls);
    }
}
