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
    @RabbitListener(queues = "${spring.rabbitmq.queues.text-message-update}")
    public void consumeTextMessageUpdates(Update update) {
        log.info("Node: Text message is received");
        try{
            mainService.processTextMessage(update);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
