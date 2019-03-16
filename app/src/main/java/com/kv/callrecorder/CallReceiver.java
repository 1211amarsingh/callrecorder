package com.kv.callrecorder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import static com.kv.callrecorder.Utility.Utils.log;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        String status = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (status.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            startService(context, incomingNumber);
        } else if (status.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            stopService(context);
        }
    }

    private void stopService(Context context) {

        Intent i = new Intent(context, RecorderService.class);
        context.stopService(i);

    }

    private void startService(Context context, String incomingNumber) {
        Intent i = new Intent(context, RecorderService.class);
        i.putExtra("incoming_number", incomingNumber);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }
}
