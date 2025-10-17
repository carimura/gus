package com.pinealpha.gus;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.data.message.SystemMessage;

class Gus {

    private static final String SYSTEM_PROMPT = """
        You are Gus, a friendly and helpful AI assistant.
        You provide clear, concise, and accurate responses.
        You're conversational but professional.
    """;

    public static void main(String[] args) throws Exception {
        var provider = "openai";
        var model = "gpt-5";

        if (args != null && args.length > 0) {
            switch (args[0]) {
                 case "--openai" -> {
                    provider = "openai";
                    model = "gpt-5";
                    break;
                }
                case "--ollama" -> {
                    provider = "ollama";
                    model = "llama3.2";
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

        IO.println("""
            -------------------------------------------------------------------
                        ,
                        |`-.__
                        / ' _/
                       ----`\s
                      /    }
                     /  \\ /
                 \\ /`   \\\\\\
                  `\\    /_\\\\
                   `~~~~~``~`

            [Using provider %s with model %s]

            (type /exit or Ctrl+D to quit, /help for help, /clear to clear memory)
            -------------------------------------------------------------------

            Hi, I'm Gus, your friendly neighborhood AI CLI! How can I help today?\s
            """.formatted(provider, model));

        var inputHandler = new InputHandler();

        while (true) {
            var input = inputHandler.readLine("> ");

            // applies background shade on typed commands
            if (!input.trim().isEmpty()) {
                IO.print("\u001B[1A\r\u001B[K\u001B[48;5;240m\u001B[37m> " + input + "\u001B[0m\n\n");
            }

            switch (input) {
                case "/exit" -> {
                    IO.println("Goodbye!");
                    inputHandler.close();
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
