package Controllers.Admin.GP;

import Models.Pack;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Services.AIServiceGenerator;
import Services.ServiceGP;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GeneratePackController {
    // Déclaration de tous les champs FXML
    @FXML private TextArea promptArea;
    @FXML private TextField nomField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextArea descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField invitesField;
    @FXML private TextField budgetField;
    @FXML private TextField dateField;
    @FXML private TextField lieuField;

    private final AIServiceGenerator aiService = new AIServiceGenerator();
    private final ServiceGP packService = new ServiceGP();

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll("Mariage", "Conférence", "Fête", "Autre");
    }

    @FXML
    private void handleGenerate() {
        try {
            Pack pack = aiService.generatePack(promptArea.getText());
            // Remplissage de tous les champs
            nomField.setText(pack.getNom());
            typeCombo.setValue(pack.getType());
            descriptionField.setText(pack.getDescription());
            prixField.setText(pack.getPrix().toString());
            invitesField.setText(pack.getNbreInvitesMax().toString());
            budgetField.setText(pack.getBudgetPrevu().toString());
            dateField.setText(pack.getDateEvenement().toString());
            lieuField.setText(pack.getLieu());
        } catch (Exception e) {
            showAlert("Erreur de génération: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            Pack pack = new Pack(
                    nomField.getText(),
                    typeCombo.getValue(),
                    descriptionField.getText(),
                    new BigDecimal(prixField.getText()),
                    Integer.parseInt(invitesField.getText()),
                    new BigDecimal(budgetField.getText()),
                    LocalDate.parse(dateField.getText()),
                    lieuField.getText(),
                    "actif"
            );

            packService.add(pack);
            showAlert("Pack sauvegardé avec succès!");
        } catch (NumberFormatException e) {
            showAlert("Format numérique invalide: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur de sauvegarde: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }
}