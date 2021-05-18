package com.example.emsapp.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MedicalRecords implements Serializable {
    private String documentName;
    private List<FileModel> fileModels;
}
