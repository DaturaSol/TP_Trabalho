package trabalho.recrutamento.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import trabalho.admin.model.Usuario;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.recrutamento.model.RegimeContratacao;
import trabalho.recrutamento.model.Vaga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gerenciar vagas
 * Permite: cadastrar, editar, excluir, listar e filtrar vagas
 */
public class GerenciarVagasController {

    // Campos do formulário
    @FXML
    private TextField txtCargo;
    @FXML
    private TextField txtDepartamento;
    @FXML
    private TextField txtSalarioBase;
    @FXML
    private TextArea txtRequisitos;
    @FXML
    private ComboBox<String> comboRegime;
    @FXML
    private ComboBox<String> comboStatus;

    // Filtros
    @FXML
    private CheckBox checkCargo;
    @FXML
    private TextField txtFiltroCargo;
    @FXML
    private CheckBox checkDepartamento;
    @FXML
    private TextField txtFiltroDepartamento;
    @FXML
    private CheckBox checkStatus;
    @FXML
    private ChoiceBox<String> choiceStatus;
    @FXML
    private CheckBox checkRegime;
    @FXML
    private ChoiceBox<String> choiceRegime;

    // Botões e resultados
    @FXML
    private Button btnSalvar;
    @FXML
    private Button btnLimpar;
    @FXML
    private Button btnPesquisar;
    @FXML
    private Button btnLimparFiltros;
    @FXML
    private Button backButton;
    @FXML
    private FlowPane flowResultados;

    private Vaga vagaEditando = null;
    private Usuario currentUser;

    @FXML
    public void initialize() {
        // Popular combos
        comboRegime.setItems(FXCollections.observableArrayList("CLT", "ESTAGIO", "PJ"));
        comboStatus.setItems(FXCollections.observableArrayList("ABERTA", "FECHADA"));
        choiceRegime.setItems(FXCollections.observableArrayList("CLT", "ESTAGIO", "PJ"));
        choiceStatus.setItems(FXCollections.observableArrayList("ABERTA", "FECHADA"));

        comboStatus.setValue("ABERTA");

        // Ações dos botões
        btnSalvar.setOnAction(e -> salvar());
        btnLimpar.setOnAction(e -> limparFormulario());
        btnPesquisar.setOnAction(e -> pesquisar());
        btnLimparFiltros.setOnAction(e -> limparFiltros());

        // Carregar lista inicial
        atualizarResultados(getListaVagas());
    }

    private void salvar() {
        try {
            String cargo = txtCargo.getText();
            String depto = txtDepartamento.getText();
            String salarioStr = txtSalarioBase.getText();
            String requisitos = txtRequisitos.getText();
            String regime = comboRegime.getValue();
            String status = comboStatus.getValue();

            if (cargo.isEmpty() || depto.isEmpty() || salarioStr.isEmpty() || regime == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Preencha cargo, departamento, salário e regime.");
                return;
            }

            double salario = Double.parseDouble(salarioStr);

            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();

            Vaga vaga;
            if (vagaEditando != null) {
                vaga = vagaEditando;
            } else {
                vaga = new Vaga();
                vaga.setDataAbertura(new Date());
            }

            vaga.setCargo(cargo);
            vaga.setDepartamento(depto);
            vaga.setSalarioBase(salario);
            vaga.setRequisitos(requisitos);
            vaga.setRegimeContratacao(RegimeContratacao.valueOf(regime));
            vaga.setStatus(Vaga.StatusVaga.valueOf(status));

            if (vagaEditando == null) {
                appData.addVaga(vaga);
            }

            dataManager.saveData();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Vaga salva com sucesso!");
            limparFormulario();
            atualizarResultados(getListaVagas());

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar: " + e.getMessage());
        }
    }

    private void limparFormulario() {
        txtCargo.clear();
        txtDepartamento.clear();
        txtSalarioBase.clear();
        txtRequisitos.clear();
        comboRegime.setValue(null);
        comboStatus.setValue("ABERTA");
        vagaEditando = null;
    }

    private void pesquisar() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        // List<Vaga> todas = appData.getVagasById();

        List<Vaga> filtradas = appData.getVagasById().values().stream()
                .filter(v -> {
                    boolean ok = true;

                    if (checkCargo.isSelected() && txtFiltroCargo.getText() != null
                            && !txtFiltroCargo.getText().isEmpty()) {
                        ok &= v.getCargo().toLowerCase().contains(txtFiltroCargo.getText().toLowerCase());
                    }

                    if (checkDepartamento.isSelected() && txtFiltroDepartamento.getText() != null
                            && !txtFiltroDepartamento.getText().isEmpty()) {
                        ok &= v.getDepartamento().toLowerCase().contains(txtFiltroDepartamento.getText().toLowerCase());
                    }

                    if (checkStatus.isSelected() && choiceStatus.getValue() != null) {
                        ok &= v.getStatus().name().equals(choiceStatus.getValue());
                    }

                    if (checkRegime.isSelected() && choiceRegime.getValue() != null) {
                        ok &= v.getRegimeContratacao().name().equals(choiceRegime.getValue());
                    }

                    return ok;
                })
                .collect(Collectors.toList());

        atualizarResultados(filtradas);
    }

    private void limparFiltros() {
        checkCargo.setSelected(false);
        checkDepartamento.setSelected(false);
        checkStatus.setSelected(false);
        checkRegime.setSelected(false);
        txtFiltroCargo.clear();
        txtFiltroDepartamento.clear();
        choiceStatus.setValue(null);
        choiceRegime.setValue(null);
        atualizarResultados(getListaVagas());
    }

    private void atualizarResultados(List<Vaga> vagas) {
        flowResultados.getChildren().clear();

        if (vagas.isEmpty()) {
            Label vazio = new Label("Nenhuma vaga encontrada.");
            flowResultados.getChildren().add(vazio);
            return;
        }

        for (Vaga v : vagas) {
            Label lbl = new Label(
                    v.getCargo() + " — " + v.getDepartamento() + " — R$ " +
                            String.format("%.2f", v.getSalarioBase()) + " — " + v.getStatus());
            lbl.setPrefWidth(500);

            Button btnEditar = new Button("Editar");
            btnEditar.setOnAction(e -> editarVaga(v));

            Button btnExcluir = new Button("Excluir");
            btnExcluir.setOnAction(e -> excluirVaga(v));

            FlowPane linha = new FlowPane();
            linha.setHgap(5);
            linha.getChildren().addAll(lbl, btnEditar, btnExcluir);

            flowResultados.getChildren().add(linha);
        }
    }

    private void editarVaga(Vaga v) {
        vagaEditando = v;
        txtCargo.setText(v.getCargo());
        txtDepartamento.setText(v.getDepartamento());
        txtSalarioBase.setText(String.format("%.2f", v.getSalarioBase()));
        txtRequisitos.setText(v.getRequisitos());
        comboRegime.setValue(v.getRegimeContratacao().name());
        comboStatus.setValue(v.getStatus().name());
    }

    private void excluirVaga(Vaga v) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deseja excluir esta vaga?");
        confirmacao.setContentText(v.getCargo() + " - " + v.getDepartamento());

        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    JsonDataManager dataManager = JsonDataManager.getInstance();
                    AppData appData = dataManager.getData();
                    appData.removeVaga(v);
                    dataManager.saveData();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Vaga excluída!");
                    atualizarResultados(getListaVagas());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir: " + e.getMessage());
                }
            }
        });
    }

    private List<Vaga> getListaVagas() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        return new ArrayList<>(appData.getVagasById().values());
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/trabalho/fxml/recrutamento/menu_recrutamento.fxml"));
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
