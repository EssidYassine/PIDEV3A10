package Services;

import Models.Reservation;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Events;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleCalendarService {

    // Méthode pour obtenir un service Google Calendar autorisé avec un compte de service.
    public static Calendar getCalendarService() throws GeneralSecurityException, IOException {
        // Spécifie le chemin vers ton fichier `service_account.json`
        String serviceAccountFilePath = "src/main/java/service_account.json"; // Modifie le chemin si nécessaire

        // Crée un Credential en utilisant un fichier de compte de service
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(serviceAccountFilePath))
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        // Crée le service Google Calendar avec les informations d'identification
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Eventus")
                .build();
    }

    // Liste les événements du calendrier principal
    public static void listEvents(Calendar service) throws IOException {
        String calendarId = "primary";  // Calendrier principal par défaut
        // Récupère les événements de la journée courante
        Events events = service.events().list(calendarId)
                .setMaxResults(10)  // Affiche les 10 premiers événements
                .setOrderBy("startTime")  // Trie par heure de début
                .setSingleEvents(true)  // Affiche les événements répétitifs en tant qu'événements distincts
                .execute();

        // Affiche les événements récupérés
        if (events.getItems().isEmpty()) {
            System.out.println("Aucun événement trouvé.");
        } else {
            for (Event event : events.getItems()) {
                System.out.printf("Événement : %s\n", event.getSummary());
                System.out.printf("Heure de début : %s\n", event.getStart().getDateTime());
            }
        }
    }

    /**
     * Crée un événement dans Google Calendar pour la réservation.
     *
     * @param reservation La réservation pour laquelle créer l'événement.
     */
    public static void createCalendarEvent(Reservation reservation) {
        try {
            // Obtient le service Google Calendar avec l'authentification du compte de service
            Calendar service = getCalendarService();

            // Crée un événement pour la réservation
            Event event = new Event()
                    .setSummary("Réservation #" + reservation.getReservationId())
                    .setDescription("Réservation effectuée pour " + reservation.getUser().getEmail());

            // Conversion de la date de réservation (Timestamp) en DateTime pour Google Calendar
            DateTime startDateTime = new DateTime(reservation.getDateReservation().getTime());

            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Europe/Paris");
            event.setStart(start);

            // Supposons une durée d'événement d'une heure.
            DateTime endDateTime = new DateTime(startDateTime.getValue() + 3600000); // +1h
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Europe/Paris");
            event.setEnd(end);

            // Insérer l'événement dans le calendrier (ici, le calendrier "primary")
            String calendarId = "primary";
            event = service.events().insert(calendarId, event).execute();

            // Affiche le lien de l'événement créé
            System.out.printf("Événement créé : %s\n", event.getHtmlLink());
            System.out.println("ID de l'événement : " + event.getId());

            // Liste les événements après l'insertion
            listEvents(service);
        } catch (GeneralSecurityException | IOException e) {
            System.err.println("Erreur lors de la création de l'événement: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
