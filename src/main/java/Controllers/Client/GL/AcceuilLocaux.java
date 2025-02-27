package Controllers.Client.GL;

import Models.Locaux;
import Services.LocauxService;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AcceuilLocaux {
    public ScrollPane scrollPaneLocaux;
    public Label equipementLabel;
    @FXML
    private GridPane gridPaneLocaux;
    @FXML
    private VBox detailsContainer;
    @FXML
    private ImageView imageViewLocal;
    @FXML
    private Label labelAdresse, labelCapacite, labelTarifs, labelType, labelEquipements;
    @FXML
    private ComboBox<String> CBType;
    @FXML
    private TextField TFAdresse, TFCapacite, TFTarifs;
    @FXML
    private Button btnChoisirImage;
    @FXML

    private final LocauxService locauxService = new LocauxService();
    private List<Locaux> listeDesLocaux;

    @FXML
    public void initialize() {
        try {
            listeDesLocaux = locauxService.getAll();

            int column = 0;
            int row = 0;

            for (Locaux local : listeDesLocaux) {
                VBox carteLocal = new VBox(10);
                carteLocal.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
                carteLocal.setPrefWidth(250);

                ImageView imageView = new ImageView();
                imageView.setImage(new Image(local.getPhoto()));
                imageView.setFitWidth(230);
                imageView.setFitHeight(150);

                Label adresse = new Label(local.getAdresse());
                adresse.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #333;");

                Label capacite = new Label("Capacité: " + local.getCapacite());
                capacite.setStyle("-fx-font-size: 16; -fx-text-fill: #1e0fc6; -fx-font-weight: bold;");

                Button btnDetail = new Button("Détail");
                btnDetail.setStyle("-fx-background-color: #1e0fc6; -fx-text-fill: white; -fx-background-radius: 10;");
                btnDetail.setOnAction(event -> afficherDetails(local));

                Button btnReserver = new Button("Réserver");
                btnReserver.setStyle("-fx-background-color: #1e0fc6; -fx-text-fill: white; -fx-background-radius: 10;");
                btnReserver.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GL/ReservationLocaux.fxml"));
                        Parent root = loader.load();

                        // Pass selected local information to the Reservation Controller
                        ReservationController reservationController = loader.getController();
                        reservationController.initData(local); // Pass the local object

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Réserver un Local");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Erreur de chargement de Reservation.fxml !");
                    }
                });


                carteLocal.getChildren().addAll(imageView, adresse, capacite, btnDetail,btnReserver );
                gridPaneLocaux.add(carteLocal, column, row);
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

    private void afficherDetails(Locaux local) {
        gridPaneLocaux.setVisible(false);
        detailsContainer.setVisible(true);

        labelAdresse.setText(local.getAdresse());
        labelCapacite.setText("Capacité: " + local.getCapacite());
        labelTarifs.setText("Tarif: " + local.getTarifs() + " DT");
        labelType.setText("Type: " + local.getType());
        imageViewLocal.setImage(new Image(local.getPhoto()));
        equipementLabel.setText("Équipement: " + local.getEquipement());




        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), detailsContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void retourListeLocaux() {
        detailsContainer.setVisible(false);
        gridPaneLocaux.setVisible(true);
    }

    public void gotoAcceuilLocaux(MouseEvent mouseEvent) {
        changerScene(mouseEvent, "/Views/Client/GL/AcceuilLocaux.fxml", "Gestionnaire de Locaux");
    }

    public void retourHome(ActionEvent actionEvent) {
        changerScene(actionEvent, "/Views/Client/GL/Home.fxml", "Accueil");
    }

    private void changerScene(Event event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de " + fxmlPath + " !");
        }
    }
    public void retourHome1(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GL/Home1.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Locaux");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilLocaux.fxml !");
        }
    }
}
