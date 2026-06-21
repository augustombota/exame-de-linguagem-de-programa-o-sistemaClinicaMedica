package com.clinica.controller;

import com.clinica.model.*;
import com.clinica.service.*;
import com.clinica.util.SessaoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MainController {

    @FXML private Label lblUsuarioLogado;
    @FXML private TabPane tabPanePrincipal;
    @FXML private Tab tabPacientes;
    @FXML private Tab tabAgendamentos;
    @FXML private Tab tabProntuarios;

    // CONTROLES DE PACIENTES
    @FXML private TextField txtIdPaciente;
    @FXML private TextField txtNomePaciente;
    @FXML private TextField txtCpfPaciente;
    @FXML private DatePicker dpDataNascimento;
    @FXML private TextField txtTelefonePaciente;
    @FXML private TextField txtEnderecoPaciente;
    @FXML private TextArea txtHistorico;
    @FXML private Button btnInativar;
    @FXML private TableView<Paciente> tblPacientes;
    @FXML private TableColumn<Paciente, Integer> colId;
    @FXML private TableColumn<Paciente, String> colNome;
    @FXML private TableColumn<Paciente, String> colCpf;
    @FXML private TableColumn<Paciente, String> colTelefone;
    @FXML private TableColumn<Paciente, String> colHistorico;

    // CONTROLES DE AGENDAMENTOS
    @FXML private TextField txtIdAgendamento;
    @FXML private ComboBox<Paciente> cbPaciente;
    @FXML private ComboBox<Medico> cbMedico;
    @FXML private DatePicker dpDataConsulta;
    @FXML private TextField txtHorarioConsulta;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TableView<Agendamento> tblAgendamentos;
    @FXML private TableColumn<Agendamento, Integer> colAgId;
    @FXML private TableColumn<Agendamento, String> colAgPaciente;
    @FXML private TableColumn<Agendamento, String> colAgMedico;
    @FXML private TableColumn<Agendamento, String> colAgData;
    @FXML private TableColumn<Agendamento, String> colAgHora;
    @FXML private TableColumn<Agendamento, String> colAgStatus;

    // CONTROLES DE PRONTUÁRIOS
    @FXML private ComboBox<Paciente> cbProntuarioPaciente;
    @FXML private Label lblAlertaHistorico;
    @FXML private TextArea txtDiagnostico;
    @FXML private TextArea txtPrescricao;
    @FXML private TextArea txtObservacoes;

    // INSTÂNCIAS DOS SERVIÇOS
    private final PacienteService pacienteService = new PacienteService();
    private final AgendamentoService agendamentoService = new AgendamentoService();
    private final ProntuarioService prontuarioService = new ProntuarioService();

    // LISTAS OBSERVÁVEIS
    private final ObservableList<Paciente> obsPacientes = FXCollections.observableArrayList();
    private final ObservableList<Agendamento> obsAgendamentos = FXCollections.observableArrayList();

    private void embutirEstilo(Scene scene) {
        URL cssUrl = getClass().getResource("/com/clinica/styles/modern.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    @FXML
    public void abrirPainelAdminManual() throws IOException {
        Stage stage = (Stage) tabPanePrincipal.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/fxml/AdminView.fxml"));
        Scene scene = new Scene(loader.load());

        embutirEstilo(scene);

        stage.setScene(scene);
        stage.setTitle("Clínica Médica - Painel de Administração");
    }

    @FXML
    public void initialize() {
        Usuario atual = SessaoUsuario.getUsuario();
        if (atual != null) {
            lblUsuarioLogado.setText("Sessão activa: " + atual.getNome() + " [" + atual.getPerfil() + "]");
            aplicarControleDeAcesso(atual.getPerfil());
        }

        if (tblPacientes != null) {
            configurarTabelaPacientes();
            handleMostrarPacientesAtivos();
        }
        if (tblAgendamentos != null) {
            configurarTabelaAgendamentos();
            carregarCombosAgendamento();
            atualizarTabelaAgendamentos();
        }

        if (cbProntuarioPaciente != null) {
            cbProntuarioPaciente.setItems(obsPacientes);

            // 🟢 CONFIGURAÇÃO PARA O COMBOBOX DO CONSULTÓRIO EXIBIR APENAS O NOME DO PACIENTE
            cbProntuarioPaciente.setConverter(new javafx.util.StringConverter<Paciente>() {
                @Override
                public String toString(Paciente paciente) {
                    return (paciente != null) ? paciente.getNome() : "";
                }

                @Override
                public Paciente fromString(String string) {
                    return null; // Não é necessário para seleção simples
                }
            });
        }
    }

    private void aplicarControleDeAcesso(String perfil) {
        if (tabPanePrincipal != null) {
            if ("Recepcionista".equalsIgnoreCase(perfil) && tabProntuarios != null) {
                tabPanePrincipal.getTabs().remove(tabProntuarios);
            } else if ("Médico".equalsIgnoreCase(perfil)) {
                if (tabPacientes != null) tabPanePrincipal.getTabs().remove(tabPacientes);
                if (tabAgendamentos != null) tabPanePrincipal.getTabs().remove(tabAgendamentos);
            }
        }
    }

    private void configurarTabelaPacientes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colHistorico.setCellValueFactory(new PropertyValueFactory<>("historicoPreliminar"));
    }

    private void configurarTabelaAgendamentos() {
        colAgId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAgPaciente.setCellValueFactory(new PropertyValueFactory<>("nomePaciente"));
        colAgMedico.setCellValueFactory(new PropertyValueFactory<>("nomeMedico"));
        colAgData.setCellValueFactory(new PropertyValueFactory<>("dataConsulta"));
        colAgHora.setCellValueFactory(new PropertyValueFactory<>("horarioConsulta"));
        colAgStatus.setCellValueFactory(new PropertyValueFactory<>("statusConsulta"));
    }

    private void carregarCombosAgendamento() {
        try {
            // Mantém as tuas atribuições originais de dados
            cbPaciente.setItems(obsPacientes);
            cbMedico.setItems(FXCollections.observableArrayList(agendamentoService.obterMedicos()));
            cbStatus.setItems(FXCollections.observableArrayList("Agendada", "Confirmada", "Realizada", "Cancelada"));
            cbStatus.setValue("Agendada");

            // 🟢 1. CONFIGURAÇÃO PARA O COMBOBOX DE PACIENTES EXIBIR APENAS O NOME
            cbPaciente.setConverter(new javafx.util.StringConverter<Paciente>() {
                @Override
                public String toString(Paciente paciente) {
                    return (paciente != null) ? paciente.getNome() : "";
                }

                @Override
                public Paciente fromString(String string) {
                    return null; // Não é necessário para seleção simples
                }
            });

            // 🟢 2. CONFIGURAÇÃO PARA O COMBOBOX DE MÉDICOS EXIBIR APENAS O NOME
            cbMedico.setConverter(new javafx.util.StringConverter<Medico>() {
                @Override
                public String toString(Medico medico) {
                    return (medico != null) ? medico.getNome() : "";
                }

                @Override
                public Medico fromString(String string) {
                    return null;
                }
            });

        } catch (Exception e) {
            exibirAlerta("Erro", "Erro ao carregar listas de seleção: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ==========================================
    // 🔑 MÓDULO DE AUTONOMIA: ALTERAR A PRÓPRIA SENHA
    // ==========================================

    @FXML
    public void handleAlterarMinhaSenha() {
        Usuario logado = SessaoUsuario.getUsuario();
        if (logado == null) return;

        // 🛡️ NOVA REGRA DE NEGÓCIO: Se for o Administrador, redireciona para o Painel Admin
        if ("Administrador".equalsIgnoreCase(logado.getPerfil())) {
            try {
                System.out.println("[NAVEGAÇÃO] Administrador clicou em Alterar Senha. Redirecionando para o Painel Oficial...");

                // Reutiliza a lógica para abrir o ecrã do administrador
                abrirPainelAdminManual();

                exibirAlerta("Painel de Administração", "Redirecionado para o painel de gestão. Altere a sua senha selecionando a sua conta na tabela.", Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                exibirAlerta("Erro de Navegação", "Não foi possível abrir o Painel do Administrador: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            return; // Para a execução aqui, não abrindo a caixa de diálogo abaixo
        }

        // 👤 COMPORTAMENTO PARA UTILIZADORES COMUNS (Médico / Recepcionista)
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Alterar Senha");
        dialog.setHeaderText("Definir nova senha para: " + logado.getNome());
        dialog.setContentText("Digite a sua nova senha:");

        java.util.Optional<String> resultado = dialog.showAndWait();

        resultado.ifPresent(novaSenha -> {
            try {
                if (novaSenha.trim().isEmpty()) {
                    throw new Exception("A senha não pode estar em branco!");
                }

                // 1. Criptografa a nova senha em MD5
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] array = md.digest(novaSenha.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : array) {
                    sb.append(String.format("%02x", b));
                }
                String senhaCriptografada = sb.toString();

                // 2. Atualiza na Base de Dados (Corrigido para 'clinica')
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinica", "root", "")) {
                    String sql = "UPDATE usuarios SET senha = ? WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, senhaCriptografada);
                        stmt.setInt(2, logado.getId());
                        stmt.executeUpdate();
                    }
                }

                // 3. Sincroniza a sessão
                logado.setSenha(senhaCriptografada);

                exibirAlerta("Sucesso", "A tua senha foi alterada com sucesso!", Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                exibirAlerta("Falha ao atualizar a senha", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    // ==========================================
    // FILTRAGEM DE PACIENTES
    // ==========================================

    @FXML
    public void handleMostrarPacientesAtivos() {
        try {
            obsPacientes.clear();
            obsPacientes.addAll(pacienteService.obterAtivos());
            tblPacientes.setItems(obsPacientes);
            System.out.println("[FILTRO] A mostrar apenas pacientes ativos.");
        } catch (Exception e) {
            exibirAlerta("Erro", "Falha ao listar pacientes ativos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleMostrarPacientesConsultados() {
        try {
            obsPacientes.clear();
            obsPacientes.addAll(pacienteService.obterJaConsultados());
            tblPacientes.setItems(obsPacientes);
            System.out.println("[FILTRO] A mostrar apenas pacientes que já realizaram consultas.");
        } catch (Exception e) {
            exibirAlerta("Erro", "Falha ao listar pacientes já consultados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void atualizarTabelaAgendamentos() {
        try {
            obsAgendamentos.clear();
            obsAgendamentos.addAll(agendamentoService.obterTodos());
            tblAgendamentos.setItems(obsAgendamentos);
        } catch (Exception e) {
            exibirAlerta("Erro", "Falha ao listar agenda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // AÇÕES DE AGENDAMENTO
    @FXML
    public void handleSalvarAgendamento() {
        try {
            int id = (txtIdAgendamento.getText() == null || txtIdAgendamento.getText().isEmpty()) ? 0 : Integer.parseInt(txtIdAgendamento.getText());

            LocalTime hora;
            try {
                hora = LocalTime.parse(txtHorarioConsulta.getText().trim());
            } catch (DateTimeParseException e) {
                throw new Exception("Formato de hora inválido! Use HH:MM (Ex: 14:00).");
            }

            Agendamento ag = new Agendamento();
            ag.setId(id);
            ag.setPaciente(cbPaciente.getValue());
            ag.setMedico(cbMedico.getValue());
            ag.setDataConsulta(dpDataConsulta.getValue());
            ag.setHorarioConsulta(hora);
            ag.setStatusConsulta(cbStatus.getValue());

            agendamentoService.agendar(ag);
            exibirAlerta("Sucesso", "Consulta agendada/atualizada com sucesso!", Alert.AlertType.INFORMATION);
            handleLimparAgendamento();
            atualizarTabelaAgendamentos();
        } catch (Exception e) {
            exibirAlerta("Validação de Agenda", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void handleCarregarAgendamento() {
        Agendamento sel = tblAgendamentos.getSelectionModel().getSelectedItem();
        if (sel != null) {
            txtIdAgendamento.setText(String.valueOf(sel.getId()));
            dpDataConsulta.setValue(sel.getDataConsulta());
            txtHorarioConsulta.setText(sel.getHorarioConsulta().toString());
            cbStatus.setValue(sel.getStatusConsulta());

            cbPaciente.getItems().stream().filter(p -> p.getId() == sel.getPaciente().getId()).findFirst().ifPresent(p -> cbPaciente.setValue(p));
            cbMedico.getItems().stream().filter(m -> m.getId() == sel.getMedico().getId()).findFirst().ifPresent(m -> cbMedico.setValue(m));
        }
    }

    @FXML
    public void handleLimparAgendamento() {
        txtIdAgendamento.clear();
        cbPaciente.setValue(null);
        cbMedico.setValue(null);
        dpDataConsulta.setValue(null);
        txtHorarioConsulta.clear();
        cbStatus.setValue("Agendada");
        tblAgendamentos.getSelectionModel().clearSelection();
    }

    // MÉTODOS DE CONTROLE DA TELA DE PACIENTES
    @FXML
    public void handleSalvarPaciente() {
        try {
            int id = (txtIdPaciente.getText() == null || txtIdPaciente.getText().isEmpty()) ? 0 : Integer.parseInt(txtIdPaciente.getText());
            Paciente p = new Paciente(id, txtNomePaciente.getText(), txtCpfPaciente.getText(), dpDataNascimento.getValue(), txtEnderecoPaciente.getText(), txtTelefonePaciente.getText(), txtHistorico.getText(), true);
            pacienteService.salvar(p);
            exibirAlerta("Sucesso", "Paciente salvo!", Alert.AlertType.INFORMATION);
            handleLimparCampos();
            handleMostrarPacientesAtivos();
        } catch (Exception e) { exibirAlerta("Erro", e.getMessage(), Alert.AlertType.WARNING); }
    }

    @FXML
    public void handleInativarPaciente() {
        try { int id = Integer.parseInt(txtIdPaciente.getText()); pacienteService.inativar(id); handleLimparCampos(); handleMostrarPacientesAtivos(); } catch (Exception e) { exibirAlerta("Erro", e.getMessage(), Alert.AlertType.ERROR); }
    }

    @FXML
    public void handleCarregarFormulario() {
        Paciente sel = tblPacientes.getSelectionModel().getSelectedItem();
        if (sel != null) { txtIdPaciente.setText(String.valueOf(sel.getId())); txtNomePaciente.setText(sel.getNome()); txtCpfPaciente.setText(sel.getCpf()); dpDataNascimento.setValue(sel.getDataNascimento()); txtTelefonePaciente.setText(sel.getTelefone()); txtEnderecoPaciente.setText(sel.getEndereco()); txtHistorico.setText(sel.getHistoricoPreliminar()); btnInativar.setDisable(false); }
    }

    @FXML
    public void handleLimparCampos() {
        txtIdPaciente.clear(); txtNomePaciente.clear(); txtCpfPaciente.clear(); dpDataNascimento.setValue(null); txtTelefonePaciente.clear(); txtEnderecoPaciente.clear(); txtHistorico.clear(); btnInativar.setDisable(true); tblPacientes.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleLogout() throws IOException {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Saída");
        alerta.setHeaderText("Módulo de Segurança");
        alerta.setContentText("Deseja realmente terminar a sua sessão no sistema?");

        java.util.Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            SessaoUsuario.encerrarSessao();
            Stage stage = (Stage) lblUsuarioLogado.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/fxml/LoginView.fxml"));
            Scene scene = new Scene(loader.load());

            embutirEstilo(scene);

            stage.setScene(scene);
            stage.setTitle("Clínica - Autenticação");
            stage.centerOnScreen();
        }
    }

    // MÉTODOS DE CONTROLE DA TELA DE PRONTUÁRIOS E EVOLUÇÃO CLÍNICA
    @FXML
    public void handleCarregarProntuarioExistente() {
        Paciente sel = cbProntuarioPaciente.getValue();
        if (sel != null) {
            lblAlertaHistorico.setText(sel.getHistoricoPreliminar() != null && !sel.getHistoricoPreliminar().isEmpty()
                    ? sel.getHistoricoPreliminar()
                    : "Nenhuma alergia ou complicação registada na admissão.");

            try {
                Prontuario existente = prontuarioService.obterPorPaciente(sel.getId());
                if (existente != null) {
                    txtDiagnostico.setText(existente.getDiagnosticoMedico());
                    txtPrescricao.setText(existente.getPrescricaoMedicamentos());
                    txtObservacoes.setText(existente.getObservacoesClinicas());
                } else {
                    txtDiagnostico.clear();
                    txtPrescricao.clear();
                    txtObservacoes.clear();
                }
            } catch (Exception e) {
                exibirAlerta("Erro", "Erro ao buscar histórico: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleSalvarProntuario() {
        try {
            Paciente pacienteSelecionado = cbProntuarioPaciente.getValue();
            if (pacienteSelecionado == null) throw new Exception("Selecione um paciente ativo!");

            List<Medico> medicos = agendamentoService.obterMedicos();
            if (medicos.isEmpty()) throw new Exception("Nenhum médico cadastrado no banco de dados.");
            Medico medicoAtendente = medicos.get(0);

            Prontuario p = new Prontuario();
            p.setPaciente(pacienteSelecionado);
            p.setMedico(medicoAtendente);
            p.setDiagnosticoMedico(txtDiagnostico.getText());
            p.setPrescricaoMedicamentos(txtPrescricao.getText());
            p.setObservacoesClinicas(txtObservacoes.getText());

            prontuarioService.salvarProntuario(p);
            exibirAlerta("Prontuário Salvo", "Evolução clínica e prescrição salvas no banco com sucesso!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            exibirAlerta("Validação Clínica", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void handleEmitirRelatorioPDF() {
        try {
            String dadosRelatorio = prontuarioService.exportarRelatorio();

            javafx.scene.layout.VBox folhaImpressao = new javafx.scene.layout.VBox();
            folhaImpressao.setPrefSize(550, 780);
            folhaImpressao.setStyle("-fx-background-color: #ffffff; -fx-padding: 30px; -fx-border-color: #FF7F32; -fx-border-width: 8px 0px 0px 0px;");

            Label lblCabecalho = new Label("CLÍNICA EXAME IV - RELATÓRIO GERAL");
            lblCabecalho.setStyle("-fx-font-family: 'Segoe UI', Helvetica, Arial; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #87CEEB; -fx-padding: 0 0 15 0;");

            TextArea textoCorpo = new TextArea(dadosRelatorio);
            textoCorpo.setEditable(false);
            textoCorpo.setWrapText(true);
            textoCorpo.setPrefHeight(680);
            textoCorpo.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px; -fx-text-fill: #2c3e50; -fx-background-color: transparent; -fx-control-inner-background: #ffffff; -fx-background-insets: 0; -fx-padding: 10;");

            folhaImpressao.getChildren().addAll(lblCabecalho, textoCorpo);

            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Escolha onde guardar o Relatório PDF");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("Documento PDF (*.pdf)", "*.pdf")
            );
            fileChooser.setInitialFileName("Relatorio_Geral_Clinica_" + java.time.LocalDate.now() + ".pdf");

            Stage stageAtual = (Stage) tabPanePrincipal.getScene().getWindow();
            java.io.File destino = fileChooser.showSaveDialog(stageAtual);

            if (destino != null) {
                javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();

                if (job != null) {
                    javafx.print.Printer printer = job.getPrinter();
                    javafx.print.PageLayout pageLayout = printer.createPageLayout(
                            javafx.print.Paper.A4,
                            javafx.print.PageOrientation.PORTRAIT,
                            javafx.print.Printer.MarginType.HARDWARE_MINIMUM
                    );

                    job.getJobSettings().setOutputFile(destino.getAbsolutePath());

                    boolean sucesso = job.printPage(pageLayout, folhaImpressao);

                    if (sucesso) {
                        job.endJob();
                        exibirAlerta("Sucesso", "O relatório em PDF foi gerado com sucesso!\nSalvo em: " + destino.getAbsolutePath(), Alert.AlertType.INFORMATION);
                    } else {
                        exibirAlerta("Erro", "Falha ao desenho dos dados nas páginas do PDF.", Alert.AlertType.ERROR);
                    }
                } else {
                    exibirAlerta("Aviso", "Não foi possível carregar os drivers de geração de PDF.", Alert.AlertType.WARNING);
                }
            }

        } catch (Exception e) {
            exibirAlerta("Erro de Compilação", "Falha crítica ao estruturar dados para o PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void exibirAlerta(String tit, String msg, Alert.AlertType t) {
        Alert a = new Alert(t); a.setTitle(tit); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}