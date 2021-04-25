package com.example.emsapp.ui.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.constants.ScheduleStatus;
import com.example.emsapp.db.ConsultationDbManager;
import com.example.emsapp.db.ConsultationListener;
import com.example.emsapp.model.ConsultationRequest;
import com.example.emsapp.ui.adapters.ConsultationRecyclerAdapter;
import com.example.emsapp.util.Globals;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ScheduleFragment extends Fragment implements ConsultationListener, ConsultationRecyclerAdapter.ConsultationClickListener {

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };
    private List<ConsultationRequest> requestList;
    private ConsultationRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_schedule, container, false);

        initLayout(root);
        getConsultations();

        initJitsi();

        return root;
    }

    private void initLayout(View root) {
        RecyclerView recyclerViewConsultations = root.findViewById(R.id.recyclerViewSchedule);

        Context context = getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewConsultations.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        recyclerViewConsultations.addItemDecoration(dividerItemDecoration);

        requestList = new ArrayList<>();
        adapter = new ConsultationRecyclerAdapter(requestList);
        adapter.setConsultationListener(this);
        recyclerViewConsultations.setAdapter(adapter);
    }

    private void getConsultations() {
        ConsultationDbManager dbManager = new ConsultationDbManager.Builder()
                .setConsultationListener(this)
                .build();

        dbManager.getConsultations(Globals.user);
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


    @Override
    public void onConsultations(List<ConsultationRequest> consultationRequests) {
        requestList.clear();
        for (ConsultationRequest request : consultationRequests) {
            if (ScheduleStatus.SCHEDULED.getValue().equals(request.getScheduleStatus())) {
                requestList.add(request);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnClick(ConsultationRequest consultationRequest) {
        startJitsi(String.format("%s - %s", consultationRequest.getToDoctor().getDisplayName(),
                consultationRequest.getFromUser().getDisplayName()));
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