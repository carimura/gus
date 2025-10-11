package com.pinealpha.gus.tools;

import dev.langchain4j.agent.tool.Tool;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class ScrapePageTool extends Tools {
    @Tool("Extracts and returns the full content from a web page URL")
    public String scrapePage(String url) {
        IO.println("[Calling tool scrapePage() with url='" + url + "']");

        String apiKey = System.getenv("TAVILY_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            return "Error: TAVILY_API_KEY environment variable not set";
        }
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("urls", url);
            requestBody.addProperty("extract_depth", "basic");
            requestBody.addProperty("format", "markdown");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tavily.com/extract"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestBody)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "Error: HTTP " + response.statusCode() + " - " + response.body();
            }

            JsonObject responseJson = new Gson().fromJson(response.body(), JsonObject.class);
            JsonArray results = responseJson.getAsJsonArray("results");

            if (results == null || results.isEmpty()) {
                return "Error: No content could be extracted from URL: " + url;
            }

            JsonObject result = results.get(0).getAsJsonObject();
            String content = result.get("raw_content").getAsString();

            return content;
        } catch (Exception e) {
            return "Error scraping page: " + e.getMessage();
        }
    }
}
