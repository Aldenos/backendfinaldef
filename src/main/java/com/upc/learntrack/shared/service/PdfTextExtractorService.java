package com.upc.learntrack.shared.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfTextExtractorService {

    private static final int MAX_TEXT_LENGTH = 3000;

    public String extractText(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(document);
            if (fullText.length() > MAX_TEXT_LENGTH) {
                fullText = fullText.substring(0, MAX_TEXT_LENGTH);
            }
            return fullText;
        }
    }
}