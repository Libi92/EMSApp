package com.example.emsapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.db.MedicinesDbManager;
import com.example.emsapp.db.MedicinesListener;
import com.example.emsapp.model.Medicine;
import com.example.emsapp.ui.adapters.MedicinesRecyclerAdapter;
import com.example.emsapp.util.Globals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MedicineFragment extends Fragment implements MedicinesListener {

    private static final String TAG = MedicineFragment.class.getSimpleName();
    private RecyclerView recyclerViewMedicines;
    private List<Medicine> medicines;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_medicine, container, false);

        initView(root);
        getMedicines();

        return root;
    }

    private void initView(View root) {
        recyclerViewMedicines = root.findViewById(R.id.recyclerViewMedicines);
        medicines = new ArrayList<>();
        MedicinesRecyclerAdapter adapter = new MedicinesRecyclerAdapter(medicines);

        Context context = getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewMedicines.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        recyclerViewMedicines.addItemDecoration(dividerItemDecoration);

        recyclerViewMedicines.setAdapter(adapter);
    }

    private void getMedicines() {
        MedicinesDbManager medicinesDbManager = new MedicinesDbManager.Builder()
                .setUserId(Globals.user.getUId())
                .setMedicinesListener(this)
                .build();

        medicinesDbManager.getMedicines();
    }

    @Override
    public void onMedicines(List<Medicine> medicines) {
        this.medicines.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0
        );

        medicines.forEach(medicine -> {
            Calendar calendarStartDate = Calendar.getInstance();
            calendarStartDate.setTimeInMillis(medicine.getStartDate());

            Calendar calendarEndDate = Calendar.getInstance();
            calendarEndDate.setTimeInMillis(medicine.getEndDate());

            if (calendar.compareTo(calendarStartDate) < 0) {
                updateMedicineRemarks(calendar, medicine, calendarStartDate, "Starts in %d");
            } else if (calendar.compareTo(calendarEndDate) <= 0) {
                updateMedicineRemarks(calendar, medicine, calendarEndDate, "%d days remaining");
            }
        });

        this.recyclerViewMedicines.getAdapter().notifyDataSetChanged();
    }

    private void updateMedicineRemarks(Calendar calendar, Medicine medicine, Calendar calendarStartDate, String s) {
        long days = getDaysDiff(calendarStartDate, calendar);
        medicine.setRemark(String.format(Locale.getDefault(), s, days));
        medicines.add(medicine);
    }

    private long getDaysDiff(Calendar calendar1, Calendar calendar2) {
        return (calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / (1000 * 60 * 60 * 24);
    }
}