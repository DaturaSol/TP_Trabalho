package trabalho.candidatura.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.exceptions.DuplicateDataException;
import trabalho.financeiro.utils.CpfManager;

public class Candidato {
    private String formacao;
    private String experiencia;
    private double pretensaoSalarial;
    private String disponibilidadeHorario;
    private String documentosAdicionais;
    private Date dataCadastro;
    // Critical Change instead of inheriting from Pessoa, we will use composition.
    // We will point to cpfCnpj here, since when we serialize to JSON our original
    // reference to Pessoa would be lost.
    private String cpfCnpj;

    public Candidato() {
        super();
    }

    public Candidato(
            String cpfCnpj,
            String formacao,
            String experiencia,
            double pretensaoSalarial,
            String disponibilidadeHorario,
            String documentosAdicionais,
            Date dataCadastro) {
        this.formacao = formacao;
        this.experiencia = experiencia;
        this.pretensaoSalarial = pretensaoSalarial;
        this.disponibilidadeHorario = disponibilidadeHorario;
        this.documentosAdicionais = documentosAdicionais;
        this.dataCadastro = dataCadastro;
    }

    public Pessoa getPessoa() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        Pessoa pessoa = appData.getPessoas().get(cpfCnpj);
        return pessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public double getPretensaoSalarial() {
        return pretensaoSalarial;
    }

    public void setPretensaoSalarial(double pretensaoSalarial) {
        this.pretensaoSalarial = pretensaoSalarial;
    }

    public String getDisponibilidadeHorario() {
        return disponibilidadeHorario;
    }

    public void setDisponibilidadeHorario(String disponibilidadeHorario) {
        this.disponibilidadeHorario = disponibilidadeHorario;
    }

    public String getDocumentosAdicionais() {
        return documentosAdicionais;
    }

    public void setDocumentosAdicionais(String documentosAdicionais) {
        this.documentosAdicionais = documentosAdicionais;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }


    // Modified for AppData integration
    public static boolean cadastrarCandidato(Candidato c) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        if (!CpfManager.validarCPF(c.getCpfCnpj())) {
            return false;
        }

        try {
            appData.addCandidato(c);
            dataManager.saveData();
            return true;
        } catch (DuplicateDataException e) {
            return false;
        }
    }

    public static boolean editarCandidato(String cpf, Candidato novosDados) {
        try {
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();

            Map<String, Candidato> candidatosByCpf = appData.getCandidatos();

            appData.removeCandidato(candidatosByCpf.get(cpf));
            appData.addCandidato(novosDados); // This will overwrite existing data
            dataManager.saveData();

            return true;
        } catch (DuplicateDataException e) {
            return false;
        }
    }

    public static boolean excluirCandidato(String cpf) {
        try {
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();
            Map<String, Candidato> candidatosByCpf = appData.getCandidatos();

            appData.removeCandidato(candidatosByCpf.get(cpf));
            dataManager.saveData();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void listarCandidatos() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        Map<String, Candidato> candidatosByCpf = appData.getCandidatos();
        List<Candidato> candidatos = candidatosByCpf.values().stream().toList();

        if (candidatos.isEmpty()) {
            return;
        }
        for (Candidato c : candidatos) {
            System.out.println(c);
        }
    }

}
