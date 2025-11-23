package trabalho.candidatura.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import trabalho.admin.model.Usuario;
import trabalho.recrutamento.controller.AgendarEntrevistasController;
import trabalho.recrutamento.controller.GerenciarVagasController;
import trabalho.recrutamento.controller.MenuRecrutamentoController;
import trabalho.recrutamento.controller.SolicitarContratacoesController;

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
    private Button backButton;

    @FXML
    public void initialize() {
        btnCadastrarCandidato
                .setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/cadastro_candidato.fxml", e));
        btnNovaCandidatura.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/nova_candidatura.fxml", e));
        btnStatusCandidatura.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/status_candidatura.fxml", e));
        btnConsultarCandidato
                .setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/consulta_candidato.fxml", e));
    }

    private Usuario currentUser;

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

            //Inicializacao de usuario
            Object controller = loader.getController();

            if (caminhoFXML.contains("status_candidatura.fxml")) {
                StatusCandidaturaController ctrl = (StatusCandidaturaController) controller;
                ctrl.initData(this.currentUser);

            } else if (caminhoFXML.contains("nova_candidatura.fxml")) {
                NovaCandidaturaController ctrl = (NovaCandidaturaController) controller;
                ctrl.initData(this.currentUser);

            } else if (caminhoFXML.contains("consulta_candidato.fxml")) {
                ConsultaCandidatoController ctrl = (ConsultaCandidatoController) controller;
                ctrl.initData(this.currentUser);

            } else if (caminhoFXML.contains("cadastro_candidato.fxml")) {
                CadastroCandidatoController ctrl = (CadastroCandidatoController) controller;
                ctrl.initData(this.currentUser);
            } 

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Candidaturas");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir tela: " + caminhoFXML);
        }
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
