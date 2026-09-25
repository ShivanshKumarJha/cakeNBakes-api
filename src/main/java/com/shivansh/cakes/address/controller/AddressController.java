package com.shivansh.cakes.address.controller;

import com.shivansh.cakes.address.dto.request.AddressRequest;
import com.shivansh.cakes.address.dto.response.AddressResponse;
import com.shivansh.cakes.address.service.AddressService;
import com.shivansh.cakes.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/profile/address")
@Tag(name = "Addresses", description = "User address management")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "Get all addresses for logged in user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> findByUser(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(addressService.findByUser(principal.getName())));
    }

    @PostMapping
    @Operation(summary = "Add a new address")
    public ResponseEntity<ApiResponse<AddressResponse>> add(
            Principal principal,
            @Valid @RequestBody AddressRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added.", addressService.add(principal.getName(), request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address")
    public ResponseEntity<ApiResponse<AddressResponse>> update(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Address updated.", addressService.update(principal.getName(), id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<ApiResponse<Void>> delete(Principal principal, @PathVariable Long id) {
        addressService.delete(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted."));
    }

    @PatchMapping("/{id}/select")
    @Operation(summary = "Mark an address as selected for checkout")
    public ResponseEntity<ApiResponse<AddressResponse>> select(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Address selected.", addressService.select(principal.getName(), id)));
    }
}
