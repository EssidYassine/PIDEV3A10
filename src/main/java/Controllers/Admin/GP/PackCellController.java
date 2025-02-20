package Controllers.Admin.GP;

import Models.Pack;
import Services.ServiceGP;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

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
    private Label titleLabel;
    @FXML
    private Button deleteButton;

    private Pack pack;
    private ObservableList<Pack> packObservableList;

    @FXML
    private VBox packVBox; // Déclaration du VBox


    public void setPack(Pack pack, ObservableList<Pack> packObservableList) {
        this.pack = pack;
        this.packObservableList = packObservableList;

        if (pack != null) {
            NamePack.setText(pack.getNom());
            TypePack.setText(pack.getType());
            DescPack.setText(pack.getDescription());
            PrixPack.setText(pack.getPrix().toString());
            NbrInvPack.setText(String.valueOf(pack.getNbreInvitesMax()));
            BudgetPack.setText(pack.getBudgetPrevu().toString());
            datePack.setText(pack.getDateEvenement().toString());
            LieuPack.setText(pack.getLieu());
            titleLabel.setText(pack.getStatut());
        }
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        if (pack != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Suppression du pack");
            alert.setContentText("Voulez-vous vraiment supprimer le pack : " + pack.getNom() + " ?");

            alert.showAndWait().ifPresent(response -> {
                if (response.getText().equals("OK")) {
                    ServiceGP serviceGP = new ServiceGP();
                    serviceGP.supprimer(pack); // Appel de la méthode de suppression du service

                    // Supprimer le pack de la ObservableList
                    packObservableList.remove(pack);


                    // Afficher une alerte de succès
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Suppression réussie");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Le pack a été supprimé avec succès !");
                    successAlert.show();
                }
            });
        }
    }
}
