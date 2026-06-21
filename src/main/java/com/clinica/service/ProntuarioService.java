package com.clinica.service;

import com.clinica.dao.ProntuarioDAO;
import com.clinica.model.Prontuario;
import java.sql.SQLException;

public class ProntuarioService {
    private final ProntuarioDAO prontuarioDAO = new ProntuarioDAO();

    public void salvarProntuario(Prontuario p) throws Exception {
        if (p.getPaciente() == null) throw new Exception("Escolha o paciente do atendimento!");
        if (p.getDiagnosticoMedico() == null || p.getDiagnosticoMedico().trim().isEmpty()) {
            throw new Exception("O diagnóstico clínico é obrigatório!");
        }
        prontuarioDAO.salvarOuAtualizar(p);
    }

    public Prontuario obterPorPaciente(int pacienteId) throws SQLException {
        return prontuarioDAO.buscarPorPaciente(pacienteId);
    }

    public String exportarRelatorio() throws SQLException {
        return prontuarioDAO.gerarRelatorioAtendimentosTexto();
    }
}