package vn.vt.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import vn.vt.controller.AiTelegramBot;

@Configuration
public class AiTelegramBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(AiTelegramBot aiTelegramBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(aiTelegramBot);
        return api;
    }

}
