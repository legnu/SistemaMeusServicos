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
import static br.com.MeusServicos.telas.PontoDeVendas.lblValorTotal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

public class TelaCompra extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaCompra() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    public void InstanciarCombobox() {
        try {

            String sql = "select nome_fornecedor from tbfornecedor";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbFornecedor.addItem(rs.getString("nome_fornecedor"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

            Limpar();

        }
    }

    public void InstanciarComboboxPagamento() {
        if (String.valueOf(cbPagamento.getSelectedItem()).equals("A Vista (Dinhero)") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Debito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Pix") == true) {
            cbNumero.setSelectedItem("1x");
            cbNumero.setEnabled(false);
            dtPagamento.setEnabled(true);

        } else if (String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Credito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Crediario") == true) {
            cbNumero.setEnabled(true);
            dtPagamento.setDate(null);
            dtPagamento.setEnabled(false);
        }
    }

    public void iniciar() {
        instanciarTabela();
        InstanciarCombobox();
        instanciarTabelaNotaCompra();
        instanciarTabelaSoma();
        txtValorUnidade.setText("0.00");
        txtValorFinal.setText("0.00");
    }

    public void adicionarCompra() {
        int quantidadeAdicionada;
        int quantidadeInicial;
        int quantidadeFinal;

        try {

            quantidadeAdicionada = Integer.parseInt(txtQuantidade.getText());
            quantidadeInicial = Integer.parseInt(txtQuantidadeInicial.getText());
            quantidadeFinal = quantidadeAdicionada + quantidadeInicial;

            String sql = "insert into tbcompra(nome_produto, valor, valor_unidade, fornecedor, quantidade_comprada)values(?,?,?,?,?)";

            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtProduto.getText());
            pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(txtValorFinal.getText())).replace(",", "."));
            pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(txtValorUnidade.getText())).replace(",", "."));
            pst.setString(4, cbFornecedor.getSelectedItem().toString());
            pst.setString(5, txtQuantidade.getText());

            //Validação dos Campos Obrigatorios
            if ((txtProduto.getText().isEmpty()) || (txtValorFinal.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios.");
                Limpar();

            } else {

                if (quantidadeAdicionada <= 0) {
                    JOptionPane.showMessageDialog(null, "Quantidade Adicionada deve ser maior que 0.");
                    Limpar();
                }
                if ((txtProduto.getText().isEmpty()) || (txtValorFinal.getText().isEmpty()) || (txtQuantidade.getText().isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios.");
                    Limpar();

                } else {
                    pst.executeUpdate();

                    String sqr = "update tbprodutos set quantidade=? where idproduto=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setInt(1, quantidadeFinal);
                    pst.setString(2, txtID.getText());
                    int adicionado = pst.executeUpdate();

                    double valor_compra = Double.parseDouble(txtValorUnidade.getText().replace(".", "")) / 100;
                    double referencial_compra = quantidadeFinal * valor_compra;

                    String sqy = "update tbprodutos set referencial_compra=? where idproduto=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(referencial_compra))).replace(",", "."));
                    pst.setString(2, txtID.getText());
                    pst.executeUpdate();

                    String squ = "select valor_venda from tbprodutos where produto=?";

                    pst = conexao.prepareStatement(squ);
                    pst.setString(1, txtProduto.getText());
                    rs = pst.executeQuery();
                    tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));

                    double valor_venda = Double.parseDouble(tbEstoque.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;
                    double referencial_venda = quantidadeFinal * valor_venda;

                    String sqo = "update tbprodutos set referencial_venda=? where idproduto=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(referencial_venda))).replace(",", "."));
                    pst.setString(2, txtID.getText());
                    pst.executeUpdate();

                    //A Linha abaixo serve de apoio ao entendimento da logica
                    //System.out.println(adicionado);
                    if (adicionado > 0) {
                        JOptionPane.showMessageDialog(null, "Produto(s) Adicionado(s) com sucesso.");
                        Limpar();

                        instanciarTabelaNotaCompra();
                        instanciarTabelaSoma();

                    }

                }

            }
        } catch (java.lang.NumberFormatException e) {
            if (txtQuantidade.getText().isEmpty() == true) {
                JOptionPane.showMessageDialog(null, "Quantidade não pode ser nula.");                
                Limpar();
            } else {               
                JOptionPane.showMessageDialog(null, "Quantidade deve ser um numero Inteiro.");
                Limpar();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();
        }
    }

    public void instanciarTabela() {
        String sql = "select idproduto as ID, produto as Produto,valor_compra as Preço, quantidade as Quantidade, fornecedor as Fornecedor from tbprodutos where estoque=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, "Com controle de estoque.");
            rs = pst.executeQuery();
            tbProduto.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    public void instanciarTabelaNotaCompra() {
        String sqr = "select idcompra as ID_Compra, nome_produto as Produto,fornecedor as Fornecedor, quantidade_comprada as Quantidade_Adicionada, valor as Valor_Total from tbcompra";
        try {
            pst = conexao.prepareStatement(sqr);
            rs = pst.executeQuery();
            tbNotaCompra.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    public void instanciarTabelaSoma() {
        double preco, x;

        String sql = "select valor from tbcompra";
        try {

            preco = 0;

            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbSoma.setModel(DbUtils.resultSetToTableModel(rs));
            for (int i = 0; i < tbSoma.getRowCount(); i++) {
                x = Double.parseDouble(tbSoma.getModel().getValueAt(i, 0).toString().replace(".", "")) / 100;
                preco = preco + x;

            }

            lblValorTotal.setText(new DecimalFormat("#,##0.00").format(preco).replace(",", "."));
        } catch (java.lang.NullPointerException e) {
            lblValorTotal.setText("0.00");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

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
            txtValorFinal.setText(String.valueOf(calculo));
            if (Integer.parseInt(txtQuantidade.getText()) <= 0) {
                txtValorFinal.setText("0.00");
            }

        } catch (java.lang.NumberFormatException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    public void comprar() {
        int acumulo = 1;
        try {
            if (Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100 <= 0) {
                JOptionPane.showMessageDialog(null, "Valor total deve ser maior que 0");
                Limpar();
            } else if (String.valueOf(cbPagamento.getSelectedItem()).equals("A Vista (Dinhero)") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Debito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Pix") == true) {
                double preco;

                preco = Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100;

                String compra = JOptionPane.showInputDialog("Escreva um identificador para esta compra.");

                Date d = new Date();
                Date y = dtPagamento.getDate();
                String status;

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);

                java.sql.Date dSqo = new java.sql.Date(y.getTime());
                df.format(dSqo);

                if (d.before(y)) {
                    status = "Pendente";
                } else {
                    status = "Pago";

                }

                String sql = "insert into tbgastos(valor, data_emissao, status_pagamento, data_pagamento, nome, forma_pagamento)values(?,?,?,?,?,?)";

                pst = conexao.prepareStatement(sql);

                pst.setString(1, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(preco))).replace(",", "."));
                pst.setDate(2, dSql);
                pst.setString(3, status);
                pst.setDate(4, dSqo);
                pst.setString(5, compra);
                pst.setString(6, cbPagamento.getSelectedItem().toString());
                pst.executeUpdate();

                try {
                    String sqr = "delete from tbcompra";
                    pst = conexao.prepareStatement(sqr);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                    tirarId();
                    criarId();
                    JOptionPane.showMessageDialog(null, "Produto(s) comprado(s) com sucesso.");
                    Limpar();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                    Limpar();

                }

            } else if (String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Credito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Crediario") == true) {

                Date d = new Date();
                int parcela = 1;

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);
                String compra = JOptionPane.showInputDialog("Escreva um identificador para esta compra.");
                
                for (int i = 0; i < Integer.parseInt(String.valueOf(cbNumero.getSelectedItem()).replace("x", "")); i++) {

                    String mes = new SimpleDateFormat("MM").format(d);
                    String ano = new SimpleDateFormat("yyyy").format(d);

                    System.out.println(mes + "x" + ano);

                    int limite = Integer.parseInt(mes) + acumulo;

                    if (limite > 12) {
                        limite = limite - 12;
                        ano = String.valueOf(Integer.parseInt(ano) + 1);
                    }

                    String dataLimite = ano + "-" + limite + "-05";

                    Date data = df.parse(dataLimite);
                    java.sql.Date dSqt = new java.sql.Date(data.getTime());
                    df.format(dSqt);

                    String sql = "insert into tbgastos(valor, data_emissao, status_pagamento, data_pagamento, nome, forma_pagamento)values(?,?,?,?,?,?)";

                    pst = conexao.prepareStatement(sql);

                    pst.setString(1, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100)) / Integer.parseInt(String.valueOf(cbNumero.getSelectedItem()).replace("x", ""))).replace(",", "."));
                    pst.setDate(2, dSql);
                    pst.setString(3, "Pendente");
                    pst.setDate(4, dSqt);
                    pst.setString(5, compra + " Parcela: "+parcela+"/"+String.valueOf(cbNumero.getSelectedItem()).replace("x",""));
                    pst.setString(6, cbPagamento.getSelectedItem().toString());
                    pst.executeUpdate();
                    acumulo++;
                    parcela++;
                }

                try {
                    String sqr = "delete from tbcompra";
                    pst = conexao.prepareStatement(sqr);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                    tirarId();
                    criarId();
                    JOptionPane.showMessageDialog(null, "Produto(s) comprado(s) com sucesso.");
                    Limpar();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                    Limpar();

                }

            }

        } catch (java.lang.NumberFormatException e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        } catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Adicione uma Data de Pagamento.");
            Limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }

    }

    public void remover() {

        int quantidadeAdicionada;
        int quantidadeAtual;
        int quantidade;
        double referencial_compra;

        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este produto?", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) {

            try {

                quantidadeAdicionada = Integer.parseInt(txtQuantidade.getText());
                quantidadeAtual = Integer.parseInt(txtQuantidadeInicial.getText());

                quantidade = quantidadeAtual - quantidadeAdicionada;

                String sqy = "select referencial_compra from tbprodutos where produto=?";

                pst = conexao.prepareStatement(sqy);
                pst.setString(1, txtProduto.getText());
                rs = pst.executeQuery();
                tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));

                referencial_compra = Double.parseDouble(tbEstoque.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                String squ = "select valor_venda from tbprodutos where produto=?";

                pst = conexao.prepareStatement(squ);
                pst.setString(1, txtProduto.getText());
                rs = pst.executeQuery();
                tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));

                double valor_venda = Double.parseDouble(tbEstoque.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                String sqt = "select referencial_venda from tbprodutos where produto=?";

                pst = conexao.prepareStatement(sqt);
                pst.setString(1, txtProduto.getText());
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

                double referencial_venda = Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                double x = valor_venda * Double.parseDouble(txtQuantidade.getText());

                String sqr = "update tbprodutos set quantidade=?, referencial_compra=?, referencial_venda=? where produto=?";
                double y = referencial_compra - Double.parseDouble(txtValorFinal.getText());
                double z = referencial_venda - x;

                pst = conexao.prepareStatement(sqr);
                pst.setInt(1, quantidade);
                pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(y))).replace(",", "."));
                pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(z))).replace(",", "."));
                pst.setString(4, txtProduto.getText());
                pst.executeUpdate();

                String sql = "delete from tbcompra where idcompra=?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtID.getText());
                int apagado = pst.executeUpdate();

                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                    tirarId();
                    criarId();
                    JOptionPane.showMessageDialog(null, "Produto removido com sucesso.");
                    Limpar();

                }
            } catch (java.lang.NullPointerException e) {

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                Limpar();

            }
        }
    }

    public void setar_campos() {
        Limpar();
        int setar = tbProduto.getSelectedRow();
        cbFornecedor.addItem(tbProduto.getModel().getValueAt(setar, 4).toString());
        txtID.setText(tbProduto.getModel().getValueAt(setar, 0).toString());
        txtProduto.setText(tbProduto.getModel().getValueAt(setar, 1).toString());
        String valor = new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(Double.parseDouble(tbProduto.getModel().getValueAt(setar, 2).toString().replace(".", "")) / 100))).replace(",", ".");
        txtValorUnidade.setText(String.valueOf(valor));
        txtQuantidadeInicial.setText(tbProduto.getModel().getValueAt(setar, 3).toString());
        cbFornecedor.setSelectedItem(tbProduto.getModel().getValueAt(setar, 4).toString());

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
            cbFornecedor.setSelectedItem(tbNotaCompra.getModel().getValueAt(setar, 2).toString());
            txtQuantidade.setText(tbNotaCompra.getModel().getValueAt(setar, 3).toString());
            double valor = Double.parseDouble(tbNotaCompra.getModel().getValueAt(setar, 4).toString().replace(".", "")) / 100;
            txtValorFinal.setText(String.valueOf(valor));

            String sql = "select quantidade from tbprodutos where produto=?";

            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtProduto.getText());
            rs = pst.executeQuery();
            tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));
            txtQuantidadeInicial.setText(tbEstoque.getModel().getValueAt(0, 0).toString());

            String sqy = "select valor_compra from tbprodutos where produto=?";

            pst = conexao.prepareStatement(sqy);
            pst.setString(1, txtProduto.getText());
            rs = pst.executeQuery();
            tbEstoque.setModel(DbUtils.resultSetToTableModel(rs));
            double valorUnidade = Double.parseDouble(tbEstoque.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;
            txtValorUnidade.setText(String.valueOf(valorUnidade));

            txtPesquisa.setText(null);
            txtQuantidade.setEnabled(false);
            btnAdicionar.setEnabled(false);
            btnRemove.setEnabled(true);
        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    private void criarId() {
        String sql = "alter table tbcompra add idcompra MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }
    }

    private void tirarId() {

        String sql = "alter table tbcompra drop idcompra";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            Limpar();

        }

    }

    public void Limpar() {
        instanciarTabelaSoma();
        instanciarTabelaNotaCompra();

        dtPagamento.setDate(null);
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

    public void CadastroProduto() {
        CadProduto produto = new CadProduto();
        produto.setVisible(true);

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
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        txtID = new javax.swing.JTextField();
        lblPesquisar = new javax.swing.JLabel();
        lblCamposObrigatorios = new javax.swing.JLabel();
        lblNomeProduto = new javax.swing.JLabel();
        lblValorUnidade = new javax.swing.JLabel();
        lblQuantidadeInicial = new javax.swing.JLabel();
        lblQuantidade = new javax.swing.JLabel();
        lblValorFinal = new javax.swing.JLabel();
        lblFornecedor = new javax.swing.JLabel();
        txtPesquisa = new javax.swing.JTextField();
        txtProduto = new javax.swing.JTextField();
        txtValorUnidade = new javax.swing.JTextField();
        txtQuantidadeInicial = new javax.swing.JTextField();
        txtQuantidade = new javax.swing.JTextField();
        txtValorFinal = new javax.swing.JTextField();
        cbFornecedor = new javax.swing.JComboBox<>();
        btnAdicionar = new javax.swing.JButton();
        pnNota = new javax.swing.JPanel();
        btnRemove = new javax.swing.JButton();
        btnComprar = new javax.swing.JToggleButton();
        lblTotal = new javax.swing.JLabel();
        lblValorTotal = new javax.swing.JLabel();
        dtPagamento = new com.toedter.calendar.JDateChooser();
        lblDataPagamento = new javax.swing.JLabel();
        pnTbNota = new javax.swing.JPanel();
        scNotaProduto1 = new javax.swing.JScrollPane();
        tbNotaCompra = new javax.swing.JTable();
        pnPagamento = new javax.swing.JPanel();
        cbPagamento = new javax.swing.JComboBox<>();
        cbNumero = new javax.swing.JComboBox<>();
        pnProduto = new javax.swing.JPanel();
        scProduto = new javax.swing.JScrollPane();
        tbProduto = new javax.swing.JTable();
        jToggleButton1 = new javax.swing.JToggleButton();

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

        txtID.setEditable(false);
        txtID.setEnabled(false);
        txtID.setFocusable(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tela de Compra");
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

        lblValorUnidade.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblValorUnidade.setText("*Valor Unidade:");

        lblQuantidadeInicial.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblQuantidadeInicial.setText("*Quantidade Atual:");

        lblQuantidade.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblQuantidade.setText("*Quantidade Comprada:");

        lblValorFinal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblValorFinal.setText("*Valor Final:");

        lblFornecedor.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblFornecedor.setText("*Fornecedor:");

        txtProduto.setEditable(false);
        txtProduto.setEnabled(false);
        txtProduto.setFocusable(false);

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

        cbFornecedor.setEnabled(false);

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

        pnNota.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

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
        btnComprar.setPreferredSize(new java.awt.Dimension(110, 41));
        btnComprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarActionPerformed(evt);
            }
        });

        lblTotal.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTotal.setText("Total:");

        lblValorTotal.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblValorTotal.setText("0.00");

        lblDataPagamento.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblDataPagamento.setText("*Data do Pagamento:");

        pnTbNota.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nota", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

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

        javax.swing.GroupLayout pnTbNotaLayout = new javax.swing.GroupLayout(pnTbNota);
        pnTbNota.setLayout(pnTbNotaLayout);
        pnTbNotaLayout.setHorizontalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scNotaProduto1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE))
        );
        pnTbNotaLayout.setVerticalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 341, Short.MAX_VALUE)
            .addGroup(pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scNotaProduto1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
        );

        pnPagamento.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Forma de Pagamento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cbPagamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A Vista (Dinhero)", "Cartão Debito", "Pix", "Cartão Credito", "Crediario" }));
        cbPagamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPagamentoActionPerformed(evt);
            }
        });

        cbNumero.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        cbNumero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1x", "2x", "3x", "4x", "5x", "6x", "7x", "8x", "9x", "10x", "11x", "12x" }));
        cbNumero.setEnabled(false);

        javax.swing.GroupLayout pnPagamentoLayout = new javax.swing.GroupLayout(pnPagamento);
        pnPagamento.setLayout(pnPagamentoLayout);
        pnPagamentoLayout.setHorizontalGroup(
            pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPagamentoLayout.createSequentialGroup()
                .addComponent(cbPagamento, 0, 133, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        pnPagamentoLayout.setVerticalGroup(
            pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPagamentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cbPagamento)
                .addComponent(cbNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnNotaLayout = new javax.swing.GroupLayout(pnNota);
        pnNota.setLayout(pnNotaLayout);
        pnNotaLayout.setHorizontalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(pnTbNota, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnNotaLayout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addComponent(lblTotal)
                            .addGap(12, 12, 12)
                            .addComponent(lblValorTotal)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnNotaLayout.createSequentialGroup()
                            .addComponent(btnComprar, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnNotaLayout.createSequentialGroup()
                        .addComponent(lblDataPagamento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dtPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnNotaLayout.setVerticalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDataPagamento)
                    .addComponent(dtPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnTbNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnNotaLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTotal)
                            .addComponent(lblValorTotal)))
                    .addComponent(pnPagamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnComprar, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        pnProduto.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Produtos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

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

        javax.swing.GroupLayout pnProdutoLayout = new javax.swing.GroupLayout(pnProduto);
        pnProduto.setLayout(pnProdutoLayout);
        pnProdutoLayout.setHorizontalGroup(
            pnProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scProduto, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
        );
        pnProdutoLayout.setVerticalGroup(
            pnProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scProduto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jToggleButton1.setBackground(new java.awt.Color(0, 0, 0));
        jToggleButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jToggleButton1.setForeground(new java.awt.Color(255, 255, 255));
        jToggleButton1.setText("Cadastrar Produto");
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(lblPesquisar)
                                .addGap(12, 12, 12)
                                .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblCamposObrigatorios)
                                .addGap(2, 2, 2))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(14, Short.MAX_VALUE)
                                .addComponent(pnProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblNomeProduto)
                                        .addGap(12, 12, 12)
                                        .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblValorUnidade)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(txtValorUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(lblValorFinal)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblFornecedor)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(cbFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(lblQuantidadeInicial)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(txtQuantidadeInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(7, 7, 7)
                                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(pnNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblCamposObrigatorios))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(lblPesquisar)))
                        .addGap(17, 17, 17)
                        .addComponent(pnProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(lblNomeProduto))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jToggleButton1)))
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblQuantidadeInicial)
                            .addComponent(txtQuantidadeInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblQuantidade)
                            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblValorFinal)
                            .addComponent(txtValorUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblValorUnidade))
                        .addGap(18, 18, 18)
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(pnNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
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

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        CadastroProduto();

    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void cbPagamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPagamentoActionPerformed
        // TODO add your handling code here:
        InstanciarComboboxPagamento();
    }//GEN-LAST:event_cbPagamentoActionPerformed

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
            java.util.logging.Logger.getLogger(TelaCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaCompra().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JToggleButton btnComprar;
    private javax.swing.JButton btnRemove;
    private javax.swing.JComboBox<String> cbFornecedor;
    private javax.swing.JComboBox<String> cbNumero;
    private javax.swing.JComboBox<String> cbPagamento;
    private com.toedter.calendar.JDateChooser dtPagamento;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblCamposObrigatorios;
    private javax.swing.JLabel lblDataPagamento;
    private javax.swing.JLabel lblFornecedor;
    private javax.swing.JLabel lblNomeProduto;
    private javax.swing.JLabel lblPesquisar;
    private javax.swing.JLabel lblQuantidade;
    private javax.swing.JLabel lblQuantidadeInicial;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblValorFinal;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JLabel lblValorUnidade;
    private javax.swing.JPanel pnNota;
    private javax.swing.JPanel pnPagamento;
    private javax.swing.JPanel pnProduto;
    private javax.swing.JPanel pnTbNota;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scEstoque;
    private javax.swing.JScrollPane scNotaProduto1;
    private javax.swing.JScrollPane scProduto;
    private javax.swing.JScrollPane scSoma;
    private javax.swing.JTable tbAuxilio;
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
