package com.example.emsapp.ui.schedule;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.constants.UserType;
import com.example.emsapp.db.PrescriptionDbManager;
import com.example.emsapp.db.PrescriptionListener;
import com.example.emsapp.model.ConsultationRequest;
import com.example.emsapp.model.Medicine;
import com.example.emsapp.ui.adapters.MedicinesRecyclerAdapter;
import com.example.emsapp.util.Globals;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AddPrescriptionFragment extends BottomSheetDialogFragment implements PrescriptionListener {

    public static final String ARG_CONSULTATION = "arg::Consultation";
    private final List<Medicine> medicines = new ArrayList<>();
    private ConsultationRequest consultationRequest;
    private EditText editTextMedicineName;
    private EditText editTextNoOfDays;
    private EditText editTextMorningDose;
    private EditText editTextAfternoonDose;
    private EditText editTextEveningDose;
    private Button buttonAddPrescription;
    private Button buttonSave;
    private RecyclerView recyclerViewPrescriptions;
    private PrescriptionDbManager dbManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_prescription, container, false);

        initLayout(view);
        initListeners();
        getPrescriptions();

        return view;
    }

    private void initLayout(View view) {
        if (UserType.USER.getValue().equals(Globals.user.getUserType())) {
            View inputView = view.findViewById(R.id.constraintLayoutInput);
            inputView.setVisibility(View.INVISIBLE);
        }

        editTextMedicineName = view.findViewById(R.id.editTextMedicineName);
        editTextNoOfDays = view.findViewById(R.id.editTextNoOfDays);
        editTextMorningDose = view.findViewById(R.id.editTextMorningDose);
        editTextAfternoonDose = view.findViewById(R.id.editTextAfternoonDose);
        editTextEveningDose = view.findViewById(R.id.editTextEveningDose);
        buttonAddPrescription = view.findViewById(R.id.buttonAddPrescription);
        buttonSave = view.findViewById(R.id.buttonSave);

        recyclerViewPrescriptions = view.findViewById(R.id.recyclerViewPrescriptions);

        Context context = getContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerViewPrescriptions.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                linearLayoutManager.getOrientation());
        recyclerViewPrescriptions.addItemDecoration(dividerItemDecoration);

        MedicinesRecyclerAdapter adapter = new MedicinesRecyclerAdapter(medicines);
        recyclerViewPrescriptions.setAdapter(adapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            consultationRequest = (ConsultationRequest) bundle.getSerializable(ARG_CONSULTATION);
            dbManager = new PrescriptionDbManager.Builder()
                    .setUId(consultationRequest.getUId())
                    .setPrescriptionListener(this)
                    .build();
        }
    }

    private void initListeners() {
        buttonAddPrescription.setOnClickListener(v -> {
            String medicineName = editTextMedicineName.getText().toString();
            if (medicineName.isEmpty()) {
                editTextMedicineName.setError("Cannot be empty");
                return;
            }
            String daysText = editTextNoOfDays.getText().toString();
            if (daysText.isEmpty()) {
                editTextNoOfDays.setError("Cannot be empty");
                return;
            }
            Integer noOfDays = Integer.valueOf(daysText);
            String morningDoseText = editTextMorningDose.getText().toString();
            Float morningDose = Float.valueOf(morningDoseText.isEmpty() ? "0" : morningDoseText);
            String afternoonDoseText = editTextAfternoonDose.getText().toString();
            Float afterNoonDose = Float.valueOf(afternoonDoseText.isEmpty() ? "0" : afternoonDoseText);
            String eveningDoseText = editTextEveningDose.getText().toString();
            Float eveningDose = Float.valueOf(eveningDoseText.isEmpty() ? "0" : eveningDoseText);

            Medicine medicine = Medicine.builder()
                    .medicineName(medicineName)
                    .daysRemaining(noOfDays)
                    .prescribedBy(consultationRequest.getToDoctor().getDisplayName())
                    .morningDoses(morningDose)
                    .afterNoonDoses(afterNoonDose)
                    .nightDoses(eveningDose)
                    .build();

            medicines.add(medicine);
            recyclerViewPrescriptions.getAdapter().notifyDataSetChanged();
            editTextMedicineName.setText("");
            editTextNoOfDays.setText("");
            editTextMorningDose.setText("");
            editTextAfternoonDose.setText("");
            editTextEveningDose.setText("");
        });

        buttonSave.setOnClickListener(v -> {
            if (dbManager != null) {
                dbManager.addPrescription(medicines);
            }
            Snackbar.make(getView(), "Prescriptions saved", Snackbar.LENGTH_SHORT)
                    .setAction("Ok", v1 -> dismiss())
                    .show();
        });
    }

    private void getPrescriptions() {
        if (dbManager != null) {
            dbManager.getPrescriptions();
        }
    }

    @Override
    public void onPrescriptions(List<Medicine> medicines) {
        this.medicines.clear();
        this.medicines.addAll(medicines);
        this.recyclerViewPrescriptions.getAdapter().notifyDataSetChanged();
    }
}
