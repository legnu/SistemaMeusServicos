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
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Ad3ln0r
 */
public class conCaixa extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    public conCaixa() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
     public void instanciarTabela() {
        String sql = "select id as Id, dia as Data, hora as Hora, venda as Valor from tbtotalvendas";
        try {
            pst = conexao.prepareStatement(sql);            
            rs = pst.executeQuery();
            tbConCaixa.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
     
    public void criar_relatorio() {

        try {

            int linha;
            linha = tbConCaixa.getRowCount();
            double x = 0;
            double y = 0;
            double z;

            for (int i = 0; i <= linha; i++) {
                x = Double.parseDouble(tbConCaixa.getModel().getValueAt(i, 3).toString());
                y = y+x;
                z = y;
                lblValorFinal.setText(new DecimalFormat("#,##0.00").format(z).replace(",", "."));
            }
             
            
           
        } catch (Exception sqlEx) {

        }
    }
     
    public void setar_campos() {
        int setar = tbConCaixa.getSelectedRow();
        txtNomeVenda.setText(tbConCaixa.getModel().getValueAt(setar, 0).toString());
        
        String sql = "select obs from tbtotalvendas where id=?";
        try {
            pst = conexao.prepareStatement(sql);  
            pst.setString(1, txtNomeVenda.getText());
            rs = pst.executeQuery();       
            tbResultado.setModel(DbUtils.resultSetToTableModel(rs));
            taConCaixa.setText(tbResultado.getModel().getValueAt(0, 0).toString());
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

        txtNomeVenda = new javax.swing.JTextField();
        scResultado = new javax.swing.JScrollPane();
        tbResultado = new javax.swing.JTable();
        lblDataInicial = new javax.swing.JLabel();
        lblDataFinal = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JTextField();
        txtDataFinal = new javax.swing.JTextField();
        scConCaixa = new javax.swing.JScrollPane();
        tbConCaixa = new javax.swing.JTable();
        scConCaixata = new javax.swing.JScrollPane();
        taConCaixa = new javax.swing.JTextArea();
        lblValor = new javax.swing.JLabel();
        lblValorFinal = new javax.swing.JLabel();

        txtNomeVenda.setText("jTextField1");

        tbResultado.setModel(new javax.swing.table.DefaultTableModel(
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
        scResultado.setViewportView(tbResultado);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        lblDataInicial.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblDataInicial.setText("Data Inicial:");

        lblDataFinal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblDataFinal.setText("Data Final:");

        tbConCaixa = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbConCaixa.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        tbConCaixa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbConCaixa.setFocusable(false);
        tbConCaixa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbConCaixaMouseClicked(evt);
            }
        });
        scConCaixa.setViewportView(tbConCaixa);

        taConCaixa.setColumns(20);
        taConCaixa.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        taConCaixa.setRows(5);
        taConCaixa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                taConCaixaMouseClicked(evt);
            }
        });
        scConCaixata.setViewportView(taConCaixa);

        lblValor.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lblValor.setText("Valor Total:");

        lblValorFinal.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lblValorFinal.setText("0.00");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scConCaixata, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(lblDataInicial)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(lblDataFinal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(scConCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, 784, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblValor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblValorFinal)))
                        .addGap(0, 177, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDataInicial)
                    .addComponent(lblDataFinal)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scConCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblValor)
                        .addComponent(lblValorFinal)))
                .addGap(18, 18, 18)
                .addComponent(scConCaixata, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        instanciarTabela();
        criar_relatorio();
    }//GEN-LAST:event_formWindowActivated

    private void taConCaixaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_taConCaixaMouseClicked
        // TODO add your handling code here:
        setar_campos();
        
    }//GEN-LAST:event_taConCaixaMouseClicked

    private void tbConCaixaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbConCaixaMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbConCaixaMouseClicked

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
            java.util.logging.Logger.getLogger(conCaixa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(conCaixa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(conCaixa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(conCaixa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new conCaixa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDataFinal;
    private javax.swing.JLabel lblDataInicial;
    private javax.swing.JLabel lblValor;
    private javax.swing.JLabel lblValorFinal;
    private javax.swing.JScrollPane scConCaixa;
    private javax.swing.JScrollPane scConCaixata;
    private javax.swing.JScrollPane scResultado;
    private javax.swing.JTextArea taConCaixa;
    private javax.swing.JTable tbConCaixa;
    private javax.swing.JTable tbResultado;
    private javax.swing.JTextField txtDataFinal;
    private javax.swing.JTextField txtDataInicial;
    private javax.swing.JTextField txtNomeVenda;
    // End of variables declaration//GEN-END:variables
}