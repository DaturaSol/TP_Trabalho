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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para agendar entrevistas
 * Permite vincular entrevista a uma candidatura
 * Implementa filtragem por recrutador e preenchimento automático de dados
 */
public class AgendarEntrevistasController {

    @FXML private ComboBox<Candidatura> comboCandidatura;
    @FXML private DatePicker dateEntrevista;
    @FXML private ComboBox<String> comboHorario;
    @FXML private TextField txtAvaliador;
    @FXML private TextArea txtObservacoes;
    @FXML private TextArea txtDadosCandidatura;
    @FXML private Button btnAgendar;
    @FXML private Button btnLimpar;
    @FXML private Button backButton;

    private Usuario currentUser;

    /**
     * Inicializa o controller, populando os campos e configurando eventos
     */
    @FXML
    public void initialize() {
        // Popular horários disponíveis
        comboHorario.getItems().addAll(
            "08:00", "09:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        );
        
        // Configurar ComboBox de candidaturas com formatação customizada
        configurarComboCandidatura();
        
        // Configurar eventos dos botões
        btnAgendar.setOnAction(e -> agendar());
        btnLimpar.setOnAction(e -> limpar());
    }

    /**
     * Inicializa os dados do usuário logado e carrega candidaturas filtradas
     * DEVE ser chamado após o controller ser carregado
     * @param user Usuário (recrutador) logado no sistema
     */
    public void initData(Usuario user) {
        this.currentUser = user;
        
        // Preencher automaticamente o campo Avaliador com o CPF do recrutador logado
        if (user != null && user.getCpfCnpj() != null) {
            txtAvaliador.setText(user.getCpfCnpj());
            txtAvaliador.setEditable(false); // Bloquear edição manual
            txtAvaliador.setStyle("-fx-opacity: 0.7;"); // Indicar visualmente que é automático
        }
        
        // Carregar candidaturas filtradas por recrutador
        carregarCandidaturasDoRecrutador();
    }

    /**
     * Configura a exibição do ComboBox de candidaturas
     */
    private void configurarComboCandidatura() {
        // Formato da célula na lista dropdown
        comboCandidatura.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Candidatura c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText(formatarCandidatura(c));
                }
            }
        });
        
        // Formato da célula quando selecionada
        comboCandidatura.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Candidatura c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText(formatarCandidatura(c));
                }
            }
        });
        
        //Evento ao selecionar candidatura
        comboCandidatura.setOnAction(e -> mostrarDadosCandidatura());
    }


    private String formatarCandidatura(Candidatura c) {
        return c.getCandidato().getPessoa().getNome() + " - " + 
               (c.getVaga() != null ? c.getVaga().getCargo() : "Sem vaga");
    }

    /**
     * Carrega apenas as candidaturas das vagas atribuídas ao recrutador logado
     * e implementa a filtro por recrutadorResponsavelCpf
     */
    private void carregarCandidaturasDoRecrutador() {
        if (currentUser == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                "Usuário não identificado. Faça login novamente.");
            return;
        }

        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        
        String cpfRecrutador = currentUser.getCpfCnpj();
        
        //Status EM_ANALISE (candidaturas que ainda não têm entrevista)
        //Vaga tem recrutadorResponsavelCpf igual ao CPF do recrutador logado
        List<Candidatura> candidaturasFiltradas = appData.getCandidaturas().stream()
            .filter(c -> "EM_ANALISE".equals(c.getStatus()))
            .filter(c -> c.getVaga() != null)
            .filter(c -> cpfRecrutador.equals(c.getVaga().getRecrutadorResponsavelCpf()))
            .collect(Collectors.toList());
        
        comboCandidatura.getItems().clear();
        comboCandidatura.getItems().addAll(candidaturasFiltradas);
        
        //Verificar se há candidaturas disponíveis
        if (candidaturasFiltradas.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Aviso", 
                "Não há candidaturas disponíveis para agendamento.");
        }
    }

    /**
     * Agenda a entrevista e persiste no JSON
     */
    private void agendar() {
        try {
            // Validar campos obrigatórios
            Candidatura candidatura = comboCandidatura.getValue();
            if (candidatura == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Selecione uma candidatura.");
                return;
            }
            
            if (dateEntrevista.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Selecione a data da entrevista.");
                return;
            }
            
            if (comboHorario.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Selecione o horário da entrevista.");
                return;
            }
            
            if (txtAvaliador.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "O campo avaliador está vazio.");
                return;
            }
            
            // Construir data e hora da entrevista
            LocalDateTime dataHora = LocalDateTime.of(
                dateEntrevista.getValue(),
                LocalTime.parse(comboHorario.getValue())
            );
            
            // Validar data futura
            if (dataHora.isBefore(LocalDateTime.now())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "A data e hora da entrevista devem ser futuras.");
                return;
            }
            
            // Criar objeto Entrevista
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();
            
            Entrevista entrevista = new Entrevista();
            entrevista.setId("ENT" + System.currentTimeMillis());
            entrevista.setCandidaturaCpf(candidatura.getCpfCnpjCandidato());
            entrevista.setVagaId(candidatura.getVaga().getId());
            entrevista.setDataHora(dataHora);
            entrevista.setAvaliadorCpf(txtAvaliador.getText());
            entrevista.setObservacoes(txtObservacoes.getText() != null ? 
                txtObservacoes.getText() : "");
            
            // Adicionar à lista de entrevistas e salvar
            appData.addEntrevista(entrevista);
            dataManager.saveData();
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", 
                "Entrevista agendada com sucesso!\n\n" +
                "Candidato: " + candidatura.getCandidato().getPessoa().getNome() + "\n" +
                "Data/Hora: " + dataHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            
            // Limpar formulário e recarregar lista
            limpar();
            carregarCandidaturasDoRecrutador();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                "Erro ao agendar entrevista: " + e.getMessage());
        }
    }

    /**
     * Exibe os dados da candidatura selecionada
     */
    private void mostrarDadosCandidatura() {
        Candidatura c = comboCandidatura.getValue();
        if (c != null) {
            StringBuilder dados = new StringBuilder();
            dados.append("Candidato: ").append(c.getCandidato().getPessoa().getNome()).append("\n");
            dados.append("CPF: ").append(c.getCandidato().getCpfCnpj()).append("\n");
            dados.append("Email: ").append(c.getCandidato().getPessoa().getEmail()).append("\n");
            dados.append("Vaga: ").append(c.getVaga() != null ? c.getVaga().getCargo() : "Não especificada").append("\n");
            dados.append("Departamento: ").append(c.getVaga() != null ? c.getVaga().getDepartamento() : "N/A").append("\n");
            dados.append("Status: ").append(c.getStatus()).append("\n");
            //dados.append("Data Candidatura: ").append(c.getDataCandidatura());
            
            txtDadosCandidatura.setText(dados.toString());
        }
    }

    /**
     * Limpa todos os campos do formulário
     */
    private void limpar() {
        comboCandidatura.setValue(null);
        dateEntrevista.setValue(null);
        comboHorario.setValue(null);
        txtObservacoes.clear();
        txtDadosCandidatura.clear();
        // Não limpar txtAvaliador pois é preenchido automaticamente
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/recrutamento/menu_recrutamento.fxml"));
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