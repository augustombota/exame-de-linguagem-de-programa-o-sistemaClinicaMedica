Sistema de Gestão de Clínica Médica — Clínica UNIKIVI
Este é um sistema desktop End-to-End para automação de processos operacionais, clínicos e administrativos de uma clínica médica. O projeto foi desenvolvido como elemento de consolidação prática para a cadeira de Linguagem de Programação do 2º Ano do curso de Engenharia Informática da Universidade Kimpa Vita (UGE / Instituto Politécnico do Uíge).

Tecnologias Utilizadas
Linguagem: Java 17
Interface Gráfica (GUI): JavaFX (estruturado em FXML com estilização via CSS)
Base de Dados: SGBD MySQL 8.0+
Conectividade: API JDBC nativa (Driver mysql-connector-j)
Gestão de Dependências: Apache Maven
Arquitetura do Software
A aplicação adota o padrão de arquitetura em camadas para isolar responsabilidades e facilitar a manutenção do código fonte:

 Camada de Visão (UI)     ➔  [ FXML / JavaFX Views ]
                                      ↓
 Camada de Controlo       ➔  [ Controllers (Manipulação de Eventos) ]
                                      ↓
 Camada de Negócio        ➔  [ Services (Validações e Lógica) ]
                                      ↓
 Camada de Persistência   ➔  [ DAO - Data Access Object (Acesso ao MySQL) ]
                                      ↓
 Camada de Dados          ➔  [ Banco de Dados MySQL ]
