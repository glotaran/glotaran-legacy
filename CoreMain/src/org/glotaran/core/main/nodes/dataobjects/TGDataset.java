package org.glotaran.core.main.nodes.dataobjects;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * @author  Sergey
 * @author  Joris
 */
public abstract class TGDataset {

    protected final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    protected final InstanceContent content = new InstanceContent();
    protected final Lookup lookup;

    protected TGDataset() {
        lookup = new AbstractLookup(content);
    }

    public Lookup getLookup() {
        return lookup;
    }

    public abstract Image getIcon(int type);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propName, listener);
    }
}
