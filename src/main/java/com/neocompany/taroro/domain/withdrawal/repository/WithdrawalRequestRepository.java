package com.neocompany.taroro.domain.withdrawal.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.withdrawal.entity.WithdrawalRequest;
import com.neocompany.taroro.domain.withdrawal.enums.WithdrawalStatus;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {

    Slice<WithdrawalRequest> findByMaster_MasterIdOrderByIdDesc(Long masterId, Pageable pageable);

    @Query("SELECT w FROM WithdrawalRequest w WHERE (:status IS NULL OR w.status = :status) ORDER BY w.id DESC")
    Slice<WithdrawalRequest> findAllByStatus(@Param("status") WithdrawalStatus status, Pageable pageable);
}
