package com.clinica.service;

import com.clinica.dao.UsuarioDAO;
import com.clinica.model.Usuario;
import java.sql.SQLException;

public class AutenticacaoService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String login, String senha) throws Exception {
        if (login == null || login.trim().isEmpty()) {
            throw new Exception("O campo Utilizador é obrigatório!");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new Exception("O campo Palavra-passe é obrigatório!");
        }

        try {
            return usuarioDAO.buscarPorLoginESenha(login, senha);
        } catch (SQLException e) {
            throw new Exception("Erro de infraestrutura ao aceder à base de dados.", e);
        }
    }
}