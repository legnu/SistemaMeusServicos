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
public class CadProduto extends javax.swing.JFrame {

    /**
     * Creates new form CadProduto
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public CadProduto() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    public void InstanciarCombobox() {
        try {
            cbFornecedor.addItem("Estoque");
            String sql = "select nome_fornecedor from tbfornecedor";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbFornecedor.addItem(rs.getString("nome_fornecedor"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void instanciarTabela() {
        String sql = "select idproduto as ID,codigo as Codigo, produto as Produto, valor_compra as Valor_de_Compra, valor_venda as Valor_de_Venda, fornecedor as Fornecedor, obs as Observações,estoque as Estoque from tbprodutos";
        try {

            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbProduto.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void limpar() {
        instanciarTabela();
        txtID.setText(null);
        txtCodigo.setText(null);
        txtCusto.setText("0.00");
        txtPreco.setText("0.00");
        txtDescricao.setText(null);
        txtPesquisa.setText(null);
        cbFornecedor.setSelectedItem(" ");
        taProduto.setText(null);
        btnAdicionar.setEnabled(true);
        btnAtualizar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnRemover.setEnabled(false);

    }

    private void pesquisar_cliente() {
        String sql = "select idproduto as ID, produto as Produto, codigo as Codigo, valor_compra as Valor_de_Compra, valor_venda as Valor_de_Venda, fornecedor as Fornecedor, obs as Observações, estoque as Estoque from tbprodutos where produto like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisa.getText() + "%");
            rs = pst.executeQuery();
            //A Linha Abaixo usa a Biblioteca rs2xml para preencher a tabela

            tbProduto.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void setar_campos() {
        int setar = tbProduto.getSelectedRow();
        txtID.setText(tbProduto.getModel().getValueAt(setar, 0).toString());
        txtCodigo.setText(tbProduto.getModel().getValueAt(setar, 1).toString());
        txtDescricao.setText(tbProduto.getModel().getValueAt(setar, 2).toString());
        double custo = Integer.parseInt(tbProduto.getModel().getValueAt(setar, 3).toString().replace(".", "")) / 100;
        double preco = Integer.parseInt(tbProduto.getModel().getValueAt(setar, 4).toString().replace(".", "")) / 100;
        txtCusto.setText(String.valueOf(custo));
        txtPreco.setText(String.valueOf(preco));
        if (tbProduto.getModel().getValueAt(setar, 5).toString() != null) {
            cbFornecedor.setSelectedItem(tbProduto.getModel().getValueAt(setar, 5).toString());
        }
        taProduto.setText(tbProduto.getModel().getValueAt(setar, 6).toString());
        cbEstoque.setSelectedItem(tbProduto.getModel().getValueAt(setar, 7).toString());

        //A Linha Abaixo desabilita o botão adicionar
        txtPesquisa.setText(null);
        btnAdicionar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnRemover.setEnabled(true);
    }

    private void adicionar() {

        String sql = "insert into tbprodutos(produto,codigo,valor_compra,valor_venda,fornecedor,obs,estoque,quantidade)values(?,?,?,?,?,?,?,?)";

        try {

            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtDescricao.getText());

            if (cbFornecedor.getSelectedItem() == null) {
                pst.setString(2, txtCodigo.getText());
            } else {
                pst.setString(2, txtCodigo.getText());
            }

            pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(txtCusto.getText())).replace(",", "."));
            pst.setString(4, new DecimalFormat("#,##0.00").format(Float.parseFloat(txtPreco.getText())).replace(",", "."));

            pst.setString(5, cbFornecedor.getSelectedItem().toString());

            pst.setString(6, taProduto.getText());
            pst.setString(7, cbEstoque.getSelectedItem().toString());
            pst.setInt(8, 0);

            if ((txtDescricao.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");

            } else if ((Double.parseDouble(txtCusto.getText()) <= 0) || (Double.parseDouble(txtPreco.getText()) <= 0)) {
                JOptionPane.showMessageDialog(null, "Valor de Compra ou Valor de Venda Deve ser maior que 0.");
            } else if ((Double.parseDouble(txtCodigo.getText()) == 0)) {
                JOptionPane.showMessageDialog(null, "Codigo não pode ser 0.");
            } else {

                int adicionado = pst.executeUpdate();

                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Produto adicionado com sucesso");
                    limpar();
                }
            }
        } catch (com.mysql.cj.jdbc.exceptions.MysqlDataTruncation n) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar Codigo de Barras, maximo de 18 caracteres");
            limpar();

        } catch (java.lang.NumberFormatException c) {
            JOptionPane.showMessageDialog(null, "Campo Codigo so aceita Numeros. \n"
                    + "Valor de Compra e Valor de Venda deve ser Salvo no Formato 0.00 .");

            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void alterar() {
        String sql = "update tbprodutos set produto=?,codigo=?,valor_compra=?,valor_venda=?,fornecedor=?,obs=?,estoque=? where idproduto=?";
        try {
            ;
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtDescricao.getText());

            if (cbFornecedor.getSelectedItem() == null) {
                pst.setString(2, txtCodigo.getText());
            } else {
                pst.setString(2, txtCodigo.getText());
            }

            pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(txtCusto.getText())).replace(",", "."));
            pst.setString(4, new DecimalFormat("#,##0.00").format(Float.parseFloat(txtPreco.getText())).replace(",", "."));

            pst.setString(5, cbFornecedor.getSelectedItem().toString());

            pst.setString(6, taProduto.getText());
            pst.setString(7, cbEstoque.getSelectedItem().toString());
            pst.setString(8, txtID.getText());

            if ((txtDescricao.getText().isEmpty()) || (txtCusto.getText().isEmpty()) || (txtPreco.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");
            } else if ((Double.parseDouble(txtCusto.getText()) <= 0) || (Double.parseDouble(txtPreco.getText()) <= 0)) {
                JOptionPane.showMessageDialog(null, "Valor de Compra ou Valor de Venda Deve ser maior que 0.");
            } else {

                //A linha abaixo altera os dados do novo usuario
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do produto alterado(s) com sucesso");
                    limpar();

                }
            }
        } catch (com.mysql.cj.jdbc.exceptions.MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(null, "Codigo de barras deve ter no maximo 18 caracteres.");
            limpar();
        } catch (java.lang.NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Codigo de barras, Valor de Compra e Valor de Venda não suportam letras. \n"
                    + "Valor de Compra e Valor de Venda deve ser Salvo no Formato 0.00 .");
            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este produto?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbprodutos where idproduto=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtID.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Aguarde.");
                    tirarId();
                    criarId();
                    JOptionPane.showMessageDialog(null, "Produto removido com sucesso");
                    limpar();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();
            }
        }
    }

    private void tirarId() {

        String sql = "alter table tbprodutos drop idproduto";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    private void criarId() {
        String sql = "alter table tbprodutos add idproduto MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
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

        txtID = new javax.swing.JTextField();
        txtPreco = new javax.swing.JTextField();
        cbFornecedor = new javax.swing.JComboBox<>();
        txtCodigo = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        scTbProduto = new javax.swing.JScrollPane();
        tbProduto = new javax.swing.JTable();
        lblCusto = new javax.swing.JLabel();
        btnAdicionar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        scProduto = new javax.swing.JScrollPane();
        taProduto = new javax.swing.JTextArea();
        lblPreco = new javax.swing.JLabel();
        btnEditar = new javax.swing.JButton();
        lblFornecedor = new javax.swing.JLabel();
        btnRemover = new javax.swing.JButton();
        lblCodigoDeBarras = new javax.swing.JLabel();
        btnAtualizar = new javax.swing.JButton();
        lblEstoque = new javax.swing.JLabel();
        cbEstoque = new javax.swing.JComboBox<>();
        txtDescricao = new javax.swing.JTextField();
        lblDescricao = new javax.swing.JLabel();
        lblCamposObrigatorios = new javax.swing.JLabel();
        txtPesquisa = new javax.swing.JTextField();
        lblPesquisa = new javax.swing.JLabel();
        txtCusto = new javax.swing.JTextField();

        txtID.setEnabled(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Produtos");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        cbFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFornecedorActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Produtos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbProduto = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbProduto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbProduto.setFocusable(false);
        tbProduto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProdutoMouseClicked(evt);
            }
        });
        scTbProduto.setViewportView(tbProduto);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scTbProduto, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scTbProduto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
        );

        lblCusto.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCusto.setText("*Valor Compra(R$):");

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeAdicionar-removebg-preview.png"))); // NOI18N
        btnAdicionar.setContentAreaFilled(false);
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OBS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        taProduto.setColumns(20);
        taProduto.setRows(5);
        scProduto.setViewportView(taProduto);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scProduto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scProduto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
        );

        lblPreco.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPreco.setText("*Valor Venda(R$):");

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeEditar-removebg-preview.png"))); // NOI18N
        btnEditar.setContentAreaFilled(false);
        btnEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditar.setEnabled(false);
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        lblFornecedor.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblFornecedor.setText("Fornecedor:");

        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeRemover-removebg-preview.png"))); // NOI18N
        btnRemover.setContentAreaFilled(false);
        btnRemover.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemover.setEnabled(false);
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        lblCodigoDeBarras.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCodigoDeBarras.setText("Codigo de Barras:");

        btnAtualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeRestart-removebg-preview.png"))); // NOI18N
        btnAtualizar.setContentAreaFilled(false);
        btnAtualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        lblEstoque.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblEstoque.setText("*Estoque:");

        cbEstoque.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        cbEstoque.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Com controle de estoque.", "Sem controle de estoque." }));

        lblDescricao.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblDescricao.setText("*Nome_Produto:");

        lblCamposObrigatorios.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCamposObrigatorios.setText("*Campos Obrigatorios");

        txtPesquisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisaActionPerformed(evt);
            }
        });
        txtPesquisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaKeyReleased(evt);
            }
        });

        lblPesquisa.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisa.setText("Pesquisa:");

        txtCusto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPesquisa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblCamposObrigatorios))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblDescricao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblCodigoDeBarras)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFornecedor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbFornecedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(lblEstoque)
                                .addGap(18, 18, 18)
                                .addComponent(cbEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCusto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCusto)
                                .addGap(18, 18, 18)
                                .addComponent(lblPreco)
                                .addGap(18, 18, 18)
                                .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
            .addGroup(layout.createSequentialGroup()
                .addGap(254, 254, 254)
                .addComponent(btnAdicionar)
                .addGap(48, 48, 48)
                .addComponent(btnEditar)
                .addGap(48, 48, 48)
                .addComponent(btnRemover)
                .addGap(48, 48, 48)
                .addComponent(btnAtualizar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPesquisa)
                    .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCamposObrigatorios))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDescricao)
                            .addComponent(lblCodigoDeBarras)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFornecedor)
                            .addComponent(cbFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEstoque)
                            .addComponent(cbEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCusto)
                            .addComponent(txtCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPreco)
                            .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAtualizar)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnEditar)
                        .addComponent(btnRemover)
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbFornecedorActionPerformed

    private void tbProdutoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProdutoMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbProdutoMouseClicked

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
        adicionar();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        alterar();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        // TODO add your handling code here:
        remover();
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        // TODO add your handling code here:
        limpar();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void txtPesquisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisaActionPerformed

    private void txtPesquisaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaKeyReleased
        // TODO add your handling code here:
        pesquisar_cliente();
    }//GEN-LAST:event_txtPesquisaKeyReleased

    private void txtCustoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustoActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        limpar();
        InstanciarCombobox();
    }//GEN-LAST:event_formWindowActivated

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
            java.util.logging.Logger.getLogger(CadProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadProduto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnRemover;
    private javax.swing.JComboBox<String> cbEstoque;
    private javax.swing.JComboBox<String> cbFornecedor;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblCamposObrigatorios;
    private javax.swing.JLabel lblCodigoDeBarras;
    private javax.swing.JLabel lblCusto;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblEstoque;
    private javax.swing.JLabel lblFornecedor;
    private javax.swing.JLabel lblPesquisa;
    private javax.swing.JLabel lblPreco;
    private javax.swing.JScrollPane scProduto;
    private javax.swing.JScrollPane scTbProduto;
    private javax.swing.JTextArea taProduto;
    private javax.swing.JTable tbProduto;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtCusto;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtPesquisa;
    private javax.swing.JTextField txtPreco;
    // End of variables declaration//GEN-END:variables
}
