/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.tgmfilesupport;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.glotaran.core.models.tgm.Tgm;
import org.openide.ErrorManager;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

@MIMEResolver.Registration(displayName="org/glotaran/tgmfilesupporrt#Services/MIMEResolver/TgmResolver.xml",
            resource="TgmResolver.xml",
            position=1700 )

public class TgmDataObject extends MultiDataObject implements SaveCookie {
    
    private static final long serialVersionUID = 1L;    
    final InstanceContent ic;
    private final AbstractLookup lookup;
    Tgm tgm;

    public TgmDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        ic = new InstanceContent();
        lookup = new AbstractLookup(ic);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) this);
//        InputSource is = DataObjectAdapters.inputSource(this);
//        Source source = DataObjectAdapters.source(this);
//        ic.add(new CheckXMLSupport(is));
//        ic.add(new ValidateXMLSupport(is));
//        ic.add(new TransformableSupport(source));        
        if (!(FileUtil.toFile(this.getPrimaryFile()) == null)) {
            tgm = getTgm();
            if (tgm!=null){
            ic.add(tgm);
            }
        }
    }

    @Override
    protected Node createNodeDelegate() {
        return new TgmDataNode(this,getLookup()); // removed: getLookup()
    }

       @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

        public void save() throws IOException {
        if (tgm == null) {
            return;
        }
        Writer out = new StringWriter();
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(tgm.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(tgm, FileUtil.toFile(this.getPrimaryFile()));
            out.close();
            setModified(false);
        } catch (javax.xml.bind.JAXBException ex) {
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }        
    }

    public Tgm getTgm() { //perhaps IOExeption?
        // If the Object "tgm" doesn't exist yet, read in from file
        if (tgm == null) {
            tgm = new Tgm();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(tgm.getClass().getPackage().getName());
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                tgm = (Tgm) unmarshaller.unmarshal(FileUtil.toFile(this.getPrimaryFile())); //NOI18N //replaced: new java.io.File("File path") //Fix this: java.lang.IllegalArgumentException: file parameter must not be null
            } catch (javax.xml.bind.JAXBException ex) {
                // XXXTODO Handle exception
                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            }
        }
        // Else simply return the object
        return tgm;
    }   
}
