package trabalho.candidatura.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Candidatura;
import trabalho.recrutamento.model.Vaga;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class NovaCandidaturaController {

    @FXML private Button btnVoltar;
    @FXML private ComboBox<Candidato> comboCandidato;
    @FXML private ComboBox<Vaga> comboVaga;
    @FXML private Button btnConfirmar;
    @FXML private Button btnLimpar;

    @FXML private TextArea txtCandidatoDados;

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
                    setText(c.getNome()); // mostra só o nome
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
                    setText(c.getNome());
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
            // Método estático da classe Candidato
            java.lang.reflect.Field field = Candidato.class.getDeclaredField("listaCandidatos");
            field.setAccessible(true);
            return (List<Candidato>) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private List<Vaga> getListaVagas() {
        try {
            java.lang.reflect.Field field = Vaga.class.getDeclaredField("listaVagas");
            field.setAccessible(true);
            return (List<Vaga>) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private void mostrarDadosCandidato() {
        Candidato c = comboCandidato.getValue();
        if (c != null) {
            txtCandidatoDados.setText(
                    "Nome: " + c.getNome() + "\n" +
                            "CPF: " + c.getCpfCnpj() + "\n" +
                            "Email: " + c.getEmail() + "\n" +
                            "Formação: " + c.getFormacao() + "\n" +
                            "Experiência: " + c.getExperiencia() + "\n" +
                            "Pretensão Salarial: R$ " + c.getPretensaoSalarial() + "\n" +
                            "Disponibilidade: " + c.getDisponibilidadeHorario()
            );
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

        Candidatura nova = new Candidatura(candidato, vaga, new Date());
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

    @FXML
    private void voltarTela(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/candidatura/inicio.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu Principal");
        stage.show();
    }
}
