package com.clinica.model;

public class Medico {
    private int id;
    private String nome;
    private String crm;
    private String especialidade;
    private String telefone;

    public Medico() {}
    public Medico(int id, String nome) { this.id = id; this.nome = nome; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() { return this.nome; } // Exibe o nome direto no ComboBox
}