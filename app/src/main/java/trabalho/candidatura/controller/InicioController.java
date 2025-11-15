package trabalho.candidatura.controller;

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
 * Controlador da tela inicial de candidatura.
 * Responsável por navegar entre as telas principais.
 */
public class InicioController {

    @FXML
    private Button btnCadastrarCandidato;
    @FXML
    private Button btnNovaCandidatura;
    @FXML
    private Button btnStatusCandidatura;
    @FXML
    private Button btnConsultarCandidato;

    @FXML
    public void initialize() {
        btnCadastrarCandidato
                .setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/cadastro_candidato.fxml", e));
        btnNovaCandidatura.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/nova_candidatura.fxml", e));
        btnStatusCandidatura.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/status_candidatura.fxml", e));
        btnConsultarCandidato
                .setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/consulta_candidato.fxml", e));
    }

    /**
     * Abre uma nova tela (FXML) e substitui a atual.
     *
     * @param caminhoFXML caminho do arquivo FXML a ser carregado
     * @param event       evento do clique
     */
    private void abrirTela(String caminhoFXML, MouseEvent event) {
        try {
            // 🔍 Teste de depuração
            System.out.println("Tentando carregar: " + caminhoFXML);
            System.out.println("Encontrado? " + (getClass().getResource(caminhoFXML) != null));

            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Candidaturas");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir tela: " + caminhoFXML);
        }
    }
}
