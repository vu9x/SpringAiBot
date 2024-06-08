package vn.vt.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import vn.vt.dto.MailParams;

import java.lang.reflect.*;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MailSenderServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private MailSenderServiceImpl mailSenderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mailSenderService = new MailSenderServiceImpl(javaMailSender);

        // Устанавливаем значения приватных полей с помощью reflection
        ReflectionTestUtils.setField(mailSenderService, "emailFrom", "test@example.com");
        ReflectionTestUtils.setField(mailSenderService, "activationServiceUri", "http://example.com/activate/{id}");

    }

    @Test
    void testSend() {
        // Arrange
        MailParams mailParams = new MailParams();
        mailParams.setId("12345");
        mailParams.setEmailTo("recipient@example.com");

        ArgumentCaptor<SimpleMailMessage> mailMessageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        mailSenderService.send(mailParams);

        // Assert
        verify(javaMailSender, times(1)).send(mailMessageCaptor.capture());
        SimpleMailMessage capturedMailMessage = mailMessageCaptor.getValue();

        assertEquals("test@example.com", capturedMailMessage.getFrom());
        assertEquals("recipient@example.com", Objects.requireNonNull(capturedMailMessage.getTo())[0]);
        assertEquals("Активация учетной записи", capturedMailMessage.getSubject());
        assertEquals("Для завершения регистрации перейдите по ссылке:\nhttp://example.com/activate/12345", capturedMailMessage.getText());
    }

    @Test
    void testGetActivationMailBody() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Act
        Method method = MailSenderServiceImpl.class.getDeclaredMethod("getActivationMailBody", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(mailSenderService, "12345");

        // Assert
        assertEquals("Для завершения регистрации перейдите по ссылке:\nhttp://example.com/activate/12345", result);
    }
}
