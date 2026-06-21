package com.clinica.controller;

import com.clinica.dao.AdminDAO;
import com.clinica.model.Medico;
import com.clinica.model.Usuario;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AdminController {

    @FXML private Label lblAdminLogado;

    // CONTROLES DA TAB DE MÉDICOS
    @FXML private TextField txtIdMedico;
    @FXML private TextField txtNomeMedico;
    @FXML private TextField txtCrmMedico;
    @FXML private TextField txtEspecialidade;
    @FXML private TextField txtTelefoneMedico;
    @FXML private TableView<Medico> tblMedicos;
    @FXML private TableColumn<Medico, Integer> colMedId;
    @FXML private TableColumn<Medico, String> colMedNome;
    @FXML private TableColumn<Medico, String> colMedCrm;
    @FXML private TableColumn<Medico, String> colMedEspec;
    @FXML private TableColumn<Medico, String> colMedTelefone;

    // CONTROLES DA TAB DE USUÁRIOS
    @FXML private TextField txtIdUsuario;
    @FXML private TextField txtNomeUsuario;
    @FXML private TextField txtLoginUsuario;
    @FXML private PasswordField txtSenhaUsuario; // Pode manter como PasswordField ou mudar no FXML para TextField se preferires
    @FXML private ComboBox<String> cbPerfilUsuario;
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Integer> colUsrId;
    @FXML private TableColumn<Usuario, String> colUsrNome;
    @FXML private TableColumn<Usuario, String> colUsrLogin;
    @FXML private TableColumn<Usuario, String> colUsrPerfil;

    private final AdminDAO adminDAO = new AdminDAO();
    private final ObservableList<Medico> obsMedicos = FXCollections.observableArrayList();
    private final ObservableList<Usuario> obsUsuarios = FXCollections.observableArrayList();

    // 🔐 ALGORITMO DE CRIPTOGRAFIA MD5 NATIVO
    private String criptografarMD5(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(senha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao inicializar o algoritmo MD5", e);
        }
    }

    private void embutirEstilo(Scene scene) {
        URL cssUrl = getClass().getResource("/com/clinica/styles/modern.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    @FXML
    public void initialize() {
        Usuario logado = SessaoUsuario.getUsuario();
        if (logado != null) {
            lblAdminLogado.setText("Administrador: " + logado.getNome());
        }

        cbPerfilUsuario.setItems(FXCollections.observableArrayList("Administrador", "Médico", "Recepcionista"));
        cbPerfilUsuario.setValue("Recepcionista");

        configurarTabelas();
        atualizarTabelaMedicos();
        atualizarTabelaUsuarios();
    }

    private void configurarTabelas() {
        colMedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMedNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colMedCrm.setCellValueFactory(new PropertyValueFactory<>("crm"));
        colMedEspec.setCellValueFactory(new PropertyValueFactory<>("especialidade"));
        colMedTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        colUsrId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsrNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colUsrLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colUsrPerfil.setCellValueFactory(new PropertyValueFactory<>("perfil"));
    }

    // ==========================================
    // AÇÕES DO MÓDULO DE MÉDICOS
    // ==========================================

    private void atualizarTabelaMedicos() {
        try {
            obsMedicos.clear();
            obsMedicos.addAll(adminDAO.listarMedicos());
            tblMedicos.setItems(obsMedicos);
        } catch (Exception e) {
            exibirAlerta("Erro", "Erro ao listar médicos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleSalvarMedico() {
        try {
            if (txtNomeMedico.getText().isEmpty() || txtCrmMedico.getText().isEmpty() || txtEspecialidade.getText().isEmpty()) {
                throw new Exception("Por favor, preencha todos os campos obrigatórios (*)");
            }
            int id = (txtIdMedico.getText() == null || txtIdMedico.getText().isEmpty()) ? 0 : Integer.parseInt(txtIdMedico.getText());

            Medico m = new Medico(id, txtNomeMedico.getText());
            m.setCrm(txtCrmMedico.getText());
            m.setEspecialidade(txtEspecialidade.getText());
            m.setTelefone(txtTelefoneMedico.getText());

            adminDAO.salvarMedico(m);
            exibirAlerta("Sucesso", "Dados do médico gravados!", Alert.AlertType.INFORMATION);
            handleLimparCamposMedico();
            atualizarTabelaMedicos();
        } catch (Exception e) {
            exibirAlerta("Aviso de Validação", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void handleCarregarFormularioMedico() {
        Medico sel = tblMedicos.getSelectionModel().getSelectedItem();
        if (sel != null) {
            txtIdMedico.setText(String.valueOf(sel.getId()));
            txtNomeMedico.setText(sel.getNome());
            txtCrmMedico.setText(sel.getCrm());
            txtEspecialidade.setText(sel.getEspecialidade());
            txtTelefoneMedico.setText(sel.getTelefone());
        }
    }

    @FXML
    public void handleExcluirMedico() {
        try {
            if (txtIdMedico.getText().isEmpty()) throw new Exception("Selecione um médico na tabela para excluir.");
            int id = Integer.parseInt(txtIdMedico.getText());
            adminDAO.excluirMedico(id);
            exibirAlerta("Sucesso", "Médico removido com sucesso.", Alert.AlertType.INFORMATION);
            handleLimparCamposMedico();
            atualizarTabelaMedicos();
        } catch (Exception e) {
            exibirAlerta("Erro de Remoção", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleLimparCamposMedico() {
        txtIdMedico.clear();
        txtNomeMedico.clear();
        txtCrmMedico.clear();
        txtEspecialidade.clear();
        txtTelefoneMedico.clear();
        tblMedicos.getSelectionModel().clearSelection();
    }

    // ==========================================
    // AÇÕES DO MÓDULO DE USUÁRIOS
    // ==========================================

    private void atualizarTabelaUsuarios() {
        try {
            obsUsuarios.clear();
            obsUsuarios.addAll(adminDAO.listarUsuarios());
            tblUsuarios.setItems(obsUsuarios);
        } catch (Exception e) {
            exibirAlerta("Erro", "Erro ao listar usuários: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleSalvarUsuario() {
        try {
            if (txtNomeUsuario.getText().isEmpty() || txtLoginUsuario.getText().isEmpty()) {
                throw new Exception("Por favor, preencha o Nome e o Login do usuário (*)");
            }

            int id = (txtIdUsuario.getText() == null || txtIdUsuario.getText().isEmpty()) ? 0 : Integer.parseInt(txtIdUsuario.getText());

            Usuario u = new Usuario();
            u.setId(id);
            u.setNome(txtNomeUsuario.getText());
            u.setLogin(txtLoginUsuario.getText());
            u.setPerfil(cbPerfilUsuario.getValue());

            if (id == 0) {
                // 🔐 REGRA PARA NOVO USUÁRIO: Senha obrigatória e encriptada em MD5
                if (txtSenhaUsuario.getText().isEmpty()) {
                    throw new Exception("A senha é obrigatória para a criação de novos usuários!");
                }
                String senhaCriptografada = criptografarMD5(txtSenhaUsuario.getText());
                u.setSenha(senhaCriptografada);
            } else {
                // 🛑 REGRA PARA EDIÇÃO CONFIGURADA:
                Usuario atual = tblUsuarios.getSelectionModel().getSelectedItem();
                Usuario logado = SessaoUsuario.getUsuario();

                // Verifica se o Admin está a editar a si próprio e se digitou uma nova senha válida
                if (logado != null && id == logado.getId() && !txtSenhaUsuario.getText().isEmpty() && !txtSenhaUsuario.getText().equals("********")) {
                    // O Admin alterou a sua própria senha -> Encripta a nova senha em MD5
                    u.setSenha(criptografarMD5(txtSenhaUsuario.getText()));
                } else if (atual != null) {
                    // É outro utilizador (ou o admin não mexeu no campo) -> Resgata e preserva a senha antiga do banco
                    u.setSenha(atual.getSenha());
                }
            }

            adminDAO.salvarUsuario(u);
            exibirAlerta("Sucesso", "Usuário salvo no sistema com segurança!", Alert.AlertType.INFORMATION);
            handleLimparCamposUsuario();
            atualizarTabelaUsuarios();
        } catch (Exception e) {
            exibirAlerta("Aviso de Validação", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void handleCarregarFormularioUsuario() {
        Usuario sel = tblUsuarios.getSelectionModel().getSelectedItem();
        if (sel != null) {
            txtIdUsuario.setText(String.valueOf(sel.getId()));
            txtNomeUsuario.setText(sel.getNome());
            txtLoginUsuario.setText(sel.getLogin());
            cbPerfilUsuario.setValue(sel.getPerfil());

            Usuario logado = SessaoUsuario.getUsuario();

            // 🔐 REGRA DE NEGÓCIO ATUALIZADA:
            if (logado != null && sel.getId() == logado.getId()) {
                // Se o Admin selecionou a si mesmo, ele PODE editar a senha!
                txtSenhaUsuario.setDisable(false);
                txtSenhaUsuario.clear(); // Limpa para ele digitar a nova senha
                txtSenhaUsuario.setPromptText("Digite a nova senha");
            } else {
                // Se for OUTRO utilizador, bloqueia totalmente!
                txtSenhaUsuario.setText("********");
                txtSenhaUsuario.setDisable(true);
            }
        }
    }

    @FXML
    public void handleExcluirUsuario() {
        try {
            if (txtIdUsuario.getText().isEmpty()) throw new Exception("Selecione um usuário para excluir.");
            int id = Integer.parseInt(txtIdUsuario.getText());

            // 🛡️ Impedir que o admin se auto-exclua
            Usuario logado = SessaoUsuario.getUsuario();
            if (logado != null && id == logado.getId()) {
                throw new Exception("Segurança do Sistema: Você não pode revogar ou excluir o seu próprio acesso administrativo em execução.");
            }

            adminDAO.excluirUsuario(id);
            exibirAlerta("Sucesso", "Acesso do usuário revogado definitivamente.", Alert.AlertType.INFORMATION);
            handleLimparCamposUsuario();
            atualizarTabelaUsuarios();
        } catch (Exception e) {
            exibirAlerta("Erro de Remoção", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleLimparCamposUsuario() {
        txtIdUsuario.clear();
        txtNomeUsuario.clear();
        txtLoginUsuario.clear();
        txtSenhaUsuario.clear();
        txtSenhaUsuario.setDisable(false); // Reativa o campo para novas inserções
        cbPerfilUsuario.setValue("Recepcionista");
        tblUsuarios.getSelectionModel().clearSelection();
    }

    // ==========================================
    // NAVEGAÇÃO GERAL CORRIGIDA
    // ==========================================

    @FXML
    public void handleIrParaSistema() throws IOException {
        Stage stage = (Stage) tblMedicos.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/fxml/MainView.fxml"));
        Scene scene = new Scene(loader.load());

        embutirEstilo(scene);

        stage.setScene(scene);
        stage.setTitle("Clínica - Módulo Operacional");
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
            Stage stage = (Stage) lblAdminLogado.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/fxml/LoginView.fxml"));
            Scene scene = new Scene(loader.load());

            embutirEstilo(scene);

            stage.setScene(scene);
            stage.setTitle("Clínica - Autenticação");
            stage.centerOnScreen();
        }
    }

    private void exibirAlerta(String tit, String msg, Alert.AlertType t) {
        Alert a = new Alert(t); a.setTitle(tit); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}