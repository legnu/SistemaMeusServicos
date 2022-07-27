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
import br.com.MeusServicos.dal.model;
import static br.com.MeusServicos.telas.PontoDeVendas.lblUsuarioPDV;
import static br.com.MeusServicos.telas.TelaPrincipal.lblUsuario;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Ad3ln0r
 */
public class TelaVale extends javax.swing.JFrame {

    /**
     * Creates new form TelaVale
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String Senha;

    public TelaVale() {
        initComponents();
        conexao = ModuloConexao.conector();
        model m = new model();
        m.InserirIcone(this);
    }

    public void CriarSenha() {
        try {

            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-07");
            Senha = df.format(d).replace("-", "").replace("1", "G").replace("2", "Q").replace("3", "Z").replace("4", "W").replace("5", "S").replace("6", "N").replace("7", "B").replace("8", "L").replace("9", "T").replace("0", "H");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void primeiraConta() {
        try {
            CriarSenha();

            Date data = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            String mes = new SimpleDateFormat("MM").format(data);
            String ano = new SimpleDateFormat("yyyy").format(data);

            int limite = Integer.parseInt(mes) + 1;

            if (limite > 12) {
                limite = limite - 12;
                ano = String.valueOf(Integer.parseInt(ano) + 1);
            }

            String dataLimite = ano + "-" + limite + "-07";

            Date d = df.parse(dataLimite);
            java.sql.Date dSqt = new java.sql.Date(d.getTime());
            df.format(dSqt);

            String sql = "insert into tbVale(senha, vencimento)values(?,?)";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, Senha);
            pst.setDate(2, dSqt);

            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void AtualizarDados() {
        try {
            Date data = new Date();
            String sqo = "select vencimento from tbVale where idVale=1";
            pst = conexao.prepareStatement(sqo);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            

            if (data.after(df.parse(tbAuxilio.getModel().getValueAt(0, 0).toString())) == true) {

                CriarSenha();

                String mes = new SimpleDateFormat("MM").format(data);
                String ano = new SimpleDateFormat("yyyy").format(data);

                int limite = Integer.parseInt(mes) + 1;

                if (limite > 12) {
                    limite = limite - 12;
                    ano = String.valueOf(Integer.parseInt(ano) + 1);
                }

                String dataLimite = ano + "-" + limite + "-07";

                Date d = df.parse(dataLimite);
                java.sql.Date dSqt = new java.sql.Date(d.getTime());
                df.format(dSqt);
                
                String sql = "update tbVale set senha=?, vencimento=? where idVale=1";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, Senha);
                pst.setDate(2, dSqt);

                pst.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void VerificarSenha() {
        try {
            Date data = new Date();
            DateFormat Dia = new SimpleDateFormat("dd");
            AtualizarDados();

            String sqy = "select senha from tbVale where idVale=1";
            pst = conexao.prepareStatement(sqy);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            //tbauxilio.getModel().getValueAt(i, 2).toString()
            if (txtSenha.getText().equals(tbAuxilio.getModel().getValueAt(0, 0).toString()) == true) {             
                
                if(Dia.format(data).equals("04") == true){
                    JOptionPane.showMessageDialog(null, "Antepenúltimo dia para vencimento da Senha Mensal.");
                }
                if(Dia.format(data).equals("05") == true){
                    JOptionPane.showMessageDialog(null, "Penúltimo dia para vencimento da Senha Mensal.");
                }
                if(Dia.format(data).equals("06") == true){
                    JOptionPane.showMessageDialog(null, "Último dia para vencimento da Senha Mensal.");
                }
                
                TelaLogin login = new TelaLogin();
                login.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Senha Invalida entre em contato com 31 98235-2599.");
            }

        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            primeiraConta();

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

        jScrollPane1 = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtSenha = new javax.swing.JTextField();
        btnEntrar = new javax.swing.JToggleButton();

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
        jScrollPane1.setViewportView(tbAuxilio);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Senha do Mes");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Senha do Mes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        btnEntrar.setBackground(new java.awt.Color(0, 204, 51));
        btnEntrar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnEntrar.setForeground(new java.awt.Color(255, 255, 255));
        btnEntrar.setText("Entrar");
        btnEntrar.setBorderPainted(false);
        btnEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEntrar))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEntrar))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        CriarSenha();
    }//GEN-LAST:event_formWindowOpened

    private void btnEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrarActionPerformed
        // TODO add your handling code here:
        VerificarSenha();
    }//GEN-LAST:event_btnEntrarActionPerformed

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
            java.util.logging.Logger.getLogger(TelaVale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaVale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaVale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaVale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaVale().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnEntrar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTextField txtSenha;
    // End of variables declaration//GEN-END:variables
}
