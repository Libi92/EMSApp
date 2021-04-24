package com.example.emsapp.db;

import androidx.annotation.NonNull;

import com.example.emsapp.constants.UserType;
import com.example.emsapp.model.AppUser;
import com.example.emsapp.model.ConsultationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class ConsultationDbManager {

    private static final String CONSULT_DB_PATH = "consultation";

    private final DatabaseReference consultDbReference;
    private ConsultationListener consultationListener;

    public ConsultationDbManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        consultDbReference = database.getReference(CONSULT_DB_PATH);
    }

    public boolean addConsultation(ConsultationRequest consultationRequest) {
        String uId = consultationRequest.getUId() != null ? consultationRequest.getUId() : UUID.randomUUID().toString();
        return consultDbReference.child(uId).setValue(consultationRequest).isSuccessful();
    }

    public void getConsultations(AppUser appUser) {
        String child = "fromUser/email";
        if (UserType.DOCTOR.getValue().equals(appUser.getUserType())) {
            child = "toDoctor/email";
        }
        consultDbReference.orderByChild(child).equalTo(appUser.getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<Map<String, ConsultationRequest>> typeIndicator = new GenericTypeIndicator<Map<String, ConsultationRequest>>() {
                        };
                        Collection<ConsultationRequest> values = snapshot.getValue(typeIndicator).values();
                        if (consultationListener != null) {
                            consultationListener.onConsultations(new ArrayList<>(values));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static class Builder {
        private final ConsultationDbManager dbManager;

        public Builder() {
            this.dbManager = new ConsultationDbManager();
        }

        public Builder setConsultationListener(ConsultationListener consultationListener) {
            this.dbManager.consultationListener = consultationListener;
            return this;
        }

        public ConsultationDbManager build() {
            return this.dbManager;
        }
    }
}
