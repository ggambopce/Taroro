package com.neocompany.taroro.domain.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.point.entity.PointWallet;
import com.neocompany.taroro.domain.users.User;

import jakarta.persistence.LockModeType;

public interface PointWalletRepository extends JpaRepository<PointWallet, Long> {

    Optional<PointWallet> findByUser(User user);

    @Query("SELECT w FROM PointWallet w WHERE w.user.userId = :userId")
    Optional<PointWallet> findByUserId(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM PointWallet w WHERE w.user.userId = :userId")
    Optional<PointWallet> findByUserIdForUpdate(@Param("userId") Long userId);
}
