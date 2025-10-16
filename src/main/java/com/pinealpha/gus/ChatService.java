package com.pinealpha.gus;

import java.util.ArrayList;
import java.util.List;

import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;

import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.CompleteToolCall;
import dev.langchain4j.model.chat.response.PartialToolCall;

import com.pinealpha.gus.tools.*;

class ChatService {

    public static void streamChat(StreamingChatModel model, String prompt, ChatMemory chatMemory) {
        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();
        List<ToolSpecification> toolSpecs = new ArrayList<>();
        for (Class<?> toolClass : Tools.class.getPermittedSubclasses()) {
            toolSpecs.addAll(ToolSpecifications.toolSpecificationsFrom(toolClass));
        }

        chatMemory.add(UserMessage.from(prompt));

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
                    Helper.stopAnimation(animationThread);
                    firstResponse[0] = true;
                }
                aiResponseBuilder.append(partialResponse);
                IO.print(partialResponse);
                System.out.flush();
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                List<ToolExecutionRequest> toolRequests = completeResponse.aiMessage().toolExecutionRequests();

                if (toolRequests != null && !toolRequests.isEmpty()) {
                    chatMemory.add(completeResponse.aiMessage());

                    for (ToolExecutionRequest request : toolRequests) {
                        String result = executeTool(request);
                        chatMemory.add(ToolExecutionResultMessage.from(request.id(), request.name(), result));
                    }

                    // Reset for continuation - animation won't restart since firstResponse is true
                    ChatRequest continueRequest = ChatRequest.builder()
                        .messages(chatMemory.messages())
                        .toolSpecifications(toolSpecs)
                        .build();

                    aiResponseBuilder.setLength(0);
                    firstResponse[0] = false;  // Reset so animation doesn't restart
                    model.chat(continueRequest, this);
                } else {
                    // Final response - done
                    if (aiResponseBuilder.length() > 0) {
                        chatMemory.add(AiMessage.from(aiResponseBuilder.toString()));
                    }
                    futureResponse.complete(completeResponse);
                }
            }

            @Override
            public void onPartialToolCall(PartialToolCall partialToolCall) {
                // Keep animation running during tool calls
                aiResponseBuilder.append(partialToolCall);
                System.out.flush();
            }

            @Override
            public void onCompleteToolCall(CompleteToolCall completeToolCall) {
                // Tool calls are handled in onCompleteResponse
            }

            @Override
            public void onError(Throwable error) {
                Helper.stopAnimation(animationThread);
                IO.println("\nError: " + error.getMessage());
                futureResponse.completeExceptionally(error);
            }
        };

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(chatMemory.messages())
                .toolSpecifications(toolSpecs)
                .build();
        model.chat(chatRequest, handler);

        try {
            futureResponse.get();
        } catch (Exception e) {
            IO.println(e);
        }
    }

    private static String executeTool(ToolExecutionRequest request) {
        Gson gson = new Gson();
        JsonObject args = gson.fromJson(request.arguments(), JsonObject.class);

        try {
            Tools tool = Tools.getToolByName(request.name());

            return switch (tool) {
                case StringLengthTool t -> String.valueOf(t.stringLength(args.get("arg0").getAsString()));
                case SecretTool t -> t.secret();
                case SearchWebTool t -> t.searchWeb(args.get("arg0").getAsString());
                case ScrapePageTool t -> t.scrapePage(args.get("arg0").getAsString());
            };
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
