package com.kv.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.kv.callrecorder.Utility.Utils;

import static com.kv.callrecorder.Utility.Utils.getPreferences;
import static com.kv.callrecorder.Utility.Utils.log;

public class CallReceiver extends BroadcastReceiver {
    static boolean incoming_flag;

    @Override
    public void onReceive(Context ctx, Intent intent) {

        String event = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (getPreferences(ctx, "permission").equals("1")) {

            if (event.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                log("RINGING");
                incoming_flag = true;
            } else if (event.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                log("Connect");
                startService(ctx, intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
            } else if (event.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                log("Disconnect");
                incoming_flag = false;
                stopService(ctx);
            }
        }else {
            log("Permission Missing");
        }
    }

    private void startService(Context context, String incomingNumber) {
        Intent i = new Intent(context, MediaRecorderService.class);
        i.putExtra("incoming_number", incomingNumber);
        i.putExtra("incoming_flag", incoming_flag);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }

    private void stopService(Context context) {
        context.stopService(new Intent(context, MediaRecorderService.class));
    }
}
