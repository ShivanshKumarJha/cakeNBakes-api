package com.shivansh.cakes.user.mapper;

import com.shivansh.cakes.user.dto.response.UserResponse;
import com.shivansh.cakes.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getContactNumber(),
                user.getImage(),
                user.getIsVerified(),
                user.getStatus()
        );
    }
}
