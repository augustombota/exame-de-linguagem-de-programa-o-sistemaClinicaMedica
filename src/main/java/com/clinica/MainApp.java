package com.clinica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.clinica.util.GerenciadorJanelas; // Importa a classe utilitária
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL fxmlUrl = getClass().getResource("/com/clinica/fxml/LoginView.fxml");
        if (fxmlUrl == null) {
            throw new java.lang.IllegalStateException("O ficheiro LoginView.fxml nao foi encontrado!");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load());

        // 🟢 Aplica o estilo na primeira abertura
        GerenciadorJanelas.aplicarEstilo(scene);

        stage.setScene(scene);
        stage.setTitle("Clínica - Autenticação");
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}