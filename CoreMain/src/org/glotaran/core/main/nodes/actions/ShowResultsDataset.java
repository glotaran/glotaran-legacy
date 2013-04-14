package org.glotaran.core.main.nodes.actions;

import java.util.Collection;
import org.glotaran.core.main.interfaces.ResultsLoaderInterface;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.main.nodes.dataobjects.TimpResultDataObject;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ShowResultsDataset extends CookieAction {

    private final Collection<? extends ResultsLoaderInterface> services;

    public ShowResultsDataset() {
        services = Lookup.getDefault().lookupAll(ResultsLoaderInterface.class);
    }

    protected void performAction(Node[] activatedNodes) {
        TimpResultDataObject dataObject = activatedNodes[0].getLookup().lookup(TimpResultDataObject.class);
        String datatype = null;
        if (!(dataObject == null)) {
            TimpResultDataset dataset = null;
            dataset = dataObject.getTimpResultDataset();
            if (dataset != null) {
                datatype = dataset.getType();
            }

            for (final ResultsLoaderInterface service : services) {
                if (service.getType().equalsIgnoreCase(datatype)) {
                    service.openResultDisplayer(dataObject);
                }
            }
        } else {
            CoreErrorMessages.somethingStrange();
        }

    }

    protected int mode() {
        return CookieAction.MODE_ALL;
    }

    public String getName() {
        return NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("showResults");
    }

    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
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

