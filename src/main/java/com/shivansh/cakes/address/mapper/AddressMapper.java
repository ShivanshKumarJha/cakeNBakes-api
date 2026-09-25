package com.shivansh.cakes.address.mapper;

import com.shivansh.cakes.address.dto.response.AddressResponse;
import com.shivansh.cakes.address.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public AddressResponse toResponse(Address a) {
        return new AddressResponse(
                a.getId(), a.getName(), a.getHouseName(), a.getStreet(),
                a.getLandMark(), a.getPinCode(), a.getDistrict(),
                a.getState(), a.getCountry(), a.getContact(), a.getSelected()
        );
    }
}
