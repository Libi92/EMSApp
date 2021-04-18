package com.example.emsapp.auth;


import com.example.emsapp.model.AppUser;

public interface AuthListener {
    interface LoginListener {
        void onLoginSuccess(AppUser appUser);

        void onLoginFailed();
    }

    interface LogoutListener {
        void onLogout();
    }

    interface SignUpListener {
        void onSignUpSuccess();

        void onSignUpFailed(String message);
    }
}
