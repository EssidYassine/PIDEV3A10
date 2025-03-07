package Controllers.Client.GS;

import Models.Service;
import Services.ServiceService;
import Tools.DataBaseConnection;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class AcceuilService {
    @FXML
    private GridPane gridPaneServices;

    @FXML
    private VBox detailsContainer;
    @FXML
    private ImageView imageService;
    @FXML
    private Label labelNomService;
    @FXML
    private Label labelDescription;
    @FXML
    private Label labelPrix;
    @FXML
    private TextField searchBar;
    @FXML
    private Label star1, star2, star3, star4, star5;
    @FXML
    private Label moyenneRating;
    @FXML
    public void rate1() { updateRating(1); }
    @FXML
    public void rate2() { updateRating(2); }
    @FXML
    public void rate3() { updateRating(3); }
    @FXML
    public void rate4() { updateRating(4); }
    @FXML
    public void rate5() { updateRating(5); }



    private int currentRating = 0;
    private Service serviceSelectionne;

    private final ServiceService serviceService = new ServiceService();
    private List<Service> listeDesServices;

    @FXML
    public void initialize() {
        try {
            // Chargement des services depuis la base de données
            listeDesServices = serviceService.getAll();

            int column = 0;
            int row = 0;

            for (Service service : listeDesServices) {
                VBox carteService = new VBox(10);

                // Construire le style de base pour la carte
                String baseStyle = "-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; " +
                        "-fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";
                // Si la quantité est 0, on réduit l'opacité pour assombrir la carte
                if (service.getQuantite_materiel() == 0) {
                    baseStyle += " -fx-opacity: 0.20;";
                }
                carteService.setStyle(baseStyle);
                carteService.setPrefWidth(250);

                // Création de l'indicateur de disponibilité (un cercle)
                Circle availabilityIndicator = new Circle(7); // rayon de 7 pixels
                if (service.getQuantite_materiel() > 0) {
                    availabilityIndicator.setFill(Color.GREEN);
                } else {
                    availabilityIndicator.setFill(Color.RED);
                }
                // Regrouper le cercle dans un HBox (vous pouvez ajouter un label si souhaité)
                HBox indicatorBox = new HBox(5, availabilityIndicator);

                // Création de l'image du service
                ImageView imageView = new ImageView();
                try {
                    imageView.setImage(new Image(service.getImage_url()));
                } catch(Exception e) {
                    // Si l'image ne peut être chargée, on laisse vide
                }
                imageView.setFitWidth(230);
                imageView.setFitHeight(150);

                // Nom du service
                Label nomService = new Label(service.getNom_service());
                nomService.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #333;");

                // Prix du service
                Label prixService = new Label(service.getPrix() + " DT");
                prixService.setStyle("-fx-font-size: 16; -fx-text-fill: #1e0fc6; -fx-font-weight: bold;");
// Calcul et affichage de la moyenne des ratings pour chaque service
                double moyenne = calculerMoyenne(service);
                Label moyenneRatingListe = new Label(String.format("%.1f ★", moyenne));
                moyenneRatingListe.setStyle("-fx-font-size: 14; -fx-text-fill: gold; -fx-font-weight: bold;");


                // Bouton Détail
                Button btnDetail = new Button("Détail");
                btnDetail.setStyle("-fx-background-color: #ed4e00; -fx-text-fill: white; -fx-background-radius: 10;");
                btnDetail.setOnAction(event -> afficherDetails(service));

                // Ajout des éléments dans la carte
                carteService.getChildren().addAll(indicatorBox, imageView, nomService, prixService,moyenneRatingListe, btnDetail);

                // Ajout de la carte dans le GridPane
                gridPaneServices.add(carteService, column, row);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherDetails(Service service) {
        gridPaneServices.setVisible(false);
        detailsContainer.setVisible(true);
        serviceSelectionne = service;
        labelNomService.setText(service.getNom_service());
        labelDescription.setText(service.getDescription());
        labelPrix.setText(service.getPrix() + " DT");
        imageService.setImage(new Image(service.getImage_url()));
        // Charger la note moyenne
        double moyenne = calculerMoyenne(service);
        moyenneRating.setText(String.format("%.1f ★", moyenne));

        // Activer le rating pour l'utilisateur
        updateRating((int) moyenne);
        // Animation de transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), detailsContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void retourListeServices() {
        detailsContainer.setVisible(false);
        gridPaneServices.setVisible(true);
    }

    public void gotoAcceuilService(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/AcceuilService.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void retourHome1(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void gotoReserverMateriel(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ReserverMateriel.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void gotoReserverStaff(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ReserverStaff.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void gotoListeReservation(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ListeReservationService.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void gotoHome(MouseEvent mouseEvent) {
        try {
            // Charger l'interface AcceuilService.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void filtrerServices() {
        // Récupérer le texte de recherche
        String texteRecherche = searchBar.getText().toLowerCase();

        // Vider le GridPane avant de le re-remplir
        gridPaneServices.getChildren().clear();

        int column = 0;
        int row = 0;

        // Parcourir tous les services
        for (Service service : listeDesServices) {
            // Si le nom du service contient le texte recherché, on l'affiche
            if (service.getNom_service().toLowerCase().contains(texteRecherche)) {
                VBox carteService = new VBox(10);

                String baseStyle = "-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; " +
                        "-fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";

                if (service.getQuantite_materiel() == 0) {
                    baseStyle += " -fx-opacity: 0.20;";
                }

                carteService.setStyle(baseStyle);
                carteService.setPrefWidth(250);

                // Création de l'indicateur de disponibilité (un cercle)
                Circle availabilityIndicator = new Circle(7);
                if (service.getQuantite_materiel() > 0) {
                    availabilityIndicator.setFill(Color.GREEN);
                } else {
                    availabilityIndicator.setFill(Color.RED);
                }

                HBox indicatorBox = new HBox(5, availabilityIndicator);

                // Création de l'image du service
                ImageView imageView = new ImageView();
                try {
                    imageView.setImage(new Image(service.getImage_url()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageView.setFitWidth(230);
                imageView.setFitHeight(150);

                // Nom du service
                Label nomService = new Label(service.getNom_service());
                nomService.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #333;");

                // Prix du service
                Label prixService = new Label(service.getPrix() + " DT");
                prixService.setStyle("-fx-font-size: 16; -fx-text-fill: #1e0fc6; -fx-font-weight: bold;");

                // Bouton Détail
                Button btnDetail = new Button("Détail");
                btnDetail.setStyle("-fx-background-color: #1e0fc6; -fx-text-fill: white; -fx-background-radius: 10;");
                btnDetail.setOnAction(event -> afficherDetails(service));

                // Ajout des éléments dans la carte
                carteService.getChildren().addAll(indicatorBox, imageView, nomService, prixService, btnDetail);

                // Ajout de la carte dans le GridPane
                gridPaneServices.add(carteService, column, row);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        }
    }
    private double calculerMoyenne(Service service) {
        double moyenne = 0;
        try {
            Connection conn = DataBaseConnection.getMyDataBase().getConnection();
            String sql = "SELECT AVG(rating) AS moyenne FROM rating WHERE service_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, service.getId_service());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                moyenne = rs.getDouble("moyenne");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moyenne;
    }
    private void updateRating(int rating) {
        currentRating = rating;
        Label[] stars = { star1, star2, star3, star4, star5 };

        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setText("★");
                stars[i].setStyle("-fx-text-fill: gold;");
            } else {
                stars[i].setText("☆");
                stars[i].setStyle("-fx-text-fill: gray;");
            }
        }
        enregistrerEvaluation(rating);

        }


    private void enregistrerEvaluation(int rating) {
        try {
            Connection conn = DataBaseConnection.getMyDataBase().getConnection();

            // Vérifier si l'utilisateur a déjà évalué ce service
            String checkSql = "SELECT COUNT(*) AS count FROM rating WHERE user_id = ? AND service_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, 1); // ID utilisateur (remplace par un ID dynamique)
            checkStmt.setInt(2, serviceSelectionne.getId_service());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt("count") > 0) {
                // L'utilisateur a déjà évalué ce service, on affiche un message d'erreur
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Évaluation déjà existante");
                alert.setHeaderText(null);
                alert.setContentText("❌ Vous avez déjà noté ce service !");
                alert.showAndWait();
                return; // On arrête ici, pas d'ajout en base
            }

            // Si aucun rating n'existe, on insère la nouvelle évaluation
            String sql = "INSERT INTO rating (user_id, service_id, rating) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 1); // ID utilisateur (remplace par un ID dynamique)
            stmt.setInt(2, serviceSelectionne.getId_service());
            stmt.setInt(3, rating);
            stmt.executeUpdate();

            // Mettre à jour la moyenne affichée
            double nouvelleMoyenne = calculerMoyenne(serviceSelectionne);
            moyenneRating.setText(String.format("%.1f ★", nouvelleMoyenne));

            // Afficher un message de confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Évaluation enregistrée");
            alert.setHeaderText(null);
            alert.setContentText("⭐ Merci pour votre évaluation !");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }}




