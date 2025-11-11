package trabalho.common.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class TelaInicialController {

    @FXML
    private Button btnCadastrarCandidato;

    // Método chamado ao clicar no botão "Fazer Login"
    @FXML
    private void abrirTelaLogin(ActionEvent event) {
        try {
            // Carrega o próximo FXML (ajuste o caminho conforme seu projeto)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/login.fxml"));
            Parent root = loader.load();

            // Obtém a janela atual e muda de cena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login do Sistema");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao abrir a tela de login.");
        }
    }
}
