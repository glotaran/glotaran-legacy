/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.hdf5interface;

import java.io.File;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5File;
import org.glotaran.core.messages.CoreInformationMessages;

/**
 *
 * @author anton
 */
class Hdf5Provider {
    public static final long version = 1L;
    private static int init = 0;

    private static void init() {
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

    public static H5File getHdf5File(File file, boolean write) {
        H5File hdfFile;

        if (0 == init) {
            init();
        }

        // Retrieve an instance of the implementing class for the HDF5 format
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
        if (fileFormat == null) {
            CoreInformationMessages.HDF5Info("Cannot find HDF5 FileFormat.");
            return null;
        }

        try {
            if (write) {
                hdfFile = (H5File) fileFormat.createFile(file.getCanonicalPath(), FileFormat.FILE_CREATE_DELETE);
            } else {
                hdfFile = (H5File) fileFormat.createInstance(file.getCanonicalPath(), FileFormat.READ);
            }
            // Check for error condition and report.
            if (hdfFile == null) {
                CoreInformationMessages.HDF5Info("Failed to create file: " + file.getName());
                return null;
            }
            hdfFile.open();
        } catch (Exception ex) {
            CoreInformationMessages.HDF5Info("Failed to open " + file.getName() + ": " + ex.getMessage());
            return null;
        }

        return hdfFile;
    }
}
