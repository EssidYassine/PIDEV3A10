package mailling;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class SendEmail {

    public static void send(String toEmail, int code) {
        String from = "essid.yass19@gmail.com"; // Ton email
        String password = "khfu fsmd ijkw zvym"; // Remplace par ton mot de passe d'application généré !
        String host = "smtp.gmail.com";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587"); // Utilisation de TLS
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Activation de TLS

        // Création d'une session avec authentification
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "Eventus", "UTF-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Réinitialisation de votre mot de passe");
            message.setText("Bonjour,\n\nVotre code de vérification est : " + code + "\n\nVeuillez l'utiliser pour réinitialiser votre mot de passe.\n\nCordialement,\nL'équipe MuseMakers");

            Transport.send(message);
            System.out.println("✅ Email envoyé avec succès à : " + toEmail);
        } catch (MessagingException | UnsupportedEncodingException mex) {
            mex.printStackTrace();
        }
    }
}
