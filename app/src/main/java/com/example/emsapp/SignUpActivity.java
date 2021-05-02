package com.example.emsapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emsapp.auth.AuthListener;
import com.example.emsapp.auth.AuthManager;
import com.example.emsapp.model.AppUser;

import java.util.Locale;

public class SignUpActivity extends AppCompatActivity implements AuthListener.SignUpListener {

    private EditText editTextName;
    private EditText editTextDoB;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Spinner spinnerGender;
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
        editTextDoB = findViewById(R.id.editTextDoB);
        spinnerGender = findViewById(R.id.spinnerGender);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonUpdate);
    }

    private void initListeners() {
        buttonSignUp.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String dob = editTextDoB.getText().toString();
            String gender = spinnerGender.getSelectedItem().toString();
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
                    .dob(dob)
                    .gender(gender)
                    .phone(phone)
                    .address(address)
                    .email(email)
                    .password(password)
                    .build();

            authManager.createUser(appUser);
        });

        editTextDoB.setOnTouchListener((v, event) -> {

            if (event.equals(MotionEvent.ACTION_UP)) {
                showDoBPicker();
            }
            return false;
        });

        editTextDoB.setOnClickListener(v -> {
            showDoBPicker();
        });
    }

    private void showDoBPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this);
        datePickerDialog.setOnDateSetListener(
                (view, year, month, dayOfMonth) ->
                        editTextDoB.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month, year)));
        datePickerDialog.show();
    }

    @Override
    public void onSignUpSuccess() {
        finish();
    }

    @Override
    public void onSignUpFailed(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}