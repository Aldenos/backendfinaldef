package com.upc.learntrack.shared.service;

import java.util.List;

public interface EmailService {
    void sendReport(List<String> recipients, String subject, String body, byte[] pdfData, String fileName);
}