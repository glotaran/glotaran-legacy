/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.hdf5interface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
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

    private static H5File getHdf5File(File file, boolean write) {
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

    private static boolean readPsisim(DatasetTimp dataset, Group groupPsisim) {
        try {
            List groupMembers = groupPsisim.getMemberList();
            for (int i = 0; i < groupMembers.size(); i++) {
                HObject obj = (HObject) groupMembers.get(i);
                switch (obj.getName()) {
                    case "spectra": {
                        Dataset dset = (Dataset) obj;
                        List meta = dset.getMetadata();
                        long[] dims = dset.getDims();
                        if (dims.length != 2) {
                            CoreInformationMessages.HDF5Info("Invalid number of DatasetTimp spectra dimensions");
                            return false;
                        }
                        dataset.setPsisim((double[]) dset.read());
                        dataset.setNl((int) dims[0]);
                        dataset.setNt((int) dims[1]);
                        //read min/max values
                        for (int j = 0; j < meta.size(); ++j) {
                            Object objMeta = (Object) meta.get(j);
                            if (!(objMeta instanceof Attribute)) {
                                continue;
                            }
                            Attribute attr = (Attribute) objMeta;
                            double[] val = (double[]) attr.getValue();
                            if (val == null) {
                                continue;
                            }
                            switch (attr.getName()) {
                                case "min":
                                    dataset.setMinInt(val[0]);
                                    break;
                                case "max":
                                    dataset.setMaxInt(val[0]);
                                    break;
                            }
                        }
                        break;
                    }
                    case "time_scale": {
                        Dataset dset = (Dataset) obj;
                        List meta = dset.getMetadata();
                        long[] dims = dset.getDims();
                        if (dims.length != 1) {
                            CoreInformationMessages.HDF5Info("Invalid number of DatasetTimp time scale dimensions");
                            break;
                        }
                        dataset.setX((double[]) dset.read());
                        //read time scale units and label
                        for (int j = 0; j < meta.size(); ++j) {
                            Object objMeta = (Object) meta.get(j);
                            if (!(objMeta instanceof Attribute)) {
                                continue;
                            }
                            Attribute attr = (Attribute) objMeta;
                            String[] val = (String[]) attr.getValue();
                            if (val == null) {
                                continue;
                            }
                            switch (attr.getName()) {
                                case "label":
                                    dataset.setX1label(val[0]);
                                    break;
                                case "units":
                                    dataset.setX1unit(val[0]);
                                    break;
                            }
                        }
                        break;
                    }
                    case "wavelength_scale": {
                        Dataset dset = (Dataset) obj;
                        List meta = dset.getMetadata();
                        long[] dims = dset.getDims();
                        if (dims.length != 1) {
                            CoreInformationMessages.HDF5Info("Invalid number of DatasetTimp wavelength scale dimensions");
                            break;
                        }
                        dataset.setX2((double[]) dset.read());
                        //read time scale units and label
                        for (int j = 0; j < meta.size(); ++j) {
                            Object objMeta = (Object) meta.get(j);
                            if (!(objMeta instanceof Attribute)) {
                                continue;
                            }
                            Attribute attr = (Attribute) objMeta;
                            String[] val = (String[]) attr.getValue();
                            if (val == null) {
                                continue;
                            }
                            switch (attr.getName()) {
                                case "label":
                                    dataset.setX2label(val[0]);
                                    break;
                                case "units":
                                    dataset.setX2unit(val[0]);
                                    break;
                            }
                        }
                        break;
                    }
                }
            }
        }catch (Exception ex) {
            CoreInformationMessages.HDF5Info(groupPsisim.getName() + ": " + ex.getMessage());
            return false;
        }
        return true;
    }
    
    private static void readFlim(DatasetTimp dataset, Group groupFlim) {
    }
    
    private static void readIRF(DatasetTimp dataset, Group groupIrf) {
    }
    
    public static DatasetTimp load(File file) throws FileNotFoundException {
        DatasetTimp dataset = new DatasetTimp();
        H5File hdfFile = getHdf5File(file, true);

        if (hdfFile == null) {
            return dataset;
        }

        try {
            long fileVersion = 0;
            Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) hdfFile.getRootNode()).getUserObject();
            if (root == null) {
                return dataset;
            }
            Group groupTimp = (Group) FileFormat.findObject(hdfFile, "/DatasetTimp");
            if (groupTimp == null) {
                return dataset; //return empty (default) DatasetTimp
            }

            // read dataset attributes
            List meta = groupTimp.getMetadata();
            for (int j = 0; j < meta.size(); ++j) {
                Object objMeta = (Object) meta.get(j);
                if (!(objMeta instanceof Attribute)) {
                    continue;
                }
                Attribute attr = (Attribute) objMeta;
                try {
                    Object objVal = attr.getValue();
                    if (objVal instanceof String[]) {
                        String[] val = (String[]) objVal;
                        switch (attr.getName()) {
                            case "name":
                                dataset.setDatasetName(val[0]);
                                break;
                            case "units":
                                dataset.setDataunit(val[0]);
                                break;
                            case "type":
                                dataset.setType(val[0]);
                                break;
                            case "label":
                                dataset.setDatalabel(val[0]);
                                break;
                        }
                    }
                    else if(objVal instanceof long[]){
                        if(attr.getName().equals("version"))
                            fileVersion = ((long[]) objVal)[0];
                    }
                } catch (Exception ex) {
                    //do not care - these are just attributes...
                }
            }
             
            if (version != fileVersion){
                CoreInformationMessages.HDF5Info("Incompatible version of HDF5 DatasetTimp file (" + fileVersion + ")");
                return dataset;
            }
            
            // read psisim data
            Group groupPsisim = (Group) FileFormat.findObject(hdfFile, "/DatasetTimp/psisim");
            if (groupPsisim == null) {
                return dataset; //psisim data must exist in DatasetTimp
            }
            if(!readPsisim(dataset, groupPsisim))
                return dataset;
            
            // read FLIM image
            Group groupFlim = (Group) FileFormat.findObject(hdfFile, "/DatasetTimp/flim");
            if (groupFlim != null)
                readFlim(dataset, groupFlim);
            
            // read FLIM image
            Group groupIrf = (Group) FileFormat.findObject(hdfFile, "/DatasetTimp/measured_irf");
            if (groupFlim != null)
                readIRF(dataset, groupIrf);

        } catch (Exception ex) {
            CoreInformationMessages.HDF5Info(file.getName() + ": " + ex.getMessage());
        } finally {
            try {
                hdfFile.close();
            } catch (HDF5Exception ex) {
                CoreInformationMessages.HDF5Info(file.getName() + ": " + ex.getMessage());
            }
        }

        return dataset;
    }

    public static void save(File file, DatasetTimp dataset) throws IOException {
        H5File hdfFile = getHdf5File(file, true);

        if (hdfFile == null) {
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
            } else if (dataset.getMeasuredIRF() != null || dataset.getMeasuredIRFDomainAxis() != null) {
                assert false : "Measured IRF must have both the response and axis data?";
            }

            //store dataset attributes (name, type etc)
            value_str = new String[]{dataset.getDatasetName()};
            attr = new Attribute("name", dtVarString, single_dim, value_str);
            groupTimp.writeMetadata(attr);
            value_str = new String[]{dataset.getType()};
            attr = new Attribute("type", dtVarString, single_dim, value_str);
            groupTimp.writeMetadata(attr);
            value_str = new String[]{dataset.getDatalabel()};
            attr = new Attribute("label", dtVarString, single_dim, value_str);
            groupTimp.writeMetadata(attr);
            value_str = new String[]{dataset.getDataunit()};
            attr = new Attribute("units", dtVarString, single_dim, value_str);
            groupTimp.writeMetadata(attr);

            //DatasetTimp HDF5 version
            value_long = new long[]{version};
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
