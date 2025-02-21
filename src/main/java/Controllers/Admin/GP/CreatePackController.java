package Controllers.Admin.GP;

import Models.Locaux;
import Models.Pack;
import Models.Service;
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
    private ComboBox<String> cbType;

    @FXML
    private ComboBox<Locaux> cbLieu;

    @FXML
    private DatePicker dpDateEvenement;

    @FXML
    private VBox servicesContainer;

    @FXML
    private Button btnEnregistrer;

    private final ServiceGP serviceGP = new ServiceGP();

    private Pack selectedPack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadComboBoxData();
        loadDynamicServices();
    }

    private void loadDynamicServices() {
        List<Service> services = serviceGP.getAllServices(); // Récupère tous les services depuis la base
        System.out.println("Services récupérés : " + services.size()); // Vérifie s'ils sont bien récupérés

        servicesContainer.getChildren().clear(); // Vide le VBox avant de recharger

        for (Service s : services) {
            CheckBox checkBox = new CheckBox(s.getNom_service()); // Afficher seulement le nom
            checkBox.setUserData(s.getId_service()); // Stocker l'ID pour l'enregistrement
            servicesContainer.getChildren().add(checkBox); // Ajouter la CheckBox dans le VBox

            System.out.println("Ajout du service : " + s.getNom_service() + " (ID: " + s.getId_service() + ")");
        }
    }

    @FXML
    private void loadComboBoxData() {
        cbType.getItems().addAll("Fête", "Mariage", "Conférence");

        List<Locaux> locaux = serviceGP.getAllLocaux();
        cbLieu.getItems().clear();
        cbLieu.getItems().addAll(locaux);
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        try {
            // Vérification des champs obligatoires
            if (tfNom.getText().isEmpty() || taDescription.getText().isEmpty() ||
                    tfPrix.getText().isEmpty() || tfNbreInvitesMax.getText().isEmpty() ||
                    tfBudgetPrevu.getText().isEmpty() || dpDateEvenement.getValue() == null ||
                    cbType.getValue() == null || cbLieu.getValue() == null) {

                showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs !");
                return;
            }

            // Récupération des valeurs du formulaire
            String nom = tfNom.getText();
            String type = cbType.getValue();
            Locaux selectedLocal = cbLieu.getValue();
            LocalDate dateEvenement = dpDateEvenement.getValue();
            int nbreInvitesMax = Integer.parseInt(tfNbreInvitesMax.getText());
            BigDecimal prix = BigDecimal.valueOf(Double.parseDouble(tfPrix.getText()));
            BigDecimal budgetPrevu = BigDecimal.valueOf(Double.parseDouble(tfBudgetPrevu.getText()));
            String description = taDescription.getText();

            // Récupération des services sélectionnés (via leur id stocké dans userData)
            List<Integer> selectedServiceIds = new ArrayList<>();
            for (Node node : servicesContainer.getChildren()) {
                if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                    selectedServiceIds.add((Integer) checkBox.getUserData());
                }
            }

            if (selectedServiceIds.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner au moins un service !");
                return;
            }

            // Création du pack – ici, on stocke dans le champ "lieu" l'adresse du local sélectionné
            Pack newPack = new Pack(nom, type, description, prix, nbreInvitesMax, budgetPrevu, dateEvenement, selectedLocal.getAdresse(), "actif");

            // Appel de la méthode d'ajout en passant le pack, la liste des services et l'id du local
            serviceGP.ajouter(newPack, selectedServiceIds, selectedLocal.getIdLocal());

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le pack a été ajouté avec succès !");

            // Rediriger vers la page principale (par exemple Pack.fxml)
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
        // Remplir les champs si modification (logique similaire à l'ajout)
        tfNom.setText(pack.getNom());
        tfNbreInvitesMax.setText(String.valueOf(pack.getNbreInvitesMax()));
        tfPrix.setText(pack.getPrix().toString());
        tfBudgetPrevu.setText(pack.getBudgetPrevu().toString());
        taDescription.setText(pack.getDescription());
        cbType.setValue(pack.getType());
        // Pour le lieu, il faudrait retrouver le Locaux correspondant (selon l'adresse stockée)
        dpDateEvenement.setValue(pack.getDateEvenement());
        btnEnregistrer.setText("Modifier");
    }
}
