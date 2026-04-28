package com.neocompany.taroro.domain.users;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserIdAndDeletedFalse(Long userId);

    List<User> findAllByUserIdIn(Collection<Long> userIds);
}
