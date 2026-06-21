package com.clinica.dao;

import com.clinica.model.Prontuario;
import com.clinica.model.Paciente;
import com.clinica.util.ConnectionFactory;
import java.sql.*;

public class ProntuarioDAO {

    public void salvarOuAtualizar(Prontuario pront) throws SQLException {
        // Verifica se o paciente já tem prontuário clínico aberto
        String checkSql = "SELECT id FROM prontuarios WHERE paciente_id = ?";
        String insertSql = "INSERT INTO prontuarios (paciente_id, medico_id, diagnostico_medico, prescricao_medicamentos, observacoes_clinicas) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE prontuarios SET medico_id = ?, diagnostico_medico = ?, prescricao_medicamentos = ?, observacoes_clinicas = ?, data_registo = CURRENT_TIMESTAMP WHERE paciente_id = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            boolean existe = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, pront.getPaciente().getId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) existe = true;
                }
            }

            if (existe) {
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, pront.getMedico().getId());
                    stmt.setString(2, pront.getDiagnosticoMedico());
                    stmt.setString(3, pront.getPrescricaoMedicamentos());
                    stmt.setString(4, pront.getObservacoesClinicas());
                    stmt.setInt(5, pront.getPaciente().getId());
                    stmt.executeUpdate();
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setInt(1, pront.getPaciente().getId());
                    stmt.setInt(2, pront.getMedico().getId());
                    stmt.setString(3, pront.getDiagnosticoMedico());
                    stmt.setString(4, pront.getPrescricaoMedicamentos());
                    stmt.setString(5, pront.getObservacoesClinicas());
                    stmt.executeUpdate();
                }
            }
        }
    }

    public Prontuario buscarPorPaciente(int pacienteId) throws SQLException {
        String sql = "SELECT * FROM prontuarios WHERE paciente_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pacienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Prontuario p = new Prontuario();
                    p.setId(rs.getInt("id"));
                    p.setDiagnosticoMedico(rs.getString("diagnostico_medico"));
                    p.setPrescricaoMedicamentos(rs.getString("prescricao_medicamentos"));
                    p.setObservacoesClinicas(rs.getString("observacoes_clinicas"));
                    return p;
                }
            }
        }
        return null; // Caso não tenha registro anterior
    }

    // PASSO 12: Geração de Relatório Consolidado em formato de Texto Limpo
    public String gerarRelatorioAtendimentosTexto() throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("          RELATÓRIO EMITIDO - SISTEMA CLÍNICA          \n");
        sb.append("\n");

        String sql = "SELECT p.nome AS paciente, m.nome AS medico, pr.data_registo, pr.diagnostico_medico " +
                "FROM prontuarios pr " +
                "INNER JOIN pacientes p ON pr.paciente_id = p.id " +
                "INNER JOIN medicos m ON pr.medico_id = m.id " +
                "ORDER BY pr.data_registo DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int total = 0;
            while(rs.next()) {
                total++;
                sb.append("Data/Hora: ").append(rs.getTimestamp("data_registo")).append("\n");
                sb.append("Paciente:  ").append(rs.getString("paciente")).append("\n");
                sb.append("Médico:    ").append(rs.getString("medico")).append("\n");
                sb.append("Diagnóstico: ").append(rs.getString("diagnostico_medico")).append("\n");
                sb.append("\n");
            }
            sb.append("\nTotal de Prontuários Registados no Sistema: ").append(total).append("\n");
        }
        return sb.toString();
    }
}