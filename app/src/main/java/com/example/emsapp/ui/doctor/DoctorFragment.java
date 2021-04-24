package com.example.emsapp.ui.doctor;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.emsapp.R;
import com.example.emsapp.constants.ScheduleStatus;
import com.example.emsapp.db.ConsultationDbManager;
import com.example.emsapp.model.AppUser;
import com.example.emsapp.model.ConsultationRequest;
import com.example.emsapp.util.Globals;
import com.google.android.material.snackbar.Snackbar;

public class DoctorFragment extends Fragment {

    public static final String ARG_DOCTOR = "arg::Doctor";

    private Button buttonConsult;
    private AppUser doctor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);

        initLayout(view);
        initListeners();
        return view;
    }

    private void initLayout(View view) {
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewDesignation = view.findViewById(R.id.textViewDesignation);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);
        TextView textViewEmail = view.findViewById(R.id.textViewEmail);
        buttonConsult = view.findViewById(R.id.buttonConsult);

        Bundle bundle = getArguments();
        if (bundle != null) {
            doctor = (AppUser) bundle.getSerializable(ARG_DOCTOR);
            textViewName.setText(doctor.getDisplayName());
            textViewDesignation.setText(doctor.getDesignation());
            textViewPhone.setText(doctor.getPhone());
            textViewEmail.setText(doctor.getEmail());
        }
    }

    private void initListeners() {
        buttonConsult.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Confirm Request")
                .setMessage(String.format("Please confirm consultation request to %s", doctor.getDisplayName()))
                .setPositiveButton("Confirm", (dialog, which) -> addConsultationRequest())
                .setCancelable(true)
                .show());
    }

    private void addConsultationRequest() {
        ConsultationRequest consultationRequest = ConsultationRequest.builder()
                .fromUser(Globals.user)
                .toDoctor(doctor)
                .scheduleStatus(ScheduleStatus.PENDING.getValue())
                .build();
        ConsultationDbManager dbManager = new ConsultationDbManager.Builder().build();
        dbManager.addConsultation(consultationRequest);
        Snackbar.make(getView(), "Request Confirmed", Snackbar.LENGTH_SHORT).show();
    }
}
