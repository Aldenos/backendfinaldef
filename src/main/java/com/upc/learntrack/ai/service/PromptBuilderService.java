package com.upc.learntrack.ai.service;

import java.util.List;

public interface PromptBuilderService {
    String buildMultiFormatPrompt(String topicName, String content, List<String> types);
}