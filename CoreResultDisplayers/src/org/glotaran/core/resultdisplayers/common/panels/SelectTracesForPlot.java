package org.glotaran.core.resultdisplayers.common.panels;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Sergey
 */
public class SelectTracesForPlot extends java.awt.Panel {

    private int numXCh, numYCh;

    public SelectTracesForPlot() {
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTYnum = new javax.swing.JTextField();
        jTXnum = new javax.swing.JTextField();
        jCbTraceColY = new javax.swing.JCheckBox();
        jCbTraceColX = new javax.swing.JCheckBox();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SelectTracesForPlot.class, "SelectTracesForPlot.jLabel1.text")); // NOI18N
        jLabel1.setEnabled(false);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(SelectTracesForPlot.class, "SelectTracesForPlot.jLabel2.text")); // NOI18N
        jLabel2.setEnabled(false);

        jTYnum.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTYnum.setText(org.openide.util.NbBundle.getMessage(SelectTracesForPlot.class, "SelectTracesForPlot.jTYnum.text")); // NOI18N
        jTYnum.setEnabled(false);
        jTYnum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTYnumKeyReleased(evt);
            }
        });

        jTXnum.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTXnum.setText(org.openide.util.NbBundle.getMessage(SelectTracesForPlot.class, "SelectTracesForPlot.jTXnum.text")); // NOI18N
        jTXnum.setEnabled(false);
        jTXnum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTXnumKeyReleased(evt);
            }
        });

        jCbTraceColY.setText(org.openide.util.NbBundle.getMessage(SelectTracesForPlot.class, "SelectTracesForPlot.jCbTraceColY.text")); // NOI18N
        jCbTraceColY.setActionCommand(org.openide.util.NbBundle.getMessage(SelectTracesForPlot.class, "SelectTracesForPlot.jCbTraceColY.actionCommand")); // NOI18N
        jCbTraceColY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCbTraceColYActionPerformed(evt);
            }
        });

        jCbTraceColX.setText(org.openide.util.NbBundle.getMessage(SelectTracesForPlot.class, "SelectTracesForPlot.jCbTraceColX.text")); // NOI18N
        jCbTraceColX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCbTraceColXActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCbTraceColX)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTYnum, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTXnum, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE))
                    .addComponent(jCbTraceColY))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jCbTraceColX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTXnum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCbTraceColY)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTYnum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCbTraceColXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCbTraceColXActionPerformed
        jTXnum.setEnabled(jCbTraceColX.isSelected());
        jLabel2.setEnabled(jCbTraceColX.isSelected());
    }//GEN-LAST:event_jCbTraceColXActionPerformed

    private void jCbTraceColYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCbTraceColYActionPerformed
        jTYnum.setEnabled(jCbTraceColY.isSelected());
        jLabel1.setEnabled(jCbTraceColY.isSelected());
    }//GEN-LAST:event_jCbTraceColYActionPerformed

    private void jTXnumKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTXnumKeyReleased
        if (Integer.parseInt(jTXnum.getText()) > numXCh) {
            NotifyDescriptor errorMessage = new NotifyDescriptor.Exception(
                    new Exception(NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("set_correct_chanNum")
                    + NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("setLess") + String.valueOf(numXCh)));
            DialogDisplayer.getDefault().notify(errorMessage);
            jTXnum.setText(String.valueOf(numXCh));
        }
    }//GEN-LAST:event_jTXnumKeyReleased

    private void jTYnumKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTYnumKeyReleased
        if (Integer.parseInt(jTYnum.getText()) > numYCh) {
            NotifyDescriptor errorMessage = new NotifyDescriptor.Exception(
                    new Exception(NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("set_correct_chanNum")
                    + NbBundle.getBundle("org/glotaran/core/main/Bundle").getString("setLess") + String.valueOf(numYCh)));
            DialogDisplayer.getDefault().notify(errorMessage);
            jTYnum.setText(String.valueOf(numYCh));
        }

    }//GEN-LAST:event_jTYnumKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCbTraceColX;
    private javax.swing.JCheckBox jCbTraceColY;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTXnum;
    private javax.swing.JTextField jTYnum;
    // End of variables declaration//GEN-END:variables

    public void setMaxNumbers(int x, int y) {
        numXCh = x;
        numYCh = y;
    }

    public boolean getSelectXState() {
        return jCbTraceColX.isSelected();
    }

    public boolean getSelectYState() {
        return jCbTraceColY.isSelected();
    }

    public int getSelectXNum() {
        return Integer.parseInt(jTXnum.getText());
    }

    public int getSelectYNum() {
        return Integer.parseInt(jTYnum.getText());
    }

    public void setEnabledXDimension(boolean state) {
        jCbTraceColX.setEnabled(state);
    }

    public void setEnabledYDimension(boolean state) {
        jCbTraceColY.setEnabled(state);
    }
}
