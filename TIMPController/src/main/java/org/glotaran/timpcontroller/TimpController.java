/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.timpcontroller;

import org.ujmp.core.Matrix;
import java.util.ArrayList;
import java.util.List;
import org.glotaran.core.interfaces.TimpControllerInterface;
import org.glotaran.core.models.structures.DatasetTimp;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.core.messages.CoreErrorMessages;
import org.glotaran.core.models.tgm.Tgm;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRList;
import org.rosuda.irconnect.IRMap;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.rengine.REngineConnectionFactory;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.ValueType;
import org.ujmp.jama.JamaDenseDoubleMatrix2D;

@org.openide.util.lookup.ServiceProvider(service = TimpControllerInterface.class)
public class TimpController implements TimpControllerInterface {

    private ITwoWayConnection connection = null;
    private boolean isRserveRunning = false;
    private boolean isTIMPLoaded = false;
    private InputOutput io;

    public TimpController() {
        io = IOProvider.getDefault().getIO("Rserve", true);
        if (connection == null) {
            connection = MakeNewConnection();
        }
    }

    @Override
    public boolean isConnected() {
        if (connection != null) {
            return connection.isConnected();
        } else {
            connection = MakeNewConnection();
            if (connection != null) {
                return connection.isConnected();
            } else {
                return false;
            }
        }
    }

    @Override
    public void cleanup() {
        connection.voidEval("try(rm(list=ls()))");
        connection.voidEval("try(gc())");
        System.gc();
    }

    private ITwoWayConnection MakeNewConnection() {
        // if (connection == null) {
        try {
            connection = new REngineConnectionFactory().createTwoWayConnection(null);
            if (connection.isConnected()) {
                connection.voidEval("");
                connection.assign("isGlotaranConnection", "TRUE");
            }
        } catch (Exception e) {
            io.getErr().println("Failed to connect to R via Rserve");
            io.getErr().println(e.getMessage());
        }
        //  }
        return connection;
    }

    private boolean isTIMPinstalled() {
        return connection.eval("is.element(\"TIMP\", installed.packages()[,1])").asBool().isTRUE();
    }

    private void verifyRserve() {
        OutputWriter writer = io.getOut();

        if (connection == null) {
            writer.println("Starting Rserve connection ...");
            connection = MakeNewConnection();
        } else {            
            writer.println("Restarting Rserve connection ...");
            connection.close();            
            connection = MakeNewConnection();
        }
        if (connection != null) {
            writer.println("Rserve connection started.");
            if (connection.isConnected()) {
                writer.println("Working Rserve connection established.");
            }
            writer.append("Rserve test evaluation:");
            try {
                connection.voidEval("isCalledFromGlotaran <- TRUE");
                if (connection.eval("isCalledFromGlotaran").asBool().isTRUE()) {
                    writer.append(" OK").flush();

                }
            } catch (Exception e) {
                // R session could not be validated
                writer.append(" FAIL").flush();
                return;
            }
            writer.println();

            writer.append("Rserve test assignment:");
            try {
                connection.assign("isAssignedFromGlotaran", "TRUE");
                if (connection.eval("as.logical(isAssignedFromGlotaran)").asBool().isTRUE()) {
                    writer.append(" OK").flush();
                    isRserveRunning = true;

                }
            } catch (Exception e) {
                // R session could not be validated
                writer.append(" FAIL").flush();
                isRserveRunning = false;
                return;
            }
            writer.println();

            writer.append("Test if R-package TIMP is installed:");
            if (isTIMPinstalled()) {
                long version;
                writer.append(" OK").flush();
                writer.println();
                writer.append("Test if TIMP is up to date:");
                int[] verArray = connection.eval("packageVersion(\"TIMP\")").asList().getHead().asIntArray();
                version = (long) (verArray[0] * Math.pow(10, 8) + verArray[1] * Math.pow(10, 4) + verArray[2]);
                if (version >= 100120001) {
                    writer.append(" OK.").flush();
                } else {
                    writer.append(" WARNING.").flush();
                    writer.println();
                    io.getErr().println("Warning: please update TIMP. Run R and run install.packages(\"TIMP\")");
                    io.getErr().println("An older version might work but could lead to unexpected behavior.");
                    StatusDisplayer.getDefault().setStatusText("Please update the R-package TIMP", 1).clear(15000);
                }
                writer.println();
                writer.println("Detected TIMP version: " + verArray[0] + "." + verArray[1] + "." + verArray[2]);

            } else {
                writer.append(" FAIL.").flush();
                writer.println();
                io.getErr().println("ERROR: please install TIMP. Run R and run install.packages(\"TIMP\")");
                io.select();
                StatusDisplayer.getDefault().setStatusText("The R-package TIMP is not installed. Please install.", 1).clear(30000);
                return;
            }

            writer.append("Loading TIMP package:");
            if (isTIMPinstalled()) {
                try {
                    isTIMPLoaded = connection.eval("as.logical(print(require(TIMP)))").asBool().isTRUE();
                    if (isTIMPLoaded) {
                        writer.append(" OK.").flush();
                    } else {
                        writer.append(" FAIL").flush();
                    }
                    //writer.append(s).flush();
                } catch (Exception e) {
                    // R session could not be validated
                    writer.append(" FAIL").flush();
                    isTIMPLoaded = false;
                    writer.println();
                    return;
                }
            }
            writer.println();

            // check if a TIMP function can be called
            writer.close();
        }

    }

