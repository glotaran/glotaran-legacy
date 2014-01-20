/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.analysis.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.glotaran.core.models.tgm.CohspecPanelModel;
import org.glotaran.core.models.tgm.IrfparPanelModel;
import org.glotaran.core.models.tgm.KMatrixPanelModel;
import org.glotaran.core.models.tgm.KinparPanelModel;
import org.glotaran.core.models.tgm.Tgm;
import org.glotaran.core.models.tgm.WeightPar;
import org.glotaran.core.models.tgm.WeightParPanelModel;

/**
 *
 * @author Katharine Mullen
 * @author Joris Snellenburg
 */
public class InitModel {

    private static String addToFixed = null;

// Public classes
    public static String parseModel(Tgm tgm) {
        //       addToFixed=null;
        String initModel = "initModel(";
        String tempStr = null;

        tempStr = get_mod_type(tgm);
        initModel = initModel.concat(tempStr + ",");

        tempStr = get_kinpar(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_kmatrix(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_irf(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_parmu(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

//       tempStr = get_dispmufun(tgm);
//       if (tempStr!=null)
//           initModel=initModel.concat(tempStr+",");
//       tempStr = get_partau(tgm);
//       if (tempStr!=null)
//           initModel=initModel.concat(tempStr+",");
//       tempStr = get_disptaufun(tgm);
//       if (tempStr!=null)
//           initModel=initModel.concat(tempStr+",");

        tempStr = get_positivepar(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_cohArtefact(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_clp0(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_clpequspec(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_weightpar(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_relations(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_fixed(tgm);
        if (tempStr != null) {
            initModel = initModel.concat(tempStr + ",");
        }

        tempStr = get_seqmod(tgm);
        initModel = initModel.concat(tempStr + ")");


        return initModel;
    }

    private static String get_cohArtefact(Tgm tgm) {
        String cohSpecStr = null;
        CohspecPanelModel cohspecPanel = tgm.getDat().getCohspecPanel();
        String typeCoh = cohspecPanel.getCohspec().getType();
        
        if (typeCoh != null) {
            cohSpecStr = "cohspec = list(type = \"" + typeCoh + "\"";
            if (typeCoh.equalsIgnoreCase("seq") || typeCoh.equalsIgnoreCase("mix")) {
                cohSpecStr = cohSpecStr + ",";
                ArrayList<Double> seqstart = (ArrayList<Double>) tgm.getDat().getCohspecPanel().getCohspec().getSeqstart();
                if (seqstart != null && !seqstart.isEmpty()) {
                    cohSpecStr = cohSpecStr + "start = c(";
                    for (int i = 0; i < seqstart.size(); i++) {
                        if (i > 0) {
                            cohSpecStr = cohSpecStr + ",";
                        }
                        cohSpecStr = cohSpecStr + seqstart.get(i);
                    }
                    cohSpecStr = cohSpecStr + ")";
                }
            }
            if (typeCoh.equalsIgnoreCase("freeirfdisp")) {
                //TODO: implement freeirfdisp etc.
                System.out.println("coh = c( " + cohspecPanel.getCoh() +")");
            }
        }
        if (cohSpecStr != null) {
            cohSpecStr = cohSpecStr + ")";
        }
        return cohSpecStr;
    }

    // Private classes
    public static String get_kmatrix(Tgm tgm) {
        String kMatrixCall = null;

        KMatrixPanelModel kMatrix = tgm.getDat().getKMatrixPanel();

        int matrixSize = kMatrix.getJVector().getVector().size();
        if (matrixSize > 0) {
            kMatrixCall = "kmat = array(c(";
            kMatrixCall = kMatrixCall.concat(String.valueOf(kMatrix.getKMatrix().getData().get(0).getRow().get(0)));
            for (int j = 1; j < matrixSize; j++) {
                kMatrixCall = kMatrixCall.concat("," + String.valueOf(kMatrix.getKMatrix().getData().get(j).getRow().get(0)));
            }
            for (int i = 1; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    kMatrixCall = kMatrixCall.concat("," + String.valueOf(kMatrix.getKMatrix().getData().get(j).getRow().get(i)));
                }
            }
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    kMatrixCall = kMatrixCall.concat("," + String.valueOf(kMatrix.getKMatrix().getData().get(j + matrixSize).getRow().get(i)));
                }
            }

            kMatrixCall = kMatrixCall.concat("), dim = c(");
            kMatrixCall = kMatrixCall.concat(String.valueOf(matrixSize) + "," + String.valueOf(matrixSize) + ",2))");
            kMatrixCall = kMatrixCall.concat(", jvec = c(");
            //TODO: verify jVector
            kMatrixCall = kMatrixCall.concat(String.valueOf(kMatrix.getJVector().getVector().get(0)));
            for (int j = 1; j < matrixSize; j++) {
                kMatrixCall = kMatrixCall.concat("," + String.valueOf(kMatrix.getJVector().getVector().get(j)));
            }
            kMatrixCall = kMatrixCall.concat(")");

            int size = kMatrix.getKinScal().size();
            if (size > 0) {
                kMatrixCall = kMatrixCall.concat(", kinscal = c(");
                double k;
                for (int i = 0; i < size; i++) {
                    if (i > 0) {
                        kMatrixCall = kMatrixCall + ",";
                    }
                    k = kMatrix.getKinScal().get(i).getStart();
                    kMatrixCall = kMatrixCall + Double.toString(k);
                }
                kMatrixCall = kMatrixCall + ")";
            }
        }
        return kMatrixCall;
    }

    public static String get_kinpar(Tgm tgm) {
        KinparPanelModel kinparPanelModel = tgm.getDat().getKinparPanel();
        String kinpar = null;
        int size = kinparPanelModel.getKinpar().size();
        if (size > 0) {
            kinpar = "kinpar = c(";
            double k;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    kinpar = kinpar + ",";
                }
                k = kinparPanelModel.getKinpar().get(i).getStart();
                kinpar = kinpar + Double.toString(k);
            }
            kinpar = kinpar + ")";
        }
        return kinpar;
    }

    public static String get_weightpar(Tgm tgm) {
        WeightParPanelModel weightParPanelModel = tgm.getDat().getWeightParPanel();
        String weightpar = null;

        int size = weightParPanelModel.getWeightpar().size();
        if (size > 0) {
            weightpar = "weightpar = list(";
            for (int i = 0; i < size; i++) {
                if ((i > 0) && (i < size)) {
                    weightpar = weightpar + ",";
                }
                weightpar = weightpar + "c(";
                WeightPar wp = weightParPanelModel.getWeightpar().get(i);
                Double[] temparray = {wp.getMin1(), wp.getMax1(), wp.getMin2(), wp.getMax2(), wp.getWeight()};
                for (int j = 0; j < temparray.length; j++) {
                    if (temparray[j] != null && !temparray[j].isNaN()) {
                        weightpar = weightpar + String.valueOf(temparray[j]);
                    } else {
                        weightpar = weightpar + "NA";
                    }
                    if (j < temparray.length - 1) {
                        weightpar = weightpar + ",";
                    }
                }
                weightpar = weightpar + ")";
            }
        }
        if (weightParPanelModel.isPoisson() != null && weightParPanelModel.isPoisson()) {
            if (weightpar == null) {
                weightpar = "";
            }
            if (weightpar.isEmpty()) {
                weightpar = "weightpar = list(poisson = TRUE";
            } else {
                weightpar = weightpar + ", poisson = TRUE";
            }
        }
        if (weightpar != null) {
            if (!weightpar.isEmpty()) {
                weightpar = weightpar + ")";
            }
        }
        return weightpar;
    }

    private static String get_measured_irf(Tgm tgm) {
        String meaIrfString = null;
        //System.out.println("DDD"+Current.GetcurrMIRF());
        IrfparPanelModel irfparPanel = tgm.getDat().getIrfparPanel();
        if (irfparPanel.isMirf()) {
            int conv = irfparPanel.getConvalg();
            meaIrfString = "measured_irf= c( " + tgm.getDat().getIrfparPanel().getMeasuredIrf();
            meaIrfString = meaIrfString + " ), convalg= ";
            meaIrfString = meaIrfString.concat(String.valueOf(conv));
            if (conv == 3) {
                meaIrfString = meaIrfString.concat(", reftau = ");
                meaIrfString = meaIrfString.concat(String.valueOf(irfparPanel.getReftau()));
            }
        }
        return meaIrfString;
    }

    private static String get_irf(Tgm tgm) {
        IrfparPanelModel irfparPanel = tgm.getDat().getIrfparPanel();
        String irfStr = null;
        if (irfparPanel.isMirf() != null && irfparPanel.isMirf()) {
            irfStr = get_measured_irf(tgm);
        } else {
            IrfparPanelModel irfPanel = tgm.getDat().getIrfparPanel();
            int count = 0;
            if (irfPanel.getIrf().size() > 0) {
                if (irfPanel.getIrf().size()==2){
                        irfStr =  "irffun = \"gaus\",";
                } else {
                    if (irfPanel.getIrf().size()==4){
                        irfStr =  "irffun = \"doublegaus\",";
                    } else {
                        irfStr =  "irffun = \"multiplegaus\",";
                    }
                }
                    
                irfStr = irfStr + "irfpar = ";
                for (int i = 0; i < irfPanel.getIrf().size(); i++) {
                    if (count > 0) {
                        irfStr = irfStr + ",";
                    } else {
                        irfStr = irfStr + "c(";
                    }
                    irfStr = irfStr + Double.toString(irfPanel.getIrf().get(i));
                    count++;
                }
                irfStr = irfStr + ")";

                if (irfPanel.getIrf().size() == 4) {
                    irfStr = irfStr + ", doublegaus = TRUE";
                }
                if (irfPanel.isBacksweepEnabled() != null && irfPanel.isBacksweepEnabled()) {
                    irfStr = irfStr + ", streak = TRUE, streakT = " + String.valueOf(irfPanel.getBacksweepPeriod());
                }
            }
        }
        return irfStr;
    }
//
//   private static String get_dispmufun(Tgm tgm) {
//       String dispStr = null;
//       IrfparPanelModel irfPanel = tgm.getDat().getIrfparPanel();
//
//       if(irfPanel.getDispmufun().equals("poly")) {
//            dispStr = "dispmufun = \"poly\"";
//       }
//       else if (irfPanel.getDispmufun().equals("discrete")) {
//            dispStr = "dispmufun = \"discrete\"";
//       }
//
//       return dispStr;
//   }
//
//   private static String get_disptaufun(Tgm tgm) {
//       String dispStr = null;
//       IrfparPanelModel irfPanel = tgm.getDat().getIrfparPanel();
//
//       if(irfPanel.getDispmufun().equals("poly")) {
//            dispStr = "disptaufun = \"poly\"";
//       }
//       else if (irfPanel.getDispmufun().equals("discrete")) {
//            dispStr = "disptaufun = \"discrete\"";
//       }
//       return dispStr;
//   }

    private static String get_parmu(Tgm tgm) {
        String parmuStr = null;
        IrfparPanelModel irfPanel = tgm.getDat().getIrfparPanel();

        if (irfPanel.getParmu() != null) {
            if (irfPanel.getParmu().trim().length() != 0) {
                if (irfPanel.getLamda() != null) {
                    if (irfPanel.getLamda() > 0) {
                        parmuStr = "lambdac = " + String.valueOf(irfPanel.getLamda());
                    }
                }
                if (parmuStr == null) {
                    parmuStr = "parmu = list(";
                } else {
                    parmuStr = parmuStr.concat(", parmu = list(");
                }

                parmuStr = parmuStr + "c(" + irfPanel.getParmu() + "))";
            }
            if (irfPanel.getDispmufun() != null) {
                if (parmuStr != null) {
                    if (irfPanel.getDispmufun().equals("poly")) {
                        parmuStr = parmuStr.concat(", dispmufun = \"poly\"");
                    } else if (irfPanel.getDispmufun().equals("discrete")) {
                        parmuStr = parmuStr.concat(", dispmufun = \"discrete\"");
                    }
                } else {
                    if (irfPanel.getDispmufun().equals("poly")) {
                        parmuStr = "dispmufun = \"poly\"";
                    } else if (irfPanel.getDispmufun().equals("discrete")) {
                        parmuStr = "dispmufun = \"discrete\"";
                    }
                }
            }
        }
        if (irfPanel.getPartau() != null) {
            if (irfPanel.getPartau().trim().length() != 0) {
                if (irfPanel.getLamda() != null) {
                    if (irfPanel.getLamda() > 0) {
                        parmuStr = "lambdac = " + String.valueOf(irfPanel.getLamda());
                    }
                }
                if (parmuStr == null) {
                    parmuStr = "partau= list(";
                } else {
                    parmuStr = parmuStr.concat(", partau= list(");
                }

                parmuStr = parmuStr + "c(" + irfPanel.getPartau() + "))";
            }

            if (irfPanel.getDisptaufun() != null && parmuStr != null) {
                if (irfPanel.getDisptaufun().equals("poly")) {
                    parmuStr = parmuStr.concat(", disptaufun = \"poly\"");
                } else if (irfPanel.getDisptaufun().equals("discrete")) {
                    parmuStr = parmuStr.concat(", disptaufun = \"discrete\"");
                }
            } else {
                if (irfPanel.getDisptaufun().equals("poly")) {
                    parmuStr = "disptaufun = \"poly\"";
                } else if (irfPanel.getDisptaufun().equals("discrete")) {
                    parmuStr = "disptaufun = \"discrete\"";
                }
            }
        }
        return parmuStr;
    }

    private static String get_fixed(Tgm tgm) {
        String fixedStr = null;
        KinparPanelModel kinparPanelModel = tgm.getDat().getKinparPanel();
        int count = 0;
        for (int i = 0; i < kinparPanelModel.getKinpar().size(); i++) {
            if (kinparPanelModel.getKinpar().get(i).isFixed()) {
                if (count > 0) {
                    fixedStr = fixedStr + ",";
                } else {
                    fixedStr = "fixed = list(kinpar=c(";
                }
                fixedStr = fixedStr + String.valueOf(i + 1);
                count++;
            }
        }
        if (count > 0) {
            fixedStr = fixedStr + ")";
        }

        count = 0;

        KMatrixPanelModel kmatPanel = tgm.getDat().getKMatrixPanel();
        for (int i = 0; i < kmatPanel.getKinScal().size(); i++) {
            if (kmatPanel.getKinScal().get(i).isFixed()) {
                if (count > 0) {
                    fixedStr = fixedStr + ",";
                } else {
                    if (fixedStr != null) {
                        fixedStr = fixedStr + ", kinscal=c(";
                    } else {
                        fixedStr = "fixed = list(kinscal=c(";
                    }
                }
                fixedStr = fixedStr + String.valueOf(i + 1);
                count++;
            }
        }
        if (count > 0) {
            fixedStr = fixedStr + ")";
        }

        count = 0;
        IrfparPanelModel irfPanel = tgm.getDat().getIrfparPanel();
        for (int i = 0; i < irfPanel.getFixed().size(); i++) {
            if (irfPanel.getFixed().get(i)) {
                if (count > 0) {
                    fixedStr = fixedStr + ",";
                } else {
                    if (fixedStr != null) {
                        fixedStr = fixedStr + ", irfpar=c(";
                    } else {
                        fixedStr = "fixed = list(irfpar=c(";
                    }
                }
                fixedStr = fixedStr + String.valueOf(i + 1);
                count++;
            }
        }
        if (count > 0) {
            fixedStr = fixedStr + ")";
        }

        if (irfPanel.getParmu() != null) {
            if (irfPanel.isParmufixed() != null && irfPanel.isParmufixed()) {
                if (fixedStr != null) {
                    fixedStr = fixedStr + ", parmu=c(";
                } else {
                    fixedStr = "fixed = list(parmu=c(";
                }

                String[] doubles = irfPanel.getParmu().split(",");
                fixedStr = fixedStr + "1:" + String.valueOf(doubles.length);
                fixedStr = fixedStr + ")";
            }
        }

        if (irfPanel.getPartau() != null) {
            if (irfPanel.isPartaufixed()) {
                if (fixedStr != null) {
                    fixedStr = fixedStr + ", partau=c(";
                } else {
                    fixedStr = "fixed = list(partau=c(";
                }

                String[] doubles = irfPanel.getPartau().split(",");
                fixedStr = fixedStr + "1:" + String.valueOf(doubles.length);
                fixedStr = fixedStr + ")";
            }
        }

        count = 0;
        KMatrixPanelModel kMatrix = tgm.getDat().getKMatrixPanel();
        for (int i = 0; i < kMatrix.getJVector().getFixed().size(); i++) {
            if (kMatrix.getJVector().getFixed().get(i)) {
                if (count > 0) {
                    fixedStr = fixedStr + ",";
                } else {
                    if (fixedStr != null) {
                        fixedStr = fixedStr + ", jvec=c(";
                    } else {
                        fixedStr = "fixed = list(jvec=c(";
                    }
                }
                fixedStr = fixedStr + String.valueOf(i + 1);
                count++;
            }
        }
        if (count > 0) {
            fixedStr = fixedStr + ")";
        }
        // TODO: add additional paramters for fixed here:

        // This closes the "fixed" argument


        if (fixedStr != null) {
            if (addToFixed != null) {
                fixedStr = fixedStr + "," + addToFixed + ")";
            } else {
                fixedStr = fixedStr + ")";
            }
        } else {
            if (addToFixed != null) {
                fixedStr = "fixed = list(" + addToFixed + ")";
            }

        }

        return fixedStr;
    }

//   private static String get_constrained(Tgm tgm) {
//       Dat dat = tgm.getDat();
//       KinparPanelModel kinparPanelModel = dat.getKinparPanel();
//       String constrained = "constrained = list(";
//       double cc;
//       int count=0;
//       for (int i = 0; i < kinparPanelModel.getKinpar().size(); i++) {
//           if(kinparPanelModel.getKinpar().get(i).isConstrained()){
//               if(kinparPanelModel.getKinpar().get(i).getMin() != null) {
//                   if(count > 0)
//                       constrained = constrained + ",";
//                   cc = kinparPanelModel.getKinpar().get(i).getMin();
//                   constrained = constrained + "list(what=\"kinpar\", ind = "+
//                                  Integer.toString(i+1) +", low=" + cc + ")";
//                   count++;
//               }
//               else if(kinparPanelModel.getKinpar().get(i).getMax() != null) {
//                    if(count > 0)
//                       constrained = constrained + ",";
//                    cc = kinparPanelModel.getKinpar().get(i).getMax();
//                    constrained = constrained + "list(what=\"kinpar\", ind = "+
//                                  Integer.toString(i+1) +", high=" + cc + ")";
//                    count++;
//               }
//           }
//       }
//       constrained = constrained + ")";
//        // need to fill in other parameters here, once we have panels for them
//       return constrained;
//   }
    private static String get_seqmod(Tgm tgm) {
        KinparPanelModel kinparPanelModel = tgm.getDat().getKinparPanel();
        if (kinparPanelModel.isSeqmod()) {
            return "seqmod = TRUE";
        } else {
            return "seqmod = FALSE";
        }
    }

    private static String get_positivepar(Tgm tgm) {
        KinparPanelModel kinparPanelModel = tgm.getDat().getKinparPanel();
        int count = 0;
        String positivepar = null;
        if (kinparPanelModel.isPositivepar()) {
            count++;
            positivepar = "positivepar = c(\"kinpar\"";
        }
        // need to fill in other parameters here, once we have panels for them
        //if(count>0)
        //        positivepar = positivepar + ",";
        if (count < 1) {
            positivepar = "positivepar=vector()";
        } else {
            positivepar = positivepar + ")";
        }

        return positivepar;
    }

    private static String get_clp0(Tgm tgm) {
        String clp0Call = null;
        int indexOfComponent = 0;
        KMatrixPanelModel kMatrix = tgm.getDat().getKMatrixPanel();
        int size = kMatrix.getSpectralContraints().getMax().size();
        if (size > 0) {
            int count = 0;
            for (int i = 0; i < size; i++) {
                Double min = kMatrix.getSpectralContraints().getMin().get(i);
                Double max = kMatrix.getSpectralContraints().getMax().get(i);
                if (min != null && max != null) {
                    if (count == 0) {
                        clp0Call = "clp0 = list(";
                    } else {
                        clp0Call = clp0Call + ",";
                    }

                    clp0Call = clp0Call + "list(low = " + Double.valueOf(min) + ", high ="
                            + Double.valueOf(max) + ", comp = " + (i + 1) + ")";
                    count++;
                }
            }
        }
        if (tgm.getDat().getCohspecPanel().isClp0Enabled() != null) {
            if (tgm.getDat().getCohspecPanel().isClp0Enabled()) {
                if (clp0Call != null) {
                    clp0Call = clp0Call + ",";
                } else {
                    clp0Call = "clp0 = list(";
                }
                if (kMatrix.getJVector() != null) {
                    if (kMatrix.getJVector().getVector().size() > 0) {
                        indexOfComponent = kMatrix.getJVector().getVector().size() + 1;
                    } else {
                        indexOfComponent = (tgm.getDat().getKinparPanel().getKinpar().size() + 1);
                    }
                } else {
                    indexOfComponent = (tgm.getDat().getKinparPanel().getKinpar().size() + 1);
                }
                clp0Call = clp0Call + "list(low = " + tgm.getDat().getCohspecPanel().getClp0Min() + ", high = "
                        + tgm.getDat().getCohspecPanel().getClp0Max() + ", comp = "
                        + indexOfComponent + ")";
            }
        }
        if (clp0Call != null) {
            clp0Call = clp0Call + ")";
        }
        return clp0Call;
    }

    private static String get_mod_type(Tgm tgm) {
        addToFixed = null;
        String mod_type = "mod_type = \"" + tgm.getDat().getModType() + "\"";
        return mod_type;
    }

    private static String get_clpequspec(Tgm tgm) {
        String clpequspecCall = null;
        String clpequCall = null;
        int count = 0;
        KMatrixPanelModel kMatrixPanel = tgm.getDat().getKMatrixPanel();
        int size = kMatrixPanel.getContrainsMatrix().getData().size();
        for (int i = 0; i < size; i++) { // to
            for (int j = 0; j < size; j++) { // from
                Double min = kMatrixPanel.getContrainsMatrix().getData().get(i).getMin().get(j);
                Double max = kMatrixPanel.getContrainsMatrix().getData().get(i).getMax().get(j);
                Double scal;
                if (!kMatrixPanel.getContrainsMatrix().getData().get(i).getScal().isEmpty()) {
                    scal = kMatrixPanel.getContrainsMatrix().getData().get(i).getScal().get(j);
                } else {
                    scal = 1.0;
                }
                if (min != null && max != null) {
                    if (count == 0) {
                        clpequspecCall = "clpequspec = list(";
                        clpequCall = "clpequ = c(";
                    }
                    clpequspecCall = clpequspecCall + "list("
                            + "to=" + String.valueOf(i + 1)
                            + ",from=" + String.valueOf(j + 1)
                            + ",low=" + String.valueOf(min)
                            + ",high=" + String.valueOf(max) + "),";
                    if (scal != null) {
                        clpequCall = clpequCall + String.valueOf(scal) + ',';
                    } else {
                        clpequCall = clpequCall + "1,";
                    }
                    count++;
                }
            }
        }
        // Removes last comma:

        if (count > 0) {
            clpequspecCall = clpequspecCall.substring(0, clpequspecCall.length() - 1);
            clpequCall = clpequCall.substring(0, clpequCall.length() - 1);
            clpequspecCall = clpequspecCall + ")";
            clpequCall = clpequCall + ")";
            clpequspecCall = clpequspecCall + "," + clpequCall;
        }

        if (addToFixed == null) {
            addToFixed = "clpequ=1:" + count;
        } else {
            addToFixed = addToFixed + ", clpequ=1:" + count;
        }
        return clpequspecCall;
    }

    private static String get_relations(Tgm tgm) {
        String relationsCall = null;
        String fixed = null;
        KMatrixPanelModel kMatrixPanel = tgm.getDat().getKMatrixPanel();
        int size = kMatrixPanel.getRelationsMatrix().getData().size();
//prelspec = list(list(what1="kinpar", what2="kinpar",ind1=6,ind2=1, start=c(0.05,0))),
        Double c0, c1;
        Boolean fc0, fc1;
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                c0 = kMatrixPanel.getRelationsMatrix().getData().get(i).getC0().get(j);
                c1 = kMatrixPanel.getRelationsMatrix().getData().get(i).getC1().get(j);
                fc0 = kMatrixPanel.getRelationsMatrix().getData().get(i).getC0Fixed().get(j);
                fc1 = kMatrixPanel.getRelationsMatrix().getData().get(i).getC1Fixed().get(j);
                if ((!Double.isNaN(c0)) && (!Double.isNaN(c1))) {
                    if (count == 0) {
                        relationsCall = "prelspec = list(";
                    } else {
                        relationsCall = relationsCall + ",";
                    }
                    relationsCall = relationsCall + "list(what1=\"kinpar\", what2=\"kinpar\",ind1=" + (i + 1)
                            + ", ind2=" + (j + 1) + ", start=c(" + c1 + "," + c0 + "))";

                    if (fc1) {
                        if (fixed == null) {
                            fixed = "prel = c(";
                        } else if (count != 0) {
                            fixed = fixed + ",";
                        }
                        fixed = fixed + (2 * count + 1);
                    }

                    if (fc0) {
                        if (fixed == null) {
                            fixed = "prel = c(";
                        } else {
                            if ((count != 0) || ((count == 0) && (fc0))) {
                                fixed = fixed + ",";
                            }
                        }

                        fixed = fixed + (2 * count + 2);
                    }
                    count++;
                }
            }
        }
        if (relationsCall != null) {
            relationsCall = relationsCall + ")";
        }
        if (fixed != null) {
            fixed = fixed + ")";
            if (addToFixed != null) {
                addToFixed = addToFixed + "," + fixed;
            } else {
                addToFixed = fixed;
            }
        }
        return relationsCall;
    }
}
