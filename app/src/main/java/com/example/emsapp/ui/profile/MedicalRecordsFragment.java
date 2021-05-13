package com.example.emsapp.ui.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.constants.FileType;
import com.example.emsapp.model.FileModel;
import com.example.emsapp.storage.StorageListener;
import com.example.emsapp.storage.StorageManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MedicalRecordsFragment extends BottomSheetDialogFragment implements StorageListener {

    private static final String TAG = MedicalRecordsFragment.class.getSimpleName();
    private RecyclerView recyclerViewMedicalRecords;
    private Button buttonAddRecords;
    private List<FileModel> filesToUpload = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_records, container, false);

        initLayout(view);
        initListeners();

        return view;
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading image, please wait.");
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void initLayout(View view) {
        recyclerViewMedicalRecords = view.findViewById(R.id.recyclerViewMedicalRecords);
        buttonAddRecords = view.findViewById(R.id.buttonAddRecords);
    }

    private void initListeners() {
        buttonAddRecords.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NormalFilePickActivity.class);
            intent.putExtra(Constant.MAX_NUMBER, 9);
            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == Constant.REQUEST_CODE_PICK_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<NormalFile> filesList = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                if (filesList != null && !filesList.isEmpty()) {
                    for (NormalFile file : filesList) {
                        FileModel model = FileModel.builder()
                                .fileType(FileType.DOC)
                                .filePath(file.getPath())
                                .build();
                        filesToUpload.add(model);
                        Log.d(TAG, "onActivityResult: " + file.getMimeType());
                    }

                    StorageManager storageManager = new StorageManager(this);
                    storageManager.uploadMultipleFileUpload(filesToUpload);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onUploadStart() {
        initProgressDialog();
    }

    @Override
    public void onUploadComplete(List<Uri> uriList) {
        dismissProgressDialog();
        filesToUpload.clear();
    }

    @Override
    public void onUpdateProgress(int currentPosition, int limit) {
        progressDialog.setTitle(String.format(Locale.getDefault(), "%d of %d files uploaded", currentPosition, limit));
    }

    @Override
    public void onFailure(Exception exception) {
        dismissProgressDialog();
        Toast.makeText(getContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        filesToUpload.clear();
    }
}
