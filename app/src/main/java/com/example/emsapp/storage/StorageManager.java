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
    private String uploadType = "SINGLE";
    private boolean isLastFile = true;
    private boolean isFirstFile = true;
    private int currentFileIndex;
    private int totalFiles;
    private List<Uri> uriList = new ArrayList<>();

    public StorageManager(StorageListener storageListener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        this.storageListener = storageListener;
    }

    public void uploadFile(FileType fileType, String path) {
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
                uriList.add(uri);

                if (storageListener != null) {
                    if (isLastFile) {
                        storageListener.onUploadComplete(uriList);
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
        uploadType = "MULTIPLE";
        isLastFile = false;
        totalFiles = fileModels.size();


        for (currentFileIndex = 0; currentFileIndex < totalFiles; currentFileIndex++) {
            FileModel model = fileModels.get(currentFileIndex);
            if (currentFileIndex == totalFiles - 1) {
                isLastFile = true;
            }
            uploadFile(model.getFileType(), model.getFilePath());
        }
    }
}
