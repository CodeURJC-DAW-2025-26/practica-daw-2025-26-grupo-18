package es.codeurjc.scam_g18.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    // Envía un correo de texto simple.
    private void sendMail(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("scam.noreply67@gmail.com");
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);

        mailSender.send(mensaje);
        System.out.println("¡Correo enviado con éxito!");
    }

    // Envía un correo con un archivo adjunto.
    private void sendMailWithAttachment(String destinatario, String asunto, String cuerpo, byte[] attachment,
            String attachmentName) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("scam.noreply67@gmail.com");
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo, false);
            helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

            mailSender.send(mimeMessage);
            System.out.println("¡Correo con adjunto enviado con éxito!");
        } catch (MessagingException e) {
            throw new RuntimeException("No se pudo enviar el correo con adjunto", e);
        }
    }

    // Envía el mensaje de bienvenida al crear una cuenta nueva.
    public void newAccountMessage(String newUserMail, String newUsername) {
        String message = """
                ¡Hola %s!

                Estamos muy emocionados de tenerte con nosotros. Tu cuenta en SCAM ha sido creada con éxito y ya puedes empezar a explorar todas nuestras funciones.

                Para comenzar, te recomendamos completar tu perfil y revisar nuestro catálogo.

                Saludos,
                El equipo de SCAM
                """
                .formatted(newUsername); // Esto inyecta el nombre donde pusimos %s

        sendMail(newUserMail, "Bienvenido a SCAM", message);
    }

    // Notifica al creador cuando su curso ha sido publicado.
    public void cursePublished(String userMail, String courseName, String creatorName) {
        String message = """
                ¡Enhorabuena, %s! 🎉

                Nos complace informarte que tu curso "%s" ha sido revisado y aprobado por nuestro equipo.

                A partir de este momento, tu curso ya está visible en nuestra plataforma y los estudiantes pueden empezar a inscribirse.

                Saludos,
                El equipo de SCAM
                """
                .formatted(creatorName, courseName); // Esto inyecta el nombre donde pusimos %s

        sendMail(userMail, "SE HA PUBLICADO TU CURSO", message);
    }

    // Informa al usuario de que su cuenta ha sido suspendida.
    public void accountBannedMessage(String userEmail, String userName) {
        String message = """
                Hola %s,

                Te informamos que tu cuenta en SCAM ha sido suspendida permanentemente debido al incumplimiento de nuestros Términos de Servicio y Normas de la Comunidad.

                A partir de este momento, ya no tienes acceso a los contenidos de la web.

                Atentamente,
                El Equipo de Seguridad de SCAM
                """
                .formatted(userName);

        sendMail(userEmail, "⚠️ Notificación de suspensión de cuenta", message);
    }

    // Notifica al creador cuando su evento ha sido publicado.
    public void eventPublished(String userMail, String eventName, String creatorName) {
        String message = """
                ¡Enhorabuena, %s! 🎉

                Nos complace informarte que tu evento "%s" ha sido revisado y aprobado por nuestro equipo.

                A partir de este momento, tu evento ya está visible en nuestra plataforma y los estudiantes pueden empezar a inscribirse.

                Saludos,
                El equipo de SCAM
                """
                .formatted(creatorName, eventName);

        sendMail(userMail, "SE HA PUBLICADO TU EVENTO", message);

    }

    // Envía por correo la factura PDF de un pedido.
    public void orderInvoiceMessage(String userMail, String userName, Long orderId, byte[] invoicePdf) {
        String message = """
                Hola %s,

                ¡Gracias por tu compra en SCAM!

                Adjuntamos la factura en PDF de tu pedido #%s.

                Saludos,
                El equipo de SCAM
                """
                .formatted(userName, orderId);

        sendMailWithAttachment(userMail, "Factura de tu pedido #" + orderId, message, invoicePdf,
                "factura-pedido-" + orderId + ".pdf");
    }
}
