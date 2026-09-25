package com.shivansh.cakes.banner.repository;

import com.shivansh.cakes.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {
}
