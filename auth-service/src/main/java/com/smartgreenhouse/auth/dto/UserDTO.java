package com.smartgreenhouse.auth.dto;

public record UserDTO(
    String username,
    String password,
    String role,        // EXPERT, STANDARD, MINIMAL
    String defaultMode
) {}
