package trabalho.recrutamento.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Candidatura;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.recrutamento.model.Entrevista;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RealizarEntrevistaController {

    @FXML private ComboBox<Entrevista> comboEntrevista;
    @FXML private TextField txtNota;
    @FXML private ComboBox<String> comboResultado;
    @FXML private TextArea txtParecer;
    @FXML private TextArea txtDadosEntrevista;
    @FXML private Button btnRealizar;
    @FXML private Button btnLimpar;
    @FXML private Button backButton;

    private Usuario currentUser;


    @FXML
    public void initialize() {
        // Popular combo de resultado
        comboResultado.getItems().addAll("APROVADO", "REPROVADO");
        
        // Configurar ComboBox de entrevistas
        configurarComboEntrevista();
        
        // Configurar eventos
        btnRealizar.setOnAction(e -> realizarEntrevista());
        btnLimpar.setOnAction(e -> limpar());
    }


    public void initData(Usuario user) {
        this.currentUser = user;
        carregarEntrevistasDoRecrutador();
    }

    private void configurarComboEntrevista() {
        comboEntrevista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Entrevista e, boolean empty) {
                super.updateItem(e, empty);
                if (empty || e == null) {
                    setText(null);
                } else {
                    setText(formatarEntrevista(e));
                }
            }
        });
        
        comboEntrevista.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Entrevista e, boolean empty) {
                super.updateItem(e, empty);
                if (empty || e == null) {
                    setText(null);
                } else {
                    setText(formatarEntrevista(e));
                }
            }
        });
        
        // Evento ao selecionar entrevista
        comboEntrevista.setOnAction(e -> mostrarDadosEntrevista());
    }

    /**
     * Formata a exibição de uma entrevista no ComboBox
     */
    private String formatarEntrevista(Entrevista e) {
        Candidatura c = getCandidatura(e.getCandidatoCpf(), e.getVagaId());
        if (c != null && c.getCandidato() != null && c.getCandidato().getPessoa() != null) {
            String nome = c.getCandidato().getPessoa().getNome();
            String vaga = c.getVaga() != null ? c.getVaga().getCargo() : "Sem vaga";
            String data = e.getDataHora() != null ? 
                e.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Data não definida";
            
            return nome + " - " + vaga + " - " + data;
        }
        return "Entrevista " + e.getId();
    }

    /**
     * Carrega apenas as entrevistas agendadas pelo recrutador logado
     * Filtra por avaliadorCpf e entrevistas ainda não realizadas (nota == null)
     */
    private void carregarEntrevistasDoRecrutador() {
        if (currentUser == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                "Usuário não identificado. Faça login novamente.");
            return;
        }

        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        
        String cpfRecrutador = currentUser.getCpfCnpj();
        
        // Filtrar entrevistas:
        // 1. Avaliador é o recrutador logado
        // 2. Ainda não foi realizada (nota == null ou nota == 0)
        List<Entrevista> entrevistasFiltradas = appData.getEntrevistas().stream()
            .filter(e -> cpfRecrutador.equals(e.getAvaliadorCpf()))
            .filter(e -> e.getNota() == null || e.getNota() == 0.0)
            .collect(Collectors.toList());
        
        comboEntrevista.getItems().clear();
        comboEntrevista.getItems().addAll(entrevistasFiltradas);
        
        // Verificar se há entrevistas disponíveis
        if (entrevistasFiltradas.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Aviso", 
                "Não há entrevistas agendadas para você.\n" +
                "Verifique se há entrevistas pendentes de realização.");
        }
    }

    /**
     * Registra o resultado da entrevista e atualiza a candidatura
     */
    private void realizarEntrevista() {
        try {
            // Validar campos obrigatórios
            Entrevista entrevista = comboEntrevista.getValue();
            if (entrevista == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Selecione uma entrevista.");
                return;
            }
            
            if (txtNota.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Digite a nota da entrevista (0 a 10).");
                return;
            }
            
            if (comboResultado.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Selecione o resultado (APROVADO ou REPROVADO).");
                return;
            }
            
            if (txtParecer.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Digite um parecer sobre a entrevista.");
                return;
            }
            
            // Validar nota (0-10)
            double nota;
            try {
                nota = Double.parseDouble(txtNota.getText());
                if (nota < 0 || nota > 10) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                        "A nota deve estar entre 0 e 10.");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Digite uma nota válida (número entre 0 e 10).");
                return;
            }
            
            String resultado = comboResultado.getValue();
            String parecer = txtParecer.getText();
            
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();
            
            // Atualizar entrevista com nota e parecer
            entrevista.registrarResultado(nota, parecer);
            
            // Buscar candidatura correspondente
            Candidatura candidatura = getCandidatura(entrevista.getCandidatoCpf(), entrevista.getVagaId());
            
            if (candidatura == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                    "Candidatura não encontrada para esta entrevista.");
                return;
            }
            
            // Atualizar status da candidatura baseado no resultado
            candidatura.setStatus(Candidatura.StatusCandidatura.valueOf(resultado));
            
            // Salvar alterações
            dataManager.saveData();
            
            // Obter nome do candidato para mensagem
            String nomeCandidato = candidatura.getCandidato() != null && 
                                  candidatura.getCandidato().getPessoa() != null ?
                                  candidatura.getCandidato().getPessoa().getNome() : "Candidato";
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", 
                "Entrevista realizada com sucesso!\n\n" +
                "Candidato: " + nomeCandidato + "\n" +
                "Nota: " + nota + "\n" +
                "Resultado: " + resultado);
            
            // Limpar formulário e recarregar lista
            limpar();
            carregarEntrevistasDoRecrutador();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                "Erro ao realizar entrevista: " + e.getMessage());
        }
    }

    /**
     * Exibe os dados da entrevista selecionada
     */
    private void mostrarDadosEntrevista() {
        Entrevista e = comboEntrevista.getValue();
        if (e == null) {
            return;
        }
        
        Candidatura c = getCandidatura(e.getCandidatoCpf(), e.getVagaId());
        
        StringBuilder dados = new StringBuilder();
        
        if (c != null) {
            // Dados do candidato
            if (c.getCandidato() != null && c.getCandidato().getPessoa() != null) {
                dados.append("=== CANDIDATO ===\n");
                dados.append("Nome: ").append(c.getCandidato().getPessoa().getNome()).append("\n");
                dados.append("CPF: ").append(c.getCandidato().getCpfCnpj()).append("\n");
                dados.append("Email: ").append(c.getCandidato().getPessoa().getEmail()).append("\n");
                
                // Dados da candidatura
                dados.append("\n=== CANDIDATURA ===\n");
                dados.append("Formação: ").append(
                    c.getCandidato().getFormacao() != null && !c.getCandidato().getFormacao().isEmpty() ?
                    c.getCandidato().getFormacao() : "Não informada").append("\n");
                dados.append("Experiência: ").append(
                    c.getCandidato().getExperiencia() != null && !c.getCandidato().getExperiencia().isEmpty() ?
                    c.getCandidato().getExperiencia() : "Não informada").append("\n");
                dados.append("Pretensão Salarial: R$ ").append(
                    String.format("%.2f", c.getCandidato().getPretensaoSalarial())).append("\n");
            }
            
            // Dados da vaga
            if (c.getVaga() != null) {
                dados.append("\n=== VAGA ===\n");
                dados.append("Cargo: ").append(c.getVaga().getCargo()).append("\n");
                dados.append("Departamento: ").append(c.getVaga().getDepartamento()).append("\n");
                dados.append("Salário Base: R$ ").append(
                    String.format("%.2f", c.getVaga().getSalarioBase())).append("\n");
                dados.append("Requisitos: ").append(c.getVaga().getRequisitos()).append("\n");
            }
        }
        
        // Dados da entrevista
        dados.append("\n=== ENTREVISTA ===\n");
        dados.append("Data/Hora: ").append(
            e.getDataHora() != null ? 
            e.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : 
            "Não definida").append("\n");
        dados.append("Observações: ").append(
            e.getObservacoes() != null && !e.getObservacoes().isEmpty() ?
            e.getObservacoes() : "Nenhuma").append("\n");
        
        txtDadosEntrevista.setText(dados.toString());
    }

    /**
     * Limpa todos os campos do formulário
     */
    private void limpar() {
        comboEntrevista.setValue(null);
        txtNota.clear();
        comboResultado.setValue(null);
        txtParecer.clear();
        txtDadosEntrevista.clear();
    }

    /**
     * Busca a candidatura correspondente a uma entrevista
     */
    private Candidatura getCandidatura(String cpfCandidato, String vagaId) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        
        return appData.getCandidaturas().stream()
            .filter(c -> c.getCpfCnpjCandidato().equals(cpfCandidato))
            .filter(c -> c.getVaga() != null && c.getVaga().getId().equals(vagaId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Exibe um alerta para o usuário
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    /**
     * Volta para o menu de recrutamento
     */
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/trabalho/fxml/recrutamento/menu_recrutamento.fxml")
            );
            Parent root = loader.load();

            MenuRecrutamentoController controller = loader.getController();
            controller.initData(this.currentUser);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Menu Recrutamento");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                "Erro ao voltar para o menu: " + e.getMessage());
        }
    }
}