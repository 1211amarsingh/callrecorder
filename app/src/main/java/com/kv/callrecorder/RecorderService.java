package com.kv.callrecorder;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.kv.callrecorder.Utility.Utils.log;

public class RecorderService extends Service {

    private MediaRecorder recorder;
    private String number = "temp";
    NotificationManagerCompat notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String tempnum = intent.getStringExtra("incoming_number");

        number = tempnum != null ? tempnum : number;

        log("Start " + number);
        startRecording();
        return super.onStartCommand(intent, flags, startId);
    }

    private void notificationBuilder() {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
        else{
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Recording")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);
        notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(getOutputFormat());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        log(getFilename());
        recorder.setOutputFile(getFilename());
        recorder.setOrientationHint(90);

        try {

            if (recorder != null) {
                recorder.prepare();


            }
        } catch (Exception e) {
            e.printStackTrace();
            log("Start Recording " + e);
            recorder = null;
        }
        recorder.start();
        notificationBuilder();
    }

    private int getOutputFormat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return MediaRecorder.OutputFormat.MPEG_2_TS;
        } else {
            return MediaRecorder.OutputFormat.AMR_NB;
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }

        try {

        } catch (Exception e) {
            log("stopRecording " + String.valueOf(e));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");
        stopRecording();

        stopForeground(true);
        notificationManager.cancel(1);
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        filepath += "/Call Recorder/" + fDate + "/";

        File file = new File(filepath);

        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/call_" + number + "_" + System.currentTimeMillis() + ".amr");
    }
}