package org.glotaran.core.main.notice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Help",
        id = "org.glotaran.core.main.notice.ShowPyglotaranNoticeAction")
@ActionRegistration(
        displayName = "#CTL_ShowPyglotaranNoticeAction")
@ActionReference(path = "Menu/Help", position = 1500)
@Messages("CTL_ShowPyglotaranNoticeAction=pyglotaran and Glotaran Releases")
public final class ShowPyglotaranNoticeAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        PyglotaranNotice.show();
    }
}
