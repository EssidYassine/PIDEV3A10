package Controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class NotificationService {
    // Remplacez ces constantes par vos informations Twilio
    public static final String ACCOUNT_SID = "ACe46e6c3a6c659a5055cf410cad8269f3";
    public static final String AUTH_TOKEN = "aa95a27f4b8574ff243ee59f590313e5";
    public static final String FROM_PHONE = "+16209554013";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendNotification(String toPhone, String messageContent) {
        Message message = Message.creator(
                        new PhoneNumber(toPhone),
                        new PhoneNumber(FROM_PHONE),
                        messageContent)
                .create();
        // Vous pouvez logger ou gérer la réponse si besoin
        System.out.println("SMS envoyé : " + message.getSid());
    }
}
