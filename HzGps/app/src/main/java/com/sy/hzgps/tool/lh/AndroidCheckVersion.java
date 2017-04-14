package com.sy.hzgps.tool.lh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Trust on 16/12/27.
 */
public class AndroidCheckVersion {
    private Context context;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    public static String[] PERMISSION = {Manifest.permission.READ_PHONE_STATE};
    public AndroidCheckVersion(Context context)
    {
        this.context = context;

    }



    public  boolean isLacksOfPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(
                    context, permission) == PackageManager.PERMISSION_DENIED;
        }
        return false;
    }

    public boolean checkVersion ()
    {







            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {



                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
//                    Toast.makeText(context,"你拒绝打开定位权限! ",Toast.LENGTH_SHORT).show();

                } else {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    return true;
                }


            }





        return false;
    }

}
