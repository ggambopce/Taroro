package com.neocompany.taroro.domain.masterauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neocompany.taroro.domain.masterauth.entity.MasterSettlement;

public interface MasterSettlementRepository extends JpaRepository<MasterSettlement, Long> {

    Optional<MasterSettlement> findByMasterId(Long masterId);

    boolean existsByMasterId(Long masterId);
}
