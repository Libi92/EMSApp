package com.example.emsapp.ui.doctor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.emsapp.R;
import com.example.emsapp.base.BaseFragment;
import com.example.emsapp.constants.ScheduleStatus;
import com.example.emsapp.db.ConsultationDbManager;
import com.example.emsapp.model.AppUser;
import com.example.emsapp.model.ConsultationRequest;
import com.example.emsapp.util.Globals;

import org.jetbrains.annotations.NotNull;

public class DoctorFragment extends BaseFragment {

    public static final String ARG_DOCTOR = "arg::Doctor";
    private static final int REQUEST_CODE = 1992;

    private Button buttonConsult;
    private CardView cardViewPhone;
    private CardView cardViewEmail;
    private String phone;
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
        buttonConsult = view.findViewById(R.id.buttonSchedule);
        cardViewPhone = view.findViewById(R.id.cardViewPhone);
        cardViewEmail = view.findViewById(R.id.cardViewEmail);

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

        cardViewPhone.setOnClickListener(v -> checkCallPermission(doctor.getPhone()));
        cardViewEmail.setOnClickListener(v -> {
            String[] addresses = {doctor.getEmail()};
            String subject = "Query on appointment with " + doctor.getDisplayName();
            composeEmail(addresses, subject);
        });
    }

    private void addConsultationRequest() {
        ConsultationRequest consultationRequest = ConsultationRequest.builder()
                .fromUser(Globals.user)
                .toDoctor(doctor)
                .scheduleStatus(ScheduleStatus.PENDING.getValue())
                .build();
        ConsultationDbManager dbManager = new ConsultationDbManager.Builder().build();
        dbManager.addConsultation(consultationRequest);
        showSnackbar("Request Confirmed");
    }

    private void checkCallPermission(String phone) {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {

            makeCall(phone);
        } else {
            this.phone = phone;
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE);
        }

    }

    private void makeCall(String phone) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Call this Doctor")
                .setMessage("Carrier charges will be applicable")
                .setPositiveButton("Ok", (dialog, which) -> {
                    String uri = "tel:" + phone;
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall(this.phone);
            } else {
                showSnackbar("Permission is required to make call");
            }
        }
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
