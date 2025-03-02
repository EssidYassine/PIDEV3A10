package Controllers.Admin.GU;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.*;
import javafx.scene.control.Cell;
import javafx.scene.layout.Pane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Models.User;
import Services.ServiceUser;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.stream.Collectors;

public class ListUsers {
    @FXML private ImageView backflech;
    @FXML private ImageView pdf;
    @FXML private VBox container;
    @FXML private TextField Recherche;
    @FXML private Pane graph; // Votre pane pour le graphique
    private BarChart barChart; // Le graphique circulaire
    private final StringProperty userStatus = new SimpleStringProperty();



    private final ServiceUser userService = new ServiceUser();
    private ObservableList<User> userList = FXCollections.observableArrayList();

    public void initialize() {
        setupBarChart(); // Configurez le graphique d'abord
        loadUserList(); // Chargez ensuite la liste des utilisateurs
        setupSearch();
        userStatus.addListener((observable, oldValue, newValue) -> refreshChart());


        backflech.setOnMouseClicked(event -> handleBack());
        backflech.setCursor(javafx.scene.Cursor.HAND);
        pdf.setOnMouseClicked(event -> exportToPDF());
        pdf.setCursor(javafx.scene.Cursor.HAND);

    }

    private int maxValue = 5;

    private void setupBarChart() {
        // Créer les axes du graphique
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Statut");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre d'utilisateurs");
        yAxis.setTickUnit(1);
        yAxis.setForceZeroInRange(true);
        yAxis.setMinorTickVisible(false);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Statistiques des utilisateurs");
        barChart.setLegendVisible(false);
        barChart.setPrefWidth(graph.getPrefWidth());
        barChart.setPrefHeight(graph.getPrefHeight());
        graph.getChildren().clear();
        graph.getChildren().add(barChart);
        initializeBarChart();
        refreshChart();
    }

    private void initializeBarChart() {
        // Initialiser les données
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        barChart.getData().add(series);

        // Créer les données pour les barres
        XYChart.Data<String, Number> activeData = new XYChart.Data<>("Actifs", 0);
        XYChart.Data<String, Number> desactiveData = new XYChart.Data<>("Désactivés", 0);

        series.getData().add(activeData);
        series.getData().add(desactiveData);

        // Appliquer les couleurs directement lors de la création des barres
        activeData.getNode().setStyle("-fx-bar-fill: green;");
        desactiveData.getNode().setStyle("-fx-bar-fill: red;");
    }

    public void refreshChart() {
        int actifs = userService.countUsersByStatus("active");
        int desactives = userService.countUsersByStatus("desactive");

        int newMax = Math.max(actifs, desactives);
        ((NumberAxis) barChart.getYAxis()).setUpperBound(newMax + 1); // Ajoute une marge de 1

        XYChart.Series<String, Number> series = (XYChart.Series<String, Number>) barChart.getData().get(0);

        XYChart.Data<String, Number> activeData = (XYChart.Data<String, Number>) series.getData().get(0);
        XYChart.Data<String, Number> desactiveData = (XYChart.Data<String, Number>) series.getData().get(1);

        activeData.setYValue(actifs);
        desactiveData.setYValue(desactives);
    }

    private void refreshChartForFilteredUsers(ObservableList<User> filteredList) {
        int actifs = (int) filteredList.stream().filter(user -> user.getIsActive().equals("active")).count();
        int desactives = (int) filteredList.stream().filter(user -> user.getIsActive().equals("desactive")).count();

        System.out.println("Actifs: " + actifs + ", Désactivés: " + desactives); // Debug

        // Met à jour la valeur supérieure de l'axe Y dynamiquement
        int newMax = Math.max(actifs, desactives);
        ((NumberAxis) barChart.getYAxis()).setUpperBound(newMax + 1); // Ajoute une marge de 1

        // Accéder à la série existante
        XYChart.Series<String, Number> series = (XYChart.Series<String, Number>) barChart.getData().get(0);

        // Assurer que les données sont bien castées
        XYChart.Data<String, Number> activeData = (XYChart.Data<String, Number>) series.getData().get(0);
        XYChart.Data<String, Number> desactiveData = (XYChart.Data<String, Number>) series.getData().get(1);

        // Met à jour les valeurs
        activeData.setYValue(actifs);
        desactiveData.setYValue(desactives);
    }

    private void filterUsers(String searchText) {
        if (searchText.isEmpty()) {
            updateDisplayedUsers(userList);
            refreshChart(); // Restaure le graphique à son état initial
        } else {
            ObservableList<User> filteredList = FXCollections.observableArrayList(
                    userList.stream()
                            .filter(user -> matchUser(user, searchText))
                            .collect(Collectors.toList())
            );
            System.out.println("Filtered users count: " + filteredList.size()); // Debug
            updateDisplayedUsers(filteredList);
            refreshChartForFilteredUsers(filteredList);
        }
    }

    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                // Titre du PDF
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750); // Réduction de l'offset pour le titre
                contentStream.showText("Liste des utilisateurs:");
                contentStream.endText();

                // Position pour le tableau
                int yPosition = 700;

                // En-têtes du tableau
                String[] headers = {"ID", "Username", "Email", "Rôle", "Statut", "Num", "DateBirth"};
                float[] columnWidths = {30, 70, 140, 80, 80, 80, 80}; // Ajustement des largeurs des colonnes

                // Dessiner l'en-tête du tableau
                drawTableHeader(contentStream, headers, columnWidths, yPosition);
                yPosition -= 20; // Espace après l'en-tête

                // Dessiner les lignes du tableau
                for (User user : userList) {
                    drawTableRow(contentStream, user, columnWidths, yPosition, headers); // Passer les en-têtes ici
                    yPosition -= 30; // Espace entre les lignes
                }

                contentStream.close();
                document.save(file);
                System.out.println("PDF exporté avec succès !");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'exportation du PDF : " + e.getMessage());
            }
        }
    }

    private void drawTableHeader(PDPageContentStream contentStream, String[] headers, float[] columnWidths, int yPosition) throws IOException {
        float xPosition = 50; // Position ajustée pour l'en-tête
        contentStream.setLineWidth(1f);
        contentStream.setNonStrokingColor(Color.LIGHT_GRAY);

        // Dessiner l'en-tête
        for (int i = 0; i < headers.length; i++) {
            contentStream.addRect(xPosition, yPosition, columnWidths[i], 20);
            xPosition += columnWidths[i];
        }
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.BLACK);

        // Afficher les en-têtes
        xPosition = 50; // Position ajustée pour l'en-tête
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        for (String header : headers) {
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition + 5, yPosition + 5); // Espace dans la cellule
            contentStream.showText(header);
            contentStream.endText();
            xPosition += columnWidths[Arrays.asList(headers).indexOf(header)];
        }
        contentStream.setFont(PDType1Font.HELVETICA, 12); // Revenir à la police normale

        // Dessiner une ligne sous l'en-tête
        contentStream.moveTo(50, yPosition); // Position ajustée pour la ligne
        contentStream.lineTo(50 + sum(columnWidths), yPosition);
        contentStream.stroke();
    }

    private void drawTableRow(PDPageContentStream contentStream, User user, float[] columnWidths, int yPosition, String[] headers) throws IOException {
        float xPosition = 50; // Position ajustée pour le contenu

        String[] userData = {
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive(),
                String.valueOf(user.getNumTel()),
                user.getDateDeNaissance() != null ? user.getDateDeNaissance().toString() : ""
        };

        Color rowColor = user.getIsActive().equals("active") ? Color.GREEN : Color.RED;
        contentStream.setNonStrokingColor(rowColor);

        contentStream.addRect(50, yPosition - 15, sum(columnWidths), 25);
        contentStream.fill();

        contentStream.setNonStrokingColor(Color.BLACK);

        for (int i = 0; i < userData.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition + 5, yPosition + 5); // Espace dans la cellule
            contentStream.showText(userData[i]);
            contentStream.endText();
            xPosition += columnWidths[i];
        }

        xPosition = 50; // Réinitialiser la position x pour les lignes verticales
        for (int i = 0; i < headers.length - 1; i++) {
            xPosition += columnWidths[i];
            contentStream.moveTo(xPosition, yPosition - 15);
            contentStream.lineTo(xPosition, yPosition + 10);
            contentStream.stroke();
        }
        contentStream.moveTo(50, yPosition - 15); // Position ajustée pour la ligne
        contentStream.lineTo(50 + sum(columnWidths), yPosition - 15);
        contentStream.stroke();
    }
    private float sum(float[] array) {
        float total = 0;
        for (float v : array) {
            total += v;
        }
        return total;
    }

    private float sum(float[] array, int start, int end) {
        float total = 0;
        for (int i = start; i < end; i++) {
            total += array[i];
        }
        return total;
    }



    private void loadUserList() {
        Set<User> users = userService.getAll();
        userList.setAll(users);
        updateDisplayedUsers(userList);
    }


    private void setupSearch() {
        Recherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers(newValue.toLowerCase());
        });
    }



    private boolean matchUser(User user, String searchText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateNaissanceStr = (user.getDateDeNaissance() != null) ? dateFormat.format(user.getDateDeNaissance()) : "";

        return user.getUsername().toLowerCase().contains(searchText) ||
                user.getEmail().toLowerCase().contains(searchText) ||
                user.getRole().toLowerCase().contains(searchText) ||
                user.getIsActive().toLowerCase().contains(searchText) ||
                String.valueOf(user.getNumTel()).contains(searchText) ||
                dateNaissanceStr.contains(searchText);
    }

    private void updateDisplayedUsers(ObservableList<User> users) {
        container.getChildren().clear();

        for (User user : users) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GU/ClientCell.fxml"));
                Parent root = loader.load();
                ClientCellController controller = loader.getController();

                // Passer la référence de ListUsers au contrôleur de cellule
                controller.setListUsersController(this);
                controller.setUserData(user);

                container.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GU/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backflech.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}