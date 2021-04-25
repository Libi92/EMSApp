package com.example.emsapp.ui.consultation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.base.BaseFragment;
import com.example.emsapp.constants.ScheduleStatus;
import com.example.emsapp.constants.UserType;
import com.example.emsapp.db.ConsultationDbManager;
import com.example.emsapp.db.ConsultationListener;
import com.example.emsapp.model.ConsultationRequest;
import com.example.emsapp.ui.adapters.ConsultationRecyclerAdapter;
import com.example.emsapp.ui.doctor.ScheduleAppointmentFragment;
import com.example.emsapp.util.Globals;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ConsultationFragment extends BaseFragment implements ConsultationListener, ConsultationRecyclerAdapter.ConsultationClickListener {

    private FloatingActionButton actionButton;
    private List<ConsultationRequest> requestList;
    private ConsultationRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_consultation, container, false);

        initLayout(root);
        initListeners();
        getConsultations();

        return root;
    }

    private void initLayout(View root) {
        RecyclerView recyclerViewConsultations = root.findViewById(R.id.recyclerViewConsultations);
        actionButton = root.findViewById(R.id.fabCunsultations);

        Context context = getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewConsultations.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        recyclerViewConsultations.addItemDecoration(dividerItemDecoration);

        requestList = new ArrayList<>();
        adapter = new ConsultationRecyclerAdapter(requestList);
        if (UserType.DOCTOR.getValue().equals(Globals.user.getUserType())) {
            adapter.setConsultationListener(this);
        }
        recyclerViewConsultations.setAdapter(adapter);
    }

    private void initListeners() {
        actionButton.setOnClickListener(v -> doNavigate(R.id.action_nav_consultation_to_nav_doctors_list, null));
    }

    private void getConsultations() {
        ConsultationDbManager dbManager = new ConsultationDbManager.Builder()
                .setConsultationListener(this)
                .build();

        dbManager.getConsultations(Globals.user);
    }

    @Override
    public void onConsultations(List<ConsultationRequest> consultationRequests) {
        requestList.clear();
        for (ConsultationRequest request : consultationRequests) {
            if (ScheduleStatus.PENDING.getValue().equals(request.getScheduleStatus())) {
                requestList.add(request);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnClick(ConsultationRequest consultationRequest) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ScheduleAppointmentFragment.ARG_CONSULT_REQUEST, consultationRequest);
        doNavigate(R.id.action_nav_consultation_to_nav_schedule_appointment, bundle);
    }
}