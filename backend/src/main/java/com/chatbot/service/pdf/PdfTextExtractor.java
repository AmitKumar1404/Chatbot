package com.chatbot.service.pdf;

import java.io.File;
import java.io.IOException;

public interface PdfTextExtractor {

    String extractText(File pdfFile) throws IOException;

}