package org.glotaran.labmonkeydataloader;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        String dir = "/home/jsg210/BF/projects/aursir/glotataran/2015512_172239/labmonkeyscript.yaml";
        Datafolder folder = new Datafolder(dir);        
        
        System.out.print(folder.getDataSets().get(0).Timepoints.toString());
        String[] paths = Datafolder.getDatasetPaths(dir.substring(0, dir.lastIndexOf(File.separator)));
        for (String path : paths) {
            System.out.print(path+"\n");
        }
        


    }





}
