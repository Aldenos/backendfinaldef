package com.upc.learntrack.shared.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    /**
     * Genera un PDF a partir de una plantilla HTML y datos dinámicos.
     * @param templateName Nombre del archivo en resources/templates (sin .html)
     * @param data Mapa de variables para reemplazar en el HTML
     * @return byte[] que representa el archivo PDF
     */
    public byte[] generatePdf(String templateName, Map<String, Object> data) {
        log.info("Iniciando generación de PDF con plantilla: {}", templateName);

        // 1. Procesar el template HTML con los datos
        Context context = new Context();
        context.setVariables(data);
        String htmlContent = templateEngine.process(templateName, context);

        // 2. Convertir HTML a PDF usando OpenHTMLtoPDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            
            // withHtmlContent recibe el string procesado
            builder.withHtmlContent(htmlContent, "/");
            builder.toStream(outputStream);
            builder.run();
            
            log.info("PDF generado correctamente.");
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error al generar PDF: {}", e.getMessage());
            throw new RuntimeException("Fallo en la generación del PDF", e);
        }
    }
}