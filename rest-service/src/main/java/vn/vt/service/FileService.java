package vn.vt.service;

import org.springframework.core.io.FileSystemResource;
import vn.vt.entity.AppDocument;
import vn.vt.entity.AppPhoto;
import vn.vt.entity.BinaryContent;

public interface FileService {

    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);
}