    @Override
    public TimpResultDataset[] runAnalysis(DatasetTimp[] datasets, Tgm[] models, int iterations) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TimpResultDataset[] runAnalysis(DatasetTimp[] datasets, ArrayList<String> initModelCalls, String fitModelCall) {
        if (!isRserveRunning || !isTIMPLoaded) {
            verifyRserve();
        }
        if (isRserveRunning && isTIMPLoaded) {

            TimpResultDataset[] results = null;

            for (int i = 0; i < datasets.length; i++) {
                sendDataset(datasets[i], i);
            }

            for (int i = 0; i < initModelCalls.size(); i++) {
                sendModel(initModelCalls.get(i), i);
            }

            connection.voidEval("try(" + fitModelCall + ")");
            if (getBool("exists(\"" + NAME_OF_RESULT_OBJECT + "\")")) {
                results = new TimpResultDataset[datasets.length];
                for (int i = 0; i < datasets.length; i++) {
                    results[i] = getTimpResultDataset(i);
                }
                //TODO: make sure this is possible
                //connection.close();
            }
            return results;
        } else {
            return null;
        }
    }

    private Matrix getTempMatrix(String cmd) {
        connection.voidEval("temp <-" + cmd);
        int[] dim = getIntArray("dim(temp)"); //[nrows ncolums]
        //TODO: replace this with getDoubleArray
        IREXP ret = connection.eval("temp");
        Matrix retMatrix = MatrixFactory.importFromArray(ret.asDoubleArray());
        //retMatrix.setSize(dim[0], dim[1]);        
        return ret.getType() == IREXP.XT_NULL ? null : retMatrix.reshape(Ret.NEW, dim[0], dim[1]);
    }

    private void sendDataset(DatasetTimp dd, int index) {
        index++;
        connection.assign("psisim", dd.getPsisim());
        connection.assign("x", dd.getX());
        connection.assign("x2", dd.getX2());
        connection.assign("nl", new int[]{dd.getNl()});
        connection.assign("nt", new int[]{dd.getNt()});
        connection.assign("intenceIm", dd.getIntenceIm());

        connection.eval("psisim <- as.matrix(psisim)");
        connection.eval("if(is.null(intenceIm))  intenceIm <- matrix(1,1,1)");
        connection.eval("intenceIm <- as.matrix(intenceIm)");
        connection.eval("dim(psisim) <- c(nt, nl)");

        connection.eval(NAME_OF_DATASET + String.valueOf(index) + " <- dat(psi.df = psisim, x = x, nt = nt, x2 = x2, nl = nl, "
                + "inten = intenceIm)");
    }

