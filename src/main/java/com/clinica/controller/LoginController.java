package com.clinica.controller;

import com.clinica.model.Usuario;
import com.clinica.service.AutenticacaoService;
import com.clinica.util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginController {

    @FXML private TextField txtLogin;
    @FXML private PasswordField txtSenha;

    private final AutenticacaoService autenticacaoService = new AutenticacaoService();

    // 🔐 MÉTODO AUXILIAR PARA GERAR O HASH MD5
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
            throw new RuntimeException("Erro ao inicializar o algoritmo MD5 no Login", e);
        }
    }

    @FXML
    public void handleLogin() {
        String login = txtLogin.getText();
        String senhaDigitada = txtSenha.getText();

        try {
            if (login.isEmpty() || senhaDigitada.isEmpty()) {
                exibirAlerta("Aviso", "Por favor, preencha todos os campos!", Alert.AlertType.WARNING);
                return;
            }

            // 🔐 CONVERSÃO MANDATÓRIA: Encripta o que o utilizador digitou antes de validar
            String senhaCriptografada = criptografarMD5(senhaDigitada);

            // Envia a senha já em formato MD5 para o Service
            Usuario usuario = autenticacaoService.autenticar(login, senhaCriptografada);

            if (usuario != null) {
                SessaoUsuario.iniciarSessao(usuario);

                // 🟢 PASSO 2: Mensagem de Login feito com sucesso!
                exibirAlerta("Sucesso", "Login feito com sucesso! Bem-vindo, " + usuario.getNome() + ".", Alert.AlertType.INFORMATION);

                Stage stage = (Stage) txtLogin.getScene().getWindow();
                FXMLLoader loader;

                // Verifica o perfil vindo do Banco de Dados
                if ("Administrador".equalsIgnoreCase(usuario.getPerfil())) {
                    loader = new FXMLLoader(getClass().getResource("/com/clinica/fxml/AdminView.fxml"));
                    stage.setTitle("Clínica UNIKIVI - Painel Administrativo");
                } else {
                    loader = new FXMLLoader(getClass().getResource("/com/clinica/fxml/MainView.fxml"));
                    stage.setTitle("Clínica UNIKIVI - Menu Principal");
                }

                Scene scene = new Scene(loader.load());

                // 🟢 INJEÇÃO SEGURA DE ESTILO NA NOVA JANELA (Evita que o CSS desapareça)
                System.out.println("[INFO] A aplicar estilo na nova janela (" + usuario.getPerfil() + ")...");
                URL cssUrl = getClass().getResource("/com/clinica/styles/modern.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("[SUCESSO] Estilo modern.css aplicado à nova janela!");
                } else {
                    System.out.println("[AVISO] modern.css não encontrado para esta janela. A carregar layout padrão.");
                }

                stage.setScene(scene);
                stage.centerOnScreen();
            } else {
                exibirAlerta("Acesso Recusado", "Utilizador ou palavra-passe incorretos.", Alert.AlertType.WARNING);
            }

        } catch (Exception e) {
            exibirAlerta("Erro de Autenticação", "Não foi possível carregar o ecrã: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}