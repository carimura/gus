package com.pinealpha.gus.tools;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchResults;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;

public final class SearchWebTool extends Tools {
    @Tool("Searches the web for current information and returns relevant results")
    public String searchWeb(String query) {
        preToolHook("searchWeb", "query='" + query + "'");

        String apiKey = System.getenv("TAVILY_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            return "Error: TAVILY_API_KEY environment variable not set";
        }

        WebSearchEngine engine = TavilyWebSearchEngine.builder()
                .apiKey(apiKey)
                .build();

        WebSearchResults results = engine.search(query);

        if (results.results().isEmpty()) {
            return "No results found for query: " + query;
        }

        StringBuilder formattedResults = new StringBuilder();
        formattedResults.append("Search results for: ").append(query).append("\n\n");

        int count = Math.min(5, results.results().size());
        for (int i = 0; i < count; i++) {
            var result = results.results().get(i);
            formattedResults.append((i + 1)).append(". ")
                    .append(result.title()).append("\n")
                    .append("   URL: ").append(result.url()).append("\n")
                    .append("   ").append(result.content()).append("\n\n");
        }

        return formattedResults.toString();
    }
}
