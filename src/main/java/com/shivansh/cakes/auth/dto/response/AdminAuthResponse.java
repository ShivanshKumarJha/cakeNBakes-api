package com.shivansh.cakes.auth.dto.response;

/** Returned on successful admin login — just the token. */
public record AdminAuthResponse(String accessToken, String tokenType) {
    public static AdminAuthResponse of(String token) {
        return new AdminAuthResponse(token, "Bearer");
    }
}
