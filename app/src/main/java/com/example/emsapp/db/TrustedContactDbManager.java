package com.example.emsapp.db;

import androidx.annotation.NonNull;

import com.example.emsapp.model.TrustedContact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TrustedContactDbManager {

    private static final String CONTACTS_DB_PATH = "user/%s/trusted_contacts";
    private final DatabaseReference consultDbReference;
    private TrustedContactListener contactListener;


    private TrustedContactDbManager(String uId, TrustedContactListener contactListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        consultDbReference = database.getReference(String.format(CONTACTS_DB_PATH, uId));
        this.contactListener = contactListener;
    }

    public void saveContact(List<TrustedContact> trustedContacts) {
        consultDbReference.setValue(trustedContacts);
    }

    public void getContacts() {
        consultDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<TrustedContact>> typeIndicator = new GenericTypeIndicator<List<TrustedContact>>() {
                };

                List<TrustedContact> contactList = snapshot.getValue(typeIndicator);
                if (contactListener != null && contactList != null) {
                    contactListener.onGetTrustedContacts(contactList);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public static class Builder {
        private String uId;
        private TrustedContactListener contactListener;

        public Builder setUId(String uId) {
            this.uId = uId;
            return this;
        }

        public Builder setContactListener(TrustedContactListener contactListener) {
            this.contactListener = contactListener;
            return this;
        }

        public TrustedContactDbManager build() {
            return new TrustedContactDbManager(uId, contactListener);
        }
    }
}
