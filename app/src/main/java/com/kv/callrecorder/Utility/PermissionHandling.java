package com.kv.callrecorder.Utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class PermissionHandling {

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;//DENIED;
                }
            }
        }
        Log.e("hasPermissions", "GRANTED");
        return true;//GRANTED
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

    public static void displayNeverAskAgainDialog(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Please allow the permissions to use this features \n"
                + "Tap Settings -> Select Permissions and Enable permission");
        builder.setCancelable(false);
        builder.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("NOT NOW", null);
        builder.show();
    }
}
