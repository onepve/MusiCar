package com.ecarx.carmedia;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import java.lang.reflect.Method;

/* loaded from: classes3.dex */
public final class NotificationPermissionHelper {
    private static final String TAG = "CarMedia_NL";

    private NotificationPermissionHelper() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v10, types: [boolean] */
    public static boolean grantNotificationListenerPermission(Context context) {
        String flatten = new ComponentName(context, (Class<?>) MediaNotificationListener.class).flattenToString();
        String str = TAG;
        Log.i(TAG, "Component: " + flatten);
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService("notification");
            if (nm == null) {
                Log.e(TAG, "❌ NotificationManager is null!");
                return fallbackGrant(context, flatten);
            }
            try {
                try {
                    try {
                        Method m = NotificationManager.class.getMethod("setNotificationListenerAccessGranted", ComponentName.class, Boolean.TYPE);
                        m.invoke(nm, new ComponentName(context, (Class<?>) MediaNotificationListener.class), Boolean.TRUE);
                        Log.i(TAG, "✅ NotificationListener enabled via setNotificationListenerAccessGranted");
                        if (isNotificationListenerEnabled(context)) {
                            return true;
                        }
                        Log.w(TAG, "reflection reported success but settings not updated, trying fallback");
                        return fallbackGrant(context, flatten);
                    } catch (SecurityException e) {
                        Log.e(TAG, "SecurityException! App is NOT signed with platform key / Cannot call hidden system API");
                        return fallbackGrant(context, flatten);
                    }
                } catch (NoSuchMethodException e2) {
                    Log.e(TAG, "Method not found! setNotificationListenerAccessGranted does not exist");
                    return fallbackGrant(context, flatten);
                }
            } catch (Exception e3) {
                Log.e(TAG, "Failed to grant permission: " + e3.getClass().getSimpleName());
                return fallbackGrant(context, flatten);
            }
        } catch (Exception e4) {
            Log.e(str, "grantNotificationListenerPermission outer error: " + e4.getMessage());
            return fallbackGrant(context, flatten);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x00e8 A[Catch: Exception -> 0x00f4, TryCatch #1 {Exception -> 0x00f4, blocks: (B:17:0x00aa, B:19:0x00b6, B:22:0x00bd, B:25:0x00de, B:27:0x00e8, B:29:0x00ee, B:31:0x00c5), top: B:16:0x00aa }] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00ee A[Catch: Exception -> 0x00f4, TRY_LEAVE, TryCatch #1 {Exception -> 0x00f4, blocks: (B:17:0x00aa, B:19:0x00b6, B:22:0x00bd, B:25:0x00de, B:27:0x00e8, B:29:0x00ee, B:31:0x00c5), top: B:16:0x00aa }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean fallbackGrant(android.content.Context r9, java.lang.String r10) {
        /*
            Method dump skipped, instructions count: 272
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.NotificationPermissionHelper.fallbackGrant(android.content.Context, java.lang.String):boolean");
    }

    public static boolean isNotificationListenerEnabled(Context context) {
        String flatten = new ComponentName(context, (Class<?>) MediaNotificationListener.class).flattenToString();
        String v = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if (v == null || !v.contains(flatten)) {
            Log.w(TAG, "⚠ NotificationListener permission is NOT enabled");
            return false;
        }
        Log.i(TAG, "✓ NotificationListener permission is ENABLED");
        return true;
    }

    public static boolean revokeNotificationListenerPermission(Context context) {
        Log.i(TAG, "Revoking NotificationListener permission...");
        String flatten = new ComponentName(context, (Class<?>) MediaNotificationListener.class).flattenToString();
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService("notification");
            if (nm == null) {
                Log.e(TAG, "❌ NotificationManager is null!");
                return false;
            }
            try {
                Method m = NotificationManager.class.getMethod("setNotificationListenerAccessGranted", ComponentName.class, Boolean.TYPE);
                m.invoke(nm, new ComponentName(context, (Class<?>) MediaNotificationListener.class), Boolean.FALSE);
                Log.i(TAG, "✅ Revoked via setNotificationListenerAccessGranted");
                return true;
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "Method not found!");
                return fallbackRevoke(context, flatten);
            } catch (SecurityException e2) {
                Log.e(TAG, "SecurityException!");
                return fallbackRevoke(context, flatten);
            } catch (Exception e3) {
                Log.e(TAG, "Revoke failed: " + e3.getMessage());
                return fallbackRevoke(context, flatten);
            }
        } catch (Exception e4) {
            Log.e(TAG, "revoke outer error: " + e4.getMessage());
            return fallbackRevoke(context, flatten);
        }
    }

    private static boolean fallbackRevoke(Context context, String flatten) {
        try {
            String v = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
            if (v != null && !v.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (String part : v.split(":")) {
                    if (!part.isEmpty() && !part.equals(flatten)) {
                        if (!first) {
                            sb.append(':');
                        }
                        sb.append(part);
                        first = false;
                    }
                }
                Settings.Secure.putString(context.getContentResolver(), "enabled_notification_listeners", sb.toString());
                Log.i(TAG, "✅ Revoked via Settings.Secure removal");
                return true;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "❌ Revoke fallback exception: " + e.getMessage());
            return false;
        }
    }
}
