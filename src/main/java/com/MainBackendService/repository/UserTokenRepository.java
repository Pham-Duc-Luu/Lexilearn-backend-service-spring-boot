package com.MainBackendService.repository;

import com.MainBackendService.model.UserToken;
import com.MainBackendService.model.UserTokenType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Integer> {
    List<UserToken> findByUser_UserId(Integer userId);

    Optional<UserToken> findTopByUserUserEmailAndUTTypeOrderByUTExpiredAtDesc(String email, UserTokenType utType);

    @Query("SELECT ut FROM UserToken ut WHERE ut.user.userEmail = :email AND ut.UTType = 'OTP' ORDER BY ut.UTExpiredAt DESC")
    Page<UserToken> findOtpByEmail(String email, Pageable pageable);

    @Modifying
    @Query("DELETE FROM UserToken ut WHERE ut.UTExpiredAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

}
