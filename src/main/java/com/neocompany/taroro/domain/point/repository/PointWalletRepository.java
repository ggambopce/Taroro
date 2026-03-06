package com.neocompany.taroro.domain.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neocompany.taroro.domain.point.entity.PointWallet;
import com.neocompany.taroro.domain.users.User;

public interface PointWalletRepository extends JpaRepository<PointWallet, Long> {
    Optional<PointWallet> findByUser(User user);
}
