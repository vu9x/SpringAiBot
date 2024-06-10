package vn.vt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import vn.vt.service.ConsumerService;
import vn.vt.service.MainService;

@Log4j2
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.doc-message-update}")
    public void consumeDocMessageUpdates(Update update) {
        log.info("Node: Doc message is received");
        try{
            mainService.processDocMessage(update);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.photo-message-update}")
    public void consumePhotoMessageUpdates(Update update) {
        log.info("Node: Photo message is received");
        try{
            mainService.processPhotoMessage(update);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
