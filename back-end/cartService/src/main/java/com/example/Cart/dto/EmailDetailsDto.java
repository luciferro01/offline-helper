package com.example.Cart.dto;

public class EmailDetailsDto {
    private String subject;
    private String message;
    private String recipient;

    public EmailDetailsDto(String subject, String message, String recipient) {
        this.subject = subject;
        this.message = message;
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
