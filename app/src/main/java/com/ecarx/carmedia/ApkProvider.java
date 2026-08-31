package com.ecarx.carmedia;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;

/* loaded from: classes3.dex */
public class ApkProvider extends ContentProvider {
    public static final String APK_NAME = "carmedia_update.apk";
    public static final String AUTHORITY = "com.ecarx.carmedia.apk";
    private static final String TAG = "ApkProvider";

    public static Uri buildApkUri() {
        return Uri.parse("content://com.ecarx.carmedia.apk/carmedia_update.apk");
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        String name = uri.getLastPathSegment();
        if (name == null || !name.equals(APK_NAME) || name.contains("..") || name.contains("/")) {
            throw new FileNotFoundException("Invalid filename: " + name);
        }
        File f = new File(getContext().getCacheDir(), APK_NAME);
        if (!f.exists()) {
            Log.w(TAG, "apk not found: " + f.getAbsolutePath());
            throw new FileNotFoundException("apk not found");
        }
        return ParcelFileDescriptor.open(f, 268435456);
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "application/vnd.android.package-archive";
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
