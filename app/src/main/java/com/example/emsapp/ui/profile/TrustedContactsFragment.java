package com.example.emsapp.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.db.TrustedContactDbManager;
import com.example.emsapp.db.TrustedContactListener;
import com.example.emsapp.model.TrustedContact;
import com.example.emsapp.ui.adapters.TrustedContactsRecyclerAdapter;
import com.example.emsapp.util.Globals;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrustedContactsFragment extends BottomSheetDialogFragment implements TrustedContactListener, TrustedContactsRecyclerAdapter.ContactsDeleteListener {

    private final List<TrustedContact> trustedContactList = new ArrayList<>();
    private EditText editTextName;
    private EditText editTextPhone;
    private Button buttonAdd;
    private TrustedContactDbManager dbManager;
    private RecyclerView recyclerViewContacts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trusted_contacts, container, false);

        initContactDbManager();
        initLayout(view);
        initListeners();

        return view;
    }

    private void initContactDbManager() {
        dbManager = new TrustedContactDbManager.Builder()
                .setUId(Globals.user.getUId())
                .setContactListener(this)
                .build();

        dbManager.getContacts();
    }

    private void initLayout(View view) {
        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        recyclerViewContacts = view.findViewById(R.id.recyclerViewTrustedContact);
        Context context = getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewContacts.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        recyclerViewContacts.addItemDecoration(dividerItemDecoration);

        TrustedContactsRecyclerAdapter adapter = new TrustedContactsRecyclerAdapter(trustedContactList, this);
        recyclerViewContacts.setAdapter(adapter);
    }

    private void initListeners() {
        buttonAdd.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String phone = editTextPhone.getText().toString();

            TrustedContact contact = TrustedContact.builder()
                    .name(name)
                    .phoneNo(phone)
                    .build();

            trustedContactList.add(contact);
            dbManager.saveContact(trustedContactList);
        });
    }

    @Override
    public void onGetTrustedContacts(List<TrustedContact> trustedContacts) {
        trustedContactList.clear();
        trustedContactList.addAll(trustedContacts);

        Objects.requireNonNull(recyclerViewContacts.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onDelete(TrustedContact contact) {
        trustedContactList.remove(contact);
        dbManager.saveContact(trustedContactList);
    }
}
