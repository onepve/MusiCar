package com.ecarx.carmedia;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;

/* loaded from: classes3.dex */
public class ArtworkHelper {
    static final String APP_ICON_FILENAME = "app_icon.png";
    private static final String ARTWORK_DIR_NAME = "artwork";
    private static final int MAX_CACHED_FILES = 10;
    private static final int MAX_HEIGHT = 512;
    private static final int MAX_WIDTH = 512;
    private static final String TAG = "ArtworkHelper";

    private ArtworkHelper() {
    }

    public static File getArtworkDir(Context context) {
        File dir = new File(context.getFilesDir(), ARTWORK_DIR_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static Uri saveArtwork(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        FileOutputStream fos = null;
        try {
            try {
                Bitmap scaled = scaleDownBitmap(bitmap);
                File dir = getArtworkDir(context);
                String hash = generateBitmapHash(scaled);
                String fileName = "artwork_" + hash + ".png";
                File file = new File(dir, fileName);
                if (!file.exists()) {
                    fos = new FileOutputStream(file);
                    scaled.compress(Bitmap.CompressFormat.PNG, 90, fos);
                }
                cleanupOldArtworks(dir, fileName);
                if (scaled != bitmap) {
                    scaled.recycle();
                }
                Uri buildUri = ArtworkContentProvider.buildUri(fileName);
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                    }
                }
                return buildUri;
            } catch (IOException e2) {
                Log.e(TAG, "Failed to save artwork", e2);
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e3) {
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    public static Bitmap loadBitmapFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();
        if (scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            Log.w(TAG, "Rejecting HTTP URL in loadBitmapFromUri — pass URL to widget directly");
            return null;
        }
        InputStream is = null;
        try {
            try {
                try {
                    ContentResolver resolver = context.getContentResolver();
                    is = resolver.openInputStream(uri);
                    if (is == null) {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                            }
                        }
                        return null;
                    }
                    Bitmap decodeStream = BitmapFactory.decodeStream(is);
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e2) {
                        }
                    }
                    return decodeStream;
                } catch (IOException e3) {
                    Log.e(TAG, "Failed to load bitmap from URI: " + uri, e3);
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e4) {
                        }
                    }
                    return null;
                }
            } catch (SecurityException e5) {
                Log.e(TAG, "No permission to access URI: " + uri, e5);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e6) {
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e7) {
                }
            }
            throw th;
        }
    }

    private static String generateBitmapHash(Bitmap bitmap) {
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            StringBuilder sb = new StringBuilder();
            sb.append(w).append("x").append(h);
            int[] xs = {0, w / 2, w - 1, 0, w / 2, w - 1, 0, w / 2, w - 1};
            int[] ys = {0, 0, 0, h / 2, h / 2, h / 2, h - 1, h - 1, h - 1};
            int xMax = Math.max(0, w - 1);
            int yMax = Math.max(0, h - 1);
            for (int i = 0; i < 9; i++) {
                int px = Math.min(xs[i], xMax);
                int py = Math.min(ys[i], yMax);
                sb.append("_").append(Integer.toHexString(bitmap.getPixel(px, py)));
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sb.toString().getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                String s = Integer.toHexString(b & 255);
                if (s.length() == 1) {
                    hex.append('0');
                }
                hex.append(s);
            }
            return hex.substring(0, 12);
        } catch (Exception e2) {
            return String.valueOf(System.currentTimeMillis());
        }
    }

    private static Bitmap scaleDownBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if (w <= 512 && h <= 512) {
            return bitmap;
        }
        float scale = Math.min(512.0f / w, 512.0f / h);
        int newW = Math.round(w * scale);
        int newH = Math.round(h * scale);
        try {
            return Bitmap.createScaledBitmap(bitmap, newW, newH, true);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OOM while scaling bitmap, using original", e);
            return bitmap;
        }
    }

    private static void cleanupOldArtworks(File dir, String currentFileName) {
        try {
            File[] files = dir.listFiles();
            if (files != null && files.length > MAX_CACHED_FILES) {
                Arrays.sort(files, new Comparator<File>() { // from class: com.ecarx.carmedia.ArtworkHelper.1
                    @Override // java.util.Comparator
                    public int compare(File a, File b) {
                        return Long.compare(a.lastModified(), b.lastModified());
                    }
                });
                int toDelete = files.length - MAX_CACHED_FILES;
                for (int i = 0; i < toDelete; i++) {
                    String name = files[i].getName();
                    if (!name.equals(currentFileName) && !name.equals(APP_ICON_FILENAME)) {
                        files[i].delete();
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to cleanup old artworks", e);
        }
    }
}
