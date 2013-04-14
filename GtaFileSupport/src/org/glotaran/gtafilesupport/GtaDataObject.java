/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.gtafilesupport;

import java.awt.Image;
import java.io.IOException;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.gta.GtaProjectScheme;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

public class GtaDataObject extends MultiDataObject implements SaveCookie {

    private GtaProjectScheme gtaScheme;
    private static final long serialVersionUID = 1;
    private final Image ICON = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/schema-icon.png", true);

    public GtaDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        //cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        cookies.add((Node.Cookie) new GtaOpenSupport(getPrimaryEntry()));
        cookies.add((Node.Cookie) this);
        //cookies.add((Node.Cookie) new GtaAnalysisSupport(getPrimaryEntry()));       
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup()) {

            @Override
            public Image getIcon(int type) {
                return ICON;
            }
        };
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    @Override
    public void save() throws IOException {
        if (gtaScheme == null) {
            return;
        }
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(gtaScheme.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(gtaScheme, FileUtil.toFile(getPrimaryFile()));
            setModified(false);
        } catch (javax.xml.bind.JAXBException ex) {
            CoreErrorMessages.jaxbException();
            // XXXTODO Handle exception
//            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
    }

    public GtaProjectScheme getProgectScheme() {
        if (gtaScheme == null) {
            gtaScheme = new GtaProjectScheme();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(gtaScheme.getClass().getPackage().getName());
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                gtaScheme = (GtaProjectScheme) unmarshaller.unmarshal(FileUtil.toFile(this.getPrimaryFile())); //NOI18N //replaced: new java.io.File("File path") //Fix this: java.lang.IllegalArgumentException: file parameter must not be null
            } catch (javax.xml.bind.JAXBException ex) {
                // XXXTODO Handle exception
                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            }
        }
        // Else simply return the object
        return gtaScheme;
    }
}
