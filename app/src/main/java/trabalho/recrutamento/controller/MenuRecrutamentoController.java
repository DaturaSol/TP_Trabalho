package trabalho.recrutamento.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Menu principal do módulo de Recrutamento
 * Padrão: InicioController de candidatura
 */
public class MenuRecrutamentoController {

    @FXML
    private Button btnGerenciarVagas;
    @FXML
    private Button btnAgendarEntrevistas;
    @FXML
    private Button btnSolicitarContratacoes;
    @FXML
    private Button btnAutorizarContratacoes;

    @FXML
    public void initialize() {
        btnGerenciarVagas.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/gerenciar_vagas.fxml", e));
        btnAgendarEntrevistas.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/agendar_entrevistas.fxml", e));
        btnSolicitarContratacoes.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/solicitar_contratacoes.fxml", e));
        btnAutorizarContratacoes.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/autorizar_contratacoes.fxml", e));
    }

    private void abrirTela(String caminhoFXML, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Recrutamento");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir tela: " + caminhoFXML);
        }
    }
}
