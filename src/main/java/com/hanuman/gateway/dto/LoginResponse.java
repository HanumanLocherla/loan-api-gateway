package com.hanuman.gateway.dto;

public record LoginResponse(String accessToken,
                            String tokenType,
                            long expiresInSeconds) {
}
