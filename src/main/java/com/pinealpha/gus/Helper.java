package com.pinealpha.gus;

class Helper {

    public static void stopAnimation(Thread animationThread) {
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
