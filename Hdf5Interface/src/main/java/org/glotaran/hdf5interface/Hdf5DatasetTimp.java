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
            CoreInformationMessages.HDF5Info("Failed to open " + file.getName() + ": " + ex.getMessage());
            return;
        }

        try {
            Dataset dsetPsisim, dset;
            Attribute attr;
            long[] data_dim;
            long[] single_dim = new long[]{1};
            double[] value_dbl;
            long[] value_long;
            String[] value_str;
            Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) hdfFile.getRootNode()).getUserObject();
            // create DatasetTimp group at the root, and 3 sub-groups: for spectra, FLIM image and instrument response function
            Group groupTimp = hdfFile.createGroup("DatasetTimp", root);
            Group groupPsisim = hdfFile.createGroup("psisim", groupTimp);
            Group groupFlim = hdfFile.createGroup("flim", groupTimp);
            Group groupIRF = hdfFile.createGroup("measured_irf", groupTimp);

            //datatypes
            Datatype dtDouble = hdfFile.createDatatype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);
            Datatype dtLong = hdfFile.createDatatype(Datatype.CLASS_INTEGER, 8, Datatype.NATIVE, Datatype.NATIVE);
            Datatype dtVarString = hdfFile.createDatatype(Datatype.CLASS_STRING, -1, Datatype.NATIVE, Datatype.SIGN_NONE);

            if (dataset.getPsisim() != null) {
                //store psisim data, and corresponding min/max values as attributes
                assert (dataset.getNl() * dataset.getNt() == dataset.getPsisim().length);
                data_dim = new long[]{dataset.getNl(), dataset.getNt()};
                dsetPsisim = hdfFile.createScalarDS("spectra", groupPsisim, dtDouble, data_dim, data_dim, null, 0, dataset.getPsisim());
                value_dbl = new double[]{dataset.getMinInt()};
                attr = new Attribute("min", dtDouble, single_dim, value_dbl);
                dsetPsisim.writeMetadata(attr);
                value_dbl = new double[]{dataset.getMaxInt()};
                attr = new Attribute("max", dtDouble, single_dim, value_dbl);
                dsetPsisim.writeMetadata(attr);

                //store (inside Psisim group) time and wavelength vectors with corresponding labels and units as attributes
                if (dataset.getX() != null) {
                    assert (dataset.getNt() == dataset.getX().length);
                    data_dim = new long[]{dataset.getNt()};
                    dset = hdfFile.createScalarDS("time_scale", groupPsisim, dtDouble, data_dim, data_dim, null, 0, dataset.getX());
                    value_str = new String[]{dataset.getX1label()};
                    attr = new Attribute("label", dtVarString, single_dim, value_str);
                    dset.writeMetadata(attr);
                    value_str = new String[]{dataset.getX1unit()};
                    attr = new Attribute("units", dtVarString, single_dim, value_str);
                    dset.writeMetadata(attr);
                }
                if (dataset.getX2() != null) {
                    assert (dataset.getNl() == dataset.getX2().length);
                    data_dim = new long[]{dataset.getNl()};
                    dset = hdfFile.createScalarDS("wavelength_scale", groupPsisim, dtDouble, data_dim, data_dim, null, 0, dataset.getX2());
                    value_str = new String[]{dataset.getX2label()};
                    attr = new Attribute("label", dtVarString, single_dim, value_str);
                    dset.writeMetadata(attr);
                    value_str = new String[]{dataset.getX2unit()};
                    attr = new Attribute("units", dtVarString, single_dim, value_str);
                    dset.writeMetadata(attr);
                }
            }

            //store FLIM image
            if (dataset.getIntenceIm() != null) {
                assert (dataset.getOriginalHeight() * dataset.getOriginalWidth() == dataset.getIntenceIm().length);
                data_dim = new long[]{dataset.getOriginalHeight(), dataset.getOriginalWidth()};
                hdfFile.createScalarDS("image", groupFlim, dtDouble, data_dim, data_dim, null, 0, dataset.getIntenceIm());
            }
            
            //store IRF data
            if (dataset.getMeasuredIRF() != null && dataset.getMeasuredIRFDomainAxis() != null) {
                assert (dataset.getMeasuredIRF().length == dataset.getMeasuredIRFDomainAxis().length);
                data_dim = new long[]{dataset.getMeasuredIRF().length};
                hdfFile.createScalarDS("irf", groupIRF, dtDouble, data_dim, data_dim, null, 0, dataset.getMeasuredIRF());
                data_dim = new long[]{dataset.getMeasuredIRFDomainAxis().length};
                hdfFile.createScalarDS("axis", groupIRF, dtDouble, data_dim, data_dim, null, 0, dataset.getMeasuredIRFDomainAxis());
            }
            else if (dataset.getMeasuredIRF() != null || dataset.getMeasuredIRFDomainAxis() != null){
                assert false : "Measured IRF must have both the response and axis data?";
            }
            
            //store dataset attributes (name, type etc)
            value_str = new String[]{ dataset.getDatasetName() };
            attr = new Attribute("name", dtVarString, single_dim, value_str);
            groupTimp.writeMetadata(attr);
            value_str = new String[]{ dataset.getType() };
            attr = new Attribute("type", dtVarString, single_dim, value_str);
            groupTimp.writeMetadata(attr);
            value_str = new String[]{ dataset.getDatalabel() };
            attr = new Attribute("label", dtVarString, single_dim, value_str);
            groupTimp.writeMetadata(attr);
            value_str = new String[]{ dataset.getDataunit() };
            attr = new Attribute("units", dtVarString, single_dim, value_str);
            groupTimp.writeMetadata(attr);
            
            //DatasetTimp HDF5 version
            value_long = new long[] { version };
            attr = new Attribute("version", dtLong, single_dim, value_long);
            groupTimp.writeMetadata(attr);

        } catch (Exception ex) {
            CoreInformationMessages.HDF5Info(file.getName() + ": " + ex.getMessage());
        } finally {
            try {
                hdfFile.close();
            } catch (HDF5Exception ex) {
                CoreInformationMessages.HDF5Info(file.getName() + ": " + ex.getMessage());
            }
        }
    }
}
