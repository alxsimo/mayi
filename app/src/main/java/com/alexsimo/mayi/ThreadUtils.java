package com.alexsimo.mayi;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public final class ThreadUtils {

  private static final String TAG = ThreadUtils.class.getSimpleName();

  public enum AssertBehavior {
    NONE,
    THROW,
  }

  private static final Thread UI_THREAD = Looper.getMainLooper().getThread();
  private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());
  private static volatile Thread backgroundThread;

  public static void setBackgroundThread(Thread thread) {
    backgroundThread = thread;
  }

  public static Thread getUiThread() {
    return UI_THREAD;
  }

  public static Handler getUiHandler() {
    return UI_HANDLER;
  }

  public static void postToUiThread(Runnable runnable) {
    UI_HANDLER.post(runnable);
  }

  public static void postDelayedToUiThread(Runnable runnable, long timeout) {
    UI_HANDLER.postDelayed(runnable, timeout);
  }

  public static void removeCallbacksFromUiThread(Runnable runnable) {
    UI_HANDLER.removeCallbacks(runnable);
  }

  public static Thread getBackgroundThread() {
    return backgroundThread;
  }

  public static void assertOnUiThread(final AssertBehavior assertBehavior) {
    assertOnThread(getUiThread(), assertBehavior);
  }

  public static void assertOnUiThread() {
    assertOnThread(getUiThread(), AssertBehavior.THROW);
  }

  public static void assertNotOnUiThread() {
    assertNotOnThread(getUiThread(), AssertBehavior.THROW);
  }

  public static void assertOnBackgroundThread() {
    assertOnThread(getBackgroundThread(), AssertBehavior.THROW);
  }

  public static void assertOnThread(final Thread expectedThread) {
    assertOnThread(expectedThread, AssertBehavior.THROW);
  }

  public static void assertOnThread(final Thread expectedThread, AssertBehavior behavior) {
    assertOnThreadComparison(expectedThread, behavior, true);
  }

  public static void assertNotOnThread(final Thread expectedThread, AssertBehavior behavior) {
    assertOnThreadComparison(expectedThread, behavior, false);
  }

  private static void assertOnThreadComparison(final Thread expectedThread, AssertBehavior behavior,
      boolean expected) {
    final Thread currentThread = Thread.currentThread();
    final long currentThreadId = currentThread.getId();
    final long expectedThreadId = expectedThread.getId();

    if ((currentThreadId == expectedThreadId) == expected) {
      return;
    }

    final String message;
    if (expected) {
      message = "Expected thread " + expectedThreadId +
          " (\"" + expectedThread.getName() + "\"), but running on thread " +
          currentThreadId + " (\"" + currentThread.getName() + "\")";
    } else {
      message = "Expected anything but " + expectedThreadId +
          " (\"" + expectedThread.getName() + "\"), but running there.";
    }

    final IllegalThreadStateException e = new IllegalThreadStateException(message);

    switch (behavior) {
      case THROW:
        throw e;
      default:
        Log.e(TAG, "Method called on wrong thread!", e);
    }
  }

  public static boolean isOnUiThread() {
    return isOnThread(getUiThread());
  }

  public static boolean isOnBackgroundThread() {
    if (backgroundThread == null) {
      return false;
    }

    return isOnThread(backgroundThread);
  }

  public static boolean isOnThread(Thread thread) {
    return (Thread.currentThread().getId() == thread.getId());
  }
}