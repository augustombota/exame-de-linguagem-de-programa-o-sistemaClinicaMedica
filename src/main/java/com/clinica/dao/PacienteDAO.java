package com.clinica.dao;

import com.clinica.model.Paciente;
import com.clinica.util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public void inserir(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO pacientes (nome, cpf, data_nascimento, endereco, telefone, historico_preliminar, ativo) VALUES (?, ?, ?, ?, ?, ?, 1)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf());
            stmt.setDate(3, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(4, paciente.getEndereco());
            stmt.setString(5, paciente.getTelefone());
            stmt.setString(6, paciente.getHistoricoPreliminar());
            stmt.executeUpdate();
        }
    }

    public void atualizar(Paciente paciente) throws SQLException {
        String sql = "UPDATE pacientes SET nome = ?, cpf = ?, data_nascimento = ?, endereco = ?, telefone = ?, historico_preliminar = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf());
            stmt.setDate(3, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(4, paciente.getEndereco());
            stmt.setString(5, paciente.getTelefone());
            stmt.setString(6, paciente.getHistoricoPreliminar());
            stmt.setInt(7, paciente.getId());
            stmt.executeUpdate();
        }
    }

    public void inativar(int id) throws SQLException {
        String sql = "UPDATE pacientes SET ativo = 0 WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Paciente> listarAtivos() throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE ativo = 1 ORDER BY nome ASC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Paciente p = new Paciente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getDate("data_nascimento").toLocalDate(),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("historico_preliminar"),
                        rs.getBoolean("ativo")
                );
                pacientes.add(p);
            }
        }
        return pacientes;
    }

    public List<Paciente> listarJáConsultados() throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        // Query com JOIN que seleciona pacientes com pelo menos uma consulta 'Realizada'
        String sql = "SELECT DISTINCT p.* FROM pacientes p " +
                "INNER JOIN agendamentos a ON p.id = a.paciente_id " +
                "WHERE a.status_consulta = 'Realizada' " +
                "ORDER BY p.nome ASC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Paciente p = new Paciente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getDate("data_nascimento").toLocalDate(),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("historico_preliminar"),
                        rs.getBoolean("ativo")
                );
                pacientes.add(p);
            }
        }
        return pacientes;
    }
}