package com.pinealpha.gus;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import java.time.Duration;


class ModelSelector {

    public static StreamingChatModel getModel(String mode, String model_name) {
        StreamingChatModel model;
        final Duration timeout = Duration.ofSeconds(120);
        
        if (mode.equals("openai")) {
            String apiKey = System.getenv("OPENAI_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                IO.println("Error: OPENAI_API_KEY environment variable not set");
                System.exit(1);
            }
            model = OpenAiStreamingChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(model_name)
                    .timeout(timeout)
                    //.logRequests(true) // Enable request logging
                    //.logResponses(true)   // Enable response logging
                    .build();
        } else {
            final String BASE_URL = "http://localhost:11434";

            model = OllamaStreamingChatModel.builder()
                    .baseUrl(BASE_URL)
                    .modelName(model_name)
                    .timeout(timeout)
                    //.logRequests(true) // Enable request logging
                    //.logResponses(true)   // Enable response logging
                    .build();
        }

        return model;
    }

}
