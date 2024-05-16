package vn.vt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import vn.vt.controller.HttpOpenAiBuilder;

@SpringBootApplication
public class OpenAiApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(OpenAiApplication.class);

        HttpOpenAiBuilder test = context.getBean(HttpOpenAiBuilder.class);

        System.out.println(test.openAiResponse("Who are you").body());

    }
}
