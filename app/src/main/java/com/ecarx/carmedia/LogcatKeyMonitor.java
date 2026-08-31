package com.ecarx.carmedia;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes3.dex */
public class LogcatKeyMonitor {
    private static final int KEY_OK = 306;
    private static final long MIN_TRIGGER_INTERVAL_MS = 80;
    private static final String TAG = "CarMedia";
    private final Map<Integer, Long> lastTriggerAt = new HashMap();
    private final Listener listener;
    private Process process;
    private volatile boolean running;
    private Thread thread;
    private static final Pattern WHEEL_KEY_PATTERN = Pattern.compile("IMS\\s+reportKeyToAdaptApi\\s*:\\s*(\\d+)\\s+(press|release)", 2);
    private static final Pattern OK_KEY_PATTERN = Pattern.compile("shouldCallback:\\s*code\\s*=\\s*(\\d+)\\s+action\\s*=\\s*1", 2);
    private static final Pattern OK_KEY_RELEASE_PATTERN = Pattern.compile("shouldCallback:\\s*code\\s*=\\s*(\\d+)\\s+action\\s*=\\s*0", 2);
    private static final Pattern MEDIA_KEY_ROUTE_PATTERN = Pattern.compile("Sending\\s+KeyEvent\\s*\\{.*?action=ACTION_DOWN.*?keyCode=(KEYCODE_MEDIA_NEXT|KEYCODE_MEDIA_PREVIOUS).*?\\}\\s*to\\s+([\\w.]+)/", 34);

    public interface Listener {
        void onKeyDown(int i);

        void onKeyUp(int i);

        void onMediaButtonRouted(int i, String str);
    }

    public LogcatKeyMonitor(Listener listener) {
        this.listener = listener;
    }

    public synchronized void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.thread = new Thread(new Runnable() { // from class: com.ecarx.carmedia.LogcatKeyMonitor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LogcatKeyMonitor.this.runLoop();
            }
        }, "LogcatKeyMonitor");
        this.thread.setDaemon(true);
        this.thread.start();
        Log.i("CarMedia", "LogcatKeyMonitor started (all wheel keys via logcat)");
    }

    public synchronized void stop() {
        this.running = false;
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        if (this.process != null) {
            try {
                this.process.destroy();
            } catch (Throwable th) {
            }
            this.process = null;
        }
        Log.i("CarMedia", "LogcatKeyMonitor stopped");
    }

    public boolean isRunning() {
        return this.running;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runLoop() {
        String line;
        int keyCode;
        while (this.running) {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"logcat", "-b", "all", "-T", "1", "-s", "InputManager:V", "CAR.INPUT:V", "MediaSessionService:V"});
                synchronized (this) {
                    this.process = p;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while (this.running && (line = br.readLine()) != null) {
                    Matcher rm = MEDIA_KEY_ROUTE_PATTERN.matcher(line);
                    if (rm.find()) {
                        String kc = rm.group(1);
                        String pkg = rm.group(2);
                        if ("KEYCODE_MEDIA_NEXT".equalsIgnoreCase(kc)) {
                            keyCode = 87;
                        } else {
                            keyCode = 88;
                        }
                        Log.i("CarMedia", "Media key routed: " + kc + " -> " + pkg);
                        if (this.listener != null) {
                            try {
                                this.listener.onMediaButtonRouted(keyCode, pkg);
                            } catch (Throwable t) {
                                Log.w("CarMedia", "onMediaButtonRouted error: " + t.getMessage());
                            }
                        }
                    } else {
                        int keyDown = parseKeyDown(line);
                        if (keyDown > 0) {
                            onKeyDownLine(keyDown, line);
                        } else {
                            int keyUp = parseKeyUp(line);
                            if (keyUp > 0) {
                                onKeyUpLine(keyUp, line);
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    return;
                }
            } catch (Throwable t2) {
                Log.w("CarMedia", "LogcatKeyMonitor loop error: " + t2.getMessage());
                if (this.running) {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e2) {
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    private int parseKeyDown(String line) {
        Matcher m = WHEEL_KEY_PATTERN.matcher(line);
        if (m.find()) {
            if ("press".equalsIgnoreCase(m.group(2))) {
                try {
                    return Integer.parseInt(m.group(1));
                } catch (NumberFormatException e) {
                }
            }
            return 0;
        }
        Matcher m2 = OK_KEY_PATTERN.matcher(line);
        if (m2.find()) {
            try {
                int code = Integer.parseInt(m2.group(1));
                if (code == KEY_OK) {
                    return code;
                }
            } catch (NumberFormatException e2) {
            }
        }
        return 0;
    }

    private int parseKeyUp(String line) {
        Matcher m = WHEEL_KEY_PATTERN.matcher(line);
        if (m.find()) {
            if ("release".equalsIgnoreCase(m.group(2))) {
                try {
                    return Integer.parseInt(m.group(1));
                } catch (NumberFormatException e) {
                }
            }
            return 0;
        }
        Matcher m2 = OK_KEY_RELEASE_PATTERN.matcher(line);
        if (m2.find()) {
            try {
                int code = Integer.parseInt(m2.group(1));
                if (code == KEY_OK) {
                    return code;
                }
            } catch (NumberFormatException e2) {
            }
        }
        return 0;
    }

    private void onKeyDownLine(int keyCode, String line) {
        long now = System.currentTimeMillis();
        Long last = this.lastTriggerAt.get(Integer.valueOf(keyCode));
        if (last != null && now - last.longValue() < MIN_TRIGGER_INTERVAL_MS) {
            return;
        }
        this.lastTriggerAt.put(Integer.valueOf(keyCode), Long.valueOf(now));
        Log.i("CarMedia", "Wheel key DOWN " + keyCode + " from logcat: " + line.trim());
        if (this.listener != null) {
            try {
                this.listener.onKeyDown(keyCode);
            } catch (Throwable t) {
                Log.w("CarMedia", "onKeyDown error: " + t.getMessage());
            }
        }
    }

    private void onKeyUpLine(int keyCode, String line) {
        Log.i("CarMedia", "Wheel key UP " + keyCode + " from logcat: " + line.trim());
        if (this.listener != null) {
            try {
                this.listener.onKeyUp(keyCode);
            } catch (Throwable t) {
                Log.w("CarMedia", "onKeyUp error: " + t.getMessage());
            }
        }
    }
}
