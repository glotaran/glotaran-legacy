/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes;

import java.util.ArrayList;
import java.util.List;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.nodes.dataobjects.TimpDatasetDataObject;
import org.glotaran.core.main.project.TGProject;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author lsp
 */
public class TgdDataChildren extends Children.Keys {

//    private TgdDataObject obj;
    private final List<TimpDatasetDataObject> datasets = new ArrayList<TimpDatasetDataObject>();
    private FileObject cachefolder;
    private FileObject datasetfolder;
    private FileObject[] files;

    public TgdDataChildren(TgdDataObject obj) {
//        this.obj=obj;
        DataObject dObj = null;
        final TGProject proj = (TGProject) FileOwnerQuery.getOwner(obj.getPrimaryFile());
        cachefolder = proj.getCacheFolder(true);
        datasetfolder = cachefolder.getFileObject(obj.getTgd().getCacheFolderName());
        if (datasetfolder!=null) {
        files = datasetfolder.getChildren();
        for (FileObject file : files) {
            try {
                dObj = DataObject.find(file);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (dObj != null) {
                if (dObj instanceof TimpDatasetDataObject) {
                    datasets.add((TimpDatasetDataObject) dObj);
                }
            }
        }
        }
    }

    public void addObj(TimpDatasetDataObject objToAdd) {
        if (datasets != null) {
            datasets.add(objToAdd);
        }
        setKeys(datasets);
    }

    @Override
    protected void addNotify() {
        setKeys(datasets);
    }

    @Override
    protected Node[] createNodes(Object key) {
        TimpDatasetDataObject datasetObject = (TimpDatasetDataObject) key;
        //TimpDatasetNode tn = (TimpDatasetNode) datasetObject.getNodeDelegate();//new TimpDatasetNode(datasetObject);
        return new Node[]{datasetObject.getNodeDelegate()};
    }

    @Override
    public boolean remove(Node[] arg0) {
        for (int i = 0; i < arg0.length; i++) {
            TimpDatasetNode node = (TimpDatasetNode) arg0[i];
            datasets.remove(node.getObject());
            setKeys(datasets);
        }
        return true;
    }
}
