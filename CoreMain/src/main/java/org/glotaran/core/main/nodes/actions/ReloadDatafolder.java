package org.glotaran.core.main.nodes.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.models.tgd.Tgd;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ReloadDatafolder extends CookieAction {

    //static OpenDataset instance;
    private static final long serialVersionUID = 1;
    private Collection<? extends TGDatasetInterface> services;
    private TGProject project;
    private DataObject dataObject;

    public ReloadDatafolder() {
        super();
    }

//    public OpenDataset getInstance() {
//        if (instance == null) {
//            instance = new OpenDataset();
//        }
//        return instance;
//    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        services = Lookup.getDefault().lookupAll(TGDatasetInterface.class);
        dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        // get Tgd file stat hold location to yaml file 
        
        final ActionListener lst;
        lst = new ActionListener() {
            String test;

            @Override
            public void actionPerformed(ActionEvent e) {
                File[] files;
                // check if Tgd file for subfolder runXXXXX already exists, if not create it.

            }

        };        
    }

    @Override
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    @Override
    public String getName() {
        return NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("openDatasetFile");
    }

    @Override
    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
  

    private void openDatasetFile(TGDatasetInterface service, File f) {
        FileObject projectCacheFolder = null;
        FileObject cacheSubFolder;
        FileObject newFO = null;
        
        FileObject originalFO = FileUtil.toFileObject(f);
        
        String cacheFolderName = originalFO.getName() + "_" + String.valueOf(System.currentTimeMillis());
        if (service.getExtention().equalsIgnoreCase("yaml")) {
            cacheFolderName = f.getParentFile().getName() + "_" + String.valueOf(System.currentTimeMillis()); 
        }
        
        
        

        project = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
        if (project != null) {
            projectCacheFolder = project.getCacheFolder(true);
        } else {
            //TODO: allow user to select project to which files must be added instead of displaying error message
            CoreErrorMessages.noMainProjectFound();
        }
        Tgd tgd = new Tgd();
        tgd.setFilename(originalFO.getName());
        tgd.setExtension(originalFO.getExt());
        tgd.setPath(originalFO.getPath());
        //try to find relative path for file to project folder root if it exists
        tgd.setRelativePath(FileUtil.getRelativePath(project.getProjectDirectory(), originalFO));
        try {
            tgd.setFiletype(service.getType(f));
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        try {
            //TODO: check if file exists
            newFO = FileUtil.createData(dataObject.getPrimaryFile(), originalFO.getName().concat(".xml"));
            cacheSubFolder = projectCacheFolder.createFolder(cacheFolderName);
            tgd.setCacheFolderName(cacheSubFolder.getNameExt());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (newFO != null) {
            File out = FileUtil.toFile(newFO);
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(tgd.getClass().getPackage().getName());
                javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                // We marshal the data to a new xml file
                marshaller.marshal(tgd, out);
            } catch (javax.xml.bind.JAXBException ex) {
                // TODO Handle exception
                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            }
        }
    }
}
