package vn.vt.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Log4j2
public class HttpOpenAiBuilder {
    private final String url;
    private final String apiKey;
    private final String model;

    public HttpOpenAiBuilder(@Value("${openai.url}") String url,
                             @Value("${openai.apiKey}") String apiKey,
                             @Value("${openai.model}") String model) {
        this.url = url;
        this.apiKey = apiKey;
        this.model = model;
    }

    public String openAiResponse(String message){

        //TODO сделать более элегантное решение через JSON объект
        String requestBody = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";

        try(HttpClient httpClient = HttpClient.newHttpClient();) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info(response.statusCode());

            return extractContentFromResponse(response.body());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }


}
