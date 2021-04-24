package com.example.emsapp.db;

import com.example.emsapp.model.ConsultationRequest;

import java.util.List;

public interface ConsultationListener {
    void onConsultations(List<ConsultationRequest> consultationRequests);
}
