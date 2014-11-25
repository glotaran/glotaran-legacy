/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes.dataobjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.TimpResultsNode;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.hdf5interface.Hdf5TimpResultDataset;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.InstanceDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@MIMEResolver.Registration(displayName="#Services/MIMEResolver/TimpResultResolver.xml",
            resource="../../TimpResultResolver.xml",
            position=1100 )
@NbBundle.Messages("Services/MIMEResolver/TimpResultResolver.xml=Timp Results")
public class TimpResultDataObject extends InstanceDataObject {

    private TimpResultDataset obj;

    public TimpResultDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
//        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
    }

    @Override
    protected Node createNodeDelegate() {
        return new TimpResultsNode(this);
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    public TimpResultDataset getTimpResultDataset() {
        TimpResultDataset dataset = null;
        ObjectInputStream ois = null;
        try {
            File file = FileUtil.toFile(this.getPrimaryFile());
            ois = new ObjectInputStream(new FileInputStream(file));
            try {
                dataset = (TimpResultDataset) ois.readObject();
                TimpResultDataset tmpDs = Hdf5TimpResultDataset.load(file);
            } catch (ClassNotFoundException ex) {
                CoreErrorMessages.oldClassException();
            }
        } catch (IOException ex) {
            CoreErrorMessages.IOException(null);
        } finally {
            try {
                ois.close();
            } catch (IOException ex) {
                CoreErrorMessages.IOException(null);
            }
        }
        return dataset;
    }
}
