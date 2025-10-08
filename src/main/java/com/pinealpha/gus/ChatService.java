package com.pinealpha.gus;

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

class ChatService {

    public static void streamChat(StreamingChatModel model, String prompt, ChatMemory chatMemory) {
        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();
        List<ToolSpecification> toolSpecs = ToolSpecifications.toolSpecificationsFrom(Tools.class);

        chatMemory.add(UserMessage.from(prompt));

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

                    ChatRequest continueRequest = ChatRequest.builder()
                        .messages(chatMemory.messages())
                        .toolSpecifications(toolSpecs)
                        .build();

                    aiResponseBuilder.setLength(0);
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
                if (!firstResponse[0]) {
                    Helper.stopAnimation(animationThread);
                    firstResponse[0] = true;
                }
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
        Tools tools = new Tools();
        Gson gson = new Gson();
        JsonObject args = gson.fromJson(request.arguments(), JsonObject.class);

        try {
            return switch (request.name()) {
                case "stringLength" -> String.valueOf(tools.stringLength(args.get("arg0").getAsString()));
                case "add" -> String.valueOf(tools.add(args.get("arg0").getAsInt(), args.get("arg1").getAsInt()));
                case "sqrt" -> String.valueOf(tools.sqrt(args.get("arg0").getAsInt()));
                case "code" -> String.valueOf(tools.code());
                default -> "Unknown tool: " + request.name();
            };
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
