package org.glotaran.core.main.notice;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 * Notice that points users to pyglotaran and to the releases page of this
 * application. Shown at startup until the user dismisses it, and from the Help
 * menu.
 */
@Messages({
    "TITLE_PyglotaranNotice=About pyglotaran and future Glotaran releases",
    "MSG_PyglotaranNotice=<html><body>"
    + "<p>For new work, Glotaran 1.x has been superseded by <b>pyglotaran</b>,<br>"
    + "a Python package for global and target analysis:<br>"
    + "<a href=\"https://github.com/glotaran/pyglotaran\">https://github.com/glotaran/pyglotaran</a></p>"
    + "<p>New releases of this application are published at:<br>"
    + "<a href=\"https://github.com/glotaran/glotaran-legacy/releases\">https://github.com/glotaran/glotaran-legacy/releases</a></p>"
    + "<p>Glotaran 1.x uses R and TIMP; pyglotaran uses a different engine, "
    + "so numerical results can differ slightly between the two.</p>"
    + "<p>You can show this notice again from Help | pyglotaran and Glotaran Releases.</p>"
    + "</body></html>",
    "CHK_PyglotaranNoticeDismiss=Do not show this notice at startup"
})
public final class PyglotaranNotice {

    private static final String PREF_DISMISSED = "pyglotaranNoticeDismissed";

    private PyglotaranNotice() {
    }

    private static Preferences preferences() {
        return NbPreferences.forModule(PyglotaranNotice.class);
    }

    public static boolean isDismissed() {
        return preferences().getBoolean(PREF_DISMISSED, false);
    }

    /**
     * Shows the notice in a modal dialog and stores the state of the
     * "do not show at startup" check box. Must be called on the event
     * dispatch thread.
     */
    public static void show() {
        JEditorPane text = new JEditorPane("text/html", Bundle.MSG_PyglotaranNotice());
        text.setEditable(false);
        text.setOpaque(false);
        text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        text.setFont(UIManager.getFont("Label.font"));
        text.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && e.getURL() != null) {
                    openInBrowser(e);
                }
            }
        });

        JCheckBox dismiss = new JCheckBox(Bundle.CHK_PyglotaranNoticeDismiss(), isDismissed());

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(text, BorderLayout.CENTER);
        panel.add(dismiss, BorderLayout.SOUTH);

        NotifyDescriptor descriptor = new NotifyDescriptor.Message(panel, NotifyDescriptor.INFORMATION_MESSAGE);
        descriptor.setTitle(Bundle.TITLE_PyglotaranNotice());
        DialogDisplayer.getDefault().notify(descriptor);

        preferences().putBoolean(PREF_DISMISSED, dismiss.isSelected());
    }

    private static void openInBrowser(HyperlinkEvent e) {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            return;
        }
        try {
            Desktop.getDesktop().browse(e.getURL().toURI());
        } catch (IOException | URISyntaxException ex) {
            // The URL is shown as text in the notice, so the user can copy it instead.
        }
    }
}
