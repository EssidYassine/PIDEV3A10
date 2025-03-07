package Controllers.Admin.GS;

import Models.Service;
import Models.User;
import Services.ServiceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class ModifierService {

    @FXML
    private TextField nomServiceField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField prixField;

    @FXML
    private ComboBox<Service.TypeService> typeServiceCombo;

    @FXML
    private TextField quantiteMaterielField;

    @FXML
    private TextField roleStaffField;

    @FXML
    private TextField experienceField;

    @FXML
    private ImageView imageView;

    private String imageUrl;
    private int serviceId; // Identifiant du service à modifier

    private final ServiceService serviceService = new ServiceService();

    @FXML
    public void initialize() {
        // Remplir le ComboBox avec les types de service disponibles
        typeServiceCombo.getItems().setAll(Service.TypeService.values());
    }

    /**
     * Préremplit le formulaire avec les données du service à modifier.
     */
    public void setServiceToModify(Service service) {
        serviceId = service.getId_service();
        nomServiceField.setText(service.getNom_service());
        descriptionField.setText(service.getDescription());
        prixField.setText(String.valueOf(service.getPrix()));
        typeServiceCombo.setValue(service.getType_service());
        quantiteMaterielField.setText(String.valueOf(service.getQuantite_materiel()));
        roleStaffField.setText(service.getRole_staff());
        experienceField.setText(service.getExperience());
        imageUrl = service.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageView.setImage(new Image(imageUrl));
        }
    }

    /**
     * Permet de choisir une nouvelle image depuis le système de fichiers.
     */
    @FXML
    private void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            imageUrl = file.toURI().toString();
            imageView.setImage(new Image(imageUrl));
        }
    }

    /**
     * Modifie le service avec les données du formulaire.
     */
    @FXML
    private void modifierService(ActionEvent event) {
        try {
            String nomService = nomServiceField.getText().trim();
            String description = descriptionField.getText().trim();
            String prixText = prixField.getText().trim();
            Service.TypeService typeService = typeServiceCombo.getValue();
            String quantiteText = quantiteMaterielField.getText().trim();
            String roleStaff = roleStaffField.getText().trim();
            String experience = experienceField.getText().trim();

            if (nomService.isEmpty() || description.isEmpty() || typeService == null ||
                    prixText.isEmpty() || quantiteText.isEmpty()) {
                showErrorAlert("Champs obligatoires", "Veuillez remplir tous les champs requis.");
                return;
            }

            int prix = Integer.parseInt(prixText);
            int quantite = Integer.parseInt(quantiteText);
            int disponibilite = 1; // Par défaut ou selon votre logique

            // Exemple d'utilisateur (remplacez par l'utilisateur connecté)
            User utilisateur = new User(
                    1,                      // id
                    "JeanDupont",           // username
                    "jean.dupont@example.com", // email
                    "emna",                 // password
                    "admin",                 // role
                    true,                   // isActive (true ou false)
                    "0600000000",           // numTel
                    null                    // dateDeNaissance (LocalDate) - vous pouvez mettre une date si besoin
            );
            Service service = new Service(serviceId, nomService, description, prix, typeService, disponibilite,
                    utilisateur, imageUrl, quantite, roleStaff, experience);

            serviceService.update(service);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Service modifié avec succès !");
            alert.showAndWait();

            // Revenir à la liste des services après modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/AfficherService.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Services");
            stage.show();

        } catch (NumberFormatException nfe) {
            showErrorAlert("Erreur de format", "Veuillez saisir des valeurs numériques valides pour le prix et la quantité.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de modifier le service : " + e.getMessage());
        }
    }

    /**
     * Affiche une alerte d'erreur.
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void gotohome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/Home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void gotocrudservice(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/CrudService.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void gotoafficherservice(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/ModifierService.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }
}
