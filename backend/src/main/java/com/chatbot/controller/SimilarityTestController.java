package com.chatbot.controller;

import com.chatbot.service.similarity.SimilarityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimilarityTestController {

    private final SimilarityService similarityService;

    public SimilarityTestController(
            SimilarityService similarityService) {

        this.similarityService = similarityService;
    }

    @GetMapping("/test-search")
    public String testSearch(@RequestParam Long documentId) {

        similarityService.findRelevantChunks(
                "What is Redis?",
                documentId,
                3
        );

        return "Similarity Test Completed";
    }
}