/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.main.nodes.actions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.glotaran.core.main.nodes.dataobjects.TgdDataObject;
import org.glotaran.core.main.project.TGProject;
import org.glotaran.core.messages.CoreErrorMessages;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

public final class CacheTgdDataset implements ActionListener {

    private final List<DataObject> context;

    public CacheTgdDataset(List<DataObject> context) {
        this.context = context;        
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (DataObject dataObject : context) {
            // TODO use dataObject
            if (dataObject instanceof TgdDataObject) {
                TgdDataObject tgdDataObject = ((TgdDataObject) dataObject);
                TGProject project = (TGProject) FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
                File relPath = new File(project.getProjectDirectory() + File.separator + tgdDataObject.getTgd().getRelativePath());
                if (relPath.exists()) {
                    FileObject datasetFileObject = FileUtil.toFileObject(relPath);
                    if (datasetFileObject.canRead()) {
                        if (FileOwnerQuery.getOwner(datasetFileObject).equals(project)) {
                            return;
                        } else {
                            FileObject cacheFolder = FileUtil.toFileObject(new File(project.getCacheFolder(true).getPath() + File.separator + tgdDataObject.getTgd().getCacheFolderName()));
                            try {
                                //TODO: fix the new extension workaround
                                FileObject copy = FileUtil.copyFile(datasetFileObject, cacheFolder, tgdDataObject.getTgd().getFilename(), tgdDataObject.getTgd().getExtension());
                                tgdDataObject.getTgd().setRelativePath(FileUtil.getRelativePath(project.getProjectDirectory(), copy));
                                tgdDataObject.save();
                                //return;
                            } catch (IOException ex) {
                                CoreErrorMessages.fileNotFound();
                                //Exceptions.printStackTrace(ex);
                            }

                        }
                    }
                }
                File absPath = new File(tgdDataObject.getTgd().getPath());
                if (absPath.exists()) {
                    FileObject datasetFileObject = FileUtil.toFileObject(absPath);
                    if (datasetFileObject.canRead()) {
                        FileObject cacheFolder = FileUtil.toFileObject(new File(project.getCacheFolder(true).getPath() + File.separator + tgdDataObject.getTgd().getCacheFolderName()));
                        try {
                            //TODO: fix the new extension workaround
                            FileObject copy = FileUtil.copyFile(datasetFileObject, cacheFolder, tgdDataObject.getTgd().getFilename(), tgdDataObject.getTgd().getExtension());
                            tgdDataObject.getTgd().setRelativePath(FileUtil.getRelativePath(project.getProjectDirectory(), copy));
                            tgdDataObject.save();
                            //return;
                        } catch (IOException ex) {
                            CoreErrorMessages.fileNotFound();
                            //Exceptions.printStackTrace(ex);
                        }
                    }
                } 
            }
        } 
    }
}
