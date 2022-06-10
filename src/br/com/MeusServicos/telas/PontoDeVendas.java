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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.joda.time.LocalDateTime;

/**
 *
 * @author Ad3ln0r
 */
public class PontoDeVendas extends javax.swing.JFrame {

    /**
     * Creates new form PontoDeVendas
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String tipo;

    public PontoDeVendas() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    public void iniciar() {
        instanciarTabelaVenda();
        txtPreco.setText("0.00");
        tipo = "Produto";
        rbOrdemDeServico.setSelected(true);
        soma();

    }

    public void CadastroCliente() {
        TelaClientesVendas cliente = new TelaClientesVendas();
        cliente.setVisible(true);

    }

    public void AdicionarDesconto() {
        telaDesconto desconto = new telaDesconto();
        desconto.setVisible(true);

    }

    public void criarNota() {
        String sql = "insert into tbvenda(nome, preco, cliente_venda, pagamento, quantidade)values(?,?,?,?,?)";

        double precoProduto;
        double precoOS;

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtNome.getText());

            if (tipo.equals("Produto")) {
                precoProduto = Double.parseDouble(lblPrecoFinal.getText());
                pst.setString(2, new DecimalFormat("#,##0.00").format(precoProduto).replace(",", "."));
            }
            if (tipo.equals("OS")) {
                precoOS = Double.parseDouble(txtPreco.getText());
                pst.setString(2, new DecimalFormat("#,##0.00").format(precoOS).replace(",", "."));
            }

            pst.setString(3, txtCliente.getText());
            pst.setString(4, cbFormaDePagamento.getSelectedItem().toString());
            pst.setString(5, txtQuantidade.getText());

            //Validação dos Campos Obrigatorios
            if ((txtNome.getText().isEmpty()) || (txtPreco.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");

            } else if ((txtQuantidade.getText().isEmpty()) && tipo.equals("Produto")) {
                JOptionPane.showMessageDialog(null, "Adicione uma Quantidade.");
                limpar();

            } else if (Integer.parseInt(txtQuantidade.getText()) <= 0 && tipo.equals("Produto")) {
                JOptionPane.showMessageDialog(null, "Adicione uma quantidade maior que 0");
                limpar();

            } else if (Integer.parseInt(txtEstoque.getText()) <= 0 && tipo.equals("Produto")) {
                JOptionPane.showMessageDialog(null, "Sem estoque de " + txtNome.getText() + ".");
                limpar();

            } else if (tipo.equals("Produto")) {
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Item adicionado com sucesso");
                    instanciarTabelaVenda();
                    QuantidadeTirada();
                    limpar();

                }

            }

            if (tipo.equals("OS")) {

                //A linha abaixo atualiza os dados do novo usuario
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Item adicionado com sucesso");
                    instanciarTabelaVenda();
                    limpar();

                }
            }

            if (tipo.equals("Cliente")) {
            } else if (txtQuantidade.getText().equals("0")) {
                tipo = "OS";

            } else {
                tipo = "Produto";
            }

        } catch (java.lang.NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Quantidade não pode ser Nula");
            limpar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void pesquisar() {

        if (tipo.equals("Produto")) {

            String sql = "select produto as Produto, preco as Preço, obs as Observações, idproduto as ID, fornecedor as Fornecedor, custo as Custo from tbprodutos where produto like ?";

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

        }
        if (tipo.equals("OS")) {

            String sql = "select servico as Serviço, Valor as Valor, equipamento as Equipamento, previsao_entreg_os as Entrega, defeito as Defeito, os as OS, tecnico as Tecnico, data_os as Data, idcli as ID_Cliente from tbos where servico like ?";

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

        }
        if (tipo.equals("Cliente")) {

            String sql = "select idcli as ID, nomecli as Nome, telefonecli as Telefone, endcli as Endereço from tbclientes where nomecli like ?";

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este item?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {

            try {
                if (txtQuantidade.getText().equals("0")) {

                    String sql = "delete from tbvenda where idvenda=?";
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtID.getText());
                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "Item removido com sucesso");
                        tirarId();
                        criarId();
                        limpar();

                    }
                } else {
                    QuantidadeAdicionada();
                    String sql = "delete from tbvenda where idvenda=?";
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtID.getText());
                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "Item removido com sucesso");
                        tirarId();
                        criarId();
                        limpar();

                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void tirarId() {

        String sql = "alter table tbvenda drop idvenda";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void limpar_nota() {
        String sql = "delete from tbvenda";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();
            ((DefaultTableModel) tbTotal.getModel()).setRowCount(0);
            lblValorTotal.setText("0.00");
            tirarId();
            criarId();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void criarId() {
        String sql = "alter table tbvenda add idvenda MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void QuantidadeTirada() {
        try {
            int quantidadeEstoque = Integer.parseInt(txtEstoque.getText());
            int quantidadeVendida = Integer.parseInt(txtQuantidade.getText());
            int quantidade = quantidadeEstoque - quantidadeVendida;

            String sql = "update tbprodutos set quantidade=? where produto=?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, String.valueOf(quantidade));
            pst.setString(2, txtNome.getText());
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void QuantidadeAdicionada() {
        try {
            int quantidadeEstoque = Integer.parseInt(txtEstoque.getText());
            int quantidadeVendida = Integer.parseInt(txtQuantidade.getText());
            int quantidade = quantidadeEstoque + quantidadeVendida;

            String sql = "update tbprodutos set quantidade=? where produto=?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, String.valueOf(quantidade));
            pst.setString(2, txtNome.getText());
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaVenda() {
        String sql = "select idvenda as ID, nome as Nome, preco as Preço, cliente_venda as Cliente, pagamento as Forma_de_Pagamento, quantidade as Quantidade from tbvenda";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbItens.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void imprimir_nota() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao desta Nota?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {

                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/RelatorioPDV.jasper"), null, conexao);

                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    public void soma() {
        double preco;

        String sql = "select sum(preco) FROM tbvenda;";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbTotal.setModel(DbUtils.resultSetToTableModel(rs));
            preco = Double.parseDouble((tbTotal.getModel().getValueAt(0, 0).toString()));
            lblValorTotal.setText(new DecimalFormat("#,##0.00").format(preco).replace(",", "."));

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void instanciarTabelaCliente() {
        String sql = "select idcli as ID, nomecli as Nome, telefonecli as Telefone, endcli as Endereço from tbclientes";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));

            tipo = "Cliente";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaProduto() {
        String sql = "select produto as Produto, preco as Preço, obs as Observações, idproduto as ID, fornecedor as Fornecedor, custo as Custo, estoque as Estoque, quantidade as Quantidade from tbprodutos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
            txtQuantidade.setEnabled(true);
            tipo = "Produto";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaOS() {
        String sql = "select servico as Serviço, Valor as Valor, equipamento as Equipamento, previsao_entreg_os as Entrega, defeito as Defeito, os as OS, tecnico as Tecnico, data_os as Data, idcli as ID_Cliente from tbos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
            txtQuantidade.setEnabled(false);

            tipo = "OS";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void habilitarAdicionar() {
        btnAdicionar.setEnabled(true);
        btnRemove.setEnabled(false);

    }

    public void calculoQuantidade() {

        try {
            double preco = Double.parseDouble(txtPreco.getText());
            double quantidade = Double.parseDouble(txtQuantidade.getText());
            double precoFinal = preco * quantidade;

            lblPrecoFinal.setText(String.valueOf(precoFinal));
        } catch (java.lang.NumberFormatException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void criar_relatorio() {

        try {

            int linha;
            linha = tbItens.getRowCount();
            taRelatorio.append("Descrição" + "\n\n\n");

            for (int i = 0; i <= linha; i++) {
                taRelatorio.append("OS/Produto: " + tbItens.getModel().getValueAt(i, 1).toString() + 
                        "          Cliente: " + tbItens.getModel().getValueAt(i, 3).toString() + 
                        "          Forma De Pagamento: " + tbItens.getModel().getValueAt(i, 4).toString() + 
                        "          Preço(R$): " + tbItens.getModel().getValueAt(i, 2).toString() + 
                        "          Quantidade: " + tbItens.getModel().getValueAt(i, 5).toString() + 
                        "\n\n");

            }

        } catch (Exception sqlEx) {

        }
    }

    public void concluir() {

        String sql = "insert into tbtotalvendas(dia, hora, venda, obs)values(?,?,?,?)";

        double preco;

        preco = Double.parseDouble(lblValorTotal.getText());
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("dd/mm/yyyy").format(dataHoraAtual);
        String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);
        
        
        try {
            pst = conexao.prepareStatement(sql);
            criar_relatorio();
            pst.setString(1, data);
            pst.setString(2, hora);
            pst.setString(3, new DecimalFormat("#,##0.00").format(preco).replace(",", "."));
            pst.setString(4, taRelatorio.getText());

            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void setar_campos() {
        habilitarAdicionar();
        int setar = tbListaDeInformaçoes.getSelectedRow();

        if (tipo.equals("Cliente")) {
            txtCliente.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString());
            btnAdicionar.setEnabled(true);
        }
        if (tipo.equals("OS")) {

            txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
            txtPreco.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString().replace(",", "."));
            taOBS.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString());
            txtCliente.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 8).toString());

            String sql = "select nomecli FROM tbclientes where idcli=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliente.getText());
                rs = pst.executeQuery();
                tbCliente.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

            txtCliente.setText(tbCliente.getModel().getValueAt(0, 0).toString());
            txtQuantidade.setEnabled(false);
            txtQuantidade.setText("0");
            txtEstoque.setText("0");
            btnAdicionar.setEnabled(true);
        }
        if (tipo.equals("Produto")) {

            txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
            txtPreco.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString().replace(",", "."));
            taOBS.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString());
            txtEstoque.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 7).toString());
            txtCliente.setText("Caixa");
            btnAdicionar.setEnabled(true);
        }
    }

    public void setarCamposRemover() {
        int setar = tbItens.getSelectedRow();
        limpar();
        try {
            txtID.setText(tbItens.getModel().getValueAt(setar, 0).toString());
            txtNome.setText(tbItens.getModel().getValueAt(setar, 1).toString());
            txtPreco.setText(tbItens.getModel().getValueAt(setar, 2).toString().replace(",", "."));
            txtCliente.setText(tbItens.getModel().getValueAt(setar, 3).toString());
            cbFormaDePagamento.setSelectedItem(tbItens.getModel().getValueAt(setar, 4).toString());
            txtQuantidade.setText(tbItens.getModel().getValueAt(setar, 5).toString());

            if (txtQuantidade.getText().equals("0") == false) {
                String sql = "select quantidade FROM tbprodutos where produto=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtNome.getText());
                rs = pst.executeQuery();
                tbSetar.setModel(DbUtils.resultSetToTableModel(rs));
                txtEstoque.setText(tbSetar.getModel().getValueAt(0, 0).toString());
            }

            btnAdicionar.setEnabled(false);
            btnRemove.setEnabled(true);
            txtQuantidade.setEnabled(false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void limpar() {
        txtNome.setText(null);
        txtPreco.setText(null);
        txtID.setText(null);
        txtCliente.setText(null);
        taOBS.setText(null);
        taRelatorio.setText(null);
        txtEstoque.setText(null);
        txtQuantidade.setText(null);
        btnAdicionar.setEnabled(false);
        btnRemove.setEnabled(false);
        txtQuantidade.setEnabled(false);
        lblValorTotal.setText("0.00");
        ((DefaultTableModel) tbListaDeInformaçoes.getModel()).setRowCount(0);
        instanciarTabelaVenda();
        soma();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        OS_Produtos = new javax.swing.ButtonGroup();
        scTotal = new javax.swing.JScrollPane();
        tbTotal = new javax.swing.JTable();
        scCliente = new javax.swing.JScrollPane();
        tbCliente = new javax.swing.JTable();
        lblPrecoFinal = new javax.swing.JLabel();
        scSetar = new javax.swing.JScrollPane();
        tbSetar = new javax.swing.JTable();
        scRelatorio = new javax.swing.JScrollPane();
        taRelatorio = new javax.swing.JTextArea();
        scRelatoriotb = new javax.swing.JScrollPane();
        tbRelatorio = new javax.swing.JTable();
        lblPesquisa = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        lblPreco = new javax.swing.JLabel();
        lblOBS = new javax.swing.JLabel();
        lblID = new javax.swing.JLabel();
        lblUsuarioPDV = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        lblCamposObrigatorios = new javax.swing.JLabel();
        rbOrdemDeServico = new javax.swing.JRadioButton();
        rbProduto = new javax.swing.JRadioButton();
        rbCliente = new javax.swing.JRadioButton();
        txtPesquisa = new javax.swing.JTextField();
        txtNome = new javax.swing.JTextField();
        txtPreco = new javax.swing.JTextField();
        btnAdicionar = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnCliente = new javax.swing.JButton();
        scPdv = new javax.swing.JScrollPane();
        tbListaDeInformaçoes = new javax.swing.JTable();
        scText = new javax.swing.JScrollPane();
        taOBS = new javax.swing.JTextArea();
        txtID = new javax.swing.JTextField();
        txtCliente = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        scItens = new javax.swing.JScrollPane();
        tbItens = new javax.swing.JTable();
        lblTotal = new javax.swing.JLabel();
        lblValorTotal = new javax.swing.JLabel();
        btnConcluir = new javax.swing.JButton();
        btnConcluir1 = new javax.swing.JButton();
        lblEstoque = new javax.swing.JLabel();
        txtEstoque = new javax.swing.JTextField();
        lblQuantidade = new javax.swing.JLabel();
        txtQuantidade = new javax.swing.JTextField();
        lblFormadePagamento = new javax.swing.JLabel();
        cbFormaDePagamento = new javax.swing.JComboBox<>();

        tbTotal.setModel(new javax.swing.table.DefaultTableModel(
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
        scTotal.setViewportView(tbTotal);

        tbCliente.setModel(new javax.swing.table.DefaultTableModel(
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
        scCliente.setViewportView(tbCliente);

        lblPrecoFinal.setText("jLabel1");

        tbSetar.setModel(new javax.swing.table.DefaultTableModel(
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
        scSetar.setViewportView(tbSetar);

        taRelatorio.setColumns(20);
        taRelatorio.setRows(5);
        scRelatorio.setViewportView(taRelatorio);

        tbRelatorio.setModel(new javax.swing.table.DefaultTableModel(
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
        scRelatoriotb.setViewportView(tbRelatorio);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        lblPesquisa.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisa.setText("Pesquisa:");

        lblNome.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblNome.setText("*Nome:");

        lblPreco.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPreco.setText("*Preço(R$):");

        lblOBS.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblOBS.setText("OBS:");

        lblID.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblID.setText("ID:");

        lblUsuarioPDV.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblUsuarioPDV.setText("Usuario");

        lblCliente.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCliente.setText("Cliente:");

        lblCamposObrigatorios.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCamposObrigatorios.setText("*Campos Obrigatorios");

        OS_Produtos.add(rbOrdemDeServico);
        rbOrdemDeServico.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbOrdemDeServico.setText("Ordem De Serviço");
        rbOrdemDeServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbOrdemDeServicoActionPerformed(evt);
            }
        });

        OS_Produtos.add(rbProduto);
        rbProduto.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbProduto.setText("Produtos");
        rbProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbProdutoActionPerformed(evt);
            }
        });

        OS_Produtos.add(rbCliente);
        rbCliente.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbCliente.setText("Clientes");
        rbCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbClienteActionPerformed(evt);
            }
        });

        txtPesquisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaKeyReleased(evt);
            }
        });

        txtNome.setEditable(false);
        txtNome.setEnabled(false);
        txtNome.setFocusable(false);
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        txtPreco.setEditable(false);
        txtPreco.setEnabled(false);
        txtPreco.setFocusable(false);

        btnAdicionar.setBackground(new java.awt.Color(51, 255, 51));
        btnAdicionar.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
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

        btnRemove.setBackground(new java.awt.Color(255, 0, 0));
        btnRemove.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
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

        btnCliente.setBackground(new java.awt.Color(0, 0, 0));
        btnCliente.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnCliente.setText("Cadastrar Cliente");
        btnCliente.setBorderPainted(false);
        btnCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteActionPerformed(evt);
            }
        });

        scPdv.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scPdvMouseClicked(evt);
            }
        });

        tbListaDeInformaçoes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbListaDeInformaçoes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbListaDeInformaçoes.setFocusable(false);
        tbListaDeInformaçoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbListaDeInformaçoesMouseClicked(evt);
            }
        });
        scPdv.setViewportView(tbListaDeInformaçoes);

        taOBS.setEditable(false);
        taOBS.setColumns(20);
        taOBS.setRows(5);
        taOBS.setEnabled(false);
        taOBS.setFocusable(false);
        scText.setViewportView(taOBS);

        txtID.setEnabled(false);

        txtCliente.setEditable(false);
        txtCliente.setEnabled(false);
        txtCliente.setFocusable(false);
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Nota", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        tbItens = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbItens.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        tbItens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbItens.setFocusable(false);
        tbItens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbItensMouseClicked(evt);
            }
        });
        scItens.setViewportView(tbItens);

        lblTotal.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lblTotal.setText("Total:");

        lblValorTotal.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lblValorTotal.setText("0.00");

        btnConcluir.setBackground(new java.awt.Color(51, 51, 255));
        btnConcluir.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btnConcluir.setForeground(new java.awt.Color(255, 255, 255));
        btnConcluir.setText("Concluir");
        btnConcluir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnConcluir.setBorderPainted(false);
        btnConcluir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConcluirActionPerformed(evt);
            }
        });

        btnConcluir1.setBackground(new java.awt.Color(102, 0, 204));
        btnConcluir1.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btnConcluir1.setForeground(new java.awt.Color(255, 255, 255));
        btnConcluir1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/minus.png"))); // NOI18N
        btnConcluir1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnConcluir1.setBorderPainted(false);
        btnConcluir1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConcluir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConcluir1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scItens, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                    .addComponent(btnConcluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblValorTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnConcluir1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scItens, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTotal)
                        .addComponent(lblValorTotal))
                    .addComponent(btnConcluir1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(btnConcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        lblEstoque.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblEstoque.setText("Estoque:");

        txtEstoque.setEditable(false);
        txtEstoque.setEnabled(false);
        txtEstoque.setFocusable(false);

        lblQuantidade.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblQuantidade.setText("Quantidade:");

        txtQuantidade.setEnabled(false);
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

        lblFormadePagamento.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblFormadePagamento.setText("*Forma de Pagamento:");

        cbFormaDePagamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dinhero", "Pix", "Cartão de Credito", "Cartão de Debito" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rbOrdemDeServico)
                        .addGap(21, 21, 21)
                        .addComponent(rbProduto)
                        .addGap(23, 23, 23)
                        .addComponent(rbCliente))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblNome)
                        .addGap(5, 5, 5)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(lblCliente)
                        .addGap(6, 6, 6)
                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(lblID)
                        .addGap(22, 22, 22)
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblCamposObrigatorios))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPesquisa)
                        .addGap(12, 12, 12)
                        .addComponent(txtPesquisa))
                    .addComponent(scPdv)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblOBS)
                                .addGap(6, 6, 6)
                                .addComponent(scText, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUsuarioPDV))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFormadePagamento)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbFormaDePagamento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnCliente))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPreco)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(lblEstoque)
                        .addGap(8, 8, 8)
                        .addComponent(txtEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblQuantidade)
                        .addGap(5, 5, 5)
                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblID)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCamposObrigatorios)))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPesquisa)
                    .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbOrdemDeServico)
                    .addComponent(rbProduto)
                    .addComponent(rbCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scPdv, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblNome)
                                    .addComponent(lblCliente)
                                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblPreco))
                            .addComponent(lblEstoque)
                            .addComponent(txtEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblQuantidade)
                            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lblOBS))
                            .addComponent(scText, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFormadePagamento)
                            .addComponent(cbFormaDePagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblUsuarioPDV)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbProdutoActionPerformed
        // TODO add your handling code here:
        instanciarTabelaProduto();

    }//GEN-LAST:event_rbProdutoActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
        criarNota();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        // TODO add your handling code here:
        remover();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnConcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConcluirActionPerformed
        // TODO add your handling code here:
        concluir();
        imprimir_nota();
        limpar_nota();

    }//GEN-LAST:event_btnConcluirActionPerformed

    private void rbOrdemDeServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbOrdemDeServicoActionPerformed
        // TODO add your handling code here:
        instanciarTabelaOS();

    }//GEN-LAST:event_rbOrdemDeServicoActionPerformed

    private void tbListaDeInformaçoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbListaDeInformaçoesMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbListaDeInformaçoesMouseClicked

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        iniciar();
    }//GEN-LAST:event_formWindowActivated

    private void tbItensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbItensMouseClicked
        // TODO add your handling code here:
        setarCamposRemover();
    }//GEN-LAST:event_tbItensMouseClicked

    private void txtPesquisaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txtPesquisaKeyReleased

    private void rbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbClienteActionPerformed
        // TODO add your handling code here:
        instanciarTabelaCliente();

    }//GEN-LAST:event_rbClienteActionPerformed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        // TODO add your handling code here:
        CadastroCliente();
    }//GEN-LAST:event_btnClienteActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnConcluir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConcluir1ActionPerformed
        // TODO add your handling code here:
        AdicionarDesconto();
    }//GEN-LAST:event_btnConcluir1ActionPerformed

    private void scPdvMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scPdvMouseClicked

    }//GEN-LAST:event_scPdvMouseClicked

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        // TODO add your handling code here:
        calculoQuantidade();
    }//GEN-LAST:event_txtQuantidadeKeyReleased

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
            java.util.logging.Logger.getLogger(PontoDeVendas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PontoDeVendas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PontoDeVendas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PontoDeVendas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PontoDeVendas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup OS_Produtos;
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnConcluir;
    private javax.swing.JButton btnConcluir1;
    private javax.swing.JButton btnRemove;
    private javax.swing.JComboBox<String> cbFormaDePagamento;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCamposObrigatorios;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblEstoque;
    private javax.swing.JLabel lblFormadePagamento;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblOBS;
    private javax.swing.JLabel lblPesquisa;
    private javax.swing.JLabel lblPreco;
    private javax.swing.JLabel lblPrecoFinal;
    private javax.swing.JLabel lblQuantidade;
    private javax.swing.JLabel lblTotal;
    public static javax.swing.JLabel lblUsuarioPDV;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JRadioButton rbCliente;
    private javax.swing.JRadioButton rbOrdemDeServico;
    private javax.swing.JRadioButton rbProduto;
    private javax.swing.JScrollPane scCliente;
    private javax.swing.JScrollPane scItens;
    private javax.swing.JScrollPane scPdv;
    private javax.swing.JScrollPane scRelatorio;
    private javax.swing.JScrollPane scRelatoriotb;
    private javax.swing.JScrollPane scSetar;
    private javax.swing.JScrollPane scText;
    private javax.swing.JScrollPane scTotal;
    private javax.swing.JTextArea taOBS;
    private javax.swing.JTextArea taRelatorio;
    private javax.swing.JTable tbCliente;
    private javax.swing.JTable tbItens;
    private javax.swing.JTable tbListaDeInformaçoes;
    private javax.swing.JTable tbRelatorio;
    private javax.swing.JTable tbSetar;
    private javax.swing.JTable tbTotal;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtEstoque;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPesquisa;
    private javax.swing.JTextField txtPreco;
    private javax.swing.JTextField txtQuantidade;
    // End of variables declaration//GEN-END:variables
}
