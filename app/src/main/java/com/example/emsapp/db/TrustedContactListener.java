package com.example.emsapp.db;

import com.example.emsapp.model.TrustedContact;

import java.util.List;

public interface TrustedContactListener {
    void onGetTrustedContacts(List<TrustedContact> trustedContacts);
}
