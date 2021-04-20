package com.example.emsapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.model.Medicine;
import com.example.emsapp.ui.adapters.MedicinesRecyclerAdapter;

import java.util.Collections;

public class MedicineFragment extends Fragment {

    private RecyclerView recyclerViewMedicines;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_medicine, container, false);

        initView(root);

        return root;
    }

    private void initView(View root) {
        recyclerViewMedicines = root.findViewById(R.id.recyclerViewMedicines);
        Medicine medicine = Medicine.builder()
                .medicineName("Paracitamol")
                .prescribedBy("Dr. Binu")
                .daysRemaining(3)
                .morningDoses(1.5F)
                .afterNoonDoses(0F)
                .nightDoses(1F)
                .build();
        MedicinesRecyclerAdapter adapter = new MedicinesRecyclerAdapter(Collections.singletonList(medicine));
        recyclerViewMedicines.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMedicines.setAdapter(adapter);
    }
}