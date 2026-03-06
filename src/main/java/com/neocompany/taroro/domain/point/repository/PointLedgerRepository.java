package com.neocompany.taroro.domain.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neocompany.taroro.domain.point.entity.PointLedger;

public interface PointLedgerRepository extends JpaRepository<PointLedger, Long> {
}
