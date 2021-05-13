package com.example.emsapp.db;

import androidx.annotation.NonNull;

import com.example.emsapp.model.Medicine;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PrescriptionDbManager {

    private static final String PRESCRIPTION_DB_PATH = "consultation/%s/prescription";
    private final DatabaseReference prescriptionDbReference;
    private PrescriptionListener prescriptionListener;

    private PrescriptionDbManager(String uId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        prescriptionDbReference = database.getReference(String.format(PRESCRIPTION_DB_PATH, uId));
    }

    public void setPrescriptionListener(PrescriptionListener prescriptionListener) {
        this.prescriptionListener = prescriptionListener;
    }

    public boolean addPrescription(List<Medicine> medicines) {
        return prescriptionDbReference.setValue(medicines).isSuccessful();
    }

    public void getPrescriptions() {
        prescriptionDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<Medicine>> typeIndicator = new GenericTypeIndicator<List<Medicine>>() {
                };

                List<Medicine> value = snapshot.getValue(typeIndicator);

                if (prescriptionListener != null && value != null) {
                    prescriptionListener.onPrescriptions(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class Builder {
        private String uId;
        private PrescriptionListener prescriptionListener;

        public Builder setUId(String uId) {
            this.uId = uId;
            return this;
        }

        public Builder setPrescriptionListener(PrescriptionListener prescriptionListener) {
            this.prescriptionListener = prescriptionListener;
            return this;
        }

        public PrescriptionDbManager build() {
            PrescriptionDbManager prescriptionDbManager = new PrescriptionDbManager(this.uId);

            if (prescriptionListener != null) {
                prescriptionDbManager.setPrescriptionListener(prescriptionListener);
            }

            return prescriptionDbManager;
        }
    }
}
