/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.analysisoverviewfilesupport;

import java.awt.Image;
import java.io.IOException;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.results.GtaResult;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.InstanceContent;

public class AnalysisResultDataObject extends MultiDataObject {

    private GtaResult gtaResult;
    private static final long serialVersionUID = 1L;

    public AnalysisResultDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
//        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        if (!(FileUtil.toFile(this.getPrimaryFile()) == null)) {
            getAnalysisResult();
        }
        //cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        cookies.add((Node.Cookie) new AnalysisResultFileOpenSupport(getPrimaryEntry()));
        cookies.add((Node.Cookie) this);
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup()) {

            private final Image ICON = ImageUtilities.loadImage("org/glotaran/analysisoverviewfilesupport/AnalysisResultsObject16.png", true);

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

    public GtaResult getAnalysisResult() {
        if (gtaResult == null) {
            gtaResult = new GtaResult();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(gtaResult.getClass().getPackage().getName());
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                gtaResult = (GtaResult) unmarshaller.unmarshal(FileUtil.toFile(this.getPrimaryFile())); //NOI18N //replaced: new java.io.File("File path") //Fix this: java.lang.IllegalArgumentException: file parameter must not be null
            } catch (javax.xml.bind.JAXBException ex) {
                // XXXTODO Handle exception
                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
                CoreErrorMessages.jaxbException();
            }
        }
        // Else simply return the object
        return gtaResult;
    }
}
