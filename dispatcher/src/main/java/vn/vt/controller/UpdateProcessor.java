package vn.vt.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import vn.vt.service.UpdateProducer;
import vn.vt.utils.*;

import static vn.vt.model.RabbitQueue.*;

@Component
@Log4j2
public class UpdateProcessor {
    private AiTelegramBot aiTelegramBot;
    private MessageUtils messageUtils;
    private UpdateProducer updateProducer;

    @Autowired
    public UpdateProcessor(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

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
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
    }

    private void processDocMessage(Update update) {
        setFileIsReceivedView(update);
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
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
