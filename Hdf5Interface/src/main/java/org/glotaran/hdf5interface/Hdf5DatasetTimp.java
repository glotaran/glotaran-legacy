/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.hdf5interface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import ncsa.hdf.object.FileFormat;
//import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.*;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.messages.CoreInformationMessages;

/**
 *
 * @author anton
 */
public class Hdf5DatasetTimp {

    private static int init = 0;
    
    public static DatasetTimp load(File file) throws FileNotFoundException {
        DatasetTimp dataset = new DatasetTimp();

        dataset.setDatasetName("Test HDF5");

        return dataset;
    }

    public static void save(File file, DatasetTimp dataset) throws IOException {

        if (0 == init) {
            try {
                Class fileclass = Class.forName("ncsa.hdf.object.h5.H5File");
                FileFormat fileformat = (FileFormat) fileclass.newInstance();
                if (fileformat != null) {
                    FileFormat.addFileFormat("HDF5", fileformat);
                    init = 1;
                }
            } catch (Throwable err) {
                CoreInformationMessages.HDF5Info(err.getMessage());
            }
        }
        // Retrieve an instance of the implementing class for the HDF5 format
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
        if (fileFormat == null) {
            CoreInformationMessages.HDF5Info("Cannot find HDF5 FileFormat.");
            //return;
        }

        try {
            H5File testFile = (H5File) fileFormat.createFile(file.getCanonicalPath(), FileFormat.FILE_CREATE_DELETE);
            // Check for error condition and report.
            if (testFile == null) {
                CoreInformationMessages.HDF5Info("Failed to create file: " + file.getName());
            }
        } catch (Exception ex) {
            CoreInformationMessages.HDF5Info(file.getName() +  ": " + ex.getMessage());
        } finally {
            CoreInformationMessages.HDF5Info("Saving to TIMP dataset \"" + dataset.getDatasetName() + "\" to HDF5 file " + file.getCanonicalPath());
        }
    }
}
