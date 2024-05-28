package vn.vt.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import vn.vt.entity.AppDocument;
import vn.vt.entity.AppPhoto;
import vn.vt.service.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long fileId, LinkType linkType);
}
