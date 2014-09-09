/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.models.structures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 *
 * @author lsp
 */
public class FormattedASCIIFileLoader {

    public static DatasetTimp loadASCIIFile(File file) throws FileNotFoundException, IOException, IllegalAccessException, InstantiationException {
        DatasetTimp dataset = new DatasetTimp();
        dataset.setDatasetName(file.getName());
        double maxInt = 0;
        double minInt = 0;
        int nt = 0;
        int nl = 0;
        double[] x;
        double[] x2;
        double[] psisim;
        Vector x2Vector = new Vector();
        Vector psisimVector = new Vector();
        String loadedString;
        Scanner sc = new Scanner(file);
        loadedString = sc.nextLine();
        loadedString = sc.nextLine();
        loadedString = sc.nextLine();
        if (loadedString.trim().equalsIgnoreCase("Time explicit")) {
            sc.skip(Pattern.compile(" *Intervalnr", Pattern.CASE_INSENSITIVE));
            nt = sc.nextInt();
            dataset.setNt(nt);
            x = new double[nt];
            for (int i = 0; i < nt; i++) {
                x[i] = Double.parseDouble(sc.next());
            }
            dataset.setX(x);
            while (sc.hasNext()) {
                x2Vector.addElement(Double.parseDouble(sc.next()));
                for (int i = 0; i < nt; i++) {
                    psisimVector.addElement(Double.parseDouble(sc.next()));
                }
            }

            nl = x2Vector.size();
            dataset.setNl(nl);
            x2 = new double[nl];
            psisim = new double[nt * nl];

            for (int j = 0; j < nl; j++) {
                for (int i = 0; i < nt; i++) {
                    psisim[j * nt + i] = (Double) psisimVector.elementAt(j * nt + i);
                    if (psisim[j * nt + i] > maxInt) {
                        maxInt = psisim[j * nt + i];
                    }
                    if (psisim[j * nt + i] < minInt) {
                        minInt = psisim[j * nt + i];
                    }
                }
                x2[j] = (Double) x2Vector.elementAt(j);
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
                dataset.setX2(x2);
                while (sc.hasNext()) {
                    x2Vector.addElement(Double.parseDouble(sc.next()));
                    for (int i = 0; i < nl; i++) {
                        psisimVector.addElement(Double.parseDouble(sc.next()));

                    }
                }
                nt = x2Vector.size();
                dataset.setNt(nt);
                x = new double[nt];
                psisim = new double[nt * nl];

                for (int j = 0; j < nt; j++) {
                    for (int i = 0; i < nl; i++) {
                        psisim[i * nt + j] = (Double) psisimVector.elementAt(j * nl + i);
                        if (psisim[i * nt + j] > maxInt) {
                            maxInt = psisim[i * nt + j];
                        }
                        if (psisim[i * nt + j] < minInt) {
                            minInt = psisim[i * nt + j];
                        }
                    }
                    x[j] = (Double) x2Vector.elementAt(j);
                }
                dataset.setX(x);
                dataset.setPsisim(psisim);
                dataset.setMaxInt(maxInt);
                dataset.setMinInt(minInt);
            } else {
                if (loadedString.trim().equalsIgnoreCase("FLIM image")) {
                    System.out.println("flim");
//TODO implement loading FLIM ASCII file
                } else {
                    throw new IllegalAccessException();
                }
            }
        }
        return dataset;
    }
}
