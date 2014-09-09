package org.glotaran.core.main.nodes.dataobjects;

import org.glotaran.core.models.tgd.Tgd;
import java.io.IOException;
import javax.xml.bind.Unmarshaller;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.TgdDataNode;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.InstanceDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@MIMEResolver.Registration(displayName="#Services/MIMEResolver/TgdResolver.xml",
            resource="../../TgdResolver.xml",
            position=1300 )
@NbBundle.Messages("Services/MIMEResolver/TgdResolver.xml=Tgd Files")
public class TgdDataObject extends InstanceDataObject implements SaveCookie {

    private Tgd tgd;
    private final static long serialVersionUID = 1L;

    public TgdDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        if (!(FileUtil.toFile(this.getPrimaryFile()) == null)) {
            tgd = getTgd();
        }
    }

    @Override
    protected Node createNodeDelegate() {
        return new TgdDataNode(this, getLookup()); // removed: getLookup()
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    public Tgd getTgd() {
        if (tgd == null) {
            tgd = new Tgd();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(tgd.getClass().getPackage().getName());
                Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                tgd = (Tgd) unmarshaller.unmarshal(FileUtil.toFile(this.getPrimaryFile()));
            } catch (javax.xml.bind.JAXBException ex) {
                CoreErrorMessages.jaxbException();
            }
        }
        return tgd;
    }

    @Override
    public void save() throws IOException {
        if (tgd == null) {
            return;
        }
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(tgd.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(tgd, FileUtil.toFile(this.getPrimaryFile()));
        } catch (javax.xml.bind.JAXBException ex) {
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
    }
}
