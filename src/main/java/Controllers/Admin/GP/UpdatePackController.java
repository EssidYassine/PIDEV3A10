package Controllers.Admin.GP;

import Models.Pack;
import Services.ServiceGP;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UpdatePackController {

    @FXML
    private Button btnEnregistrer;

    @FXML
    private ComboBox<?> cbLieu;

    @FXML
    private ComboBox<?> cbType;

    @FXML
    private DatePicker dpDateEvenement;

    @FXML
    private VBox servicesContainer;

    @FXML
    private TextArea taDescription;

    @FXML
    private TextField tfBudgetPrevu;

    @FXML
    private TextField tfNbreInvitesMax;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfPrix;

    private final ServiceGP serviceGP = new ServiceGP();

    private Pack selectedPack;

    @FXML
    private void handleUpdate(ActionEvent event) {
        try {
            // Vérification si un pack est bien sélectionné
            if (selectedPack == null) {
                showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner un pack à modifier !");
                return;
            }

            // Vérification des champs vides
            if (tfNom.getText().isEmpty() || taDescription.getText().isEmpty() ||
                    tfPrix.getText().isEmpty() || tfNbreInvitesMax.getText().isEmpty() ||
                    tfBudgetPrevu.getText().isEmpty() || dpDateEvenement.getValue() == null ||
                    cbType.getValue() == null || cbLieu.getValue() == null) {

                showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs !");
                return;
            }

            // Récupération des nouvelles valeurs
            String nom = tfNom.getText();
            String type = (String) cbType.getValue();
            String lieu = (String) cbLieu.getValue();
            LocalDate dateEvenement = dpDateEvenement.getValue();
            int nbreInvitesMax = Integer.parseInt(tfNbreInvitesMax.getText());
            BigDecimal prix = BigDecimal.valueOf(Double.parseDouble(tfPrix.getText()));
            BigDecimal budgetPrevu = BigDecimal.valueOf(Double.parseDouble(tfBudgetPrevu.getText()));
            String description = taDescription.getText();

            // Récupération des services sélectionnés
            List<String> selectedServices = new ArrayList<>();
            for (Node node : servicesContainer.getChildren()) {
                if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                    selectedServices.add(checkBox.getText());
                }
            }

            if (selectedServices.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner au moins un service !");
                return;
            }

            // Mise à jour de l'objet Pack
            selectedPack.setNom(nom);
            selectedPack.setType(type);
            selectedPack.setDescription(description);
            selectedPack.setPrix(prix);
            selectedPack.setNbreInvitesMax(nbreInvitesMax);
            selectedPack.setBudgetPrevu(budgetPrevu);
            selectedPack.setDateEvenement(dateEvenement);
            selectedPack.setLieu(lieu);

            // Enregistrement des modifications
            serviceGP.modifier(selectedPack);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le pack a été modifié avec succès !");

            // Rediriger vers la page Pack.fxml après la modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GP/Pack.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer des valeurs numériques valides !");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la modification du pack.");
            System.out.println(e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
