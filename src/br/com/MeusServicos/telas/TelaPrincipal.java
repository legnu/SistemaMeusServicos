/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.MeusServicos.telas;

import br.com.MeusServicos.dal.ModuloConexao;
import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.joda.time.DateTime;

/**
 *
 * @author Leandro Clemente
 */
public class TelaPrincipal extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaPrincipal
     */
    public TelaPrincipal() {
        initComponents();
        conexao = ModuloConexao.conector();

    }

    public void pontoDeVendas() {
        PontoDeVendas pdv = new PontoDeVendas();
        pdv.setVisible(true);
        PontoDeVendas.lblUsuarioPDV.setText(lblUsuario.getText());
        PontoDeVendas.lblUsuarioPDV.setForeground(Color.red);
        this.dispose();

    }

    public void salarioFuncionario() {
        try {
            String funcionario;

            Date data = new Date();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date dSql = new java.sql.Date(data.getTime());
            df.format(dSql);

            String sql = "select idFuncionario,data_pagamento,salario,funcionario from tbFuncionarios";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbFuncionario.setModel(DbUtils.resultSetToTableModel(rs));

            for (int i = 0; i < tbFuncionario.getRowCount(); i++) {
                System.out.println(tbFuncionario.getModel().getValueAt(i, 1).toString());
                String pagamento = tbFuncionario.getModel().getValueAt(i, 1).toString();
                
                 
                if(tbFuncionario.getModel().getValueAt(i, 1).toString().isEmpty() == true){
                    
                }else if (data.after(df.parse(tbFuncionario.getModel().getValueAt(i, 1).toString())) ==  true) {
                    String sqr = "insert into tbgastos(nome, data_pagamento, status_pagamento, valor, tipo)values(?,?,?,?,?)";
                    pst = conexao.prepareStatement(sqr);
                    pst.setString(1, "Pagamento de Funcionario");
                    pst.setDate(2, dSql);
                    pst.setString(3, "Pago");
                    pst.setString(4, tbFuncionario.getModel().getValueAt(i, 2).toString());
                    pst.setString(5, "Salario");
                    pst.executeUpdate();

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

                    String sqo = "update tbFuncionarios set data_pagamento=? where idFuncionario=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setDate(1, dSqt);
                    pst.setString(2, tbFuncionario.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                }
            }

        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }
    
    

    public void instanciarTabelaHoje() {
        try {
            String sql = "select idservico as ID, servico as Serviço, valor as Valor, data_agendada as Data_Agendada, obs as OBS from tbservicos where date(data_agendada) = current_date() and tipo='Agendada' and  idservico ORDER BY time(data_agendada) asc";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbHoje.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void instanciarTabelaPendente() {
        try {
            String sql = "select idservico as ID, servico as Serviço, valor as Valor, data_agendada as Data_Agendada, obs as OBS from tbservicos where tipo='Pendente' and  idservico ORDER BY date(data_agendada) asc";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbPendentes.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void instanciarAgendamentosPendentes() {

        try {

            String sql = "select idservico, data_agendada from tbservicos where tipo='Agendada' or tipo='Pendente'";
            pst = conexao.prepareStatement(sql);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            rs = pst.executeQuery();
            tbAuxilioPendentes.setModel(DbUtils.resultSetToTableModel(rs));
            Date data = new Date();

            for (int i = 0; i < tbAuxilioPendentes.getRowCount(); i++) {
                Timestamp banco = Timestamp.valueOf(tbAuxilioPendentes.getModel().getValueAt(i, 1).toString());
                System.out.println(data);
                System.out.println(banco);
                if (data.after(banco) == true) {
                    String sqr = "update tbservicos set tipo='Pendente' where idservico=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setString(1, tbAuxilioPendentes.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();
                } else {
                    String sqr = "update tbservicos set tipo='Agendada' where idservico=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setString(1, tbAuxilioPendentes.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();
                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void instanciarTabelaVendas() {

        try {

            String sql = "select id as ID ,dia as Data_Emissao, hora as Hora, cliente as Cliente_Suprimento, venda as Valor , dia_Pagamento as Dia_Pagamento from tbtotalvendas where status_pagamento='Pago' and dia_Pagamento = current_date()";
            pst = conexao.prepareStatement(sql);

            rs = pst.executeQuery();
            tbRecebido.setModel(DbUtils.resultSetToTableModel(rs));

            double preco, x;

            preco = 0;

            for (int i = 0; i < tbRecebido.getRowCount(); i++) {
                x = Double.parseDouble(String.valueOf(Double.parseDouble(tbRecebido.getModel().getValueAt(i, 4).toString().replace(".", "")) / 100).replace(",", "."));

                preco = preco + x;
                lblResultadoVendas.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(preco))).replace(",", "."));

            }

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void instanciarTabelaGasto() {

        try {

            String sql = "select idgastos as ID, nome as Identificador, data_pagamento as Dia_Pagamento, valor as Valor  from tbgastos where status_pagamento='Pago' and data_pagamento = current_date()";
            pst = conexao.prepareStatement(sql);

            rs = pst.executeQuery();
            tbPago.setModel(DbUtils.resultSetToTableModel(rs));

            double preco, x;

            preco = 0;

            for (int i = 0; i < tbPago.getRowCount(); i++) {
                x = Double.parseDouble(String.valueOf(Double.parseDouble(tbPago.getModel().getValueAt(i, 3).toString().replace(".", "")) / 100).replace(",", "."));
                preco = preco + x;
                lblResultadoGastos.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(preco))).replace(",", "."));

            }

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void instanciarTabelaCliente() {
        try {

            String sql = "select id from tbtotalvendas where tipo='Venda'";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbAuxilio1.setModel(DbUtils.resultSetToTableModel(rs));

            for (int i = 0; i < tbAuxilio1.getRowCount(); i++) {

                String sqo = "select idcliente from tbtotalvendas where id=?";
                pst = conexao.prepareStatement(sqo);
                pst.setString(1, tbAuxilio1.getModel().getValueAt(i, 0).toString());
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                String id = tbAuxilio.getModel().getValueAt(0, 0).toString();

                String sqy = "select nomecli from tbclientes where idcli=?";
                pst = conexao.prepareStatement(sqy);
                pst.setString(1, id);
                rs = pst.executeQuery();
                tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));
                String nome = tbAuxilio.getModel().getValueAt(0, 0).toString();
                System.out.println(nome + " " + id);

                String sqr = "update tbtotalvendas set cliente=? where id=?";
                pst = conexao.prepareStatement(sqr);
                pst.setString(1, nome);
                pst.setString(2, tbAuxilio1.getModel().getValueAt(i, 0).toString());
                pst.executeUpdate();

            }
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

        scRecebido = new javax.swing.JScrollPane();
        tbRecebido = new javax.swing.JTable();
        scPago = new javax.swing.JScrollPane();
        tbPago = new javax.swing.JTable();
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        scAuxilio1 = new javax.swing.JScrollPane();
        tbAuxilio1 = new javax.swing.JTable();
        scAuxilioPendentes = new javax.swing.JScrollPane();
        tbAuxilioPendentes = new javax.swing.JTable();
        scAuxilioFuncionario = new javax.swing.JScrollPane();
        tbFuncionario = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblData = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblResultadoVendas = new javax.swing.JLabel();
        lblVendas = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lblGastos = new javax.swing.JLabel();
        lblResultadoGastos = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbHoje = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbPendentes = new javax.swing.JTable();
        Menu = new javax.swing.JMenuBar();
        MenCad = new javax.swing.JMenu();
        MenCadCli = new javax.swing.JMenuItem();
        MenCadOs = new javax.swing.JMenuItem();
        MenCadUsu = new javax.swing.JMenuItem();
        menServicos = new javax.swing.JMenuItem();
        menCadastroFornecedor = new javax.swing.JMenuItem();
        menCadastroServico = new javax.swing.JMenuItem();
        menCadFuncionario = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        relAtividadeClientes = new javax.swing.JMenuItem();
        relOrdemServico = new javax.swing.JMenuItem();
        RelOrçamento = new javax.swing.JMenuItem();
        menRelatorioDadosRelatorio = new javax.swing.JMenuItem();
        menEstoque = new javax.swing.JMenu();
        menEstoqueProdutoQuantidade = new javax.swing.JMenuItem();
        menEstoqueInventario = new javax.swing.JMenuItem();
        menCaixa = new javax.swing.JMenu();
        menCaixaCaixa = new javax.swing.JMenuItem();
        menCaixaSuplemento = new javax.swing.JMenuItem();
        menCaixaSangria = new javax.swing.JMenuItem();
        menCompra = new javax.swing.JMenu();
        menCompraCompra = new javax.swing.JMenuItem();
        menContas = new javax.swing.JMenu();
        menContasContas = new javax.swing.JMenuItem();
        menContasClientes = new javax.swing.JMenuItem();
        menAgendamento = new javax.swing.JMenu();
        menAgendamentosAgendamentos = new javax.swing.JMenuItem();
        menAgendamentosConcluidos = new javax.swing.JMenuItem();
        menPontoDeVendas = new javax.swing.JMenu();
        MenAju = new javax.swing.JMenu();
        MenAjuSob = new javax.swing.JMenuItem();
        MenOpc = new javax.swing.JMenu();
        MenOpcSai = new javax.swing.JMenuItem();

        tbRecebido.setModel(new javax.swing.table.DefaultTableModel(
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
        scRecebido.setViewportView(tbRecebido);

        tbPago.setModel(new javax.swing.table.DefaultTableModel(
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
        scPago.setViewportView(tbPago);

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

        tbAuxilioPendentes.setModel(new javax.swing.table.DefaultTableModel(
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
        scAuxilioPendentes.setViewportView(tbAuxilioPendentes);

        tbFuncionario.setModel(new javax.swing.table.DefaultTableModel(
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
        scAuxilioFuncionario.setViewportView(tbFuncionario);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tela Principal");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(204, 204, 204));

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblData.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblData.setText("Data:");

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("Data atual login:");

        lblUsuario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblUsuario.setText("Usuario");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("Nome do Usuário:");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/LogoNova 200x64 sem fundo.png"))); // NOI18N

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblResultadoVendas.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblResultadoVendas.setForeground(new java.awt.Color(0, 51, 204));
        lblResultadoVendas.setText("0.00");

        lblVendas.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblVendas.setForeground(new java.awt.Color(0, 51, 204));
        lblVendas.setText("Vendas:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblVendas)
                .addGap(8, 8, 8)
                .addComponent(lblResultadoVendas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblResultadoVendas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblVendas))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblGastos.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblGastos.setForeground(new java.awt.Color(255, 0, 0));
        lblGastos.setText(" Gastos:");

        lblResultadoGastos.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblResultadoGastos.setForeground(new java.awt.Color(255, 0, 0));
        lblResultadoGastos.setText("0.00");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblGastos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblResultadoGastos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGastos)
                    .addComponent(lblResultadoGastos))
                .addGap(13, 13, 13))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUsuario)
                            .addComponent(lblData)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(50, 50, 50)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblData)
                .addGap(63, 63, 63)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        jPanel6.setBackground(new java.awt.Color(204, 204, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Agendados para Hoje", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbHoje = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbHoje.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbHoje.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbHoje.setFocusable(false);
        jScrollPane1.setViewportView(tbHoje);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Agendamentos Pendentes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbPendentes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbPendentes.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tbPendentes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbPendentes.setFocusable(false);
        jScrollPane2.setViewportView(tbPendentes);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );

        Menu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        MenCad.setText("Cadastro");
        MenCad.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        MenCad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenCadActionPerformed(evt);
            }
        });

        MenCadCli.setText("Clientes");
        MenCadCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenCadCliActionPerformed(evt);
            }
        });
        MenCad.add(MenCadCli);

        MenCadOs.setText("OS");
        MenCadOs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenCadOsActionPerformed(evt);
            }
        });
        MenCad.add(MenCadOs);

        MenCadUsu.setText("Usuários");
        MenCadUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenCadUsuActionPerformed(evt);
            }
        });
        MenCad.add(MenCadUsu);

        menServicos.setText("Produtos");
        menServicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menServicosActionPerformed(evt);
            }
        });
        MenCad.add(menServicos);

        menCadastroFornecedor.setText("Fornecedor");
        menCadastroFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCadastroFornecedorActionPerformed(evt);
            }
        });
        MenCad.add(menCadastroFornecedor);

        menCadastroServico.setText("Serviço");
        menCadastroServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCadastroServicoActionPerformed(evt);
            }
        });
        MenCad.add(menCadastroServico);

        menCadFuncionario.setText("Funcionario");
        menCadFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCadFuncionarioActionPerformed(evt);
            }
        });
        MenCad.add(menCadFuncionario);

        Menu.add(MenCad);

        jMenu1.setText("Relatorios");
        jMenu1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        relAtividadeClientes.setText("Situação Clientela");
        relAtividadeClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relAtividadeClientesActionPerformed(evt);
            }
        });
        jMenu1.add(relAtividadeClientes);

        relOrdemServico.setText("Ordem De Serviço");
        relOrdemServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relOrdemServicoActionPerformed(evt);
            }
        });
        jMenu1.add(relOrdemServico);

        RelOrçamento.setText("Orçamento");
        RelOrçamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RelOrçamentoActionPerformed(evt);
            }
        });
        jMenu1.add(RelOrçamento);

        menRelatorioDadosRelatorio.setText("Dados do Relatorio");
        menRelatorioDadosRelatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menRelatorioDadosRelatorioActionPerformed(evt);
            }
        });
        jMenu1.add(menRelatorioDadosRelatorio);

        Menu.add(jMenu1);

        menEstoque.setText("Estoque");
        menEstoque.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        menEstoqueProdutoQuantidade.setText("Produto/Quantidade");
        menEstoqueProdutoQuantidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menEstoqueProdutoQuantidadeActionPerformed(evt);
            }
        });
        menEstoque.add(menEstoqueProdutoQuantidade);

        menEstoqueInventario.setText("Inventario");
        menEstoqueInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menEstoqueInventarioActionPerformed(evt);
            }
        });
        menEstoque.add(menEstoqueInventario);

        Menu.add(menEstoque);

        menCaixa.setText("Caixa");
        menCaixa.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        menCaixaCaixa.setText("Caixa");
        menCaixaCaixa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCaixaCaixaActionPerformed(evt);
            }
        });
        menCaixa.add(menCaixaCaixa);

        menCaixaSuplemento.setText("Suprimento");
        menCaixaSuplemento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCaixaSuplementoActionPerformed(evt);
            }
        });
        menCaixa.add(menCaixaSuplemento);

        menCaixaSangria.setText("Sangria");
        menCaixaSangria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCaixaSangriaActionPerformed(evt);
            }
        });
        menCaixa.add(menCaixaSangria);

        Menu.add(menCaixa);

        menCompra.setText("Compra");
        menCompra.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        menCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCompraActionPerformed(evt);
            }
        });

        menCompraCompra.setText("Compra");
        menCompraCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCompraCompraActionPerformed(evt);
            }
        });
        menCompra.add(menCompraCompra);

        Menu.add(menCompra);

        menContas.setText("Contas");
        menContas.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        menContasContas.setText("Contas");
        menContasContas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menContasContasActionPerformed(evt);
            }
        });
        menContas.add(menContasContas);

        menContasClientes.setText("Clientes");
        menContasClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menContasClientesActionPerformed(evt);
            }
        });
        menContas.add(menContasClientes);

        Menu.add(menContas);

        menAgendamento.setText("Agendamentos");
        menAgendamento.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        menAgendamentosAgendamentos.setText("Agendamentos");
        menAgendamentosAgendamentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menAgendamentosAgendamentosActionPerformed(evt);
            }
        });
        menAgendamento.add(menAgendamentosAgendamentos);

        menAgendamentosConcluidos.setText("Concluidos");
        menAgendamentosConcluidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menAgendamentosConcluidosActionPerformed(evt);
            }
        });
        menAgendamento.add(menAgendamentosConcluidos);

        Menu.add(menAgendamento);

        menPontoDeVendas.setText("PDV");
        menPontoDeVendas.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        menPontoDeVendas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menPontoDeVendasMouseClicked(evt);
            }
        });
        menPontoDeVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menPontoDeVendasActionPerformed(evt);
            }
        });
        Menu.add(menPontoDeVendas);

        MenAju.setText("Ajuda");
        MenAju.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        MenAjuSob.setText("Sobre");
        MenAjuSob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenAjuSobActionPerformed(evt);
            }
        });
        MenAju.add(MenAjuSob);

        Menu.add(MenAju);

        MenOpc.setText("Opções");
        MenOpc.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        MenOpcSai.setText("Sair");
        MenOpcSai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenOpcSaiActionPerformed(evt);
            }
        });
        MenOpc.add(MenOpcSai);

        Menu.add(MenOpc);

        setJMenuBar(Menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(1215, 598));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void MenOpcSaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenOpcSaiActionPerformed

        int sair = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja sair?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (sair == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_MenOpcSaiActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        //As Linhas abaixo substituem a label lblData pela Data Atual        
        instanciarTabelaVendas();
        instanciarTabelaGasto();
        instanciarTabelaCliente();
        instanciarAgendamentosPendentes();
        instanciarTabelaHoje();
        instanciarTabelaPendente();
        salarioFuncionario();
        Date data = new Date();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        lblData.setText(df.format(data));
    }//GEN-LAST:event_formWindowActivated

    private void MenAjuSobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenAjuSobActionPerformed
        TelaSobre sobre = new TelaSobre();
        sobre.setVisible(true);
    }//GEN-LAST:event_MenAjuSobActionPerformed

    private void MenCadUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenCadUsuActionPerformed
        CadUsuarios usuario = new CadUsuarios();
        usuario.setVisible(true);

    }//GEN-LAST:event_MenCadUsuActionPerformed

    private void MenCadCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenCadCliActionPerformed
        CadClientes cliente = new CadClientes();
        cliente.setVisible(true);

    }//GEN-LAST:event_MenCadCliActionPerformed

    private void MenCadOsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenCadOsActionPerformed
        CadOS os = new CadOS();
        os.setVisible(true);

    }//GEN-LAST:event_MenCadOsActionPerformed

    private void menServicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menServicosActionPerformed
        // TODO add your handling code here:
        CadProduto produto = new CadProduto();
        produto.setVisible(true);

    }//GEN-LAST:event_menServicosActionPerformed

    private void menPontoDeVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menPontoDeVendasActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_menPontoDeVendasActionPerformed

    private void menPontoDeVendasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menPontoDeVendasMouseClicked
        // TODO add your handling code here: 
        pontoDeVendas();
    }//GEN-LAST:event_menPontoDeVendasMouseClicked

    private void menCadastroFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCadastroFornecedorActionPerformed
        // TODO add your handling code here:
        CadFornecedor fornecedor = new CadFornecedor();
        fornecedor.setVisible(true);

    }//GEN-LAST:event_menCadastroFornecedorActionPerformed

    private void menEstoqueProdutoQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menEstoqueProdutoQuantidadeActionPerformed
        // TODO add your handling code here:
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao deste relatorio?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/Produto.jasper"), null, conexao);

                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_menEstoqueProdutoQuantidadeActionPerformed

    private void menCaixaCaixaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCaixaCaixaActionPerformed
        // TODO add your handling code here:
        telaCaixa caixa = new telaCaixa();
        caixa.setVisible(true);
    }//GEN-LAST:event_menCaixaCaixaActionPerformed

    private void menCaixaSuplementoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCaixaSuplementoActionPerformed
        // TODO add your handling code here:
        telaSuprimento suplemento = new telaSuprimento();
        suplemento.setVisible(true);
    }//GEN-LAST:event_menCaixaSuplementoActionPerformed

    private void menCaixaSangriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCaixaSangriaActionPerformed
        // TODO add your handling code here:
        telaSangria sangria = new telaSangria();
        sangria.setVisible(true);
    }//GEN-LAST:event_menCaixaSangriaActionPerformed

    private void menEstoqueInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menEstoqueInventarioActionPerformed
        // TODO add your handling code here:
        TelaInventario inventario = new TelaInventario();
        inventario.setVisible(true);

    }//GEN-LAST:event_menEstoqueInventarioActionPerformed

    private void menCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCompraActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_menCompraActionPerformed

    private void menCompraCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCompraCompraActionPerformed
        // TODO add your handling code here:
        TelaCompra compra = new TelaCompra();
        compra.setVisible(true);
    }//GEN-LAST:event_menCompraCompraActionPerformed

    private void menContasContasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menContasContasActionPerformed
        // TODO add your handling code here:
        TelaCrediario contas = new TelaCrediario();
        contas.setVisible(true);
    }//GEN-LAST:event_menContasContasActionPerformed

    private void menContasClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menContasClientesActionPerformed
        // TODO add your handling code here:
        RelacaoClientes clientes = new RelacaoClientes();
        clientes.setVisible(true);
    }//GEN-LAST:event_menContasClientesActionPerformed

    private void relAtividadeClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relAtividadeClientesActionPerformed
        // TODO add your handling code here:
        TelaClientes clientes = new TelaClientes();
        clientes.setVisible(true);
    }//GEN-LAST:event_relAtividadeClientesActionPerformed

    private void RelOrçamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RelOrçamentoActionPerformed
        // TODO add your handling code here:
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao deste relatorio?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/relOrçamento.jasper"), null, conexao);

                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_RelOrçamentoActionPerformed

    private void relOrdemServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relOrdemServicoActionPerformed
        // TODO add your handling code here:
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao deste relatorio?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/relOrdemDeServico.jasper"), null, conexao);

                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_relOrdemServicoActionPerformed

    private void menCadastroServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCadastroServicoActionPerformed
        // TODO add your handling code here:
        CadServico servico = new CadServico();
        servico.setVisible(true);
    }//GEN-LAST:event_menCadastroServicoActionPerformed

    private void MenCadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenCadActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_MenCadActionPerformed

    private void menCadFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCadFuncionarioActionPerformed
        // TODO add your handling code here:
        CadFuncionarios funcionarios = new CadFuncionarios();
        funcionarios.setVisible(true);
    }//GEN-LAST:event_menCadFuncionarioActionPerformed

    private void menAgendamentosAgendamentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menAgendamentosAgendamentosActionPerformed
        // TODO add your handling code here:
        TelaServico servico = new TelaServico();
        servico.setVisible(true);
    }//GEN-LAST:event_menAgendamentosAgendamentosActionPerformed

    private void menAgendamentosConcluidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menAgendamentosConcluidosActionPerformed
        // TODO add your handling code here:
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao deste relatorio?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/agendamentosConcluidos.jasper"), null, conexao);

                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_menAgendamentosConcluidosActionPerformed

    private void menRelatorioDadosRelatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menRelatorioDadosRelatorioActionPerformed
        // TODO add your handling code here:
        TelaRelatorio relatorio = new TelaRelatorio();
        relatorio.setVisible(true);
        
    }//GEN-LAST:event_menRelatorioDadosRelatorioActionPerformed

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
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu MenAju;
    private javax.swing.JMenuItem MenAjuSob;
    private javax.swing.JMenu MenCad;
    private javax.swing.JMenuItem MenCadCli;
    private javax.swing.JMenuItem MenCadOs;
    public static javax.swing.JMenuItem MenCadUsu;
    private javax.swing.JMenu MenOpc;
    private javax.swing.JMenuItem MenOpcSai;
    private javax.swing.JMenuBar Menu;
    private javax.swing.JMenuItem RelOrçamento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblGastos;
    private javax.swing.JLabel lblResultadoGastos;
    private javax.swing.JLabel lblResultadoVendas;
    public static javax.swing.JLabel lblUsuario;
    private javax.swing.JLabel lblVendas;
    private javax.swing.JMenu menAgendamento;
    private javax.swing.JMenuItem menAgendamentosAgendamentos;
    private javax.swing.JMenuItem menAgendamentosConcluidos;
    private javax.swing.JMenuItem menCadFuncionario;
    private javax.swing.JMenuItem menCadastroFornecedor;
    private javax.swing.JMenuItem menCadastroServico;
    private javax.swing.JMenu menCaixa;
    private javax.swing.JMenuItem menCaixaCaixa;
    private javax.swing.JMenuItem menCaixaSangria;
    private javax.swing.JMenuItem menCaixaSuplemento;
    private javax.swing.JMenu menCompra;
    private javax.swing.JMenuItem menCompraCompra;
    private javax.swing.JMenu menContas;
    private javax.swing.JMenuItem menContasClientes;
    private javax.swing.JMenuItem menContasContas;
    private javax.swing.JMenu menEstoque;
    private javax.swing.JMenuItem menEstoqueInventario;
    private javax.swing.JMenuItem menEstoqueProdutoQuantidade;
    private javax.swing.JMenu menPontoDeVendas;
    private javax.swing.JMenuItem menRelatorioDadosRelatorio;
    private javax.swing.JMenuItem menServicos;
    private javax.swing.JMenuItem relAtividadeClientes;
    private javax.swing.JMenuItem relOrdemServico;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilio1;
    private javax.swing.JScrollPane scAuxilioFuncionario;
    private javax.swing.JScrollPane scAuxilioPendentes;
    private javax.swing.JScrollPane scPago;
    private javax.swing.JScrollPane scRecebido;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilio1;
    private javax.swing.JTable tbAuxilioPendentes;
    private javax.swing.JTable tbFuncionario;
    private javax.swing.JTable tbHoje;
    private javax.swing.JTable tbPago;
    private javax.swing.JTable tbPendentes;
    private javax.swing.JTable tbRecebido;
    // End of variables declaration//GEN-END:variables
}
