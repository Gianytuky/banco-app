package com.banco.service;

import com.banco.event.NotificacionEvent;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Envia los correos DESPUES de confirmar (commit) la transaccion bancaria y en segundo plano.
 * Asi: si la operacion se revierte no sale ningun correo, y si Gmail falla no se deshace la operacion.
 */
@Component
public class CorreoListener {

    private static final Logger log = LoggerFactory.getLogger(CorreoListener.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean habilitado;

    @Value("${spring.mail.username:}")
    private String remitente;

    public CorreoListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** Muestra al iniciar la configuracion de correo que realmente se esta usando. */
    @PostConstruct
    public void iniciar() {
        log.info("Configuracion de correo -> habilitado={} | remitente={}", habilitado, remitente);
        if (!habilitado) {
            log.warn("Los correos NO se enviaran: ponga app.mail.enabled=true en application.properties y reinicie");
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void enviar(NotificacionEvent evento) {
        if (!habilitado) {
            log.info("[CORREO DESHABILITADO] Para: {} | Asunto: {}", evento.para(), evento.asunto());
            return;
        }
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(evento.para());
            helper.setSubject(evento.asunto());
            helper.setText(evento.html(), true);
            mailSender.send(mensaje);
            log.info("Correo enviado a {} ({})", evento.para(), evento.asunto());
        } catch (Exception e) {
            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }
            log.error("No se pudo enviar el correo a {} -> {} | causa: {}", evento.para(), e.getMessage(), causa.toString());
        }
    }
}
