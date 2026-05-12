package com.neocompany.taroro.domain.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.payment.entity.MasterEarningWallet;

import jakarta.persistence.LockModeType;

public interface MasterEarningWalletRepository extends JpaRepository<MasterEarningWallet, Long> {

    @Query("SELECT w FROM MasterEarningWallet w WHERE w.master.masterId = :masterId")
    Optional<MasterEarningWallet> findByMasterId(@Param("masterId") Long masterId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM MasterEarningWallet w WHERE w.master.masterId = :masterId")
    Optional<MasterEarningWallet> findByMasterIdForUpdate(@Param("masterId") Long masterId);
}
