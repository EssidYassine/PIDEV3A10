package Controllers.Client.GL;

import Models.Locaux;
import Services.LocauxService;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AcceuilLocaux {

    @FXML
    private ScrollPane scrollPaneLocaux;
    @FXML
    private Label equipementLabel;
    @FXML
    private GridPane gridPaneLocaux;
    @FXML
    private VBox detailsContainer;
    @FXML
    private ImageView imageViewLocal;
    @FXML
    private Label labelAdresse, labelCapacite, labelTarifs, labelType;
    @FXML
    private ComboBox<String> CBType;
    @FXML
    private TextField TFAdresse, TFCapacite, TFTarifs, searchField;
    @FXML
    private Button btnChoisirImage, clearSearchButton;

    private final LocauxService locauxService = new LocauxService();
    private ObservableList<Locaux> allLocaux = FXCollections.observableArrayList();
    private ObservableList<Locaux> filteredLocaux = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            List<Locaux> locauxList = locauxService.getAll();
            allLocaux.setAll(locauxList);
            filteredLocaux.setAll(locauxList);
            displayLocaux(filteredLocaux);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterLocaux() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            filteredLocaux.setAll(allLocaux);
        } else {
            filteredLocaux.setAll(allLocaux.stream()
                    .filter(local -> local.getAdresse().toLowerCase().contains(searchText) ||
                            local.getType().toLowerCase().contains(searchText))
                    .collect(Collectors.toList()));
        }
        displayLocaux(filteredLocaux);
    }
    @FXML
    private void sortLocauxByTarif() {
        filteredLocaux.sort(Comparator.comparing(Locaux::getTarifs)); // Sorting in ascending order (cheapest first)
        displayLocaux(filteredLocaux);
    }

    @FXML
    private void clearSearch() {
        searchField.setText("");
        filterLocaux();
    }

    private void displayLocaux(List<Locaux> locauxList) {
        gridPaneLocaux.getChildren().clear();
        int column = 0;
        int row = 0;

        for (Locaux local : locauxList) {
            VBox carteLocal = new VBox(10);
            carteLocal.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
            carteLocal.setPrefWidth(250);

            ImageView imageView = new ImageView(new Image(local.getPhoto()));
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
            btnReserver.setOnAction(event -> afficherReservationForm(local));

            carteLocal.getChildren().addAll(imageView, adresse, capacite, btnDetail, btnReserver);
            gridPaneLocaux.add(carteLocal, column, row);
            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private void afficherReservationForm(Locaux local) {
        gridPaneLocaux.setVisible(false);
        detailsContainer.setVisible(false);
        detailsContainer.getChildren().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GL/ReservationLocaux.fxml"));
            Parent reservationContent = loader.load();

            ReservationController reservationController = loader.getController();
            reservationController.initData(local);

            detailsContainer.getChildren().add(reservationContent);
            detailsContainer.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), detailsContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de ReservationLocaux.fxml !");
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
        detailsContainer.getChildren().clear();
    }

    public void gotoListeReservation(MouseEvent mouseEvent) {
        changerScene(mouseEvent, "/Views/Client/GL/LocauxReserves.fxml", "Locaux Réservés");
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
        changerScene(actionEvent, "/Views/Client/GL/Home1.fxml", "Gestionnaire de Locaux");
    }
}
