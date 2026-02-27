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

    // Envía un correo en formato HTML.
    private void sendHtmlMail(String destinatario, String asunto, String cuerpoHtml) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("scam.noreply67@gmail.com");
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true);

            mailSender.send(mimeMessage);
            System.out.println("¡Correo HTML enviado con éxito!");
        } catch (MessagingException e) {
            throw new RuntimeException("No se pudo enviar el correo HTML", e);
        }
    }

    // Envía el mensaje de bienvenida al crear una cuenta nueva.
    public void newAccountMessage(String newUserMail, String newUsername) {
                String safeUsername = (newUsername == null || newUsername.isBlank()) ? "usuario" : newUsername;

                String messageHtml = """
                                <div style="margin:0;padding:24px;font-family:Arial,sans-serif;color:#2f2f2f;">
                                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:640px;margin:0 auto;background:#ffffff;border-radius:14px;overflow:hidden;border:1px solid #e9e9e9;box-shadow:0 6px 20px rgba(0,0,0,0.06);">
                                        <tr>
                                            <td style="padding:24px 28px;background:#2f3b59;color:#ffffff;">
                                                <h1 style="margin:0;font-size:24px;line-height:1.2;">¡Bienvenido a SCAM!</h1>
                                                <p style="margin:8px 0 0 0;font-size:14px;opacity:0.95;">Tu cuenta ya está lista</p>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="padding:28px;">
                                                <p style="margin:0 0 16px 0;font-size:16px;">Hola <strong>%s</strong>,</p>
                                                <p style="margin:0 0 14px 0;font-size:15px;line-height:1.6;">
                                                    Nos alegra tenerte con nosotros. Tu cuenta se ha creado correctamente y ya puedes empezar a aprovechar todas las funcionalidades de la plataforma.
                                                </p>
                                                <div style="margin:20px 0;padding:14px 16px;background:#f7f9fc;border:1px solid #d7e0f0;border-radius:10px;">
                                                    <p style="margin:0 0 8px 0;font-size:14px;color:#405175;"><strong>Siguiente paso recomendado:</strong></p>
                                                    <p style="margin:0;font-size:14px;line-height:1.5;">Completa tu perfil y explora el catálogo para encontrar tus primeros cursos y eventos.</p>
                                                </div>
                                                <div style="margin:20px 0 0 0;">
                                                    <a href="https://localhost:8443/" style="display:inline-block;background:#2f3b59;color:#ffffff;text-decoration:none;padding:10px 18px;border-radius:8px;font-size:14px;font-weight:700;">Ir a SCAM</a>
                                                </div>
                                                <p style="margin:0;font-size:14px;line-height:1.6;">Gracias por confiar en SCAM.</p>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="padding:16px 28px;background:#f7f9fc;border-top:1px solid #d7e0f0;font-size:12px;color:#405175;">
                                                Equipo de SCAM
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                                """
                                .formatted(safeUsername);

                sendHtmlMail(newUserMail, "Bienvenido a SCAM", messageHtml);
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
