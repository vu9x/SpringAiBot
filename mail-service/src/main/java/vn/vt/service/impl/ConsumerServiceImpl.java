package vn.vt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import vn.vt.dto.MailParams;
import vn.vt.service.ConsumerService;
import vn.vt.service.MailSenderService;

@Log4j2
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MailSenderService mailSenderService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.registration-mail}")
    public void consumeRegistrationMail(MailParams mailParams){
        try {
            mailSenderService.send(mailParams);
        } catch (Exception e) {
            log.error(e);
        }
    }
}
