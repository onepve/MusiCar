package com.ecarx.carmedia;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;

/* loaded from: classes3.dex */
public final class AppIconResolver {
    private static final int ICON_SIZE = 128;

    private AppIconResolver() {
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(ICON_SIZE, ICON_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
        drawable.draw(canvas);
        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0 || bitmap.isRecycled()) {
            Log.w("AppIconResolver", "drawableToBitmap: invalid bitmap");
            return null;
        }
        return bitmap;
    }

    public static String drawableToUri(Context context, Drawable drawable) {
        Uri uri;
        try {
            Bitmap bitmap = drawableToBitmap(drawable);
            if (bitmap != null && (uri = ArtworkHelper.saveArtwork(context, bitmap)) != null) {
                return uri.toString();
            }
        } catch (Throwable t) {
            Log.w("AppIconResolver", "drawableToUri failed", t);
        }
        return null;
    }

    public static String drawableToDataUri(Drawable drawable) {
        try {
            Bitmap bitmap = drawableToBitmap(drawable);
            if (bitmap == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
            byte[] bytes = baos.toByteArray();
            String base64 = Base64.encodeToString(bytes, 2);
            return "data:image/png;base64," + base64;
        } catch (Throwable t) {
            Log.w("AppIconResolver", "drawableToDataUri failed", t);
            return null;
        }
    }

    public static String resolve(Context context, String pkg, String fallback) {
        String uri = null;
        if (TextUtils.isEmpty(pkg)) {
            return fallback;
        }
        try {
            PackageManager pm = context.getPackageManager();
            Drawable drawable = pm.getApplicationIcon(pkg);
            uri = drawableToUri(context, drawable);
        } catch (Throwable t) {
            Log.w("AppIconResolver", "resolve failed for " + pkg, t);
        }
        if (uri != null) {
            return uri;
        }
        return fallback;
    }
}
