/*
 * The MIT License
 *
 * Copyright 2022 Ad3ln0r.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.MeusServicos.telas;

import br.com.MeusServicos.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Ad3ln0r
 */
public class telaSangria extends javax.swing.JFrame {

    /**
     * Creates new form telaSangria
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public telaSangria() {
        initComponents();
        conexao = ModuloConexao.conector();

    }

    private void adicionar() {

        String sql = "insert into tbgastos(nome, data_pagamento, status_pagamento, valor, obs)values(?,?,?,?,?)";

        try {
            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);
            pst = conexao.prepareStatement(sql);

            pst.setString(1, "Sangria");
            pst.setDate(2, dSql);
            pst.setString(3, "Pago");
            pst.setString(4, txtSuplemento.getText());
            pst.setString(5, taDescricao.getText());

            //Validação dos Campos Obrigatorios
            if ((txtSuplemento.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Adicione uma Sangria.");

            } else if (Integer.parseInt(txtSuplemento.getText()) <= 0) {
                JOptionPane.showMessageDialog(null, "Adicione uma Sangria maior que 0.");

            } else {

                //A linha abaixo atualiza os dados do novo usuario
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Produto adicionado com sucesso");
                    this.dispose();

                }
            }

        } catch (java.lang.NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Somente Numeros.");

        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnPrincipalSangria = new javax.swing.JPanel();
        btnAdicionarSangria = new javax.swing.JToggleButton();
        lblSangria = new javax.swing.JLabel();
        txtSangria = new javax.swing.JTextField();
        pnDescricaoSangria = new javax.swing.JPanel();
        scDescricaoSangria = new javax.swing.JScrollPane();
        taDescricaoSangria = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnPrincipalSangria.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnAdicionarSangria.setBackground(new java.awt.Color(51, 255, 0));
        btnAdicionarSangria.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        btnAdicionarSangria.setForeground(new java.awt.Color(255, 255, 255));
        btnAdicionarSangria.setText("Adicionar");
        btnAdicionarSangria.setBorderPainted(false);
        btnAdicionarSangria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarSangriaActionPerformed(evt);
            }
        });

        lblSangria.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblSangria.setText("Valor Sangria:");

        pnDescricaoSangria.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OBS:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        taDescricaoSangria.setColumns(20);
        taDescricaoSangria.setRows(5);
        scDescricaoSangria.setViewportView(taDescricaoSangria);

        javax.swing.GroupLayout pnDescricaoSangriaLayout = new javax.swing.GroupLayout(pnDescricaoSangria);
        pnDescricaoSangria.setLayout(pnDescricaoSangriaLayout);
        pnDescricaoSangriaLayout.setHorizontalGroup(
            pnDescricaoSangriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scDescricaoSangria, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        pnDescricaoSangriaLayout.setVerticalGroup(
            pnDescricaoSangriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scDescricaoSangria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnPrincipalSangriaLayout = new javax.swing.GroupLayout(pnPrincipalSangria);
        pnPrincipalSangria.setLayout(pnPrincipalSangriaLayout);
        pnPrincipalSangriaLayout.setHorizontalGroup(
            pnPrincipalSangriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPrincipalSangriaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnPrincipalSangriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnPrincipalSangriaLayout.createSequentialGroup()
                        .addComponent(lblSangria)
                        .addGap(11, 11, 11)
                        .addComponent(txtSangria, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnDescricaoSangria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdicionarSangria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnPrincipalSangriaLayout.setVerticalGroup(
            pnPrincipalSangriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPrincipalSangriaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnPrincipalSangriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSangria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSangria))
                .addGap(16, 16, 16)
                .addComponent(pnDescricaoSangria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(btnAdicionarSangria, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPrincipalSangria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPrincipalSangria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarSangriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarSangriaActionPerformed
        // TODO add your handling code here:
        adicionar();
    }//GEN-LAST:event_btnAdicionarSangriaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(telaSangria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(telaSangria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(telaSangria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(telaSangria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new telaSangria().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnAdicionar;
    private javax.swing.JToggleButton btnAdicionar1;
    private javax.swing.JToggleButton btnAdicionarSangria;
    private javax.swing.JLabel lblSangria;
    private javax.swing.JLabel lblSupleento;
    private javax.swing.JLabel lblSupleento1;
    private javax.swing.JPanel pnDescricao;
    private javax.swing.JPanel pnDescricao1;
    private javax.swing.JPanel pnDescricaoSangria;
    private javax.swing.JPanel pnPrincipal;
    private javax.swing.JPanel pnPrincipal1;
    private javax.swing.JPanel pnPrincipalSangria;
    private javax.swing.JScrollPane scDescricao;
    private javax.swing.JScrollPane scDescricao1;
    private javax.swing.JScrollPane scDescricaoSangria;
    private javax.swing.JTextArea taDescricao;
    private javax.swing.JTextArea taDescricao1;
    private javax.swing.JTextArea taDescricaoSangria;
    private javax.swing.JTextField txtSangria;
    private javax.swing.JTextField txtSuplemento;
    private javax.swing.JTextField txtSuplemento1;
    // End of variables declaration//GEN-END:variables
}