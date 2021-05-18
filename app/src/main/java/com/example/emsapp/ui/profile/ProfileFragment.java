package com.example.emsapp.ui.profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.emsapp.R;
import com.example.emsapp.base.BaseFragment;
import com.example.emsapp.constants.FileType;
import com.example.emsapp.db.UserDbManager;
import com.example.emsapp.model.AppUser;
import com.example.emsapp.model.FileModel;
import com.example.emsapp.storage.StorageListener;
import com.example.emsapp.storage.StorageManager;
import com.example.emsapp.util.Globals;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class ProfileFragment extends BaseFragment implements StorageListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ImageView imageViewProfile;
    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private EditText editTextEmail;
    private EditText editTextDoB;
    private Spinner spinnerGender;
    private Button buttonUpdate;
    private FloatingActionButton fabAddTrustedContacts;
    private FloatingActionButton fabAddMedicalRecords;
    private ProgressDialog progressDialog;

    private String profileImagePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initLayout(view);
        initListeners();

        return view;
    }

    private void initLayout(View view) {
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        editTextName = view.findViewById(R.id.editTextName);
        editTextDoB = view.findViewById(R.id.editTextDoB);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        fabAddTrustedContacts = view.findViewById(R.id.fabAddTrustedContacts);
        fabAddMedicalRecords = view.findViewById(R.id.fabAddMedicalRecords);

        AppUser appUser = Globals.user;
        editTextName.setText(appUser.getDisplayName());
        editTextDoB.setText(appUser.getDob());

        String[] genderArray = getResources().getStringArray(R.array.gender);
        int genderLength = genderArray.length;
        int spinnerPosition = IntStream.range(0, genderLength).filter(i -> genderArray[i].equals(appUser.getGender())).findFirst().orElse(0);

        spinnerGender.setSelection(spinnerPosition);
        editTextPhone.setText(appUser.getPhone());
        editTextAddress.setText(appUser.getAddress());
        editTextEmail.setText(appUser.getEmail());

        if (appUser.getPhoto() != null && !appUser.getPhoto().isEmpty()) {
            Glide.with(getContext()).load(appUser.getPhoto()).into(imageViewProfile);
        }
    }

    private void initListeners() {

        imageViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ImagePickActivity.class);
            intent.putExtra(ImagePickActivity.IS_NEED_CAMERA, true);
            intent.putExtra(Constant.MAX_NUMBER, 1);
            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
        });

        buttonUpdate.setOnClickListener(v -> {

            if (profileImagePath != null) {
                FileModel model = FileModel.builder()
                        .fileType(FileType.IMAGE)
                        .filePath(profileImagePath)
                        .build();

                StorageManager storageManager = new StorageManager(this);
                storageManager.uploadFile(model);
            } else {
                updateProfile(null);
            }
        });

        fabAddTrustedContacts.setOnClickListener(v -> {
            TrustedContactsFragment contactsFragment = new TrustedContactsFragment();
            contactsFragment.show(getChildFragmentManager(), TAG);
        });

        fabAddMedicalRecords.setOnClickListener(v -> {
            MedicalRecordsFragment recordsFragment = new MedicalRecordsFragment();
            recordsFragment.show(getChildFragmentManager(), TAG);
        });

        editTextDoB.setOnTouchListener((v, event) -> {

            if (event.equals(MotionEvent.ACTION_UP)) {
                showDoBPicker();
            }
            return false;
        });

        editTextDoB.setOnClickListener(v -> showDoBPicker());
    }

    private void showDoBPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
        datePickerDialog.setOnDateSetListener(
                (view, year, month, dayOfMonth) ->
                        editTextDoB.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)));
        datePickerDialog.show();
    }

    private void updateProfile(String profilePicture) {
        String name = editTextName.getText().toString();
        String dob = editTextDoB.getText().toString();
        String gender = spinnerGender.getSelectedItem().toString();
        String phone = editTextPhone.getText().toString();
        String address = editTextAddress.getText().toString();
        String email = editTextEmail.getText().toString();

        AppUser appUser = Globals.user;
        appUser.setDisplayName(name);
        appUser.setDob(dob);
        appUser.setGender(gender);
        appUser.setPhone(phone);
        appUser.setAddress(address);
        appUser.setEmail(email);

        if (profilePicture != null) {
            appUser.setPhoto(profilePicture);
        }

        UserDbManager dbManager = new UserDbManager.Builder().build();
        dbManager.createUser(appUser);

        showSnackbar("User Profile Updated");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<ImageFile> imagesFiles = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                if (imagesFiles != null && !imagesFiles.isEmpty()) {
                    profileImagePath = imagesFiles.get(0).getPath();

                    Bitmap bitmap = BitmapFactory.decodeFile(profileImagePath);
                    imageViewProfile.setImageBitmap(bitmap);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onUploadStart() {
        initProgressDialog();
    }

    @Override
    public void onUploadComplete(List<FileModel> uriList) {
        dismissProgressDialog();
        updateProfile(uriList.get(0).getFilePath());
    }

    @Override
    public void onUpdateProgress(int currentPosition, int limit) {

    }

    @Override
    public void onFailure(Exception exception) {
        dismissProgressDialog();
        Toast.makeText(getContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
