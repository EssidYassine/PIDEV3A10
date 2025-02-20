package Controllers.Admin.GP;

import Models.Pack;
import Services.ServiceGP;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreatePackController implements Initializable {

    @FXML
    private TextField tfNom, tfNbreInvitesMax, tfPrix, tfBudgetPrevu;

    @FXML
    private TextArea taDescription;

    @FXML
    private ComboBox<String> cbType, cbLieu;

    @FXML
    private DatePicker dpDateEvenement;

    @FXML
    private VBox servicesContainer;

    @FXML
    private Button btnEnregistrer;

    private final ServiceGP serviceGP = new ServiceGP();

    private Pack selectedPack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadStaticServices();
        loadComboBoxData();
    }


    private void loadStaticServices() {
        // Liste statique des services disponibles
        String[] services = {"Décoration", "Traiteur", "Musique", "Photographie", "Transport"};

        // Ajouter les CheckBox statiques
        for (String s : services) {
            CheckBox checkBox = new CheckBox(s);
            servicesContainer.getChildren().add(checkBox);
        }

    }

    @FXML
    private void loadComboBoxData() {
        // Remplir les ComboBox avec des valeurs fictives (à remplacer par des données de la BDD si nécessaire)
        cbType.getItems().addAll("Fête", "Mariage", "Conférence");
        cbLieu.getItems().addAll("Tunis", "Sousse", "Sfax", "Djerba");
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        try {
            // Vérification des champs vides
            if (tfNom.getText().isEmpty() || taDescription.getText().isEmpty() ||
                    tfPrix.getText().isEmpty() || tfNbreInvitesMax.getText().isEmpty() ||
                    tfBudgetPrevu.getText().isEmpty() || dpDateEvenement.getValue() == null ||
                    cbType.getValue() == null || cbLieu.getValue() == null) {

                showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs !");
                return;
            }

            // Récupération des valeurs
            String nom = tfNom.getText();
            String type = cbType.getValue();
            String lieu = cbLieu.getValue();
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

            // Création et enregistrement du Pack
            Pack newPack = new Pack(nom, type, description, prix, nbreInvitesMax, budgetPrevu, dateEvenement, lieu, "actif");
            serviceGP.ajouter(newPack, new ArrayList<>());

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le pack a été ajouté avec succès !");

            // Rediriger vers la page initiale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GP/Pack.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer des valeurs numériques valides !");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'ajout du pack.");
            System.out.println(e.getMessage());
        }
    }


    private void clearForm() {
        tfNom.clear();
        tfNbreInvitesMax.clear();
        tfPrix.clear();
        tfBudgetPrevu.clear();
        taDescription.clear();
        cbType.setValue(null);
        cbLieu.setValue(null);
        dpDateEvenement.setValue(null);

        // Désélectionner toutes les CheckBox
        for (Node node : servicesContainer.getChildren()) {
            if (node instanceof CheckBox checkBox) {
                checkBox.setSelected(false);
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setSelectedPack(Pack pack) {
        this.selectedPack = pack;

        // Remplir les champs avec les données du pack sélectionné
        tfNom.setText(pack.getNom());
        tfNbreInvitesMax.setText(String.valueOf(pack.getNbreInvitesMax()));
        tfPrix.setText(pack.getPrix().toString());
        tfBudgetPrevu.setText(pack.getBudgetPrevu().toString());
        taDescription.setText(pack.getDescription());
        cbType.setValue(pack.getType());
        cbLieu.setValue(pack.getLieu());
        dpDateEvenement.setValue(pack.getDateEvenement());


        // Changer le texte du bouton pour indiquer qu'on modifie un pack
        btnEnregistrer.setText("Modifier");
    }

}
