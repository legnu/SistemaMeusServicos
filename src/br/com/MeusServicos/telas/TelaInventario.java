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
public class TelaInventario extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaInventario() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
  

    public void instanciarTabelaCompra() {
        try {
            String sql = "select idproduto as ID, produto as Produto, valor_compra as Valor_de_Compra, quantidade as Quantidade, referencial_compra as Valor_Total from tbprodutos";

            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbProdutos.setModel(DbUtils.resultSetToTableModel(rs));

            String sq0 = "select sum(referencial_compra) from tbprodutos";

            pst = conexao.prepareStatement(sq0);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            lblValor.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString())).replace(",", "."));
            lblNome.setText("Total Compras(R$):");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaVenda() {
        String sql = "select idproduto as ID, produto as Produto, valor_venda as Valor_de_Venda, quantidade as Quantidade, referencial_venda as Valor_Total from tbprodutos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbProdutos.setModel(DbUtils.resultSetToTableModel(rs));

            String sq0 = "select sum(referencial_venda) from tbprodutos";

            pst = conexao.prepareStatement(sq0);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            lblValor.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString())).replace(",", "."));
            lblNome.setText("Total Vendas(R$):");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaCompra_X_Venda() {      

        try {
            
           String squ = "select idproduto as ID, produto as Produto, quantidade as Quantidade,"
                    + " referencial_compra as Referencial_Compra, referencial_venda as Referencial_Venda, compra_x_venda as Lucratividade from tbprodutos";

            pst = conexao.prepareStatement(squ);
            rs = pst.executeQuery();
            tbProdutos.setModel(DbUtils.resultSetToTableModel(rs));
            
            calculoLucro();
            
            String sqo = "select idproduto as ID, produto as Produto, quantidade as Quantidade,"
                    + " referencial_compra as Referencial_Compra, referencial_venda as Referencial_Venda, compra_x_venda as Lucratividade from tbprodutos";

            pst = conexao.prepareStatement(sqo);
            rs = pst.executeQuery();
            tbProdutos.setModel(DbUtils.resultSetToTableModel(rs));
            
            String sqz = "select sum(compra_x_venda) from tbprodutos";

            pst = conexao.prepareStatement(sqz);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            lblValor.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString())).replace(",", "."));
            lblNome.setText("Lucro Bruto(R$):");
            

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void calculoLucro() {

        try {
            
            int linha;
            linha = tbProdutos.getRowCount();
            double x;
            double y;
            double z;
            int t = 1;
            
            for (int i = 0; i <= linha; i++) {
                x = Double.parseDouble(tbProdutos.getModel().getValueAt(i, 3).toString());
                y = Double.parseDouble(tbProdutos.getModel().getValueAt(i, 4).toString());
                z = y - x;
                

                String sqr = "update tbprodutos set compra_x_venda=? where idproduto=?";

                pst = conexao.prepareStatement(sqr);
                pst.setDouble(1, z);
                pst.setInt(2, t);
                pst.executeUpdate();
                t++;

            }

        } catch (Exception sqlEx) {

        }
    }
    
     private void tirar() {
        
            String sql = "alter table tbprodutos drop compra_x_venda";
            try {
                pst = conexao.prepareStatement(sql);                
                pst.executeUpdate();
               
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            
            
        
    }
    
    private void criar(){
        String sql = "alter table tbprodutos add compra_x_venda double";
            try {
                pst = conexao.prepareStatement(sql);                
                pst.executeUpdate();
               
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
        scProdutos = new javax.swing.JScrollPane();
        tbProdutos = new javax.swing.JTable();
        rbReferencialCompra = new javax.swing.JRadioButton();
        rbReferrencialVenda = new javax.swing.JRadioButton();
        rbVendaCompra = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        lblNome = new javax.swing.JLabel();
        lblValor = new javax.swing.JLabel();

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        scProdutos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Produtos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        tbProdutos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbProdutos.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        tbProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbProdutos.setFocusable(false);
        tbProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProdutosMouseClicked(evt);
            }
        });
        scProdutos.setViewportView(tbProdutos);

        grupo1.add(rbReferencialCompra);
        rbReferencialCompra.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbReferencialCompra.setText("Referencial P.Compra");
        rbReferencialCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbReferencialCompraActionPerformed(evt);
            }
        });

        grupo1.add(rbReferrencialVenda);
        rbReferrencialVenda.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbReferrencialVenda.setText("Referencial P.Venda");
        rbReferrencialVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbReferrencialVendaActionPerformed(evt);
            }
        });

        grupo1.add(rbVendaCompra);
        rbVendaCompra.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbVendaCompra.setText("Venda X Compra");
        rbVendaCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbVendaCompraActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setText("Pesquisar:");

        lblNome.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblNome.setText("Total(R$):");

        lblValor.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblValor.setText("0.00");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scProdutos, javax.swing.GroupLayout.DEFAULT_SIZE, 1206, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rbReferencialCompra)
                                .addGap(18, 18, 18)
                                .addComponent(rbReferrencialVenda)
                                .addGap(18, 18, 18)
                                .addComponent(rbVendaCompra))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblValor)
                .addGap(60, 60, 60))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbReferencialCompra)
                    .addComponent(rbReferrencialVenda)
                    .addComponent(rbVendaCompra))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scProdutos, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome)
                    .addComponent(lblValor))
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
       tirar();
       criar();
    }//GEN-LAST:event_formWindowActivated

    private void tbProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProdutosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbProdutosMouseClicked

    private void rbReferencialCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbReferencialCompraActionPerformed
        // TODO add your handling code here:
        instanciarTabelaCompra();
    }//GEN-LAST:event_rbReferencialCompraActionPerformed

    private void rbReferrencialVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbReferrencialVendaActionPerformed
        // TODO add your handling code here:
        instanciarTabelaVenda();
    }//GEN-LAST:event_rbReferrencialVendaActionPerformed

    private void rbVendaCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbVendaCompraActionPerformed
        // TODO add your handling code here:
        instanciarTabelaCompra_X_Venda();
    }//GEN-LAST:event_rbVendaCompraActionPerformed

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
            java.util.logging.Logger.getLogger(TelaInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaInventario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup grupo1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblValor;
    private javax.swing.JRadioButton rbReferencialCompra;
    private javax.swing.JRadioButton rbReferrencialVenda;
    private javax.swing.JRadioButton rbVendaCompra;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scProdutos;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbProdutos;
    // End of variables declaration//GEN-END:variables
}
