module com.clinica {
    // Exigido para rodar a interface gráfica
    requires javafx.controls;
    requires javafx.fxml;

    // Exigido para a conexão com o banco de dados e drivers do MySQL
    requires java.sql;
    requires mysql.connector.j;

    // Abre as pastas para o JavaFX conseguir ler os Controllers e Models
    opens com.clinica to javafx.fxml;
    opens com.clinica.controller to javafx.fxml;
    opens com.clinica.model to javafx.base; // Permite ao JavaFX ler dados nas tabelas gráficas

    // Exporta o pacote principal para execução
    exports com.clinica;
    exports com.clinica.controller;
}