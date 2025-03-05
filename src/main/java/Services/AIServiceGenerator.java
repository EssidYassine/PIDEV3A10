package Services;

import Models.Pack;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

public class AIServiceGenerator {
    // Utilisez un modèle mieux adapté au JSON
    private static final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";
    private static final String API_KEY = System.getenv("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"); // Stockez la clé dans les variables d'environnement

    public Pack generatePack(String prompt) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Structurez le prompt pour un meilleur résultat
        String formattedPrompt = """
            [INSTRUCTION]
            Génère un pack événementiel au format JSON strict avec ces champs :
            {
              "nom": "string",
              "type": "Mariage|Conférence|Fête|Autre",
              "description": "text",
              "prix": "decimal",
              "nbreInvitesMax": "integer",
              "budgetPrevu": "decimal",
              "dateEvenement": "yyyy-MM-dd",
              "lieu": "string",
              "statut": "actif|inactif|archivé",
              "services": "service1,service2,service3"
            }
            [DEMANDE]
            """ + prompt;

        // Construction sécurisée du JSON
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("inputs", formattedPrompt);
        requestBody.putObject("parameters")
                .put("max_length", 500)
                .put("return_full_text", false);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur API (" + response.statusCode() + "): " + response.body());
        }

        return parseResponse(response.body());
    }

    private Pack parseResponse(String jsonResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);

        // Validation de la structure
        if (!rootNode.isArray() || rootNode.isEmpty()) {
            throw new RuntimeException("Réponse API invalide");
        }

        String generatedText = rootNode.get(0).get("generated_text").asText()
                .replaceAll("[^\\x20-\\x7E]", ""); // Nettoyage des caractères non imprimables

        try {
            JsonNode packData = mapper.readTree(generatedText);

            return new Pack(
                    packData.path("nom").asText(),
                    packData.path("type").asText(),
                    packData.path("description").asText(),
                    new BigDecimal(packData.path("prix").asText("0.00")),
                    packData.path("nbreInvitesMax").asInt(0),
                    new BigDecimal(packData.path("budgetPrevu").asText("0.00")),
                    LocalDate.parse(packData.path("dateEvenement").asText("1970-01-01")),
                    packData.path("lieu").asText(),
                    packData.path("statut").asText("actif"),
                    Arrays.asList(packData.path("services").asText("").split(","))
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur de parsing JSON généré: " + generatedText, e);
        }
    }
}