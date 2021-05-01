package com.example.emsapp.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.constants.AdapterType;
import com.example.emsapp.constants.ScheduleStatus;
import com.example.emsapp.constants.UserType;
import com.example.emsapp.model.ConsultationRequest;
import com.example.emsapp.util.DateUtil;
import com.example.emsapp.util.Globals;

import java.util.List;

public class ConsultationRecyclerAdapter extends RecyclerView.Adapter<ConsultationRecyclerAdapter.ConsultationViewHolder> {

    private final List<ConsultationRequest> requestList;
    private final AdapterType adapterType;
    private ConsultationClickListener clickListener;

    public ConsultationRecyclerAdapter(List<ConsultationRequest> requestList, AdapterType adapterType) {
        this.requestList = requestList;
        this.adapterType = adapterType;
    }

    public void setConsultationListener(ConsultationClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ConsultationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_consultation, parent, false);
        return new ConsultationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultationViewHolder holder, int position) {
        ConsultationRequest request = requestList.get(position);
        String displayName = UserType.USER.getValue().equals(Globals.user.getUserType()) ?
                request.getToDoctor().getDisplayName() : request.getFromUser().getDisplayName();
        holder.textViewName.setText(displayName);

        if (AdapterType.CONSULTATION.equals(adapterType)) {
            holder.textViewDateTitle.setText("Requested On: ");
            holder.textViewRequestedOn.setText(DateUtil.getDate(request.getRequestedOn()));
        } else {
            holder.textViewDateTitle.setText("Scheduled On: ");
            holder.textViewRequestedOn.setText(DateUtil.getDate(request.getSchedulesDateTime()));
        }

        if (ScheduleStatus.PENDING.getValue().equals(request.getScheduleStatus())) {
            holder.textViewStatus.setBackgroundColor(Color.CYAN);
        } else if (ScheduleStatus.SCHEDULED.getValue().equals(request.getScheduleStatus())) {
            holder.textViewStatus.setBackgroundColor(Color.MAGENTA);
        } else if (ScheduleStatus.COMPLETE.getValue().equals(request.getScheduleStatus())) {
            holder.textViewStatus.setBackgroundColor(Color.GREEN);
        }
        holder.textViewStatus.setText(request.getScheduleStatus());
        holder.textViewStatus.setVisibility(View.VISIBLE);

        holder.view.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.OnClick(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public interface ConsultationClickListener {
        void OnClick(ConsultationRequest consultationRequest);
    }

    public static class ConsultationViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final TextView textViewName;
        private final TextView textViewRequestedOn;
        private final TextView textViewDateTitle;
        private final TextView textViewStatus;

        public ConsultationViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewRequestedOn = itemView.findViewById(R.id.textViewRequestedOn);
            textViewDateTitle = itemView.findViewById(R.id.textViewDateTitle);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
