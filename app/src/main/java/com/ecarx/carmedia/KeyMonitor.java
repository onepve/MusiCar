package com.ecarx.carmedia;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes3.dex */
public class KeyMonitor {
    public static final String ACTION_HOME = "home";
    public static final String ACTION_MODE = "mode";
    public static final String ACTION_MUTE = "mute";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PLAYPAUSE = "playpause";
    public static final String ACTION_PREV = "prev";
    public static final String ACTION_SOURCE = "source";
    private static final long DOUBLE_CLICK_WINDOW_MS = 400;
    public static final int KEY_MODE = 348;
    public static final int KEY_MUTE = 300;
    public static final int KEY_NEXT = 305;
    public static final int KEY_PREV = 304;
    private static final long LONG_PRESS_WINDOW_MS = 600;
    private static final String TAG = "CarMedia";
    private final Callback callback;
    private KeyRecorder pendingRecorder;
    private Map<String, String> actions = new HashMap();
    private final Map<Integer, Long> lastKeyDownTime = new HashMap();
    private final Map<Integer, Runnable> pendingSingleClick = new HashMap();
    private final Map<Integer, Runnable> pendingLongPress = new HashMap();
    private final Map<Integer, Boolean> longPressed = new HashMap();
    private final Map<Integer, Boolean> keyDownState = new HashMap();
    private final Map<Integer, Boolean> pendingSingleOnUp = new HashMap();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onKeyAction(String str, int i);

        void onMonitorStatus(String str);
    }

    public interface KeyRecorder {
        void onKeyRecorded(int i);
    }

    public KeyMonitor(Context context, Callback callback) {
        this.callback = callback;
        this.actions.put("348:1", "openapp:" + context.getPackageName());
        this.actions.put("348:2", ACTION_SOURCE);
        this.actions.put("304:1", ACTION_PREV);
        this.actions.put("305:1", ACTION_NEXT);
        this.actions.put("300:1", ACTION_PLAYPAUSE);
    }

    public synchronized void updateActions(Map<String, String> newActions) {
        this.actions = new HashMap();
        if (newActions != null) {
            this.actions.putAll(newActions);
        }
    }

    public synchronized Map<String, String> getActions() {
        return new HashMap(this.actions);
    }

    public synchronized boolean hasAction(String action) {
        return this.actions.containsValue(action);
    }

    public synchronized void startRecording(KeyRecorder recorder) {
        this.pendingRecorder = recorder;
    }

    public synchronized void stopRecording() {
        this.pendingRecorder = null;
    }

    public synchronized void handleExternalKeyDown(int keyCode) {
        KeyRecorder rec = this.pendingRecorder;
        if (rec != null) {
            this.pendingRecorder = null;
            rec.onKeyRecorded(keyCode);
        } else {
            this.keyDownState.put(Integer.valueOf(keyCode), true);
            handleKeyDown(keyCode);
        }
    }

    public synchronized void handleExternalKeyUp(int keyCode) {
        this.keyDownState.remove(Integer.valueOf(keyCode));
        if (Boolean.TRUE.equals(this.longPressed.get(Integer.valueOf(keyCode)))) {
            this.longPressed.remove(Integer.valueOf(keyCode));
            return;
        }
        cancelLongPress(keyCode);
        if (Boolean.TRUE.equals(this.pendingSingleOnUp.get(Integer.valueOf(keyCode)))) {
            this.pendingSingleOnUp.remove(Integer.valueOf(keyCode));
            String single = this.actions.get(keyCode + ":1");
            if (single != null) {
                this.callback.onKeyAction(single, keyCode);
            }
        }
    }

    private void handleKeyDown(final int keyCode) {
        final String single = this.actions.get(keyCode + ":1");
        String dbl = this.actions.get(keyCode + ":2");
        String lp = this.actions.get(keyCode + ":3");
        final boolean hasLongPress = lp != null;
        if (single == null && dbl == null && lp == null) {
            return;
        }
        if (hasLongPress) {
            startLongPressTimer(keyCode);
        }
        long now = SystemClock.elapsedRealtime();
        long last = this.lastKeyDownTime.containsKey(Integer.valueOf(keyCode)) ? this.lastKeyDownTime.get(Integer.valueOf(keyCode)).longValue() : 0L;
        this.lastKeyDownTime.put(Integer.valueOf(keyCode), Long.valueOf(now));
        if (dbl == null) {
            if (single != null) {
                if (hasLongPress) {
                    this.pendingSingleOnUp.put(Integer.valueOf(keyCode), true);
                    return;
                } else {
                    cancelPendingSingle(keyCode);
                    this.callback.onKeyAction(single, keyCode);
                    return;
                }
            }
            return;
        }
        if (last > 0 && now - last < DOUBLE_CLICK_WINDOW_MS) {
            cancelPendingSingle(keyCode);
            cancelLongPress(keyCode);
            this.pendingSingleOnUp.remove(Integer.valueOf(keyCode));
            this.callback.onKeyAction(dbl, keyCode);
            return;
        }
        Runnable r = new Runnable() { // from class: com.ecarx.carmedia.KeyMonitor.1
            @Override // java.lang.Runnable
            public void run() {
                KeyMonitor.this.pendingSingleClick.remove(Integer.valueOf(keyCode));
                if (hasLongPress && Boolean.TRUE.equals(KeyMonitor.this.keyDownState.get(Integer.valueOf(keyCode)))) {
                    KeyMonitor.this.pendingSingleOnUp.put(Integer.valueOf(keyCode), true);
                } else if (single != null) {
                    KeyMonitor.this.callback.onKeyAction(single, keyCode);
                }
            }
        };
        this.pendingSingleClick.put(Integer.valueOf(keyCode), r);
        this.handler.postDelayed(r, DOUBLE_CLICK_WINDOW_MS);
    }

    private void startLongPressTimer(final int keyCode) {
        cancelLongPress(keyCode);
        Runnable r = new Runnable() { // from class: com.ecarx.carmedia.KeyMonitor.2
            @Override // java.lang.Runnable
            public void run() {
                KeyMonitor.this.pendingLongPress.remove(Integer.valueOf(keyCode));
                if (Boolean.TRUE.equals(KeyMonitor.this.keyDownState.get(Integer.valueOf(keyCode)))) {
                    KeyMonitor.this.longPressed.put(Integer.valueOf(keyCode), true);
                    KeyMonitor.this.cancelPendingSingle(keyCode);
                    KeyMonitor.this.pendingSingleOnUp.remove(Integer.valueOf(keyCode));
                    String lp = (String) KeyMonitor.this.actions.get(keyCode + ":3");
                    if (lp != null) {
                        Log.i("CarMedia", "KeyMonitor long press: keyCode=" + keyCode + " action=" + lp);
                        KeyMonitor.this.callback.onKeyAction(lp, keyCode);
                    }
                }
            }
        };
        this.pendingLongPress.put(Integer.valueOf(keyCode), r);
        this.handler.postDelayed(r, LONG_PRESS_WINDOW_MS);
    }

    private void cancelLongPress(int keyCode) {
        Runnable r = this.pendingLongPress.remove(Integer.valueOf(keyCode));
        if (r != null) {
            this.handler.removeCallbacks(r);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelPendingSingle(int keyCode) {
        Runnable r = this.pendingSingleClick.remove(Integer.valueOf(keyCode));
        if (r != null) {
            this.handler.removeCallbacks(r);
        }
    }
}
