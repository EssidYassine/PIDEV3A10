package Controllers.Admin.GL;

import Services.LocauxService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class StatisticsLocal implements Initializable {

    @FXML
    private PieChart pieChartLocaux;

    private final LocauxService locauxService = new LocauxService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLocauxStatistics();
    }
    @FXML
    private void loadLocauxStatistics() {
        try {
            pieChartLocaux.getData().clear(); // Clear existing data

            // Get locaux count by type from the service
            Map<String, Integer> locauxData = locauxService.getLocauxCountByType();

            // Add data to the pie chart with labels showing the number of locals
            for (Map.Entry<String, Integer> entry : locauxData.entrySet()) {
                String label = entry.getKey() + " (" + entry.getValue() + ")"; // Format: "Type (Number)"
                PieChart.Data data = new PieChart.Data(label, entry.getValue());
                pieChartLocaux.getData().add(data);
            }
        } catch (SQLException e) {
            showError("Erreur SQL", "Impossible de charger les statistiques : " + e.getMessage());
        }
    }
    @FXML
    public void returnHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/GestionnaireLocaux.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de locaux ");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilLocaux.fxml !");
        }
    }


    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
