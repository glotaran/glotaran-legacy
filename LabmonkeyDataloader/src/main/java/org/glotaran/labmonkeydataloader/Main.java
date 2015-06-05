package org.glotaran.labmonkeydataloader;

public class Main {

    public static void main(String[] args) {

        String dir = "S:\\experimentdata\\2015512\\2015512_172239\\labmonkeyscript.yaml";
        Datafolder folder = new Datafolder(dir);
        System.out.print(folder.getDataSets().get(0).Timepoints.toString());


    }





}
