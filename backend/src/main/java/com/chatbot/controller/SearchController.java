package com.chatbot.controller;

import com.chatbot.constant.ResponseCode;
import com.chatbot.dto.SearchResultDto;
import com.chatbot.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.chatbot.constant.AppConstants.SEARCH_BASE_PATH;

@RestController
@RequestMapping(SEARCH_BASE_PATH)
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<List<SearchResultDto>> search(@RequestParam(name = "q", required = false) String query) {
        return ResponseEntity.status(ResponseCode.OK).body(searchService.search(query));
    }
}
