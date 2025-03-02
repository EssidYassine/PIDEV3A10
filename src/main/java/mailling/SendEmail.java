package mailling;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class SendEmail {

    private static final String FROM_EMAIL = "essid.yass19@gmail.com";
    private static final String PASSWORD = "khfu fsmd ijkw zvym";
    private static final String HOST = "smtp.gmail.com";
    private static final String LOGO_PATH = "C:\\Users\\yessi\\Desktop\\Eventus\\src\\main\\resources\\Images\\EVENTUS-removebg-preview.png"; // Mets le bon chemin ici

    private static Session getSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });
    }

    public static void send(String toEmail, int code) {
        try {
            Session session = getSession();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Eventus", "UTF-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("🔒 Réinitialisation de votre mot de passe");

            // Création du contenu HTML avec le logo
            String htmlContent = "<html><body>"
                    + "<div style='text-align: center;'>"
                    + "<img src='cid:logo' style='width: 150px;'><br><br>"
                    + "<h2 style='color: #2c3e50;'>Réinitialisation de votre mot de passe</h2>"
                    + "<p>Bonjour,</p>"
                    + "<p>Votre code de vérification est : <strong style='font-size: 18px;'>" + code + "</strong></p>"
                    + "<p>Veuillez l'utiliser pour réinitialiser votre mot de passe.</p>"
                    + "<br><p>Cordialement,</p><p><strong>L'équipe Eventus</strong></p>"
                    + "</div></body></html>";

            // Création des parties de l'e-mail
            MimeMultipart multipart = new MimeMultipart();

            // Partie texte HTML
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(htmlContent, "text/html; charset=UTF-8");
            multipart.addBodyPart(textPart);

            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setDataHandler(new DataHandler(new FileDataSource(LOGO_PATH)));
            imagePart.setHeader("Content-ID", "<logo>");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            multipart.addBodyPart(imagePart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✅ Email envoyé avec succès à : " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendAccountStatusEmail(String toEmail, String status, String customMessage) {
        try {
            Session session = getSession();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Eventus", "UTF-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            String subject, htmlContent;
            if ("active".equalsIgnoreCase(status)) {
                subject = "✅ Activation de votre compte";
                htmlContent = "<html><body>"
                        + "<div style='text-align: center;'>"
                        + "<img src='cid:logo' style='width: 150px;'><br><br>"
                        + "<h2 style='color: #27ae60;'>Votre compte est activé !</h2>"
                        + "<p>Bonjour,</p>"
                        + "<p>Votre compte Eventus a été activé avec succès. Vous pouvez maintenant accéder à toutes les fonctionnalités.</p>";
            } else {
                subject = "⚠️ Désactivation de votre compte";
                htmlContent = "<html><body>"
                        + "<div style='text-align: center;'>"
                        + "<img src='cid:logo' style='width: 150px;'><br><br>"
                        + "<h2 style='color: #e74c3c;'>Votre compte a été désactivé</h2>"
                        + "<p>Bonjour,</p>"
                        + "<p>Votre compte Eventus a été désactivé. Si vous pensez qu'il s'agit d'une erreur, veuillez contacter notre support.</p>";
            }

            // Ajout du message personnalisé s'il existe
            if (customMessage != null && !customMessage.trim().isEmpty()) {
                htmlContent += "<br><p style='font-style: italic; color: #555;'>Message de l'administrateur :</p>"
                        + "<blockquote style='background: #f8f8f8; padding: 10px; border-left: 4px solid #ccc;'>"
                        + customMessage + "</blockquote>";
            }

            htmlContent += "<br><p>Cordialement,</p><p><strong>L'équipe Eventus</strong></p></div></body></html>";

            message.setSubject(subject);

            // Création des parties de l'e-mail
            MimeMultipart multipart = new MimeMultipart();

            // Partie texte HTML
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(htmlContent, "text/html; charset=UTF-8");
            multipart.addBodyPart(textPart);

            // Partie image (logo)
            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setDataHandler(new DataHandler(new FileDataSource(LOGO_PATH)));
            imagePart.setHeader("Content-ID", "<logo>");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            multipart.addBodyPart(imagePart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✅ Email de " + status + " envoyé avec succès à : " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
