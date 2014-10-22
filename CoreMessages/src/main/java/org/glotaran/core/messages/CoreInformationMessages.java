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
 * @author sergey
 */
public class CoreInformationMessages {
    public static void SVDFilterInfo(){
        NotifyDescriptor.Message warningMessage =new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("svdFilterInfo"),
                NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(warningMessage);
    }

    public static void HDF5Info(String datasetname){
        NotifyDescriptor.Message warningMessage =new NotifyDescriptor.Message(datasetname,
                NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(warningMessage);
    }
}
