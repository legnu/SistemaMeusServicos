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
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    String funcionario;
    String identificador;
    String permissao = "1";

    public PontoDeVendas() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    public void iniciar() {
        InstanciarComboboxComanda();
        instanciarTabelaVenda();
        instanciarTabelaCliente();
        txtPreco.setText("0.00");
        tipo = "OS";
        rbOrdemDeServico.setSelected(true);
        instanciarTabela();
        soma();

    }

    public void CadastroCliente() {
        CadClientes cliente = new CadClientes();
        cliente.setVisible(true);

    }

    public void InstanciarCombobox() {
        if (String.valueOf(cbPagamento.getSelectedItem()).equals("A Vista (Dinhero)") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Debito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Pix") == true) {
            cbNumero.setSelectedItem("1x");
            cbNumero.setEnabled(false);

        } else if (String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Credito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Crediario") == true) {
            cbNumero.setEnabled(true);
        }
    }

    public void InstanciarComboboxComanda() {
        try {
            permissao = "1";
            cbComanda.removeAllItems();
            cbComanda.addItem("Caixa Principal");
            String sql = "select nomeComanda from tbComanda";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbComanda.addItem(rs.getString("nomeComanda"));
            }
            permissao = "0";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void AdicionarDesconto() {

        String sql = "select idvenda from tbvenda where nome='Desconto'";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbDesconto.setModel(DbUtils.resultSetToTableModel(rs));

            String sqo = "delete from tbvenda where idvenda=" + tbDesconto.getModel().getValueAt(0, 0).toString();

            pst = conexao.prepareStatement(sqo);
            pst.executeUpdate();
            limpar();
            telaDesconto desconto = new telaDesconto();
            desconto.setVisible(true);

        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            telaDesconto desconto = new telaDesconto();
            desconto.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void identificador() {
        try {
            String sqy = "select identificador from tbtotalvendas where id ORDER BY identificador desc;";
            pst = conexao.prepareStatement(sqy);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            identificador = String.valueOf(Integer.parseInt(tbAuxilio.getModel().getValueAt(0, 0).toString()) + 1);
            System.out.println(tbAuxilio.getModel().getValueAt(0, 0).toString());

        } catch (Exception e) {
            identificador = "1";

        }
    }

    public void criarNota() {

        try {

            String sql = "insert into tbvenda(nome, preco, quantidade, tipo, comissao, vendedor, comanda_nota)values(?,?,?,?,?,?,?)";
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtNome.getText());

            if (tipo.equals("Produto") == true) {

                pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText()) * Integer.parseInt(txtQuantidade.getText())))).replace(",", "."));
            } else if (tipo.equals("OS") == true) {

                pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
            } else if (tipo.equals("Serviço") == true) {

                pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
            }

            pst.setString(3, txtQuantidade.getText());
            pst.setString(4, tipo);
            if (tipo.equals("Produto") == true) {
                pst.setString(5, lblUsuarioPDV.getText());
            } else {
                pst.setString(5, funcionario);
            }
            pst.setString(6, lblUsuarioPDV.getText());
            pst.setString(7, cbComanda.getSelectedItem().toString());

            //Validação dos Campos Obrigatorios
            if ((txtNome.getText().isEmpty()) || (txtPreco.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios");

            } else if ((txtQuantidade.getText().isEmpty()) && tipo.equals("Produto")) {
                JOptionPane.showMessageDialog(null, "Adicione uma Quantidade.");
                limpar();

            } else if (Integer.parseInt(txtQuantidade.getText()) <= 0 && tipo.equals("Produto")) {
                JOptionPane.showMessageDialog(null, "Adicione uma quantidade maior que 0");
                limpar();

            } else if (Integer.parseInt(txtEstoque.getText()) <= 0 && tipo.equals("Produto") && txtTipo.getText().equals("Com controle de estoque.") == true) {
                JOptionPane.showMessageDialog(null, "Sem estoque de " + txtNome.getText() + ".");
                limpar();

            } else if (Integer.parseInt(txtQuantidade.getText()) > Integer.parseInt(txtEstoque.getText()) && txtTipo.getText().equals("Com controle de estoque.") == true) {
                JOptionPane.showMessageDialog(null, "Estoque insuficiente de " + txtNome.getText() + ".");
                limpar();

            } else if (tipo.equals("Produto")) {
                int adicionado = pst.executeUpdate();
                //A Linha abaixo serve de apoio ao entendimento da logica
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    QuantidadeTirada();
                    instanciarTabelaVenda();
                    JOptionPane.showMessageDialog(null, "Produto adicionado com sucesso.");

                    limpar();

                }

            } else if (tipo.equals("OS")) {
                int adicionado = 0;

                for (int i = 0; i < tbItem.getRowCount(); i++) {
                    String Nome = tbItem.getModel().getValueAt(i, 1).toString();
                    if (Nome.equals(txtNome.getText()) == true) {
                        JOptionPane.showMessageDialog(null, "OS já esta na Nota.");
                        adicionado = 1;
                        limpar();
                    }
                }

                if (adicionado == 0) {
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "OS adicionada com sucesso.");
                    instanciarTabelaVenda();
                    limpar();

                }

            } else if (tipo.equals("Serviço")) {
                int adicionado = 0;

                for (int i = 0; i < tbItem.getRowCount(); i++) {
                    String Nome = tbItem.getModel().getValueAt(i, 1).toString();
                    if (Nome.equals(txtNome.getText()) == true) {
                        JOptionPane.showMessageDialog(null, "Este Serviço já esta na Nota.");
                        adicionado = 1;
                        limpar();
                    }
                }

                if (adicionado == 0) {
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Serviço adicionado com sucesso.");
                    instanciarTabelaVenda();
                    limpar();

                }
            }

        } catch (java.lang.NumberFormatException e) {
            if (txtQuantidade.getText().isEmpty() == true) {
                JOptionPane.showMessageDialog(null, "Quantidade não pode ser nula.");
                limpar();
            } else {
                JOptionPane.showMessageDialog(null, "Quantidade deve ser um numero Inteiro.");
                limpar();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void criarComanda() {
        try {
            String Comanda = JOptionPane.showInputDialog("Nomeie a nova comanda.");
            if (Comanda.isEmpty() == false) {
                JOptionPane.showMessageDialog(null, "Aguarde.");
                String sql = "insert into tbComanda(nomeComanda)values(?)";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, Comanda);
                pst.executeUpdate();
                InstanciarComboboxComanda();
                JOptionPane.showMessageDialog(null, "Comanda Adicionada com sucesso.");
            }

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void pesquisarCliente() {

        String sql = "select nomecli as Cliente from tbclientes where nomecli like ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisarCliente.getText() + "%");
            rs = pst.executeQuery();
            tbCliente.setModel(DbUtils.resultSetToTableModel(rs));
            pnCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente Selecionado:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

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
                        JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");

                        JOptionPane.showMessageDialog(null, "OS/Serviço removida com sucesso");
                        limpar();

                    }
                } else {
                    QuantidadeAdicionada();
                    String sql = "delete from tbvenda where idvenda=?";
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtID.getText());
                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");

                        JOptionPane.showMessageDialog(null, "Produto removido com sucesso");
                        limpar();

                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();
            }
        }
    }

    public void QuantidadeTirada() {
        try {
            if (txtTipo.getText().equals("Sem controle de estoque.")) {

            } else {
                int quantidadeEstoque = Integer.parseInt(txtEstoque.getText());
                int quantidadeVendida = Integer.parseInt(txtQuantidade.getText());
                int quantidade = quantidadeEstoque - quantidadeVendida;

                int auxilioQuantidade;

                double valor_compra;
                double valor_venda;

                String sqy = "select valor_compra from tbprodutos where produto=?";

                pst = conexao.prepareStatement(sqy);
                pst.setString(1, txtNome.getText());
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                valor_compra = Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                String sqk = "select valor_venda from tbprodutos where produto=?";

                pst = conexao.prepareStatement(sqk);
                pst.setString(1, txtNome.getText());
                rs = pst.executeQuery();
                tbAuxilio2.setModel(DbUtils.resultSetToTableModel(rs));
                valor_venda = Double.parseDouble(tbAuxilio2.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

                String sql = "update tbprodutos set quantidade=? where produto=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, String.valueOf(quantidade));
                pst.setString(2, txtNome.getText());
                pst.executeUpdate();

                String squ = "select quantidade from tbprodutos where produto=?";

                pst = conexao.prepareStatement(squ);
                pst.setString(1, txtNome.getText());
                rs = pst.executeQuery();
                tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
                auxilioQuantidade = Integer.parseInt(tbAuxilio1.getModel().getValueAt(0, 0).toString());

                String sqo = "update tbprodutos set referencial_compra=?, referencial_venda=? where produto=?";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(auxilioQuantidade * valor_compra))).replace(",", "."));
                pst.setString(2, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(auxilioQuantidade * valor_venda))).replace(",", "."));
                pst.setString(3, txtNome.getText());
                pst.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void QuantidadeAdicionada() {
        try {

            int quantidadeEstoque = Integer.parseInt(txtEstoque.getText());
            int quantidadeVendida = Integer.parseInt(txtQuantidade.getText());
            int quantidade = quantidadeEstoque + quantidadeVendida;
            double auxilioQuantidade;

            double valor_compra;
            double valor_venda;

            String sqy = "select valor_compra from tbprodutos where produto=?";

            pst = conexao.prepareStatement(sqy);
            pst.setString(1, txtNome.getText());
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            valor_compra = Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

            String sqk = "select valor_venda from tbprodutos where produto=?";

            pst = conexao.prepareStatement(sqk);
            pst.setString(1, txtNome.getText());
            rs = pst.executeQuery();
            tbAuxilio2.setModel(DbUtils.resultSetToTableModel(rs));
            valor_venda = Double.parseDouble(tbAuxilio2.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

            String sql = "update tbprodutos set quantidade=? where produto=?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, String.valueOf(quantidade));
            pst.setString(2, txtNome.getText());
            pst.executeUpdate();

            String squ = "select quantidade from tbprodutos where produto=?";

            pst = conexao.prepareStatement(squ);
            pst.setString(1, txtNome.getText());
            rs = pst.executeQuery();
            tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
            auxilioQuantidade = Integer.parseInt(tbAuxilio1.getModel().getValueAt(0, 0).toString());

            String sqo = "update tbprodutos set referencial_compra=?, referencial_venda=? where produto=?";
            pst = conexao.prepareStatement(sqo);
            pst.setString(1, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(auxilioQuantidade * valor_compra))).replace(",", "."));
            pst.setString(2, new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(auxilioQuantidade * valor_venda))).replace(",", "."));
            pst.setString(3, txtNome.getText());
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void imprimir_nota() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao desta Nota?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {            
            try {

                String sql = "update tbrelatorio set cliente=?,preco=? where idRelatorio=1";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliente.getText());
                pst.setString(2, lblValorTotal.getText());
                pst.executeUpdate();
                
                
                HashMap filtro = new HashMap();
                
                
                filtro.put("IT", Integer.parseInt(identificador));
                filtro.put("cliente", txtCliente.getText());
                
                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/NotaPdv.jasper"), filtro, conexao);
                JasperViewer.viewReport(print, false);               
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }
        }
    }

    public void soma() {

        try {
            double preco, x;

            preco = 0;

            for (int i = 0; i < tbItem.getRowCount(); i++) {
                x = Double.parseDouble(tbItem.getModel().getValueAt(i, 2).toString().replace(".", "")) / 100;
                preco = preco + x;

            }

            lblValorTotal.setText(new DecimalFormat("#,##0.00").format(preco).replace(",", "."));
        } catch (java.lang.NullPointerException e) {
            lblValorTotal.setText("0.00");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void instanciarTabelaAuxilioProduto() {

        try {
            String sqo = "select idproduto, produto, valor_venda, obs, quantidade, estoque from tbprodutos";
            limpar();
            pst = conexao.prepareStatement(sqo);
            rs = pst.executeQuery();
            tbSetar.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void instanciarTabelaVenda() {
        String sql = "select idvenda as ID, nome as Nome, preco as Preço, quantidade as Quantidade,tipo as tipo from tbvenda where comanda_nota=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, cbComanda.getSelectedItem().toString());
            rs = pst.executeQuery();
            tbItem.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void instanciarTabelaCliente() {
        String sql = "select idcli as ID, nomecli as Cliente from tbclientes";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbCliente.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void instanciarTabela() {

        try {
            limpar();
            if (tipo.equals("Produto") == true) {
                instanciarTabelaAuxilioProduto();
                String sql = "select idproduto as ID, produto as Produto, valor_venda as Preço, obs as Observações from tbprodutos where quantidade > 0";
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                txtQuantidade.setEnabled(true);
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Produtos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } else if (tipo.equals("OS") == true) {
                String sql = "select servico as Serviço, Valor as Valor, equipamento as Equipamento, previsao_entreg_os as Entrega, defeito as Defeito, os as OS, funcionario as Funcionario, data_os as Data, cliente as Cliente from tbos where tipo='Ordem de Serviço'";

                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                txtQuantidade.setEnabled(false);
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } else if (tipo.equals("Serviço") == true) {
                String sql = "select servico as Servico, valor as Valor, obs as OBS, funcionario as Funcionario from tbservicos where tipo='Concluido'";

                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                txtQuantidade.setEnabled(false);
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Serviço", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    private void pesquisar() {

        if (tipo.equals("Produto") == true) {

            String sql = "select idproduto as ID, produto as Produto, valor_venda as Preço, obs as Observações from tbprodutos where produto like ? and quantidade > 0";

            try {
                rbProduto.setSelected(true);
                tipo = "Produto";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Produtos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        } else if (tipo.equals("OS") == true) {

            String sql = "select servico as Serviço, Valor as Valor, equipamento as Equipamento, previsao_entreg_os as Entrega, defeito as Defeito, os as OS, funcionario as Funcionario, data_os as Data, cliente as Cliente from tbos where tipo='Ordem de Serviço' and servico like ?";

            try {
                rbOrdemDeServico.setSelected(true);
                tipo = "OS";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        } else if (tipo.equals("Serviço") == true) {

            String sql = "select servico as Servico, valor as Valor, obs as OBS, funcionario as Funcionario from tbservicos where tipo='Concluido' and funcionario like ?";

            try {
                rbServico.setSelected(true);
                tipo = "Serviço";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Serviço", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        }

    }

    public void habilitarAdicionar() {
        btnAdicionar.setEnabled(true);
        btnRemove.setEnabled(false);

    }

    public void inserirDadosCliente() {
        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dSql = new java.sql.Date(d.getTime());
        df.format(dSql);
        try {
            if (txtCliente.getText().isEmpty() == false) {
                String squ = "select quantidade_comprada from tbclientes where nomecli=?";

                pst = conexao.prepareStatement(squ);
                pst.setString(1, txtCliente.getText());
                rs = pst.executeQuery();
                tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
                int numero_compra = Integer.parseInt(tbAuxilio1.getModel().getValueAt(0, 0).toString()) + 1;

                String sqt = "update tbclientes set quantidade_comprada=? where nomecli=?";
                pst = conexao.prepareStatement(sqt);
                pst.setInt(1, numero_compra);
                pst.setString(2, txtCliente.getText());
                pst.executeUpdate();

                String sqh = "update tbclientes set ultima_compra=? where nomecli=?";
                pst = conexao.prepareStatement(sqh);
                pst.setDate(1, dSql);
                pst.setString(2, txtCliente.getText());
                pst.executeUpdate();

                String sqb = "select valor_gasto from tbclientes where nomecli=?";

                pst = conexao.prepareStatement(sqb);
                pst.setString(1, txtCliente.getText());
                rs = pst.executeQuery();
                tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));
                double Valor_final = Double.parseDouble(String.valueOf(Double.parseDouble(tbAuxilio1.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100)) + Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100;

                String sqn = "update tbclientes set valor_gasto=? where nomecli=?";
                pst = conexao.prepareStatement(sqn);
                pst.setString(1, String.valueOf(new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Valor_final)))).replace(",", "."));
                pst.setString(2, txtCliente.getText());
                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void removerOS() {
        try {
            String sql = "select nome from tbvenda where tipo='OS'";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbOS.setModel(DbUtils.resultSetToTableModel(rs));

            for (int i = 0; i < tbOS.getRowCount(); i++) {

                String OS = "'" + tbOS.getModel().getValueAt(i, 0).toString() + "'";

                String sqr = "select os from tbos where servico=" + OS;
                pst = conexao.prepareStatement(sqr);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                String id_OS = tbAuxilio.getModel().getValueAt(0, 0).toString();

                String sqy = "delete from tbos where os=" + id_OS;
                pst = conexao.prepareStatement(sqy);
                pst.executeUpdate();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void AlterarServico() {
        try {
            String sql = "select nome from tbvenda where tipo='Serviço'";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbOS.setModel(DbUtils.resultSetToTableModel(rs));

            for (int i = 0; i < tbOS.getRowCount(); i++) {

                String sqo = "update tbservicos set tipo='Pago' where servico=?";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, tbOS.getModel().getValueAt(i, 0).toString());
                pst.executeUpdate();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    public void tirarComanda() {
        try {
            if (cbComanda.getSelectedItem().toString().equals("Caixa Principal") == false) {
                for (int i = 0; i < tbItem.getRowCount(); i++) {
                String id = tbItem.getModel().getValueAt(i, 0).toString();
                String sqo = "update tbvenda set comanda_nota='Caixa Principal' where idvenda=?";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, id);
                pst.executeUpdate();
                }
                String sqy = "delete from tbComanda where nomeComanda=?";
                pst = conexao.prepareStatement(sqy);
                pst.setString(1, cbComanda.getSelectedItem().toString());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Comanda removida com sucesso.");
                InstanciarComboboxComanda();
                instanciarTabelaVenda();
            } else {
                JOptionPane.showMessageDialog(null, "Caixa Principal não pode ser removido.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    public void adicionarComissao() {
        try {
            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);
            String sqo = "select preco, tipo, comissao from tbvenda where comanda_nota=?";
            pst = conexao.prepareStatement(sqo);
            pst.setString(1, cbComanda.getSelectedItem().toString());
            rs = pst.executeQuery();
            tbComissao.setModel(DbUtils.resultSetToTableModel(rs));

            for (int i = 0; i < tbComissao.getRowCount(); i++) {
                String tipoVenda = tbComissao.getModel().getValueAt(i, 1).toString();

                if (tipoVenda.equals("Produto") == true) {
                    String sqy = "select comissao from tbusuarios where usuario=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, lblUsuarioPDV.getText());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                    String comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);
                    String valorComissao = new DecimalFormat("#,##0.00").format(Double.parseDouble(tbComissao.getModel().getValueAt(i, 0).toString().replace(".", "")) / 100 * Double.parseDouble(comissao)).replace(",", ".");
                    System.out.println(valorComissao);

                    if (Integer.parseInt(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) > 0) {

                        String sql = "insert into tbgastos(nome, data_pagamento, status_pagamento, valor, tipo)values(?,?,?,?,?)";
                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, "Comiçâo de " + lblUsuarioPDV.getText());
                        pst.setDate(2, dSql);
                        pst.setString(3, "Pago");
                        pst.setString(4, valorComissao);
                        pst.setString(5, "Comissao");
                        pst.executeUpdate();

                    }

                } else {
                    String sqy = "select comissao from tbFuncionarios where funcionario=?";
                    pst = conexao.prepareStatement(sqy);
                    pst.setString(1, tbComissao.getModel().getValueAt(i, 2).toString());
                    rs = pst.executeQuery();
                    tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                    String comissao = String.valueOf(Double.parseDouble(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) / 100);
                    String valorComissao = new DecimalFormat("#,##0.00").format(Double.parseDouble(tbComissao.getModel().getValueAt(i, 0).toString().replace(".", "")) / 100 * Double.parseDouble(comissao)).replace(",", ".");
                    System.out.println(valorComissao);

                    if (Integer.parseInt(tbAuxilio.getModel().getValueAt(0, 0).toString().replace("%", "")) > 0) {

                        String sql = "insert into tbgastos(nome, data_pagamento, status_pagamento, valor, tipo)values(?,?,?,?,?)";
                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, "Comiçâo de " + tbComissao.getModel().getValueAt(i, 2).toString());
                        pst.setDate(2, dSql);
                        pst.setString(3, "Pago");
                        pst.setString(4, valorComissao);
                        pst.setString(5, "Comissao");
                        pst.executeUpdate();

                    }

                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void finalizarVenda() {
        try {
            for (int i = 0; i < tbItem.getRowCount(); i++) {
                String id = tbItem.getModel().getValueAt(i, 0).toString();
                String sqo = "update tbvenda set comanda_nota='',identificador=? where idvenda=?";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, identificador);
                pst.setString(2, id);
                pst.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void concluir() {
        identificador();
        Date dataHoraAtual = new Date();
        String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);

        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dSql = new java.sql.Date(d.getTime());
        df.format(dSql);
        int acumulo = 1;

        try {

            if (Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100 <= 0) {
                JOptionPane.showMessageDialog(null, "Valor total deve ser maior que 0");
                limpar();
            } else if (String.valueOf(cbPagamento.getSelectedItem()).equals("A Vista (Dinhero)") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Debito") == true || String.valueOf(cbPagamento.getSelectedItem()).equals("Pix") == true) {

                String sqo = "insert into tbtotalvendas(dia, hora, venda, idcliente, forma_pagamento, status_pagamento, dia_Pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?,?)";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, dSql.toString());
                pst.setString(2, hora);
                pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100))).replace(",", "."));
                pst.setString(4, idCliente.getText());
                pst.setString(5, cbPagamento.getSelectedItem().toString());
                pst.setString(6, "Pago");
                pst.setDate(7, dSql);
                pst.setString(8, "Venda");
                pst.setString(9, identificador);
                pst.executeUpdate();
                adicionarComissao();
                finalizarVenda();
                imprimir_nota();
                inserirDadosCliente();

                removerOS();
                AlterarServico();

                JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                lblValorTotal.setText("0.00");
                JOptionPane.showMessageDialog(null, "Concluido.");
                limpar();

            } else if (txtCliente.getText().isEmpty() == false & String.valueOf(cbPagamento.getSelectedItem()).equals("Cartão Credito") == true) {
                System.out.println(txtCrediario.getText());
                for (int i = 0; i < Integer.parseInt(String.valueOf(cbNumero.getSelectedItem()).replace("x", "")); i++) {

                    String mes = new SimpleDateFormat("MM").format(d);
                    String ano = new SimpleDateFormat("yyyy").format(d);

                    int limite = Integer.parseInt(mes) + acumulo;

                    if (limite > 12) {
                        limite = limite - 12;
                        ano = String.valueOf(Integer.parseInt(ano) + 1);
                    }

                    String dataLimite = ano + "-" + limite + "-05";

                    Date data = df.parse(dataLimite);
                    java.sql.Date dSqt = new java.sql.Date(data.getTime());
                    df.format(dSqt);

                    String sqo = "insert into tbtotalvendas(dia, hora, venda, idcliente, forma_pagamento, status_pagamento, dia_Pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?,?)";

                    pst = conexao.prepareStatement(sqo);

                    pst.setString(1, dSql.toString());
                    pst.setString(2, hora);
                    pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100)) / Integer.parseInt(String.valueOf(cbNumero.getSelectedItem()).replace("x", ""))).replace(",", "."));
                    pst.setString(4, idCliente.getText());
                    pst.setString(5, cbPagamento.getSelectedItem().toString());
                    pst.setString(6, "Pendente");
                    pst.setDate(7, dSqt);
                    pst.setString(8, "Venda");
                    pst.setString(9, identificador);
                    pst.executeUpdate();
                    acumulo++;
                }
                adicionarComissao();
                finalizarVenda();
                imprimir_nota();
                inserirDadosCliente();

                removerOS();
                AlterarServico();

                JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                lblValorTotal.setText("0.00");
                JOptionPane.showMessageDialog(null, "Concluido.");
                limpar();

            } else if (txtCliente.getText().isEmpty() == false & txtCrediario.getText().equals("Habilitado") == true & String.valueOf(cbPagamento.getSelectedItem()).equals("Crediario") == true) {
                System.out.println(txtCrediario.getText());
                for (int i = 0; i < Integer.parseInt(String.valueOf(cbNumero.getSelectedItem()).replace("x", "")); i++) {

                    String mes = new SimpleDateFormat("MM").format(d);
                    String ano = new SimpleDateFormat("yyyy").format(d);

                    int limite = Integer.parseInt(mes) + acumulo;

                    if (limite > 12) {
                        limite = limite - 12;
                        ano = String.valueOf(Integer.parseInt(ano) + 1);
                    }

                    String dataLimite = ano + "-" + limite + "-05";

                    Date data = df.parse(dataLimite);
                    java.sql.Date dSqt = new java.sql.Date(data.getTime());
                    df.format(dSqt);

                    String sqo = "insert into tbtotalvendas(dia, hora, venda, idcliente, forma_pagamento, status_pagamento, dia_Pagamento,tipo, identificador)values(?,?,?,?,?,?,?,?,?)";

                    pst = conexao.prepareStatement(sqo);

                    pst.setString(1, dSql.toString());
                    pst.setString(2, hora);
                    pst.setString(3, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100)) / Integer.parseInt(String.valueOf(cbNumero.getSelectedItem()).replace("x", ""))).replace(",", "."));
                    pst.setString(4, idCliente.getText());
                    pst.setString(5, cbPagamento.getSelectedItem().toString());
                    pst.setString(6, "Pendente");
                    pst.setDate(7, dSqt);
                    pst.setString(8, "Venda");
                    pst.setString(9, identificador);
                    pst.executeUpdate();
                    acumulo++;
                }
                adicionarComissao();
                finalizarVenda();
                imprimir_nota();
                inserirDadosCliente();

                removerOS();
                AlterarServico();

                JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                lblValorTotal.setText("0.00");
                JOptionPane.showMessageDialog(null, "Concluido.");
                limpar();

            } else if (txtCrediario.getText().equals("Habilitado") == false & String.valueOf(cbPagamento.getSelectedItem()).equals("Crediario") == true) {
                JOptionPane.showMessageDialog(null, "Cliente não tem permissão para Comprar no Crediario.");
                limpar();

            }

        } catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    private void setar_campos() {
        habilitarAdicionar();
        int setar = tbListaDeInformaçoes.getSelectedRow();

        try {

            if (tipo.equals("OS") == true) {
                txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                txtPreco.setText(String.valueOf(Double.parseDouble(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString().replace(".", "")) / 100));
                funcionario = tbListaDeInformaçoes.getModel().getValueAt(setar, 6).toString();
                taOBS.setText("Equipamento: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString() + "\n" + "Defeito: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 4).toString() + "\n" + "Previsão de entrega: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 3).toString());

                txtQuantidade.setEnabled(false);
                txtQuantidade.setText("0");
                txtEstoque.setText("0");
                btnAdicionar.setEnabled(true);

            } else if (tipo.equals("Produto") == true) {
                txtID.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString());
                txtPreco.setText(String.valueOf(Double.parseDouble(tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString().replace(".", "")) / 100));
                taOBS.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 3).toString());

                String sql = "select estoque FROM tbprodutos where idproduto=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                rs = pst.executeQuery();
                tbSetar.setModel(DbUtils.resultSetToTableModel(rs));
                txtTipo.setText(tbSetar.getModel().getValueAt(0, 0).toString());

                if (txtQuantidade.getText().equals("0") == false) {
                    String sqo = "select quantidade FROM tbprodutos where idproduto=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                    rs = pst.executeQuery();
                    tbSetar.setModel(DbUtils.resultSetToTableModel(rs));
                    txtEstoque.setText(tbSetar.getModel().getValueAt(0, 0).toString());
                }

            } else if (tipo.equals("Serviço") == true) {
                txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
                txtPreco.setText(String.valueOf(Double.parseDouble(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString().replace(".", "")) / 100));
                funcionario = tbListaDeInformaçoes.getModel().getValueAt(setar, 3).toString();
                taOBS.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString());

                txtQuantidade.setEnabled(false);
                txtQuantidade.setText("0");
                txtEstoque.setText("0");
                btnAdicionar.setEnabled(true);

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void setarCamposRemover() {
        int setar = tbItem.getSelectedRow();
        limpar();
        try {
            txtID.setText(tbItem.getModel().getValueAt(setar, 0).toString());
            txtNome.setText(tbItem.getModel().getValueAt(setar, 1).toString());
            txtPreco.setText(String.valueOf(Double.parseDouble(tbItem.getModel().getValueAt(setar, 2).toString().replace(".", "")) / 100));
            txtQuantidade.setText(tbItem.getModel().getValueAt(setar, 3).toString());

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
            limpar();

        }
    }

    public void setarCamposCliente() {
        int setar = tbCliente.getSelectedRow();
        limpar();
        try {
            idCliente.setText(tbCliente.getModel().getValueAt(setar, 0).toString());
            txtCliente.setText(tbCliente.getModel().getValueAt(setar, 1).toString());
            String sqy = "select crediario from tbclientes where idcli=?";
            pst = conexao.prepareStatement(sqy);
            pst.setString(1, tbCliente.getModel().getValueAt(setar, 0).toString());
            rs = pst.executeQuery();
            tbAuxilio2.setModel(DbUtils.resultSetToTableModel(rs));
            txtCrediario.setText(tbAuxilio2.getModel().getValueAt(0, 0).toString());

            pnCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente Selecionado: " + txtCliente.getText(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void fecharPdv() {

        String Perfil;
        limpar();
        try {
            String sqy = "select perfil from tbusuarios where usuario=?";
            pst = conexao.prepareStatement(sqy);
            pst.setString(1, lblUsuarioPDV.getText());
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
            Perfil = tbAuxilio.getModel().getValueAt(0, 0).toString();
            if (Perfil.equals("Administrador")) {
                TelaPrincipal principal = new TelaPrincipal();
                principal.setVisible(true);
                TelaPrincipal.MenCadUsu.setEnabled(true);
                TelaPrincipal.lblUsuario.setText(lblUsuarioPDV.getText());
                TelaPrincipal.lblUsuario.setForeground(Color.red);
                this.dispose();
                conexao.close();
            } else if (Perfil.equals("Usuario") == true) {

                TelaLimitada limitada = new TelaLimitada();
                limitada.setVisible(true);
                TelaLimitada.btnPDV.setEnabled(false);
                TelaLimitada.btnCadOS.setEnabled(false);
                TelaLimitada.btnCadServiço.setEnabled(false);
                TelaLimitada.lblNome.setText(lblUsuarioPDV.getText());
                this.dispose();
                conexao.close();

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void limpar() {
        txtNome.setText(null);
        txtPreco.setText(null);
        txtID.setText(null);
        idCliente.setText("1");
        taOBS.setText(null);
        txtEstoque.setText(null);
        txtTipo.setText(null);
        txtQuantidade.setText(null);
        btnAdicionar.setEnabled(false);
        btnRemove.setEnabled(false);
        txtQuantidade.setEnabled(false);
        lblValorTotal.setText("0.00");
        ((DefaultTableModel) tbListaDeInformaçoes.getModel()).setRowCount(0);
        txtCliente.setText("Vendas Sem Registro.");
        txtCrediario.setText(null);
        cbPagamento.setSelectedItem("A Vista (Dinhero)");
        cbNumero.setSelectedItem("1x");
        funcionario = null;
        pnCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente Selecionado: ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

        instanciarTabelaVenda();
        instanciarTabelaCliente();
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

        lblPrecoFinal = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        txtEstoque = new javax.swing.JTextField();
        OS_Produtos = new javax.swing.ButtonGroup();
        scTotal = new javax.swing.JScrollPane();
        tbTotal = new javax.swing.JTable();
        scSetar = new javax.swing.JScrollPane();
        tbSetar = new javax.swing.JTable();
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        scAuxilio1 = new javax.swing.JScrollPane();
        tbAuxilio1 = new javax.swing.JTable();
        scAuxilio2 = new javax.swing.JScrollPane();
        tbAuxilio2 = new javax.swing.JTable();
        scDesconto = new javax.swing.JScrollPane();
        tbDesconto = new javax.swing.JTable();
        scOS = new javax.swing.JScrollPane();
        tbOS = new javax.swing.JTable();
        txtCliente = new javax.swing.JTextField();
        txtCrediario = new javax.swing.JTextField();
        idCliente = new javax.swing.JTextField();
        txtTipo = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        scComissao = new javax.swing.JScrollPane();
        tbComissao = new javax.swing.JTable();
        lblUsuarioPDV = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        pnNota = new javax.swing.JPanel();
        pnTbNota = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbItem = new javax.swing.JTable();
        cbComanda = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        btnComanda = new javax.swing.JToggleButton();
        btnTirarComanda = new javax.swing.JToggleButton();
        lblTotal = new javax.swing.JLabel();
        lblValorTotal = new javax.swing.JLabel();
        btnConcluir = new javax.swing.JButton();
        btnConcluir1 = new javax.swing.JButton();
        pnTbClientes = new javax.swing.JPanel();
        lblPesquisarCliente = new javax.swing.JLabel();
        txtPesquisarCliente = new javax.swing.JTextField();
        pnCliente = new javax.swing.JPanel();
        scCliente = new javax.swing.JScrollPane();
        tbCliente = new javax.swing.JTable();
        pnPagamento = new javax.swing.JPanel();
        cbPagamento = new javax.swing.JComboBox<>();
        cbNumero = new javax.swing.JComboBox<>();
        pnTbPrincipal = new javax.swing.JPanel();
        scPdv = new javax.swing.JScrollPane();
        tbListaDeInformaçoes = new javax.swing.JTable();
        lblPesquisa = new javax.swing.JLabel();
        txtPesquisa = new javax.swing.JTextField();
        rbOrdemDeServico = new javax.swing.JRadioButton();
        rbProduto = new javax.swing.JRadioButton();
        rbServico = new javax.swing.JRadioButton();
        btnOS = new javax.swing.JToggleButton();
        btnCliente = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        txtQuantidade = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        txtNome = new javax.swing.JTextField();
        pnOBS = new javax.swing.JPanel();
        scText = new javax.swing.JScrollPane();
        taOBS = new javax.swing.JTextArea();
        btnAdicionar = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtPreco = new javax.swing.JTextField();

        lblPrecoFinal.setText("jLabel1");

        txtID.setEnabled(false);

        txtEstoque.setText("jTextField1");

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

        tbAuxilio1.setModel(new javax.swing.table.DefaultTableModel(
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
        scAuxilio1.setViewportView(tbAuxilio1);

        tbAuxilio2.setModel(new javax.swing.table.DefaultTableModel(
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
        scAuxilio2.setViewportView(tbAuxilio2);

        tbDesconto.setModel(new javax.swing.table.DefaultTableModel(
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
        scDesconto.setViewportView(tbDesconto);

        tbOS.setModel(new javax.swing.table.DefaultTableModel(
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
        scOS.setViewportView(tbOS);

        txtCliente.setText("Vendas Sem Registro.");

        idCliente.setText("1");

        tbComissao.setModel(new javax.swing.table.DefaultTableModel(
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
        scComissao.setViewportView(tbComissao);

        lblUsuarioPDV.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblUsuarioPDV.setText("Usuario");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ponto de Vendas");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        pnNota.setBackground(new java.awt.Color(204, 204, 204));
        pnNota.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        pnTbNota.setBackground(new java.awt.Color(204, 204, 204));
        pnTbNota.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nota", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbItem = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbItem.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbItem.setFocusable(false);
        tbItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbItemMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbItem);

        cbComanda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Caixa Principal" }));
        cbComanda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbComandaItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setText("Comanda: ");

        btnComanda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/plus.png"))); // NOI18N
        btnComanda.setBorderPainted(false);
        btnComanda.setContentAreaFilled(false);
        btnComanda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComandaActionPerformed(evt);
            }
        });

        btnTirarComanda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/minus.png"))); // NOI18N
        btnTirarComanda.setBorderPainted(false);
        btnTirarComanda.setContentAreaFilled(false);
        btnTirarComanda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTirarComandaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnTbNotaLayout = new javax.swing.GroupLayout(pnTbNota);
        pnTbNota.setLayout(pnTbNotaLayout);
        pnTbNotaLayout.setHorizontalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
            .addGroup(pnTbNotaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbComanda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTirarComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnTbNotaLayout.setVerticalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTbNotaLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnTirarComanda, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        lblTotal.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lblTotal.setText("Total:");

        lblValorTotal.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lblValorTotal.setText("0.00");

        btnConcluir.setBackground(new java.awt.Color(0, 0, 153));
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

        btnConcluir1.setBackground(new java.awt.Color(102, 0, 102));
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

        pnTbClientes.setBackground(new java.awt.Color(204, 204, 204));
        pnTbClientes.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Clientes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        lblPesquisarCliente.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisarCliente.setText("Pesquisar:");

        txtPesquisarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarClienteActionPerformed(evt);
            }
        });
        txtPesquisarCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarClienteKeyReleased(evt);
            }
        });

        pnCliente.setBackground(new java.awt.Color(204, 204, 204));
        pnCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente Selecinado:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbCliente = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbCliente.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbCliente.setFocusable(false);
        tbCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbClienteMouseClicked(evt);
            }
        });
        scCliente.setViewportView(tbCliente);

        javax.swing.GroupLayout pnClienteLayout = new javax.swing.GroupLayout(pnCliente);
        pnCliente.setLayout(pnClienteLayout);
        pnClienteLayout.setHorizontalGroup(
            pnClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        pnClienteLayout.setVerticalGroup(
            pnClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnTbClientesLayout = new javax.swing.GroupLayout(pnTbClientes);
        pnTbClientes.setLayout(pnTbClientesLayout);
        pnTbClientesLayout.setHorizontalGroup(
            pnTbClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTbClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnTbClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTbClientesLayout.createSequentialGroup()
                        .addComponent(lblPesquisarCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPesquisarCliente))
                    .addComponent(pnCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnTbClientesLayout.setVerticalGroup(
            pnTbClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTbClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnTbClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPesquisarCliente)
                    .addComponent(txtPesquisarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        pnPagamento.setBackground(new java.awt.Color(204, 204, 204));
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
                .addComponent(cbPagamento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(pnTbNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnTbClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblTotal)
                .addGap(10, 10, 10)
                .addComponent(lblValorTotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnConcluir1)
                .addGap(16, 16, 16))
            .addGroup(pnNotaLayout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(btnConcluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(80, 80, 80))
        );
        pnNotaLayout.setVerticalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnTbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnTbNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnConcluir1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTotal)
                        .addComponent(lblValorTotal)))
                .addGap(16, 16, 16)
                .addComponent(btnConcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        pnTbPrincipal.setBackground(new java.awt.Color(204, 204, 204));
        pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

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
        tbListaDeInformaçoes.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
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

        lblPesquisa.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisa.setText("Pesquisa:");

        txtPesquisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaKeyReleased(evt);
            }
        });

        rbOrdemDeServico.setBackground(new java.awt.Color(204, 204, 204));
        OS_Produtos.add(rbOrdemDeServico);
        rbOrdemDeServico.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbOrdemDeServico.setText("OS");
        rbOrdemDeServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbOrdemDeServicoActionPerformed(evt);
            }
        });

        rbProduto.setBackground(new java.awt.Color(204, 204, 204));
        OS_Produtos.add(rbProduto);
        rbProduto.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbProduto.setText("Produtos");
        rbProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbProdutoActionPerformed(evt);
            }
        });

        rbServico.setBackground(new java.awt.Color(204, 204, 204));
        OS_Produtos.add(rbServico);
        rbServico.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbServico.setText("Serviço");
        rbServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbServicoActionPerformed(evt);
            }
        });

        btnOS.setBackground(new java.awt.Color(0, 0, 0));
        btnOS.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnOS.setForeground(new java.awt.Color(255, 255, 255));
        btnOS.setText("Cadastrar OS");
        btnOS.setBorderPainted(false);
        btnOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSActionPerformed(evt);
            }
        });

        btnCliente.setBackground(new java.awt.Color(0, 0, 0));
        btnCliente.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnCliente.setText("Cadastrar Cliente");
        btnCliente.setBorderPainted(false);
        btnCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnTbPrincipalLayout = new javax.swing.GroupLayout(pnTbPrincipal);
        pnTbPrincipal.setLayout(pnTbPrincipalLayout);
        pnTbPrincipalLayout.setHorizontalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scPdv, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnTbPrincipalLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTbPrincipalLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(rbOrdemDeServico)
                        .addGap(18, 18, 18)
                        .addComponent(rbProduto)
                        .addGap(18, 18, 18)
                        .addComponent(rbServico)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(btnOS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCliente))
                    .addGroup(pnTbPrincipalLayout.createSequentialGroup()
                        .addComponent(lblPesquisa)
                        .addGap(18, 18, 18)
                        .addComponent(txtPesquisa)))
                .addContainerGap())
        );
        pnTbPrincipalLayout.setVerticalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTbPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPesquisa))
                .addGap(16, 16, 16)
                .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rbOrdemDeServico)
                        .addComponent(rbProduto)
                        .addComponent(rbServico))
                    .addGroup(pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOS)
                        .addComponent(btnCliente)))
                .addGap(16, 16, 16)
                .addComponent(scPdv, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Quantidade", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtQuantidade.setEnabled(false);
        txtQuantidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantidadeActionPerformed(evt);
            }
        });
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtQuantidade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtQuantidade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nome", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtNome.setEditable(false);
        txtNome.setEnabled(false);
        txtNome.setFocusable(false);
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnOBS.setBackground(new java.awt.Color(204, 204, 204));
        pnOBS.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OBS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        taOBS.setEditable(false);
        taOBS.setColumns(20);
        taOBS.setRows(5);
        taOBS.setEnabled(false);
        taOBS.setFocusable(false);
        scText.setViewportView(taOBS);

        javax.swing.GroupLayout pnOBSLayout = new javax.swing.GroupLayout(pnOBS);
        pnOBS.setLayout(pnOBSLayout);
        pnOBSLayout.setHorizontalGroup(
            pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
        );
        pnOBSLayout.setVerticalGroup(
            pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 135, Short.MAX_VALUE)
            .addGroup(pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
        );

        btnAdicionar.setBackground(new java.awt.Color(0, 204, 51));
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

        btnRemove.setBackground(new java.awt.Color(204, 0, 0));
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

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preço(R$)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtPreco.setEditable(false);
        txtPreco.setEnabled(false);
        txtPreco.setFocusable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(txtPreco, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(25, 25, 25)
                        .addComponent(btnRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addComponent(pnOBS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnTbPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(pnNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(pnTbPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(pnOBS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void rbProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbProdutoActionPerformed
        // TODO add your handling code here:
        limpar();
        tipo = "Produto";
        instanciarTabela();

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
    }//GEN-LAST:event_btnConcluirActionPerformed

    private void rbOrdemDeServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbOrdemDeServicoActionPerformed
        // TODO add your handling code here:
        limpar();
        tipo = "OS";
        instanciarTabela();

    }//GEN-LAST:event_rbOrdemDeServicoActionPerformed

    private void tbListaDeInformaçoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbListaDeInformaçoesMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbListaDeInformaçoesMouseClicked

    private void txtPesquisaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txtPesquisaKeyReleased

    private void btnConcluir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConcluir1ActionPerformed
        // TODO add your handling code here:
        AdicionarDesconto();
    }//GEN-LAST:event_btnConcluir1ActionPerformed

    private void scPdvMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scPdvMouseClicked

    }//GEN-LAST:event_scPdvMouseClicked

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_txtQuantidadeKeyReleased

    private void tbClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClienteMouseClicked
        // TODO add your handling code here:
        setarCamposCliente();
    }//GEN-LAST:event_tbClienteMouseClicked

    private void txtPesquisarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisarClienteActionPerformed

    private void txtQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantidadeActionPerformed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        // TODO add your handling code here:
        CadClientes cliente = new CadClientes();
        cliente.setVisible(true);
    }//GEN-LAST:event_btnClienteActionPerformed

    private void txtPesquisarClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarClienteKeyReleased
        // TODO add your handling code here:
        pesquisarCliente();
    }//GEN-LAST:event_txtPesquisarClienteKeyReleased

    private void cbPagamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPagamentoActionPerformed
        // TODO add your handling code here:
        InstanciarCombobox();
    }//GEN-LAST:event_cbPagamentoActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        fecharPdv();

    }//GEN-LAST:event_formWindowClosed

    private void btnOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSActionPerformed
        // TODO add your handling code here:
        CadOS os = new CadOS();
        os.setVisible(true);
    }//GEN-LAST:event_btnOSActionPerformed

    private void tbItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbItemMouseClicked
        // TODO add your handling code here:
        setarCamposRemover();
    }//GEN-LAST:event_tbItemMouseClicked

    private void rbServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbServicoActionPerformed
        // TODO add your handling code here:
        limpar();
        tipo = "Serviço";
        instanciarTabela();
    }//GEN-LAST:event_rbServicoActionPerformed

    private void btnComandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComandaActionPerformed
        // TODO add your handling code here:
        criarComanda();
    }//GEN-LAST:event_btnComandaActionPerformed

    private void cbComandaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbComandaItemStateChanged
        // TODO add your handling code here:
        if (permissao.equals("0") == true) {
            instanciarTabelaVenda();
            soma();
        }
    }//GEN-LAST:event_cbComandaItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        iniciar();
    }//GEN-LAST:event_formWindowOpened

    private void btnTirarComandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTirarComandaActionPerformed
        // TODO add your handling code here:
        tirarComanda();
    }//GEN-LAST:event_btnTirarComandaActionPerformed

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
    private javax.swing.JToggleButton btnCliente;
    private javax.swing.JToggleButton btnComanda;
    private javax.swing.JButton btnConcluir;
    private javax.swing.JButton btnConcluir1;
    private javax.swing.JToggleButton btnOS;
    private javax.swing.JButton btnRemove;
    private javax.swing.JToggleButton btnTirarComanda;
    private javax.swing.JComboBox<String> cbComanda;
    private javax.swing.JComboBox<String> cbNumero;
    private javax.swing.JComboBox<String> cbPagamento;
    private javax.swing.JTextField idCliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblPesquisa;
    private javax.swing.JLabel lblPesquisarCliente;
    private javax.swing.JLabel lblPrecoFinal;
    private javax.swing.JLabel lblTotal;
    public static javax.swing.JLabel lblUsuarioPDV;
    public static javax.swing.JLabel lblValorTotal;
    private javax.swing.JPanel pnCliente;
    private javax.swing.JPanel pnNota;
    private javax.swing.JPanel pnOBS;
    private javax.swing.JPanel pnPagamento;
    private javax.swing.JPanel pnTbClientes;
    private javax.swing.JPanel pnTbNota;
    private javax.swing.JPanel pnTbPrincipal;
    private javax.swing.JRadioButton rbOrdemDeServico;
    private javax.swing.JRadioButton rbProduto;
    private javax.swing.JRadioButton rbServico;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilio1;
    private javax.swing.JScrollPane scAuxilio2;
    private javax.swing.JScrollPane scCliente;
    private javax.swing.JScrollPane scComissao;
    private javax.swing.JScrollPane scDesconto;
    private javax.swing.JScrollPane scOS;
    private javax.swing.JScrollPane scPdv;
    private javax.swing.JScrollPane scSetar;
    private javax.swing.JScrollPane scText;
    private javax.swing.JScrollPane scTotal;
    private javax.swing.JTextArea taOBS;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilio1;
    private javax.swing.JTable tbAuxilio2;
    private javax.swing.JTable tbCliente;
    private javax.swing.JTable tbComissao;
    private javax.swing.JTable tbDesconto;
    private javax.swing.JTable tbItem;
    private javax.swing.JTable tbListaDeInformaçoes;
    private javax.swing.JTable tbOS;
    private javax.swing.JTable tbSetar;
    private javax.swing.JTable tbTotal;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtCrediario;
    private javax.swing.JTextField txtEstoque;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPesquisa;
    private javax.swing.JTextField txtPesquisarCliente;
    private javax.swing.JTextField txtPreco;
    private javax.swing.JTextField txtQuantidade;
    private javax.swing.JTextField txtTipo;
    // End of variables declaration//GEN-END:variables
}
