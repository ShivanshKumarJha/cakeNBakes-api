package com.shivansh.cakes.auth.dto.response;

/**
 * Returned on successful user login.
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        UserInfo user
) {
    public record UserInfo(Long id, String name, String email, String image) {}

    public static AuthResponse of(String token, Long id, String name, String email, String image) {
        return new AuthResponse(token, "Bearer", new UserInfo(id, name, email, image));
    }
}
