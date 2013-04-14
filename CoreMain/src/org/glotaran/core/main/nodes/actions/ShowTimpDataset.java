package org.glotaran.core.main.nodes.actions;

import java.util.Collection;
import org.glotaran.core.main.interfaces.DatasetLoaderInterface;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.models.structures.DatasetTimp;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ShowTimpDataset extends CookieAction {

    private final Collection<? extends DatasetLoaderInterface> services;

    public ShowTimpDataset() {
        services = Lookup.getDefault().lookupAll(DatasetLoaderInterface.class);
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        TimpDatasetDataObject dataObject = activatedNodes[0].getLookup().lookup(TimpDatasetDataObject.class);
        String datatype = null;
        if (!(dataObject == null)) {
            DatasetTimp dataset = null;
            dataset = dataObject.getDatasetTimp();
            if (dataset != null) {
                datatype = dataset.getType();
            }
            for (final DatasetLoaderInterface service : services) {
                if (service.getType().equalsIgnoreCase(datatype)) {
                    service.openDatasetEditor(dataObject);
                }

            }

            //filename = FileUtil.toFile(dataObject.getPrimaryFile()).getAbsolutePath();
//            filename = dataObject.getPrimaryFile().getPath().concat(dataObject. getPrimaryFile().getName());
//            Confirmation msg = new NotifyDescriptor.Confirmation(filename, NotifyDescriptor.OK_CANCEL_OPTION);
//                DialogDisplayer.getDefault().notify(msg);
        } else {
            CoreErrorMessages.somethingStrange();
        }

    }

    protected int mode() {
        return CookieAction.MODE_ALL;
    }

    public String getName() {
        return NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("showDataset");
    }

    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

