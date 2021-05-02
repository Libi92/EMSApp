package com.example.emsapp.storage;

import android.net.Uri;

public interface StorageListener {
    void onUploadStart();

    void onUploadComplete(Uri uri);

    void onFailure(Exception exception);
}
