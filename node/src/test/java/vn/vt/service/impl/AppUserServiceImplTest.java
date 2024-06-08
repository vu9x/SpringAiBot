package vn.vt.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import vn.vt.dao.AppUserDAO;
import vn.vt.dto.MailParams;
import vn.vt.entity.AppUser;
import vn.vt.enums.UserState;
import vn.vt.service.AppUserService;
import vn.vt.utils.CryptoTool;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.lang.reflect.Method;
import java.util.Optional;
import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserServiceImplTest {

    @Mock
    private AppUserDAO appUserDAO;

    @Mock
    private CryptoTool cryptoTool;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @InjectMocks
    private AppUserServiceImpl appUserService;


    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);

        // Injecting the @Value field using reflection
        Field registrationMailQueueField = AppUserServiceImpl.class.getDeclaredField("registrationMailQueue");
        registrationMailQueueField.setAccessible(true);
        registrationMailQueueField.set(appUserService, "test-queue");
    }

    @Test
    void testRegisterUser_AlreadyActive() {
        // Arrange
        AppUser appUser = new AppUser();
        appUser.setIsActive(true);

        // Act
        String result = appUserService.registerUser(appUser);

        // Assert
        assertEquals("Вы уже зарегистрированы!", result);
    }

    @Test
    void testRegisterUser_EmailAlreadySet() {
        // Arrange
        AppUser appUser = new AppUser();
        appUser.setIsActive(false);
        appUser.setEmail("test@example.com");

        // Act
        String result = appUserService.registerUser(appUser);

        // Assert
        assertEquals("Вам на почту уже было отправлено письмо. Перейдите по ссылке в письме для подтверждения регистрации.", result);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        AppUser appUser = new AppUser();
        appUser.setIsActive(false);
        appUser.setEmail(null);

        // Act
        String result = appUserService.registerUser(appUser);

        // Assert
        assertEquals("Введите, пожалуйста, ваш email:", result);
        assertEquals(UserState.WAIT_FOR_EMAIL_STATE, appUser.getState());
        verify(appUserDAO, times(1)).save(appUser);
    }

    @Test
    void testSetEmail_InvalidEmail() {
        // Arrange
        AppUser appUser = new AppUser();
        String email = "invalid-email";

        // Act
        String result = appUserService.setEmail(appUser, email);

        // Assert
        assertEquals("Введите, пожалуйста, корректный email. Для отмены команды введите /cancel", result);
    }

    @Test
    void testSetEmail_EmailAlreadyUsed() {
        // Arrange
        AppUser appUser = new AppUser();
        String email = "test@example.com";
        when(appUserDAO.findByEmail(email)).thenReturn(Optional.of(new AppUser()));

        // Act
        String result = appUserService.setEmail(appUser, email);

        // Assert
        assertEquals("Этот email уже используется. Введите корректный email. Для отмены команды введите /cancel", result);
    }

    @Test
    void testSetEmail_Success() {
        // Arrange
        AppUser appUser = new AppUser();
        appUser.setId(1L);
        String email = "test@example.com";
        when(appUserDAO.findByEmail(email)).thenReturn(Optional.empty());
        when(appUserDAO.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cryptoTool.hashOf(appUser.getId())).thenReturn("hashed-id");

        // Act
        String result = appUserService.setEmail(appUser, email);

        // Assert
        assertEquals("Вам на почту было отправлено письмо. Перейдите по ссылке в письме для подтверждения регистрации.", result);
        assertEquals(email, appUser.getEmail());
        assertEquals(UserState.BASIC_STATE, appUser.getState());
        verify(appUserDAO, times(1)).save(appUser);

        ArgumentCaptor<MailParams> captor = ArgumentCaptor.forClass(MailParams.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("test-queue"), captor.capture());
        MailParams mailParams = captor.getValue();
        assertEquals("hashed-id", mailParams.getId());
        assertEquals(email, mailParams.getEmailTo());
    }

    @Test
    void testSendRegistrationMail() throws Exception {
        // Arrange
        String cryptoUserId = "hashed-id";
        String email = "test@example.com";

        // Act
        Method sendRegistrationMail = AppUserServiceImpl.class.getDeclaredMethod("sendRegistrationMail", String.class, String.class);
        sendRegistrationMail.setAccessible(true);
        sendRegistrationMail.invoke(appUserService, cryptoUserId, email);

        // Assert
        ArgumentCaptor<MailParams> captor = ArgumentCaptor.forClass(MailParams.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("test-queue"), captor.capture());
        MailParams mailParams = captor.getValue();
        assertEquals(cryptoUserId, mailParams.getId());
        assertEquals(email, mailParams.getEmailTo());
    }
}
