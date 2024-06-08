package vn.vt.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import vn.vt.dao.AppUserDAO;
import vn.vt.dao.RawDataDAO;
import vn.vt.entity.AppDocument;
import vn.vt.entity.AppPhoto;
import vn.vt.entity.AppUser;
import vn.vt.exceptions.UploadFileException;
import vn.vt.service.FileService;
import vn.vt.service.ProducerService;
import vn.vt.service.AppUserService;
import vn.vt.service.enums.LinkType;
import vn.vt.service.enums.ServiceCommand;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static vn.vt.enums.UserState.*;

class MainServiceImplTest {

    @Mock
    private RawDataDAO rawDataDAO;

    @Mock
    private ProducerService producerService;

    @Mock
    private AppUserDAO appUserDAO;

    @Mock
    private FileService fileService;

    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private MainServiceImpl mainService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessTextMessage() {
        // Arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User telegramUser = mock(User.class);
        AppUser appUser = new AppUser();
        appUser.setState(BASIC_STATE);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(telegramUser);
        when(message.getText()).thenReturn("/start");
        when(appUserDAO.findByTelegramUserId(telegramUser.getId())).thenReturn(java.util.Optional.of(appUser));

        // Act
        mainService.processTextMessage(update);

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(producerService, times(1)).producerAnswer(captor.capture());
        assertEquals("Приветствую! Чтобы посмотреть список доступных команд введите /help", captor.getValue().getText());
    }

    @Test
    void testProcessDocMessage() throws UploadFileException {
        // Arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        AppDocument doc = mock(AppDocument.class);
        User telegramUser = mock(User.class);
        AppUser appUser = new AppUser();
        appUser.setState(BASIC_STATE);
        appUser.setIsActive(true);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(telegramUser);
        when(appUserDAO.findByTelegramUserId(telegramUser.getId())).thenReturn(java.util.Optional.of(appUser));
        when(fileService.processDoc(any())).thenReturn(doc);
        when(fileService.generateLink(doc.getId(), LinkType.GET_DOC)).thenReturn("link");

        // Act
        mainService.processDocMessage(update);

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(producerService, times(1)).producerAnswer(captor.capture());
        assertEquals("Документ успешно загружен!Ссылка для скачивания: link", captor.getValue().getText());
    }

    @Test
    void testProcessPhotoMessage() throws UploadFileException {
        // Arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        AppPhoto appPhoto = mock(AppPhoto.class);
        User telegramUser = mock(User.class);
        AppUser appUser = new AppUser();
        appUser.setState(BASIC_STATE);
        appUser.setIsActive(true);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(telegramUser);
        when(appUserDAO.findByTelegramUserId(telegramUser.getId())).thenReturn(java.util.Optional.of(appUser));
        when(fileService.processPhoto(any())).thenReturn(appPhoto);
        when(fileService.generateLink(appPhoto.getId(), LinkType.GET_PHOTO)).thenReturn("link");

        // Act
        mainService.processPhotoMessage(update);

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(producerService, times(1)).producerAnswer(captor.capture());
        assertEquals("Фото успешно загружено! Ссылка для скачивания: link", captor.getValue().getText());
    }

}
