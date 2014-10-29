/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.hdf5interface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
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
    
    public static DatasetTimp load(File file) throws FileNotFoundException {
        if (0 == init) {
            init();
        }
        
        DatasetTimp dataset = new DatasetTimp();

        dataset.setDatasetName("Test HDF5");

        return dataset;
    }

    public static void save(File file, DatasetTimp dataset) throws IOException {
        H5File hdfFile = null;
                
        if (0 == init) {
            init();
        }
        // Retrieve an instance of the implementing class for the HDF5 format
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
        if (fileFormat == null) {
            CoreInformationMessages.HDF5Info("Cannot find HDF5 FileFormat.");
            return;
        }

        
        try {
            hdfFile = (H5File) fileFormat.createFile(file.getCanonicalPath(), FileFormat.FILE_CREATE_DELETE);
            // Check for error condition and report.
            if (hdfFile == null) {
                CoreInformationMessages.HDF5Info("Failed to create file: " + file.getName());
                return;
            }
            hdfFile.open();
        } catch (Exception ex) {
            CoreInformationMessages.HDF5Info("Failed to open " + file.getName() +  ": " + ex.getMessage());
            return;
        } 
        
        try {
            Datatype dtype;
            Attribute attr;
            long[] data_dim;
            int[] value_int;
            String[] value_str;
            Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) hdfFile.getRootNode()).getUserObject();
            // create DatasetTimp group at the root
            Group groupTimp = hdfFile.createGroup("DatasetTimp", root);

            //create data type and store the data
            data_dim = new long[] {dataset.getNl(), dataset.getNt()};
            dtype = hdfFile.createDatatype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);
            Dataset dset = hdfFile.createScalarDS("psisim", groupTimp, dtype, data_dim, data_dim, null, 0, dataset.getPsisim());
            CoreInformationMessages.HDF5Info("psisim size " + data_dim[0] + ", " + data_dim[1]);
            
            //store data dimension as attributes (for testing - the size of the dataset is provided by HDF5 API)
            dtype = hdfFile.createDatatype(Datatype.CLASS_INTEGER, 4, Datatype.NATIVE, Datatype.NATIVE);
            data_dim = new long[] { 1 };
            value_int = new int[] { dataset.getNl() };
            attr = new Attribute("number of wavelength steps", dtype, data_dim, value_int );
            dset.writeMetadata(attr);
            value_int = new int[] { dataset.getNt() };
            attr = new Attribute("number of time steps", dtype, data_dim, value_int );
            dset.writeMetadata(attr);
            
            //store dataset attributes
            dtype = hdfFile.createDatatype(Datatype.CLASS_STRING, -1, Datatype.NATIVE, Datatype.SIGN_NONE);
            value_str = new String[] { dataset.getDatasetName() };
            attr = new Attribute("name", dtype, data_dim, value_str);
            groupTimp.writeMetadata(attr);
            
        } catch (Exception ex) {
            CoreInformationMessages.HDF5Info(file.getName() +  ": " + ex.getMessage());
        } finally {
            try {
                hdfFile.close();
                CoreInformationMessages.HDF5Info("Saved TIMP dataset \"" + dataset.getDatasetName() + "\" to HDF5 file " + file.getCanonicalPath());
            } catch (HDF5Exception ex) {
                CoreInformationMessages.HDF5Info(file.getName() +  ": " + ex.getMessage());
            }
        }
    }
}
