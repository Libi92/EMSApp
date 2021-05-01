package com.example.emsapp.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicine implements Serializable {
    private String medicineName;
    private String prescribedBy;
    private Integer daysRemaining;
    private Float morningDoses;
    private Float afterNoonDoses;
    private Float nightDoses;
}
