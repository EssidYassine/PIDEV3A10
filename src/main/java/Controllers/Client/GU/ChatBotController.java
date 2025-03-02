package Controllers.Client.GU;

import Models.Reclamation;
import Models.Session;
import Models.User;
import Services.ServiceReclamation;
import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.animation.KeyFrame;

import java.io.IOException;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;




public class ChatBotController {

    @FXML
    private ImageView backflech;

    @FXML
    private TextField txtInput;

    @FXML
    private ImageView btnSend;

    @FXML
    private VBox vboxMessages;

    @FXML
    private ScrollPane scrollPaneMessages;

    @FXML
    private AnchorPane anchorPane;

    private final ServiceReclamation serviceReclamation;

    public ChatBotController() {
        this.serviceReclamation = new ServiceReclamation();
    }

    @FXML
    public void initialize() {
        backflech.setOnMouseClicked(event -> BACK());

        loadChatHistory();
        btnSend.setOnMouseClicked(event -> handleSendButton());
        startGradientAnimation();
    }

    private void BACK() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GU/HOME1.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backflech.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGradientAnimation() {
        // Définir les couleurs de départ et d'arrivée
        Color startColor1 = Color.web("#0A2540");
        Color endColor1 = Color.web("#1D3B58");
        Color startColor2 = Color.web("#56CCF2");
        Color endColor2 = Color.web("#87CEEB");

        // Créer des propriétés pour les couleurs des Stop
        ObjectProperty<Color> color1 = new SimpleObjectProperty<>(startColor1);
        ObjectProperty<Color> color2 = new SimpleObjectProperty<>(startColor2);

        // Mettre à jour le dégradé lorsque les couleurs changent
        color1.addListener((obs, oldColor, newColor) -> updateGradient(color1.get(), color2.get()));
        color2.addListener((obs, oldColor, newColor) -> updateGradient(color1.get(), color2.get()));

        // Créer une Timeline pour animer les couleurs
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(color1, startColor1),
                        new KeyValue(color2, startColor2)
                ),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(color1, endColor1),
                        new KeyValue(color2, endColor2)
                ),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(color1, startColor1),
                        new KeyValue(color2, startColor2)
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE); // Répéter indéfiniment
        timeline.play(); // Démarrer l'animation
    }

    private void updateGradient(Color color1, Color color2) {
        // Recréer le dégradé avec les nouvelles couleurs
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(1, color2));

        // Mettre à jour l'arrière-plan de l'AnchorPane
        anchorPane.setBackground(new Background(new BackgroundFill(gradient, null, null)));
    }

    private void handleSendButton() {
        String userInput = txtInput.getText().trim();

        if (!userInput.isEmpty()) {
            addMessage(userInput, true);

            String botResponse = getChatBotResponse(userInput);
            displayBotResponse(botResponse);

            // Enregistrement dans la base de données
            User currentUser = Session.getUser();
            if (currentUser != null) {
                Reclamation reclamation = new Reclamation();
                reclamation.setUserId(currentUser.getId());
                reclamation.setUserMessage(userInput);
                reclamation.setChatResponse(botResponse);
                serviceReclamation.ajouter(reclamation);
            }

            txtInput.clear();
            txtInput.requestFocus();
        }
    }

    private void addMessage(String message, boolean isUser) {
        HBox messageBox = new HBox();
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);

        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setMinWidth(Region.USE_PREF_SIZE);
        messageLabel.setPrefWidth(scrollPaneMessages.getWidth() - 50);
        messageLabel.setMinHeight(Region.USE_PREF_SIZE);

        messageLabel.setPadding(new Insets(10));

        String baseStyle = "-fx-background-radius: 15px; -fx-padding: 10px; -fx-font-size: 14px;";
        messageLabel.setStyle(baseStyle);

        if (isUser) {
            messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #0084FF; -fx-text-fill: white;");
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #E5E5EA; -fx-text-fill: black;");
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        messageBox.getChildren().add(messageLabel);
        messageBox.setPadding(new Insets(5, 10, 5, 10));

        scrollPaneMessages.widthProperty().addListener((obs, oldVal, newVal) -> {
            messageLabel.setPrefWidth(newVal.doubleValue() - 50);
        });

        vboxMessages.getChildren().add(messageBox);
        scrollToBottom();
    }

    private void loadChatHistory() {
        User currentUser = Session.getUser();
        if (currentUser != null) {
            List<Reclamation> chatHistory = serviceReclamation.getUserChatHistory(currentUser.getId());

            for (Reclamation reclamation : chatHistory) {
                addMessage(reclamation.getUserMessage(), true);
                addMessage(reclamation.getChatResponse(), false);
            }
        }
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPaneMessages.setVvalue(1.0));
    }

    private void displayBotResponse(String botResponse) {
        String[] words = botResponse.split(" ");
        StringBuilder fullResponse = new StringBuilder();

        new Thread(() -> {
            for (String word : words) {
                fullResponse.append(word).append(" ");
                String currentResponse = fullResponse.toString();

                try {
                    Thread.sleep(500); // Délai entre chaque mot
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    // Afficher le message complet du bot en un seul message
                    // Vérifie si le dernier message est un message du bot pour éviter les doublons
                    if (vboxMessages.getChildren().size() > 0) {
                        HBox lastMessageBox = (HBox) vboxMessages.getChildren().get(vboxMessages.getChildren().size() - 1);
                        Label lastMessageLabel = (Label) lastMessageBox.getChildren().get(0);
                        if (!lastMessageLabel.getText().startsWith("🤖")) { // Remplacer par un symbole ou un préfixe pour les messages du bot
                            addMessage("🤖 " + currentResponse.trim(), false);
                        } else {
                            lastMessageLabel.setText("🤖 " + currentResponse.trim()); // Mettre à jour le message existant
                        }
                    } else {
                        addMessage("🤖 " + currentResponse.trim(), false);
                    }

                    // Scroll vers le bas après chaque ajout de mot
                    scrollToBottom();
                });
            }
        }).start();
    }

    private String getChatBotResponse(String userInput) {
        String message = userInput.toLowerCase();

        if (message.contains("bonjour")) {
            return "Bonjour! Vous avez un problème ou une réclamation ? Dites-moi ce qui ne va pas, et l'administrateur le corrigera dès que possible.";
        } else if (message.contains("salut")) {
            return "Salut! Vous semblez avoir un souci. Décrivez votre problème, et nous le résoudrons rapidement.";
        } else if (message.contains("merci")) {
            return "Je vous en prie! Si vous avez d'autres réclamations, n'hésitez pas à me les faire savoir. L'administrateur y répondra.";
        } else if (message.contains("aide")) {
            return "Je suis là pour vous aider avec vos réclamations. Dites-moi ce qui ne va pas, et l'administrateur le corrigera.";
        } else if (message.contains("compte bloqué")) {
            return "Votre compte est bloqué ? L'administrateur en a été informé et le débloquera dès que possible.";
        } else if (message.contains("mot de passe oublié")) {
            return "Vous avez oublié votre mot de passe ? L'administrateur vous enverra un email pour le réinitialiser.";
        } else if (message.contains("changer email")) {
            return "Vous souhaitez changer votre adresse email ? L'administrateur mettra à jour votre email rapidement.";
        } else if (message.contains("supprimer compte")) {
            return "Vous voulez supprimer votre compte ? L'administrateur traitera votre demande sous peu.";
        } else if (message.contains("réservation annulée")) {
            return "Votre réservation a été annulée ? L'administrateur vérifiera la situation et vous contactera.";
        } else if (message.contains("modifier réservation")) {
            return "Vous voulez modifier une réservation ? L'administrateur vous aidera à apporter les changements nécessaires.";
        } else if (message.contains("payer réservation")) {
            return "Vous avez un problème pour payer une réservation ? L'administrateur vérifiera le problème et vous guidera.";
        } else if (message.contains("confirmation réservation")) {
            return "Vous n'avez pas reçu de confirmation de réservation ? L'administrateur vous enverra une confirmation sous peu.";
        } else if (message.contains("local indisponible")) {
            return "Le local que vous souhaitez réserver est indisponible ? L'administrateur vérifiera les disponibilités et vous proposera une solution.";
        } else if (message.contains("local endommagé")) {
            return "Vous avez trouvé un local endommagé ? L'administrateur en a été informé et le fera réparer rapidement.";
        } else if (message.contains("local propre")) {
            return "Le local que vous avez réservé n'est pas propre ? L'administrateur contactera l'équipe de maintenance pour résoudre ce problème.";
        } else if (message.contains("actualité incorrecte")) {
            return "Vous avez trouvé une actualité incorrecte ? L'administrateur la vérifiera et la corrigera si nécessaire.";
        } else if (message.contains("commentaire supprimé")) {
            return "Votre commentaire a été supprimé ? L'administrateur vérifiera la raison et vous contactera pour plus d'informations.";
        } else if (message.contains("like ne fonctionne pas")) {
            return "Le bouton 'Like' ne fonctionne pas ? L'administrateur vérifiera le problème et le corrigera rapidement.";
        } else if (message.contains("service indisponible")) {
            return "Le service que vous souhaitez utiliser est indisponible ? L'administrateur vérifiera la situation et vous tiendra informé.";
        } else if (message.contains("service de qualité")) {
            return "Vous n'êtes pas satisfait de la qualité d'un service ? L'administrateur prendra note de votre réclamation et améliorera le service.";
        } else if (message.contains("service annulé")) {
            return "Votre service a été annulé ? L'administrateur vérifiera la raison et vous contactera pour une solution.";
        } else if (message.contains("service retardé")) {
            return "Votre service est retardé ? L'administrateur vous tiendra informé des délais et des solutions possibles.";
        } else if (message.contains("service non reçu")) {
            return "Vous n'avez pas reçu le service que vous avez réservé ? L'administrateur vérifiera la situation et vous contactera.";
        } else if (message.contains("service mal facturé")) {
            return "Vous avez été mal facturé pour un service ? L'administrateur corrigera cette erreur et vous enverra une nouvelle facture.";
        } else if (message.contains("service recommandé")) {
            return "Vous avez une recommandation ou une plainte concernant un service ? L'administrateur en prendra note et l'utilisera pour améliorer notre offre.";
        } else if (message.contains("service personnalisé")) {
            return "Vous avez besoin d'un service personnalisé ou vous rencontrez un problème avec un service existant ? L'administrateur vous contactera pour en discuter.";
        } else if (message.contains("service urgent")) {
            return "Vous avez un problème urgent avec un service ? L'administrateur en a été informé et vous contactera rapidement pour une solution.";
        } else if (message.contains("service gratuit")) {
            return "Vous avez un problème avec un service gratuit ? L'administrateur vérifiera la situation et vous proposera une solution.";
        } else if (message.contains("service payant")) {
            return "Vous avez un problème avec un service payant ? L'administrateur vérifiera votre facture et vous contactera pour résoudre le problème.";
        } else {
            return "Désolé, je n'ai pas compris votre réclamation. Si vous avez un problème, décrivez-le plus précisément, et l'administrateur le corrigera.";
        }
    }
}