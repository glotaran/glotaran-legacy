package org.glotaran.tgmfilesupport;

/**
 *
 * @author slapten
 */
public class UpdateModelParameters extends javax.swing.JPanel {

    /** Creates new form UpdateModelParameters */
    public UpdateModelParameters() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jCBIrfPar = new javax.swing.JCheckBox();
        jCBKinPar = new javax.swing.JCheckBox();
        jCBParMu = new javax.swing.JCheckBox();
        jCBSpecPar = new javax.swing.JCheckBox();
        jCBParTau = new javax.swing.JCheckBox();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jLabel1.text")); // NOI18N

        jCBIrfPar.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBIrfPar.text")); // NOI18N

        jCBKinPar.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBKinPar.text")); // NOI18N

        jCBParMu.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBParMu.text")); // NOI18N

        jCBSpecPar.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBSpecPar.text")); // NOI18N
        jCBSpecPar.setEnabled(false);

        jCBParTau.setText(org.openide.util.NbBundle.getMessage(UpdateModelParameters.class, "UpdateModelParameters.jCBParTau.text")); // NOI18N
        jCBParTau.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(8, 8, 8).addComponent(jLabel1)).addGroup(layout.createSequentialGroup().addGap(76, 76, 76).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jCBIrfPar).addComponent(jCBParTau).addComponent(jCBSpecPar).addComponent(jCBParMu).addComponent(jCBKinPar)))).addContainerGap(51, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jCBKinPar).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCBIrfPar).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCBParMu).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCBParTau).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCBSpecPar).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JCheckBox jCBParMu;
    private javax.swing.JCheckBox jCBParTau;
    private javax.swing.JCheckBox jCBSpecPar;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
