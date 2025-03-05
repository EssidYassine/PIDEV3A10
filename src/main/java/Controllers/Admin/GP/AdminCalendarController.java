package Controllers.Admin.GP;

import Models.Reservation;
import Services.ReservationGP;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.ResourceBundle;

public class AdminCalendarController implements Initializable {

    @FXML private GridPane calendarGrid;
    @FXML private Label labelWeekRange;
    @FXML private DatePicker datePicker;
    @FXML private VBox detailsContainer; // Conteneur pour les détails

    private LocalDate currentWeekStart;
    private final ReservationGP reservationService = new ReservationGP();
    private static final DateTimeFormatter HEADER_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateWeekLabel();
        setupGridStructure();
        buildCalendar();
    }

    private void setupGridStructure() {
        calendarGrid.getChildren().clear();
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        // Colonne des heures
        ColumnConstraints timeCol = new ColumnConstraints();
        timeCol.setPrefWidth(90);
        calendarGrid.getColumnConstraints().add(timeCol);

        // Colonnes des jours
        for (int i = 0; i < 7; i++) {
            ColumnConstraints dayCol = new ColumnConstraints();
            dayCol.setPrefWidth(150);
            dayCol.setHgrow(Priority.SOMETIMES);
            calendarGrid.getColumnConstraints().add(dayCol);
        }

        // Configuration des lignes
        RowConstraints headerRow = new RowConstraints();
        headerRow.setPrefHeight(40);
        calendarGrid.getRowConstraints().add(headerRow);

        for (int i = 0; i < 48; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(40);
            row.setMinHeight(40);
            calendarGrid.getRowConstraints().add(row);
        }
    }

    private void buildCalendar() {
        // Désactiver temporairement le GridPane
        calendarGrid.setDisable(true);

        Platform.runLater(() -> {
            try {
                setupGridStructure();
                createHeaderRow();
                createTimeColumn();
                createDayColumns();
                loadReservations();
            } finally {
                calendarGrid.setDisable(false);
            }
        });
    }

    private void createHeaderRow() {
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            Label dayLabel = new Label(date.format(HEADER_DATE_FORMATTER));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10; -fx-background-color: #f5f5f5;");
            GridPane.setMargin(dayLabel, new Insets(0, 0, 5, 0));
            calendarGrid.add(dayLabel, i + 1, 0);
        }
    }

    private void createTimeColumn() {
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                int rowIndex = (hour * 2) + (minute / 30) + 1;
                Label timeLabel = new Label(String.format("%02d:%02d", hour, minute));
                timeLabel.setStyle("-fx-font-size: 10px; -fx-padding: 0 5 0 5;");
                GridPane.setMargin(timeLabel, Insets.EMPTY);
                calendarGrid.add(timeLabel, 0, rowIndex);
            }
        }
    }

    private void createDayColumns() {
        for (int day = 0; day < 7; day++) {
            for (int row = 1; row <= 48; row++) {
                StackPane timeSlot = new StackPane();
                timeSlot.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                timeSlot.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0.2;");
                calendarGrid.add(timeSlot, day + 1, row);
            }
        }
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationService.getReservationsByWeek(currentWeekStart);

            for (Reservation res : reservations) {
                addReservationEvent(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addReservationEvent(Reservation reservation) {
        LocalDateTime dateTime = reservation.getDateReservation().toLocalDateTime();
        int dayColumn = dateTime.getDayOfWeek().getValue();
        int startRow = calculateRow(dateTime);

        VBox eventBox = new VBox(2);
        eventBox.setPadding(new Insets(2));
        eventBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        eventBox.setStyle("-fx-background-color: #e3f2fd; "
                + "-fx-border-color: #90caf9; "
                + "-fx-border-radius: 3; "
                + "-fx-background-radius: 3;");

        Label titleLabel = new Label("Réservation #" + reservation.getReservationId());
        titleLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        System.out.println("Réservation #" + reservation.getReservationId() + " -> Statut: " + reservation.getStatutReservation());


        String color = switch (reservation.getStatutReservation()) {
            case CONFIRMÉE -> "#C8E6C9"; // Vert
            case EN_ATTENTE -> "#FFF3E0"; // Orange
            case ANNULÉE -> "#FFCDD2"; // Rouge
            default -> "#E3F2FD"; // Bleu clair par défaut
        };
        Button detailsBtn = new Button("Détails");
        detailsBtn.setStyle("-fx-font-size: 9px; -fx-padding: 2 5 2 5;");
        detailsBtn.setOnAction(e -> showReservationDetails(reservation.getReservationId()));


        eventBox.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");

        eventBox.getChildren().addAll(titleLabel, detailsBtn);
        GridPane.setMargin(eventBox, new Insets(1));
        calendarGrid.add(eventBox, dayColumn, startRow);
    }

    private int calculateRow(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        return (hour * 2) + (minute / 30) + 1;
    }

    private void showReservationDetails(int reservationId) {
        try {
            detailsContainer.getChildren().clear();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GP/cardReservationcellule.fxml"));
            Parent cardRoot = loader.load();

            CardReservationController controller = loader.getController();
            Reservation reservation = reservationService.getById(reservationId);

            if (reservation != null) {
                controller.setReservationData(reservation);
                // Ajoutez ce listener pour rafraîchir le calendrier
                controller.setOnRefreshListener(() -> {
                    setupGridStructure();
                    buildCalendar();
                });
                controller.setOnDeleteListener(() -> {
                    setupGridStructure();
                    buildCalendar();
                    detailsContainer.getChildren().clear();
                });
                detailsContainer.getChildren().add(cardRoot);
            }

        } catch (IOException | SQLException ex) {
            showAlert("Erreur", "Impossible d'afficher les détails : " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void updateWeekLabel() {
        LocalDate endOfWeek = currentWeekStart.plusDays(6);
        labelWeekRange.setText(String.format(
                "Semaine du %s au %s",
                currentWeekStart.format(HEADER_DATE_FORMATTER),
                endOfWeek.format(HEADER_DATE_FORMATTER)
        ));
    }

    @FXML
    private void handleNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateWeekLabel();
        setupGridStructure();
        buildCalendar();
    }

    @FXML
    private void handlePreviousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateWeekLabel();
        setupGridStructure();
        buildCalendar();
    }

    @FXML
    private void handleDateSearch() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            currentWeekStart = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            updateWeekLabel();
            setupGridStructure();
            buildCalendar();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}