package com.example.emsapp.db;

import com.example.emsapp.model.Medicine;

import java.util.List;

public interface MedicinesListener {
    void onMedicines(List<Medicine> medicines);
}
