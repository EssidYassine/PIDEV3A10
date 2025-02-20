package Controllers.Admin.GL;


import Models.Locaux;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DetailController {

    @FXML
    private ImageView localImage;

    @FXML
    private Label adresseLabel;

    @FXML
    private Label capaciteLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label tarifsLabel;

    @FXML
    private Label equipementLabel;

    private Locaux local;

    public void setLocal(Locaux local) {
        this.local = local;
        afficherDetails();
    }

    private void afficherDetails() {
        if (local != null) {
            // Afficher l'image du local
            if (local.getPhoto() != null && !local.getPhoto().isEmpty()) {
                localImage.setImage(new Image(local.getPhoto()));
            }

            // Afficher les détails du local
            adresseLabel.setText("Adresse: " + local.getAdresse());
            capaciteLabel.setText("Capacité: " + local.getCapacite());
            typeLabel.setText("Type: " + local.getType());
            tarifsLabel.setText("Tarifs: " + local.getTarifs() + " TND");
            equipementLabel.setText("Équipement: " + local.getEquipement());
        }
    }

    @FXML
    private void retour() {
        // Retour à la page précédente
        localImage.getScene().getWindow().hide();
    }
}