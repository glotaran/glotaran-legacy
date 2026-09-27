package org.glotaran.core.main.notice;

import org.openide.windows.OnShowing;

/**
 * Shows the pyglotaran notice once the main window is visible, unless the user
 * has dismissed it.
 */
@OnShowing
public final class ShowNoticeOnStartup implements Runnable {

    @Override
    public void run() {
        if (!PyglotaranNotice.isDismissed()) {
            PyglotaranNotice.show();
        }
    }
}
