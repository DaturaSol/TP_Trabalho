package trabalho.recrutamento.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import trabalho.common.database.JsonDataManager;
import trabalho.common.model.Role;
import trabalho.financeiro.model.Funcionario; // Assumindo esta localização
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Candidatura;
import trabalho.candidatura.model.Candidatura.StatusCandidatura;

/**
 * Representa um Recrutador (Recrutador) no sistema.
 * Herda de Funcionario e é responsável pela gestão do processo seletivo.
 */
public class Recrutador extends Funcionario {

    //Construtor para bibliotecas de serialização
    public Recrutador() {
        super();
    }


    public Recrutador(
            String nome,
            String cpfCnpj,
            String email,
            String endereco,
            long telefone,
            String login,
            String passHash,
            String cargo,
            String status,
            String departamento,
            double salarioBase) {
        super(nome,
                cpfCnpj,
                email,
                endereco,
                telefone,
                login,
                passHash,
                Role.RECRUTADOR, // Define a Role específica
                cargo,
                status,
                departamento,
                salarioBase);
    }


    //Cadastra um novo candidato no sistema. Esta é uma fachada
    // que chama o metodo estático da classe Candidato. (Regra: Recrutador cadastra candidatos)
    public boolean cadastrarCandidato(Candidato c) {
        // Validação de CPF e duplicação é tratada dentro de Candidato.cadastrarCandidato
        boolean sucesso = Candidato.cadastrarCandidato(c);
        if (sucesso) {
            // Se o Candidato.java não salvar no JSON, o recrutador precisa
            // (Assumindo que o Aluno 2 não usa o JsonDataManager)
            JsonDataManager.getInstance().getData().saveCandidato(c);
            JsonDataManager.getInstance().saveData();
        }
        return sucesso;
    }

    //Atualiza o status de uma candidatura. (Regra: Recrutador gerencia o processo seletivo)
    public void atualizarStatusCandidatura(Candidatura candidatura, StatusCandidatura novoStatus)
            throws Exception {

        // Regra de Negócio: Recrutador só gerencia vagas sob sua responsabilidade
        validarPermissaoVaga(candidatura.getVaga());

        candidatura.atualizarStatus(novoStatus);

        // Salva a mudança no banco de dados JSON
        JsonDataManager.getInstance().getData().saveCandidatura(candidatura);
        JsonDataManager.getInstance().saveData();
    }

    //Agenda uma nova entrevista para uma candidatura. (Regra: Recrutador agenda entrevistas)
    public Entrevista agendarEntrevista(Candidatura candidatura, Date dataHora) throws Exception {

        // Regra de negócio: Recrutador só gerencia vagas sob sua responsabilidade
        validarPermissaoVaga(candidatura.getVaga());

        Entrevista entrevista = new Entrevista(
                dataHora,
                this.getNome(), // O avaliador é o próprio recrutador logado
                candidatura.getCandidato().getCpfCnpj(),
                candidatura.getVaga().getId()
        );

        // Salva a nova entrevista no banco de dados
        JsonDataManager.getInstance().getData().saveEntrevista(entrevista);
        JsonDataManager.getInstance().saveData();
        return entrevista;
    }

    //Solicita a contratação de um candidato aprovado. (Regra: Recrutador solicita contratação)
    public Contratacao solicitarContratacao(Candidatura candidatura, RegimeContratacao regime) throws Exception {

        // Regra de Negócio: Recrutador só gerencia vagas sob sua responsabilidade
        validarPermissaoVaga(candidatura.getVaga());

        // Regra de Negócio: Apenas candidatos APROVADOS podem ser contratados (CORRIGIDO)
        if (candidatura.getStatusEnum() != StatusCandidatura.APROVADO) {
            throw new Exception("Apenas candidatos com status 'APROVADO' podem ser contratados.");
        }

        // Regra de Negócio: Deve ter ao menos uma entrevista (CÓDIGO AGORA VÁLIDO)
        long countEntrevistas = JsonDataManager.getInstance().getData().getEntrevistas().stream()
                .filter(e -> e.getCandidatoCpf().equals(candidatura.getCandidato().getCpfCnpj()) &&
                        e.getVagaId().equals(candidatura.getVaga().getId()))
                .count();

        if (countEntrevistas == 0) {
            throw new Exception("Nenhum candidato pode ser contratado sem ao menos uma entrevista registrada.");
        }

        Contratacao contratacao = new Contratacao(
                candidatura.getCandidato().getCpfCnpj(),
                candidatura.getVaga().getId(),
                regime,
                this.getCpfCnpj() // O solicitante é o recrutador logado
        );

        // Salva a nova solicitação de contratação
        JsonDataManager.getInstance().getData().saveContratacao(contratacao);
        JsonDataManager.getInstance().saveData();
        return contratacao;
    }

    //Consulta as candidaturas APENAS das vagas pelas quais este recrutador é responsável
    public List<Candidatura> consultarMinhasCandidaturas() {
        List<Candidatura> todasCandidaturas = JsonDataManager.getInstance().getData().getCandidaturas();

        return todasCandidaturas.stream()
                .filter(c -> c.getVaga() != null &&
                        this.cpfCnpj.equals(c.getVaga().getRecrutadorResponsavelCpf()))
                .collect(Collectors.toList());
    }
    //Metodo auxiliar para checar se o recrutador logado é responsavel pela vaga
    private void validarPermissaoVaga(Vaga vaga) throws Exception {
        if (!this.cpfCnpj.equals(vaga.getRecrutadorResponsavelCpf())) {
            throw new Exception("Permissão negada: Você não é o recrutador responsável por esta vaga.");
        }
    }

    //Sobrescreve o toString para exibir corretamente
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                super.dataString() + "}";
    }
}