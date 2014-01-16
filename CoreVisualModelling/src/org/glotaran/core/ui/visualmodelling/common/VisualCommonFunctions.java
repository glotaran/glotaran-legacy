/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.common;

import java.util.List;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.glotaran.core.models.tgm.Cohspec;
import org.glotaran.core.models.tgm.CohspecPanelModel;
import org.glotaran.core.models.tgm.Dat;
import org.glotaran.core.models.tgm.Double2BoolMatrix;
import org.glotaran.core.models.tgm.Double2Matrix;
import org.glotaran.core.models.tgm.IntMatrix;
import org.glotaran.core.models.tgm.IrfparPanelModel;
import org.glotaran.core.models.tgm.JVector;
import org.glotaran.core.models.tgm.KMatrixPanelModel;
import org.glotaran.core.models.tgm.KinPar;
import org.glotaran.core.models.tgm.KinparPanelModel;
import org.glotaran.core.models.tgm.SpectralConstraints;
import org.glotaran.core.models.tgm.Tgm;
import org.glotaran.core.models.tgm.WeightPar;
import org.glotaran.core.models.tgm.WeightParPanelModel;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes.CohSpecTypes;
import org.glotaran.core.ui.visualmodelling.common.EnumTypes.IRFTypes;
import org.glotaran.core.ui.visualmodelling.nodes.CohSpecNode;
import org.glotaran.core.ui.visualmodelling.nodes.DispersionModelingNode;
import org.glotaran.core.ui.visualmodelling.nodes.IrfParametersNode;
import org.glotaran.core.ui.visualmodelling.nodes.KineticParametersNode;
import org.glotaran.core.ui.visualmodelling.nodes.KmatrixNode;
import org.glotaran.core.ui.visualmodelling.nodes.ParametersSubNode;
import org.glotaran.core.ui.visualmodelling.nodes.WeightParametersNode;
import org.glotaran.core.ui.visualmodelling.palette.PaletteItem;
import org.openide.nodes.Children;
import static java.lang.Math.floor;

/**
 *
 * @author slapten
 */
public class VisualCommonFunctions {

