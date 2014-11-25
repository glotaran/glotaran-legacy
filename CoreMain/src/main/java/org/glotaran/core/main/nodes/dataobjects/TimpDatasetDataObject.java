/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes.dataobjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.TimpDatasetNode;
import org.glotaran.core.messages.CoreInformationMessages;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.hdf5interface.Hdf5DatasetTimp;
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

@MIMEResolver.Registration(displayName="#Services/MIMEResolver/TimpDatasetResolver.xml",
            resource="../../TimpDatasetResolver.xml",
            position=1400 )
@NbBundle.Messages("Services/MIMEResolver/TimpDatasetResolver.xml=Timp Datasets")
public class TimpDatasetDataObject extends InstanceDataObject {

    public TimpDatasetDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        //cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
    }

    @Override
    protected Node createNodeDelegate() {
        return new TimpDatasetNode(this);
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    public DatasetTimp getDatasetTimp() {
        DatasetTimp dataset = null;
        File file = FileUtil.toFile(this.getPrimaryFile());
        try {
            dataset = Hdf5DatasetTimp.load(file);
            // for back compatibility, if fail to load the dataset from the HDF5,
            // try to load it from Java object serialisation binary file
            if (dataset == null) {
                try {
                    ObjectInputStream ois = null;
                    try {
                        ois = new ObjectInputStream(new FileInputStream(file));
                        dataset = (DatasetTimp) ois.readObject();
                    } catch (IOException ex) {
                        CoreErrorMessages.IOException(null);
                    } finally {
                        try {
                            if(ois != null)
                                ois.close();
                        } catch (IOException ex) {
                            CoreErrorMessages.IOException(null);
                        }
                    }

                } catch (ClassNotFoundException ex) {
                    CoreErrorMessages.oldClassException();
                }
            }
        } catch (FileNotFoundException ex) {
            CoreErrorMessages.fileNotFound();
        }
        return dataset;
    }
}
