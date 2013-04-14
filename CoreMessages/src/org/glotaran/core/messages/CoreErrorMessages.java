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
public class CoreErrorMessages {

    public static void folderNotWritable(String path) {
        NotifyDescriptor.Message errorMessage;
        if (path != null) {
            errorMessage = new NotifyDescriptor.Message(
                    NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("folderNotWritable")
                    + "The specified path was: " + path, 
                    NotifyDescriptor.ERROR_MESSAGE);
        } else {
            errorMessage = new NotifyDescriptor.Message(
                    NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("folderNotWritable"), 
                    NotifyDescriptor.ERROR_MESSAGE);
        }
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void fileNotFound() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("fileNotFound"), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }
    
    public static void noSVDCalculated() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("noSVDCalculated"), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void noOutputPathSpecified() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("noOutputPathSpecified"), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void fileSaveError(String filename) {
        NotifyDescriptor.Message errorMessage;
        if (filename != null) {
            errorMessage = new NotifyDescriptor.Message(
                    NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("IOException")
                    + " " + filename, 
                    NotifyDescriptor.ERROR_MESSAGE);
        } else {
            errorMessage = new NotifyDescriptor.Message(
                    NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("fileSaveError"), 
                    NotifyDescriptor.ERROR_MESSAGE);
        }
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void fileLoadException(String filetype) {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                filetype + " "
                + NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("fileLoadException"), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void fileDeleteException(String filetype) {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                filetype + " "
                + NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("fileDeleteException"), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void noMainProjectFound() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("selMainProj"), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void projectFolderException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("projectFolderException"), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void somethingStrange() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                "Something strange!!!", 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void jaxbException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("JAXBException"), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void IOException(String place) {
        NotifyDescriptor.Message errorMessage;
        if (place != null) {
            errorMessage = new NotifyDescriptor.Message(
                    NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("IOException")
                    +" "+ place, 
                    NotifyDescriptor.ERROR_MESSAGE);
        } else {
            errorMessage = new NotifyDescriptor.Message(
                    NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("IOException"), 
                    NotifyDescriptor.ERROR_MESSAGE);
        }
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void oldClassException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("oldClassException"), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void createFolderException(String foldername) {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("createFolderException")
                + " " + foldername, 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void headerFileException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("headerNotValid"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void selCorrChNum() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("set_correct_chanNum"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void updLinLogException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("updateLinLogException"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void dragDropException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("sdtFileLoadException"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void numberFormatException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("numberFormatException"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

        
    public static void notAValidModelException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("notAValidModel"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }
         
    public static void initModelException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("initModelException"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void noRFoundException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("noRerrorMessage"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }
    
    public static void noRServeFoundException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("noRserveerrorMessage"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }
    
    public static void noResultsException() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("noResultsError"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void parametersExists(String paramName) {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                paramName + NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("parametersExists"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void containerConnected(String container, String model) {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("datasetContainer")
                + container + " "
                + NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("datasetContainerConnected")
                + " " + model,
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void containerNotConnected() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("connectDatasetContainer"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void differentSizeDatasetsError() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("differentDatasetSize"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }

    public static void notEnoughTimesteps() {
        NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(
                NbBundle.getBundle("org/glotaran/core/messages/Bundle").getString("fewTimeSteps"),
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(errorMessage);
    }
}