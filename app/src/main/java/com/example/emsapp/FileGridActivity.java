package com.example.emsapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.model.FileModel;
import com.example.emsapp.model.MedicalRecords;
import com.example.emsapp.ui.adapters.MedicalRecordItemAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileGridActivity extends AppCompatActivity implements MedicalRecordItemAdapter.OnFileItemClickListener {

    public static final String ARG_FILE_MODEL = "arg::fileModel";
    private static final int PERM_REQ_CODE = 1009;
    private final List<FileModel> fileModels = new ArrayList<>();
    private final BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
//            openFile();
            Toast.makeText(getApplicationContext(), "File downloaded", Toast.LENGTH_SHORT).show();
        }
    };
    private FileModel fileModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_grid);

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        getData();
        initLayout();
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        MedicalRecords record = (MedicalRecords) bundle.getSerializable(ARG_FILE_MODEL);
        fileModels.addAll(record.getFileModels());
    }

    private void initLayout() {
        RecyclerView recyclerViewFiles = findViewById(R.id.recyclerFiles);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerViewFiles.setLayoutManager(layoutManager);

        MedicalRecordItemAdapter adapter = new MedicalRecordItemAdapter(fileModels, this);
        recyclerViewFiles.setAdapter(adapter);
    }

    @Override
    public void onItemClick(FileModel fileModel) {
        this.fileModel = fileModel;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            downloadFile(fileModel);
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_REQ_CODE);
        }
    }

    private void openFile() {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileModel.getFileName());
        String type = fileModel.getMimeType();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName(), file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
        startActivityForResult(intent, 1009);
    }

    private void downloadFile(FileModel fileModel) {
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(fileModel.getFilePath());

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileModel.getFileName());
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileModel.getFileName());
        downloadmanager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_REQ_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                downloadFile(this.fileModel);
            } else {
                Toast.makeText(this, "Permission is required to view file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
