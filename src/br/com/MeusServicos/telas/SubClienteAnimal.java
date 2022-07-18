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
import static br.com.MeusServicos.telas.SubClientePessoa.IdCliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Ad3ln0r
 */
public class SubClienteAnimal extends javax.swing.JFrame {

    /**
     * Creates new form SubClienteAnimal
     */
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    public SubClienteAnimal() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
     private void adicionar() {
        String sql = "insert into tbsubclientes(nome,especie_marca,raca_modelo,cor,tamanho,genero,referencia,obs,tipo)values(?,?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNome.getText());
            pst.setString(2, txtEspecie.getText());
            pst.setString(3, txtRaca.getText());
            pst.setString(4, txtCor.getText());
            pst.setString(5, txtTamanho.getText());
            pst.setString(6, cbSexo.getSelectedItem().toString());
            pst.setString(7, IdCliente.getText());
            pst.setString(8, txtOBS.getText());
            pst.setString(9, "Animal");

            //Validação dos Campos Obrigatorios
            if ((txtNome.getText().isEmpty() == true )) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");
            } else {

                //A linha abaixo atualiza os dados do novo usuario
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "SubCliente adicionado com sucesso");
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void NomeReferencia() {
        try {
            String sql = "select nomecli from tbclientes where idcli=?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, IdCliente.getText());
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            NomeCliente.setText(tbAuxilio.getModel().getValueAt(0, 0).toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void instanciarTabela() {
        try {
            NomeReferencia();
            String sql = "select idsub as ID, nome as Nome from tbsubclientes where tipo='Animal' and referencia=?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, IdCliente.getText());
            rs = pst.executeQuery();
            tbSubTabela.setModel(DbUtils.resultSetToTableModel(rs));
            pnTabela.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente Referencia: " + NomeCliente.getText(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));
            instanciarTabelaAuxilio();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }
    
    public void instanciarTabelaAuxilio() {
        try {          
            String sql = "select idsub,nome,especie_marca,raca_modelo,cor,tamanho,genero,referencia,obs from tbsubclientes where tipo='Animal' and referencia=?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, IdCliente.getText());
            rs = pst.executeQuery();
            tbAuxilioSub.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }
    

    private void alterar() {
        //idsub,nome,especie_marca,raca_modelo,cor,tamanho,genero,obs
        String sql = "update tbsubclientes set nome=?,especie_marca=?,raca_modelo=?,cor=?,tamanho=?,genero=?,obs=? where idsub=?";
        try {
            pst = conexao.prepareStatement(sql);
           pst.setString(1, txtNome.getText());
            pst.setString(2, txtEspecie.getText());
            pst.setString(3, txtRaca.getText());
            pst.setString(4, txtCor.getText());
            pst.setString(5, txtTamanho.getText());
            pst.setString(6, cbSexo.getSelectedItem().toString());
            pst.setString(7, txtOBS.getText());
            pst.setString(8, ID_Sub.getText());

            //Validação dos Campos Obrigatorios
            if ((txtNome.getText().isEmpty() == true)) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");
            } else {

                //A linha abaixo atualiza os dados do novo usuario
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "SubCliente alterado com sucesso");
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este SubCliente?", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) {

            try {

                String sql = "delete from tbsubclientes where idsub=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, ID_Sub.getText());
                int apagado = pst.executeUpdate();

                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");

                    JOptionPane.showMessageDialog(null, "SubCliente removido com sucesso.");
                    limpar();

                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();
            }
        }

    }

    public void setar_campos() {
        int setar = tbSubTabela.getSelectedRow();
        //idsub,nome,especie_marca,raca_modelo,cor,tamanho,genero,obs
        ID_Sub.setText(tbAuxilioSub.getModel().getValueAt(setar, 0).toString());
        txtNome.setText(tbAuxilioSub.getModel().getValueAt(setar, 1).toString());
        txtEspecie.setText(tbAuxilioSub.getModel().getValueAt(setar, 2).toString());
        txtRaca.setText(tbAuxilioSub.getModel().getValueAt(setar, 3).toString());
        txtCor.setText(tbAuxilioSub.getModel().getValueAt(setar, 4).toString());
        txtTamanho.setText(tbAuxilioSub.getModel().getValueAt(setar, 5).toString());
        cbSexo.setSelectedItem(tbAuxilioSub.getModel().getValueAt(setar, 6).toString());      
        txtOBS.setText(tbAuxilioSub.getModel().getValueAt(setar, 7).toString());
        

        //A Linha Abaixo desabilita o botão adicionar
        btnAdicionar.setEnabled(false);
        btnAtualizar.setEnabled(true);
        btnAlterar.setEnabled(true);
        btnRemover.setEnabled(true);
  

    }

    private void limpar() {

        txtCor.setText(null);
        txtEspecie.setText(null);
        txtNome.setText(null);
        txtRaca.setText(null);
        txtTamanho.setText(null);
        txtOBS.setText(null);

        btnAdicionar.setEnabled(true);
        btnAtualizar.setEnabled(true);
        btnAlterar.setEnabled(false);
        btnRemover.setEnabled(false);

    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ID_Sub = new javax.swing.JTextField();
        ID = new javax.swing.JTextField();
        scAuxilioSub = new javax.swing.JScrollPane();
        tbAuxilioSub = new javax.swing.JTable();
        IdCliente = new javax.swing.JTextField();
        NomeCliente = new javax.swing.JTextField();
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtEspecie = new javax.swing.JTextField();
        txtNome = new javax.swing.JTextField();
        txtRaca = new javax.swing.JTextField();
        txtTamanho = new javax.swing.JTextField();
        txtCor = new javax.swing.JTextField();
        cbSexo = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOBS = new javax.swing.JTextArea();
        btnAlterar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JToggleButton();
        btnAdicionar = new javax.swing.JButton();
        pnTabela = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbSubTabela = new javax.swing.JTable();

        tbAuxilioSub.setModel(new javax.swing.table.DefaultTableModel(
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
        scAuxilioSub.setViewportView(tbAuxilioSub);

        NomeCliente.setText("jTextField1");

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
        setTitle("SubCliente");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setText("*Nome: ");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("Especie: ");

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("Raça: ");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setText("Tamanho: ");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setText("Cor:");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setText("Sexo:");

        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        cbSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Macho", "Femea" }));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OBS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtOBS.setColumns(20);
        txtOBS.setRows(5);
        jScrollPane1.setViewportView(txtOBS);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeEditar-removebg-preview.png"))); // NOI18N
        btnAlterar.setToolTipText("Alterar");
        btnAlterar.setContentAreaFilled(false);
        btnAlterar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterar.setEnabled(false);
        btnAlterar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeRemover-removebg-preview.png"))); // NOI18N
        btnRemover.setToolTipText("Apagar");
        btnRemover.setContentAreaFilled(false);
        btnRemover.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemover.setEnabled(false);
        btnRemover.setPreferredSize(new java.awt.Dimension(80, 80));
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        btnAtualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeRestart-removebg-preview.png"))); // NOI18N
        btnAtualizar.setBorderPainted(false);
        btnAtualizar.setContentAreaFilled(false);
        btnAtualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeAdicionar-removebg-preview.png"))); // NOI18N
        btnAdicionar.setToolTipText("Adicionar");
        btnAdicionar.setContentAreaFilled(false);
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        pnTabela.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente Referencia", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbSubTabela = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbSubTabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbSubTabela.setFocusable(false);
        tbSubTabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbSubTabelaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbSubTabela);

        javax.swing.GroupLayout pnTabelaLayout = new javax.swing.GroupLayout(pnTabela);
        pnTabela.setLayout(pnTabelaLayout);
        pnTabelaLayout.setHorizontalGroup(
            pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
        );
        pnTabelaLayout.setVerticalGroup(
            pnTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(pnTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNome))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtCor, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtTamanho, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(1, 1, 1)
                            .addComponent(txtEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtRaca, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addComponent(btnAtualizar)))
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtRaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtCor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(txtTamanho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(cbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdicionar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAlterar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemover, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAtualizar, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        alterar();
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        remover();
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        // TODO add your handling code here:
        limpar();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        adicionar();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        instanciarTabela();
    }//GEN-LAST:event_formWindowActivated

    private void tbSubTabelaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbSubTabelaMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbSubTabelaMouseClicked

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
            java.util.logging.Logger.getLogger(SubClienteAnimal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SubClienteAnimal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SubClienteAnimal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SubClienteAnimal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SubClienteAnimal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ID;
    private javax.swing.JTextField ID_Sub;
    public static javax.swing.JTextField IdCliente;
    private javax.swing.JTextField NomeCliente;
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnAlterar;
    private javax.swing.JToggleButton btnAtualizar;
    private javax.swing.JButton btnRemover;
    private javax.swing.JComboBox<String> cbSexo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnTabela;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilioSub;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilioSub;
    private javax.swing.JTable tbSubTabela;
    private javax.swing.JTextField txtCor;
    private javax.swing.JTextField txtEspecie;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextArea txtOBS;
    private javax.swing.JTextField txtRaca;
    private javax.swing.JTextField txtTamanho;
    // End of variables declaration//GEN-END:variables
}
