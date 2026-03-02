package es.codeurjc.scam_g18.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    // Sends an email with an attachment.
    private void sendMailWithAttachment(String destinatario, String asunto, String cuerpoHtml, byte[] attachment,
            String attachmentName) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("scam.noreply67@gmail.com");
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true);
            helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

            mailSender.send(mimeMessage);
            System.out.println("¡Correo con adjunto enviado con éxito!");
        } catch (MessagingException e) {
            throw new RuntimeException("No se pudo enviar el correo con adjunto", e);
        }
    }

    private String buildEmailLayout(String title, String subtitle, String greeting, String bodyHtml,
            String highlightTitle, String highlightContent, String buttonText, String buttonUrl, String footerText) {
        String titleSafe = escapeHtml(title);
        String subtitleSafe = escapeHtml(subtitle);
        String greetingSafe = greeting == null ? "" : "<p style=\"margin:0 0 16px 0;font-size:16px;\">" + greeting + "</p>";
        String highlightBlock = "";
        if (highlightTitle != null && !highlightTitle.isBlank() && highlightContent != null && !highlightContent.isBlank()) {
            highlightBlock = """
                    <div style="margin:20px 0;padding:14px 16px;background:#f7f9fc;border:1px solid #d7e0f0;border-radius:10px;">
                        <p style="margin:0 0 8px 0;font-size:14px;color:#405175;"><strong>%s</strong></p>
                        <p style="margin:0;font-size:14px;line-height:1.5;">%s</p>
                    </div>
                    """.formatted(escapeHtml(highlightTitle), highlightContent);
        }

        String buttonBlock = "";
        if (buttonText != null && !buttonText.isBlank() && buttonUrl != null && !buttonUrl.isBlank()) {
            buttonBlock = """
                    <div style="margin:20px 0 0 0;">
                        <a href="%s" style="display:inline-block;background:#2f3b59;color:#ffffff;text-decoration:none;padding:10px 18px;border-radius:8px;font-size:14px;font-weight:700;">%s</a>
                    </div>
                    """.formatted(escapeHtml(buttonUrl), escapeHtml(buttonText));
        }

        String footerSafe = escapeHtml(footerText);

        return """
                <div style="margin:0;padding:24px;font-family:Arial,sans-serif;color:#2f2f2f;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:640px;margin:0 auto;background:#ffffff;border-radius:14px;overflow:hidden;border:1px solid #e9e9e9;box-shadow:0 6px 20px rgba(0,0,0,0.06);">
                        <tr>
                            <td style="padding:24px 28px;background:#2f3b59;color:#ffffff;">
                                <h1 style="margin:0;font-size:24px;line-height:1.2;">%s</h1>
                                <p style="margin:8px 0 0 0;font-size:14px;opacity:0.95;">%s</p>
                            </td>
                        </tr>
                        <tr>
                            <td style="padding:28px;">
                                %s
                                %s
                                %s
                                %s
                            </td>
                        </tr>
                        <tr>
                            <td style="padding:16px 28px;background:#f7f9fc;border-top:1px solid #d7e0f0;font-size:12px;color:#405175;">
                                %s
                            </td>
                        </tr>
                    </table>
                </div>
                """.formatted(titleSafe, subtitleSafe, greetingSafe, bodyHtml, highlightBlock, buttonBlock, footerSafe);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // Sends an email in HTML format.
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

    // Sends the welcome message when a new account is created.
    public void newAccountMessage(String newUserMail, String newUsername) {
                String safeUsername = (newUsername == null || newUsername.isBlank()) ? "usuario" : escapeHtml(newUsername);

                String bodyHtml = """
                        <p style="margin:0 0 14px 0;font-size:15px;line-height:1.6;">
                            Nos alegra tenerte con nosotros. Tu cuenta se ha creado correctamente y ya puedes empezar a aprovechar todas las funcionalidades de la plataforma.
                        </p>
                        <p style="margin:0;font-size:14px;line-height:1.6;">Gracias por confiar en SCAM.</p>
                        """;

                String messageHtml = buildEmailLayout(
                        "¡Bienvenido a SCAM!",
                        "Tu cuenta ya está lista",
                        "Hola <strong>%s</strong>,".formatted(safeUsername),
                        bodyHtml,
                        "Siguiente paso recomendado:",
                        "Completa tu perfil y explora el catálogo para encontrar tus primeros cursos y eventos.",
                        "Ir a SCAM",
                        "https://localhost:8443/",
                        "Equipo de SCAM");

                sendHtmlMail(newUserMail, "Bienvenido a SCAM", messageHtml);
    }

    // Notifies the creator when their course has been published.
    public void cursePublished(String userMail, String courseName, String creatorName) {
        String safeCreatorName = (creatorName == null || creatorName.isBlank()) ? "creador" : escapeHtml(creatorName);
        String safeCourseName = (courseName == null || courseName.isBlank()) ? "tu curso" : escapeHtml(courseName);

        String bodyHtml = """
                <p style="margin:0 0 14px 0;font-size:15px;line-height:1.6;">
                    Nos complace informarte que tu curso <strong>\"%s\"</strong> ha sido revisado y aprobado por nuestro equipo.
                </p>
                <p style="margin:0;font-size:14px;line-height:1.6;">
                    Ya está visible en la plataforma y los estudiantes pueden empezar a inscribirse.
                </p>
                """.formatted(safeCourseName);

        String messageHtml = buildEmailLayout(
                "¡Tu curso ha sido publicado!",
                "Aprobación completada",
                "Enhorabuena, <strong>%s</strong> 🎉".formatted(safeCreatorName),
                bodyHtml,
                "Qué hacer ahora:",
                "Revisa la ficha pública y comparte el curso con tu comunidad.",
                "Ver cursos",
                "https://localhost:8443/courses",
                "Equipo de SCAM");

        sendHtmlMail(userMail, "SE HA PUBLICADO TU CURSO", messageHtml);
    }

    // Informs the user that their account has been suspended.
    public void accountBannedMessage(String userEmail, String userName) {
        String safeUserName = (userName == null || userName.isBlank()) ? "usuario" : escapeHtml(userName);

        String bodyHtml = """
                <p style="margin:0 0 14px 0;font-size:15px;line-height:1.6;">
                    Te informamos de que tu cuenta en SCAM ha sido suspendida por incumplimiento de nuestros Términos de Servicio y Normas de la Comunidad.
                </p>
                <p style="margin:0;font-size:14px;line-height:1.6;">
                    Desde este momento ya no tienes acceso a los contenidos de la plataforma.
                </p>
                """;

        String messageHtml = buildEmailLayout(
                "Notificación de seguridad",
                "Suspensión de cuenta",
                "Hola <strong>%s</strong>,".formatted(safeUserName),
                bodyHtml,
                "Si crees que es un error:",
                "Contacta con el equipo de soporte para revisar tu caso.",
                "Contactar soporte",
                "https://localhost:8443/",
                "Equipo de Seguridad de SCAM");

        sendHtmlMail(userEmail, "⚠️ Notificación de suspensión de cuenta", messageHtml);
    }

    // Notifies the creator when their event has been published.
    public void eventPublished(String userMail, String eventName, String creatorName) {
        String safeCreatorName = (creatorName == null || creatorName.isBlank()) ? "creador" : escapeHtml(creatorName);
        String safeEventName = (eventName == null || eventName.isBlank()) ? "tu evento" : escapeHtml(eventName);

        String bodyHtml = """
                <p style="margin:0 0 14px 0;font-size:15px;line-height:1.6;">
                    Nos complace informarte que tu evento <strong>\"%s\"</strong> ha sido revisado y aprobado por nuestro equipo.
                </p>
                <p style="margin:0;font-size:14px;line-height:1.6;">
                    Ya está visible en la plataforma y los usuarios pueden empezar a apuntarse.
                </p>
                """.formatted(safeEventName);

        String messageHtml = buildEmailLayout(
                "¡Tu evento ha sido publicado!",
                "Aprobación completada",
                "Enhorabuena, <strong>%s</strong> 🎉".formatted(safeCreatorName),
                bodyHtml,
                "Qué hacer ahora:",
                "Comprueba los detalles del evento y compártelo para aumentar asistencia.",
                "Ver eventos",
                "https://localhost:8443/events",
                "Equipo de SCAM");

        sendHtmlMail(userMail, "SE HA PUBLICADO TU EVENTO", messageHtml);

    }

    // Sends the PDF invoice of an order by email.
    public void orderInvoiceMessage(String userMail, String userName, Long orderId, byte[] invoicePdf) {
        String safeUserName = (userName == null || userName.isBlank()) ? "usuario" : escapeHtml(userName);
        String safeOrderId = orderId == null ? "-" : escapeHtml(String.valueOf(orderId));

        String bodyHtml = """
            <p style="margin:0 0 14px 0;font-size:15px;line-height:1.6;">
                ¡Gracias por tu compra en SCAM!
            </p>
            <p style="margin:0;font-size:14px;line-height:1.6;">
                Te enviamos adjunta la factura en PDF de tu pedido <strong>#%s</strong>.
            </p>
            """.formatted(safeOrderId);

        String messageHtml = buildEmailLayout(
            "Factura de compra",
            "Documento adjunto",
            "Hola <strong>%s</strong>,".formatted(safeUserName),
            bodyHtml,
            "Incluye:",
            "El detalle de los productos y el justificante de pago.",
            "Ir a SCAM",
            "https://localhost:8443/",
            "Equipo de SCAM");

        sendMailWithAttachment(userMail, "Factura de tu pedido #" + safeOrderId, messageHtml, invoicePdf,
                "factura-pedido-" + orderId + ".pdf");
    }
}
