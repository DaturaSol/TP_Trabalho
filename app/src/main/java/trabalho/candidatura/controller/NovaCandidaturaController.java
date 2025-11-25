package trabalho.candidatura.controller;

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
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Candidatura;
import trabalho.common.controller.ProfilePageController;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.recrutamento.model.Vaga;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class NovaCandidaturaController {

    @FXML
    private Button btnVoltar;
    @FXML
    private ComboBox<Candidato> comboCandidato;
    @FXML
    private ComboBox<Vaga> comboVaga;
    @FXML
    private Button btnConfirmar;
    @FXML
    private Button btnLimpar;

    @FXML
    private TextArea txtCandidatoDados;

    @FXML
    public void initialize() {
        // Popula os ComboBox com candidatos e vagas cadastrados
        List<Candidato> candidatos = getListaCandidatos();
        List<Vaga> vagas = getListaVagas();

        comboCandidato.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Candidato c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText(c.getPessoa().getNome()); // mostra só o nome
                }
            }
        });
        comboCandidato.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Candidato c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText(c.getPessoa().getNome());
                }
            }
        });
        comboCandidato.getItems().addAll(candidatos);
        comboVaga.getItems().addAll(vagas);
        comboCandidato.setOnAction(e -> mostrarDadosCandidato());

        // Configurar botões
        btnConfirmar.setOnAction(e -> confirmarCandidatura());
        btnLimpar.setOnAction(e -> limparSelecao());
    }

    private List<Candidato> getListaCandidatos() {
        try {
            // Use AppData to get the list of candidates
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();
            return appData.getCandidatos().values().stream().toList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private List<Vaga> getListaVagas() {
        try {
            // use appData to get the list of vagas
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();
            return appData.getVagasById().values().stream().filter(v -> v.getStatus() == Vaga.StatusVaga.ABERTA).toList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private void mostrarDadosCandidato() {
        Candidato c = comboCandidato.getValue();
        if (c != null) {
            txtCandidatoDados.setText(
                    "Nome: " + c.getPessoa().getNome() + "\n" +
                            "CPF: " + c.getCpfCnpj() + "\n" +
                            "Email: " + c.getPessoa().getEmail() + "\n" +
                            "Formação: " + c.getFormacao() + "\n" +
                            "Experiência: " + c.getExperiencia() + "\n" +
                            "Pretensão Salarial: R$ " + c.getPretensaoSalarial() + "\n" +
                            "Disponibilidade: " + c.getDisponibilidadeHorario());
        }
    }

    private void limparSelecao() {
        comboCandidato.setValue(null);
        comboVaga.setValue(null);
        txtCandidatoDados.clear();
    }

    private void confirmarCandidatura() {
        Candidato candidato = comboCandidato.getValue();
        Vaga vaga = comboVaga.getValue();

        if (candidato == null || vaga == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos obrigatórios", "Selecione um candidato e uma vaga.");
            return;
        }

        Candidatura nova = new Candidatura(candidato.getCpfCnpj(), vaga, new Date());
        boolean sucesso = Candidatura.cadastrarCandidatura(nova);

        if (sucesso) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Candidatura registrada com sucesso!");
            limparSelecao();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Este candidato já está cadastrado para essa vaga.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }


    private Usuario currentUser;
    
    @FXML
    private Button backButton;

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/candidatura/inicio.fxml"));
            Parent root = loader.load();

            InicioController controller = loader.getController();
            controller.initData(this.currentUser);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Candidatura");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void initData(Usuario user) {
        this.currentUser = user;
    }
}
