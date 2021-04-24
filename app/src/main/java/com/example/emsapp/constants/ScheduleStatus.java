package com.example.emsapp.constants;

public enum ScheduleStatus {
    PENDING("Pending"),
    SCHEDULED("Scheduled"),
    COMPLETE("Complete");

    private String value;

    ScheduleStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
