package com.example.emsapp.storage;

import com.example.emsapp.model.FileModel;

import java.util.List;

public interface StorageListener {
    void onUploadStart();

    void onUploadComplete(List<FileModel> fileModels);

    void onUpdateProgress(int currentPosition, int limit);

    void onFailure(Exception exception);
}
