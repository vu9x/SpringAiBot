package vn.vt.service.impl;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Update;
import vn.vt.service.MainService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Log4j2
public class ConsumerServiceImplTest {

    @Mock
    private MainService mainService;

    @InjectMocks
    private ConsumerServiceImpl consumerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConsumeTextMessageUpdates() {
        Update update = new Update();

        consumerService.consumeTextMessageUpdates(update);

        verify(mainService, times(1)).processTextMessage(update);
    }

    @Test
    public void testConsumeDocMessageUpdates() {
        Update update = new Update();

        consumerService.consumeDocMessageUpdates(update);

        verify(mainService, times(1)).processDocMessage(update);
    }

    @Test
    public void testConsumePhotoMessageUpdates() {
        Update update = new Update();

        consumerService.consumePhotoMessageUpdates(update);

        verify(mainService, times(1)).processPhotoMessage(update);
    }

    @Test
    public void testConsumeTextMessageUpdates_ExceptionLogging() {
        Update update = new Update();
        doThrow(new RuntimeException("Test exception")).when(mainService).processTextMessage(update);

        consumerService.consumeTextMessageUpdates(update);

        verify(mainService, times(1)).processTextMessage(update);
        // Здесь мы могли бы проверить, что логгирование ошибки произошло, если бы у нас была возможность мокировать логгер
    }

    @Test
    public void testConsumeDocMessageUpdates_ExceptionLogging() {
        Update update = new Update();
        doThrow(new RuntimeException("Test exception")).when(mainService).processDocMessage(update);

        consumerService.consumeDocMessageUpdates(update);

        verify(mainService, times(1)).processDocMessage(update);
        // Здесь мы могли бы проверить, что логгирование ошибки произошло, если бы у нас была возможность мокировать логгер
    }

    @Test
    public void testConsumePhotoMessageUpdates_ExceptionLogging() {
        Update update = new Update();
        doThrow(new RuntimeException("Test exception")).when(mainService).processPhotoMessage(update);

        consumerService.consumePhotoMessageUpdates(update);

        verify(mainService, times(1)).processPhotoMessage(update);
        // Здесь мы могли бы проверить, что логгирование ошибки произошло, если бы у нас была возможность мокировать логгер
    }
}
