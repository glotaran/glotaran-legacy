/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.simfilesupport.spec;

import java.io.IOException;
import javax.xml.bind.Unmarshaller;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.sim.SpectralModelSpecification;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.text.DataEditorSupport;
import org.openide.util.Lookup;

/**
 *
 * @author jsg210
 */
@MIMEResolver.Registration(displayName="org/glotaran/simfilesupporrt#Services/MIMEResolver/SpectralModelResolver.xml",
            resource="../SpectralModelResolver.xml",
            position=1600 )

 public class SpectralModelDataObject extends MultiDataObject implements SaveCookie {

    SpectralModelSpecification sim;

    public SpectralModelDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        if (!(FileUtil.toFile(this.getPrimaryFile()) == null)) {
            sim = getSim();
        }
    }

    @Override
    protected Node createNodeDelegate() {
        return new SpectralModelDataNode(this, Children.LEAF, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    public SpectralModelSpecification getSim() {
        if (sim == null) {
            sim = new SpectralModelSpecification();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(sim.getClass().getPackage().getName());
                Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                sim = (SpectralModelSpecification) unmarshaller.unmarshal(FileUtil.toFile(this.getPrimaryFile()));
            } catch (javax.xml.bind.JAXBException ex) {
                CoreErrorMessages.jaxbException();
            }
        }
        return sim;
    }

    public void save() throws IOException {
        if (sim == null) {
            return;
        }
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(sim.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(sim, FileUtil.toFile(this.getPrimaryFile()));            
        } catch (javax.xml.bind.JAXBException ex) {
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
        setModified(false);
    }
}
