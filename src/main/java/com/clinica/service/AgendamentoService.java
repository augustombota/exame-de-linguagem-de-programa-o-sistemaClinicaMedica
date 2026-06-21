package com.clinica.service;

import com.clinica.dao.AgendamentoDAO;
import com.clinica.model.Agendamento;
import com.clinica.model.Medico;
import java.util.List;

public class AgendamentoService {
    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();

    public void agendar(Agendamento ag) throws Exception {
        if (ag.getPaciente() == null) throw new Exception("Selecione um paciente!");
        if (ag.getMedico() == null) throw new Exception("Selecione um médico!");
        if (ag.getDataConsulta() == null) throw new Exception("Defina a data da consulta!");
        if (ag.getHorarioConsulta() == null) throw new Exception("Defina o horário da consulta!");

        if (ag.getId() == 0) {
            agendamentoDAO.inserir(ag);
        } else {
            agendamentoDAO.atualizar(ag);
        }
    }

    public List<Agendamento> obterTodos() throws Exception {
        return agendamentoDAO.listarTodos();
    }

    public List<Medico> obterMedicos() throws Exception {
        return agendamentoDAO.listarMedicos();
    }
}