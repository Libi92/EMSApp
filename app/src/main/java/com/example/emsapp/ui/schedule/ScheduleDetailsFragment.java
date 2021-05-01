package com.example.emsapp.ui.schedule;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.emsapp.R;
import com.example.emsapp.base.BaseFragment;
import com.example.emsapp.constants.ScheduleStatus;
import com.example.emsapp.constants.UserType;
import com.example.emsapp.db.ConsultationDbManager;
import com.example.emsapp.model.AppUser;
import com.example.emsapp.model.ConsultationRequest;
import com.example.emsapp.util.Globals;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

public class ScheduleDetailsFragment extends BaseFragment {

    public static final String ARG_SCHEDULE = "arg::Schedule";
    private static final String TAG = ScheduleDetailsFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 1992;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };
    private CardView cardViewPhone;
    private CardView cardViewEmail;
    private Button buttonCall;
    private Button buttonAddPrescription;
    private Button buttonClose;

    private String phone;
    private ConsultationRequest consultationRequest;
    private AppUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_details, container, false);

        initLayout(view);
        initListeners();
        initJitsi();

        return view;
    }

    private void initLayout(View view) {
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewDesignation = view.findViewById(R.id.textViewDesignation);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);
        TextView textViewEmail = view.findViewById(R.id.textViewEmail);

        cardViewPhone = view.findViewById(R.id.cardViewPhone);
        cardViewEmail = view.findViewById(R.id.cardViewEmail);
        buttonCall = view.findViewById(R.id.buttonCall);
        buttonAddPrescription = view.findViewById(R.id.buttonPrescription);
        buttonClose = view.findViewById(R.id.buttonClose);

        Bundle bundle = getArguments();
        if (bundle != null) {
            consultationRequest = (ConsultationRequest) bundle.getSerializable(ARG_SCHEDULE);
            user = consultationRequest.getFromUser();
            textViewName.setText(user.getDisplayName());
            textViewDesignation.setText(user.getDesignation());
            textViewPhone.setText(user.getPhone());
            textViewEmail.setText(user.getEmail());

            if (ScheduleStatus.COMPLETE.getValue().equals(consultationRequest.getScheduleStatus())) {
                buttonCall.setVisibility(View.GONE);
                buttonClose.setVisibility(View.GONE);
            }
        }

        if (UserType.USER.getValue().equals(Globals.user.getUserType())) {
            buttonClose.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        cardViewPhone.setOnClickListener(v -> checkCallPermission(user.getPhone()));
        cardViewEmail.setOnClickListener(v -> {
            String[] addresses = {user.getEmail()};
            String subject = "Query on appointment with " + consultationRequest.getToDoctor().getDisplayName();
            composeEmail(addresses, subject);
        });

        buttonCall.setOnClickListener(v -> startJitsi(String.format("%s - %s", consultationRequest.getToDoctor().getDisplayName(),
                consultationRequest.getFromUser().getDisplayName())));

        buttonAddPrescription.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(AddPrescriptionFragment.ARG_CONSULTATION, consultationRequest);

            AddPrescriptionFragment prescriptionFragment = new AddPrescriptionFragment();
            prescriptionFragment.setArguments(bundle);
            prescriptionFragment.show(getParentFragmentManager(), TAG);
        });

        buttonClose.setOnClickListener(v -> {
            ConsultationDbManager dbManager = new ConsultationDbManager.Builder().build();
            dbManager.closeConsultation(consultationRequest);

            Snackbar.make(getView(), "Consultation Completed", Snackbar.LENGTH_SHORT)
                    .setAction("Ok", v1 -> getChildFragmentManager().popBackStack())
                    .show();
        });
    }

    private void checkCallPermission(String phone) {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {

            makeCall(phone);
        } else {
            this.phone = phone;
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE);
        }
    }

    private void makeCall(String phone) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Call this User")
                .setMessage("Carrier charges will be applicable")
                .setPositiveButton("Ok", (dialog, which) -> {
                    String uri = "tel:" + phone;
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall(this.phone);
            } else {
                showSnackbar("Permission is required to make call");
            }
        }
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // Example for handling different JitsiMeetSDK events

    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);

            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participant joined%s", event.getData().get("name"));
                    break;
            }
        }
    }

    private void initJitsi() {
        // Initialize default options for Jitsi Meet conferences.
        URL serverURL;
        try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                // When using JaaS, set the obtained JWT here
                //.setToken("MyJWT")
                // Different features flags can be set
                // .setFeatureFlag("toolbox.enabled", false)
                // .setFeatureFlag("filmstrip.enabled", false)
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        registerForBroadcastMessages();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... other events
         */
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void startJitsi(String meetingRoom) {
        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(meetingRoom)
                // Settings for audio and video
                //.setAudioMuted(true)
                //.setVideoMuted(true)
                .build();
        // Launch the new activity with the given options. The launch() method takes care
        // of creating the required Intent and passing the options.
        JitsiMeetActivity.launch(getContext(), options);
    }

    // Example for sending actions to JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(hangupBroadcastIntent);
    }
}
