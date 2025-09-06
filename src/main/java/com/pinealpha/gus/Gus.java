package com.pinealpha.gus;

import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import java.time.Duration;

class Gus {
public static void main(String[] args) {
    String mode = "ollama"; // default mode
    StreamingChatModel model;
    final String MODEL;
    
    if (args != null && args.length > 0) {
        if (args[0].equals("--openai")) {
            mode = "openai";
        } else if (args[0].equals("--ollama")) {
            mode = "ollama";
        }
    }
    
    if (mode.equals("openai")) {
        MODEL = "gpt-5";

        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            IO.println("Error: OPENAI_API_KEY environment variable not set");
            System.exit(1);
        }
        model = OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(MODEL)
                .timeout(Duration.ofSeconds(120))
                .build();
        
        IO.println("[Using OpenAI mode with " + MODEL + "]");
    } else {
        MODEL = "gemma3";
        final String BASE_URL = "http://localhost:11434";
        final Duration timeout = Duration.ofSeconds(120);
        
        model = OllamaStreamingChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(MODEL)
                .timeout(timeout)
                .build();
        
        IO.println("[Using Ollama mode with " + MODEL + "]");
    }
    
    ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(100);
        IO.println("type /exit or Ctrl+D to quit, /help for help)");
        IO.println("-------------------------------------------------------------------");
        IO.println("Hi, I'm Gus, your friendly neighborhood AI CLI!");
        
        while (true) {
            IO.print("> ");
            
            String input = IO.readln();
            
            switch (input) {
                case null -> {
                    IO.println("\nGoodbye!");
                    return;
                }
                case "/exit" -> {
                    IO.println("Goodbye!");
                    return;
                }
                case "/clear" -> {
                    chatMemory.clear();
                    IO.println("Memory cleared!");
                }
                case "/help" -> {
                    IO.println(Helper.getHelpText());
                }
                case String s when s.trim().isEmpty() -> {
                }
                default -> {
                    Helper.streamChat(model, input, chatMemory);
                    IO.println("\n");
                }
            }
        }
    }
}