package com.tennisch.tennisCH.payload.response;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private Map<String, String> errors;
}