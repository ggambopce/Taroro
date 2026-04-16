package com.neocompany.taroro.domain.masterauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neocompany.taroro.domain.masterauth.entity.MasterVerification;

public interface MasterVerificationRepository extends JpaRepository<MasterVerification, Long> {

    Optional<MasterVerification> findByMasterId(Long masterId);
}
