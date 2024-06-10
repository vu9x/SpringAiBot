package vn.vt.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {

    void processDocMessage(Update update);

    void processPhotoMessage(Update update);

}
