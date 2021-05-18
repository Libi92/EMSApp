package com.example.emsapp.db;

import com.example.emsapp.model.MedicalRecords;

import java.util.List;

public interface MedicalRecordsListener {
    void onGetMedicalRecords(List<MedicalRecords> medicalRecords);
}
