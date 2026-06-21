package com.clinica.dao;

import com.clinica.model.Agendamento;
import com.clinica.model.Medico;
import com.clinica.model.Paciente;
import com.clinica.util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {

    public void inserir(Agendamento ag) throws SQLException {
        String sql = "INSERT INTO agendamentos (paciente_id, medico_id, data_consulta, horario_consulta, status_consulta) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ag.getPaciente().getId());
            stmt.setInt(2, ag.getMedico().getId());
            stmt.setDate(3, Date.valueOf(ag.getDataConsulta()));
            stmt.setTime(4, Time.valueOf(ag.getHorarioConsulta()));
            stmt.setString(5, ag.getStatusConsulta());
            stmt.executeUpdate();
        }
    }

    public void atualizar(Agendamento ag) throws SQLException {
        String sql = "UPDATE agendamentos SET paciente_id = ?, medico_id = ?, data_consulta = ?, horario_consulta = ?, status_consulta = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ag.getPaciente().getId());
            stmt.setInt(2, ag.getMedico().getId());
            stmt.setDate(3, Date.valueOf(ag.getDataConsulta()));
            stmt.setTime(4, Time.valueOf(ag.getHorarioConsulta()));
            stmt.setString(5, ag.getStatusConsulta());
            stmt.setInt(6, ag.getId());
            stmt.executeUpdate();
        }
    }

    public List<Agendamento> listarTodos() throws SQLException {
        List<Agendamento> lista = new ArrayList<>();
        String sql = "SELECT a.*, p.nome AS p_nome, m.nome AS m_nome " +
                "FROM agendamentos a " +
                "INNER JOIN pacientes p ON a.paciente_id = p.id " +
                "INNER JOIN medicos m ON a.medico_id = m.id " +
                "ORDER BY a.data_consulta DESC, a.horario_consulta DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Agendamento ag = new Agendamento();
                ag.setId(rs.getInt("id"));

                // Mapeamento correto da data sem linhas duplicadas
                ag.setDataConsulta(rs.getDate("data_consulta").toLocalDate());
                ag.setHorarioConsulta(rs.getTime("horario_consulta").toLocalTime());
                ag.setStatusConsulta(rs.getString("status_consulta"));

                Paciente p = new Paciente();
                p.setId(rs.getInt("paciente_id"));
                p.setNome(rs.getString("p_nome"));
                ag.setPaciente(p);

                Medico m = new Medico();
                m.setId(rs.getInt("medico_id"));
                m.setNome(rs.getString("m_nome"));
                ag.setMedico(m);

                lista.add(ag);
            }
        }
        return lista;
    }

    public List<Medico> listarMedicos() throws SQLException {
        List<Medico> medicos = new ArrayList<>();
        String sql = "SELECT id, nome FROM medicos ORDER BY nome ASC";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                medicos.add(new Medico(rs.getInt("id"), rs.getString("nome")));
            }
        }
        return medicos;
    }
}