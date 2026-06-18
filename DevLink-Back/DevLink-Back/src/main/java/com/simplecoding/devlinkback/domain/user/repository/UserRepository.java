package com.simplecoding.devlinkback.domain.user.repository;

import com.simplecoding.devlinkback.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    // 탈퇴 여부 포함 중복 체크 (재가입 허용용)
    boolean existsByEmailAndDeleteYn(String email, String deleteYn);

    boolean existsByNicknameAndDeleteYn(String nickname, String deleteYn);

    // ✅ 관리자 - 통계용
    long countByDeleteYn(String deleteYn);
    long countByDeleteYnAndBanned(String deleteYn, Boolean banned);

    // ✅ 관리자 - 회원 목록
    List<User> findByDeleteYnOrderByCreatedAtDesc(String deleteYn);
}