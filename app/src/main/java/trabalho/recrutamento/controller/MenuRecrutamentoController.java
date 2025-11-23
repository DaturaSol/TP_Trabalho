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
    private Button btnGerenciarCandidaturas;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        btnGerenciarVagas.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/gerenciar_vagas.fxml", e));
        btnAgendarEntrevistas.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/agendar_entrevistas.fxml", e));
        btnSolicitarContratacoes.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/recrutamento/solicitar_contratacoes.fxml", e));
        btnGerenciarCandidaturas.setOnMouseClicked(e -> abrirTela("/trabalho/fxml/candidatura/inicio.fxml", e));
    }

    private void abrirTela(String caminhoFXML, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();

            //Inicializacao de usuario
            Object controller = loader.getController();

            if (caminhoFXML.contains("agendar_entrevistas.fxml")) {
                // Se for AgendarEntrevistas, faça o cast para AgendarEntrevistasController
                AgendarEntrevistasController ctrl = (AgendarEntrevistasController) controller;
                ctrl.initData(this.currentUser);
                
            } else if (caminhoFXML.contains("gerenciar_vagas.fxml")) {
                // Se for GerenciarVagas, faça o cast para GerenciarVagasController
                GerenciarVagasController ctrl = (GerenciarVagasController) controller;
                ctrl.initData(this.currentUser);

            } else if (caminhoFXML.contains("solicitar_contratacoes.fxml")) {
                // Se for SolicitarContratacoes
                SolicitarContratacoesController ctrl = (SolicitarContratacoesController) controller;
                ctrl.initData(this.currentUser);

            } else if (caminhoFXML.contains("inicio.fxml")) {
                // Se for o Menu de Candidaturas (InicioController)
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

    private Usuario currentUser;

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
