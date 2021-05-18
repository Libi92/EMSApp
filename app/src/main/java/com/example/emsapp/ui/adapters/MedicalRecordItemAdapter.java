package com.example.emsapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.model.FileModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MedicalRecordItemAdapter extends RecyclerView.Adapter<MedicalRecordItemAdapter.MedicalRecordItemViewHolder> {

    private final List<FileModel> fileModels;
    private final OnFileItemClickListener fileItemClickListener;

    public MedicalRecordItemAdapter(List<FileModel> fileModels, OnFileItemClickListener fileItemClickListener) {
        this.fileModels = fileModels;
        this.fileItemClickListener = fileItemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public MedicalRecordItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_file, parent, false);
        return new MedicalRecordItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MedicalRecordItemAdapter.MedicalRecordItemViewHolder holder, int position) {
        FileModel model = fileModels.get(position);
        holder.textViewFileName.setText(model.getFileName());

        holder.itemView.setOnClickListener(v -> {
            if (fileItemClickListener != null) {
                fileItemClickListener.onItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileModels.size();
    }

    public interface OnFileItemClickListener {
        void onItemClick(FileModel fileModel);
    }

    public static class MedicalRecordItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewFileName;
        private final View itemView;

        public MedicalRecordItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
        }
    }
}
