package vn.vt.service.impl;

import org.springframework.stereotype.Service;
import vn.vt.controller.HttpOpenAiBuilder;

@Service
public class OpenAiService {
    private final HttpOpenAiBuilder httpOpenAiBuilder;

    public OpenAiService(HttpOpenAiBuilder httpOpenAiBuilder) {
        this.httpOpenAiBuilder = httpOpenAiBuilder;
    }

    public String sendRequestToOpenAiServer(String message){
        return httpOpenAiBuilder.openAiResponse(message);
    }
}
