package com.ecarx.carmedia;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;

/* loaded from: classes3.dex */
public class ArtworkContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.ecarx.carmedia.artwork";
    private static final String TAG = "ArtworkProvider";

    public static Uri buildUri(String fileName) {
        return Uri.parse("content://com.ecarx.carmedia.artwork/" + fileName);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        String fileName = uri.getLastPathSegment();
        if (fileName != null && !fileName.contains("..") && !fileName.contains("/")) {
            File file = new File(getArtworkDir(getContext()), fileName);
            if (file.exists()) {
                return ParcelFileDescriptor.open(file, 268435456);
            }
            throw new FileNotFoundException("Artwork not found: " + fileName);
        }
        throw new FileNotFoundException("Invalid filename: " + fileName);
    }

    private File getArtworkDir(Context context) {
        return ArtworkHelper.getArtworkDir(context);
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "image/png";
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

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }
}
