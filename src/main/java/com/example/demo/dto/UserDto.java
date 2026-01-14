package com.example.demo.dto;

public record UserDto(
    String id,
    String username,
    String email,
    boolean isBlocked
) {
}
