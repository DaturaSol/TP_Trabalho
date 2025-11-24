package trabalho.candidatura.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Candidatura;
import trabalho.common.controller.ProfilePageController;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.recrutamento.model.Vaga;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultaCandidatoController {

    // ====== Filtros ======
    @FXML
    private CheckBox checkNome;
    @FXML
    private TextField txtNome;

    @FXML
    private CheckBox checkVaga;
    @FXML
    private TextField txtVaga;

    @FXML
    private CheckBox checkStatus;
    @FXML
    private ChoiceBox<String> choiceStatus;

    @FXML
    private Button btnPesquisar;
    @FXML
    private Button btnLimpar;

    @FXML
    private TextArea txtAreaResultados;

    // ====== Resultados ======
    @FXML
    private FlowPane flowResultados;

    @FXML
    public void initialize() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        // Inicializa os valores possíveis do status
        choiceStatus.getItems().addAll(
                "PENDENTE", "EM_ANALISE", "APROVADO", "REPROVADO");

        // Conecta os botões aos métodos
        btnPesquisar.setOnAction(e -> pesquisar());
        btnLimpar.setOnAction(e -> limparFiltros());

        atualizarResultados(appData.getCandidaturas());
    }

    /**
     * Executa a pesquisa aplicando os filtros selecionados.
     */
    @FXML
    private void pesquisar() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        List<Candidatura> todas = appData.getCandidaturas();

        List<Candidatura> filtradas = todas.stream()
                .filter(c -> {
                    boolean corresponde = true;

                    // Filtro por nome
                    if (checkNome.isSelected() && txtNome.getText() != null && !txtNome.getText().isEmpty()) {
                        String nomeFiltro = txtNome.getText().toLowerCase();
                        corresponde &= c.getCandidato().getPessoa().getNome().toLowerCase().contains(nomeFiltro);
                    }

                    // Filtro por vaga
                    if (checkVaga.isSelected() && txtVaga.getText() != null && !txtVaga.getText().isEmpty()) {
                        String vagaFiltro = txtVaga.getText().toLowerCase();
                        Vaga vaga = c.getVaga();
                        if (vaga != null && vaga.getCargo() != null) {
                            corresponde &= vaga.getCargo().toLowerCase().contains(vagaFiltro);
                        } else {
                            corresponde = false;
                        }
                    }

                    // Filtro por status
                    if (checkStatus.isSelected() && choiceStatus.getValue() != null) {
                        corresponde &= c.getStatus().equals(choiceStatus.getValue());
                    }

                    return corresponde;
                })
                .collect(Collectors.toList());

        atualizarResultados(filtradas);
    }

    /**
     * Remove os filtros e exibe todos os resultados.
     */
    @FXML
    private void limparFiltros() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        checkNome.setSelected(false);
        checkVaga.setSelected(false);
        checkStatus.setSelected(false);

        txtNome.clear();
        txtVaga.clear();
        choiceStatus.setValue(null);

        atualizarResultados(appData.getCandidaturas());
    }

    /**
     * Atualiza a área de resultados (FlowPane) com as candidaturas filtradas.
     */
    private void atualizarResultados(List<Candidatura> candidaturas) {
        flowResultados.getChildren().clear();

        if (candidaturas.isEmpty()) {
            Label vazio = new Label("Nenhum resultado encontrado.");
            flowResultados.getChildren().add(vazio);
            return;
        }

        for (Candidatura c : candidaturas) {
            Label lbl = new Label(
                    c.getCandidato().getPessoa().getNome() + " — " +
                            (c.getVaga() != null ? c.getVaga().getCargo() : "Sem vaga") +
                            " — Status: " + c.getStatus());
            lbl.setPrefWidth(500);

            Button btnDetalhes = new Button(">");
            btnDetalhes.setOnAction(e -> abrirDetalhes(c));

            FlowPane linha = new FlowPane();
            linha.setHgap(10);
            linha.getChildren().addAll(lbl, btnDetalhes);

            flowResultados.getChildren().add(linha);
        }
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

    /**
     * Exibe detalhes da candidatura selecionada (pode ser substituído por uma nova
     * tela).
     */
    private void abrirDetalhes(Candidatura c) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Detalhes do Candidato");
        alerta.setHeaderText("Candidato: " + c.getCandidato().getPessoa().getNome());
        alerta.setContentText(
                "Vaga: " + (c.getVaga() != null ? c.getVaga().getCargo() : "Sem vaga associada") + "\n" +
                        "Status: " + c.getStatus() + "\n" +
                        "CPF: " + c.getCandidato().getCpfCnpj() + "\n" +
                        "Email: " + c.getCandidato().getPessoa().getEmail() + "\n" +
                        "Formação: " + c.getCandidato().getFormacao() + "\n" +
                        "Experiência: " + c.getCandidato().getExperiencia());
        alerta.showAndWait();
    }
}