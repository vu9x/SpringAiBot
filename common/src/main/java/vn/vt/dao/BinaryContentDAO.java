package vn.vt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vt.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
