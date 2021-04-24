package com.example.emsapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.model.AppUser;

import java.util.List;

public class DoctorsRecyclerAdapter extends RecyclerView.Adapter<DoctorsRecyclerAdapter.DoctorsRecyclerViewHolder> {

    private final List<AppUser> doctorList;
    private final OnDoctorSelected onDoctorSelected;

    public DoctorsRecyclerAdapter(List<AppUser> doctorList, OnDoctorSelected onDoctorSelected) {
        this.doctorList = doctorList;
        this.onDoctorSelected = onDoctorSelected;
    }

    @NonNull
    @Override
    public DoctorsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_doctors, parent, false);
        return new DoctorsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorsRecyclerViewHolder holder, int position) {
        AppUser doctor = doctorList.get(position);
        holder.textViewName.setText(doctor.getDisplayName());
        holder.textViewDesignation.setText(doctor.getDesignation());

        holder.view.setOnClickListener(v -> {
            if (onDoctorSelected != null) {
                onDoctorSelected.onSelected(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public interface OnDoctorSelected {
        void onSelected(AppUser doctor);
    }

    public static class DoctorsRecyclerViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final TextView textViewName;
        private final TextView textViewDesignation;

        public DoctorsRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDesignation = itemView.findViewById(R.id.textViewDesignation);
        }
    }
}
