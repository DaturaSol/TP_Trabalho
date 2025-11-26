package trabalho.recrutamento.controller;

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
import trabalho.candidatura.controller.InicioController;
import trabalho.common.controller.ProfilePageController;

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
    private Button btnRealizarEntrevistas;
    @FXML
    private Button btnGerenciarCandidaturas;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        btnGerenciarVagas.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/gerenciar_vagas.fxml", e));
        btnAgendarEntrevistas.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/agendar_entrevistas.fxml", e));
        btnRealizarEntrevistas.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/realizar_entrevista.fxml", e));
        btnSolicitarContratacoes.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/solicitar_contratacoes.fxml", e));
        btnGerenciarCandidaturas.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/inicio.fxml", e));
    }

    private Usuario currentUser;

    private void abrirTela(String caminhoFXML, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();

            //Inicializacao de usuario
            Object controller = loader.getController();

            if (caminhoFXML.contains("agendar_entrevistas.fxml")) {
                AgendarEntrevistasController ctrl = (AgendarEntrevistasController) controller;
                ctrl.initData(this.currentUser);
                
            } else if (caminhoFXML.contains("gerenciar_vagas.fxml")) {
                GerenciarVagasController ctrl = (GerenciarVagasController) controller;
                ctrl.initData(this.currentUser);

            } else if (caminhoFXML.contains("solicitar_contratacoes.fxml")) {
                SolicitarContratacoesController ctrl = (SolicitarContratacoesController) controller;
                ctrl.initData(this.currentUser);

            } else if (caminhoFXML.contains("realizar_entrevista.fxml")) {
                RealizarEntrevistaController ctrl = (RealizarEntrevistaController) controller;
                ctrl.initData(this.currentUser);

            } else if (caminhoFXML.contains("inicio.fxml")) {
                InicioController ctrl = (InicioController) controller;
                ctrl.initData(this.currentUser);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Recrutamento");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir tela: " + caminhoFXML);
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/common/profile_page.fxml"));
            Parent root = loader.load();

            ProfilePageController controller = loader.getController();
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
