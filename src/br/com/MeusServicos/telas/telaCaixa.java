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
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Ad3ln0r
 */
public class telaCaixa extends javax.swing.JFrame {

    /**
     * Creates new form telaCaixa
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public telaCaixa() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    public void instanciarTabela() {
        try {
            String sql = "select * from tbgastos";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbAuxilio.setModel(DbUtils.resultSetToTableModel(rs));

            double caixa = Double.parseDouble(lblTotalRecebido.getText().replace(".", "")) / 100 - Double.parseDouble(lblTotalPago.getText().replace(".", "")) / 100;
            lblTotalCaixa.setText(new DecimalFormat("#,##0.00").format(caixa).replace(",", "."));

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {

        }
    }

    public void setarPorData() {

        try {                  
                           
                java.util.Date aInicial = DaInicial.getDate();
                java.sql.Date bInicial = new java.sql.Date(aInicial.getTime());
                System.out.println(tbCaixaRecebido.getRowCount());

                java.util.Date aFinal = DaFinal.getDate();
                java.sql.Date bFinal = new java.sql.Date(aFinal.getTime());
                
                String sql = "select id as Id, dia as Data, hora as Hora, venda as Valor from tbtotalvendas where dia between '" + bInicial + "' and '" + bFinal + "';";

                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                tbCaixaRecebido.setModel(DbUtils.resultSetToTableModel(rs));

                double entrada, saida, x, y;

                entrada = 0;
                saida = 0;

                String sqo = "select idgastos as ID, nome as Nome_Dia, data_pagamento as Data_Pagamento, valor  as Valor from tbgastos where status_pagamento='Pago' and data_pagamento between '" + bInicial + "' and '" + bFinal + "';";

                pst = conexao.prepareStatement(sqo);
                rs = pst.executeQuery();
                tbCaixaPago.setModel(DbUtils.resultSetToTableModel(rs));
                

                for (int i = 0; i < tbCaixaRecebido.getRowCount(); i++) {
                    x = Double.parseDouble(String.valueOf(Double.parseDouble(tbCaixaRecebido.getModel().getValueAt(i, 3).toString().replace(".", "")) / 100).replace(",", "."));

                    entrada = entrada + x;
                    lblTotalRecebido.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(entrada))).replace(",", "."));

                }

                for (int i = 0; i < tbCaixaPago.getRowCount(); i++) {
                    y = Double.parseDouble(String.valueOf(Double.parseDouble(tbCaixaPago.getModel().getValueAt(i, 3).toString().replace(".", "")) / 100).replace(",", "."));
                    saida = saida + y;
                    lblTotalPago.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(saida))).replace(",", "."));

                }
                 
                if(tbCaixaRecebido.getRowCount() == 0){
                    lblTotalRecebido.setText("0.00");
                }
                
                if(tbCaixaPago.getRowCount() == 0){
                    lblTotalPago.setText("0.00");
                    
                }

                double caixa = Double.parseDouble(lblTotalRecebido.getText().replace(".", "")) / 100 - Double.parseDouble(lblTotalPago.getText().replace(".", "")) / 100;
                lblTotalCaixa.setText(new DecimalFormat("#,##0.00").format(caixa).replace(",", "."));
            

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void Status() {

        instanciarTabela();

        try {
            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date dSql = new java.sql.Date(d.getTime());
            df.format(dSql);

            String x;

            for (int i = 0; i < tbAuxilio.getRowCount(); i++) {
                x = tbAuxilio.getModel().getValueAt(i, 3).toString();
                Date data = df.parse(x);
                System.out.println();

                if (d.after(data) || d.equals(data)) {
                    String sqo = "update tbgastos set status_pagamento='Pago' where idgastos=?";
                    pst = conexao.prepareStatement(sqo);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                } else if (d.before(data)) {
                    String sqr = "update tbgastos set status_pagamento='Pendente' where idgastos=?";
                    pst = conexao.prepareStatement(sqr);
                    pst.setString(1, tbAuxilio.getModel().getValueAt(i, 0).toString());
                    pst.executeUpdate();

                }

            }

            instanciarTabela();

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void instanciarTabelaVendas() {

        try {

            String sql = "select id as ID, nome as Nome_Dia, hora as Hora, venda as Vendas from tbtotalvendas";
            pst = conexao.prepareStatement(sql);

            rs = pst.executeQuery();
            tbCaixaRecebido.setModel(DbUtils.resultSetToTableModel(rs));

            double preco, x;

            preco = 0;

            for (int i = 0; i < tbCaixaRecebido.getRowCount(); i++) {
                x = Double.parseDouble(String.valueOf(Double.parseDouble(tbCaixaRecebido.getModel().getValueAt(i, 3).toString().replace(".", "")) / 100).replace(",", "."));

                preco = preco + x;
                lblTotalRecebido.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(preco))).replace(",", "."));

            }

        } catch (java.lang.NullPointerException e) {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }

    public void instanciarTabelaGasto() {

        try {
            Status();

            String sql = "select idgastos as ID, nome as Nome_Dia, data_pagamento as Data_Pagamento, valor  as Valor from tbgastos where status_pagamento=?";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, "Pago");
            rs = pst.executeQuery();
            tbCaixaPago.setModel(DbUtils.resultSetToTableModel(rs));

            double preco, x;

            preco = 0;

            for (int i = 0; i < tbCaixaPago.getRowCount(); i++) {
                x = Double.parseDouble(String.valueOf(Double.parseDouble(tbCaixaPago.getModel().getValueAt(i, 3).toString().replace(".", "")) / 100).replace(",", "."));
                preco = preco + x;
                lblTotalPago.setText(new DecimalFormat("#,##0.00").format(Double.parseDouble(String.valueOf(preco))).replace(",", "."));

            }

        } catch (java.lang.NullPointerException e) {

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

        grupo1 = new javax.swing.ButtonGroup();
        scAuxilio = new javax.swing.JScrollPane();
        tbAuxilio = new javax.swing.JTable();
        scAuxilioPago = new javax.swing.JScrollPane();
        tbAuxilioPago = new javax.swing.JTable();
        scAuxilioRecebido = new javax.swing.JScrollPane();
        tbAuxilioRecebido = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        DaInicial = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        DaFinal = new com.toedter.calendar.JDateChooser();
        btnPesquisar = new javax.swing.JToggleButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        pnRelatorio = new javax.swing.JPanel();
        lblNome1 = new javax.swing.JLabel();
        lblTotalCaixa = new javax.swing.JLabel();
        pnPago = new javax.swing.JPanel();
        scCaixaPago = new javax.swing.JScrollPane();
        tbCaixaPago = new javax.swing.JTable();
        lblTotalPago = new javax.swing.JLabel();
        lblNome2 = new javax.swing.JLabel();
        pnRecebido = new javax.swing.JPanel();
        scCaixaRecebido = new javax.swing.JScrollPane();
        tbCaixaRecebido = new javax.swing.JTable();
        lblTotalRecebido = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();

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

        tbAuxilioPago.setModel(new javax.swing.table.DefaultTableModel(
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
        scAuxilioPago.setViewportView(tbAuxilioPago);

        tbAuxilioRecebido.setModel(new javax.swing.table.DefaultTableModel(
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
        scAuxilioRecebido.setViewportView(tbAuxilioRecebido);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Caixa");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Painel", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Inicial", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        DaInicial.setDateFormatString("y-MM-dd");
        DaInicial.setFocusable(false);
        DaInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                DaInicialKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Final", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        DaFinal.setDateFormatString("y-MM-dd");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(DaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        btnPesquisar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/lupa-removebg-preview.png"))); // NOI18N
        btnPesquisar.setSelected(true);
        btnPesquisar.setText("Pesquisar");
        btnPesquisar.setContentAreaFilled(false);
        btnPesquisar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/Logo_200x164.png"))); // NOI18N
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);

        pnRelatorio.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        lblNome1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblNome1.setText("Valor Lucrado(R$):");

        lblTotalCaixa.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTotalCaixa.setText("0.00");

        javax.swing.GroupLayout pnRelatorioLayout = new javax.swing.GroupLayout(pnRelatorio);
        pnRelatorio.setLayout(pnRelatorioLayout);
        pnRelatorioLayout.setHorizontalGroup(
            pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRelatorioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNome1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotalCaixa)
                .addContainerGap())
        );
        pnRelatorioLayout.setVerticalGroup(
            pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRelatorioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnRelatorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome1)
                    .addComponent(lblTotalCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnPago.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Saida", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        pnPago.setPreferredSize(new java.awt.Dimension(464, 398));

        tbCaixaPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbCaixaPago.setFocusable(false);
        tbCaixaPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCaixaPagoMouseClicked(evt);
            }
        });
        scCaixaPago.setViewportView(tbCaixaPago);

        lblTotalPago.setBackground(new java.awt.Color(51, 255, 0));
        lblTotalPago.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTotalPago.setText("0.00");

        lblNome2.setBackground(new java.awt.Color(51, 255, 0));
        lblNome2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblNome2.setText("Valor Pago(R$):");

        javax.swing.GroupLayout pnPagoLayout = new javax.swing.GroupLayout(pnPago);
        pnPago.setLayout(pnPagoLayout);
        pnPagoLayout.setHorizontalGroup(
            pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scCaixaPago, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnPagoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNome2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotalPago)
                .addContainerGap())
        );
        pnPagoLayout.setVerticalGroup(
            pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPagoLayout.createSequentialGroup()
                .addComponent(scCaixaPago, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome2)
                    .addComponent(lblTotalPago, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnRecebido.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Entrada", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbCaixaRecebido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbCaixaRecebido.setFocusable(false);
        tbCaixaRecebido.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCaixaRecebidoMouseClicked(evt);
            }
        });
        scCaixaRecebido.setViewportView(tbCaixaRecebido);

        lblTotalRecebido.setBackground(new java.awt.Color(51, 255, 0));
        lblTotalRecebido.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTotalRecebido.setText("0.00");

        lblNome.setBackground(new java.awt.Color(51, 255, 0));
        lblNome.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblNome.setText("Valor Recebido(R$):");

        javax.swing.GroupLayout pnRecebidoLayout = new javax.swing.GroupLayout(pnRecebido);
        pnRecebido.setLayout(pnRecebidoLayout);
        pnRecebidoLayout.setHorizontalGroup(
            pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scCaixaRecebido)
            .addGroup(pnRecebidoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotalRecebido)
                .addContainerGap())
        );
        pnRecebidoLayout.setVerticalGroup(
            pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRecebidoLayout.createSequentialGroup()
                .addComponent(scCaixaRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addGroup(pnRecebidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome)
                    .addComponent(lblTotalRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnRelatorio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnPago, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnRecebido, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(pnRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(pnPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(pnRelatorio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 696, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        instanciarTabelaGasto();
        instanciarTabelaVendas();
        instanciarTabela();

    }//GEN-LAST:event_formWindowActivated

    private void DaInicialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DaInicialKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_DaInicialKeyReleased

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        // TODO add your handling code here:
        setarPorData();

    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void tbCaixaPagoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCaixaPagoMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_tbCaixaPagoMouseClicked

    private void tbCaixaRecebidoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCaixaRecebidoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbCaixaRecebidoMouseClicked

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
            java.util.logging.Logger.getLogger(telaCaixa.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(telaCaixa.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(telaCaixa.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(telaCaixa.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new telaCaixa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DaFinal;
    private com.toedter.calendar.JDateChooser DaInicial;
    private javax.swing.JToggleButton btnPesquisar;
    private javax.swing.ButtonGroup grupo1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblNome1;
    private javax.swing.JLabel lblNome2;
    private javax.swing.JLabel lblTotalCaixa;
    private javax.swing.JLabel lblTotalPago;
    private javax.swing.JLabel lblTotalRecebido;
    private javax.swing.JPanel pnPago;
    private javax.swing.JPanel pnRecebido;
    private javax.swing.JPanel pnRelatorio;
    private javax.swing.JScrollPane scAuxilio;
    private javax.swing.JScrollPane scAuxilioPago;
    private javax.swing.JScrollPane scAuxilioRecebido;
    private javax.swing.JScrollPane scCaixaPago;
    private javax.swing.JScrollPane scCaixaRecebido;
    private javax.swing.JTable tbAuxilio;
    private javax.swing.JTable tbAuxilioPago;
    private javax.swing.JTable tbAuxilioRecebido;
    private javax.swing.JTable tbCaixaPago;
    private javax.swing.JTable tbCaixaRecebido;
    // End of variables declaration//GEN-END:variables
}
