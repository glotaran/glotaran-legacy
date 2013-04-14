/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.tgmeditor.panels;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import org.glotaran.core.models.tgm.KMatrixPanelModel;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;

/**
 *
 * @author slapten
 */
public class KMatrixPanel extends SectionInnerPanel {

    private TgmDataObject dObj;

    public KMatrixPanel(SectionView view, TgmDataObject dObj, KMatrixPanelModel kMatrixPanelModel) {
        super(view);
        setLayout(new BorderLayout());
        add(new KMatrixPanelForm(dObj));
        this.dObj = dObj;
    }

    @Override
    public void setValue(JComponent jc, Object o) {
         dObj.setModified(true);
    }

    public void linkButtonPressed(Object o, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JComponent getErrorComponent(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
