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
import java.util.Map;
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
    private Button btnPesquisar;
    @FXML
    private Button btnLimpar;


    // ====== Resultados ======
    @FXML
    private FlowPane flowResultados;

    @FXML
    public void initialize() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        // Conecta os botões aos métodos
        btnPesquisar.setOnAction(e -> pesquisar());
        btnLimpar.setOnAction(e -> limparFiltros());

        atualizarResultados(appData.getCandidatos().values().stream().toList());
    }

    /**
     * Executa a pesquisa aplicando os filtros selecionados.
     */
    @FXML
    private void pesquisar() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        Map<String, Candidato> todos = appData.getCandidatos();

        List<Candidato> filtrados = todos.values().stream()
                .filter(c -> {
                    boolean corresponde = true;

                    // filtro nome
                    if (checkNome.isSelected() && !txtNome.getText().isBlank()) {
                        String filtro = txtNome.getText().toLowerCase();
                        corresponde &= c.getPessoa().getNome().toLowerCase().contains(filtro);
                    }

                    // filtro CPF
                    if (checkVaga.isSelected() && !txtVaga.getText().isBlank()) {
                        String filtro = txtVaga.getText().toLowerCase();
                        corresponde &= c.getPessoa().getCpfCnpj().toLowerCase().contains(filtro);
                    }

                    return corresponde;
                })
                .toList();

        atualizarResultados(filtrados);

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

        txtNome.clear();
        txtVaga.clear();

        atualizarResultados(appData.getCandidatos().values().stream().toList());
    }

    /**
     * Atualiza a área de resultados (FlowPane) com as candidaturas filtradas.
     */
    private void atualizarResultados(List<Candidato> candidatos) {
        flowResultados.getChildren().clear();

        if (candidatos.isEmpty()) {
            Label vazio = new Label("Nenhum resultado encontrado.");
            flowResultados.getChildren().add(vazio);
            return;
        }

        for (Candidato c : candidatos) {

            Label lbl = new Label(
                    c.getPessoa().getNome() +
                            " — CPF: " + c.getPessoa().getCpfCnpj() +
                            " — Email: " + c.getPessoa().getEmail()
            );
            lbl.setPrefWidth(400);

            Button btnDetalhes = new Button("Detalhes");
            btnDetalhes.setOnAction(e -> abrirDetalhesCandidato(c));

            Button btnExcluir = new Button("Excluir");
            btnExcluir.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
            btnExcluir.setOnAction(e -> excluirCandidato(c));

            FlowPane linha = new FlowPane();
            linha.setHgap(10);
            linha.getChildren().addAll(lbl, btnDetalhes, btnExcluir);

            flowResultados.getChildren().add(linha);
        }
    }

    private void abrirDetalhesCandidato(Candidato c) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Detalhes do Candidato");
        alerta.setHeaderText("Candidato: " + c.getPessoa().getNome());

        alerta.setContentText(
                "CPF: " + c.getPessoa().getCpfCnpj() + "\n" +
                        "Email: " + c.getPessoa().getEmail() + "\n" +
                        "Formação: " + c.getFormacao() + "\n" +
                        "Experiência: " + c.getExperiencia()
        );

        alerta.showAndWait();
    }

    private void excluirCandidato(Candidato candidato) {
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Excluir Candidato");
        confirmar.setHeaderText("Excluir o candidato?");
        confirmar.setContentText("Isso removerá o candidato e suas informações.");

        if (confirmar.showAndWait().get() != ButtonType.OK) return;

        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        appData.getCandidaturas().removeIf(c ->
                c.getCandidato() != null &&
                        c.getCandidato().getCpfCnpj().equals(candidato.getCpfCnpj())
        );

        appData.getCandidatos().remove(candidato.getCpfCnpj());

        appData.getPessoas().remove(candidato.getCpfCnpj());

        dataManager.saveData();

        atualizarResultados(appData.getCandidatos().values().stream().toList());
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