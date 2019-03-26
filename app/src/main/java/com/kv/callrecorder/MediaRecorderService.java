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
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.kv.callrecorder.Utility.Utils.log;

public class MediaRecorderService extends Service {

    private MediaRecorder recorder;
    private String number = "temp";
    NotificationManagerCompat notificationManager;
    private boolean status;
    private boolean incoming_flag;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String tempnum = intent.getStringExtra("incoming_number");
        incoming_flag = intent.getBooleanExtra("incoming_flag", false);
        number = tempnum != null ? tempnum : number;

        startRecording();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        log("Start " + getFilename());
        recorder.setOutputFile(getFilename());
        recorder.setOrientationHint(Surface.ROTATION_0);

        try {
            recorder.prepare();
            recorder.start();
            notificationBuilder();
            status = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notificationBuilder() {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Recording")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOngoing(true);
            notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(1, builder.build());
        }
    }

    private void stopRecording() {
        log("Stop " + status);

        if (status) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;

            if (Build.VERSION.SDK_INT >= 26) {
                stopForeground(true);
            }else {
                notificationManager.cancel(1);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    private String getFilename() {
        String state = "OUT_";
        String filepath = Environment.getExternalStorageDirectory().getPath();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        filepath += "/Call Recorder/" + date + "/";

        File file = new File(filepath);

        if (!file.exists()) {
            file.mkdirs();
            createNomedia(file.getAbsolutePath());
        }
        if (incoming_flag) {
            state = "IN_";
        }
        String time = new SimpleDateFormat("hhmmss").format(new Date());
        return (file.getAbsolutePath() + "/CALL_"+state + number + "_" + time + ".amr");
    }

    private void createNomedia(String absolutePath) {
        File file = new File(absolutePath+"/"+".nomedia");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}