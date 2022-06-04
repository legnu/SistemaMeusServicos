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
public class Compra extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form Compra
     */
    public Compra() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    public void iniciar() {
        instanciarTabela();

        instanciarTabelaNotaCompra();
        instanciarTabelaSoma();
        txtValorUnidade.setText("0.00");
        txtValorFinal.setText("0.00");
    }

    public void adicionarCompra() {
        int quantidadeAdicionada;
        int quantidadeInicial;
        int quantidadeFinal;
        double preco;
        double unidade;

        try {

            quantidadeAdicionada = Integer.parseInt(txtQuantidade.getText());
            quantidadeInicial = Integer.parseInt(txtQuantidadeInicial.getText());
            quantidadeFinal = quantidadeAdicionada + quantidadeInicial;

            String sql = "insert into tbcompra(nome_produto, valor, valor_unidade, quantidade_comprada)values(?,?,?,?)";
            preco = Double.parseDouble(txtValorFinal.getText());
            unidade = Double.parseDouble(txtValorUnidade.getText());
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtProduto.getText());
            pst.setString(2, new DecimalFormat("#,##0.00").format(preco).replace(",", "."));
            pst.setString(3, new DecimalFormat("#,##0.00").format(unidade).replace(",", "."));
            pst.setString(4, txtQuantidade.getText());

            //Validação dos Campos Obrigatorios
            if ((txtProduto.getText().isEmpty()) || (txtValorFinal.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");
            } else {

                if (quantidadeAdicionada <= 0) {
                    JOptionPane.showMessageDialog(null, "Quantidade Adicionada deve ser maior que 0.");
                    Limpar();
                }
                if ((txtProduto.getText().isEmpty()) || (txtValorFinal.getText().isEmpty()) || (txtQuantidade.getText().isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");

                } else {
                    pst.executeUpdate();
                    String sqr = "update tbprodutos set quantidade=? where idproduto=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setInt(1, quantidadeFinal);
                    pst.setString(2, txtID.getText());

                    //A linha abaixo altera os dados do novo usuario
                    int adicionado = pst.executeUpdate();
                    //A Linha abaixo serve de apoio ao entendimento da logica
                    //System.out.println(adicionado);
                    if (adicionado > 0) {
                        JOptionPane.showMessageDialog(null, "Produto(s) comprado(s) com sucesso");
                        Limpar();

                        instanciarTabelaNotaCompra();
                        instanciarTabelaSoma();

                    }

                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabela() {
        String sql = "select idproduto as ID, produto as Produto,custo as Custo, quantidade as Quantidade from tbprodutos where estoque=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, "Com controle de estoque.");
            rs = pst.executeQuery();
            tbProduto.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaNotaCompra() {
        String sqr = "select idcompra as ID_Compra, nome_produto as Produto, quantidade_comprada as Quantidade_Adicionada, valor as Valor_Total from tbcompra";
        try {
            pst = conexao.prepareStatement(sqr);
            rs = pst.executeQuery();
            tbNotaCompra.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaSoma() {
        double preco;

        String sql = "select sum(valor) from tbcompra";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbSoma.setModel(DbUtils.resultSetToTableModel(rs));
            preco = Double.parseDouble((tbSoma.getModel().getValueAt(0, 0).toString()));
            lblValorTotal.setText(new DecimalFormat("#,##0.00").format(preco).replace(",", "."));
        } catch (java.lang.NullPointerException e) {
            lblValorTotal.setText("0.00");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void Calculo_valor_final() {

        double calculo;
        int quantidade;
        double custo;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText());
            custo = Double.parseDouble(txtValorUnidade.getText());
            calculo = quantidade * custo;
            txtValorFinal.setText(String.valueOf(new DecimalFormat("#,##0.00").format(calculo).replace(",", ".")));
            if (txtQuantidade.getText().equals("0")) {
                txtValorFinal.setText("0.00");
            }

        } catch (java.lang.NumberFormatException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void comprar() {
        String sql = "insert into gastos(valor)values(?)";

        double preco;

        preco = Double.parseDouble(lblValorTotal.getText());

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, new DecimalFormat("#,##0.00").format(preco).replace(",", "."));

            //Validação dos Campos Obrigatorios
            if ((lblValorTotal.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Sua lista de compras deve conter algo.");
            } else {

                //A linha abaixo atualiza os dados do novo usuario
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Item(s) comprado(s) com sucesso");
                    try {
                        String sqr = "delete from tbcompra";
                        pst = conexao.prepareStatement(sqr);
                        pst.executeUpdate();
                        Limpar();
                        tirarId();
                        criarId();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }

                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void remover() {

        int quantidadeAdicionada;
        int quantidadeAtual;
        int quantidade;

        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este produto?", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) {

            quantidadeAdicionada = Integer.parseInt(txtQuantidade.getText());
            quantidadeAtual = Integer.parseInt(txtQuantidadeInicial.getText());

            quantidade = quantidadeAtual - quantidadeAdicionada;

            String sqr = "update tbprodutos set quantidade=? where produto=?";
            try {

                pst = conexao.prepareStatement(sqr);
                pst.setInt(1, quantidade);
                pst.setString(2, txtProduto.getText());
                pst.executeUpdate();

                String sql = "delete from tbcompra where idcompra=?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtID.getText());
                int apagado = pst.executeUpdate();

                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Produto removido com sucesso");
                    tirarId();
                    criarId();
                    Limpar();

                }
            } catch (java.lang.NullPointerException e) {

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    public void setar_campos() {
        int setar = tbProduto.getSelectedRow();
        txtID.setText(tbProduto.getModel().getValueAt(setar, 0).toString());
        txtProduto.setText(tbProduto.getModel().getValueAt(setar, 1).toString());
        txtValorUnidade.setText(tbProduto.getModel().getValueAt(setar, 2).toString().replace(",", "."));
        txtQuantidadeInicial.setText(tbProduto.getModel().getValueAt(setar, 3).toString());

        //A Linha Abaixo desabilita o botão adicionar
        txtPesquisa.setText(null);
        txtQuantidade.setEnabled(true);
        btnAdicionar.setEnabled(true);

    }

    public void setar_campos_remover() {
        try {
            int setar = tbNotaCompra.getSelectedRow();
            txtID.setText(tbNotaCompra.getModel().getValueAt(setar, 0).toString());
            txtProduto.setText(tbNotaCompra.getModel().getValueAt(setar, 1).toString());
            txtQuantidade.setText(tbNotaCompra.getModel().getValueAt(setar, 2).toString());
            txtValorFinal.setText(tbNotaCompra.getModel().getValueAt(setar, 3).toString().replace(",", "."));

            String sql = "select quantidade from tbprodutos where produto=?";

            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtProduto.getText());
            rs = pst.executeQuery();
            tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));

            txtQuantidadeInicial.setText(tbEstoque.getModel().getValueAt(0, 0).toString());

            txtPesquisa.setText(null);
            btnRemove.setEnabled(true);
        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void criarId() {
        String sql = "alter table tbcompra add idcompra MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void tirarId() {

        String sql = "alter table tbcompra drop idcompra";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void Limpar() {
        instanciarTabelaSoma();
        instanciarTabelaNotaCompra();

        txtProduto.setText(null);
        txtID.setText(null);
        txtQuantidade.setText(null);
        txtValorUnidade.setText("0.00");
        txtValorFinal.setText("0.00");
        txtQuantidadeInicial.setText(null);
        txtQuantidade.setEnabled(false);
        btnAdicionar.setEnabled(false);
        btnRemove.setEnabled(false);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scSoma = new javax.swing.JScrollPane();
        tbSoma = new javax.swing.JTable();
        scEstoque = new javax.swing.JScrollPane();
        tbEstoque = new javax.swing.JTable();
        lblPesquisar = new javax.swing.JLabel();
        lblCamposObrigatorios = new javax.swing.JLabel();
        lblNomeProduto = new javax.swing.JLabel();
        lblID = new javax.swing.JLabel();
        lblValorUnidade = new javax.swing.JLabel();
        lblQuantidadeInicial = new javax.swing.JLabel();
        lblQuantidade = new javax.swing.JLabel();
        lblValorFinal = new javax.swing.JLabel();
        txtPesquisa = new javax.swing.JTextField();
        txtProduto = new javax.swing.JTextField();
        txtID = new javax.swing.JTextField();
        txtValorUnidade = new javax.swing.JTextField();
        txtQuantidadeInicial = new javax.swing.JTextField();
        txtQuantidade = new javax.swing.JTextField();
        txtValorFinal = new javax.swing.JTextField();
        btnAdicionar = new javax.swing.JButton();
        scProduto = new javax.swing.JScrollPane();
        tbProduto = new javax.swing.JTable();
        pnNota = new javax.swing.JPanel();
        btnRemove = new javax.swing.JButton();
        btnComprar = new javax.swing.JToggleButton();
        lblTotal = new javax.swing.JLabel();
        lblValorTotal = new javax.swing.JLabel();
        scNotaProduto1 = new javax.swing.JScrollPane();
        tbNotaCompra = new javax.swing.JTable();

        tbSoma.setModel(new javax.swing.table.DefaultTableModel(
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
        scSoma.setViewportView(tbSoma);

        tbEstoque.setModel(new javax.swing.table.DefaultTableModel(
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
        scEstoque.setViewportView(tbEstoque);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

        lblNomeProduto.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblNomeProduto.setText("*Nome_Produto:");

        lblID.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblID.setText("ID:");

        lblValorUnidade.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblValorUnidade.setText("*Valor Unidade:");

        lblQuantidadeInicial.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblQuantidadeInicial.setText("*Quantidade Atual:");

        lblQuantidade.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblQuantidade.setText("*Quantidade Comprada:");

        lblValorFinal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblValorFinal.setText("*Valor Final:");

        txtProduto.setEditable(false);
        txtProduto.setEnabled(false);
        txtProduto.setFocusable(false);

        txtID.setEditable(false);
        txtID.setEnabled(false);
        txtID.setFocusable(false);

        txtValorUnidade.setEditable(false);
        txtValorUnidade.setEnabled(false);
        txtValorUnidade.setFocusable(false);

        txtQuantidadeInicial.setEditable(false);
        txtQuantidadeInicial.setEnabled(false);
        txtQuantidadeInicial.setFocusable(false);

        txtQuantidade.setEnabled(false);
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

        txtValorFinal.setEditable(false);
        txtValorFinal.setEnabled(false);
        txtValorFinal.setFocusable(false);

        btnAdicionar.setBackground(new java.awt.Color(0, 0, 255));
        btnAdicionar.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        btnAdicionar.setForeground(new java.awt.Color(255, 255, 255));
        btnAdicionar.setText("Adicionar");
        btnAdicionar.setBorder(null);
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionar.setEnabled(false);
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

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
        scProduto.setViewportView(tbProduto);

        pnNota.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Nota", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        btnRemove.setBackground(new java.awt.Color(255, 0, 0));
        btnRemove.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        btnRemove.setForeground(new java.awt.Color(255, 255, 255));
        btnRemove.setText("Remover");
        btnRemove.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnRemove.setBorderPainted(false);
        btnRemove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemove.setEnabled(false);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnComprar.setBackground(new java.awt.Color(51, 255, 0));
        btnComprar.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        btnComprar.setForeground(new java.awt.Color(255, 255, 255));
        btnComprar.setText("Comprar");
        btnComprar.setBorderPainted(false);
        btnComprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarActionPerformed(evt);
            }
        });

        lblTotal.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lblTotal.setText("Total:");

        lblValorTotal.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lblValorTotal.setText("0.00");

        tbNotaCompra = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbNotaCompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbNotaCompra.setFocusable(false);
        tbNotaCompra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbNotaCompraMouseClicked(evt);
            }
        });
        scNotaProduto1.setViewportView(tbNotaCompra);

        javax.swing.GroupLayout pnNotaLayout = new javax.swing.GroupLayout(pnNota);
        pnNota.setLayout(pnNotaLayout);
        pnNotaLayout.setHorizontalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNotaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scNotaProduto1, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
                    .addGroup(pnNotaLayout.createSequentialGroup()
                        .addComponent(lblTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblValorTotal)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnNotaLayout.createSequentialGroup()
                        .addComponent(btnComprar, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnNotaLayout.setVerticalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNotaLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(scNotaProduto1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(lblValorTotal))
                .addGap(18, 18, 18)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnComprar, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(30, 50, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(lblID)
                                        .addGap(12, 12, 12)
                                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(335, 335, 335)
                                        .addComponent(lblCamposObrigatorios))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblPesquisar)
                                        .addGap(6, 6, 6)
                                        .addComponent(txtPesquisa))
                                    .addComponent(scProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblValorUnidade)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblNomeProduto)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(txtProduto)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblQuantidadeInicial)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtQuantidadeInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(txtValorUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lblValorFinal)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblQuantidade)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(pnNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblID)
                                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblCamposObrigatorios)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPesquisar)
                            .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addComponent(scProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtQuantidadeInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblQuantidadeInicial)
                            .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNomeProduto))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblQuantidade)
                            .addComponent(txtValorUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblValorFinal)
                            .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblValorUnidade))
                        .addGap(50, 50, 50)
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(74, 74, 74))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        iniciar();
    }//GEN-LAST:event_formWindowActivated

    private void tbProdutoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProdutoMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbProdutoMouseClicked

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        // TODO add your handling code here:
        Calculo_valor_final();
    }//GEN-LAST:event_txtQuantidadeKeyReleased

    private void btnComprarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComprarActionPerformed
        // TODO add your handling code here:
        comprar();
    }//GEN-LAST:event_btnComprarActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
        adicionarCompra();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        // TODO add your handling code here:
        remover();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void tbNotaCompraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbNotaCompraMouseClicked
        // TODO add your handling code here:
        setar_campos_remover();
    }//GEN-LAST:event_tbNotaCompraMouseClicked

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
            java.util.logging.Logger.getLogger(Compra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Compra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Compra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Compra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Compra().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JToggleButton btnComprar;
    private javax.swing.JButton btnRemove;
    private javax.swing.JLabel lblCamposObrigatorios;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblNomeProduto;
    private javax.swing.JLabel lblPesquisar;
    private javax.swing.JLabel lblQuantidade;
    private javax.swing.JLabel lblQuantidadeInicial;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblValorFinal;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JLabel lblValorUnidade;
    private javax.swing.JPanel pnNota;
    private javax.swing.JScrollPane scEstoque;
    private javax.swing.JScrollPane scNotaProduto1;
    private javax.swing.JScrollPane scProduto;
    private javax.swing.JScrollPane scSoma;
    private javax.swing.JTable tbEstoque;
    private javax.swing.JTable tbNotaCompra;
    private javax.swing.JTable tbProduto;
    private javax.swing.JTable tbSoma;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtPesquisa;
    private javax.swing.JTextField txtProduto;
    private javax.swing.JTextField txtQuantidade;
    private javax.swing.JTextField txtQuantidadeInicial;
    private javax.swing.JTextField txtValorFinal;
    private javax.swing.JTextField txtValorUnidade;
    // End of variables declaration//GEN-END:variables
}
