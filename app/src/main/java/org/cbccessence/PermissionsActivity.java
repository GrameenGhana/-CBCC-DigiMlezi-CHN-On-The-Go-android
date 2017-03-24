package org.cbccessence;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import org.digitalcampus.oppia.activity.LoginActivity;


/**
 * Created by aangjnr on 08/02/2017.
 */


public class PermissionsActivity  extends LoginActivity {

    String TAG = PermissionsActivity.class.getSimpleName();

    static String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.VIBRATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR

    };

    static int OVERLAY_PERMISSION_REQ_CODE = 10;
     static Boolean hasAllPermissions = null;


    public static boolean launchMultiplePermissions(Activity context) {
        Boolean hasPermissions = null;

        for(String permission : PERMISSIONS){
            if(!hasPermission(context, permission)){


                if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                    ActivityCompat.requestPermissions(context, PERMISSIONS, 30);
                } else {
                    ActivityCompat.requestPermissions(context, PERMISSIONS, 30);
                }

                return false;
            }


        }

        return true;
    }


    public static boolean hasPermission(Context context, String PERMISSION) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {

            if (ActivityCompat.checkSelfPermission(context, PERMISSION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, PERMISSION)) {


                }
                return false;
            }

        }
        return true;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);



        if(grantResults[0] != PackageManager.PERMISSION_GRANTED ) {

            hasAllPermissions = false;

        }else hasAllPermissions = true;
    }



}





