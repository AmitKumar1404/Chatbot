package com.chatbot.service;

import com.chatbot.dto.SearchResultDto;

import java.util.List;

public interface SearchService {

    List<SearchResultDto> search(String query);
}
