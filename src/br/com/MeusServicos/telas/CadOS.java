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
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.jfree.chart.title.Title;

/**
 *
 * @author Ad3ln0r
 */
public class CadOS extends javax.swing.JFrame {

    /**
     * Creates new form CadOS
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private String tipo;

    public CadOS() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    public void InstanciarTabelaCliente() {
        String sql = "select idcli as ID, nomecli as Nome, telefonecli as Telefone from tbclientes ";
        try {
            limpar();
            txtCliPesquisar.setText(null);
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
            pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Clientes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));
            tipo = "Cliente";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void pesquisar_cliente() {
        String sql = "select idcli as ID, nomecli as Nome, telefonecli as Telefone from tbclientes where nomecli like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void instanciarTabelaOrcamento() {
        String sql = "select os as Numero_da_Ordem_de_Serviço, data_os as Emição, tipo as Tipo, situacao as Situação, previsao_entreg_os as Previsão_de_Entrega, equipamento as Equipamento, defeito as Defeito, servico as Serviço, tecnico as Tecnico, valor as Valor, cliente as Cliente from tbos ";
        try {
            limpar();
            txtCliPesquisar.setText(null);
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
            pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Orçamento e Ordem de Serviço", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12)));
            tipo = "Orcamento_OS";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void pesquisar_orcamento() {
        String sql = "select os as Numero_da_Ordem_de_Serviço, data_os as Emição, tipo as Tipo, situacao as Situação, previsao_entreg_os as Previsão_de_Entrega, equipamento as Equipamento, defeito as Defeito, servico as Serviço, tecnico as Tecnico, valor as Valor, cliente as Cliente from tbos where servico like ? ";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            tbPrincipal.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    public void pesquisar() {
        if (tipo.equals("Cliente") == true) {

            pesquisar_cliente();

        } else {

            pesquisar_orcamento();
        }
    }

    private void setar_campos() {
        if (tipo.equals("Cliente") == true) {

            int setar = tbPrincipal.getSelectedRow();
            txtCliente.setText(tbPrincipal.getModel().getValueAt(setar, 1).toString());
            txtOsDef.setEnabled(true);
            txtOsEquip.setEnabled(true);
            txtOsServ.setEnabled(true);
            txtOsTec.setEnabled(true);
            txtOsValor.setEnabled(true);
            cbTipo.setEnabled(true);
            cboOsSit.setEnabled(true);
            dtPrevisao.setEnabled(true);
            btnAdicionar.setEnabled(true);
            btnResetar.setEnabled(true);
            btnRemover.setEnabled(false);
            btnImprimir.setEnabled(false);
            btnEditar.setEnabled(false);

        } else {

            try {

                int setar = tbPrincipal.getSelectedRow();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                txtOs.setText(tbPrincipal.getModel().getValueAt(setar, 0).toString());
                cbTipo.setSelectedItem(tbPrincipal.getModel().getValueAt(setar, 2).toString());
                cboOsSit.setSelectedItem(tbPrincipal.getModel().getValueAt(setar, 3).toString());
                dtPrevisao.setDate(df.parse(tbPrincipal.getModel().getValueAt(setar, 4).toString()));
                txtOsEquip.setText(tbPrincipal.getModel().getValueAt(setar, 5).toString());
                txtOsDef.setText(tbPrincipal.getModel().getValueAt(setar, 6).toString());
                txtOsServ.setText(tbPrincipal.getModel().getValueAt(setar, 7).toString());
                txtOsTec.setText(tbPrincipal.getModel().getValueAt(setar, 8).toString());
                double valor = Double.parseDouble(tbPrincipal.getModel().getValueAt(setar, 9).toString().replace(".", "")) / 100;
                txtOsValor.setText(String.valueOf(valor));
                txtCliente.setText(tbPrincipal.getModel().getValueAt(setar, 10).toString());
                txtOsDef.setEnabled(true);
                txtOsEquip.setEnabled(true);
                txtOsServ.setEnabled(true);
                txtOsTec.setEnabled(true);
                txtOsValor.setEnabled(true);
                cbTipo.setEnabled(true);
                cboOsSit.setEnabled(true);
                dtPrevisao.setEnabled(true);
                btnAdicionar.setEnabled(false);
                btnResetar.setEnabled(true);
                btnRemover.setEnabled(true);
                btnImprimir.setEnabled(true);
                btnEditar.setEnabled(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }
        }
    }

    private void emitir_os() {

        try {
            int confirma = 0;
            if ((txtCliente.getText().isEmpty()) || (txtOsEquip.getText().isEmpty()) || (txtOsDef.getText().isEmpty()) || (txtOsServ.getText().isEmpty()) || (txtOsValor.getText().isEmpty()) || (txtOsTec.getText().isEmpty()) || dtPrevisao.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios.");
                limpar();
                confirma = 1;
            } else if ((Double.parseDouble(txtOsValor.getText().replace(",", ".")) <= 0)) {
                JOptionPane.showMessageDialog(null, "Adicione um valor maior que 0.");
                limpar();
            } else if (confirma == 0) {
                Date d = dtPrevisao.getDate();
                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);

                String sql = "insert into tbos(tipo,situacao,previsao_entreg_os,equipamento,defeito,servico,tecnico,valor,cliente) values(?,?,?,?,?,?,?,?,?)";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, cbTipo.getSelectedItem().toString());
                pst.setString(2, cboOsSit.getSelectedItem().toString());
                pst.setDate(3, dSql);
                pst.setString(4, txtOsEquip.getText());
                pst.setString(5, txtOsDef.getText());
                pst.setString(6, txtOsServ.getText());
                pst.setString(7, txtOsTec.getText());
                pst.setString(8, new DecimalFormat("#,##0.00").format(Double.parseDouble(txtOsValor.getText().replace(",", "."))).replace(",", "."));
                pst.setString(9, txtCliente.getText());

                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "OS emitida com sucesso.");
                    limpar();

                }
            }
        } catch (java.lang.NumberFormatException c) {
            JOptionPane.showMessageDialog(null, "Campo Valor só suporta Numeros.");
            JOptionPane.showMessageDialog(null, "Campo Valor Deve ser salvo no formato 0.00 .");
            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void alterar_os() {
     

        try {
            int confirma = 0;
            if ((txtCliente.getText().isEmpty()) || (txtOsEquip.getText().isEmpty()) || (txtOsDef.getText().isEmpty()) || (txtOsServ.getText().isEmpty()) || (txtOsValor.getText().isEmpty()) || (txtOsTec.getText().isEmpty()) || dtPrevisao.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatorios.");
                limpar();
                confirma = 1;
            } else if ((Double.parseDouble(txtOsValor.getText().replace(",", ".")) <= 0)) {
                JOptionPane.showMessageDialog(null, "Adicione um valor maior que 0.");
                limpar();
            } else if (confirma == 0) {
                Date d = dtPrevisao.getDate();
                java.sql.Date dSql = new java.sql.Date(d.getTime());
                df.format(dSql);

                String sql = "update tbos set tipo=?,situacao=?,previsao_entreg_os=?,equipamento=?,defeito=?,servico=?,tecnico=?,valor=? where os=?";
                pst = conexao.prepareStatement(sql);
                pst.setString(1, cbTipo.getSelectedItem().toString());
                pst.setString(2, cboOsSit.getSelectedItem().toString());
                pst.setDate(3, dSql);
                pst.setString(4, txtOsEquip.getText());
                pst.setString(5, txtOsDef.getText());
                pst.setString(6, txtOsServ.getText());
                pst.setString(7, txtOsTec.getText());
                pst.setString(8, new DecimalFormat("#,##0.00").format(Double.parseDouble(txtOsValor.getText().replace(",", "."))).replace(",", "."));
                pst.setString(9, txtOs.getText());
          

                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "OS alterada com sucesso.");
                    limpar();

                }
            }
        } catch (java.lang.NumberFormatException c) {
            JOptionPane.showMessageDialog(null, "Campo Valor só suporta Numeros.");
            JOptionPane.showMessageDialog(null, "Campo Valor Deve ser salvo no formato 0.00 .");
            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();

        }
    }

    private void excluir_os() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir esta OS?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbos where os=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtOs.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Clique no OK e Aguarde.");
                    tirarId();
                    criarId();
                    JOptionPane.showMessageDialog(null, "OS excluida com sucesso.");
                    limpar();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }

        }
    }

    private void tirarId() {

        String sql = "alter table tbos drop os";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }

    }

    private void criarId() {
        String sql = "alter table tbos add os MEDIUMINT NOT NULL AUTO_INCREMENT Primary key";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            limpar();
        }
    }

    private void imprimir_os() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressao desta OS?", "Atençao", JOptionPane.YES_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {

                HashMap filtro = new HashMap();
                filtro.put("os", Integer.parseInt(txtOs.getText()));

                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/RelatorioCadOS.jasper"), filtro, conexao);

                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                limpar();

            }
        }
    }

    private void limpar() {
        txtCliId.setText(null);
        txtCliPesquisar.setText(null);
        txtCliente.setText(null);
        txtOs.setText(null);
        txtOsDef.setText(null);
        txtOsEquip.setText(null);
        txtOsServ.setText(null);
        txtOsTec.setText(null);
        txtOsValor.setText("0.00");
        dtPrevisao.setDate(null);
        txtCliId.setEnabled(false);
        txtOsDef.setEnabled(false);
        txtOsEquip.setEnabled(false);
        txtOsServ.setEnabled(false);
        txtOsTec.setEnabled(false);
        txtOsValor.setEnabled(false);
        cbTipo.setEnabled(false);
        cboOsSit.setEnabled(false);
        dtPrevisao.setEnabled(false);
        btnAdicionar.setEnabled(false);
        btnResetar.setEnabled(false);
        btnRemover.setEnabled(false);
        btnImprimir.setEnabled(false);
       
       
        btnEditar.setEnabled(false);

        ((DefaultTableModel) tbPrincipal.getModel()).setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtCliId = new javax.swing.JTextField();
        txtOs = new javax.swing.JTextField();
        grupoTbPrincipal = new javax.swing.ButtonGroup();
        lblValorTotal = new javax.swing.JLabel();
        lblEquipamento = new javax.swing.JLabel();
        lblDefeito = new javax.swing.JLabel();
        lblSituacao = new javax.swing.JLabel();
        lblServico = new javax.swing.JLabel();
        lblTecnico = new javax.swing.JLabel();
        lblPrevisaoEntrega = new javax.swing.JLabel();
        lblPesquisar = new javax.swing.JLabel();
        lblTipo = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        lblCamposObrigatorios = new javax.swing.JLabel();
        dtPrevisao = new com.toedter.calendar.JDateChooser();
        rbClientes = new javax.swing.JRadioButton();
        rbOS_Orcamento = new javax.swing.JRadioButton();
        txtOsValor = new javax.swing.JTextField();
        txtOsEquip = new javax.swing.JTextField();
        txtOsDef = new javax.swing.JTextField();
        txtOsServ = new javax.swing.JTextField();
        txtOsTec = new javax.swing.JTextField();
        txtCliPesquisar = new javax.swing.JTextField();
        txtCliente = new javax.swing.JTextField();
        cboOsSit = new javax.swing.JComboBox<>();
        cbTipo = new javax.swing.JComboBox<>();
        btnAdicionar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnResetar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        pnTbPrincipal = new javax.swing.JPanel();
        scTbPrincipal = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable();
        btnCliente = new javax.swing.JToggleButton();
        btnImprimir = new javax.swing.JButton();

        txtCliId.setEditable(false);

        txtOs.setEditable(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de OS");
        setResizable(false);

        lblValorTotal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblValorTotal.setText("*Valor Total:");

        lblEquipamento.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblEquipamento.setText("*Equipamento:");

        lblDefeito.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblDefeito.setText("*Defeito:");

        lblSituacao.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblSituacao.setText("*Situação:");

        lblServico.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblServico.setText("*Serviço:");

        lblTecnico.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblTecnico.setText("*Técnico:");

        lblPrevisaoEntrega.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPrevisaoEntrega.setText("*Previsao Entrega:");

        lblPesquisar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPesquisar.setText("Pesquisar:");

        lblTipo.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblTipo.setText("*Tipo:");

        lblCliente.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCliente.setText("*Cliente:");

        lblCamposObrigatorios.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCamposObrigatorios.setText("*Campos Obrigatorios");

        dtPrevisao.setDateFormatString("yyyy-MM-dd");
        dtPrevisao.setEnabled(false);

        grupoTbPrincipal.add(rbClientes);
        rbClientes.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbClientes.setText("Clientes");
        rbClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbClientesActionPerformed(evt);
            }
        });

        grupoTbPrincipal.add(rbOS_Orcamento);
        rbOS_Orcamento.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        rbOS_Orcamento.setText("Orçamento/Ordem de Serviço");
        rbOS_Orcamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbOS_OrcamentoActionPerformed(evt);
            }
        });

        txtOsValor.setEnabled(false);

        txtOsEquip.setEnabled(false);
        txtOsEquip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOsEquipActionPerformed(evt);
            }
        });

        txtOsDef.setEnabled(false);

        txtOsServ.setEnabled(false);

        txtOsTec.setEnabled(false);

        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        txtCliente.setEnabled(false);
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        cboOsSit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Na bancada", "Entrega OK", "Orçamento REPROVADO", "Aguardando Aprovação", "Aguardando peças", "Abandonado pelo cliente", "Retornou" }));
        cboOsSit.setEnabled(false);

        cbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Orçamento", "Ordem de Serviço" }));
        cbTipo.setEnabled(false);
        cbTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTipoActionPerformed(evt);
            }
        });

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeAdicionar-removebg-preview.png"))); // NOI18N
        btnAdicionar.setToolTipText("Adicionar");
        btnAdicionar.setContentAreaFilled(false);
        btnAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionar.setEnabled(false);
        btnAdicionar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeEditar-removebg-preview.png"))); // NOI18N
        btnEditar.setToolTipText("Pesquisar");
        btnEditar.setContentAreaFilled(false);
        btnEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditar.setEnabled(false);
        btnEditar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnResetar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeRestart-removebg-preview.png"))); // NOI18N
        btnResetar.setToolTipText("Editar");
        btnResetar.setContentAreaFilled(false);
        btnResetar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnResetar.setEnabled(false);
        btnResetar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnResetar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetarActionPerformed(evt);
            }
        });

        btnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/iconeRemover-removebg-preview.png"))); // NOI18N
        btnRemover.setToolTipText("Deletar");
        btnRemover.setContentAreaFilled(false);
        btnRemover.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemover.setEnabled(false);
        btnRemover.setPreferredSize(new java.awt.Dimension(80, 80));
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        pnTbPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbPrincipal.setFocusable(false);
        tbPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPrincipalMouseClicked(evt);
            }
        });
        scTbPrincipal.setViewportView(tbPrincipal);

        javax.swing.GroupLayout pnTbPrincipalLayout = new javax.swing.GroupLayout(pnTbPrincipal);
        pnTbPrincipal.setLayout(pnTbPrincipalLayout);
        pnTbPrincipalLayout.setHorizontalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scTbPrincipal)
        );
        pnTbPrincipalLayout.setVerticalGroup(
            pnTbPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scTbPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
        );

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

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/MeusServicos/icones/ImpresoraIcon.png"))); // NOI18N
        btnImprimir.setToolTipText("Imprimir");
        btnImprimir.setContentAreaFilled(false);
        btnImprimir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnImprimir.setEnabled(false);
        btnImprimir.setPreferredSize(new java.awt.Dimension(80, 80));
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
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
                        .addComponent(rbClientes)
                        .addGap(18, 18, 18)
                        .addComponent(rbOS_Orcamento))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPesquisar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblCamposObrigatorios))
                    .addComponent(pnTbPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblServico)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtOsServ, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblTecnico)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtOsTec, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblValorTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtOsValor))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblEquipamento)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtOsEquip, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(27, 27, 27)
                                        .addComponent(lblDefeito)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtOsDef))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(lblCliente)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblSituacao)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cboOsSit, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblTipo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblPrevisaoEntrega)))
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dtPrevisao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(4, 4, 4)))
                .addGap(16, 16, 16))
            .addGroup(layout.createSequentialGroup()
                .addGap(183, 183, 183)
                .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(btnResetar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(183, 183, 183))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPesquisar)
                    .addComponent(lblCamposObrigatorios))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbClientes)
                    .addComponent(rbOS_Orcamento))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnTbPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCliente)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSituacao)
                            .addComponent(cboOsSit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTipo)
                            .addComponent(cbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPrevisaoEntrega)))
                    .addComponent(dtPrevisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEquipamento)
                    .addComponent(txtOsEquip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDefeito)
                    .addComponent(txtOsDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliente))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServico)
                    .addComponent(txtOsServ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTecnico)
                    .addComponent(txtOsTec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValorTotal)
                    .addComponent(txtOsValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEditar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemover, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnImprimir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnResetar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
        emitir_os();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        alterar_os();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnResetarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetarActionPerformed
        // TODO add your handling code here:
        limpar();
    }//GEN-LAST:event_btnResetarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        // TODO add your handling code here:
        excluir_os();
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tbPrincipalMouseClicked

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void cbTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTipoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbTipoActionPerformed

    private void txtOsEquipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOsEquipActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOsEquipActionPerformed

    private void rbOS_OrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbOS_OrcamentoActionPerformed
        // TODO add your handling code here:
        instanciarTabelaOrcamento();
    }//GEN-LAST:event_rbOS_OrcamentoActionPerformed

    private void rbClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbClientesActionPerformed
        // TODO add your handling code here:
        InstanciarTabelaCliente();
    }//GEN-LAST:event_rbClientesActionPerformed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        // TODO add your handling code here:
        CadClientes cliente = new CadClientes();
        cliente.setVisible(true);
    }//GEN-LAST:event_btnClienteActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        // TODO add your handling code here:
       imprimir_os();
    }//GEN-LAST:event_btnImprimirActionPerformed

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
            java.util.logging.Logger.getLogger(CadOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadOS().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JToggleButton btnCliente;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnRemover;
    private javax.swing.JButton btnResetar;
    private javax.swing.JComboBox<String> cbTipo;
    private javax.swing.JComboBox<String> cboOsSit;
    private com.toedter.calendar.JDateChooser dtPrevisao;
    private javax.swing.ButtonGroup grupoTbPrincipal;
    private javax.swing.JLabel lblCamposObrigatorios;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblDefeito;
    private javax.swing.JLabel lblEquipamento;
    private javax.swing.JLabel lblPesquisar;
    private javax.swing.JLabel lblPrevisaoEntrega;
    private javax.swing.JLabel lblServico;
    private javax.swing.JLabel lblSituacao;
    private javax.swing.JLabel lblTecnico;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JPanel pnTbPrincipal;
    private javax.swing.JRadioButton rbClientes;
    private javax.swing.JRadioButton rbOS_Orcamento;
    private javax.swing.JScrollPane scTbPrincipal;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtCliId;
    private javax.swing.JTextField txtCliPesquisar;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtOs;
    private javax.swing.JTextField txtOsDef;
    private javax.swing.JTextField txtOsEquip;
    private javax.swing.JTextField txtOsServ;
    private javax.swing.JTextField txtOsTec;
    private javax.swing.JTextField txtOsValor;
    // End of variables declaration//GEN-END:variables
}
