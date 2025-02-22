package Controllers.Admin.GP;

import Models.Pack;
import Services.ServiceGP;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class PackCellController {

    @FXML
    private Label BudgetPack;
    @FXML
    private Label DescPack;
    @FXML
    private Label LieuPack;
    @FXML
    private Label NamePack;
    @FXML
    private Label NbrInvPack;
    @FXML
    private Label PrixPack;
    @FXML
    private Label ServPack;
    @FXML
    private Label TypePack;
    @FXML
    private Label datePack;
    @FXML
    private Label statusPack;
    @FXML
    private Button deleteButton;
    @FXML
    private Button updateButton; // Bouton pour modifier le pack

    private Pack pack;
    private ServiceGP serviceGP = new ServiceGP();

    @FXML
    private VBox packVBox;

    public void initialize() {
        deleteButton.setOnAction(event -> {
            if (pack != null) {
                supprimerPack(pack.getId());
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erreur");
                alert.setHeaderText("Aucun pack sélectionné");
                alert.setContentText("Veuillez sélectionner un pack à supprimer.");
                alert.showAndWait();
            }
        });
    }

    @FXML
    private Circle statusCircle;

    // Modifier la méthode setStatus
    public void setStatus(boolean isActive) {
        if(statusCircle == null) return; // Sécurité

        if(isActive) {
            statusCircle.setFill(Color.LIMEGREEN);
            statusCircle.setStroke(Color.DARKGREEN);
        } else {
            statusCircle.setFill(Color.TOMATO);
            statusCircle.setStroke(Color.DARKRED);
        }
    }



    private void supprimerPack(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer le pack avec l'ID : " + id + " ?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceGP.supprimer(id);
                // Recharger la vue après suppression
                Stage stage = (Stage) deleteButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GP/Pack.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText("Une erreur est survenue lors de la suppression du pack.");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    public void setPack(Pack pack) {
        this.pack = pack;
        if (pack != null) {
            System.out.println("Pack sélectionné : " + pack.getNom() + " avec ID : " + pack.getId());
            NamePack.setText(pack.getNom());
            TypePack.setText(pack.getType());
            DescPack.setText(pack.getDescription());
            PrixPack.setText(pack.getPrix().toString());
            NbrInvPack.setText(String.valueOf(pack.getNbreInvitesMax()));
            BudgetPack.setText(pack.getBudgetPrevu().toString());
            datePack.setText(pack.getDateEvenement().toString());
            LieuPack.setText(pack.getLieu());
            setStatus(pack.isActive());

        } else {
            System.out.println("⚠ Aucun pack sélectionné !");
            NamePack.setText("");
            TypePack.setText("");
            DescPack.setText("");
            PrixPack.setText("");
            NbrInvPack.setText("");
            BudgetPack.setText("");
            datePack.setText("");
            LieuPack.setText("");
            statusPack.setText("");
        }
    }


    @FXML
    private void handleUpdateClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GP/UpdatePack.fxml"));
            Parent root = loader.load();

            UpdatePackController updatePackController = loader.getController();
            updatePackController.initData(pack);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier le Pack");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader parentLoader = new FXMLLoader(getClass().getResource("/Views/Admin/GP/Pack.fxml"));
            Parent parentRoot = parentLoader.load();
            currentStage.setScene(new Scene(parentRoot));
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
