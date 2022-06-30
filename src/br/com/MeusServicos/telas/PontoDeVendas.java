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
import java.text.ParseException;
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

    public void InstanciarCombobox() {
        try {

            String sql = "select nomecli from tbclientes";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbCliente.addItem(rs.getString("nomecli"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void CadastroCliente() {
        CadClientes cliente = new CadClientes();
        cliente.setVisible(true);

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

    public void criarNota() {
        String sql = "insert into tbvenda(nome, preco, cliente_venda, pagamento, quantidade)values(?,?,?,?,?)";

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtNome.getText());

            if (tipo.equals("Produto")) {

                pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText()) * Integer.parseInt(txtQuantidade.getText())))).replace(",", "."));
            }
            if (tipo.equals("OS")) {

                pst.setString(2, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(txtPreco.getText())))).replace(",", "."));
            }

            pst.setString(3, cbCliente.getSelectedItem().toString());
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

            } else if (Integer.parseInt(txtQuantidade.getText()) > Integer.parseInt(txtEstoque.getText())) {
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

            }

            if (tipo.equals("OS")) {
                int adicionado = 0;

                for (int i = 0; i < tbItens.getRowCount(); i++) {
                    String Nome = tbItens.getModel().getValueAt(i, 1).toString();
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
            limpar();

        }
    }

    private void pesquisar() {

        if (tipo.equals("Produto")) {

            String sql = "select produto as Produto, valor_venda as Preço, obs as Observações, idproduto as ID, fornecedor as Fornecedor, valor_compra as Custo from tbprodutos where produto like ?";

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Produtos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        }
        if (tipo.equals("OS")) {

            String sql = "select servico as Serviço, Valor as Valor, equipamento as Equipamento, previsao_entreg_os as Entrega, defeito as Defeito, os as OS, tecnico as Tecnico, data_os as Data, cliente as Cliente from tbos where servico like ?";

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtPesquisa.getText() + "%");
                rs = pst.executeQuery();
                tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
                pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ordem de Serviço", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

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
                        JOptionPane.showMessageDialog(null, "Aguarde.");
                        tirarId();
                        criarId();
                        JOptionPane.showMessageDialog(null, "OS removida com sucesso");
                        limpar();

                    }
                } else {
                    QuantidadeAdicionada();
                    String sql = "delete from tbvenda where idvenda=?";
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
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();
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
            limpar();

        }

    }

    private void criarId() {
        String sql = "alter table tbvenda add idvenda MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void tirarIdOS() {

        String sql = "alter table tbos drop os";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    private void criarIdOS() {
        String sql = "alter table tbos add os MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

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
            tirarIdOS();
            criarIdOS();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void QuantidadeTirada() {
        try {
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
            pst.setDouble(1, auxilioQuantidade * valor_compra);
            pst.setDouble(2, auxilioQuantidade * valor_venda);
            pst.setString(3, txtNome.getText());
            pst.executeUpdate();

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
            auxilioQuantidade = Double.parseDouble(tbAuxilio1.getModel().getValueAt(0, 0).toString().replace(".", "")) / 100;

            String sqo = "update tbprodutos set referencial_compra=?, referencial_venda=? where produto=?";
            pst = conexao.prepareStatement(sqo);
            pst.setDouble(1, auxilioQuantidade * valor_compra);
            pst.setDouble(2, auxilioQuantidade * valor_venda);
            pst.setString(3, txtNome.getText());
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

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
            limpar();

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
                limpar();

            }
        }
    }

    public void soma() {
        double preco, x;

        String sql = "select preco from tbvenda";
        try {

            preco = 0;

            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbTotal.setModel(DbUtils.resultSetToTableModel(rs));
            for (int i = 0; i < tbTotal.getRowCount(); i++) {
                x = Double.parseDouble(tbTotal.getModel().getValueAt(i, 0).toString().replace(".", "")) / 100;
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

    public void instanciarTabelaProduto() {
        String sql = "select produto as Produto, valor_venda as Preço, obs as Observações, idproduto as ID, fornecedor as Fornecedor, valor_compra as Custo, estoque as Estoque, quantidade as Quantidade from tbprodutos";
        try {
            limpar();
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
            txtQuantidade.setEnabled(true);
            pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Produtos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            tipo = "Produto";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void instanciarTabelaOS() {
        String sql = "select servico as Serviço, Valor as Valor, equipamento as Equipamento, previsao_entreg_os as Entrega, defeito as Defeito, os as OS, tecnico as Tecnico, data_os as Data, cliente as Cliente from tbos";
        try {
            limpar();
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbListaDeInformaçoes.setModel(DbUtils.resultSetToTableModel(rs));
            txtQuantidade.setEnabled(false);
            pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ordem de Serviço", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));

            tipo = "OS";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    public void habilitarAdicionar() {
        btnAdicionar.setEnabled(true);
        btnRemove.setEnabled(false);

    }

    public void criar_relatorio() {

        try {

            int linha;
            linha = tbItens.getRowCount();
            taRelatorio.append("Descrição" + "\n\n\n");

            for (int i = 0; i <= linha; i++) {
                taRelatorio.append("OS/Produto: " + tbItens.getModel().getValueAt(i, 1).toString()
                        + "          Cliente: " + tbItens.getModel().getValueAt(i, 3).toString()
                        + "          Forma De Pagamento: " + tbItens.getModel().getValueAt(i, 4).toString()
                        + "          Preço(R$): " + tbItens.getModel().getValueAt(i, 2).toString()
                        + "          Quantidade: " + tbItens.getModel().getValueAt(i, 5).toString()
                        + "\n\n");

            }

        } catch (Exception sqlEx) {

        }
    }

    public void concluir() {

        Date dataHoraAtual = new Date();
        String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);

        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dSql = new java.sql.Date(d.getTime());
        df.format(dSql);

        try {

            String sqo = "insert into tbtotalvendas(dia, nome, hora, venda, obs)values(?,?,?,?,?)";

            pst = conexao.prepareStatement(sqo);
            criar_relatorio();
            pst.setString(1, dSql.toString());
            pst.setString(2, dSql.toString());
            pst.setString(3, hora);
            pst.setString(4, new DecimalFormat("#,##0.00").format(Float.parseFloat(String.valueOf(Double.parseDouble(lblValorTotal.getText().replace(".", "")) / 100))));
            pst.setString(5, taRelatorio.getText());

            pst.executeUpdate();

            String sql = "select nome from tbvenda where quantidade=0";
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

            imprimir_nota();
            JOptionPane.showMessageDialog(null, "Aguarde.");
            limpar_nota();
            JOptionPane.showMessageDialog(null, "Concluido.");
            limpar();

        } catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(null, e);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }

    }

    private void setar_campos() {
        habilitarAdicionar();
        int setar = tbListaDeInformaçoes.getSelectedRow();

        if (tipo.equals("OS")) {
            cbCliente.addItem(tbListaDeInformaçoes.getModel().getValueAt(setar, 8).toString());
            txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
            txtPreco.setText(String.valueOf(Double.parseDouble(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString().replace(".", "")) / 100));
            taOBS.setText("Equipamento: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString() + "\n" + "Defeito: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 3).toString() + "\n" + "Previsão de entrega: " + tbListaDeInformaçoes.getModel().getValueAt(setar, 4).toString());
            cbCliente.setSelectedItem(tbListaDeInformaçoes.getModel().getValueAt(setar, 8).toString());

            txtQuantidade.setEnabled(false);
            txtQuantidade.setText("0");
            txtEstoque.setText("0");
            btnAdicionar.setEnabled(true);
            cbCliente.setEnabled(false);
        }

        if (tipo.equals("Produto")) {

            txtNome.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 0).toString());
            txtPreco.setText(String.valueOf(Double.parseDouble(tbListaDeInformaçoes.getModel().getValueAt(setar, 1).toString().replace(".", "")) / 100));
            taOBS.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 2).toString());
            txtEstoque.setText(tbListaDeInformaçoes.getModel().getValueAt(setar, 7).toString());
            InstanciarCombobox();
            btnAdicionar.setEnabled(true);
            cbCliente.setEnabled(true);
        }
    }

    public void setarCamposRemover() {
        int setar = tbItens.getSelectedRow();
        limpar();
        try {
            cbCliente.addItem(tbItens.getModel().getValueAt(setar, 3).toString());
            txtID.setText(tbItens.getModel().getValueAt(setar, 0).toString());
            txtNome.setText(tbItens.getModel().getValueAt(setar, 1).toString());
            txtPreco.setText(String.valueOf(Double.parseDouble(tbItens.getModel().getValueAt(setar, 2).toString().replace(".", "")) / 100));
            cbCliente.setSelectedItem(tbItens.getModel().getValueAt(setar, 3).toString());
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
            limpar();

        }
    }

    public void limpar() {
        txtNome.setText(null);
        txtPreco.setText(null);
        txtID.setText(null);

        taOBS.setText(null);
        taRelatorio.setText(null);
        txtEstoque.setText(null);
        txtQuantidade.setText(null);
        btnAdicionar.setEnabled(false);
        btnRemove.setEnabled(false);
        txtQuantidade.setEnabled(false);
        cbCliente.setEnabled(false);
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

        lblPrecoFinal = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        OS_Produtos = new javax.swing.ButtonGroup();
        scTotal = new javax.swing.JScrollPane();
        tbTotal = new javax.swing.JTable();
        scCliente = new javax.swing.JScrollPane();
        tbCliente = new javax.swing.JTable();
        scSetar = new javax.swing.JScrollPane();
        tbSetar = new javax.swing.JTable();
        scRelatorio = new javax.swing.JScrollPane();
        taRelatorio = new javax.swing.JTextArea();
        scRelatoriotb = new javax.swing.JScrollPane();
        tbRelatorio = new javax.swing.JTable();
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
        lblPesquisa = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        lblPreco = new javax.swing.JLabel();
        lblUsuarioPDV = new javax.swing.JLabel();
        lblCamposObrigatorios = new javax.swing.JLabel();
        lblQuantidade = new javax.swing.JLabel();
        lblEstoque = new javax.swing.JLabel();
        rbOrdemDeServico = new javax.swing.JRadioButton();
        rbProduto = new javax.swing.JRadioButton();
        txtPesquisa = new javax.swing.JTextField();
        txtNome = new javax.swing.JTextField();
        txtPreco = new javax.swing.JTextField();
        txtEstoque = new javax.swing.JTextField();
        txtQuantidade = new javax.swing.JTextField();
        btnAdicionar = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnCliente = new javax.swing.JButton();
        pnNota = new javax.swing.JPanel();
        pnTbNota = new javax.swing.JPanel();
        scItens = new javax.swing.JScrollPane();
        tbItens = new javax.swing.JTable();
        lblTotal = new javax.swing.JLabel();
        lblValorTotal = new javax.swing.JLabel();
        btnConcluir = new javax.swing.JButton();
        btnConcluir1 = new javax.swing.JButton();
        pnTbPrincipal = new javax.swing.JPanel();
        scPdv = new javax.swing.JScrollPane();
        tbListaDeInformaçoes = new javax.swing.JTable();
        pnOBS = new javax.swing.JPanel();
        scText = new javax.swing.JScrollPane();
        taOBS = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        cbFormaDePagamento = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        cbCliente = new javax.swing.JComboBox<>();

        lblPrecoFinal.setText("jLabel1");

        txtID.setEnabled(false);

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ponto de Vendas");
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

        lblUsuarioPDV.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblUsuarioPDV.setText("Usuario");

        lblCamposObrigatorios.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCamposObrigatorios.setText("*Campos Obrigatorios");

        lblQuantidade.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblQuantidade.setText("Quantidade:");

        lblEstoque.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblEstoque.setText("Estoque:");

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

        txtEstoque.setEditable(false);
        txtEstoque.setEnabled(false);
        txtEstoque.setFocusable(false);

        txtQuantidade.setEnabled(false);
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
        });

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

        pnNota.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        pnTbNota.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nota", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

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

        javax.swing.GroupLayout pnTbNotaLayout = new javax.swing.GroupLayout(pnTbNota);
        pnTbNota.setLayout(pnTbNotaLayout);
        pnTbNotaLayout.setHorizontalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 483, Short.MAX_VALUE)
            .addGroup(pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scItens, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE))
        );
        pnTbNotaLayout.setVerticalGroup(
            pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 403, Short.MAX_VALUE)
            .addGroup(pnTbNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scItens, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout pnNotaLayout = new javax.swing.GroupLayout(pnNota);
        pnNota.setLayout(pnNotaLayout);
        pnNotaLayout.setHorizontalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNotaLayout.createSequentialGroup()
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnNotaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnConcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnNotaLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnTbNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnNotaLayout.createSequentialGroup()
                                .addComponent(lblTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblValorTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnConcluir1)))))
                .addGap(16, 16, 16))
        );
        pnNotaLayout.setVerticalGroup(
            pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNotaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnTbNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTotal)
                        .addComponent(lblValorTotal))
                    .addComponent(btnConcluir1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnConcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

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

        javax.swing.GroupLayout pnTbPrincipalLayout = new javax.swing.GroupLayout(pnTbPrincipal);
        pnTbPrincipal.setLayout(pnTbPrincipalLayout);
        pnTbPrincipalLayout.setHorizontalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scPdv, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );
        pnTbPrincipalLayout.setVerticalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scPdv, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
        );

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
                .addComponent(scText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
        );
        pnOBSLayout.setVerticalGroup(
            pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 131, Short.MAX_VALUE)
            .addGroup(pnOBSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Forma de Pagamento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cbFormaDePagamento.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cbFormaDePagamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A Vista (Dinhero)", "Cartão Debito", "Pix", "Cartão Credito", "Crediario" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbFormaDePagamento, javax.swing.GroupLayout.Alignment.TRAILING, 0, 168, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbFormaDePagamento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cbCliente.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        cbCliente.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbCliente, javax.swing.GroupLayout.Alignment.TRAILING, 0, 168, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblQuantidade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblEstoque)
                                .addGap(12, 12, 12)
                                .addComponent(txtEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblPreco)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblNome)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCliente)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnOBS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(rbOrdemDeServico)
                            .addGap(21, 21, 21)
                            .addComponent(rbProduto))
                        .addComponent(lblUsuarioPDV, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pnTbPrincipal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblPesquisa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblCamposObrigatorios)))
                .addGap(16, 16, 16)
                .addComponent(pnNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rbOrdemDeServico)
                                    .addComponent(rbProduto)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblCamposObrigatorios)
                                .addComponent(lblPesquisa))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addComponent(pnTbPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(lblNome))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCliente)))
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblQuantidade)
                                    .addComponent(lblEstoque)
                                    .addComponent(txtEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblPreco)
                                    .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(pnOBS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(lblUsuarioPDV)))
                .addGap(16, 16, 16))
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

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        // TODO add your handling code here:
        CadastroCliente();
    }//GEN-LAST:event_btnClienteActionPerformed

    private void btnConcluir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConcluir1ActionPerformed
        // TODO add your handling code here:
        AdicionarDesconto();
    }//GEN-LAST:event_btnConcluir1ActionPerformed

    private void scPdvMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scPdvMouseClicked

    }//GEN-LAST:event_scPdvMouseClicked

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        // TODO add your handling code here:

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
    private javax.swing.JComboBox<String> cbCliente;
    private javax.swing.JComboBox<String> cbFormaDePagamento;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblCamposObrigatorios;
    private javax.swing.JLabel lblEstoque;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblPesquisa;
    private javax.swing.JLabel lblPreco;
    private javax.swing.JLabel lblPrecoFinal;
    private javax.swing.JLabel lblQuantidade;
    private javax.swing.JLabel lblTotal;
    public static javax.swing.JLabel lblUsuarioPDV;
    public static javax.swing.JLabel lblValorTotal;
    private javax.swing.JPanel pnNota;
    private javax.swing.JPanel pnOBS;
    private javax.swing.JPanel pnTbNota;
    private javax.swing.JPanel pnTbPrincipal;
    private javax.swing.JRadioButton rbOrdemDeServico;
    private javax.swing.JRadioButton rbProduto;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilio1;
    private javax.swing.JScrollPane scAuxilio2;
    private javax.swing.JScrollPane scCliente;
    private javax.swing.JScrollPane scDesconto;
    private javax.swing.JScrollPane scItens;
    private javax.swing.JScrollPane scOS;
    private javax.swing.JScrollPane scPdv;
    private javax.swing.JScrollPane scRelatorio;
    private javax.swing.JScrollPane scRelatoriotb;
    private javax.swing.JScrollPane scSetar;
    private javax.swing.JScrollPane scText;
    private javax.swing.JScrollPane scTotal;
    private javax.swing.JTextArea taOBS;
    private javax.swing.JTextArea taRelatorio;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilio1;
    private javax.swing.JTable tbAuxilio2;
    private javax.swing.JTable tbCliente;
    private javax.swing.JTable tbDesconto;
    private javax.swing.JTable tbItens;
    private javax.swing.JTable tbListaDeInformaçoes;
    private javax.swing.JTable tbOS;
    private javax.swing.JTable tbRelatorio;
    private javax.swing.JTable tbSetar;
    private javax.swing.JTable tbTotal;
    private javax.swing.JTextField txtEstoque;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPesquisa;
    private javax.swing.JTextField txtPreco;
    private javax.swing.JTextField txtQuantidade;
    // End of variables declaration//GEN-END:variables
}
