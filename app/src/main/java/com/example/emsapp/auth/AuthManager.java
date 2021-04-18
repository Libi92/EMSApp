package com.example.emsapp.auth;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.emsapp.db.DatabaseManager;
import com.example.emsapp.db.UserListener;
import com.example.emsapp.model.AppUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AuthManager {
    private static final String TAG = AuthManager.class.getSimpleName();
    private final DatabaseManager databaseManager;
    private final FirebaseAuth mAuth;
    private Activity activity;
    private AuthListener.LoginListener loginListener;
    private AuthListener.SignUpListener signUpListener;

    public AuthManager() {
        mAuth = FirebaseAuth.getInstance();

        databaseManager = new DatabaseManager.Builder().userListener(new UserListener() {
            @Override
            public void onGetUser(AppUser appUser) {
                if (loginListener != null) {
                    loginListener.onLoginSuccess(appUser);
                }
            }

            @Override
            public void onListUser(List<AppUser> appUsers) {

            }
        }).build();
    }

    public boolean isLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    public void doLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (loginListener != null) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();
                                databaseManager.getUser(uid);
                            }
                        } else {
                            Log.e(TAG, "onComplete: ", task.getException());
                            if (loginListener != null) {
                                loginListener.onLoginFailed();
                            }
                        }
                    }
                });
    }

    public void doLogout() {
        mAuth.signOut();
    }

    public void createUser(AppUser appUser) {
        mAuth.createUserWithEmailAndPassword(appUser.getEmail(), appUser.getPassword())
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        appUser.setUId(user.getUid());
                        databaseManager.createUser(appUser);
                        if (signUpListener != null) {
                            signUpListener.onSignUpSuccess();
                        }
                    } else {
                        if (signUpListener != null) {
                            signUpListener.onSignUpFailed(task.getException().getMessage());
                        }
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                });
    }

    public static class Builder {
        private final AuthManager authManager;

        public Builder() {
            authManager = new AuthManager();
        }

        public Builder activity(Activity activity) {
            authManager.activity = activity;
            return this;
        }

        public Builder loginListener(AuthListener.LoginListener loginListener) {
            authManager.loginListener = loginListener;
            return this;
        }

        public Builder signUpListener(AuthListener.SignUpListener signUpListener) {
            authManager.signUpListener = signUpListener;
            return this;
        }

        public AuthManager build() {
            return this.authManager;
        }
    }
}
