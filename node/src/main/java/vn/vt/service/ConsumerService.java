package vn.vt.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {

    void consumeDocMessageUpdates(Update update);

    void consumePhotoMessageUpdates(Update update);
}
