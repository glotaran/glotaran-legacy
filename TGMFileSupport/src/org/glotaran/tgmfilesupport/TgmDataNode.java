/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.tgmfilesupport;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.glotaran.core.main.nodes.TimpResultsNode;
import org.glotaran.core.models.structures.TimpResultDataset;
import org.glotaran.core.models.tgm.KinPar;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.PasteType;

public class TgmDataNode extends DataNode implements Node.Cookie, Transferable {

    private final Image ICON_ROOT = ImageUtilities.loadImage("org/glotaran/tgmfilesupport/resources/Model-icon-16.png", true);
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(TgmDataNode.class, "TgmDataNode");
    private TgmDataObject obj;

    public TgmDataNode(TgmDataObject obj) {
        super(obj, Children.LEAF);
        this.obj = obj;
        //setIconBaseWithExtension(IMAGE_ICON_BASE);
        //obj.ic.add(this);
    }

    TgmDataNode(TgmDataObject obj, Lookup lookup) {
        super(obj, Children.LEAF, lookup);
        this.obj = obj;
        //setIconBaseWithExtension(IMAGE_ICON_BASE);
        //obj.ic.add(this);
    }

    @Override
    public Image getIcon(int type) {
        return ICON_ROOT;
    }

    /** Creates a property sheet. */
//    @Override
//    protected Sheet createSheet() {
//        Sheet s = super.createSheet();
//        Sheet.Set ss = s.get(Sheet.PROPERTIES);
//        if (ss == null) {
//            ss = Sheet.createPropertiesSet();
//            s.put(ss);
//        }
//        // TODO add some relevant properties: ss.put(...)
//        return s;
//    }
    //Aditonal code added for TGM Project Support:
    private FileObject getFile() {
        return getDataObject().getPrimaryFile();
    }

    public TgmDataObject getObject() {
        return obj;
    }

    private Object getFromProject(Class clazz) {
        // TODO: fix unchecked conversion here
        Object result;
        Project p = FileOwnerQuery.getOwner(getFile());
        if (p != null) {
            result = p.getLookup().lookup(clazz);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public PasteType getDropType(final Transferable arg0, final int arg1, int arg2) {
        if (arg0.isDataFlavorSupported(TimpResultsNode.DATA_FLAVOR)) {
            return new PasteType() {

                @Override
                public Transferable paste() throws IOException {
                    try {
                        TimpResultDataset results = ((TimpResultsNode) arg0.getTransferData(TimpResultsNode.DATA_FLAVOR)).getObject().getTimpResultDataset();
                        final UpdateModelParameters updParamPanel = new UpdateModelParameters();
                        NotifyDescriptor detParamToUpdateDialog = new NotifyDescriptor(
                                updParamPanel,
                                NbBundle.getBundle("org/glotaran/tgmfilesupport/Bundle").getString("selParamForUpdate"),
                                NotifyDescriptor.OK_CANCEL_OPTION,
                                NotifyDescriptor.PLAIN_MESSAGE,
                                null,
                                NotifyDescriptor.OK_OPTION);

                        if (DialogDisplayer.getDefault().notify(detParamToUpdateDialog).equals(NotifyDescriptor.OK_OPTION)) {
//update kinpar if necessary, new model will have same number of kinpars as result object;
                            if (updParamPanel.isKinParSelected()) {
                                if (obj.tgm.getDat().getKinparPanel().getKinpar().size() == results.getKineticParameters().length / 2) {
                                    for (int i = 0; i < results.getKineticParameters().length / 2; i++) {
                                        obj.tgm.getDat().getKinparPanel().getKinpar().get(i).setStart(results.getKineticParameters()[i]);
                                    }
                                } else {
                                    obj.tgm.getDat().getKinparPanel().getKinpar().clear();
                                    for (int i = 0; i < results.getKineticParameters().length / 2; i++) {
                                        KinPar kp = new KinPar();
                                        kp.setStart((Double) results.getKineticParameters()[i]);
                                        kp.setFixed(false);
                                        kp.setConstrained(false);
                                        kp.setMin(new Double(0));
                                        kp.setMax(new Double(0));
                                        obj.tgm.getDat().getKinparPanel().getKinpar().add(kp);
                                    }
                                }
                            }

//update irfpar if necessary, new model will have same number of irfpar as result object;
                            if (updParamPanel.isIrfParSelected()) {
                                if (obj.tgm.getDat().getIrfparPanel().getIrf().size() == results.getIrfpar().length / 2) {
                                    for (int i = 0; i < results.getIrfpar().length / 2; i++) {
                                        obj.tgm.getDat().getIrfparPanel().getIrf().set(i, results.getIrfpar()[i]);
                                    }
                                } else {
                                    obj.tgm.getDat().getIrfparPanel().getIrf().clear();
                                    for (int i = 0; i < results.getIrfpar().length / 2; i++) {
                                        obj.tgm.getDat().getIrfparPanel().getIrf().add(results.getIrfpar()[i]);
                                        obj.tgm.getDat().getIrfparPanel().getFixed().add(false);
                                    }
                                }
                            }

//update parmu if necessary, new model will have same parmu as result object;
                            if (updParamPanel.isParMuSelected()) {
                                
                                String parmuStr = "";
                                obj.tgm.getDat().getIrfparPanel().getParmulist().clear();
                                for (int i = 0; i < results.getParmu().length / 2; i++) {
                                    if (i > 0) {
                                        parmuStr = parmuStr + ",";
                                    }
                                    parmuStr = parmuStr + String.valueOf(results.getParmu()[i]);
                                    obj.tgm.getDat().getIrfparPanel().getParmulist().add(results.getParmu()[i]);
                                }
                                obj.tgm.getDat().getIrfparPanel().setParmu(parmuStr);
                                obj.tgm.getDat().getIrfparPanel().setLamda(results.getLamdac());
                                obj.tgm.getDat().getIrfparPanel().setDispmufun("poly");
                            }

//update partau if necessary, new model will have same parmu as result object;
//TODO IMPLEMENT IT
//                                if (updParamPanel.isjParTauSelected()){
//                                    String parmuStr = "";
//                                    for (int i = 0; i < results.get().length; i++){
//                                        if (i>0)
//                                            parmuStr = parmuStr + ",";
//                                        parmuStr = parmuStr+String.valueOf(results.getParmu()[i]);
//                                    }
//                                    obj.tgm.getDat().getIrfparPanel().setParmu(parmuStr);
//                                }


                            obj.setModified(true);
                        }
                    } catch (UnsupportedFlavorException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    return null;
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public Transferable drag() {
        return (Transferable) (this);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return (new DataFlavor[]{DATA_FLAVOR});
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor == DATA_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor == DATA_FLAVOR) {
            return (this);
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
