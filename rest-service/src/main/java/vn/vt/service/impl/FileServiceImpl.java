package vn.vt.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.vt.dao.AppDocumentDAO;
import vn.vt.dao.AppPhotoDAO;
import vn.vt.entity.AppDocument;
import vn.vt.entity.AppPhoto;
import vn.vt.entity.BinaryContent;
import vn.vt.service.FileService;
import vn.vt.utils.CryptoTool;


@Service
@Log4j2
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument getDocument(String hash) {
        var id = cryptoTool.idOf(hash);
        if(id == null){
            return null;
        }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = cryptoTool.idOf(hash);
        if(id == null){
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }

}
