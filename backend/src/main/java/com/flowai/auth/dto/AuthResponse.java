package com.flowai.auth.dto;

import java.util.UUID;

/**
 * Authentication response DTO.
 *
 * SECURITY: Never include the user's password or password_hash here.
 * Never include the full User entity. Only expose what the client needs.
 */
public record AuthResponse(
        String token,
        String tokenType,
        UUID userId,
        String email,
        String fullName,
        String role
) {
    /**
     * Convenience factory — token type is always "Bearer".
     */
    public static AuthResponse of(String token, com.flowai.auth.User user) {
        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );
    }
}