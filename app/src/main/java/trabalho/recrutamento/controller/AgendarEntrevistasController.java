package trabalho.recrutamento.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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

    @FXML
    public void initialize() {
        // Popular horários
        comboHorario.getItems().addAll(
            "08:00", "09:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        );
        
        // Carregar candidaturas aprovadas
        List<Candidatura> candidaturas = getCandidaturasAprovadas();
        
        comboCandidatura.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Candidatura c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText(c.getCandidato().getPessoa().getNome() + " - " + 
                           (c.getVaga() != null ? c.getVaga().getCargo() : "Sem vaga"));
                }
            }
        });
        
        comboCandidatura.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Candidatura c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText(c.getCandidato().getPessoa().getNome() + " - " + 
                           (c.getVaga() != null ? c.getVaga().getCargo() : "Sem vaga"));
                }
            }
        });
        
        comboCandidatura.getItems().addAll(candidaturas);
        comboCandidatura.setOnAction(e -> mostrarDadosCandidatura());
        
        btnAgendar.setOnAction(e -> agendar());
        btnLimpar.setOnAction(e -> limpar());
    }

    private Usuario currentUser;

    private void agendar() {
        try {
            Candidatura candidatura = comboCandidatura.getValue();
            if (candidatura == null || dateEntrevista.getValue() == null || 
                comboHorario.getValue() == null || txtAvaliador.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Preencha candidatura, data, horário e avaliador.");
                return;
            }
            
            LocalDateTime dataHora = LocalDateTime.of(
                dateEntrevista.getValue(),
                LocalTime.parse(comboHorario.getValue())
            );
            
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();
            
            Entrevista entrevista = new Entrevista();
            entrevista.setId("ENT" + System.currentTimeMillis());
            entrevista.setCandidaturaCpf(candidatura.getCpfCnpjCandidato());
            entrevista.setVagaId(candidatura.getVaga() != null ? candidatura.getVaga().getId() : null);
            entrevista.setDataHora(dataHora);
            entrevista.setAvaliador(txtAvaliador.getText());
            entrevista.setObservacoes(txtObservacoes.getText());
            
            appData.addEntrevista(entrevista);
            dataManager.saveData();
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Entrevista agendada com sucesso!");
            limpar();
            
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao agendar: " + e.getMessage());
        }
    }

    private void mostrarDadosCandidatura() {
        Candidatura c = comboCandidatura.getValue();
        if (c != null) {
            txtDadosCandidatura.setText(
                "Candidato: " + c.getCandidato().getPessoa().getNome() + "\n" +
                "CPF: " + c.getCandidato().getCpfCnpj() + "\n" +
                "Email: " + c.getCandidato().getPessoa().getEmail() + "\n" +
                "Vaga: " + (c.getVaga() != null ? c.getVaga().getCargo() : "Não especificada") + "\n" +
                "Status: " + c.getStatus()
            );
        }
    }

    private void limpar() {
        comboCandidatura.setValue(null);
        dateEntrevista.setValue(null);
        comboHorario.setValue(null);
        txtAvaliador.clear();
        txtObservacoes.clear();
        txtDadosCandidatura.clear();
    }

    private List<Candidatura> getCandidaturasAprovadas() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        return appData.getCandidaturas().stream()
            .filter(c -> "APROVADO".equals(c.getStatus()) || "EM_ANALISE".equals(c.getStatus()))
            .collect(Collectors.toList());
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

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
            stage.setTitle("User Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void initData(Usuario user) {
        this.currentUser = user;
    }
}
