/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.glotaran.core.messages;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author slapten
 */
public class CoreWarningMessages {
    public static void propFileWarning(){
        NotifyDescriptor.Message warningMessage =new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("propFileWarning"),
                NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(warningMessage);
    }

    public static void wrongIterNumWarning(){
        NotifyDescriptor.Message warningMessage =new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("wrongIterNumber"),
                NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(warningMessage);
    }

    public static void wrongSelectionWarning(){
        NotifyDescriptor.Message warningMessage =new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("wrongSelectionNumber"),
                NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(warningMessage);
    }

    public static Object folderExistsWarning(){
        NotifyDescriptor warningMessage =new NotifyDescriptor.Confirmation(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("folderExistsWarning"),
                NotifyDescriptor.WARNING_MESSAGE);
        return DialogDisplayer.getDefault().notify(warningMessage);
    }


}
