package com.shivansh.cakes.address.service;

import com.shivansh.cakes.address.dto.request.AddressRequest;
import com.shivansh.cakes.address.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> findByUser(String email);
    AddressResponse add(String email, AddressRequest request);
    AddressResponse update(String email, Long addressId, AddressRequest request);
    void delete(String email, Long addressId);
    AddressResponse select(String email, Long addressId);
}
