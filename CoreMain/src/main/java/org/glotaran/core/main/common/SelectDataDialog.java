/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * selectDataDialog.java
 *
 * Created on May 18, 2010, 3:44:50 PM
 */

package org.glotaran.core.main.common;

/**
 *
 * @author jsg210
 */
public class SelectDataDialog extends javax.swing.JPanel {

    /** Creates new form selectDataDialog */
    public SelectDataDialog() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPDim1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTFDim1From = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFDim1To = new javax.swing.JTextField();
        jPDim2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTFDim2From = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFDim2To = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        jPDim1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jPDim1.border.title"))); // NOI18N
        jPDim1.setMinimumSize(new java.awt.Dimension(140, 70));
        jPDim1.setPreferredSize(new java.awt.Dimension(150, 70));
        jPDim1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 0);
        jPDim1.add(jLabel1, gridBagConstraints);

        jTFDim1From.setText(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jTFDim1From.text")); // NOI18N
        jTFDim1From.setPreferredSize(new java.awt.Dimension(20, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 125;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 1, 0);
        jPDim1.add(jTFDim1From, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        jPDim1.add(jLabel2, gridBagConstraints);

        jTFDim1To.setText(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jTFDim1To.text")); // NOI18N
        jTFDim1To.setPreferredSize(new java.awt.Dimension(20, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 125;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 0, 0);
        jPDim1.add(jTFDim1To, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jPDim1, gridBagConstraints);

        jPDim2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jPDim2.border.title"))); // NOI18N
        jPDim2.setMinimumSize(new java.awt.Dimension(140, 70));
        jPDim2.setPreferredSize(new java.awt.Dimension(150, 70));
        jPDim2.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPDim2.add(jLabel3, gridBagConstraints);

        jTFDim2From.setText(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jTFDim2From.text")); // NOI18N
        jTFDim2From.setPreferredSize(new java.awt.Dimension(20, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 125;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 1, 0);
        jPDim2.add(jTFDim2From, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPDim2.add(jLabel4, gridBagConstraints);

        jTFDim2To.setText(org.openide.util.NbBundle.getMessage(SelectDataDialog.class, "SelectDataDialog.jTFDim2To.text")); // NOI18N
        jTFDim2To.setPreferredSize(new java.awt.Dimension(20, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 125;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 0, 0);
        jPDim2.add(jTFDim2To, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jPDim2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPDim1;
    private javax.swing.JPanel jPDim2;
    private javax.swing.JTextField jTFDim1From;
    private javax.swing.JTextField jTFDim1To;
    private javax.swing.JTextField jTFDim2From;
    private javax.swing.JTextField jTFDim2To;
    // End of variables declaration//GEN-END:variables

    public double getDim1From(){
        return Double.parseDouble(jTFDim1From.getText());
    }
    public double getDim1To(){
        return Double.parseDouble(jTFDim1To.getText());
    }
    public double getDim2From(){
        return Double.parseDouble(jTFDim2From.getText());
    }
    public double getDim2To(){
        return Double.parseDouble(jTFDim2To.getText());
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (int i = 0; i < getComponentCount(); i++) {
            getComponent(i).setEnabled(enabled);
        }
        for (int i = 0; i < jPDim1.getComponentCount(); i++) {
            jPDim1.getComponent(i).setEnabled(enabled);
        }
        for (int i = 0; i < jPDim2.getComponentCount(); i++) {
            jPDim2.getComponent(i).setEnabled(enabled);
        }
    }
}
