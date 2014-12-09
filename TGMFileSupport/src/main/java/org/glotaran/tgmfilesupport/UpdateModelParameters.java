package org.glotaran.tgmfilesupport;

import java.util.ArrayList;

/**
 *
 * @author slapten
 */
public class UpdateModelParameters extends javax.swing.JPanel {

    /** Creates new form UpdateModelParameters */
    public UpdateModelParameters(ArrayList<Boolean> paramsToCopy) {
        initComponents();
        
        
    }
   
    public UpdateModelParameters() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jCBIrfPar = new javax.swing.JCheckBox();
        jCBKinPar = new javax.swing.JCheckBox();
        jCBParMu = new javax.swing.JCheckBox();
        jCBSpecPar = new javax.swing.JCheckBox();
        jCBParTau = new javax.swing.JCheckBox();
        jCBOscilParams = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 6, 7, 6);
        add(jLabel1, gridBagConstraints);

        jCBIrfPar.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBIrfPar.text")); // NOI18N
        jCBIrfPar.setToolTipText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBIrfPar.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 35, 0, 0);
        add(jCBIrfPar, gridBagConstraints);

        jCBKinPar.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBKinPar.text")); // NOI18N
        jCBKinPar.setToolTipText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBKinPar.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 35, 0, 0);
        add(jCBKinPar, gridBagConstraints);

        jCBParMu.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBParMu.text")); // NOI18N
        jCBParMu.setToolTipText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBParMu.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 35, 0, 0);
        add(jCBParMu, gridBagConstraints);

        jCBSpecPar.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBSpecPar.text")); // NOI18N
        jCBSpecPar.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 35, 0, 0);
        add(jCBSpecPar, gridBagConstraints);

        jCBParTau.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBParTau.text")); // NOI18N
        jCBParTau.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 35, 0, 0);
        add(jCBParTau, gridBagConstraints);

        jCBOscilParams.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBOscilParams.text")); // NOI18N
        jCBOscilParams.setToolTipText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBOscilParams.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 35, 0, 0);
        add(jCBOscilParams, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    public boolean isOscParSelected(){
        return jCBOscilParams.isSelected();
    }
    
    public boolean isIrfParSelected() {
        return jCBIrfPar.isSelected();
    }

    public boolean isKinParSelected() {
        return jCBKinPar.isSelected();
    }

    public boolean isParMuSelected() {
        return jCBParMu.isSelected();
    }

    public boolean isjParTauSelected() {
        return jCBParTau.isSelected();
    }

    public boolean isSpecParSpecPar() {
        return jCBSpecPar.isSelected();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCBIrfPar;
    private javax.swing.JCheckBox jCBKinPar;
    private javax.swing.JCheckBox jCBOscilParams;
    private javax.swing.JCheckBox jCBParMu;
    private javax.swing.JCheckBox jCBParTau;
    private javax.swing.JCheckBox jCBSpecPar;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
