package com.clinica.model;

import java.time.LocalDateTime;

public class Prontuario {
    private int id;
    private Paciente paciente;
    private Medico medico;
    private LocalDateTime dataRegistro;
    private String diagnosticoMedico;
    private String prescricaoMedicamentos;
    private String observacoesClinicas;

    public Prontuario() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }
    public String getDiagnosticoMedico() { return diagnosticoMedico; }
    public void setDiagnosticoMedico(String diagnosticoMedico) { this.diagnosticoMedico = diagnosticoMedico; }
    public String getPrescricaoMedicamentos() { return prescricaoMedicamentos; }
    public void setPrescricaoMedicamentos(String prescricaoMedicamentos) { this.prescricaoMedicamentos = prescricaoMedicamentos; }
    public String getObservacoesClinicas() { return observacoesClinicas; }
    public void setObservacoesClinicas(String observacoesClinicas) { this.observacoesClinicas = observacoesClinicas; }
}