    private Boolean sendModel(String modelString, int index) {
        String nameOfModel;
        nameOfModel = modelString.substring(0, modelString.indexOf("<-")).trim();
        //TODO: remove this ugly try and catch, by checking if the model is valid before we send it to R
        try {
            connection.voidEval("try(" + modelString + ")");
        } catch (Exception e) {
            CoreErrorMessages.initModelException();
            return null;
        }
        return getBool("exists(\"" + nameOfModel + "\")");
    }

    private String getOptions(String modelType, int iterations) {
        String result = "opt = " + modelType + "opt("
                + "iter = " + String.valueOf(iterations)
                + ", plot=FALSE)";
        connection.eval(result);
        return result;
    }

    private TimpResultDataset getTimpResultDataset(int datasetNumber) {

        String datasetName = "dataset" + String.valueOf(datasetNumber);
        datasetNumber++;
        TimpResultDataset result = new TimpResultDataset();

        result.setDatasetName(datasetName);
//        result.setSpectra(getCLP(NAME_OF_RESULT_OBJECT, datasetNumber));
        Matrix spectra = getSpectra(NAME_OF_RESULT_OBJECT, datasetNumber);
        if (spectra != null) {
            result.setSpectra(new JamaDenseDoubleMatrix2D(spectra).getWrappedObject());
        }
        Matrix clp = getCLP(NAME_OF_RESULT_OBJECT, true, datasetNumber);
        if (clp != null) {
            result.setSpectraErr(new JamaDenseDoubleMatrix2D(clp).getWrappedObject());
        }
        result.setX(getdim1(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setX2(getdim2(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setResiduals(new JamaDenseDoubleMatrix2D(getResiduals(NAME_OF_RESULT_OBJECT, datasetNumber)).getWrappedObject());
        result.setTraces(new JamaDenseDoubleMatrix2D(getData(NAME_OF_RESULT_OBJECT, datasetNumber)).getWrappedObject()); //if weighted=TRUE then use 3rd argument
        result.setFittedTraces(new JamaDenseDoubleMatrix2D(getTraces(NAME_OF_RESULT_OBJECT, datasetNumber)).getWrappedObject());

        result.setKineticParameters(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "kinpar"));
        result.setSpectralParameters(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "specpar"));
        result.setIrfpar(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "irfpar"));
        result.setCoh(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "coh"));
        result.setOscpar(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "oscpar"));
//        result.setIrfpar(getIrfpar(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setParmu(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "parmu"));
//        result.setParmu(getParmu(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setSpecdisppar(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "specdisppar"));
//        result.setSpecdisppar(getSpecdisppar(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setJvec(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "jvec"));
//        result.setJvec(getJvec(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setClpequ(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "clpequ"));
//        result.setClpequ(getClpeq(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setKinscal(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "kinscal"));
//        result.setKinscal(getKinscal(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setPrel(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "prel"));
//        result.setPrel(getPrel(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setRms(getRMS(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setPartau(getParEst(NAME_OF_RESULT_OBJECT, datasetNumber, "partau"));
//        result.setPartau(getPartau(NAME_OF_RESULT_OBJECT, datasetNumber));
        result.setEigenvaluesK(getEigenvaluesK(NAME_OF_RESULT_OBJECT, datasetNumber));

