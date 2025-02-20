package Controllers.Admin.GS;

import Models.Service;
import Services.ServiceService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.EventObject;

public class ServiceController {

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

    private String imageUrl = null; // Par défaut, aucune image sélectionnée

    private final ServiceService serviceService = new ServiceService();

    @FXML
    public void initialize() {
        typeServiceCombo.getItems().setAll(Service.TypeService.values());
    }

    @FXML
    private void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageUrl = file.toURI().toString();
            imageView.setImage(new Image(imageUrl));
        }
    }

    @FXML
    private void decrementerQuantite(ActionEvent event) {
        try {
            int quantite = Integer.parseInt(quantiteMaterielField.getText());
            if (quantite > 1) {
                quantite--;
                quantiteMaterielField.setText(String.valueOf(quantite));
            }
        } catch (NumberFormatException e) {
            quantiteMaterielField.setText("1"); // Valeur par défaut en cas d'erreur
        }
    }

    @FXML
    private void incrementerQuantite(ActionEvent event) {
        try {
            int quantite = Integer.parseInt(quantiteMaterielField.getText());
            quantite++;
            quantiteMaterielField.setText(String.valueOf(quantite));
        } catch (NumberFormatException e) {
            quantiteMaterielField.setText("1"); // Valeur par défaut en cas d'erreur
        }
    }

    @FXML
    private void ajouterService(ActionEvent event) {
        try {
            // Récupération des valeurs des champs
            String nomService = nomServiceField.getText().trim();
            String description = descriptionField.getText().trim();
            String prixText = prixField.getText().trim();
            Service.TypeService typeService = typeServiceCombo.getValue();
            String quantiteMaterielText = quantiteMaterielField.getText().trim();
            String roleStaff = roleStaffField.getText().trim();
            String experience = experienceField.getText().trim();
            int idUtilisateur = 1; // Remplace par l'ID réel de l'utilisateur connecté

            // Validation des entrées
            if (nomService.isEmpty() || description.isEmpty() || roleStaff.isEmpty() || experience.isEmpty() || typeService == null) {
                showErrorAlert("Champs obligatoires", "Veuillez remplir tous les champs requis.");
                return;
            }

            // Vérification du nom du service (Pas de caractères spéciaux)
            if (!nomService.matches("[a-zA-Z0-9 ]+")) {
                showErrorAlert("Nom invalide", "Le nom du service ne doit pas contenir de caractères spéciaux.");
                return;
            }

            // Vérification de la longueur de la description
            if (description.length() < 10) {
                showErrorAlert("Description trop courte", "La description doit contenir au moins 10 caractères.");
                return;
            }

            // Vérification du prix (Nombre positif)
            int prix;
            try {
                prix = Integer.parseInt(prixText);
                if (prix <= 0) {
                    showErrorAlert("Prix invalide", "Le prix doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Prix invalide", "Veuillez entrer un nombre valide pour le prix.");
                return;
            }

            // Vérification de la quantité de matériel (Nombre entier positif)
            int quantiteMateriel;
            try {
                quantiteMateriel = Integer.parseInt(quantiteMaterielText);
                if (quantiteMateriel < 0) {
                    showErrorAlert("Quantité invalide", "La quantité de matériel ne peut pas être négative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Quantité invalide", "Veuillez entrer un nombre entier pour la quantité de matériel.");
                return;
            }

            // Par défaut, on force `disponibilite = 1` lors de l'ajout
            int disponibilite = 1;

            // Création du service
            Service service = new Service(0, nomService, description, prix, typeService, disponibilite, idUtilisateur, imageUrl, quantiteMateriel, roleStaff, experience);
            serviceService.add(service);

            // Affichage d'une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Service ajouté avec succès !");
            alert.showAndWait();

            // Redirection vers la liste des services
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/GetService.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomServiceField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur SQL", "Impossible d'ajouter le service : " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger la page des services.");
        }
    }
    public void revenirFenetreGestion(ActionEvent actionEvent) {

        try {
            // Charger l'interface Service.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/GestionnaireS.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("gestionnaire");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de GestionnaireS.fxml !");
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    }

