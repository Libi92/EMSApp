package com.example.emsapp.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.emsapp.constants.UserState;
import com.example.emsapp.model.AppUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DatabaseManager {
    private static final String USER_DB_PATH = "user";
    private static final String USER_TYPE = "userType";
    private static final String TAG = DatabaseManager.class.getSimpleName();

    private final DatabaseReference userDbReference;

    private UserListener userListener;

    public DatabaseManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userDbReference = database.getReference(USER_DB_PATH);
    }

    public boolean createUser(AppUser appUser) {
        return userDbReference.child(appUser.getUId()).setValue(appUser).isSuccessful();
    }

    public void getUser(String uId) {

        userDbReference.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: " + snapshot);
                if (userListener != null) {
                    if (snapshot.getValue() != null) {
                        AppUser appUser = new AppUser();
                        appUser.setUserType(snapshot.child(USER_TYPE).getValue(String.class));
                        appUser.setAddress(snapshot.child("address").getValue(String.class));
                        appUser.setDisplayName(snapshot.child("displayName").getValue(String.class));
                        appUser.setEmail(snapshot.child("email").getValue(String.class));
                        appUser.setPhone(snapshot.child("phone").getValue(String.class));
                        String status = snapshot.child("status").getValue(String.class);
                        appUser.setStatus(status);

                        userListener.onGetUser(appUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getDetails());
            }
        });
    }

    public void getUsers() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<AppUser>> userListIndicator = new GenericTypeIndicator<List<AppUser>>() {
                };
                List<AppUser> appUserList = snapshot.getValue(userListIndicator);
                if (userListener != null) {
                    userListener.onListUser(appUserList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getDetails());
            }
        };
        userDbReference.orderByChild("status").equalTo(UserState.ACTIVE.getValue())
                .addValueEventListener(valueEventListener);
    }

    public static class Builder {
        private final DatabaseManager databaseManager;

        public Builder() {
            databaseManager = new DatabaseManager();
        }

        public Builder userListener(UserListener userListener) {
            databaseManager.userListener = userListener;
            return this;
        }

        public DatabaseManager build() {
            return this.databaseManager;
        }
    }
}
