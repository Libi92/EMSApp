package com.example.emsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emsapp.auth.AuthListener;
import com.example.emsapp.auth.AuthManager;
import com.example.emsapp.model.AppUser;

public class SignUpActivity extends AppCompatActivity implements AuthListener.SignUpListener {

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonSignUp;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        authManager = new AuthManager.Builder()
                .signUpListener(this)
                .activity(this).build();

        initLayout();
        initListeners();
    }

    private void initLayout() {
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
    }

    private void initListeners() {
        buttonSignUp.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String phone = editTextPhone.getText().toString();
            String address = editTextAddress.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            if (!password.equals(confirmPassword)) {
                editTextPassword.setError("Password doesn't match");
                return;
            }

            AppUser appUser = AppUser.builder()
                    .displayName(name)
                    .phone(phone)
                    .address(address)
                    .email(email)
                    .password(password)
                    .build();

            authManager.createUser(appUser);
        });
    }

    @Override
    public void onSignUpSuccess() {
        Toast.makeText(SignUpActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSignUpFailed(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}