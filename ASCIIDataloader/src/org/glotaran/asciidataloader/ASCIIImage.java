package org.glotaran.asciidataloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;
import org.glotaran.core.interfaces.TGDatasetInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.FlimImageAbstract;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author lsp
 */
public class ASCIIImage implements TGDatasetInterface {

    @Override
    public String getExtention() {
        return "ascii";
    }

    @Override
    public String getFilterString() {
        return ".ascii formated TIMP files";
    }

    @Override
    public String getType(File file) throws FileNotFoundException {
        String loadedString;
        Scanner sc = new Scanner(file);
        try {
            loadedString = sc.nextLine();
            loadedString = sc.nextLine();
            loadedString = sc.nextLine();
            if (loadedString.contains("\t")) { //check for tabs instead of spaces
                loadedString = loadedString.trim().replaceAll("\t", " ");
            }
            if (loadedString.trim().equalsIgnoreCase("Time explicit") | loadedString.trim().equalsIgnoreCase("Wavelength explicit")) {
                return "spec";
            } else {
                if (loadedString.trim().equalsIgnoreCase("FLIM image")) {
                    return "FLIMascii";
                } else {
                    return "error";
                }

            }
        } catch (Exception e) {
            return "error";
        }
    }

    @Override
    public boolean Validator(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException {
        String ext = FileUtil.getExtension(file.getName());
        if (ext.equalsIgnoreCase(getExtention())) {
            String loadedString;
            Scanner sc = new Scanner(file);
            if (sc.hasNextLine()) { //workaround for binary img file
                loadedString = sc.nextLine();
                loadedString = sc.nextLine();
                loadedString = sc.nextLine();
                if (loadedString.contains("\t")) { //check for tabs instead of spaces
                    loadedString = loadedString.trim().replaceAll("\t", " ");
                }
                if (loadedString.trim().equalsIgnoreCase("Time explicit")
                        | loadedString.trim().equalsIgnoreCase("Wavelength explicit")
                        | loadedString.trim().equalsIgnoreCase("FLIM image")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public DatasetTimp loadFile(File file) throws FileNotFoundException {
        DatasetTimp dataset = new DatasetTimp();
        dataset.setDatasetName(file.getName());
        double maxInt = 0;
        double minInt = 0;
        int nt = 0;
        int nl = 0;
        double[] x;
        double[] x2;
        double[] psisim;
        double[] intim;
        Vector<Double> x2Vector = new Vector<Double>();
        Vector<Double> psisimVector = new Vector<Double>();
        Vector<Double> intFluoVector = new Vector<Double>();
        Vector<Double> row = new Vector<Double>();
        String loadedString;
        Scanner sc = new Scanner(file);
        loadedString = sc.nextLine();
        loadedString = sc.nextLine();
        loadedString = sc.nextLine();
        if (loadedString.contains("\t")) { //check for tabs instead of spaces
            loadedString = loadedString.trim().replaceAll("\t", " ");
        }
        if (loadedString.trim().equalsIgnoreCase("Time explicit")) {
            sc.skip(Pattern.compile(" *Intervalnr", Pattern.CASE_INSENSITIVE));
            nt = sc.nextInt();
            dataset.setNt(nt);
            x = new double[nt];
            for (int i = 0; i < nt; i++) {
                x[i] = Double.parseDouble(sc.next());
            }
            dataset.setX(x);
            int lc = 0;
            while (sc.hasNext() && (!sc.hasNext(Pattern.compile(" *Integrated", Pattern.CASE_INSENSITIVE)))) {
                try {
                    x2Vector.addElement(Double.parseDouble(sc.next()));
                    lc++;
                    for (int i = 0; i < nt; i++) {
                        row.addElement(Double.parseDouble(sc.next()));
                    }
                } catch (java.lang.NumberFormatException e) {
                    if (x2Vector.size() >= lc) {
                        x2Vector.remove(x2Vector.size() - 1);
                        row.removeAllElements();
                    }
                    sc.nextLine();
                    lc--;
                }
                psisimVector.addAll(row);
                row.removeAllElements();
            }

            nl = x2Vector.size();
            dataset.setNl(nl);

            if (sc.hasNext(Pattern.compile(" *Integrated", Pattern.CASE_INSENSITIVE))) {
                loadedString = sc.nextLine();
                loadedString = sc.nextLine();
                for (int i = 0; i < nt; i++) {
                    intFluoVector.addElement(Double.parseDouble(sc.next()));
                }
                intim = new double[nt];
                for (int i = 0; i < nt; i++) {
                    intim[i] = intFluoVector.elementAt(i);
                }
                dataset.setIntenceIm(intim);
            }
            x2 = new double[nl];
            psisim = new double[nt * nl];
            boolean invertedWaves = x2Vector.get(0) < x2Vector.get(1) ? false : true;
            invertedWaves = false;
            for (int j = 0; j < nl; j++) {
                for (int i = 0; i < nt; i++) {
                    if (invertedWaves) {
                        psisim[(nl - j - 1) * nt + i] = (Double) psisimVector.elementAt(j * nt + i);
                    } else {
                        psisim[j * nt + i] = (Double) psisimVector.elementAt(j * nt + i);
                    }
                    if (psisim[j * nt + i] > maxInt) {
                        maxInt = psisim[j * nt + i];
                    }
                    if (psisim[j * nt + i] < minInt) {
                        minInt = psisim[j * nt + i];
                    }
                }
                if (invertedWaves) {
                    x2[nl - j - 1] = (Double) x2Vector.elementAt(j);
                } else {
                    x2[j] = (Double) x2Vector.elementAt(j);
                }
            }
            dataset.setX2(x2);
            dataset.setPsisim(psisim);
            dataset.setMaxInt(maxInt);
            dataset.setMinInt(minInt);
        } else {
            if (loadedString.trim().equalsIgnoreCase("Wavelength explicit")) {
                sc.skip(Pattern.compile(" *Intervalnr", 2));
                nl = sc.nextInt();
                dataset.setNl(nl);
                x2 = new double[nl];
                for (int i = 0; i < nl; i++) {
                    x2[i] = Double.parseDouble(sc.next());
                }
                boolean invertedWaves = x2[0] < x2[1] ? false : true;
                invertedWaves = false;
                if (invertedWaves) {
                    double[] x2t = new double[nl];
                    for (int i = 0; i < nl; i++) {
                        x2t[nl - i - 1] = x2[i];
                    }
                    dataset.setX2(x2t);
                } else {
                    dataset.setX2(x2);
                }
                while (sc.hasNext() && (!sc.hasNext(Pattern.compile(" *Integrated", Pattern.CASE_INSENSITIVE)))) {
                    x2Vector.addElement(Double.parseDouble(sc.next()));
                    for (int i = 0; i < nl; i++) {
                        psisimVector.addElement(Double.parseDouble(sc.next()));
                    }
                }
                nt = x2Vector.size();
                dataset.setNt(nt);
                if (sc.hasNext(Pattern.compile(" *Integrated", Pattern.CASE_INSENSITIVE))) {
                    loadedString = sc.nextLine();
                    loadedString = sc.nextLine();
                    for (int i = 0; i < nt; i++) {
                        intFluoVector.addElement(Double.parseDouble(sc.next()));
                    }
                    intim = new double[nt];
                    for (int i = 0; i < nt; i++) {
                        intim[i] = intFluoVector.elementAt(i);
                    }
                    dataset.setIntenceIm(intim);
                }
                x = new double[nt];
                psisim = new double[nt * nl];
                for (int j = 0; j < nt; j++) {
                    for (int i = 0; i < nl; i++) {
                        if (invertedWaves) {
                            psisim[(nl - i - 1) * nt + j] = (Double) psisimVector.elementAt(j * nl + i);
                        } else {
                            psisim[i * nt + j] = (Double) psisimVector.elementAt(j * nl + i);
                        }
                        if (psisim[i * nt + j] > maxInt) {
                            maxInt = psisim[i * nt + j];
                        }
                        if (psisim[i * nt + j] < minInt) {
                            minInt = psisim[i * nt + j];
                        }
                    }
                    x[j] = x2Vector.elementAt(j);
                }
                dataset.setX(x);
                dataset.setPsisim(psisim);
                dataset.setMaxInt(maxInt);
                dataset.setMinInt(minInt);
            } else {
                if (loadedString.trim().equalsIgnoreCase("FLIM image")) {
                    System.out.println("flim");
//TODO implement loading FLIM ASCII file
                }
            }
        }
        return dataset;
    }

    @Override
    public FlimImageAbstract loadFlimFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
