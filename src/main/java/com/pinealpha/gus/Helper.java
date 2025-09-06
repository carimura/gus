package com.pinealpha.gus;

import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import java.util.concurrent.CompletableFuture;

class Helper {

public static void streamChat(StreamingChatModel model, String prompt, ChatMemory chatMemory) {
    CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();
    
    // Add user message to memory if memory is available
    if (chatMemory != null) {
        chatMemory.add(UserMessage.from(prompt));
    }
    
    // Start thinking animation in a separate thread
    Thread animationThread = new Thread(() -> {
        String[] animation = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        int i = 0;
        String RED = "\u001B[31m";
        String RESET = "\u001B[0m";
        while (!Thread.currentThread().isInterrupted()) {
            IO.print("\r" + RED + animation[i % animation.length] + " Thinking..." + RESET);
            System.out.flush();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            i++;
        }
        // Clear the thinking message
        IO.print("\r                    \r");
        System.out.flush();
    });
    animationThread.start();
    
    boolean[] firstResponse = {false};
    StringBuilder aiResponseBuilder = new StringBuilder();
    
        // Use conversation history if memory is available, otherwise just the current prompt
        StreamingChatResponseHandler handler = new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String partialResponse) {
                if (!firstResponse[0]) {
                    stopAnimation(animationThread);
                    firstResponse[0] = true;
                }
                aiResponseBuilder.append(partialResponse);
                IO.print(partialResponse);
                System.out.flush();
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                // Add AI response to memory if memory is available
                if (chatMemory != null) {
                    chatMemory.add(AiMessage.from(aiResponseBuilder.toString()));
                }
                futureResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                stopAnimation(animationThread);
                IO.println("\nError: " + error.getMessage());
                futureResponse.completeExceptionally(error);
            }
        };
    
        if (chatMemory != null) {
            model.chat(chatMemory.messages(), handler);
        } else {
            model.chat(prompt, handler);
        }
        
        try {
            futureResponse.get();
            } catch (Exception e) {
        }
    }

    private static void stopAnimation(Thread animationThread) {
        animationThread.interrupt();
        try {
            animationThread.join(100);
        } catch (InterruptedException e) {
        }
    }

    public static String getHelpText() {
        return """
        Commands:
            /exit    - Exit the program
            /clear   - Clear chat memory
            /help    - Show this help message

        \nUsage:");
            java -jar gus.jar                    - Interactive mode with Ollama (default)
            java -jar gus.jar --openai           - Interactive mode with OpenAI
            java -jar gus.jar 'prompt'           - Single prompt with Ollama
            java -jar gus.jar --openai 'prompt'  - Single prompt with OpenAI
        """;
    }

}