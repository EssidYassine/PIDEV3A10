package mailling;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


public class SendEmailExpo {

    public static void send(String toEmail, String dateDebut, String dateFin, String nomExpo, String timeD, String timeF,byte[] attachmentData) {
        String from = "oussama.sfaxi@esprit.tn";
        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("oussama.sfaxi@esprit.tn", "211JMT6879");
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(from, "musemakers", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Information sur les dates");

            Multipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
//            textPart.setText("Votre réservation pour l'exposition " + nomExpo + " a été confirmée.\n" +
//                    "La date de début est : " + dateDebut + "\nLa date de fin est : " + dateFin + "\n L'heure de début est : " + timeD + "\n L'heure de fin est : " + timeF);
            textPart.setText("Votre réservation pour l'exposition " + nomExpo + " a été confirmée.\n" +
                    "ci joint vous trouverez les détails de votre réservations ");
            multipart.addBodyPart(textPart);

            BodyPart pdfBodyPart = new MimeBodyPart();
            pdfBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(attachmentData, "application/pdf")));
            pdfBodyPart.setFileName("Reservation.pdf");
            multipart.addBodyPart(pdfBodyPart);


            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email sent successfully to " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
