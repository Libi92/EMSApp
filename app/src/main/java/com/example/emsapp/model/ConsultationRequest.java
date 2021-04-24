package com.example.emsapp.model;

import java.util.Calendar;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequest {
    private String uId;
    private AppUser fromUser;
    private AppUser toDoctor;
    private String scheduleStatus;
    private Long schedulesDateTime;
    private List<Medicine> medicineList;

    @Builder.Default
    private Long requestedOn = Calendar.getInstance().getTime().getTime();
}
