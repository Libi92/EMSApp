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
import java.util.Map;

public class MedicinesDbManager {
    private static final String MEDICINES_DB_PATH = "medicines/%s";
    private final MedicinesListener medicinesListener;
    private final DatabaseReference medicinesReference;

    private MedicinesDbManager(String userId, MedicinesListener medicinesListener) {
        this.medicinesListener = medicinesListener;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        medicinesReference = database.getReference(String.format(MEDICINES_DB_PATH, userId));
    }

    public boolean addMedicines(String consultationId, List<Medicine> medicines) {
        return medicinesReference.child(consultationId).setValue(medicines).isSuccessful();
    }

    public void getMedicines() {
        medicinesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    GenericTypeIndicator<Map<String, List<Medicine>>> typeIndicator = new GenericTypeIndicator<Map<String, List<Medicine>>>() {
                    };
                    Map<String, List<Medicine>> medicineMap = snapshot.getValue(typeIndicator);
                    if (medicineMap != null) {
                        medicineMap.values().stream().reduce((a, b) -> {
                            a.addAll(b);
                            return a;
                        }).ifPresent(medicines -> {
                            if (medicinesListener != null) {
                                medicinesListener.onMedicines(medicines);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class Builder {
        private String userId;
        private MedicinesListener medicinesListener;

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setMedicinesListener(MedicinesListener medicinesListener) {
            this.medicinesListener = medicinesListener;
            return this;
        }

        public MedicinesDbManager build() {
            return new MedicinesDbManager(userId, medicinesListener);
        }
    }
}
