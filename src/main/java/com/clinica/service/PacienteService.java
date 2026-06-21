package com.clinica.service;

import com.clinica.dao.PacienteDAO;
import com.clinica.model.Paciente;
import java.util.List;

public class PacienteService {
    private final PacienteDAO pacienteDAO = new PacienteDAO();

    public void salvar(Paciente p) throws Exception {
        if (p.getNome() == null || p.getNome().trim().isEmpty()) {
            throw new Exception("O nome do paciente é obrigatório!");
        }
        if (p.getCpf() == null || p.getCpf().trim().length() < 11) {
            throw new Exception("Insira um CPF válido!");
        }
        if (p.getDataNascimento() == null) {
            throw new Exception("A data de nascimento é obrigatória!");
        }

        if (p.getId() == 0) {
            pacienteDAO.inserir(p);
        } else {
            pacienteDAO.atualizar(p);
        }
    }

    public void inativar(int id) throws Exception {
        if (id <= 0) throw new Exception("ID inválido para inativação.");
        pacienteDAO.inativar(id);
    }

    public List<Paciente> obterAtivos() throws Exception {
        return pacienteDAO.listarAtivos();
    }

    // 🟢 NOVO MÉTODO: Faz a ponte com o DAO para buscar pacientes com consultas realizadas
    public List<Paciente> obterJaConsultados() throws Exception {
        return pacienteDAO.listarJáConsultados();
    }
}