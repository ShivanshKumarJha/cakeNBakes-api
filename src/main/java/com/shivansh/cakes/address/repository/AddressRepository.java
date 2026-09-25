package com.shivansh.cakes.address.repository;

import com.shivansh.cakes.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
}
