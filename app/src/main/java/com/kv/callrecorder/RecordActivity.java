package com.kv.callrecorder;

import android.Manifest;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kv.callrecorder.Utility.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kv.callrecorder.Utility.Utils.hasPermissions;
import static com.kv.callrecorder.Utility.Utils.log;

public class RecordActivity extends AppCompatActivity {

    Activity activity;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    ArrayList<AudioModel> audiolist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setBodyUI();
    }

    private void setBodyUI() {
        activity = this;
        check_Permission();

        setRecyclerView();
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        getRecordedFiles();
        RecordAdapter recordAdapter = new RecordAdapter(activity, audiolist);
        recyclerView.setAdapter(recordAdapter);
    }

    private void getRecordedFiles() {
        String path = Environment.getExternalStorageDirectory().toString() + "/Call Recorder";

        File folder = new File(path);
        File[] listOfDirs = folder.listFiles();

        for (File listOfDir : listOfDirs) {
            if (listOfDir.isDirectory()) {
                File folder2 = new File(path + '/' + listOfDir.getName());
                File[] listOfFile = folder2.listFiles();
                for (File audio : listOfFile) {
                    if (!audio.getName().equals(".nomedia")) {
                        AudioModel audioModel = new AudioModel();
                        audioModel.setName(audio.getName());
                        audioModel.setPath(audio.getAbsolutePath());
                        audiolist.add(audioModel);
                    }
                }
            }
        }
    }

    /**
     * check permission is granted or not
     * @return
     */
    public boolean check_Permission() {
        String[] permissions = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //checking for required permissions
        if (Build.VERSION.SDK_INT >= 23 && !hasPermissions(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, 1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int permission = 1;
        for (int status : grantResults) {
            if (status == -1) {
                permission = 0;
            }
        }
        Utils.setPreferences(this, "permission", String.valueOf(permission));
    }
}
