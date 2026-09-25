package com.shivansh.cakes.address.dto.response;

public record AddressResponse(
        Long id,
        String name,
        String houseName,
        String street,
        String landMark,
        String pinCode,
        String district,
        String state,
        String country,
        String contact,
        Boolean selected
) {}
