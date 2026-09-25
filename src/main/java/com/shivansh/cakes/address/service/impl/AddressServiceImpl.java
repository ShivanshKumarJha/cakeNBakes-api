package com.shivansh.cakes.address.service.impl;

import com.shivansh.cakes.address.dto.request.AddressRequest;
import com.shivansh.cakes.address.dto.response.AddressResponse;
import com.shivansh.cakes.address.entity.Address;
import com.shivansh.cakes.address.mapper.AddressMapper;
import com.shivansh.cakes.address.repository.AddressRepository;
import com.shivansh.cakes.address.service.AddressService;
import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.user.entity.User;
import com.shivansh.cakes.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository,
                              AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> findByUser(String email) {
        User user = getUserOrThrow(email);
        return addressRepository.findByUserId(user.getId()).stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse add(String email, AddressRequest request) {
        User user = getUserOrThrow(email);

        // First address defaults to selected
        boolean isFirst = addressRepository.findByUserId(user.getId()).isEmpty();

        Address address = new Address();
        populate(address, request);
        address.setUser(user);
        address.setSelected(isFirst);

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    public AddressResponse update(String email, Long addressId, AddressRequest request) {
        User user = getUserOrThrow(email);
        Address address = getAddressOrThrow(addressId, user.getId());

        populate(address, request);
        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    public void delete(String email, Long addressId) {
        User user = getUserOrThrow(email);
        Address address = getAddressOrThrow(addressId, user.getId());
        addressRepository.delete(address);
    }

    @Override
    public AddressResponse select(String email, Long addressId) {
        User user = getUserOrThrow(email);
        
        // Deselect all
        List<Address> all = addressRepository.findByUserId(user.getId());
        all.forEach(a -> a.setSelected(false));
        addressRepository.saveAll(all);

        // Select the chosen one
        Address address = getAddressOrThrow(addressId, user.getId());
        address.setSelected(true);
        return addressMapper.toResponse(addressRepository.save(address));
    }

    private void populate(Address address, AddressRequest request) {
        address.setName(request.name());
        address.setHouseName(request.houseName());
        address.setStreet(request.street());
        address.setLandMark(request.landMark());
        address.setPinCode(request.pinCode());
        address.setDistrict(request.district());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setContact(request.contact());
    }

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Address getAddressOrThrow(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found for current user");
        }
        return address;
    }
}
