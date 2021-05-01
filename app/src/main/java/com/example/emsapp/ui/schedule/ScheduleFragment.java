package com.example.emsapp.ui.schedule;

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
import com.example.emsapp.constants.AdapterType;
import com.example.emsapp.constants.ScheduleStatus;
import com.example.emsapp.db.ConsultationDbManager;
import com.example.emsapp.db.ConsultationListener;
import com.example.emsapp.model.ConsultationRequest;
import com.example.emsapp.ui.adapters.ConsultationRecyclerAdapter;
import com.example.emsapp.util.Globals;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends BaseFragment implements ConsultationListener, ConsultationRecyclerAdapter.ConsultationClickListener {

    private List<ConsultationRequest> requestList;
    private ConsultationRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_schedule, container, false);

        initLayout(root);
        getConsultations();

        return root;
    }

    private void initLayout(View root) {
        RecyclerView recyclerViewConsultations = root.findViewById(R.id.recyclerViewSchedule);

        Context context = getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewConsultations.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        recyclerViewConsultations.addItemDecoration(dividerItemDecoration);

        requestList = new ArrayList<>();
        adapter = new ConsultationRecyclerAdapter(requestList, AdapterType.SCHEDULE);
        adapter.setConsultationListener(this);
        recyclerViewConsultations.setAdapter(adapter);
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
            if (ScheduleStatus.SCHEDULED.getValue().equals(request.getScheduleStatus()) ||
                    ScheduleStatus.COMPLETE.getValue().equals(request.getScheduleStatus())) {
                requestList.add(request);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnClick(ConsultationRequest consultationRequest) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ScheduleDetailsFragment.ARG_SCHEDULE, consultationRequest);
        doNavigate(R.id.action_nav_schedule_to_nav_schedule_details, bundle);
    }
}