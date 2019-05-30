package com.kv.callrecorder;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import static com.kv.callrecorder.Utility.Utils.hasPermissions;
import static com.kv.callrecorder.Utility.Utils.log;

public class PhoneStateListener extends BroadcastReceiver {
    boolean incoming_flag;

    @Override
    public void onReceive(Context ctx, Intent intent) {

        String event = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (event.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            log("RINGING");
            incoming_flag = true;
        } else if (event.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            log("Connect");
            startService(ctx, intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
        } else if (event.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            log("Disconnect");
            ctx.stopService(new Intent(ctx, MediaRecorderService.class));
        }
    }

    private void startService(Context context, String incomingNumber) {
        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (hasPermissions(context, PERMISSIONS)) {
            Intent i = new Intent(context, MediaRecorderService.class);
            i.putExtra("incoming_number", incomingNumber);
            i.putExtra("incoming_flag", incoming_flag);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(i);
            } else {
                context.startService(i);
            }
        } else {
            log("Permission Missing");
        }
    }
}
