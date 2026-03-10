package com.neocompany.taroro.domain.image;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    List<ProfileImage> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);

}
