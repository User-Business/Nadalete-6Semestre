package com.example.atlazdb.dto;

public record ErrorResponseDTO(int status, String error, String message) {
}
