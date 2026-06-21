package com.clinica.dao;

import com.clinica.model.Medico;
import com.clinica.model.Usuario;
import com.clinica.util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // ==========================================
    // OPERAÇÕES DE MÉDICOS
    // ==========================================

    public List<Medico> listarMedicos() throws SQLException {
        List<Medico> medicos = new ArrayList<>();
        String sql = "SELECT * FROM medicos ORDER BY id DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Medico m = new Medico(rs.getInt("id"), rs.getString("nome"));
                m.setCrm(rs.getString("crm"));
                m.setEspecialidade(rs.getString("especialidade"));
                m.setTelefone(rs.getString("telefone"));
                medicos.add(m);
            }
        }
        return medicos;
    }

    public void salvarMedico(Medico m) throws SQLException {
        if (m.getId() == 0) {
            String sql = "INSERT INTO medicos (nome, crm, especialidade, telefone) VALUES (?, ?, ?, ?)";
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, m.getNome());
                stmt.setString(2, m.getCrm());
                stmt.setString(3, m.getEspecialidade());
                stmt.setString(4, m.getTelefone());
                stmt.executeUpdate();
            }
        } else {
            String sql = "UPDATE medicos SET nome = ?, crm = ?, especialidade = ?, telefone = ? WHERE id = ?";
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, m.getNome());
                stmt.setString(2, m.getCrm());
                stmt.setString(3, m.getEspecialidade());
                stmt.setString(4, m.getTelefone());
                stmt.setInt(5, m.getId());
                stmt.executeUpdate();
            }
        }
    }

    public void excluirMedico(int id) throws SQLException {
        String sql = "DELETE FROM medicos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // ==========================================
    // OPERAÇÕES DE USUÁRIOS (Credenciais)
    // ==========================================

    public List<Usuario> listarUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                u.setSenha(rs.getString("senha"));
                u.setPerfil(rs.getString("perfil"));
                usuarios.add(u);
            }
        }
        return usuarios;
    }

    public void salvarUsuario(Usuario u) throws SQLException {
        if (u.getId() == 0) {
            String sql = "INSERT INTO usuarios (nome, login, senha, perfil) VALUES (?, ?, ?, ?)";
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, u.getNome());
                stmt.setString(2, u.getLogin());
                stmt.setString(3, u.getSenha());
                stmt.setString(4, u.getPerfil());
                stmt.executeUpdate();
            }
        } else {
            String sql = "UPDATE usuarios SET nome = ?, login = ?, senha = ?, perfil = ? WHERE id = ?";
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, u.getNome());
                stmt.setString(2, u.getLogin());
                stmt.setString(3, u.getSenha());
                stmt.setString(4, u.getPerfil());
                stmt.setInt(5, u.getId());
                stmt.executeUpdate();
            }
        }
    }

    public void excluirUsuario(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}