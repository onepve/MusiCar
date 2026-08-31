package com.ecarx.carmedia;

import android.content.ComponentName;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/* loaded from: classes3.dex */
public class MediaNotificationListener extends NotificationListenerService {
    public static volatile ComponentName COMPONENT_NAME = null;
    private static final String TAG = "CarMedia_NL";
    private static volatile MediaNotificationListener sInstance;

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        try {
            MediaCenterBridge.get().init(this);
            Log.i(TAG, "NL onCreate: MediaCenterBridge initialized");
        } catch (Throwable t) {
            Log.w(TAG, "NL onCreate: MediaCenterBridge init failed: " + t.getMessage());
        }
        try {
            boolean ok = NotificationPermissionHelper.grantNotificationListenerPermission(this);
            Log.i(TAG, "NL onCreate: grant result=" + ok);
        } catch (Throwable t2) {
            Log.w(TAG, "NL onCreate: grant failed: " + t2.getMessage());
        }
        COMPONENT_NAME = new ComponentName(this, getClass());
        ensureMainServiceAlive("NL onCreate");
    }

    @Override // android.service.notification.NotificationListenerService, android.app.Service
    public void onDestroy() {
        super.onDestroy();
        sInstance = null;
        COMPONENT_NAME = null;
    }

    @Override // android.service.notification.NotificationListenerService
    public void onListenerConnected() {
        super.onListenerConnected();
        sInstance = this;
        Log.i(TAG, "NotificationListener connected");
        COMPONENT_NAME = new ComponentName(this, getClass());
        ensureMainServiceAlive("NL connected");
    }

    @Override // android.service.notification.NotificationListenerService
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.i(TAG, "NotificationListener disconnected");
        COMPONENT_NAME = null;
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRemoved(StatusBarNotification sbn) {
        CarMediaService svc;
        super.onNotificationRemoved(sbn);
        if (sbn == null) {
            return;
        }
        try {
            if (sbn.getNotification() == null) {
                return;
            }
            String pkg = sbn.getPackageName();
            if (!CarMediaService.PKG_SELF.equals(pkg) && (svc = CarMediaService.sInstance) != null) {
                svc.onNotificationRemoved(pkg);
            }
        } catch (Throwable t) {
            Log.w(TAG, "onNotificationRemoved error: " + t.getMessage());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:98:0x01f4 A[Catch: all -> 0x025f, TRY_ENTER, TryCatch #3 {all -> 0x025f, blocks: (B:76:0x01ac, B:78:0x01b9, B:83:0x01c6, B:86:0x01d2, B:93:0x01e6, B:95:0x01ec, B:98:0x01f4, B:99:0x0220, B:100:0x0251), top: B:75:0x01ac }] */
    /* JADX WARN: Removed duplicated region for block: B:99:0x0220 A[Catch: all -> 0x025f, TryCatch #3 {all -> 0x025f, blocks: (B:76:0x01ac, B:78:0x01b9, B:83:0x01c6, B:86:0x01d2, B:93:0x01e6, B:95:0x01ec, B:98:0x01f4, B:99:0x0220, B:100:0x0251), top: B:75:0x01ac }] */
    @Override // android.service.notification.NotificationListenerService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onNotificationPosted(android.service.notification.StatusBarNotification r20) {
        /*
            Method dump skipped, instructions count: 645
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.MediaNotificationListener.onNotificationPosted(android.service.notification.StatusBarNotification):void");
    }

    public static void requestActiveNotifications(String targetPkg) {
        try {
            MediaNotificationListener instance = getCurrentInstance();
            if (instance == null) {
                Log.w(TAG, "requestActiveNotifications: no instance");
                return;
            }
            StatusBarNotification[] all = instance.getActiveNotifications();
            if (all != null && all.length != 0) {
                Log.d(TAG, "requestActiveNotifications: total=" + all.length + " target=" + targetPkg);
                for (StatusBarNotification sbn : all) {
                    if (sbn != null && sbn.getNotification() != null) {
                        String pkg = sbn.getPackageName();
                        if (!CarMediaService.PKG_SELF.equals(pkg) && (targetPkg == null || targetPkg.equals(pkg))) {
                            instance.onNotificationPosted(sbn);
                            Log.d(TAG, "requestActiveNotifications: dispatched " + pkg);
                        }
                    }
                }
                return;
            }
            Log.d(TAG, "requestActiveNotifications: no active notifications");
        } catch (Throwable t) {
            Log.w(TAG, "requestActiveNotifications error: " + t.getMessage());
        }
    }

    private static MediaNotificationListener getCurrentInstance() {
        return sInstance;
    }

    private void ensureMainServiceAlive(String reason) {
        try {
            if (CarMediaService.sInstance == null) {
                Intent svc = new Intent(this, (Class<?>) CarMediaService.class);
                startForegroundService(svc);
                Log.i(TAG, "ensureMainServiceAlive: restarting CarMediaService (reason=" + reason + ")");
            }
        } catch (Throwable t) {
            Log.w(TAG, "ensureMainServiceAlive failed (reason=" + reason + "): " + t.getMessage());
        }
    }
}
