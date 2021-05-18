package com.example.emsapp.storage;

import android.net.Uri;
import android.text.TextUtils;

import com.example.emsapp.constants.FileType;
import com.example.emsapp.model.FileModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class StorageManager {
    private final StorageReference storageRef;
    private final StorageListener storageListener;
    private final List<FileModel> fileModels = new ArrayList<>();
    private boolean isFirstFile = true;
    private int currentFileIndex;
    private int totalFiles;

    public StorageManager(StorageListener storageListener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        this.storageListener = storageListener;
    }

    public void uploadFile(FileModel model) {
        FileType fileType = model.getFileType();
        String path = model.getFilePath();
        Uri file = Uri.fromFile(new File(path));
        String fileName = file.getLastPathSegment();
        String[] fileNameSplit = fileName.split("\\.");

        List<String> stringList = new ArrayList<>(Arrays.asList(fileNameSplit));
        stringList.add(stringList.size() - 1, String.valueOf(Calendar.getInstance().getTimeInMillis()));

        fileName = TextUtils.join("\\.", stringList);

        String dir = "general/";
        if (FileType.IMAGE.equals(fileType)) {
            dir = "images/";
        } else if (FileType.DOC.equals(fileType)) {
            dir = "docs/";
        }

        StorageReference storageRef = this.storageRef.child(dir + fileName);
        UploadTask uploadTask = storageRef.putFile(file);

        if (storageListener != null && isFirstFile) {
            storageListener.onUploadStart();
            isFirstFile = false;
        }

        uploadTask.addOnCompleteListener(task -> {
            Task<Uri> downloadUrl = storageRef.getDownloadUrl();
            downloadUrl.addOnCompleteListener(task1 -> {
                Uri uri = task1.getResult();
                Timber.i("initListeners: adding Uri - %s", uri);
                model.setFilePath(uri.toString());
                fileModels.add(model);

                currentFileIndex++;

                if (storageListener != null) {
                    if (currentFileIndex == totalFiles - 1) {
                        storageListener.onUploadComplete(fileModels);
                    } else {
                        storageListener.onUpdateProgress(currentFileIndex, totalFiles);
                    }
                }
            }).addOnFailureListener(exception -> {
                Timber.i("initListeners: image upload failed");

                if (storageListener != null) {
                    storageListener.onFailure(exception);
                }
            });
        });
    }

    public void uploadMultipleFileUpload(List<FileModel> fileModels) {
        totalFiles = fileModels.size();

        for (FileModel model : fileModels) {
            uploadFile(model);
        }
    }
}
