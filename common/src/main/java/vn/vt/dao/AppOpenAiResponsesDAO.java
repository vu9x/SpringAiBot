package vn.vt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vt.entity.AppOpenAiResponses;

public interface AppOpenAiResponsesDAO extends JpaRepository<AppOpenAiResponses, Long> {
}
