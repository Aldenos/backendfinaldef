package com.upc.learntrack.shared.service.impl;

import com.upc.learntrack.shared.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async("taskExecutor") // Referencia a un bean definido en AsyncConfig
    @Override
    public void sendReport(List<String> recipients, String subject, String body, byte[] pdfData, String fileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(body, true);

            if (pdfData != null) {
                helper.addAttachment(fileName, new ByteArrayResource(pdfData));
            }

            mailSender.send(message);
            log.info("Reporte enviado exitosamente a {} destinatarios", recipients.size());
        } catch (MessagingException e) {
            log.error("Error al enviar el reporte: {}", e.getMessage(), e);
        }
    }
}