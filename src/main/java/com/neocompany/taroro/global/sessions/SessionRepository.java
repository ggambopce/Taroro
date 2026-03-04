package com.neocompany.taroro.global.sessions;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findBySessionId(String sessionId);

    long deleteBySessionId(String sessionId);

    @Modifying
    @Query("delete from Session s where s.expiresAt < :now")
    int deleteExpired(@Param("now") Instant now);

}
