package com.kv.callrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.kv.callrecorder.Utility.Utils.log;

public class AudioRecorderService extends Service {

    RecordAudio recordTask;
    boolean isRecording = false;
    File recordingFile;
    int sampleRateInHz = 11025; //44100, 22050, 11025, 16000, 8000
    int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; // AudioFormat.CHANNEL_CONFIGURATION_STEREO
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int source = MediaRecorder.AudioSource.MIC;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        record();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isRecording = false;
    }

    public void record() {
        recordTask = new RecordAudio();
        recordTask.execute();
    }

    private File getFileDir() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        filepath += "/Call Recorder/" + fDate + "/";

        File file = new File(filepath);

        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private class RecordAudio extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            isRecording = true;
            log("Start Recording...");
            try {
                recordingFile = File.createTempFile("call", ".pcm", getFileDir());
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(recordingFile)));
                int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
                final AudioRecord audioRecord = new AudioRecord(source, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
                short[] buffer = new short[bufferSizeInBytes];

                audioRecord.startRecording();

                log(sampleRateInHz + " " + channelConfig + " " + audioFormat + " " + bufferSizeInBytes + " " + audioRecord.getRecordingState());
                int r = 0;
                while (isRecording) {
                    int bufferReadResult = audioRecord.read(buffer, 0, bufferSizeInBytes);
                    for (int i = 0; i < bufferReadResult; i++) {
                        dos.writeShort(buffer[i]);
                    }
                    publishProgress(new Integer(r));
                    r++;
                }
                audioRecord.stop();
                dos.close();
            } catch (Exception e) {
                e.printStackTrace();
                log("Error : " + String.valueOf(e.getMessage()));
            }
            log("Stop Recording");
            return null;
        }
    }
}
