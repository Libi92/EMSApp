package com.example.emsapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.constants.UserType;
import com.example.emsapp.model.ConsultationRequest;
import com.example.emsapp.util.DateUtil;
import com.example.emsapp.util.Globals;

import java.util.List;

public class ConsultationRecyclerAdapter extends RecyclerView.Adapter<ConsultationRecyclerAdapter.ConsultationViewHolder> {

    private final List<ConsultationRequest> requestList;
    private ConsultationClickListener clickListener;

    public ConsultationRecyclerAdapter(List<ConsultationRequest> requestList) {
        this.requestList = requestList;
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
        holder.textViewRequestedOn.setText(DateUtil.getDate(request.getRequestedOn()));
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

        public ConsultationViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewRequestedOn = itemView.findViewById(R.id.textViewRequestedOn);
        }
    }
}
