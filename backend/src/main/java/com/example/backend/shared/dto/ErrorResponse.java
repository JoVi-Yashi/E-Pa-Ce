package com.example.backend.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private Integer status;

    public ErrorResponse(String message, String details, Integer status) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.details = details;
        this.status = status;
    }
}
