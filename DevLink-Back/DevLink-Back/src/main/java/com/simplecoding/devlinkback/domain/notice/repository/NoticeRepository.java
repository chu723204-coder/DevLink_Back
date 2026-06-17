package com.simplecoding.devlinkback.domain.notice.repository;

import com.simplecoding.devlinkback.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByDeleteYnOrderByCreatedAtDesc(String deleteYn);
    Optional<Notice> findByNoticeIdAndDeleteYn(Long noticeId, String deleteYn);
}