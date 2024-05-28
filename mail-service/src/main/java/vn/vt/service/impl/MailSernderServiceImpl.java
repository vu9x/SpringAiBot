//package vn.vt.service.impl;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//import vn.vt.dto.MailParams;
//import vn.vt.service.MailSenderService;
//
//@Service
//public class MailSernderServiceImpl implements MailSenderService {
//    private final JavaMailSender javaMailSender;
//    @Value("${spring.mail.username}")
//    private String emailFrom;
//    @Value("${service.activation.uri}")
//    private String activationServiceUri;
//
//    public MailSernderServiceImpl(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//
//
//    @Override
//    public void send(MailParams mailParams) {
//        var subject = "Активация учетной записи";
//        String messageBody = getActivationMailBody(mailParams.getId());
//        var emailTo = mailParams.getEmailTo();
//
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom(emailFrom);
//        mailMessage.setTo(emailTo);
//        mailMessage.setSubject(subject);
//        mailMessage.setText(messageBody);
//
//        javaMailSender.send(mailMessage);
//    }
//
//
//    private String getActivationMailBody(String id) {
//        var msg = String.format("Для завершения регистрации перейдите по ссылке:\n%s",
//                activationServiceUri);
//        return msg.replace("{id}", id);
//    }
//}
