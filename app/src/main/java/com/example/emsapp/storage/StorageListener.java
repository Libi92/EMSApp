package com.example.emsapp.storage;

import android.net.Uri;

import java.util.List;

public interface StorageListener {
    void onUploadStart();

    void onUploadComplete(List<Uri> uriList);

    void onUpdateProgress(int currentPosition, int limit);

    void onFailure(Exception exception);
}
