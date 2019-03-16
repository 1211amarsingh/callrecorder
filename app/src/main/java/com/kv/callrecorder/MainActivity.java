package com.kv.callrecorder;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;

import static com.kv.callrecorder.Utility.Utils.hasPermissions;

public class MainActivity extends AppCompatActivity {

    Activity activity;

    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBodyUI();
    }

    private void setBodyUI() {
        activity = this;

        check_Permission();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException ignored) {

        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
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
}
