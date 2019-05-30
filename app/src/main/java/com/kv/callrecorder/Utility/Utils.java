package com.kv.callrecorder.Utility;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;


public class Utils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showDialog(Context context, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(title)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void log(String message) {
        String Tag = "Util";
        Log.w(Tag, message);
//        createLogFile(message);
    }

    private static void createLogFile(String message) {
        String time = new SimpleDateFormat("yyyy-MM-dd hhmmss").format(new Date()) + " ";
        String path = Environment.getExternalStorageDirectory().getPath() + "/Call Recorder/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(path + "/Log.txt", true);
            fos.write(time.getBytes());
            fos.write(message.getBytes());
            fos.write("\n".getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isGpsOn(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWifiOn(Context context) {

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            //wifi is enabled
            return true;
        } else {
            return false;
        }
    }

//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    //return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

    public static void setPreferences(Context con, String key, String value) {

        SharedPreferences preferences = con.getSharedPreferences("session", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPreferences(Context con, String key) {
        SharedPreferences sharedPreferences = con.getSharedPreferences("session", 0);
        String value = sharedPreferences.getString(key, "0");
        return value;

    }

    public static void clearPreferences(Context con) {
        SharedPreferences preferences = con.getSharedPreferences("session", 0);//Context.MODE_PRIVATE
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void exitDialog(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * creating new image file here
     *
     * @return
     * @throws IOException
     */
    public static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * hide soft keyboard and remove focus from view
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * show soft keyboard
     *
     * @param activity
     */
    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean isFileDeleted(String filepath) {
        File file = new File(filepath);
        return file.exists() && file.delete();
    }

}
