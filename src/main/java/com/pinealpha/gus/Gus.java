package com.pinealpha.gus;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.data.message.SystemMessage;

class Gus {

    private static final String SYSTEM_PROMPT = "You are Gus, a friendly and helpful AI assistant. " +
            "You provide clear, concise, and accurate responses. " +
            "You're conversational but professional.";

    public static void main(String[] args) {
        String provider = "openai";
        String model = "gpt-5";

        if (args != null && args.length > 0) {
            switch (args[0]) {
                 case "--openai" -> {
                    provider = "openai";
                    model = "gpt-5";
                    break;
                }
                case "--ollama" -> {
                    provider = "ollama";
                    model = "gemma3";
                    break;
                }
                default -> {
                    IO.println("Model provider not recognized: " + args[0]);
                    IO.println("Use --openai or --ollama");
                    System.exit(1);
                }
            }
        }

        var stream = ModelSelector.getModel(provider, model);
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(100);
        chatMemory.add(SystemMessage.from(SYSTEM_PROMPT));
        
        IO.println("-------------------------------------------------------------------");
        IO.println("            ,");
        IO.println("            |`-.__");
        IO.println("            / ' _/");
        IO.println("           ----` ");
        IO.println("          /    }");
        IO.println("         /  \\ /");
        IO.println("     \\ /`   \\\\\\");
        IO.println("      `\\    /_\\\\");
        IO.println("       `~~~~~``~`");
        IO.println("");
        IO.println("[Using provider " + provider + " with model " + model + "]\n");
        IO.println("(type /exit or Ctrl+D to quit, /help for help, /clear to clear memory)");
        IO.println("-------------------------------------------------------------------\n");
        IO.println("Hi, I'm Gus, your friendly neighborhood AI CLI! How can I help today? \n");

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
                    ChatService.streamChat(stream, input, chatMemory);
                    IO.println("\n");
                }
            }
        }
    }
}
