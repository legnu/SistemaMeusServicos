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
import br.com.MeusServicos.dal.ValidadorDeCNPJ;
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
public class CadFornecedor extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaFornecedor
     */
    public CadFornecedor() {
        initComponents();
        conexao = ModuloConexao.conector();

    }

    public void iniciar() {
        instanciarTabela();
    }

    private void pesquisar_fornecedor() {
        String sql = "select idfornecedor as ID, nome_fornecedor as Fonecedor, razaosocial as Razao_Social, pessoa_juridica_fisica as Pessoa, cnpj as CNPJ, cidade_fornecedor as Cidade, bairro_fornecedor as Bairro, complemento_fornecedor where fornecedor like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisa.getText() + "%");
            rs = pst.executeQuery();
            //A Linha Abaixo usa a Biblioteca rs2xml para preencher a tabela

            tbFornecedor.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void instanciarTabela() {
        String sql = "select idfornecedor as ID, nome_fornecedor as Fonecedor, razaosocial as Razao_Social, pessoa_juridica_fisica as Pessoa, cnpj as CNPJ, cidade_fornecedor as Cidade, bairro_fornecedor as Bairro, complemento_fornecedor as Complemento, cep_fornecedor as CEP, numero_fornecedor as Numero, telefone_fornecedor as Telefone, email_fornecedor as Email from tbfornecedor";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbFornecedor.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void tratoDePessoa() {
        if (cbPessoa.getSelectedItem() == "Juridica") {
            txtCNPJ.setEnabled(true);
        } else {
            txtCNPJ.setEnabled(false);
        }
    }

    private void adicionar() {

        String sql = "insert into tbfornecedor(nome_fornecedor,razaosocial,pessoa_juridica_fisica,cnpj,cidade_fornecedor,bairro_fornecedor,complemento_fornecedor,cep_fornecedor,numero_fornecedor,telefone_fornecedor,email_fornecedor)values(?,?,?,?,?,?,?,?,?,?,?)";

        try {

            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtFornecedor.getText());
            pst.setString(2, txtRazaoSocial.getText());
            pst.setString(3, cbPessoa.getSelectedItem().toString());
            pst.setString(4, txtCNPJ.getText());
            pst.setString(5, txtCidade.getText());
            pst.setString(6, txtBairro.getText());
            pst.setString(7, txtComplemento.getText());
            
                pst.setString(8, txtCEP.getText());
            

            if (txtNumero.getText().isEmpty()) {
                pst.setString(9, txtNumero.getText());
            } else {
                pst.setInt(9, Integer.parseInt(txtNumero.getText()));
            }

            pst.setString(10, txtTelefone.getText());
            pst.setString(11, txtEmail.getText());

            if ((txtFornecedor.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");
                limpar();
            }
            if (cbPessoa.getSelectedItem() == "Juridica") {
                if (ValidadorDeCNPJ.isCNPJ(txtCNPJ.getText()) == false) {
                    JOptionPane.showMessageDialog(null, "CNPJ invalido.");
                } else {
                    int adicionado = pst.executeUpdate();

                    if (adicionado > 0) {
                        JOptionPane.showMessageDialog(null, "Fornecedor adicionado com sucesso");
                        limpar();
                    }

                }
            } else {

                //A linha abaixo atualiza os dados do novo usuario
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Fornecedor adicionado com sucesso");
                    limpar();
                }
            }
        } catch (java.lang.NumberFormatException e) {
           
                JOptionPane.showMessageDialog(null, "Campo Numero Suporta somente numeros.");
                limpar();
            
        } catch (com.mysql.cj.jdbc.exceptions.MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(null, " Campo Numero suporta somente 5 numeros.");
            limpar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void alterar() {
        String sql = "update tbfornecedor set nome_fornecedor=?,razaosocial=?,pessoa_juridica_fisica=?,cnpj=?,cidade_fornecedor=?,bairro_fornecedor=?,complemento_fornecedor=?,cep_fornecedor=?,numero_fornecedor=?,telefone_fornecedor=?,email_fornecedor=? where idfornecedor=?";
        
            try {

            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtFornecedor.getText());
            pst.setString(2, txtRazaoSocial.getText());
            pst.setString(3, cbPessoa.getSelectedItem().toString());
            pst.setString(4, txtCNPJ.getText());
            pst.setString(5, txtCidade.getText());
            pst.setString(6, txtBairro.getText());
            pst.setString(7, txtComplemento.getText());
            
                pst.setString(8, txtCEP.getText());
            

            if (txtNumero.getText().isEmpty()) {
                pst.setString(9, txtNumero.getText());
            } else {
                pst.setInt(9, Integer.parseInt(txtNumero.getText()));
            }

            pst.setString(10, txtTelefone.getText());
            pst.setString(11, txtEmail.getText());
            pst.setString(12, txtID.getText());

            if ((txtFornecedor.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");
                limpar();
            }
            if (cbPessoa.getSelectedItem() == "Juridica") {
                if (ValidadorDeCNPJ.isCNPJ(txtCNPJ.getText()) == false) {
                    JOptionPane.showMessageDialog(null, "CNPJ invalido.");
                } else {
                    int adicionado = pst.executeUpdate();

                    if (adicionado > 0) {
                        JOptionPane.showMessageDialog(null, "Fornecedor adicionado com sucesso");
                        limpar();
                    }

                }
            } else {

                //A linha abaixo atualiza os dados do novo usuario
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Fornecedor adicionado com sucesso");
                    limpar();
                }
            }
        } catch (java.lang.NumberFormatException e) {
           
                JOptionPane.showMessageDialog(null, "Campo Numero Suporta somente numeros.");
                limpar();
            
        } catch (com.mysql.cj.jdbc.exceptions.MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(null, " Campo Numero suporta somente 5 numeros.");
            limpar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este produto?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbfornecedor where idfornecedor=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtID.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                    tirarId();
                    criarId();
                    JOptionPane.showMessageDialog(null, "Fornecedor removido com sucesso");
                    limpar();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();
            }
        }
    }

    private void tirarId() {

        String sql = "alter table tbfornecedor drop idfornecedor";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void criarId() {
        String sql = "alter table tbfornecedor add idfornecedor MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void limpar() {
        instanciarTabela();
        txtBairro.setText(null);
        txtCEP.setText(null);
        txtCNPJ.setText(null);
        txtCidade.setText(null);
        txtComplemento.setText(null);
        txtEmail.setText(null);
        txtFornecedor.setText(null);
        txtID.setText(null);
        txtNumero.setText(null);
        txtPesquisa.setText(null);
        txtRazaoSocial.setText(null);
        txtTelefone.setText(null);
        btnAdicionar.setEnabled(true);
        btnAtualizar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnRemover.setEnabled(false);

    }

    public void setar_campos() {
        int setar = tbFornecedor.getSelectedRow();
        txtID.setText(tbFornecedor.getModel().getValueAt(setar, 0).toString());
        txtFornecedor.setText(tbFornecedor.getModel().getValueAt(setar, 1).toString());
        txtRazaoSocial.setText(tbFornecedor.getModel().getValueAt(setar, 2).toString());
        cbPessoa.setSelectedItem(tbFornecedor.getModel().getValueAt(setar, 3).toString());
        txtCNPJ.setText(tbFornecedor.getModel().getValueAt(setar, 4).toString());
        txtCidade.setText(tbFornecedor.getModel().getValueAt(setar, 5).toString());
        txtBairro.setText(tbFornecedor.getModel().getValueAt(setar, 6).toString());
        txtComplemento.setText(tbFornecedor.getModel().getValueAt(setar, 7).toString());
        txtCEP.setText(tbFornecedor.getModel().getValueAt(setar, 8).toString());
        txtNumero.setText(tbFornecedor.getModel().getValueAt(setar, 9).toString());
        txtTelefone.setText(tbFornecedor.getModel().getValueAt(setar, 10).toString());
        txtEmail.setText(tbFornecedor.getModel().getValueAt(setar, 11).toString());

        //A Linha Abaixo desabilita o botão adicionar
        txtPesquisa.setText(null);
        btnAdicionar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnRemover.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtID = new javax.swing.JTextField();
        lblPesquisar = new javax.swing.JLabel();
        lblCamposObrigatorios = new javax.swing.JLabel();
        lblCEP = new javax.swing.JLabel();
        lblCNPJ = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblBairro = new javax.swing.JLabel();
        lblTelefone = new javax.swing.JLabel();
        lblComplemento = new javax.swing.JLabel();
        lblNumero = new javax.swing.JLabel();
        lblPessoa = new javax.swing.JLabel();
        lblFornecedor = new javax.swing.JLabel();
        lblRazaoSocial = new javax.swing.JLabel();
        lblCidade = new javax.swing.JLabel();
        txtPesquisa = new javax.swing.JTextField();
        txtCNPJ = new javax.swing.JTextField();
        txtCidade = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtComplemento = new javax.swing.JTextField();
        txtBairro = new javax.swing.JTextField();
        txtFornecedor = new javax.swing.JTextField();
        txtRazaoSocial = new javax.swing.JTextField();
        txtNumero = new javax.swing.JTextField();
        cbPessoa = new javax.swing.JComboBox<>();
        btnAdicionar = new javax.swing.JToggleButton();
        btnEditar = new javax.swing.JToggleButton();
        btnRemover = new javax.swing.JToggleButton();
        btnAtualizar = new javax.swing.JToggleButton();
        pnFornecedor = new javax.swing.JPanel();
        scFornecedor = new javax.swing.JScrollPane();
        tbFornecedor = new javax.swing.JTable();
        txtTelefone = new javax.swing.JFormattedTextField();
        txtCEP = new javax.swing.JFormattedTextField();

        txtID.setEditable(false);
        txtID.setEnabled(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Fornecedor");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        lblPesquisar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisar.setText("Pesquisar:");

        lblCamposObrigatorios.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCamposObrigatorios.setText("*Campos Obrigatorios");

        lblCEP.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCEP.setText("CEP:");

        lblCNPJ.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCNPJ.setText("CNPJ:");

        lblEmail.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblEmail.setText("E-mail:");

        lblBairro.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblBairro.setText("Bairro:");

        lblTelefone.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblTelefone.setText("*Telefone:");

        lblComplemento.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblComplemento.setText("Complemento:");

        lblNumero.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblNumero.setText("Numero:");

        lblPessoa.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPessoa.setText("*Pessoa:");

        lblFornecedor.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblFornecedor.setText("*Nome Fornecedor:");

        lblRazaoSocial.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblRazaoSocial.setText("Razão Social:");

        lblCidade.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCidade.setText("Cidade:");

        txtPesquisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaKeyReleased(evt);
            }
        });

        txtBairro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBairroActionPerformed(evt);
            }
        });

        cbPessoa.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        cbPessoa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fisica", "Juridica" }));
        cbPessoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPessoaActionPerformed(evt);
            }
        });
        cbPessoa.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cbPessoaPropertyChange(evt);
            }
        });

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeAdicionar-removebg-preview.png"))); // NOI18N
        btnAdicionar.setContentAreaFilled(false);
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeEditar-removebg-preview.png"))); // NOI18N
        btnEditar.setContentAreaFilled(false);
        btnEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditar.setEnabled(false);
        btnEditar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeRemover-removebg-preview.png"))); // NOI18N
        btnRemover.setContentAreaFilled(false);
        btnRemover.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemover.setEnabled(false);
        btnRemover.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        btnAtualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeRestart-removebg-preview.png"))); // NOI18N
        btnAtualizar.setContentAreaFilled(false);
        btnAtualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAtualizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        pnFornecedor.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fornecedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbFornecedor = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbFornecedor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbFornecedor.setFocusable(false);
        tbFornecedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbFornecedorMouseClicked(evt);
            }
        });
        scFornecedor.setViewportView(tbFornecedor);

        javax.swing.GroupLayout pnFornecedorLayout = new javax.swing.GroupLayout(pnFornecedor);
        pnFornecedor.setLayout(pnFornecedorLayout);
        pnFornecedorLayout.setHorizontalGroup(
            pnFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scFornecedor, javax.swing.GroupLayout.DEFAULT_SIZE, 841, Short.MAX_VALUE)
        );
        pnFornecedorLayout.setVerticalGroup(
            pnFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scFornecedor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
        );

        try {
            txtTelefone.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##) #####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtCEP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblPesquisar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(281, 281, 281)
                        .addComponent(lblCamposObrigatorios)
                        .addGap(3, 3, 3))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCidade)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnAdicionar)
                                        .addGap(48, 48, 48)
                                        .addComponent(btnEditar)
                                        .addGap(48, 48, 48)
                                        .addComponent(btnRemover)
                                        .addGap(48, 48, 48)
                                        .addComponent(btnAtualizar)
                                        .addGap(170, 170, 170))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lblEmail)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblTelefone)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblBairro)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblComplemento)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtComplemento)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCEP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCEP, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblNumero)
                                .addGap(13, 13, 13)
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(pnFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRazaoSocial, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFornecedor)
                        .addGap(13, 13, 13)
                        .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(lblPessoa)
                        .addGap(13, 13, 13)
                        .addComponent(cbPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblCNPJ)
                        .addGap(13, 13, 13)
                        .addComponent(txtCNPJ)))
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPesquisar)
                    .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCamposObrigatorios))
                .addGap(24, 24, 24)
                .addComponent(pnFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFornecedor)
                    .addComponent(lblPessoa)
                    .addComponent(cbPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCNPJ)
                    .addComponent(txtCNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRazaoSocial)
                    .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTelefone)
                    .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBairro)
                    .addComponent(lblCidade)
                    .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblComplemento)
                    .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCEP)
                    .addComponent(txtCEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNumero)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionar)
                    .addComponent(btnEditar)
                    .addComponent(btnRemover)
                    .addComponent(btnAtualizar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        iniciar();
    }//GEN-LAST:event_formWindowActivated

    private void tbFornecedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbFornecedorMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbFornecedorMouseClicked

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
        adicionar();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        // TODO add your handling code here:
        limpar();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        alterar();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        // TODO add your handling code here:
        remover();
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void txtPesquisaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaKeyReleased
        // TODO add your handling code here:
        pesquisar_fornecedor();
    }//GEN-LAST:event_txtPesquisaKeyReleased

    private void cbPessoaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbPessoaPropertyChange
        // TODO add your handling code here:
        tratoDePessoa();
    }//GEN-LAST:event_cbPessoaPropertyChange

    private void cbPessoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPessoaActionPerformed
        // TODO add your handling code here:
        tratoDePessoa();
    }//GEN-LAST:event_cbPessoaActionPerformed

    private void txtBairroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBairroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBairroActionPerformed

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
            java.util.logging.Logger.getLogger(CadFornecedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadFornecedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadFornecedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadFornecedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadFornecedor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnAdicionar;
    private javax.swing.JToggleButton btnAtualizar;
    private javax.swing.JToggleButton btnEditar;
    private javax.swing.JToggleButton btnRemover;
    private javax.swing.JComboBox<String> cbPessoa;
    private javax.swing.JLabel lblBairro;
    private javax.swing.JLabel lblCEP;
    private javax.swing.JLabel lblCNPJ;
    private javax.swing.JLabel lblCamposObrigatorios;
    private javax.swing.JLabel lblCidade;
    private javax.swing.JLabel lblComplemento;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFornecedor;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblPesquisar;
    private javax.swing.JLabel lblPessoa;
    private javax.swing.JLabel lblRazaoSocial;
    private javax.swing.JLabel lblTelefone;
    private javax.swing.JPanel pnFornecedor;
    private javax.swing.JScrollPane scFornecedor;
    private javax.swing.JTable tbFornecedor;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JFormattedTextField txtCEP;
    private javax.swing.JTextField txtCNPJ;
    private javax.swing.JTextField txtCidade;
    private javax.swing.JTextField txtComplemento;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFornecedor;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtPesquisa;
    private javax.swing.JTextField txtRazaoSocial;
    private javax.swing.JFormattedTextField txtTelefone;
    // End of variables declaration//GEN-END:variables
}
