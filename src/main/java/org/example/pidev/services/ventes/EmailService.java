package org.example.pidev.services.ventes;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service d'envoi d'emails via SMTP Gmail
 * Utilisé pour envoyer des coupons de réduction aux clients inactifs
 */
public class EmailService {

    // Veuillez configurer ces valeurs avec votre adresse Gmail et mot de passe d'application
    private final String username = "ahmed.bouss.2004@gmail.com";  // Remplacez par votre email Gmail
    private final String password = "emvelvgcwrcgfbxe";  // Remplacez par votre mot de passe d'application Gmail

    /**
     * Envoie un email contenant un coupon de réduction à un client
     *
     * @param destinataire Email du client
     * @param nomClient Nom du client
     * @param codeCoupon Code/Montant du coupon à offrir
     * @throws Exception Si l'envoi de l'email échoue
     */
    public void sendCouponEmail(String destinataire, String nomClient, String codeCoupon) throws Exception {
        // Vérification que les identifiants sont configurés (pas de placeholders)
        if ("VOTRE_EMAIL@gmail.com".equals(username) || "VOTRE_MOT_DE_PASSE_APPLICATION".equals(password)) {
            throw new RuntimeException("Veuillez configurer votre email et mot de passe d'application Gmail dans EmailService.java");
        }

        // Vérification que l'adresse email du destinataire est valide
        if (destinataire == null || destinataire.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse email du destinataire ne peut pas être vide");
        }

        // Configuration du serveur SMTP de Gmail avec TLS (Port 587)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        // Création de la session SMTP sécurisée
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Création du message email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject("Une surprise vous attend chez SMART FARM !");

            // Corps du message HTML avec formatage attrayant
            String contenu = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head><meta charset='UTF-8'></head>"
                    + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                    + "<h1 style='color: #2d5a27;'>Bonjour " + htmlEscape(nomClient) + ",</h1>"
                    + "<p style='font-size: 16px;'>Vous nous manquez à la ferme ! 🌾</p>"
                    + "<p style='font-size: 14px;'>Nous avons remarqué que cela fait un moment que vous ne nous avez pas visités.</p>"
                    + "<p style='font-size: 14px;'>Pour vous remercier de votre fidélité et vous encourager à revenir, "
                    + "voici un coupon de réduction exceptionnel :</p>"
                    + "<div style='background-color: #d4edda; border: 2px solid #28a745; border-radius: 10px; "
                    + "padding: 20px; text-align: center; margin: 20px 0;'>"
                    + "<p style='font-size: 12px; margin: 0; color: #155724;'>VOTRE CODE COUPON</p>"
                    + "<h2 style='color: #28a745; margin: 10px 0; font-size: 32px;'>" + htmlEscape(codeCoupon) + "</h2>"
                    + "</div>"
                    + "<p style='font-size: 14px;'>✅ Valide immédiatement en magasin</p>"
                    + "<p style='font-size: 14px;'>À très bientôt chez SMART FARM !</p>"
                    + "<br><hr style='border: none; border-top: 1px solid #ddd;'>"
                    + "<p style='font-size: 12px; color: #666;'>L'équipe SMART FARM<br>"
                    + "Votre ferme écologique de confiance 🌱</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            message.setContent(contenu, "text/html; charset=utf-8");

            // Envoi du message
            Transport.send(message);
            System.out.println("[EmailService] ✅ Email envoyé avec succès à : " + destinataire);

        } catch (MessagingException e) {
            System.err.println("[EmailService] ❌ Erreur lors de l'envoi de l'email à " + destinataire + " : " + e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email à " + destinataire + ". Vérifiez les paramètres SMTP.", e);
        }
    }

    /**
     * Échappe les caractères spéciaux HTML pour éviter les injections
     */
    private String htmlEscape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}