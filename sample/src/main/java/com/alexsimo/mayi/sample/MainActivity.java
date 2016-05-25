package com.alexsimo.mayi.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.alexsimo.mayi.Mayi;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  protected void onStart() {
    super.onStart();
    requestPermissions();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    Mayi.onRequestPermissionsResult(this, permissions, grantResults);
  }

  private void requestPermissions() {
    Mayi.from(this)
        .withPermissions(Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION)
        .needExplanation()
        .onUiThread()
        .andFallback(onPermissionDenied())
        .run(onPermissionGranted());
  }

  private Runnable onPermissionGranted() {
    return new Runnable() {
      public void run() {
        Log.d(TAG, "Mayi granted");
      }
    };
  }

  private Runnable onPermissionDenied() {
    return new Runnable() {
      public void run() {
        Log.d(TAG, "Permissions DENIED");
      }
    };
  }
}