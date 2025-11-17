package trabalho.recrutamento.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import trabalho.candidatura.model.Candidatura;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.recrutamento.model.Contratacao;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gestor autorizar ou rejeitar contratações
 */
public class AutorizarContratacoesController {

    @FXML private FlowPane flowResultados;
    @FXML private Button btnAtualizar;
    @FXML private Button btnVoltar;

    @FXML
    public void initialize() {
        btnAtualizar.setOnAction(e -> atualizarLista());
        atualizarLista();
    }

    private void atualizarLista() {
        flowResultados.getChildren().clear();
        List<Contratacao> pendentes = getContratacoesPendentes();
        
        if (pendentes.isEmpty()) {
            Label vazio = new Label("Nenhuma solicitação pendente.");
            flowResultados.getChildren().add(vazio);
            return;
        }
        
        for (Contratacao c : pendentes) {
            Candidatura candidatura = getCandidatura(c.getCandidatoCpf());
            if (candidatura == null) continue;
            
            Label lbl = new Label(
                candidatura.getCandidato().getPessoa().getNome() + " — " +
                c.getRegimeContratacao() + " — " +
                "Solicitado em: " + c.getDataSolicitacao()
            );
            lbl.setPrefWidth(400);
            
            Button btnAutorizar = new Button("Autorizar");
            btnAutorizar.setOnAction(e -> autorizar(c));
            
            Button btnRejeitar = new Button("Rejeitar");
            btnRejeitar.setOnAction(e -> rejeitar(c));
            
            FlowPane linha = new FlowPane();
            linha.setHgap(5);
            linha.getChildren().addAll(lbl, btnAutorizar, btnRejeitar);
            
            flowResultados.getChildren().add(linha);
        }
    }

    private void autorizar(Contratacao c) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Autorização");
        confirmacao.setHeaderText("Autorizar contratação?");
        
        Candidatura candidatura = getCandidatura(c.getCandidatoCpf());
        if (candidatura != null) {
            confirmacao.setContentText(
                "Candidato: " + candidatura.getCandidato().getPessoa().getNome() + "\n" +
                "Regime: " + c.getRegimeContratacao()
            );
        }
        
        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    c.autorizar("GESTOR_CPF");
                    
                    JsonDataManager dataManager = JsonDataManager.getInstance();
                    dataManager.saveData();
                    
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", 
                        "Contratação autorizada! Candidato pode ser cadastrado no módulo Financeiro.");
                    atualizarLista();
                    
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao autorizar: " + e.getMessage());
                }
            }
        });
    }

    private void rejeitar(Contratacao c) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rejeitar Contratação");
        dialog.setHeaderText("Informe o motivo da rejeição:");
        dialog.setContentText("Motivo:");
        
        dialog.showAndWait().ifPresent(motivo -> {
            if (motivo != null && !motivo.trim().isEmpty()) {
                try {
                    c.rejeitar("GESTOR_CPF", motivo);
                    
                    JsonDataManager dataManager = JsonDataManager.getInstance();
                    dataManager.saveData();
                    
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Rejeitado", 
                        "Contratação rejeitada.");
                    atualizarLista();
                    
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao rejeitar: " + e.getMessage());
                }
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Informe o motivo da rejeição.");
            }
        });
    }

    private List<Contratacao> getContratacoesPendentes() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        return appData.getContratacoes().stream()
            .filter(Contratacao::isPendente)
            .collect(Collectors.toList());
    }

    private Candidatura getCandidatura(String cpf) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        return appData.getCandidaturas().stream()
            .filter(c -> c.getCpfCnpjCandidato().equals(cpf))
            .findFirst()
            .orElse(null);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/recrutamento/menu_recrutamento.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu Recrutamento");
        stage.show();
    }
}
