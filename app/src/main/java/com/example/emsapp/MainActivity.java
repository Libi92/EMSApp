package com.example.emsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emsapp.auth.AuthListener;
import com.example.emsapp.auth.AuthManager;
import com.example.emsapp.constants.UserType;
import com.example.emsapp.model.AppUser;

public class MainActivity extends AppCompatActivity implements AuthListener.LoginListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authManager = new AuthManager.Builder()
                .activity(this)
                .loginListener(this)
                .build();

        initLayout();
        initListeners();
    }

    private void initLayout() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);
    }

    private void initListeners() {
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            authManager.doLogin(email, password);
        });

        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onLoginSuccess(AppUser appUser) {
        if (UserType.USER.getValue().equals(appUser.getUserType())) {
            Intent intent = new Intent(MainActivity.this, UserHomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoginFailed() {
        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
    }
}