package com.example.emsapp.db;

import androidx.annotation.NonNull;

import com.example.emsapp.model.MedicalRecords;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MedicalRecordsDbManager {
    private static final String RECORDS_DB_PATH = "user/%s/medical_records";
    private final DatabaseReference recordsDbReference;
    private final MedicalRecordsListener recordsListener;

    private MedicalRecordsDbManager(String uId, MedicalRecordsListener recordsListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        recordsDbReference = database.getReference(String.format(RECORDS_DB_PATH, uId));
        this.recordsListener = recordsListener;
    }

    public void saveMedicalRecords(List<MedicalRecords> medicalRecordsList) {
        recordsDbReference.setValue(medicalRecordsList);
    }

    public void getMedicalRecords() {
        recordsDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<MedicalRecords>> typeIndicator = new GenericTypeIndicator<List<MedicalRecords>>() {
                };

                List<MedicalRecords> medicalRecords = snapshot.getValue(typeIndicator);
                if (medicalRecords != null && recordsListener != null) {
                    recordsListener.onGetMedicalRecords(medicalRecords);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public static class Builder {
        private String uId;
        private MedicalRecordsListener recordsListener;

        public Builder setUId(String uId) {
            this.uId = uId;
            return this;
        }

        public Builder setRecordsListener(MedicalRecordsListener recordsListener) {
            this.recordsListener = recordsListener;
            return this;
        }

        public MedicalRecordsDbManager build() {
            return new MedicalRecordsDbManager(uId, recordsListener);
        }
    }
}