        if (result.getKineticParameters().length > 2) {
            result.setConcentrations(new JamaDenseDoubleMatrix2D(getC(NAME_OF_RESULT_OBJECT, datasetNumber)).getWrappedObject());
        } else {
//            result.setConcentrations(getX(NAME_OF_RESULT_OBJECT, datasetNumber, true));
            result.setConcentrations(new JamaDenseDoubleMatrix2D(getC(NAME_OF_RESULT_OBJECT, datasetNumber)).getWrappedObject());
        }
        return result;
    }

    // TIMP specific functions
    public double[] getIrfpar(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "$currTheta[["
                + dataset + "]]@irfpar");
    }

    public double[] getParmu(String resultVariable, int dataset) {
        return getDoubleArray("as.vector(matrix(unlist(" + resultVariable
                + "$currTheta[[" + dataset + "]]@parmu),nrow=1))");
    }

    public double[] getPartau(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "$currTheta[["
                + dataset + "]]@partau");
    }

    public double[] getClpeq(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "$currTheta[["
                + dataset + "]]@clpequ");
    }

    public double[] getKinscal(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "$currTheta[["
                + dataset + "]]@kinscal");
    }

    public double[] getPrel(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "$currTheta[["
                + dataset + "]]@prel");
    }

    public double[] getSpecdisppar(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "$currTheta[["
                + dataset + "]]@specdisppar");
    }

    public double[] getJvec(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "$currTheta[["
                + dataset + "]]@jvec");
    }

    public double getLamdac(String resultVariable, int dataset) {
        return getDouble(resultVariable + "$currTheta[[" + dataset + "]]@lamdac");
    }

    public double getRMS(String resultVariable, int dataset) {
        //return getDouble("onls(" + resultVariable + ")$m$deviance()");
        return getDouble(resultVariable + "$currModel@fit@nlsres$sumonls$sigma");
    }

    private double[] getEigenvaluesK(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "$currTheta[[" + dataset + "]]@eigenvaluesK");
    }

    public double[] getParEst(String resultVariable, int dataset,
            String param) {
        return getDoubleArray(
                new StringBuffer().append(""
                        + "unlist("
                        + "parEst("
                        + resultVariable + ", "
                        + "param = \"" + param + "\""
                        + ",dataset = " + dataset
                        + "))").toString());
    }

    public List<Matrix> getXList(String resultVariable, boolean single) {
        return getXList(resultVariable, 0, single);
    }

    public List<Matrix> getXList(String resultVariable, int group, boolean single) {
        //getXList(cmd, group = vector())
        List<Matrix> ls = null;
        int length = connection.eval("length(" + resultVariable + ")").asInt();
        for (int i = 0; i < length; i++) {
            ls.add(i, getX(resultVariable, group, i, single));
        }
        return ls;
    }

    public Matrix getX(String resultVariable, int index, boolean single) {
        return getX(resultVariable, 1, index, single);
    }

    public Matrix getC(String resultVariable, int index) {
        return getTempMatrix("getC(" + resultVariable + ", dataset =" + index + ")");
    }

    public Matrix getX(String resultVariable, int group, int index, boolean single) {
        //getX(cmd, group = vector(), dataset=1)
        if (group == 0) {
            String groupStr = "vector()";
        } else {
            String groupStr = Integer.toString(group);
        }
        if (single) {
            double[] temp = connection.eval("getX(" + resultVariable + ", dataset =" + index + ")").asDoubleArray();
            Matrix x = MatrixFactory.importFromArray(temp);
            //x.setSize(1, (temp.length-1));
//            Matrix x = new Matrix(1, temp.length);
//            for (int i = 0; i < temp.length; i++) {
//                x.set(0, i, temp[i]);
//            }
            return x.reshape(Ret.NEW, 1, (temp.length - 1));
        } else {
            //return getDoubleMatrix("getX(" + resultVariable + ", dataset =" + index + ")");
            return getTempMatrix("getX(" + resultVariable + ", dataset =" + index + ")");

        }
    }

    public List<Matrix> getCLPList(String resultVariable) {
        return getCLPList(resultVariable, false);
    }

    public List<Matrix> getCLPList(String resultVariable, boolean getclperr) {
        //getCLPList(cmd, group = vector())
        List<Matrix> ls = null;
        int length = getInt("length(" + resultVariable + ")");
        for (int i = 0; i < length; i++) {
            ls.add(i, getCLP(resultVariable, getclperr, i));
        }
        return ls;
    }

    public Matrix getSpectra(String resultVariable, int dataset) {
        Matrix sas = getCLP(resultVariable, false, dataset);
        Matrix das = getDAS(resultVariable, dataset);
        Matrix results = MatrixFactory.concat(0, sas, das);
        // Matrix results = new Matrix(sas.getRowDimension() * 2, sas.getColumnDimension());
        //results.setMatrix(0, sas.getRowDimension() - 1, 0, sas.getColumnDimension() - 1, sas);
        //results.setMatrix(sas.getRowDimension(), sas.getRowDimension() * 2 - 1, 0, sas.getColumnDimension() - 1, das);
        return results;
    }

    private Matrix getDAS(String resultVariable, int dataset) {
        //TODO: Have Ralf fix this bug related to asMatrix not working.
        //return getDoubleMatrix("t(getCLP(" + resultVariable + ", getclperr = " + getclperrStr + ", dataset =" + dataset + "))");
        String cmd = "t(getDAS(" + resultVariable + ", dataset =" + dataset + "))";
        return getTempMatrix(cmd);
    }

    public Matrix getCLP(String resultVariable, int dataset) {
        return getCLP(resultVariable, false, dataset);
    }

    public Matrix getCLP(String resultVariable, boolean getclperr, int dataset) {
        //getCLP(cmd, group = vector(), dataset=1)
        String getclperrStr;
        if (getclperr) {
            getclperrStr = "TRUE";
            boolean clperr = getBool(resultVariable + "$currModel@stderrclp");
            if (!clperr) {
                return null;
            }
        } else {
            getclperrStr = "FALSE";
        }
        //TODO: Have Ralf fix this bug related to asMatrix not working.
        //return getDoubleMatrix("t(getCLP(" + resultVariable + ", getclperr = " + getclperrStr + ", dataset =" + dataset + "))");
        String cmd = "t(getCLP(" + resultVariable + ", getclperr = " + getclperrStr + ", dataset =" + dataset + "))";
        return getTempMatrix(cmd);

    }

    public List<Matrix> getDataList(String resultVariable) {
        return getCLPList(resultVariable, false);
    }

    public List<Matrix> getDataList(String resultVariable, boolean weighted) {
        //getCLPList(cmd, group = vector())
        List<Matrix> ls = null;
        int length = getInt("length(" + resultVariable + ")");
        for (int i = 0; i < length; i++) {
            ls.add(i, getData(resultVariable, i, weighted));
        }
        return ls;
    }

    public Matrix getData(String resultVariable, int dataset) {
        return getData(resultVariable, dataset, false);
    }

    public Matrix getData(String resultVariable, int dataset, boolean weighted) {
        //getCLP(cmd, group = vector(), dataset=1)
        String weightedStr;
        if (weighted) {
            weightedStr = "TRUE";
        } else {
            weightedStr = "FALSE";
        }
        //return getDoubleMatrix("getData(" + resultVariable + ", dataset =" + dataset + ", weighted = " + weightedStr + ")");
        return getTempMatrix("getData(" + resultVariable + ", dataset =" + dataset + ", weighted = " + weightedStr + ")");
    }

    public List<Matrix> getResidualsList(String resultVariable, boolean weighted) {
        //getCLPList(cmd, group = vector())
        List<Matrix> ls = null;
        int length = getInt("length(" + resultVariable + ")");
        for (int i = 0; i < length; i++) {
            ls.add(i, getData(resultVariable, i, weighted));
        }
        return ls;
    }

    public Matrix getResiduals(String resultVariable) {
        return getResiduals(resultVariable, 1);
    }

    public Matrix getResiduals(String resultVariable, int dataset) {
        //getCLP(cmd, group = vector(), dataset=1)
        //return getDoubleMatrix("getResiduals(" + resultVariable + ", dataset =" + dataset + ")");
        return getTempMatrix("getResiduals(" + resultVariable + ", dataset =" + dataset + ")");
    }

    public List getSVDResiduals(String resultVariable) {
        return getSVDResiduals(resultVariable, 2, 1);
    }

    public List getSVDResiduals(String resultVariable, int numsing, int dataset) {
        //TODO: this function will not return a proper list
        return getList("getSVDResiduals(" + resultVariable + ", numsing =" + numsing + ",dataset =" + ")");
    }

    public List<Matrix> getTracesList(String resultVariable, boolean weighted) {
        //getCLPList(cmd, group = vector())
        List<Matrix> ls = null;
        int length = getInt("length(" + resultVariable + ")");
        for (int i = 0; i < length; i++) {
            ls.add(i, getData(resultVariable, i, weighted));
        }
        return ls;
    }

    public Matrix getTraces(String resultVariable) {
        return getTraces(resultVariable, 1);
    }

    public Matrix getTraces(String resultVariable, int dataset) {
        //getCLP(cmd, group = vector(), dataset=1)
        //return getDoubleMatrix("getTraces(" + resultVariable + ", dataset =" + dataset + ")");

        if (getBool(resultVariable + "$currModel@modellist[[" + dataset + "]]@weight")) {
            connection.eval("w <- " + resultVariable + "$currModel@modellist[[" + dataset + "]]@weightM");
            connection.eval("f <- " + resultVariable + "$currModel@fit@resultlist[[" + dataset + "]]@fitted");
            connection.eval("f2 = matrix(unlist(f), ncol=ncol(w))/w");
            double[] dim = connection.eval("dim(f2)").asDoubleArray();
            double[] temp = connection.eval("f2").asDoubleArray();
            Matrix retMatrix = MatrixFactory.importFromArray(temp);
            //retMatrix.setSize((long)dim[0], (long)dim[1]);
            return retMatrix.reshape(Ret.NEW, (long) dim[0], (long) dim[1]);
            //return new Matrix(temp, (int) dim[0]);
        } else {
            return getTempMatrix("getTraces(" + resultVariable + ", dataset =" + dataset + ")");
        }
    }

    public double[] getdim1(String resultVariable) {
        //getdim1(cmd)
        return connection.eval("getdim1(" + resultVariable + ")").asDoubleArray();
    }

    public double[] getdim1(String resultVariable, int datasetIndex) {
        return connection.eval("getdim1(" + resultVariable + ", dataset = " + String.valueOf(datasetIndex) + ")").asDoubleArray();
    }

    public double[] getdim2(String resultVariable) {
        //getdim2(cmd)
        return connection.eval("getdim2(" + resultVariable + ")").asDoubleArray();
    }

    public double[] getdim2(String resultVariable, int datasetIndex) {
        //getdim2(cmd)
        return connection.eval("getdim2(" + resultVariable + ", dataset = " + String.valueOf(datasetIndex) + ")").asDoubleArray();
    }

    public boolean existsInR(String varname) {
        return connection.eval("exists(\"" + varname + "\")").asBool().isTRUE();
    }

    public void sendMeasuredIRF(String name, float[] refArray) {

        double[] refD = new double[refArray.length];
        for (int i = 0; i < refD.length; i++) {
            refD[i] = (double) refArray[i];
        }
        connection.assign(name, refD);
    }

    public static void fitModel(int[] datasetIndices, int modelIndex, int optionIndex, String resultsName) {
    }

    public boolean getBool(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        return ret.getType() == ret.XT_BOOL ? ret.asBool().isTRUE() : null;
    }

    public void getBoolArray(String cmd) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public double getDouble(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        return ret.getType() == ret.XT_DOUBLE ? ret.asDouble() : null;
    }

    public double[] getDoubleArray(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        return ret.getType() == ret.XT_ARRAY_DOUBLE ? ret.asDoubleArray() : null;
    }

    public void getFactor(String cmd) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int getInt(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        return ret.getType() == ret.XT_INT ? ret.asInt() : null;
    }

    public int[] getIntArray(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        return ret.getType() == ret.XT_ARRAY_INT ? ret.asIntArray() : null;
    }

    public IRMatrix getIRMatrix(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        return ret.getType() == ret.XT_MATRIX ? ret.asMatrix() : null;
    }

    public Matrix getDoubleMatrix(String cmd) {
        //just once the values
        IRMatrix retIRMatrix = getIRMatrix(cmd);
        Matrix doubleMatrix = MatrixFactory.dense(ValueType.DOUBLE, retIRMatrix.getRows(), retIRMatrix.getColumns());
        for (int row = 0; row < retIRMatrix.getRows(); row++) {
            for (int col = 0; col < retIRMatrix.getColumns(); col++) {
                doubleMatrix.setAsDouble(retIRMatrix.getValueAt(row, col).asDouble(), row, col);
            }
        }
        return doubleMatrix;
    }

    public Matrix getIntMatrix(String cmd) {
        //just once the values
        IRMatrix retIRMatrix = getIRMatrix(cmd);
        Matrix intMatrix = MatrixFactory.dense(ValueType.INT, retIRMatrix.getRows(), retIRMatrix.getColumns());
        //Matrix intMatrix = new Matrix(retIRMatrix.getRows(), retIRMatrix.getColumns());
        for (int row = 0; row < retIRMatrix.getRows(); row++) {
            for (int col = 0; col < retIRMatrix.getColumns(); col++) {
                intMatrix.setAsInt(retIRMatrix.getValueAt(row, col).asInt(), row, col);
            }
        }
        return intMatrix;
    }

    public String getString(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        return ret.getType() == ret.XT_STR ? ret.asString() : null;
    }

    public String[] getStringArray(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        String[] result = new String[1];
        if (ret.getType() == ret.XT_STR) {
            result[0] = ret.asString();
        }
        //return ret.getType()==ret.XT_ARRAY_STR ? ret.asStringArray() : null;
        return ret.getType() == ret.XT_ARRAY_STR ? ret.asStringArray() : null;
    }

    public IRList getIRList(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        return ret.getType() == ret.XT_LIST ? ret.asList() : null;
    }

    public IRMap getIRMap(String cmd) {
        final IREXP ret = connection.eval(new StringBuffer().append(
                "try(").append(cmd).append(")").toString());
        return ret.getType() == ret.XT_MAP ? ret.asMap() : null;
    }

    public List<?> getList(String cmd) {
        // TODO: this function is not yet working.
        List ls = null;
        final IRMap namedRList = getIRMap(cmd);
        final String[] keys = namedRList.keys();
        final IREXP header = namedRList.at(keys[1]);
        final IREXP footer = namedRList.at(keys[1]);
        final IREXP results = namedRList.at(keys[1]);
        ls.add(results.asDoubleArray());
        return ls;
    }

    public void getSymbol(String cmd) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void getVector(String cmd) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private int getDatasetTimplength() {
        return 1;
    }

    public DatasetTimp[] runSimulation(ArrayList<String> initModelCalls, String simModel) {

        DatasetTimp[] results = null;

        for (int i = 0; i < initModelCalls.size(); i++) {
            sendModel(initModelCalls.get(i), i);
        }
        connection.voidEval("try(" + simModel + ")");
        if (getBool("exists(\"" + NAME_OF_SIM_OBJECT + "\")")) {
            results = new DatasetTimp[getDatasetTimplength()];
            for (int i = 0; i < getDatasetTimplength(); i++) {
                results[i] = getResultDataset(i);
            }
            //TODO: make sure this is possible
            //connection.close();
        }
        return results;
    }

    private DatasetTimp getResultDataset(int i) {
        String datasetName = "dataset";
        DatasetTimp result = new DatasetTimp();

        // connection.assign("intenceIm", dd.getIntenceIm());
        result.setDatasetName(datasetName);
        result.setType("spec");
        result.setPsisim(getPsisim(NAME_OF_SIM_OBJECT, 0));
        result.setX(getTime(NAME_OF_SIM_OBJECT, 0));
        result.setX2(getX2(NAME_OF_SIM_OBJECT, 0));
        result.setNl(getNl(NAME_OF_SIM_OBJECT, 0));
        result.setNt(getNt(NAME_OF_SIM_OBJECT, 0));
        result.calcRangeInt();
        return result;
    }

    private double[] getPsisim(String resultVariable, int dataset) {
        String cmd = "t(" + resultVariable + "@psi.df)";
        return getDoubleArray("as.vector(" + resultVariable + "@psi.df)");
        //return getTempMatrix(cmd).get;
        //return getDoubleMatrix(resultVariable + "@psi.df").getRowPackedCopy();
    }

    private double[] getTime(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "@x");
    }

    private double[] getX2(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "@x2");
    }

    private int getNl(String resultVariable, int dataset) {
        return getInt(resultVariable + "@nl");
    }

    private int getNt(String resultVariable, int dataset) {
        return getInt(resultVariable + "@nt");
    }

    private double[] getIntenceIm(String resultVariable, int dataset) {
        return getDoubleArray(resultVariable + "@x2");
    }
}
