package com.kv.callrecorder;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import static com.kv.callrecorder.Utility.Utils.hasPermissions;
import static com.kv.callrecorder.Utility.Utils.log;

public class PhoneStateListener extends BroadcastReceiver {
    static boolean incoming_flag;

    @Override
    public void onReceive(Context ctx, Intent intent) {

        String event = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (event.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            log("RINGING");
            incoming_flag = true;
        } else if (event.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            log("Connect");
            startService(ctx, intent);
        } else if (event.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            log("Disconnect");
            ctx.stopService(new Intent(ctx, MediaRecorderService.class));
        }
    }

    private void startService(Context context, Intent intent) {
        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (hasPermissions(context, PERMISSIONS)) {
            intent.setClass(context,MediaRecorderService.class);
            intent.putExtra("incoming_flag", incoming_flag);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } else {
            log("Permission Missing");
        }
    }
}
