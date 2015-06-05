package org.glotaran.labmonkeydataloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joern on 6/4/2015.
 */
public class Dataset {
    public List Timepoints = new ArrayList<>();
    public List<double[]> Timetrace = new ArrayList<>();

    Dataset(String folder){
        String[] dirs = getDatasetPaths(folder);
        for (String dir : dirs) {
            loadDir(dir);
        }
    }

    private String[] getDatasetPaths(String dir) {

        File file = new File(dir);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return name.contains("step") && new File(current, name).isDirectory();
            }
        });
        for(int i = 0; i < directories.length; i++) {
            directories[i]=dir+File.separator+directories[i];
        }
        return directories;
    }

    private void loadDir(String dir) {
        dir += File.separator;
        dir += "stepdata.json";
        String jsonData = readFile(dir);
        JSONObject jobj = new JSONObject(jsonData);
        JSONArray jarr = jobj.getJSONArray("Data");
        double tp = 0;
        for(int i = 0; i < jarr.length(); i++) {
            JSONObject data = jarr.getJSONObject(i);
            switch (data.get("Function").toString()) {
                case "AcquireDeltaOd":
                    JSONArray res = data.getJSONObject("Result").getJSONArray("DeltaOd").getJSONArray(0);
                    double[] resdata = new double[res.length()];
                    for(int j = 0; j < res.length(); j++) {
                        resdata[j] = res.getDouble(j);
                    }
                    Timetrace.add(resdata);
                    break;
               case "SetRelativePositionPSec":
                   tp += data.getJSONObject("Result").getDouble("Position");
                    break;
               case "SetDelayInteger":
                   tp += data.getJSONObject("Result").getInt("DelayInteger")*1.0/84960000 * 1e12;
                   break;

            }
        }
        Timepoints.add(tp);
    }

    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
