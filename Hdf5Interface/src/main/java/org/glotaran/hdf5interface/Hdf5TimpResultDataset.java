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
    
    public static void save(File file, TimpResultDataset dataset) throws IOException {
        H5File hdfFile = Hdf5Provider.getHdf5File(file, true);

        if (hdfFile == null) {
            return;
        }

        try {
            long[] value_long;
            Attribute attr;
            long[] single_dim = new long[]{1};
            
            //datatypes
            Datatype dtLong = hdfFile.createDatatype(Datatype.CLASS_INTEGER, 8, Datatype.NATIVE, Datatype.NATIVE);
            
            Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) hdfFile.getRootNode()).getUserObject();
            // create DatasetTimp group at the root, and 3 sub-groups: for spectra, FLIM image and instrument response function
            Group groupTimpResult = hdfFile.createGroup("TimpResult", root);
            
            //Glotaran HDF5 version
            value_long = new long[]{ Hdf5Provider.version };
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
