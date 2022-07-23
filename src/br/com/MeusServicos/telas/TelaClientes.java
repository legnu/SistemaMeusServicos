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
public class TelaClientes extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String tipo = "Ativo";

    /**
     * Creates new form TelaClientes
     */
    public TelaClientes() {
        initComponents();
        conexao = ModuloConexao.conector();

    }

    public void ClientesAtivos() {
        instanciarTabelaClientesAtivos();
        tipo = "Ativo";

    }

    public void ClientesInativos() {
        instanciarTabelaClientesInativos();
        tipo = "Inativo";

    }

    public void instanciarTabelaClientesAtivos() {
        String sql = "select nomecli as Cliente, quantidade_comprada as N_Compras, valor_gasto As Valor_Gasto, ticket_medio as Ticket_Medio from tbclientes where quantidade_comprada > 0 and atividade='Ativo' order by quantidade_comprada desc";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbClientesAtivos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void Pesquisar() {
        try {
            if (tipo.equals("Ativo") == true) {
                String sql = "select nomecli as Cliente, quantidade_comprada as N_Compras, valor_gasto As Valor_Gasto, ticket_medio as Ticket_Medio from tbclientes where quantidade_comprada > 0 and nomecli like ? and atividade='Ativo' order by quantidade_comprada desc ";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisar.getText() + "%");
                System.out.println(sql);
                rs = pst.executeQuery();
                tbClientesAtivos.setModel(DbUtils.resultSetToTableModel(rs));
                System.out.println(tipo);
            }else if(tipo.equals("Inativo") == true){
                String sql = "select nomecli as Cliente, quantidade_comprada as N_Compras, valor_gasto As Valor_Gasto, ticket_medio as Ticket_Medio from tbclientes where quantidade_comprada > 0 and nomecli like ? and atividade='Inativo'  order by quantidade_comprada desc ";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisar.getText() + "%");
                rs = pst.executeQuery();
                tbClientesAtivos.setModel(DbUtils.resultSetToTableModel(rs));
                System.out.println(tipo);
                
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void instanciarTabelaClientesInativos() {
        String sql = "select nomecli as Cliente, quantidade_comprada as N_Compras, valor_gasto As Valor_Gasto, ticket_medio as Ticket_Medio from tbclientes where quantidade_comprada > 0 and atividade='Inativo' order by quantidade_comprada desc";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbClientesAtivos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void instanciarTabelaAuxilioCliente() {
        String sql = "select idcli, valor_gasto, quantidade_comprada, ultima_compra from tbclientes where quantidade_comprada > 0";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void ValidarCliente() {
        try {

            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);

            for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                Date base = df.parse(tbAuxilio.getModel().getValueAt(i, 3).toString());

                String mes = new SimpleDateFormat("MM").format(base);
                String ano = new SimpleDateFormat("yyyy").format(base);

                int limite = Integer.parseInt(mes) + 3;

                if (limite > 12) {
                    limite = limite - 12;
                    ano = String.valueOf(Integer.parseInt(ano) + 1);
                }

                String dataLimite = ano + "-" + limite + "-01";

                Date data = df.parse(dataLimite);
                java.sql.Date dSqt = new java.sql.Date(data.getTime());
                df.format(dSqt);

                String sqr = "update tbclientes set prazo_inativo=? where idcli=?";
                pst = conexao.prepareStatement(sqr);
                pst.setDate(1, dSqt);
                pst.setString(2, tbAuxilio.getModel().getValueAt(i, 0).toString());
                pst.executeUpdate();

                if (dSql.after(dSqt) == true) {
                    String sqy = "update tbclientes set atividade='Inativo' where idcli=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();
                } else {
                    String sqy = "update tbclientes set atividade='Ativo' where idcli=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();
                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void CalcularTicket() {

        try {

            double preco, x;

            preco = 0;
            for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                x = Double.parseDouble(tbAuxilio.getModel().getValueAt(i, 1).toString().replace(".", "")) / 100;
                preco = x / Double.parseDouble(tbAuxilio.getModel().getValueAt(i, 2).toString());

                String sqr = "update tbclientes set ticket_medio=? where idcli=?";
                pst = conexao.prepareStatement(sqr);
                pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(preco))).replace(",", "."));
                pst.setString(2, tbAuxilio.getModel().getValueAt(i, 0).toString());
                pst.executeUpdate();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void Iniciar() {
        instanciarTabelaAuxilioCliente();
        ValidarCliente();
        CalcularTicket();
        instanciarTabelaClientesAtivos();
        rbAtivo.setSelected(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        Grupo1 = new javax.swing.ButtonGroup();
        pnPrincipal = new javax.swing.JPanel();
        pnClientes = new javax.swing.JPanel();
        scClientes = new javax.swing.JScrollPane();
        tbClientesAtivos = new javax.swing.JTable();
        rbAtivo = new javax.swing.JRadioButton();
        rbInativo = new javax.swing.JRadioButton();
        lblPesquisar = new javax.swing.JLabel();
        txtPesquisar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

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
        setTitle("Tela Clientes");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        pnPrincipal.setBackground(new java.awt.Color(204, 204, 204));
        pnPrincipal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        pnClientes.setBackground(new java.awt.Color(204, 204, 204));
        pnClientes.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Clientes Ativos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbClientesAtivos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbClientesAtivos.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbClientesAtivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbClientesAtivos.setFocusable(false);
        scClientes.setViewportView(tbClientesAtivos);

        rbAtivo.setBackground(new java.awt.Color(204, 204, 204));
        Grupo1.add(rbAtivo);
        rbAtivo.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbAtivo.setText("Ativos");
        rbAtivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbAtivoActionPerformed(evt);
            }
        });

        rbInativo.setBackground(new java.awt.Color(204, 204, 204));
        Grupo1.add(rbInativo);
        rbInativo.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbInativo.setText("Inativos");
        rbInativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbInativoActionPerformed(evt);
            }
        });

        lblPesquisar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisar.setText("Pesquisar:");

        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnClientesLayout = new javax.swing.GroupLayout(pnClientes);
        pnClientes.setLayout(pnClientesLayout);
        pnClientesLayout.setHorizontalGroup(
            pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
            .addGroup(pnClientesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnClientesLayout.createSequentialGroup()
                        .addComponent(rbAtivo)
                        .addGap(16, 16, 16)
                        .addComponent(rbInativo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnClientesLayout.createSequentialGroup()
                        .addComponent(lblPesquisar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPesquisar)))
                .addGap(16, 16, 16))
        );
        pnClientesLayout.setVerticalGroup(
            pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnClientesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPesquisar)
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(pnClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbAtivo)
                    .addComponent(rbInativo))
                .addGap(16, 16, 16)
                .addComponent(scClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel1.setText("OBS: Cliente só aparecera se tiver feito ao menos uma compra.");

        javax.swing.GroupLayout pnPrincipalLayout = new javax.swing.GroupLayout(pnPrincipal);
        pnPrincipal.setLayout(pnPrincipalLayout);
        pnPrincipalLayout.setHorizontalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPrincipalLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(24, 24, 24))
        );
        pnPrincipalLayout.setVerticalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPrincipalLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(pnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 627, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 599, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        Iniciar();
    }//GEN-LAST:event_formWindowActivated

    private void rbAtivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAtivoActionPerformed
        // TODO add your handling code here:
        ClientesAtivos();
    }//GEN-LAST:event_rbAtivoActionPerformed

    private void rbInativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbInativoActionPerformed
        // TODO add your handling code here:
        ClientesInativos();
    }//GEN-LAST:event_rbInativoActionPerformed

    private void txtPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyReleased
        // TODO add your handling code here:
        Pesquisar();
    }//GEN-LAST:event_txtPesquisarKeyReleased

    private void txtPesquisarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisarKeyPressed

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
            java.util.logging.Logger.getLogger(TelaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaClientes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup Grupo1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblPesquisar;
    private javax.swing.JPanel pnClientes;
    private javax.swing.JPanel pnPrincipal;
    private javax.swing.JRadioButton rbAtivo;
    private javax.swing.JRadioButton rbInativo;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scClientes;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbClientesAtivos;
    private javax.swing.JTextField txtPesquisar;
    // End of variables declaration//GEN-END:variables
}
