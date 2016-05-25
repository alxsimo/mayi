package com.alexsimo.mayi;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

public class PermissionBlock {

  private final PermissionsHelper helper;
  private Context context;
  private String[] permissions;
  private boolean onUIThread;
  private Runnable onPermissionsGranted;
  private Runnable onPermissionsDenied;
  private boolean doNotPrompt;

  public PermissionBlock(Context context, PermissionsHelper helper) {
    this.context = context;
    this.helper = helper;
  }

  public PermissionBlock withPermissions(@NonNull String... permissions) {
    this.permissions = permissions;
    return this;
  }

  public PermissionBlock onUIThread() {
    this.onUIThread = true;
    return this;
  }

  public PermissionBlock doNotPrompt() {
    doNotPrompt = true;
    return this;
  }

  public PermissionBlock doNotPromptIf(boolean condition) {
    if (condition) {
      doNotPrompt();
    }
    return this;
  }

  public void run() {
    run(null);
  }

  public void run(Runnable onPermissionsGranted) {
    if (!doNotPrompt && !(context instanceof Activity)) {
      throw new IllegalStateException(
          "You need to either specify doNotPrompt() or pass in an Activity context");
    }

    this.onPermissionsGranted = onPermissionsGranted;

    if (hasPermissions(context)) {
      onPermissionsGranted();
    } else if (doNotPrompt) {
      onPermissionsDenied();
    } else {
      Permissions.prompt((Activity) context, this);
    }
    context = null;
  }

  public PermissionBlock andFallback(@NonNull Runnable onPermissionsDenied) {
    this.onPermissionsDenied = onPermissionsDenied;
    return this;
  }

  void onPermissionsGranted() {
    executeRunnable(onPermissionsGranted);
  }

  void onPermissionsDenied() {
    executeRunnable(onPermissionsDenied);
  }

  private void executeRunnable(Runnable runnable) {
    if (runnable == null) {
      return;
    }

    if (onUIThread) {
      ThreadUtils.postToUiThread(runnable);
    } else {
      runnable.run();
    }
  }

  String[] getPermissions() {
    return permissions;
  }

  boolean hasPermissions(Context context) {
    return helper.hasPermissions(context, permissions);
  }
}