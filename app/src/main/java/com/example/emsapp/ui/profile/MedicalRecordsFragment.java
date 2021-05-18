package com.example.emsapp.ui.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.FileGridActivity;
import com.example.emsapp.R;
import com.example.emsapp.constants.FileType;
import com.example.emsapp.constants.UserType;
import com.example.emsapp.db.MedicalRecordsDbManager;
import com.example.emsapp.db.MedicalRecordsListener;
import com.example.emsapp.model.FileModel;
import com.example.emsapp.model.MedicalRecords;
import com.example.emsapp.storage.StorageListener;
import com.example.emsapp.storage.StorageManager;
import com.example.emsapp.ui.adapters.MedicalRecordsRecyclerAdapter;
import com.example.emsapp.util.Globals;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class MedicalRecordsFragment extends BottomSheetDialogFragment implements StorageListener,
        MedicalRecordsListener, MedicalRecordsRecyclerAdapter.MedicalRecordsClickListener {

    public static final String ARG_USER_ID = "arg::userID";
    private final List<FileModel> filesToUpload = new ArrayList<>();
    private final List<MedicalRecords> medicalRecords = new ArrayList<>();
    private MedicalRecordsDbManager dbManager;
    private RecyclerView recyclerViewMedicalRecords;
    private Button buttonSelectFiles;
    private Button buttonAddRecords;
    private TextView textViewUploadFiles;
    private ProgressDialog progressDialog;
    private EditText editTextDocumentName;
    private String userId;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_records, container, false);

        Bundle bundle = getArguments();
        userId = Globals.user.getUId();
        if (bundle != null) {
            userId = bundle.getString(ARG_USER_ID);
        }
        dbManager = new MedicalRecordsDbManager.Builder()
                .setUId(userId)
                .setRecordsListener(this)
                .build();

        initLayout(view);
        initListeners();

        dbManager.getMedicalRecords();

        return view;
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading image, please wait.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
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
        buttonSelectFiles = view.findViewById(R.id.buttonSelectFiles);
        textViewUploadFiles = view.findViewById(R.id.textViewUploadFiles);
        editTextDocumentName = view.findViewById(R.id.editTextDocumentName);

        Context context = getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewMedicalRecords.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        recyclerViewMedicalRecords.addItemDecoration(dividerItemDecoration);

        MedicalRecordsRecyclerAdapter adapter = new MedicalRecordsRecyclerAdapter(medicalRecords,
                this, Globals.user.getUId().equals(userId));
        recyclerViewMedicalRecords.setAdapter(adapter);

        if (UserType.DOCTOR.getValue().equals(Globals.user.getUserType())) {
            buttonSelectFiles.setVisibility(View.GONE);
            buttonAddRecords.setVisibility(View.GONE);
            ((View) editTextDocumentName.getParent()).setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        buttonSelectFiles.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NormalFilePickActivity.class);
            intent.putExtra(Constant.MAX_NUMBER, 5);
            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
        });

        buttonAddRecords.setOnClickListener(v -> {
            StorageManager storageManager = new StorageManager(this);
            storageManager.uploadMultipleFileUpload(filesToUpload);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == Constant.REQUEST_CODE_PICK_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<NormalFile> filesList = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                if (filesList != null && !filesList.isEmpty()) {
                    for (NormalFile file : filesList) {
                        String[] split = file.getPath().split("/");
                        FileModel model = FileModel.builder()
                                .fileType(FileType.DOC)
                                .filePath(file.getPath())
                                .fileName(split[split.length - 1])
                                .mimeType(file.getMimeType())
                                .build();
                        filesToUpload.add(model);
                    }

                    textViewUploadFiles.setText(filesToUpload.stream().map(FileModel::getFileName)
                            .collect(Collectors.joining(",")));
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
    public void onUploadComplete(List<FileModel> fileModels) {
        MedicalRecords medicalRecords = new MedicalRecords();
        medicalRecords.setDocumentName(editTextDocumentName.getText().toString());
        medicalRecords.setFileModels(fileModels);

        this.medicalRecords.add(medicalRecords);
        dbManager.saveMedicalRecords(this.medicalRecords);

        dismissProgressDialog();
        filesToUpload.clear();

        Toast.makeText(getContext(), "Medical Records Upload Complete", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void onUpdateProgress(int currentPosition, int limit) {
        ++currentPosition;
        progressDialog.setMessage(String.format(Locale.getDefault(), "%d of %d files uploaded", currentPosition, limit));
        progressDialog.setProgress((int) (((float) currentPosition / (float) limit) * 100));
    }

    @Override
    public void onFailure(Exception exception) {
        dismissProgressDialog();
        Toast.makeText(getContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        filesToUpload.clear();
    }

    @Override
    public void onGetMedicalRecords(List<MedicalRecords> medicalRecords) {
        this.medicalRecords.clear();
        this.medicalRecords.addAll(medicalRecords);
        Objects.requireNonNull(recyclerViewMedicalRecords.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onItemClick(MedicalRecords record) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FileGridActivity.ARG_FILE_MODEL, record);

        Intent intent = new Intent(getContext(), FileGridActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(MedicalRecords record) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Delete")
                .setMessage(String.format("Want to delete %s?", record.getDocumentName()))
                .setPositiveButton("yes", (dialog, which) -> {
                    medicalRecords.remove(record);
                    dbManager.saveMedicalRecords(medicalRecords);
                })
                .setNegativeButton("no", ((dialog, which) -> dismiss()))
                .show();
    }
}
