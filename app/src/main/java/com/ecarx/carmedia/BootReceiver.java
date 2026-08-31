package com.ecarx.carmedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* loaded from: classes3.dex */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "CarMedia";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        Log.i("CarMedia", "BootReceiver action=" + action);
        if ("android.intent.action.BOOT_COMPLETED".equals(action) || "android.intent.action.LOCKED_BOOT_COMPLETED".equals(action)) {
            try {
                Intent service = new Intent(context, (Class<?>) CarMediaService.class);
                context.startForegroundService(service);
            } catch (Throwable t) {
                Log.w("CarMedia", "BootReceiver startForegroundService failed: " + t.getMessage());
            }
        }
    }
}
