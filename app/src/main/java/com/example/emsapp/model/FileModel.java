package com.example.emsapp.model;

import com.example.emsapp.constants.FileType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileModel {
    FileType fileType;
    String filePath;
}
