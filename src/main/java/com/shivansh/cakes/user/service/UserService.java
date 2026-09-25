package com.shivansh.cakes.user.service;

import com.shivansh.cakes.user.dto.request.UserRequest;
import com.shivansh.cakes.user.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserResponse getProfile(String email);
    UserResponse updateProfile(String email, UserRequest request, MultipartFile avatar);
    void changePassword(String email, String currentPassword, String newPassword);

    // Admin operations
    List<UserResponse> getAllUsers();
    UserResponse blockUser(Long id);
    UserResponse unblockUser(Long id);
}
