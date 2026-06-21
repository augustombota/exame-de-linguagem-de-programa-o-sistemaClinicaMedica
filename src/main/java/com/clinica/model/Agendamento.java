package com.clinica.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Agendamento {
    private int id;
    private Paciente paciente;
    private Medico medico;
    private LocalDate dataConsulta;
    private LocalTime horarioConsulta;
    private String statusConsulta;

    // Propriedades auxiliares para as colunas da TableView lerem texto direto
    private String nomePaciente;
    private String nomeMedico;

    public Agendamento() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if(paciente != null) this.nomePaciente = paciente.getNome();
    }
    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) {
        this.medico = medico;
        if(medico != null) this.nomeMedico = medico.getNome();
    }
    public LocalDate getDataConsulta() { return dataConsulta; }
    public void setDataConsulta(LocalDate dataConsulta) { this.dataConsulta = dataConsulta; }
    public LocalTime getHorarioConsulta() { return horarioConsulta; }
    public void setHorarioConsulta(LocalTime horarioConsulta) { this.horarioConsulta = horarioConsulta; }
    public String getStatusConsulta() { return statusConsulta; }
    public void setStatusConsulta(String statusConsulta) { this.statusConsulta = statusConsulta; }

    public String getNomePaciente() { return nomePaciente; }
    public String getNomeMedico() { return nomeMedico; }
}