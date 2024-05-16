package vn.vt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vt.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
