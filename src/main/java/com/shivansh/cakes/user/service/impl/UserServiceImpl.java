package com.shivansh.cakes.user.service.impl;

import com.shivansh.cakes.common.exception.BusinessException;
import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.common.service.FileStorageService;
import com.shivansh.cakes.user.dto.request.UserRequest;
import com.shivansh.cakes.user.dto.response.UserResponse;
import com.shivansh.cakes.user.entity.User;
import com.shivansh.cakes.user.mapper.UserMapper;
import com.shivansh.cakes.user.repository.UserRepository;
import com.shivansh.cakes.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           PasswordEncoder passwordEncoder, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {
        return userMapper.toResponse(getOrThrow(email));
    }

    @Override
    public UserResponse updateProfile(String email, UserRequest request, MultipartFile avatar) {
        User user = getOrThrow(email);
        user.setName(request.name());
        if (request.contactNumber() != null && !request.contactNumber().isBlank()) {
            user.setContactNumber(request.contactNumber());
        }
        if (avatar != null && !avatar.isEmpty()) {
            fileStorageService.delete(user.getImage());
            user.setImage(fileStorageService.store(avatar, "user-img"));
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = getOrThrow(email);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(userMapper::toResponse).toList();
    }

    @Override
    public UserResponse blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setStatus(true); // true = blocked
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse unblockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setStatus(false);
        return userMapper.toResponse(userRepository.save(user));
    }

    private User getOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
