/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.components;

/**
 *
 * @author jsg210
 */
import java.io.*;
import javax.swing.filechooser.*;

public class SingleRootFileSystemView extends FileSystemView {

    File root;
    File[] roots = new File[1];

    public SingleRootFileSystemView(File root) {
        super();
        this.root = root;
        roots[0] = root;
    }

    public File createNewFolder(File containingDir) {
        File folder = new File(containingDir, "New Folder");
        folder.mkdir();
        return folder;
    }

    @Override
    public File getDefaultDirectory() {
        return root;
    }

    @Override
    public File getHomeDirectory() {
        return root;
    }

    @Override
    public File[] getRoots() {
        return roots;
    }
}

