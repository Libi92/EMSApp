package com.example.emsapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.model.Medicine;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class MedicinesRecyclerAdapter extends RecyclerView.Adapter<MedicinesRecyclerAdapter.MedicinesViewHolder> {

    private final List<Medicine> medicineList;

    public MedicinesRecyclerAdapter(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_medicine, parent, false);
        return new MedicinesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicinesViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.textViewMedicineName.setText(medicine.getMedicineName());
        holder.textViewPrescribedBy.setText(String.format(Locale.getDefault(), "Prescribed by %s", medicine.getPrescribedBy()));
        holder.textViewRemainingDays.setText(medicine.getRemark());
        holder.textViewMorningDoses.setText(getDosesFormat(medicine.getMorningDoses()));
        holder.textViewAfternoonDoses.setText(getDosesFormat(medicine.getAfterNoonDoses()));
        holder.textViewNightDoses.setText(getDosesFormat(medicine.getNightDoses()));
    }

    @NotNull
    private String getDosesFormat(Float doses) {
        String format = "%.1f";
        Object value = doses;
        if (doses == doses.intValue()) {
            format = "%d";
            value = doses.intValue();
        }
        return String.format(Locale.getDefault(), format, value);
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class MedicinesViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewMedicineName;
        private final TextView textViewPrescribedBy;
        private final TextView textViewRemainingDays;
        private final TextView textViewMorningDoses;
        private final TextView textViewAfternoonDoses;
        private final TextView textViewNightDoses;

        public MedicinesViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewMedicineName = itemView.findViewById(R.id.textViewMedicineName);
            textViewPrescribedBy = itemView.findViewById(R.id.textViewPrescribedBy);
            textViewRemainingDays = itemView.findViewById(R.id.textViewRemainingDays);
            textViewMorningDoses = itemView.findViewById(R.id.textViewMorningDose);
            textViewAfternoonDoses = itemView.findViewById(R.id.textViewAfternoonDose);
            textViewNightDoses = itemView.findViewById(R.id.textViewNightDose);
        }
    }
}
