package org.example.dto;

public class ErrorDto {
    private String error_message;

    public ErrorDto() {
    }

    public ErrorDto(String error_message) {
        this.error_message = error_message;
    }

    // Hàm này đặt tên đặc biệt để khớp với code AuthFilter của bạn
    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}