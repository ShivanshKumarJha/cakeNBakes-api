package com.shivansh.cakes.user.controller;

import com.shivansh.cakes.common.response.ApiResponse;
import com.shivansh.cakes.user.dto.request.UserRequest;
import com.shivansh.cakes.user.dto.response.UserResponse;
import com.shivansh.cakes.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@Tag(name = "Profile", description = "User profile management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** GET /api/profile — returns logged-in user's profile */
    @GetMapping("/api/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(principal.getName())));
    }

    /** PUT /api/profile — update name, contact, avatar */
    @PutMapping(value = "/api/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Principal principal,
            @Valid @RequestPart("data") UserRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated.",
                userService.updateProfile(principal.getName(), request, avatar)));
    }

    /** POST /api/profile/change-password */
    @PostMapping("/api/profile/change-password")
    @Operation(summary = "Change password (current + new)")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Principal principal,
            @RequestBody Map<String, String> body
    ) {
        userService.changePassword(
                principal.getName(),
                body.get("currentPassword"),
                body.get("newPassword")
        );
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully."));
    }

    // ── Admin user management ──────────────────────────────────────────────

    @GetMapping("/api/admin/users")
    @Tag(name = "Admin - Users")
    @Operation(summary = "List all users (ADMIN)")
    public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    @PatchMapping("/api/admin/users/{id}/block")
    @Tag(name = "Admin - Users")
    @Operation(summary = "Block user (ADMIN)")
    public ResponseEntity<ApiResponse<UserResponse>> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User blocked.", userService.blockUser(id)));
    }

    @PatchMapping("/api/admin/users/{id}/unblock")
    @Tag(name = "Admin - Users")
    @Operation(summary = "Unblock user (ADMIN)")
    public ResponseEntity<ApiResponse<UserResponse>> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User unblocked.", userService.unblockUser(id)));
    }
}
