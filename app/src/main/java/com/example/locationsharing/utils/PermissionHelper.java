package com.example.locationsharing.utils;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import pub.devrel.easypermissions.EasyPermissions;

public class PermissionHelper {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public static boolean hasLocationPermissions(Activity activity) {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        return EasyPermissions.hasPermissions(activity, perms);
    }

    public static void requestLocationPermissions(Activity activity) {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        EasyPermissions.requestPermissions(activity, "Location permission is needed", LOCATION_PERMISSION_REQUEST_CODE, perms);
    }

    public static boolean hasBackgroundLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return EasyPermissions.hasPermissions(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        return true;
    }

    public static void requestBackgroundLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(activity, "Background location is needed", LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
    }
}
