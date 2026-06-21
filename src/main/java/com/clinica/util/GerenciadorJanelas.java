package com.clinica.util;

import javafx.scene.Scene;
import java.net.URL;

public class GerenciadorJanelas {

    public static void aplicarEstilo(Scene scene) {
        // Altera para o estilo que quiseres como padrão (ex: modern.css ou agendamento.css)
        URL cssUrl = GerenciadorJanelas.class.getResource("/com/clinica/styles/modern.css");

        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("[ESTILO] 'modern.css' aplicado com sucesso a esta nova janela!");
        } else {
            System.out.println("[AVISO] Ficheiro de estilo não encontrado para esta janela.");
        }
    }
}