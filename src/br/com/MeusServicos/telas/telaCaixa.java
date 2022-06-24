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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Ad3ln0r
 */
public class telaCaixa extends javax.swing.JFrame {

    /**
     * Creates new form telaCaixa
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public telaCaixa() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    public void instanciarTabela() {
        try {
            String sql = "select * from tbgastos";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

            double caixa = Double.parseDouble(tbAuxilioRecebido.getModel().getValueAt(0, 0).toString()) - Double.parseDouble(tbAuxilioPago.getModel().getValueAt(0, 0).toString());
            lblTotalCaixa.setText(new DecimalFormat("#,##0.00").format(caixa).replace(",", "."));

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {

        }
    }

    public void Status() {

        instanciarTabela();

        try {
            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);

            String x;

            for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                x = tbAuxilio.getModel().getValueAt(i, 3).toString();
                Date data = df.parse(x);
                System.out.println();

                if (d.after(data) || d.equals(data)) {
                    String sqo = "update tbgastos set status_pagamento='Pago' where idgastos=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                } else if (d.before(data)) {
                    String sqr = "update tbgastos set status_pagamento='Pendente' where idgastos=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                }

            }

            instanciarTabela();

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaVendas() {

        try {

            String sql = "select id as ID, nome as Nome_Dia, hora as Hora, venda as Vendas from tbtotalvendas";
            pst = conexao.prepareStatement(sql);

            rs = pst.executeQuery();
            tbCaixaRecebido.setModel(DbUtils.resultSetToTableModel(rs));

            String sq0 = "select sum(venda) from tbtotalvendas";

            pst = conexao.prepareStatement(sq0);
            rs = pst.executeQuery();
            tbAuxilioRecebido.setModel(DbUtils.resultSetToTableModel(rs));
            lblTotalRecebido.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(tbAuxilioRecebido.getModel().getValueAt(0, 0).toString())).replace(",", "."));

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void instanciarTabelaGasto() {

        try {
            Status();

            String sql = "select idgastos as ID, nome as Nome_Dia, data_pagamento as Data_Pagamento, valor  as Valor from tbgastos where status_pagamento=?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, "Pago");
            rs = pst.executeQuery();
            tbCaixaPago.setModel(DbUtils.resultSetToTableModel(rs));

            String sqr = "select sum(valor) from tbgastos where status_pagamento=?";

            pst = conexao.prepareStatement(sqr);
            pst.setString(1, "Pago");
            rs = pst.executeQuery();
            tbAuxilioPago.setModel(DbUtils.resultSetToTableModel(rs));
            lblTotalPago.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(tbAuxilioPago.getModel().getValueAt(0, 0).toString())).replace(",", "."));

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
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

        grupo1 = new javax.swing.ButtonGroup();
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        scAuxilioPago = new javax.swing.JScrollPane();
        tbAuxilioPago = new javax.swing.JTable();
        scAuxilioRecebido = new javax.swing.JScrollPane();
        tbAuxilioRecebido = new javax.swing.JTable();
        pnRelatorio = new javax.swing.JPanel();
        lblNome1 = new javax.swing.JLabel();
        lblTotalCaixa = new javax.swing.JLabel();
        lblTotalRecebido = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        lblNome2 = new javax.swing.JLabel();
        lblTotalPago = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        pnPago = new javax.swing.JPanel();
        scCaixaPago = new javax.swing.JScrollPane();
        tbCaixaPago = new javax.swing.JTable();
        pnRecebido = new javax.swing.JPanel();
        scCaixaRecebido = new javax.swing.JScrollPane();
        tbCaixaRecebido = new javax.swing.JTable();

        tbAuxilio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scAuxilio.setViewportView(tbAuxilio);

        tbAuxilioPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scAuxilioPago.setViewportView(tbAuxilioPago);

        tbAuxilioRecebido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scAuxilioRecebido.setViewportView(tbAuxilioRecebido);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        pnRelatorio.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Total", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        lblNome1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblNome1.setForeground(new java.awt.Color(0, 0, 204));
        lblNome1.setText("Valor em Caixa:");

        lblTotalCaixa.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTotalCaixa.setForeground(new java.awt.Color(0, 0, 204));
        lblTotalCaixa.setText("0.00");

        lblTotalRecebido.setBackground(new java.awt.Color(51, 255, 0));
        lblTotalRecebido.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTotalRecebido.setForeground(new java.awt.Color(51, 255, 0));
        lblTotalRecebido.setText("0.00");

        lblNome.setBackground(new java.awt.Color(51, 255, 0));
        lblNome.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblNome.setForeground(new java.awt.Color(51, 255, 0));
        lblNome.setText("Valor Recebido:");

        lblNome2.setBackground(new java.awt.Color(51, 255, 0));
        lblNome2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblNome2.setForeground(new java.awt.Color(255, 0, 0));
        lblNome2.setText("Valor Pago:");

        lblTotalPago.setBackground(new java.awt.Color(51, 255, 0));
        lblTotalPago.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTotalPago.setForeground(new java.awt.Color(255, 0, 0));
        lblTotalPago.setText("0.00");

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/Logo_200x164.png"))); // NOI18N
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);

        javax.swing.GroupLayout pnRelatorioLayout = new javax.swing.GroupLayout(pnRelatorio);
        pnRelatorio.setLayout(pnRelatorioLayout);
        pnRelatorioLayout.setHorizontalGroup(
            pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRelatorioLayout.createSequentialGroup()
                .addGroup(pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnRelatorioLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnRelatorioLayout.createSequentialGroup()
                                .addComponent(lblNome1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblTotalCaixa))
                            .addGroup(pnRelatorioLayout.createSequentialGroup()
                                .addComponent(lblNome2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblTotalPago))))
                    .addGroup(pnRelatorioLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalRecebido)))
                .addContainerGap(67, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRelatorioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnRelatorioLayout.setVerticalGroup(
            pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRelatorioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 198, Short.MAX_VALUE)
                .addGroup(pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome)
                    .addComponent(lblTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNome2)
                    .addComponent(lblTotalPago, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome1)
                    .addComponent(lblTotalCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );

        pnPago.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Valor_Pago", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbCaixaPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbCaixaPago.setFocusable(false);
        tbCaixaPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCaixaPagoMouseClicked(evt);
            }
        });
        scCaixaPago.setViewportView(tbCaixaPago);

        javax.swing.GroupLayout pnPagoLayout = new javax.swing.GroupLayout(pnPago);
        pnPago.setLayout(pnPagoLayout);
        pnPagoLayout.setHorizontalGroup(
            pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scCaixaPago, javax.swing.GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnPagoLayout.setVerticalGroup(
            pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scCaixaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnRecebido.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Valor_Recebido", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbCaixaRecebido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbCaixaRecebido.setFocusable(false);
        tbCaixaRecebido.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCaixaRecebidoMouseClicked(evt);
            }
        });
        scCaixaRecebido.setViewportView(tbCaixaRecebido);

        javax.swing.GroupLayout pnRecebidoLayout = new javax.swing.GroupLayout(pnRecebido);
        pnRecebido.setLayout(pnRecebidoLayout);
        pnRecebidoLayout.setHorizontalGroup(
            pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRecebidoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scCaixaRecebido)
                .addContainerGap())
        );
        pnRecebidoLayout.setVerticalGroup(
            pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRecebidoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scCaixaRecebido, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnRecebido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(pnRelatorio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnRelatorio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        instanciarTabelaGasto();
        instanciarTabelaVendas();
        instanciarTabela();

    }//GEN-LAST:event_formWindowActivated

    private void tbCaixaPagoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCaixaPagoMouseClicked
        // TODO add your handling code here:


    }//GEN-LAST:event_tbCaixaPagoMouseClicked

    private void tbCaixaRecebidoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCaixaRecebidoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbCaixaRecebidoMouseClicked

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
            java.util.logging.Logger.getLogger(telaCaixa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(telaCaixa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(telaCaixa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(telaCaixa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new telaCaixa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup grupo1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblNome1;
    private javax.swing.JLabel lblNome2;
    private javax.swing.JLabel lblTotalCaixa;
    private javax.swing.JLabel lblTotalPago;
    private javax.swing.JLabel lblTotalRecebido;
    private javax.swing.JPanel pnPago;
    private javax.swing.JPanel pnRecebido;
    private javax.swing.JPanel pnRelatorio;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilioPago;
    private javax.swing.JScrollPane scAuxilioRecebido;
    private javax.swing.JScrollPane scCaixaPago;
    private javax.swing.JScrollPane scCaixaRecebido;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilioPago;
    private javax.swing.JTable tbAuxilioRecebido;
    private javax.swing.JTable tbCaixaPago;
    private javax.swing.JTable tbCaixaRecebido;
    // End of variables declaration//GEN-END:variables
}
