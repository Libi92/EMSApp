package com.example.emsapp.ui.doctor;

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
import com.example.emsapp.constants.UserType;
import com.example.emsapp.db.UserDbManager;
import com.example.emsapp.db.UserListener;
import com.example.emsapp.model.AppUser;
import com.example.emsapp.ui.adapters.DoctorsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DoctorsListFragment extends BaseFragment implements UserListener, DoctorsRecyclerAdapter.OnDoctorSelected {
    private final List<AppUser> doctorList = new ArrayList<>();
    private RecyclerView recyclerViewDoctors;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_doctors_list, container, false);

        initLayout(root);
        getDoctors();

        return root;
    }

    private void initLayout(View root) {
        recyclerViewDoctors = root.findViewById(R.id.recyclerViewDoctors);

        Context context = getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewDoctors.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        recyclerViewDoctors.addItemDecoration(dividerItemDecoration);

        DoctorsRecyclerAdapter adapter = new DoctorsRecyclerAdapter(doctorList, this);
        recyclerViewDoctors.setAdapter(adapter);
    }

    private void getDoctors() {
        UserDbManager userDbManager = new UserDbManager.Builder().userListener(this).build();
        userDbManager.getUsers(UserType.DOCTOR.getValue());
    }

    @Override
    public void onGetUser(AppUser appUser) {

    }

    @Override
    public void onListUser(List<AppUser> appUsers) {
        doctorList.clear();
        doctorList.addAll(appUsers);
        recyclerViewDoctors.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onSelected(AppUser doctor) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DoctorFragment.ARG_DOCTOR, doctor);
        doNavigate(R.id.action_nav_doctors_list_to_nav_doctor, bundle);
    }
}
