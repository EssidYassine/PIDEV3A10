package Controllers.Client.GU;

import Models.Reclamation;
import Models.Session;
import Models.User;
import Services.ServiceReclamation;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

import java.util.List;

public class ChatBotController {

    @FXML
    private TextField txtInput;

    @FXML
    private ImageView btnSend;

    @FXML
    private VBox vboxMessages; // Zone où les messages seront affichés

    @FXML
    private ScrollPane scrollPaneMessages; // Ajout du ScrollPane

    private final ServiceReclamation serviceReclamation;

    public ChatBotController() {
        this.serviceReclamation = new ServiceReclamation();
    }

    @FXML
    public void initialize() {
        loadChatHistory(); // Charger l'historique des messages au démarrage
        btnSend.setOnMouseClicked(event -> handleSendButton());
    }

    private void handleSendButton() {
        String userInput = txtInput.getText().trim();

        if (!userInput.isEmpty()) {
            // Affichage du message utilisateur (aligné à droite)
            addMessage(userInput, true);

            // Obtenir et afficher la réponse du bot (aligné à gauche)
            String botResponse = getChatBotResponse(userInput);
            addMessage(botResponse, false);

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

            scrollToBottom();
        }
    }

    private void addMessage(String message, boolean isUser) {
        HBox messageBox = new HBox();
        Label messageLabel = new Label(message);

        // Permettre l'affichage complet du texte
        messageLabel.setWrapText(true); // Active le retour à la ligne automatique
        messageLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP); // Empêche les "..."

        // Définir une largeur dynamique et illimitée en hauteur
        messageLabel.setMaxWidth(Double.MAX_VALUE); // Largeur maximale
        messageLabel.setMinWidth(Region.USE_PREF_SIZE); // Assurer que le texte prend l'espace nécessaire
        messageLabel.setPrefWidth(scrollPaneMessages.getWidth() - 50); // Ajustement dynamique
        messageLabel.setMinHeight(Region.USE_PREF_SIZE); // Hauteur dynamique selon le texte

        messageLabel.setPadding(new Insets(10));

        // Appliquer un style différent pour l'utilisateur et le bot
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

        // Ajouter un écouteur pour ajuster la largeur quand la fenêtre change
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
                addMessage(reclamation.getUserMessage(), true);  // Message de l'utilisateur
                addMessage(reclamation.getChatResponse(), false); // Réponse du chatbot
            }
        }
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPaneMessages.setVvalue(1.0));
    }

    private String getChatBotResponse(String userInput) {
        String message = userInput.toLowerCase();

        if (message.contains("bonjour")) {
            return "Bonjour! Comment puis-je vous aider?";
        } else if (message.contains("aide")) {
            return "Bien sûr! De quoi avez-vous besoin?";
        } else {
            return "Désolé, je n'ai pas compris. Pouvez-vous reformuler?";
        }
    }
}
