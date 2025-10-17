package com.pinealpha.gus.tools;

public abstract sealed class Tools permits SecretTool,
                                           SearchWebTool,
                                           ScrapePageTool {

    public static Tools getToolByName(String name) {
        return switch (name) {
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

        IO.print("\r                    \r");

        String formattedToolName = GREEN + "● " + RESET + BOLD + toolName + RESET;
        String message = params.isEmpty()
            ? formattedToolName + "()"
            : formattedToolName + "(" + params + ")";
        IO.println(message);
    }
}
