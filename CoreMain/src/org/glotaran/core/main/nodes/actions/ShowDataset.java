package org.glotaran.core.main.nodes.actions;

import java.util.Collection;
import org.glotaran.core.main.interfaces.DatasetLoaderInterface;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
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
        TgdDataObject dataObject = activatedNodes[0].getLookup().lookup(TgdDataObject.class);
        if (!(dataObject == null)) {
            filetype = dataObject.getTgd().getFiletype();
            for (final DatasetLoaderInterface service : services) {
                if (service.getType().equalsIgnoreCase(filetype)) {
                    service.openDatasetEditor(dataObject);
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

