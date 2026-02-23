package es.codeurjc.scam_g18.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    private void sendMail(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("scam.noreply67@gmail.com");
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);

        mailSender.send(mensaje);
        System.out.println("¡Correo enviado con éxito!");
    }

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
}
