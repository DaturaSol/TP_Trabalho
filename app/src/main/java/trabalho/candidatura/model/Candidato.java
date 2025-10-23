package trabalho.candidatura.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Candidato extends Pessoa {
    private String formacao;
    private String experiencia;
    private double pretensaoSalarial;
    private String disponibilidadeHorario;
    private String documentosAdicionais;
    private Date dataCadastro;

    private static final List<Candidato> listaCandidatos = new ArrayList<>();

    public Candidato() {
        super();
    }

    public Candidato(
            String nome,
            String cpfCnpj,
            String email,
            String endereco,
            long telefone,
            String formacao,
            String experiencia,
            double pretensaoSalarial,
            String disponibilidadeHorario,
            String documentosAdicionais,
            Date dataCadastro) {
        super(nome, cpfCnpj, email, endereco, telefone);
        this.formacao = formacao;
        this.experiencia = experiencia;
        this.pretensaoSalarial = pretensaoSalarial;
        this.disponibilidadeHorario = disponibilidadeHorario;
        this.documentosAdicionais = documentosAdicionais;
        this.dataCadastro = dataCadastro;
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

    public static boolean validarCPF(String cpf) {
        if (cpf == null || cpf.isEmpty()) return false;

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) return false;

        int num1 = Character.getNumericValue(cpf.charAt(0));
        int num2 = Character.getNumericValue(cpf.charAt(1));
        int num3 = Character.getNumericValue(cpf.charAt(2));
        int num4 = Character.getNumericValue(cpf.charAt(3));
        int num5 = Character.getNumericValue(cpf.charAt(4));
        int num6 = Character.getNumericValue(cpf.charAt(5));
        int num7 = Character.getNumericValue(cpf.charAt(6));
        int num8 = Character.getNumericValue(cpf.charAt(7));
        int num9 = Character.getNumericValue(cpf.charAt(8));
        int num10 = Character.getNumericValue(cpf.charAt(9));
        int num11 = Character.getNumericValue(cpf.charAt(10));

        if (num1 == num2 && num2 == num3 && num3 == num4 && num4 == num5 &&
                num5 == num6 && num6 == num7 && num7 == num8 && num8 == num9 &&
                num9 == num10 && num10 == num11) {
            return false;
        }

        int soma1 = num1 * 10 + num2 * 9 + num3 * 8 + num4 * 7 + num5 * 6 + num6 * 5 + num7 * 4 + num8 * 3 + num9 * 2;
        int resto1 = (soma1 * 10) % 11;
        if (resto1 == 10) resto1 = 0;

        int soma2 = num1 * 11 + num2 * 10 + num3 * 9 + num4 * 8 + num5 * 7 + num6 * 6 + num7 * 5 + num8 * 4 + num9 * 3 + num10 * 2;
        int resto2 = (soma2 * 10) % 11;
        if (resto2 == 10) resto2 = 0;

        return (resto1 == num10) && (resto2 == num11);
    }

    public static boolean cadastrarCandidato(Candidato c) {
        if (!validarCPF(c.getCpfCnpj())) {
            return false;
        }
        for (Candidato existente : listaCandidatos) {
            if (existente.getCpfCnpj().equals(c.getCpfCnpj())) {
                return false;
            }
        }
        listaCandidatos.add(c);
        return true;
    }

    public static boolean editarCandidato(String cpf, Candidato novosDados) {
        for (int i = 0; i < listaCandidatos.size(); i++) {
            if (listaCandidatos.get(i).getCpfCnpj().equals(cpf)) {
                listaCandidatos.set(i, novosDados);
                return true;
            }
        }
        return false;
    }

    public static boolean excluirCandidato(String cpf) {
        for (Candidato c : listaCandidatos) {
            if (c.getCpfCnpj().equals(cpf)) {
                listaCandidatos.remove(c);
                return true;
            }
        }
        return false;
    }

    public static void listarCandidatos() {
        if (listaCandidatos.isEmpty()) {
            return;
        }
        for (Candidato c : listaCandidatos) {
            System.out.println(c);
        }
    }

    @Override
    public String toString() {
        return "Candidato{" +
                "formacao='" + formacao + '\'' +
                ", experiencia='" + experiencia + '\'' +
                ", pretensaoSalarial=" + pretensaoSalarial +
                ", disponibilidadeHorario='" + disponibilidadeHorario + '\'' +
                ", documentosAdicionais='" + documentosAdicionais + '\'' +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}