    public static void createNewTgmFile(File file, String type) {
        Tgm tgm = new Tgm();
        tgm.setDat(new Dat());
        tgm.getDat().setModType(type);
        tgm.getDat().setModelName(file.getName());
        tgm.getDat().setCohspecPanel(new CohspecPanelModel());
        tgm.getDat().setIrfparPanel(new IrfparPanelModel());
        tgm.getDat().setKMatrixPanel(new KMatrixPanelModel());
        tgm.getDat().setKinparPanel(new KinparPanelModel());
        tgm.getDat().setWeightParPanel(new WeightParPanelModel());
        tgm.getDat().getKMatrixPanel().setJVector(new JVector());
        tgm.getDat().getKMatrixPanel().setContrainsMatrix(new Double2Matrix());
        tgm.getDat().getKMatrixPanel().setKMatrix(new IntMatrix());
        tgm.getDat().getKMatrixPanel().setRelationsMatrix(new Double2BoolMatrix());
        tgm.getDat().getKMatrixPanel().setSpectralContraints(new SpectralConstraints());
        tgm.getDat().getIrfparPanel().setParmu(null);
        tgm.getDat().getIrfparPanel().setPartau(null);
        tgm.getDat().getWeightParPanel().getWeightpar().clear();
        tgm.getDat().getCohspecPanel().setCohspec(new Cohspec());

        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(tgm.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(tgm, file);

        } catch (javax.xml.bind.JAXBException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
    }

    public static PaletteItem getPaletteItemTransferable(Transferable transferable) {
        Object o = null;
        try {
            o = transferable.getTransferData(new DataFlavor(PaletteItem.class, "PaletteItem"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace();
        }
        return o instanceof PaletteItem ? (PaletteItem) o : null; //TODO: not null
    }

    public static ArrayList<Double> strToParams(String paramStr) {
        ArrayList<Double> paramList = new ArrayList<Double>();
        String[] paramStrArr = paramStr.split(",");
        for (int i = 0; i < paramStrArr.length; i++) {
            paramList.add(Double.parseDouble(paramStrArr[i]));
        }
//        StringTools.getListFromCsv(paramStr);
        return paramList;
    }

    public static String paramsToStr(List<Double> paramList){
        String paramString = null;
        for (int i = 0; i < paramList.size(); i++) {
            if (i > 0) {
                paramString = paramString + ", " + String.valueOf(paramList.get(i));
            } else {
                paramString = String.valueOf(paramList.get(i));
            }
        }

        return paramString;
    }
    
    public static boolean modelParametersChange(Dat model, PropertyChangeEvent evt) {
//        System.out.println("evt.getPropertyName() = " + evt.getPropertyName());
        
        
        if (evt.getSource().getClass().equals(KmatrixNode.class)) {
            if (evt.getPropertyName().equalsIgnoreCase("Positive rates")) {
                model.getKinparPanel().setPositivepar((Boolean) evt.getNewValue());
                return true;
            }
              if (evt.getPropertyName().equalsIgnoreCase("NNLS")) {
                model.getKinparPanel().setNnls((Boolean) evt.getNewValue());
                return true;
            }
        }
        
        if (evt.getSource().getClass().equals(KineticParametersNode.class)) {
            if (model.getKinparPanel() == null) {
                model.setKinparPanel(new KinparPanelModel());
            }
            return handleKinParSignals(model.getKinparPanel(), evt);
        }
        
        if (evt.getSource().getClass().equals(IrfParametersNode.class)) {
            if (model.getIrfparPanel() == null) {
                model.setIrfparPanel(new IrfparPanelModel());
            }
            return handleIRFSignals(model.getIrfparPanel(), evt);
        }
        
        if (evt.getSource().getClass().equals(CohSpecNode.class)) {
            if (model.getCohspecPanel() == null) {
                model.setCohspecPanel(new CohspecPanelModel());
            }
            return handleCohSpecSignals(model.getCohspecPanel(), evt);
        }
       
        if (evt.getSource().getClass().equals(DispersionModelingNode.class)) {
            if (model.getIrfparPanel() == null) {
                model.setIrfparPanel(new IrfparPanelModel());
            }    
            return handleDispersionSignals(model.getIrfparPanel(), evt);
        }
        
        if (evt.getSource().getClass().equals(WeightParametersNode.class)) {
            if (model.getWeightParPanel() == null) {
                model.setWeightParPanel(new WeightParPanelModel());
            }   
            return handleWeightSignals(model.getWeightParPanel(), evt);
        }
        return false;
    }
    
    
    private static boolean handleKinParSignals(KinparPanelModel kinParModel, PropertyChangeEvent evt) {
         if (evt.getPropertyName().equalsIgnoreCase("Number of components")) {
                if ((Integer) evt.getNewValue() > (Integer) evt.getOldValue()) {
                    for (int i = 0; i < (Integer) evt.getNewValue() - (Integer) evt.getOldValue(); i++) {
                        KinPar newkp = new KinPar();
                        newkp.setFixed(Boolean.FALSE);
                        newkp.setConstrained(Boolean.FALSE);
                        kinParModel.getKinpar().add(newkp);
                    }
                } else {
                    for (int i = 0; i < (Integer) evt.getOldValue() - (Integer) evt.getNewValue(); i++) {
                        kinParModel.getKinpar().remove(
                                kinParModel.getKinpar().size() - 1);
                    }
                }
            }
            if (evt.getPropertyName().equalsIgnoreCase("Positive rates")) {
                kinParModel.setPositivepar((Boolean) evt.getNewValue());
            }
              if (evt.getPropertyName().equalsIgnoreCase("NNLS")) {
                kinParModel.setNnls((Boolean) evt.getNewValue());
                return true;
            }
            if (evt.getPropertyName().equalsIgnoreCase("Sequential model")) {
                kinParModel.setSeqmod((Boolean) evt.getNewValue());
            }
            if (evt.getPropertyName().equalsIgnoreCase("start")) {
                int index;
                if (evt.getOldValue() == null) {
                    index = (int) floor((Double) evt.getNewValue());
                } else {
                    index = (int) floor((Double) evt.getOldValue());
                }
                kinParModel.getKinpar().get(index).setStart((Double) evt.getNewValue());
            }
            if (evt.getPropertyName().equalsIgnoreCase("fixed")) {
                int index;
                if (evt.getOldValue() == null) {
                    index = (int) floor((Double) evt.getNewValue());
                } else {
                    index = (int) floor((Double) evt.getOldValue());
                }
                kinParModel.getKinpar().get(index).setFixed((Boolean) evt.getNewValue());
            }
            if (evt.getPropertyName().equalsIgnoreCase("delete")) {
                int index = (Integer) evt.getNewValue();
                kinParModel.getKinpar().remove(index);
            }
            if (evt.getPropertyName().equalsIgnoreCase("mainNodeDeleted")) {
                kinParModel.getKinpar().clear();
                kinParModel.setPositivepar(false);
                kinParModel.setSeqmod(false);
            }
            return true;
    }
    
    private static boolean handleIRFSignals(IrfparPanelModel irfModel, PropertyChangeEvent evt) {
                   if (evt.getPropertyName().equalsIgnoreCase("SetBackSweep")) {
                irfModel.setBacksweepEnabled((Boolean) evt.getNewValue());
            }
            if (evt.getPropertyName().equalsIgnoreCase("SetBackSweepPeriod")) {
                irfModel.setBacksweepPeriod((Double) evt.getNewValue());
            }
            if (evt.getPropertyName().equalsIgnoreCase("mainNodeDeleted")) {
                irfModel.getIrf().clear();
                irfModel.getFixed().clear();
                irfModel.setParmufixed(Boolean.FALSE);
                irfModel.setPartaufixed(Boolean.FALSE);
                irfModel.setBacksweepEnabled(Boolean.FALSE);
                irfModel.setMirf(Boolean.FALSE);
            }
            if (evt.getPropertyName().equalsIgnoreCase("SetIRFType")) {
                setIrfType(irfModel, evt, (IRFTypes) evt.getNewValue());
            }
            
            if (evt.getPropertyName().equalsIgnoreCase("multiGausNum")) {
                Integer oldVal = (Integer)evt.getOldValue();
                Integer newVal = (Integer)evt.getNewValue();
                if (oldVal < newVal){
                    for (int i = 0; i < (newVal-oldVal)*3; i++) {
                        irfModel.getIrf().add(
                            ((ParametersSubNode) ((IrfParametersNode) evt.getSource()).getChildren().getNodes()[i]).getDataObj().getStart());
                        irfModel.getFixed().add(
                            ((ParametersSubNode) ((IrfParametersNode) evt.getSource()).getChildren().getNodes()[i]).getDataObj().isFixed());
                    }
                } else {
                    for (int i = 0; i < (oldVal-newVal)*3; i++) {
                        irfModel.getIrf().remove(irfModel.getIrf().size() - 1);
                        irfModel.getFixed().remove(irfModel.getFixed().size() - 1);
                }
                    
                }
//                setIrfType(irfModel, evt, (IRFTypes) evt.getNewValue());
            }

            if (evt.getPropertyName().equalsIgnoreCase("start")) {
                if (irfModel.getIrf().isEmpty()) {
                    setIrfType(irfModel, evt, ((IrfParametersNode) evt.getSource()).getIRFType());
                }
                int index;
                if (evt.getOldValue() == null) {
                    index = (int) floor((Double) evt.getNewValue());
                } else {
                    index = (int) floor((Double) evt.getOldValue());
                }
                irfModel.getIrf().set(index, (Double) evt.getNewValue());

            }
            if (evt.getPropertyName().equalsIgnoreCase("fixed")) {
                if (irfModel.getIrf().isEmpty()) {
                    setIrfType(irfModel, evt, ((IrfParametersNode) evt.getSource()).getIRFType());
                }
                irfModel.getFixed().set((int) floor((Double) evt.getOldValue()), (Boolean) evt.getNewValue());
            }
            return true;
    }
    
    private static boolean handleWeightSignals(WeightParPanelModel weightModel, PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase("Number of components")) {
            if ((Integer) evt.getNewValue() > (Integer) evt.getOldValue()) {
                for (int i = 0; i < (Integer) evt.getNewValue() - (Integer) evt.getOldValue(); i++) {
                    weightModel.getWeightpar().add(new WeightPar());
                }
            } else {
                for (int i = 0; i < (Integer) evt.getOldValue() - (Integer) evt.getNewValue(); i++) {
                    weightModel.getWeightpar().remove(
                            weightModel.getWeightpar().size() - 1);
                }
            }
        }
        if (evt.getPropertyName().equalsIgnoreCase("Set poisson weight")) {
            weightModel.setPoisson((Boolean) evt.getNewValue());
        }
        if (evt.getPropertyName().equalsIgnoreCase("mainNodeDeleted")) {
            weightModel.getWeightpar().clear();
        }
        if (evt.getPropertyName().equalsIgnoreCase("weight")) {
            weightModel.getWeightpar().get((int) floor((Double) evt.getOldValue())).setWeight((Double) evt.getNewValue());
        }
        if (evt.getPropertyName().equalsIgnoreCase("setmin1")) {
            weightModel.getWeightpar().get((int) floor((Double) evt.getOldValue())).setMin1((Double) evt.getNewValue());
        }
        if (evt.getPropertyName().equalsIgnoreCase("setmin2")) {
            weightModel.getWeightpar().get((int) floor((Double) evt.getOldValue())).setMin2((Double) evt.getNewValue());
        }
        if (evt.getPropertyName().equalsIgnoreCase("setmax1")) {
            weightModel.getWeightpar().get((int) floor((Double) evt.getOldValue())).setMax1((Double) evt.getNewValue());
        }
        if (evt.getPropertyName().equalsIgnoreCase("setmax2")) {
            weightModel.getWeightpar().get((int) floor((Double) evt.getOldValue())).setMax2((Double) evt.getNewValue());
        }
        if (evt.getPropertyName().equalsIgnoreCase("delete")) {
            int index = (Integer) evt.getNewValue();
            weightModel.getWeightpar().remove(index);
        }
        return true;
    }
    
    private static boolean handleDispersionSignals(IrfparPanelModel irfModel, PropertyChangeEvent evt) {
        boolean parMu = evt.getOldValue().equals(EnumTypes.DispersionTypes.PARMU);
        if (evt.getPropertyName().equalsIgnoreCase("mainNodeDeleted")) {
            Children nodes = ((DispersionModelingNode) evt.getSource()).getParentNode().getChildren();

            for (int i = 0; i < nodes.getNodesCount(); i++) {
                if (nodes.getNodes()[i] instanceof DispersionModelingNode) {
                    ((DispersionModelingNode) nodes.getNodes()[i]).setSingle(true);
                    ((DispersionModelingNode) nodes.getNodes()[i]).recreateSheet();
                }
            }
            if (parMu) {
                irfModel.setParmufixed(false);
                irfModel.setDispmufun("");
                irfModel.setParmu("");
            } else {
                irfModel.setPartaufixed(false);
                irfModel.setDisptaufun("");
                irfModel.setPartau("");
            }
        }
        if (evt.getPropertyName().equalsIgnoreCase("setCentralWave")) {
            irfModel.setLamda((Double) evt.getNewValue());
        }

        if (evt.getPropertyName().equalsIgnoreCase("setDisptype")) {
            if ((evt.getOldValue().equals(EnumTypes.DispersionTypes.PARMU))
                    & (evt.getNewValue().equals(EnumTypes.DispersionTypes.PARTAU))) {

                irfModel.setPartaufixed(irfModel.isParmufixed());
                irfModel.setDisptaufun(irfModel.getDispmufun());
                irfModel.setPartau(irfModel.getParmu());
                irfModel.setParmufixed(false);
                irfModel.setDispmufun("");
                irfModel.setParmu("");
            } else {
                if ((evt.getOldValue().equals(EnumTypes.DispersionTypes.PARTAU))
                        & (evt.getNewValue().equals(EnumTypes.DispersionTypes.PARMU))) {

                    irfModel.setPartaufixed(irfModel.isParmufixed());
                    irfModel.setDisptaufun(irfModel.getDispmufun());
                    irfModel.setPartau(irfModel.getParmu());
                    irfModel.setParmufixed(false);
                    irfModel.setDispmufun("");
                    irfModel.setParmu("");
                }
            }
        }

        if (evt.getPropertyName().equalsIgnoreCase("Number of components")) {
            String paramString;

            if (((DispersionModelingNode) evt.getSource()).getDisptype().equals(EnumTypes.DispersionTypes.PARMU)) {
                if ((Integer) evt.getNewValue() > (Integer) evt.getOldValue()) {
                    for (int i = 0; i < (Integer) evt.getNewValue() - (Integer) evt.getOldValue(); i++) {
                        irfModel.getParmulist().add(0.0);
                    }
                } else {
                    for (int i = 0; i < (Integer) evt.getOldValue() - (Integer) evt.getNewValue(); i++) {
                        irfModel.getParmulist().remove(irfModel.getParmulist().size() - 1);
                    }
                }
                paramString = paramsToStr(irfModel.getParmulist());
                irfModel.setParmu(paramString);
            }
            if (((DispersionModelingNode) evt.getSource()).getDisptype().equals(EnumTypes.DispersionTypes.PARTAU)) {
                if ((Integer) evt.getNewValue() > (Integer) evt.getOldValue()) {
                    for (int i = 0; i < (Integer) evt.getNewValue() - (Integer) evt.getOldValue(); i++) {
                        irfModel.getPartaulist().add(0.0);
                    }
                } else {
                    for (int i = 0; i < (Integer) evt.getOldValue() - (Integer) evt.getNewValue(); i++) {
                        irfModel.getPartaulist().remove(irfModel.getPartaulist().size() - 1);
                    }
                }
                paramString = paramsToStr(irfModel.getPartaulist());
                irfModel.setPartau(paramString);
            }
        }

        if (evt.getPropertyName().equalsIgnoreCase("delete")) {
// in the parsing old style string parameter is used                
            int index = (Integer) evt.getNewValue();
            String paramString;
            if (evt.getOldValue().equals(EnumTypes.DispersionTypes.PARMU)) {
                irfModel.getParmulist().remove(index);
                if (irfModel.getPartaufixedlist() != null && !irfModel.getPartaufixedlist().isEmpty()) {
                    irfModel.getPartaufixedlist().remove(index);
                }
                paramString = paramsToStr(irfModel.getParmulist());
                irfModel.setParmu(paramString);
            }
            if (evt.getOldValue().equals(EnumTypes.DispersionTypes.PARTAU)) {
                irfModel.getPartaulist().remove(index);
                if (irfModel.getPartaufixedlist() != null && !irfModel.getPartaufixedlist().isEmpty()) {
                    irfModel.getPartaufixedlist().remove(index);
                }
                paramString = paramsToStr(irfModel.getPartaulist());
                irfModel.setPartau(paramString);
            }
        }

        if (evt.getPropertyName().equalsIgnoreCase("start")) {
            // in the parsing old style string parameter is used   
            String paramString;
            ParametersSubNode paramSubNode;

            if (evt.getOldValue().equals(EnumTypes.DispersionTypes.PARMU)) {
                irfModel.getParmulist().clear();
                for (int i = 0; i < ((DispersionModelingNode) evt.getSource()).getChildren().getNodesCount(); i++) {
                    paramSubNode = (ParametersSubNode) ((DispersionModelingNode) evt.getSource()).getChildren().getNodeAt(i);
                    irfModel.getParmulist().add(paramSubNode.getDataObj().getStart());
                }
                paramString = paramsToStr(irfModel.getParmulist());
                irfModel.setParmu(paramString);
                irfModel.setDispmufun("poly");

            }

            if (evt.getOldValue().equals(EnumTypes.DispersionTypes.PARTAU)) {
                irfModel.getPartaulist().clear();
                for (int i = 0; i < ((DispersionModelingNode) evt.getSource()).getChildren().getNodesCount(); i++) {
                    paramSubNode = (ParametersSubNode) ((DispersionModelingNode) evt.getSource()).getChildren().getNodeAt(i);
                    irfModel.getPartaulist().add(paramSubNode.getDataObj().getStart());
                }
                paramString = paramsToStr(irfModel.getPartaulist());
                irfModel.setPartau(paramString);
                irfModel.setDisptaufun("poly");
            }
        }

        if (evt.getPropertyName().equalsIgnoreCase("fixed")) {
            ParametersSubNode paramSubNode;
            irfModel.getParmufixedlist().clear();
            irfModel.getPartaufixedlist().clear();
            for (int i = 0; i < ((DispersionModelingNode) evt.getSource()).getChildren().getNodesCount(); i++) {
                paramSubNode = (ParametersSubNode) ((DispersionModelingNode) evt.getSource()).getChildren().getNodeAt(i);
                if (evt.getOldValue().equals(EnumTypes.DispersionTypes.PARMU)) {
                    irfModel.getParmufixedlist().add(paramSubNode.getDataObj().isFixed());
                }
                if (evt.getOldValue().equals(EnumTypes.DispersionTypes.PARTAU)) {
                    irfModel.getPartaufixedlist().add(paramSubNode.getDataObj().isFixed());
                }
            }

            //TODO: this code is now obsolete in the parsing this parameter is still used 
            if (evt.getOldValue().equals(EnumTypes.DispersionTypes.PARMU)) {
                irfModel.setParmufixed((Boolean) evt.getNewValue());
            } else {
                if (evt.getOldValue().equals(EnumTypes.DispersionTypes.PARTAU)) {
                    irfModel.setPartaufixed((Boolean) evt.getNewValue());
                }
            }
        }
        return true;
    }
    
    private static boolean handleCohSpecSignals(CohspecPanelModel cohSpecModel, PropertyChangeEvent evt) {
        //{"cohSpecName", "cohSpecModelType", "cohSpecClpZero", "cohSpecClpMin", "cohSpecClpMax"}
        if (evt.getPropertyName().equalsIgnoreCase("cohSpecClpMax")) {
            cohSpecModel.setClp0Max((Double) evt.getNewValue());
        }
        if (evt.getPropertyName().equalsIgnoreCase("cohSpecClpMin")) {
            cohSpecModel.setClp0Min((Double) evt.getNewValue());
        }
        if (evt.getPropertyName().equalsIgnoreCase("cohSpecClpZero")) {
            cohSpecModel.setClp0Enabled((Boolean) evt.getNewValue());
        }
        if (evt.getPropertyName().equalsIgnoreCase("cohSpecModelType")) {
            EnumTypes.CohSpecTypes cohType = (CohSpecTypes) evt.getNewValue();
            switch (cohType) {
                case IRF: {
                    cohSpecModel.getCohspec().setType("irf");
                    cohSpecModel.getCohspec().setSet(true);
                    break;
                }
                case FREE_IRF: {
                    cohSpecModel.getCohspec().setType("freeirfdisp");
                    cohSpecModel.getCohspec().setSet(true);
                    break;
                }
                case IRF_MULTY: {
                    cohSpecModel.getCohspec().setType("irfmulti");
                    cohSpecModel.getCohspec().setSet(true);
                    break;
                }
                case MIXED: {
                    cohSpecModel.getCohspec().setType("mix");
                    cohSpecModel.getCohspec().setSet(true);
                    break;
                }
                case SEQ: {
                    cohSpecModel.getCohspec().setType("seq");
                    cohSpecModel.getCohspec().setSet(true);
                    break;
                }
            }
        }
        if (evt.getPropertyName().equalsIgnoreCase("mainNodeDeleted")) {
            cohSpecModel.setClp0Enabled(null);
            cohSpecModel.setClp0Max(null);
            cohSpecModel.setClp0Min(null);
            cohSpecModel.setCoh("");
            cohSpecModel.getCohspec().setSet(false);
            cohSpecModel.getCohspec().setType("");
        }

        if (evt.getPropertyName().equalsIgnoreCase("Number of components")) {
            if ((Integer) evt.getNewValue() > (Integer) evt.getOldValue()) {
                for (int i = 0; i < (Integer) evt.getNewValue() - (Integer) evt.getOldValue(); i++) {
                    cohSpecModel.getCohspec().getSeqstart().add(0.0);
                }
            } else {
                for (int i = 0; i < (Integer) evt.getOldValue() - (Integer) evt.getNewValue(); i++) {
                    cohSpecModel.getCohspec().getSeqstart().remove(cohSpecModel.getCohspec().getSeqstart().size() - 1);
                }
            }
        }

        if (evt.getPropertyName().equalsIgnoreCase("delete")) {
            int index = (Integer) evt.getNewValue();
            cohSpecModel.getCohspec().getSeqstart().remove(index);
        }

        if (evt.getPropertyName().equalsIgnoreCase("start")) {
            ParametersSubNode paramSubNode;
            cohSpecModel.getCohspec().getSeqstart().clear();
            for (int i = 0; i < ((CohSpecNode) evt.getSource()).getChildren().getNodesCount(); i++) {
                paramSubNode = (ParametersSubNode) ((CohSpecNode) evt.getSource()).getChildren().getNodeAt(i);
                cohSpecModel.getCohspec().getSeqstart().add(paramSubNode.getDataObj().getStart());
            }
        }
        return true;
    }
            
    private static void setIrfType(IrfparPanelModel irfModel, PropertyChangeEvent evt, EnumTypes.IRFTypes type) {
        EnumTypes.IRFTypes irfType = type;
        irfModel.setIrftype(type.toString());
        switch (irfType) {
            case GAUSSIAN: {
                irfModel.setMirf(Boolean.FALSE);
                if (irfModel.getIrf() != null) {
                    irfModel.getIrf().clear();
                    irfModel.getFixed().clear();
                }
                irfModel.setIrftype(irfType.toString());
                for (int i = 0; i < 2; i++) {
                    irfModel.getIrf().add(
                            ((ParametersSubNode) ((IrfParametersNode) evt.getSource()).getChildren().getNodes()[i]).getDataObj().getStart());
                    irfModel.getFixed().add(
                            ((ParametersSubNode) ((IrfParametersNode) evt.getSource()).getChildren().getNodes()[i]).getDataObj().isFixed());
                }
                break;
            }
            case DOUBLE_GAUSSIAN: {
                irfModel.setMirf(Boolean.FALSE);
                if (irfModel.getIrf() != null) {
                    irfModel.getIrf().clear();
                    irfModel.getFixed().clear();
                }
                irfModel.setIrftype(irfType.toString());
                for (int i = 0; i < 4; i++) {
                    irfModel.getIrf().add(
                            ((ParametersSubNode) ((IrfParametersNode) evt.getSource()).getChildren().getNodes()[i]).getDataObj().getStart());
                    irfModel.getFixed().add(
                            ((ParametersSubNode) ((IrfParametersNode) evt.getSource()).getChildren().getNodes()[i]).getDataObj().isFixed());
                }
                break;
            }
            case MULTIPLE_GAUSSIAN: {
                irfModel.setMirf(Boolean.FALSE);
                if (irfModel.getIrf() != null) {
                    irfModel.getIrf().clear();
                    irfModel.getFixed().clear();
                }
                irfModel.setIrftype(irfType.toString());
                int nodeCount = ((IrfParametersNode) evt.getSource()).getChildren().getNodesCount();
                for (int i = 0; i < nodeCount; i++) {
                    irfModel.getIrf().add(
                            ((ParametersSubNode) ((IrfParametersNode) evt.getSource()).getChildren().getNodes()[i]).getDataObj().getStart());
                    irfModel.getFixed().add(
                            ((ParametersSubNode) ((IrfParametersNode) evt.getSource()).getChildren().getNodes()[i]).getDataObj().isFixed());
                }
                break;
            }
            case MEASURED_IRF: {
                irfModel.setMirf(Boolean.TRUE);
                irfModel.setIrftype(irfType.toString());
                //todo finish measured IRF implementation
                break;
            }
        }

    }
}
