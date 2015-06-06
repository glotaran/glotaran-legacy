package org.glotaran.labmonkeydataloader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joern on 6/4/2015.
 */
public class Datafolder {
    private String scriptdir;

    Datafolder(String dir) {
        scriptdir = dir.substring(0, dir.lastIndexOf(File.separator));

    }

    public List<Dataset> getDataSets(){
        List<Dataset> datasets = new ArrayList<>();
        String[] dirs = getDatasetPaths(scriptdir);
        for (String dir:dirs){
            datasets.add(new Dataset(dir));
        }
        return datasets;
    }

    public static String[] getDatasetPaths(String scriptpath) {

        File file = new File(scriptpath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return name.contains("run") && new File(current, name).isDirectory();
            }
        });
        for(int i = 0; i < directories.length; i++) {
            directories[i]=scriptpath+File.separator+directories[i];
        }
        return directories;
    }
}
