package vn.vt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vt.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
