package com.example.emsapp.constants;

public enum UserType {
    USER("User"),
    DOCTOR("Doctor"),
    ADMIN("Admin");

    private final String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
