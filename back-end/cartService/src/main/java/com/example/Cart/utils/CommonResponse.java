package com.example.Cart.utils;

public class CommonResponse<T> {
    private boolean success;
    private int statusCode;
    private T data;
    private String message;

    public CommonResponse() {}

    public CommonResponse(boolean success, int statusCode, T data, String message) {
        this.success = success;
        this.statusCode = statusCode;
        this.data = data;
        this.message = message;
    }

    public static <T> CommonResponse<T> success(T data, int statusCode, String message) {
        return new CommonResponse<>(true, statusCode, data, message);
    }

    public static <T> CommonResponse<T> failure(String message, int statusCode) {
        return new CommonResponse<>(false, statusCode, null, message);
    }

    // Getters and setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
