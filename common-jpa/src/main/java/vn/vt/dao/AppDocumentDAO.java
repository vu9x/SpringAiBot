package vn.vt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vt.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
