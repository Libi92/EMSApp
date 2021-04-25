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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Locale;

public class ScheduleAppointmentFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String ARG_CONSULT_REQUEST = "arg::ConsultRequest";
    private static final int REQUEST_CODE = 1992;

    private Button buttonSchedule;
    private CardView cardViewPhone;
    private CardView cardViewEmail;
    private TextView textViewDate;
    private TextView textViewTime;

    private String phone;
    private ConsultationRequest consultationRequest;
    private AppUser user;
    private Calendar calendarSchedule;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_appointment, container, false);

        calendarSchedule = Calendar.getInstance();

        initLayout(view);
        initListeners();

        return view;
    }

    private void initLayout(View view) {
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewDesignation = view.findViewById(R.id.textViewDesignation);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);
        TextView textViewEmail = view.findViewById(R.id.textViewEmail);
        buttonSchedule = view.findViewById(R.id.buttonSchedule);
        cardViewPhone = view.findViewById(R.id.cardViewPhone);
        cardViewEmail = view.findViewById(R.id.cardViewEmail);
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewTime = view.findViewById(R.id.textViewTime);

        Bundle bundle = getArguments();
        if (bundle != null) {
            consultationRequest = (ConsultationRequest) bundle.getSerializable(ARG_CONSULT_REQUEST);
            user = consultationRequest.getFromUser();
            textViewName.setText(user.getDisplayName());
            textViewDesignation.setText(user.getDesignation());
            textViewPhone.setText(user.getPhone());
            textViewEmail.setText(user.getEmail());
        }
    }

    private void initListeners() {
        buttonSchedule.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Confirm Schedule")
                .setMessage(String.format("Please confirm schedule of %s", user.getDisplayName()))
                .setPositiveButton("Confirm", (dialog, which) -> {
                    updateSchedule();
                })
                .setCancelable(true)
                .show());

        cardViewPhone.setOnClickListener(v -> checkCallPermission(user.getPhone()));
        cardViewEmail.setOnClickListener(v -> {
            String[] addresses = {user.getEmail()};
            String subject = "Query on appointment with " + consultationRequest.getToDoctor().getDisplayName();
            composeEmail(addresses, subject);
        });

        textViewDate.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    ScheduleAppointmentFragment.this,
                    now.get(Calendar.YEAR), // Initial year selection
                    now.get(Calendar.MONTH), // Initial month selection
                    now.get(Calendar.DAY_OF_MONTH) // Initial day selection
            );

            // If you're calling this from a support Fragment
            dpd.show(getParentFragmentManager(), "DatePickerDialog");
        });

        textViewTime.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                    ScheduleAppointmentFragment.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false);

            timePickerDialog.show(getParentFragmentManager(), "TimePickerDialog");
        });
    }

    private void updateSchedule() {
        consultationRequest.setSchedulesDateTime(calendarSchedule.getTime().getTime());
        consultationRequest.setScheduleStatus(ScheduleStatus.SCHEDULED.getValue());

        ConsultationDbManager dbManager = new ConsultationDbManager.Builder().build();
        dbManager.addConsultation(consultationRequest);
        showSnackbar("Schedule Confirmed");
        getChildFragmentManager().popBackStack();
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
                .setTitle("Call this User")
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendarSchedule.set(year, monthOfYear, dayOfMonth);
        textViewDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear, year));
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        int year = calendarSchedule.get(Calendar.YEAR);
        int month = calendarSchedule.get(Calendar.MONTH);
        int day = calendarSchedule.get(Calendar.DAY_OF_MONTH);
        calendarSchedule.set(year, month, day, hourOfDay, minute);

        textViewTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
    }
}
