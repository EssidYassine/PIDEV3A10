package Controllers.Admin.GP;

import Models.Reservation;
import Models.User;
import Services.ReservationGP;
import Services.NotificationService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ScrollPane;


public class AdminCalendarController implements Initializable {

    @FXML private GridPane calendarGrid;
    @FXML private Label labelWeekRange;
    @FXML private DatePicker datePicker;
    @FXML private VBox detailsContainer;
    @FXML private ComboBox<Reservation.StatutReservation> filterComboBox;
    @FXML private VBox notificationContainer;
    @FXML private ScrollPane calendarScrollPane; // Référence directe au ScrollPane

    private LocalDate currentWeekStart;
    private final ReservationGP reservationService = new ReservationGP();
    private final NotificationService notificationService = new NotificationService();
    private LocalDateTime lastNotificationCheck = LocalDateTime.now();
    private static final DateTimeFormatter HEADER_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        setupStatusFilter();
        updateWeekLabel();
        setupGridStructure();
        buildCalendar();
        checkForNewReservations();
    }

    private void setupStatusFilter() {
        filterComboBox.getItems().setAll(Reservation.StatutReservation.values());
        filterComboBox.getItems().add(null);
        filterComboBox.setPromptText("Filtrer par statut");
        filterComboBox.setOnAction(e -> buildCalendar());
    }

    private void setupGridStructure() {
        calendarGrid.getChildren().clear();
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        ColumnConstraints timeCol = new ColumnConstraints();
        timeCol.setPrefWidth(90);
        calendarGrid.getColumnConstraints().add(timeCol);

        for (int i = 0; i < 7; i++) {
            ColumnConstraints dayCol = new ColumnConstraints();
            dayCol.setPrefWidth(150);
            dayCol.setHgrow(Priority.SOMETIMES);
            calendarGrid.getColumnConstraints().add(dayCol);
        }

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
            Reservation.StatutReservation filter = filterComboBox.getValue();
            List<Reservation> reservations = reservationService.getReservationsByWeekAndStatus(currentWeekStart, filter);
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

        String color = switch (reservation.getStatutReservation()) {
            case CONFIRMÉE -> "#C8E6C9";
            case EN_ATTENTE -> "#FFF3E0";
            case ANNULÉE -> "#FFCDD2";
            default -> "#E3F2FD";
        };

        eventBox.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");
        Label titleLabel = new Label("Réservation #" + reservation.getReservationId());
        titleLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        Button detailsBtn = new Button("Détails");
        detailsBtn.setStyle("-fx-font-size: 9px; -fx-padding: 2 5 2 5;");
        detailsBtn.setOnAction(e -> showReservationDetails(reservation.getReservationId()));

        eventBox.getChildren().addAll(titleLabel, detailsBtn);
        GridPane.setMargin(eventBox, new Insets(1));
        calendarGrid.add(eventBox, dayColumn, startRow);
    }


    private void checkForNewReservations() {
        // Vérifier immédiatement les 5 dernières minutes au démarrage
        checkNow();

        // Vérifier toutes les 30 secondes
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> checkNow()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void checkNow() {
        try {
            // SUPPRIMER TOUTE NOTIFICATION DE TEST ICI
            List<Reservation> newReservations = notificationService.getNewReservations();
            newReservations.forEach(this::showNotification);
        } catch (Exception e) {
            System.err.println("Erreur de vérification : " + e.getMessage());
        }
    }

    private void showNotification(Reservation reservation) {
        Platform.runLater(() -> {
            VBox notification = new VBox(5);
            notification.setStyle("-fx-background-color: #fff3e0; -fx-padding: 10; -fx-background-radius: 5;");

            Label title = new Label("Nouvelle réservation!");
            title.setStyle("-fx-font-weight: bold;");

            String dateStr = reservation.getDateReservation().toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String content = String.format(
                    "ID: %d\nPack: %d\nClient: %s\nDate: %s",
                    reservation.getReservationId(),
                    reservation.getPackId(),
                    reservation.getUser().getEmail(),
                    dateStr
            );

            Label contentLabel = new Label(content);
            contentLabel.setWrapText(true);

            // Bouton de navigation
            Button goToButton = new Button("Voir");
            goToButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 5 2 5;");
            goToButton.setOnAction(e -> navigateToReservation(reservation));

            HBox buttonContainer = new HBox(goToButton);
            buttonContainer.setAlignment(Pos.CENTER_RIGHT);

            notification.getChildren().addAll(title, contentLabel, buttonContainer);

            // Auto-suppression après 1 minute
            PauseTransition delay = new PauseTransition(Duration.minutes(1));
            delay.setOnFinished(e -> notificationContainer.getChildren().remove(notification));
            delay.play();

            notificationContainer.getChildren().add(0, notification);
        });
    }

    private void navigateToReservation(Reservation reservation) {
        LocalDateTime reservationDate = reservation.getDateReservation().toLocalDateTime();

        // Mettre à jour la semaine courante
        currentWeekStart = reservationDate.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Mettre à jour l'interface
        updateWeekLabel();
        setupGridStructure();
        buildCalendar();

        // Faire défiler jusqu'au créneau horaire
        scrollToTimeSlot(reservationDate);
    }

    private void scrollToTimeSlot(LocalDateTime dateTime) {
        int targetDay = dateTime.getDayOfWeek().getValue();
        int targetRow = calculateRow(dateTime);

        Platform.runLater(() -> {
            // Attendre que le calendrier soit rendu
            PauseTransition delay = new PauseTransition(Duration.millis(100));
            delay.setOnFinished(e -> {
                for (Node node : calendarGrid.getChildren()) {
                    Integer rowIndex = GridPane.getRowIndex(node);
                    Integer columnIndex = GridPane.getColumnIndex(node);

                    if (rowIndex != null && columnIndex != null
                            && rowIndex == targetRow && columnIndex == targetDay) {

                        // Calcul précis de la position
                        Bounds nodeBounds = node.getBoundsInParent();
                        Bounds viewportBounds = calendarScrollPane.getViewportBounds();
                        double scrollY = (nodeBounds.getMinY() - viewportBounds.getHeight()/2)
                                / calendarGrid.getHeight();

                        calendarScrollPane.setVvalue(Math.min(scrollY, 1.0));
                        break;
                    }
                }
            });
            delay.play();
        });
    }

    private int calculateRow(LocalDateTime dateTime) {
        return (dateTime.getHour() * 2) + (dateTime.getMinute() / 30) + 1;
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
        }
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

    private void updateWeekLabel() {
        LocalDate endOfWeek = currentWeekStart.plusDays(6);
        labelWeekRange.setText(String.format(
                "Semaine du %s au %s",
                currentWeekStart.format(HEADER_DATE_FORMATTER),
                endOfWeek.format(HEADER_DATE_FORMATTER)
        ));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handlBack(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/Pack.fxml");
    }
    private void navigateToScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}