package com.pinealpha.gus.tools;

public abstract sealed class Tools permits StringLengthTool,
                                           CodeTool,
                                           SearchWebTool,
                                           ScrapePageTool {

    public static Tools getToolByName(String name) {
        return switch (name) {
            case "stringLength" -> new StringLengthTool();
            case "code" -> new CodeTool();
            case "searchWeb" -> new SearchWebTool();
            case "scrapePage" -> new ScrapePageTool();
            default -> throw new IllegalArgumentException("Unknown tool: " + name);
        };
    }
}
