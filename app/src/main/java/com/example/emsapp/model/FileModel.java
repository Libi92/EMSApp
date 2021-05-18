package com.example.emsapp.model;

import com.example.emsapp.constants.FileType;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileModel implements Serializable {
    private FileType fileType;
    private String fileName;
    private String mimeType;
    private String filePath;
}
