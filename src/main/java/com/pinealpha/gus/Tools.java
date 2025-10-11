package com.pinealpha.gus;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchResults;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Tools {

        @Tool("Calculates the length of a string")
        int stringLength(String s) {
            IO.println("[Calling tool stringLength() with s='" + s + "']");
            return s.length();
        }

        @Tool("Calculates the sum of two numbers")
        int add(int a, int b) {
            IO.println("[Calling tool add() with a=" + a + ", b=" + b + "]");
            return a + b;
        }

        @Tool("Calculates the square root of a number")
        double sqrt(int x) {
            IO.println("[Calling tool ssqrt() with x=" + x + "]");
            return Math.sqrt(x);
        }

        @Tool("Returns our secret code")
        String code() {
            IO.println("[Calling code tool]");
            return "Gandalf";
        }

        @Tool("Searches the web for current information and returns relevant results")
        String searchWeb(String query) {
            IO.println("[Calling tool searchWeb() with query='" + query + "']");

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

            var resultStr = formattedResults.toString();
            //IO.println("SEARCH RESULTS --> " + resultStr);
            return resultStr;
        }

        @Tool("Extracts and returns the full content from a web page URL")
        String scrapePage(String url) {
            IO.println("[Calling tool scrapePage() with url='" + url + "']");

            String apiKey = System.getenv("TAVILY_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                return "Error: TAVILY_API_KEY environment variable not set";
            }
            try {
                // Build request body
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

                //IO.println("SCRAPED CONTENT --> " + content);
                return content;
            } catch (Exception e) {
                return "Error scraping page: " + e.getMessage();
            }
        }

}