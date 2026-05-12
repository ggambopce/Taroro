package com.neocompany.taroro.domain.payment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.neocompany.taroro.domain.payment.entity.MasterEarningLedger;

public interface MasterEarningLedgerRepository extends JpaRepository<MasterEarningLedger, Long> {

    Slice<MasterEarningLedger> findByMaster_MasterIdOrderByIdDesc(Long masterId, Pageable pageable);
}
