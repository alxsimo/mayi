package com.alexsimo.mayi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionsHelper {

  private static final int PERMISSIONS_REQUEST_CODE = 212;

  public boolean hasPermissions(Context context, String... permissions) {
    for (String permission : permissions) {
      final int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
      if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  public void prompt(Activity activity, String[] permissions) {
    ActivityCompat.requestPermissions(activity, permissions, PERMISSIONS_REQUEST_CODE);
  }
}