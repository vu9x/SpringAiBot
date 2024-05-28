package vn.vt.controller;


import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class AiTelegramBot extends TelegramWebhookBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String token;
    @Value("${bot.uri}")
    private String botUri;

    private UpdateProcessor updateController;

    public AiTelegramBot(@Value("${bot.token}") String token, UpdateProcessor updateProcessor){
        super(token);
        this.updateController = updateProcessor;
    }

    @PostConstruct
    public void init(){
        updateController.registerBot(this);
        try{
            var setWebHook = SetWebhook.builder()
                    .url(botUri)
                    .build();
            this.setWebhook(setWebHook);
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

//    @Override
//    public void onUpdateReceived(Update update) {
//        updateController.processUpdate(update);
//    }

    @Override
    public String getBotUsername() {
        return botName;
    }


    @Override
    public String getBotPath() {
        return "/update";
    }

    public void sendMessage(SendMessage message){
//        if(message != null){
//            try {
//                execute(message);
//            } catch (TelegramApiException e) {
//                log.error(e);
//            }
//        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

}
