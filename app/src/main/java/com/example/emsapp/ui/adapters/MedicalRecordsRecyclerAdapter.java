package com.example.emsapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.model.MedicalRecords;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MedicalRecordsRecyclerAdapter extends RecyclerView.Adapter<MedicalRecordsRecyclerAdapter.MedicalRecordsRecyclerViewHolder> {

    private final List<MedicalRecords> medicalRecords;
    private final MedicalRecordsClickListener clickListener;
    private final boolean showDeleteIcon;

    public MedicalRecordsRecyclerAdapter(List<MedicalRecords> medicalRecords,
                                         MedicalRecordsClickListener clickListener, boolean showDeleteIcon) {
        this.medicalRecords = medicalRecords;
        this.clickListener = clickListener;
        this.showDeleteIcon = showDeleteIcon;
    }

    @NonNull
    @NotNull
    @Override
    public MedicalRecordsRecyclerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_medical_record, parent, false);
        return new MedicalRecordsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MedicalRecordsRecyclerAdapter.MedicalRecordsRecyclerViewHolder holder, int position) {
        MedicalRecords record = medicalRecords.get(position);
        holder.textViewDocumentName.setText(record.getDocumentName());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(record);
            }
        });

        holder.imageViewDelete.setOnClickListener(v -> {
            clickListener.onDeleteClick(record);
        });

        if (!showDeleteIcon) {
            holder.imageViewDelete.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return medicalRecords.size();
    }

    public interface MedicalRecordsClickListener {
        void onItemClick(MedicalRecords record);

        void onDeleteClick(MedicalRecords record);
    }

    public static class MedicalRecordsRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDocumentName;
        private final ImageView imageViewDelete;
        private final View itemView;

        public MedicalRecordsRecyclerViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewDocumentName = itemView.findViewById(R.id.textViewDocumentName);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
        }
    }
}
