package com.example.emsapp.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.base.BaseFragment;
import com.example.emsapp.constants.UserType;
import com.example.emsapp.db.MedicinesDbManager;
import com.example.emsapp.db.MedicinesListener;
import com.example.emsapp.db.TrustedContactDbManager;
import com.example.emsapp.db.TrustedContactListener;
import com.example.emsapp.model.Medicine;
import com.example.emsapp.model.TrustedContact;
import com.example.emsapp.ui.adapters.MedicinesRecyclerAdapter;
import com.example.emsapp.util.Globals;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MedicineFragment extends BaseFragment implements MedicinesListener, TrustedContactListener {

    private static final String TAG = MedicineFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 1008;
    private RecyclerView recyclerViewMedicines;
    private List<Medicine> medicines;
    private FloatingActionButton floatingActionButtonAlert;
    private FusedLocationProviderClient fusedLocationClient;
    private List<TrustedContact> trustedContacts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_medicine, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        initView(root);
        initListeners();
        getMedicines();

        return root;
    }

    private void initView(View root) {
        recyclerViewMedicines = root.findViewById(R.id.recyclerViewMedicines);
        medicines = new ArrayList<>();
        MedicinesRecyclerAdapter adapter = new MedicinesRecyclerAdapter(medicines);

        Context context = getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewMedicines.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        recyclerViewMedicines.addItemDecoration(dividerItemDecoration);

        recyclerViewMedicines.setAdapter(adapter);

        floatingActionButtonAlert = root.findViewById(R.id.floatingActionButtonAlert);
        if (Globals.user.getUserType().equals(UserType.DOCTOR.getValue())) {
            floatingActionButtonAlert.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        floatingActionButtonAlert.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Send Emergency notification")
                    .setMessage("Send emergency message to all trusted contacts?")
                    .setPositiveButton("yes", ((dialog, which) -> initContactDbManager()))
                    .setNegativeButton("no", ((dialog, which) -> dialog.dismiss()))
                    .show();
        });
    }

    private void initContactDbManager() {
        TrustedContactDbManager dbManager = new TrustedContactDbManager.Builder()
                .setUId(Globals.user.getUId())
                .setContactListener(this)
                .build();

        dbManager.getContacts();
    }

    private void getMedicines() {
        MedicinesDbManager medicinesDbManager = new MedicinesDbManager.Builder()
                .setUserId(Globals.user.getUId())
                .setMedicinesListener(this)
                .build();

        medicinesDbManager.getMedicines();
    }

    @Override
    public void onMedicines(List<Medicine> medicines) {
        this.medicines.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0
        );

        medicines.forEach(medicine -> {
            Calendar calendarStartDate = Calendar.getInstance();
            calendarStartDate.setTimeInMillis(medicine.getStartDate());

            Calendar calendarEndDate = Calendar.getInstance();
            calendarEndDate.setTimeInMillis(medicine.getEndDate());

            if (calendar.compareTo(calendarStartDate) < 0) {
                updateMedicineRemarks(calendar, medicine, calendarStartDate, "Starts in %d");
            } else if (calendar.compareTo(calendarEndDate) <= 0) {
                updateMedicineRemarks(calendar, medicine, calendarEndDate, "%d days remaining");
            }
        });

        this.recyclerViewMedicines.getAdapter().notifyDataSetChanged();
    }

    private void updateMedicineRemarks(Calendar calendar, Medicine medicine, Calendar calendarStartDate, String s) {
        long days = getDaysDiff(calendarStartDate, calendar);
        medicine.setRemark(String.format(Locale.getDefault(), s, days));
        medicines.add(medicine);
    }

    private long getDaysDiff(Calendar calendar1, Calendar calendar2) {
        return (calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / (1000 * 60 * 60 * 24);
    }

    @Override
    public void onGetTrustedContacts(List<TrustedContact> trustedContacts) {
        this.trustedContacts = trustedContacts;
        checkLocationPermission();
    }

    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            sendEmergencyNotification();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    public void sendEmergencyNotification() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        trustedContacts.forEach(contact -> {
                            String message = String.format(Locale.getDefault(),
                                    "Hi %s! I have an emergency at " +
                                            "https://www.google.com/maps/search/?api=1&query=%f,%f",
                                    contact.getName(), location.getLatitude(), location.getLongitude());
                            sendSMS(contact.getPhoneNo(), message);
                        });
                    }
                });
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendEmergencyNotification();
            } else {
                showSnackbar("Location permission is required to send emergency alert");
            }
        }
    }
}