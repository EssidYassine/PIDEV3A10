package Controllers.Admin.GS;

import Models.Service;
import Models.User;
import Services.ServiceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class CrudService {

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

    @FXML
    private GridPane gridPaneServices;

    private String imageUrl = null;

    private final ServiceService serviceService = new ServiceService();

    @FXML
    public void initialize() {
        typeServiceCombo.getItems().setAll(Service.TypeService.values());
        try {
            chargerServices();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur SQL", "Impossible de charger les services : " + e.getMessage());
        }
    }

    /**
     * Méthode pour choisir une image depuis le système de fichiers.
     */
    @FXML
    private void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        // Utilisation de la fenêtre parent pour une meilleure intégration
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            imageUrl = file.toURI().toString();
            imageView.setImage(new Image(imageUrl));
        }
    }

    /**
     * Méthode pour ajouter un nouveau service.
     */
    @FXML
    private void ajouterService(ActionEvent event) {
        try {
            String nomService = nomServiceField.getText().trim();
            String description = descriptionField.getText().trim();
            String prixText = prixField.getText().trim();
            Service.TypeService typeService = typeServiceCombo.getValue();
            String quantiteMaterielText = quantiteMaterielField.getText().trim();
            String roleStaff = roleStaffField.getText().trim();
            String experience = experienceField.getText().trim();

            // Exemple d'utilisateur (à remplacer par l'utilisateur connecté)
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

            if (nomService.isEmpty() || description.isEmpty() || typeService == null ||
                    prixText.isEmpty() || quantiteMaterielText.isEmpty()) {
                showErrorAlert("Champs obligatoires", "Veuillez remplir tous les champs requis.");
                return;
            }

            int prix = Integer.parseInt(prixText);
            int quantiteMateriel = Integer.parseInt(quantiteMaterielText);
            int disponibilite = 1;

            Service service = new Service(0, nomService, description, prix, typeService, disponibilite,
                    utilisateur, imageUrl, quantiteMateriel, roleStaff, experience);

            serviceService.add(service);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Service ajouté avec succès !");
            alert.showAndWait();

            chargerServices();
            resetForm();

        } catch (NumberFormatException nfe) {
            showErrorAlert("Erreur de format", "Veuillez saisir des valeurs numériques valides pour le prix et la quantité.");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur SQL", "Impossible d'ajouter le service : " + e.getMessage());
        }
    }

    /**
     * Méthode pour charger et afficher les services dans le GridPane.
     */
    private void chargerServices() throws SQLException {
        gridPaneServices.getChildren().clear();
        List<Service> services = serviceService.getAll();
        int row = 0;
        int column = 0;

        for (Service service : services) {
            VBox card = new VBox();
            card.setSpacing(10);
            card.setStyle("-fx-background-color: #1f2c50; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");

            // Image du service
            ImageView imgView = new ImageView();
            imgView.setFitWidth(150);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);

            try {
                String serviceImageUrl = service.getImageUrl();
                if (serviceImageUrl != null && !serviceImageUrl.isEmpty()) {
                    // Si l'URL ne commence pas par "http:" ou "file:", on ajoute "file:///" et on remplace les antislashs par des slashs
                    if (!serviceImageUrl.startsWith("http") && !serviceImageUrl.startsWith("file:")) {
                        serviceImageUrl = "file:///" + serviceImageUrl.replace("\\", "/");
                    }
                    Image image = new Image(serviceImageUrl, true);
                    if (image.getException() == null) {
                        imgView.setImage(image);
                    } else {
                        throw image.getException();
                    }
                } else {
                    // Chargement de l'image par défaut depuis les ressources
                    String defaultImagePath = "/Images/default.png";
                    URL defaultUrl = getClass().getResource(defaultImagePath);
                    if (defaultUrl != null) {
                        String defaultImageUrl = defaultUrl.toExternalForm();
                        imgView.setImage(new Image(defaultImageUrl, true));
                    } else {
                        System.err.println("Default image resource not found: " + defaultImagePath);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image : " + e.getMessage());
                // Tentative de charger l'image par défaut si une erreur survient
                String defaultImagePath = "/Images/default.png";
                URL defaultUrl = getClass().getResource(defaultImagePath);
                if (defaultUrl != null) {
                    String defaultImageUrl = defaultUrl.toExternalForm();
                    imgView.setImage(new Image(defaultImageUrl, true));
                } else {
                    System.err.println("Default image resource not found: " + defaultImagePath);
                }
            }

            card.getChildren().add(imgView);

            // Nom du service (utilisation du getter adapté, ici getNom_service())
            Label nomLabel = new Label(service.getNom_service());
            nomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            card.getChildren().add(nomLabel);

            // Prix du service
            Label prixLabel = new Label("Prix : " + service.getPrix() + " €");
            card.getChildren().add(prixLabel);

            gridPaneServices.add(card, column, row);
            column++;
            if (column == 2) {
                column = 0;
                row++;
            }
        }
    }

    /**
     * Réinitialisation du formulaire.
     */
    private void resetForm() {
        nomServiceField.clear();
        descriptionField.clear();
        prixField.clear();
        typeServiceCombo.setValue(null);
        quantiteMaterielField.clear();
        roleStaffField.clear();
        experienceField.clear();
        imageView.setImage(null);
        imageUrl = null;
    }

    /**
     * Affichage d'une alerte d'erreur.
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Navigation vers la fenêtre d'accueil.
     */
    public void revenirFenetreHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de Home.fxml !");
        }
    }

    public void retourAfficherService(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/AfficherService.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de Home.fxml !");
        }
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
}
