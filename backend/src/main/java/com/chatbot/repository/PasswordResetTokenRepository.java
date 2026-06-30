package com.chatbot.repository;

import com.chatbot.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHashAndRevokedFalse(String tokenHash);

    @Modifying
    @Query("UPDATE PasswordResetToken prt SET prt.revoked = true WHERE prt.user.id = :userId AND prt.revoked = false")
    void revokeAllActiveByUserId(@Param("userId") Long userId);
}
