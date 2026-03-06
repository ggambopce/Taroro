package com.neocompany.taroro.domain.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neocompany.taroro.domain.point.entity.PointCharge;

public interface PointChargeRepository extends JpaRepository<PointCharge, Long> {
    Optional<PointCharge> findByOrderId(String orderId);
}
