package org.glotaran.core.main.nodes.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.main.interfaces.DatasetLoaderInterface;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.structures.DatasetTimp;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ShowDataset extends CookieAction {

    private final Collection<? extends DatasetLoaderInterface> services;

    public ShowDataset() {
        services = Lookup.getDefault().lookupAll(DatasetLoaderInterface.class);
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        String filetype;
        DatasetTimp data=null;
        File tgdFile;
        TGProject project;
        TgdDataObject dataObject = activatedNodes[0].getLookup().lookup(TgdDataObject.class);
        
//        setName(dataObject.getTgd().getFilename());

        //try to get the file from local cache
        project = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
        if (dataObject.getTgd().getRelativePath() != null) {
            tgdFile = new File(project.getProjectDirectory().getPath() + File.separator + dataObject.getTgd().getRelativePath());
        } else { //try the orginal location
            tgdFile = new File(dataObject.getTgd().getPath());
        }

//        setName(NbBundle.getMessage(SpecEditorTopCompNew.class, "CTL_StreakLoaderTopComponent"));
//        setToolTipText(NbBundle.getMessage(SpecEditorTopCompNew.class, "HINT_StreakLoaderTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
//        data = new DatasetTimp();
        
//get loaders from lookup        
        Collection<? extends TGDatasetInterface> loadServices = Lookup.getDefault().lookupAll(TGDatasetInterface.class);
        for (final TGDatasetInterface service : loadServices) {
            try {
                if (service.Validator(tgdFile)) {
                    data = service.loadFile(tgdFile);
                    if (data == null) {
                        CoreErrorMessages.fileLoadException(tgdFile.getName());
                        return;
                    }
                    break;
                }
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException | IllegalAccessException | InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        if (!(data == null)) {
            filetype = data.getType();
            for (final DatasetLoaderInterface service : services) {
                if (service.getType().equalsIgnoreCase(filetype)) {
                    service.openDatasetEditor(data,dataObject);
                }
            }
        } else {
            CoreErrorMessages.somethingStrange();
        }
    }

    @Override
    protected int mode() {
        return CookieAction.MODE_ALL;
    }

    @Override
    public String getName() {
        return NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("showDataset");
    }

    @Override
    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
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
}

