package trabalho.candidatura.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import trabalho.candidatura.model.Candidatura;
import trabalho.common.controller.ProfilePageController;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Candidato;
import trabalho.recrutamento.model.Vaga;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StatusCandidaturaController {


    @FXML
    private TableView<Candidatura> tabelaCandidaturas;
    @FXML
    private TableColumn<Candidatura, String> colCandidato;
    @FXML
    private TableColumn<Candidatura, String> colVaga;
    @FXML
    private TableColumn<Candidatura, String> colData;
    @FXML
    private TableColumn<Candidatura, String> colStatus;
    @FXML
    private TableColumn<Candidatura, Void> colAcoes;


    @FXML
    private TextField txtNomeCandidato;
    @FXML
    private TextField txtVaga;
    @FXML
    private Button btnPesquisar;
    @FXML
    private Button btnLimpar;

    private ObservableList<Candidatura> listaCandidaturas;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurarColunas();
        carregarCandidaturas();
        configurarAcoes();
    }

    private void configurarColunas() {
        // Se as colunas estiverem definidas no FXML com fx:id, usamos
        // setCellValueFactory com lambdas
        colCandidato.setCellValueFactory(cell -> {
            Candidato c = cell.getValue().getCandidato();
            String nome = (c != null && c.getPessoa().getNome() != null) ? c.getPessoa().getNome() : "";
            return new SimpleStringProperty(nome);
        });

        colVaga.setCellValueFactory(cell -> {
            Vaga v = cell.getValue().getVaga();
            String vagaNome = obterNomeVaga(v);
            return new SimpleStringProperty(vagaNome);
        });

        colData.setCellValueFactory(cell -> {
            String data = obterDataFormatada(cell.getValue());
            return new SimpleStringProperty(data);
        });

        colStatus.setCellValueFactory(cell -> {
            String s = cell.getValue().getStatus();
            return new SimpleStringProperty(s != null ? s : "");
        });
        adicionarColunaAcoes();
    }

    private void adicionarColunaAcoes() {
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");

            {
                btnEditar.setOnAction(e -> {
                    Candidatura candidatura = getTableView().getItems().get(getIndex());
                    abrirDialogEditarStatus(candidatura);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEditar);
                }
            }
        });
    }

    private void abrirDialogEditarStatus(Candidatura candidatura) {

        ChoiceDialog<Candidatura.StatusCandidatura> dialog =
                new ChoiceDialog<>(candidatura.getStatusEnum(),
                        Candidatura.StatusCandidatura.values());

        dialog.setTitle("Editar Status");
        dialog.setHeaderText("Editar status da candidatura");
        dialog.setContentText("Selecione o novo status:");

        dialog.showAndWait().ifPresent(statusSelecionado -> {

            candidatura.setStatus(statusSelecionado);

            JsonDataManager dataManager = JsonDataManager.getInstance();
            dataManager.saveData(); // salva no JSON

            tabelaCandidaturas.refresh();
        });
    }

    private void carregarCandidaturas() {
        List<Candidatura> todas = getListaCandidaturas();
        listaCandidaturas = FXCollections.observableArrayList(todas);
        tabelaCandidaturas.setItems(listaCandidaturas);
    }

    private void configurarAcoes() {
        btnPesquisar.setOnAction(e -> filtrarCandidaturas());
        btnLimpar.setOnAction(e -> limparFiltros());
    }

    private String obterNomeVaga(Vaga v) {
        if (v == null)
            return "";
        try {
            // tenta métodos comuns
            try {
                // getCargo()
                var m = v.getClass().getMethod("getCargo");
                Object res = m.invoke(v);
                if (res != null)
                    return res.toString();
            } catch (NoSuchMethodException ignored) {
            }
            try {
                // getTitulo()
                var m = v.getClass().getMethod("getTitulo");
                Object res = m.invoke(v);
                if (res != null)
                    return res.toString();
            } catch (NoSuchMethodException ignored) {
            }
            // fallback para toString()
            return v.toString();
        } catch (Exception ex) {
            return v.toString();
        }
    }

    private String obterDataFormatada(Candidatura c) {
        if (c == null)
            return "";
        // tenta obter campo/prop de data por reflexao (suporta várias implementações)
        try {
            // tenta getter getDataCandidatura()
            try {
                var gm = c.getClass().getMethod("getDataCandidatura");
                Object d = gm.invoke(c);
                if (d instanceof Date)
                    return sdf.format((Date) d);
            } catch (NoSuchMethodException ignored) {
            }
            // tenta getter getData()
            try {
                var gm = c.getClass().getMethod("getData");
                Object d = gm.invoke(c);
                if (d instanceof Date)
                    return sdf.format((Date) d);
            } catch (NoSuchMethodException ignored) {
            }
            // tenta acessar campo dataCandidatura ou data
            try {
                Field f = c.getClass().getDeclaredField("dataCandidatura");
                f.setAccessible(true);
                Object d = f.get(c);
                if (d instanceof Date)
                    return sdf.format((Date) d);
            } catch (NoSuchFieldException ignored) {
            }
            try {
                Field f = c.getClass().getDeclaredField("data");
                f.setAccessible(true);
                Object d = f.get(c);
                if (d instanceof Date)
                    return sdf.format((Date) d);
            } catch (NoSuchFieldException ignored) {
            }
        } catch (Exception ex) {
            // segue pro fallback
        }
        return ""; // se não conseguir localizar, retorna vazio
    }

    private List<Candidatura> getListaCandidaturas() {
        try {
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();
            return appData.getCandidaturas();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private void filtrarCandidaturas() {
        String nomeFiltro = txtNomeCandidato.getText() == null ? "" : txtNomeCandidato.getText().trim().toLowerCase();
        String vagaFiltro = txtVaga.getText() == null ? "" : txtVaga.getText().trim().toLowerCase();

        List<Candidatura> todas = getListaCandidaturas();

        List<Candidatura> filtradas = todas.stream().filter(c -> {
            boolean okNome = true;
            boolean okVaga = true;

            if (!nomeFiltro.isEmpty()) {
                Candidato cand = c.getCandidato();
                okNome = cand != null && cand.getPessoa().getNome() != null
                        && cand.getPessoa().getNome().toLowerCase().contains(nomeFiltro);
            }
            if (!vagaFiltro.isEmpty()) {
                String nomeVaga = obterNomeVaga(c.getVaga()).toLowerCase();
                okVaga = nomeVaga.contains(vagaFiltro);
            }
            return okNome && okVaga;
        }).collect(Collectors.toList());

        tabelaCandidaturas.setItems(FXCollections.observableArrayList(filtradas));
    }

    private void limparFiltros() {
        txtNomeCandidato.clear();
        txtVaga.clear();
        tabelaCandidaturas.setItems(listaCandidaturas);
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
