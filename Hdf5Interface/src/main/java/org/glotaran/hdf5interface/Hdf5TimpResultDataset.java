/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.hdf5interface;

import Jama.Matrix;
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
import ncsa.hdf.object.h5.H5File;
import org.glotaran.core.messages.CoreInformationMessages;
import org.glotaran.core.models.structures.TimpResultDataset;

/**
 *
 * @author anton
 */
public class Hdf5TimpResultDataset {

    public static TimpResultDataset load(File file) throws FileNotFoundException {
        TimpResultDataset dataset = new TimpResultDataset();
        H5File hdfFile = Hdf5Provider.getHdf5File(file, false);

        if (hdfFile == null) {
            return null;
        }

        //start reading the data from the file
        try {
            long fileVersion = 0;
            Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) hdfFile.getRootNode()).getUserObject();
            if (root == null) {
                return null;
            }
            Group groupTimp = (Group) FileFormat.findObject(hdfFile, "/TimpResult");
            if (groupTimp == null) {
                return null;
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
                    if (objVal instanceof long[]) {
                        if (attr.getName().equals("version")) {
                            fileVersion = ((long[]) objVal)[0];
                        }
                    }
                } catch (Exception ex) {
                    //do not care - these are just attributes...
                }
            }

            if (Hdf5Provider.version != fileVersion) {
                CoreInformationMessages.HDF5Info("Incompatible version of HDF5 TimpResultDataset file (" + fileVersion + ")");
                return null;
            }

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

    private static void saveSourceData(H5File hdfFile, Group root, TimpResultDataset dataset) throws Exception {
        long[] data_dim;
        double[] value_dbl;
        long[] single_dim = new long[]{1};
        Dataset dsetPsisim;
        Attribute attr;

        Group groupTimp = hdfFile.createGroup("DatasetTimp", root);
        Group groupPsisim = hdfFile.createGroup("psisim", groupTimp);
        Group groupFlim = hdfFile.createGroup("flim", groupTimp);

        //datatypes
        Datatype dtDouble = hdfFile.createDatatype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);

        if (dataset.getTraces() != null) {
            //store psisim data, and corresponding min/max values as attributes
            Matrix m = dataset.getTraces();
            data_dim = new long[]{m.getColumnDimension(), m.getRowDimension()};
            dsetPsisim = hdfFile.createScalarDS("spectra", groupPsisim, dtDouble, data_dim, data_dim, null, 0, m);
            value_dbl = new double[]{dataset.getMinInt()};
            attr = new Attribute("min", dtDouble, single_dim, value_dbl);
            dsetPsisim.writeMetadata(attr);
            value_dbl = new double[]{dataset.getMaxInt()};
            attr = new Attribute("max", dtDouble, single_dim, value_dbl);
            dsetPsisim.writeMetadata(attr);

            //store (inside Psisim group) time and wavelength vectors with corresponding labels and units as attributes
            if (dataset.getX() != null) {
                assert (m.getRowDimension() == dataset.getX().length);
                data_dim = new long[]{m.getRowDimension()};
                hdfFile.createScalarDS("time_scale", groupPsisim, dtDouble, data_dim, data_dim, null, 0, dataset.getX());
            }
            if (dataset.getX2() != null) {
                assert (m.getColumnDimension() == dataset.getX2().length);
                data_dim = new long[]{m.getColumnDimension()};
                hdfFile.createScalarDS("wavelength_scale", groupPsisim, dtDouble, data_dim, data_dim, null, 0, dataset.getX2());
            }
        }

        //store FLIM image
        if (dataset.getIntenceIm() != null) {
            assert (dataset.getOrheigh() * dataset.getOrwidth() == dataset.getIntenceIm().length);
            data_dim = new long[]{dataset.getOrheigh(), dataset.getOrwidth()};
            hdfFile.createScalarDS("image", groupFlim, dtDouble, data_dim, data_dim, null, 0, dataset.getIntenceIm());
        }
    }

    private static void saveIRFModel(H5File hdfFile, Group root, TimpResultDataset dataset) throws Exception {
        long[] data_dim;
        double[] value_dbl;
        long[] single_dim = new long[]{1};
        Attribute attr;

        //datatypes
        Datatype dtDouble = hdfFile.createDatatype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);

        Group groupIRFModel = hdfFile.createGroup("irf_model", root);

        assert (dataset.getIrfpar() != null);
        data_dim = new long[]{dataset.getIrfpar().length};
        hdfFile.createScalarDS("parameters", groupIRFModel, dtDouble, data_dim, data_dim, null, 0, dataset.getIrfpar());

        assert (dataset.getParmu() != null);
        data_dim = new long[]{dataset.getParmu().length};
        hdfFile.createScalarDS("mu", groupIRFModel, dtDouble, data_dim, data_dim, null, 0, dataset.getParmu());

        if (dataset.getPartau() != null) {
            data_dim = new long[]{dataset.getPartau().length};
            hdfFile.createScalarDS("tau", groupIRFModel, dtDouble, data_dim, data_dim, null, 0, dataset.getPartau());
        }

        value_dbl = new double[]{dataset.getLamdac()};
        attr = new Attribute("centre_wavelength", dtDouble, single_dim, value_dbl);
        groupIRFModel.writeMetadata(attr);
    }

    private static void saveSpectralModel(H5File hdfFile, Group root, TimpResultDataset dataset) throws Exception {
        long[] data_dim;
        double[] value_dbl;
        Attribute attr;
        long[] single_dim = new long[]{1};
        
         //datatypes
        Datatype dtDouble = hdfFile.createDatatype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);
        
        Group groupSpectralModel = hdfFile.createGroup("spectral_model", root);

        if (dataset.getSpectralParameters() != null) {
            data_dim = new long[]{dataset.getSpectralParameters().length};
            hdfFile.createScalarDS("spec_params", groupSpectralModel, dtDouble, data_dim, data_dim, null, 0, dataset.getSpectralParameters());
        }
        
        if (dataset.getSpecdisppar() != null) {
            data_dim = new long[]{dataset.getSpecdisppar().length};
            hdfFile.createScalarDS("spec_params_time_model", groupSpectralModel, dtDouble, data_dim, data_dim, null, 0, dataset.getSpecdisppar());
        }
        
        if (dataset.getClpequ() != null) {
            data_dim = new long[]{dataset.getClpequ().length};
            hdfFile.createScalarDS("spec_coeffs_equality", groupSpectralModel, dtDouble, data_dim, data_dim, null, 0, dataset.getClpequ());
        }
        
        if (dataset.getCoh() != null) {
            data_dim = new long[]{dataset.getCoh().length};
            hdfFile.createScalarDS("cohspec_params", groupSpectralModel, dtDouble, data_dim, data_dim, null, 0, dataset.getCoh());
        }
        
        if (dataset.getOscpar() != null) {
            data_dim = new long[]{dataset.getOscpar().length};
            hdfFile.createScalarDS("oscspec_params", groupSpectralModel, dtDouble, data_dim, data_dim, null, 0, dataset.getOscpar());
        }
        
        if (dataset.getSpectra() != null) {
            //store calculated spectra data
            Matrix m = dataset.getSpectra();
            data_dim = new long[]{m.getColumnDimension(), m.getRowDimension()};
            hdfFile.createScalarDS("spectra_calculated", groupSpectralModel, dtDouble, data_dim, data_dim, null, 0, m);
        }

        if (dataset.getSpectraErr()!= null) {
            //store errors for the calculated spectra data
            Matrix m = dataset.getSpectraErr();
            data_dim = new long[]{m.getColumnDimension(), m.getRowDimension()};
            hdfFile.createScalarDS("errors", groupSpectralModel, dtDouble, data_dim, data_dim, null, 0, m);
        }
        
        value_dbl = new double[]{dataset.getRms()};
        attr = new Attribute("rms", dtDouble, single_dim, value_dbl);
        groupSpectralModel.writeMetadata(attr);
    }

    private static void saveKineticModel(H5File hdfFile, Group root, TimpResultDataset dataset) throws Exception {
        long[] data_dim;
        
         //datatypes
        Datatype dtDouble = hdfFile.createDatatype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);
        
        Group groupKineticModel = hdfFile.createGroup("kinetic_model", root);
        
        assert(dataset.getKineticParameters() != null);
        data_dim = new long[]{dataset.getKineticParameters().length};
        hdfFile.createScalarDS("kinetic_params", groupKineticModel, dtDouble, data_dim, data_dim, null, 0, dataset.getKineticParameters());
        
        if (dataset.getJvec() != null) {
            data_dim = new long[]{dataset.getJvec().length};
            hdfFile.createScalarDS("jvec", groupKineticModel, dtDouble, data_dim, data_dim, null, 0, dataset.getJvec());
        }
        
        if (dataset.getPrel() != null) {
            data_dim = new long[]{dataset.getPrel().length};
            hdfFile.createScalarDS("relations_params", groupKineticModel, dtDouble, data_dim, data_dim, null, 0, dataset.getPrel());
        }
        
        if (dataset.getKinscal() != null) {
            data_dim = new long[]{dataset.getKinscal().length};
            hdfFile.createScalarDS("branching_scaling_factors", groupKineticModel, dtDouble, data_dim, data_dim, null, 0, dataset.getKinscal());
        }
        
        if(dataset.getConcentrations() != null){
            Matrix m = dataset.getConcentrations();
            data_dim = new long[]{m.getColumnDimension(), m.getRowDimension()};
            hdfFile.createScalarDS("concentrations", groupKineticModel, dtDouble, data_dim, data_dim, null, 0, m);
        }
        
        if(dataset.getResiduals() != null){
            Matrix m = dataset.getResiduals();
            data_dim = new long[]{m.getColumnDimension(), m.getRowDimension()};
            hdfFile.createScalarDS("residuals", groupKineticModel, dtDouble, data_dim, data_dim, null, 0, m);
        }
        
        if(dataset.getFittedTraces() != null){
            Matrix m = dataset.getFittedTraces();
            data_dim = new long[]{m.getColumnDimension(), m.getRowDimension()};
            hdfFile.createScalarDS("spectra_fitted", groupKineticModel, dtDouble, data_dim, data_dim, null, 0, m);
        }
        
        if (dataset.getEigenvaluesK() != null) {
            data_dim = new long[]{dataset.getEigenvaluesK().length};
            hdfFile.createScalarDS("eigen_valuesK", groupKineticModel, dtDouble, data_dim, data_dim, null, 0, dataset.getEigenvaluesK());
        }
    }

    public static void save(File file, TimpResultDataset dataset) throws IOException {
        H5File hdfFile = Hdf5Provider.getHdf5File(file, true);

        if (hdfFile == null) {
            return;
        }

        try {
            long[] value_long;
            String[] value_str;
            Attribute attr;
            long[] single_dim = new long[]{1};

            //datatypes
            Datatype dtLong = hdfFile.createDatatype(Datatype.CLASS_INTEGER, 8, Datatype.NATIVE, Datatype.NATIVE);
            Datatype dtVarString = hdfFile.createDatatype(Datatype.CLASS_STRING, -1, Datatype.NATIVE, Datatype.SIGN_NONE);

            Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) hdfFile.getRootNode()).getUserObject();
            // create DatasetTimp group at the root, and 3 sub-groups: for spectra, FLIM image and instrument response function
            Group groupTimpResult = hdfFile.createGroup("TimpResult", root);

            saveSourceData(hdfFile, groupTimpResult, dataset);
            saveIRFModel(hdfFile, groupTimpResult, dataset);
            saveSpectralModel(hdfFile, groupTimpResult, dataset);
            saveKineticModel(hdfFile, groupTimpResult, dataset);

            //store dataset attributes (name, type etc)
            value_str = new String[]{dataset.getDatasetName()};
            attr = new Attribute("name", dtVarString, single_dim, value_str);
            groupTimpResult.writeMetadata(attr);
            value_str = new String[]{dataset.getType()};
            attr = new Attribute("type", dtVarString, single_dim, value_str);
            groupTimpResult.writeMetadata(attr);

            //Glotaran HDF5 version
            value_long = new long[]{Hdf5Provider.version};
            attr = new Attribute("version", dtLong, single_dim, value_long);
            groupTimpResult.writeMetadata(attr);
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
