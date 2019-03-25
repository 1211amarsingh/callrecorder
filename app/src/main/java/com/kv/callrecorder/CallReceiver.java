package com.kv.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import static com.kv.callrecorder.Utility.Utils.log;

public class CallReceiver extends BroadcastReceiver {
    static boolean incoming_flag;

    @Override
    public void onReceive(Context context, Intent intent) {
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        String event = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (event.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            incoming_flag = true;
        } else if (event.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            startService(context, incomingNumber);
        } else if (event.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            incoming_flag = false;
            stopService(context);
        }
    }

    private void startService(Context context, String incomingNumber) {
        Intent i = new Intent(context, MediaRecorderService.class);
//        Intent i = new Intent(context, AudioRecorderService.class);
        i.putExtra("incoming_number", incomingNumber);
        i.putExtra("incoming_flag", incoming_flag);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }

    private void stopService(Context context) {
        Intent i = new Intent(context, MediaRecorderService.class);
//        Intent i = new Intent(context, AudioRecorderService.class);
        context.stopService(i);
    }
}
