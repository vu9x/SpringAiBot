package vn.vt.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import vn.vt.entity.AppDocument;
import vn.vt.entity.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
