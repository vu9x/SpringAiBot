package vn.vt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import vn.vt.configuration.RabbitConfiguration;
import vn.vt.service.UpdateProducer;
import vn.vt.utils.*;


@Log4j2
@RequiredArgsConstructor
@Component
public class UpdateProcessor {

    private AiTelegramBot aiTelegramBot;

    private final MessageUtils messageUtils;

    private final UpdateProducer updateProducer;

    private final RabbitConfiguration rabbitConfiguration;

    public void registerBot(AiTelegramBot aiTelegramBot){
        this.aiTelegramBot = aiTelegramBot;
    }

    public void processUpdate(Update update){
        if(update == null) {
            log.error("Received update is null");
            return;
        }

        if(update.hasMessage()){
            distributeMessageByType(update);
        } else {
            log.error("Unsupported message type is receveid: " + update);
        }
    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()){
            processTextMessage(update);
        } else if (message.hasDocument()){
            processDocMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }


    private void processPhotoMessage(Update update) {
        setFileIsReceivedView(update);
        updateProducer.produce(rabbitConfiguration.getPhotoMessageUpdateQueue(), update);
    }

    private void processDocMessage(Update update) {
        setFileIsReceivedView(update);
        updateProducer.produce(rabbitConfiguration.getDocMessageUpdateQueue(), update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(rabbitConfiguration.getTextMessageUpdateQueue(), update);
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения");
        setView(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Файл получен! Обрабатывается...");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        aiTelegramBot.sendMessage(sendMessage);
    }
}
