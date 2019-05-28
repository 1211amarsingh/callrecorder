package com.kv.callrecorder.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class PermissionHandling {

    private static boolean hasPermissions(Activity activity, String... permissions) {
        if (activity != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean checkPermissionWithDialog(Activity activity, String[] PERMISSIONS) {
        if (Build.VERSION.SDK_INT >= 23 && !hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, 1);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkRequiredPermissions(Activity activity) {
        String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        return checkPermissionWithDialog(activity, PERMISSIONS);
    }
}
