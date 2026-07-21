package com.chatbot.service.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class PdfTextExtractorImpl implements PdfTextExtractor {

    @Override
    public String extractText(File pdfFile) throws IOException {

        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            PDFTextStripper stripper = new PDFTextStripper();

            String extractedText = stripper.getText(document);

            return extractedText.trim();
        }
    }
}