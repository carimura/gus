package com.pinealpha.gus.tools;

public abstract sealed class Tools permits StringLengthTool,
                                           SecretTool,
                                           SearchWebTool,
                                           ScrapePageTool {

    public static Tools getToolByName(String name) {
        return switch (name) {
            case "stringLength" -> new StringLengthTool();
            case "secret" -> new SecretTool();
            case "searchWeb" -> new SearchWebTool();
            case "scrapePage" -> new ScrapePageTool();
            default -> throw new IllegalArgumentException("Unknown tool: " + name);
        };
    }

    protected static void preToolHook(String toolName, String params) {
        String GREEN = "\u001B[32m";
        String BOLD = "\u001B[1m";
        String RESET = "\u001B[0m";

        // Clear the animation line before printing tool output
        IO.print("\r                    \r");

        String formattedToolName = GREEN + "● " + RESET + BOLD + toolName + RESET;
        String message = params.isEmpty()
            ? formattedToolName + "()"
            : formattedToolName + "(" + params + ")";
        IO.println(message);
    }
}
