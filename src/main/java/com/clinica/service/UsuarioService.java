package com.clinica.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UsuarioService {

    // Métoodo para encriptar a password em MD5
    public String criptografarMD5(String senhaForte) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(senhaForte.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao inicializar o algoritmo MD5", e);
        }
    }

    // No teu métdo de salvar ou registar usuário, faz isto antes de mandar para o DAO:
    // usuario.setSenha(criptografarMD5(usuario.getSenha()));
}