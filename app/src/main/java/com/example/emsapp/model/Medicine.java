package com.example.emsapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Medicine {
    private String medicineName;
    private String prescribedBy;
    private Integer daysRemaining;
    private Float morningDoses;
    private Float afterNoonDoses;
    private Float nightDoses;
}
