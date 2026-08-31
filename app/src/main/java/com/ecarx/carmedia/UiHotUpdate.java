package com.ecarx.carmedia;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/* loaded from: classes3.dex */
public class UiHotUpdate {
    private static final String API_LATEST_COMMIT = "https://api.github.com/repos/q361955274/carmedia/commits?per_page=1";
    private static final String CONTENTS_API = "https://api.github.com/repos/q361955274/carmedia/contents/app/src/main/assets/ui.html?ref=main";
    private static final String FILE_PATH = "app/src/main/assets/ui.html";
    private static final String GITHUB_TOKEN = "";
    private static final String KEY_SHA = "ui_remote_sha";
    private static final String LOCAL_FILE = "ui.html";
    private static final String PREF = "carmedia_prefs";
    private static final String RAW_URL = "https://raw.githubusercontent.com/q361955274/carmedia/main/app/src/main/assets/ui.html";
    private static final String REPO_NAME = "carmedia";
    private static final String REPO_OWNER = "q361955274";
    private static final String TAG = "UiHotUpdate";

    public interface Callback {
        void onResult(boolean z, String str);
    }

    public static void checkAndUpdate(final Context ctx, final Callback cb) {
        new Thread(new Runnable() { // from class: com.ecarx.carmedia.UiHotUpdate.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    String remoteSha = UiHotUpdate.fetchLatestCommitSha();
                    if (remoteSha != null && !remoteSha.isEmpty()) {
                        String localSha = ctx.getSharedPreferences(UiHotUpdate.PREF, 0).getString(UiHotUpdate.KEY_SHA, "");
                        if (!remoteSha.equals(localSha)) {
                            byte[] data = UiHotUpdate.downloadViaContentsApi();
                            if (data == null || data.length < 1000) {
                                Log.w(UiHotUpdate.TAG, "contents api download failed or too small, fallback to raw");
                                data = UiHotUpdate.downloadRaw();
                            }
                            if (data != null && data.length >= 1000) {
                                File out = new File(ctx.getFilesDir(), UiHotUpdate.LOCAL_FILE);
                                FileOutputStream fos = new FileOutputStream(out);
                                fos.write(data);
                                fos.flush();
                                fos.close();
                                ctx.getSharedPreferences(UiHotUpdate.PREF, 0).edit().putString(UiHotUpdate.KEY_SHA, remoteSha).apply();
                                Log.i(UiHotUpdate.TAG, "ui.html updated, sha=" + remoteSha + ", bytes=" + data.length);
                                if (cb != null) {
                                    cb.onResult(true, "已更新 UI v" + data.length);
                                }
                                return;
                            }
                            if (cb != null) {
                                cb.onResult(false, "下载失败");
                                return;
                            }
                            return;
                        }
                        if (cb != null) {
                            cb.onResult(false, "已是最新");
                            return;
                        }
                        return;
                    }
                    if (cb != null) {
                        cb.onResult(false, "获取版本失败");
                    }
                } catch (Exception e) {
                    Log.w(UiHotUpdate.TAG, "checkAndUpdate error", e);
                    if (cb != null) {
                        cb.onResult(false, "检查失败");
                    }
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String fetchLatestCommitSha() throws Exception {
        HttpURLConnection conn = openConn(API_LATEST_COMMIT);
        try {
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code != 200) {
                return null;
            }
            InputStream in = conn.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
            }
            r.close();
            String json = sb.toString();
            int i = json.indexOf("\"sha\"");
            if (i < 0) {
                return null;
            }
            int i2 = json.indexOf(34, i + 6);
            int j = json.indexOf(34, i2 + 1);
            return json.substring(i2 + 1, j);
        } finally {
            conn.disconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] downloadViaContentsApi() throws Exception {
        HttpURLConnection conn = openConn(CONTENTS_API);
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            int code = conn.getResponseCode();
            if (code != 200) {
                Log.w(TAG, "contents api response code: " + code);
                return null;
            }
            InputStream in = conn.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
            }
            r.close();
            String json = sb.toString();
            int i = json.indexOf("\"content\"");
            if (i < 0) {
                return null;
            }
            int i2 = json.indexOf(34, i + 9);
            int j = json.indexOf(34, i2 + 1);
            if (i2 >= 0 && j >= 0) {
                String base64 = json.substring(i2 + 1, j);
                return Base64.decode(base64.replace("\\n", "").replace("\n", "").replace("\r", ""), 0);
            }
            return null;
        } finally {
            conn.disconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] downloadRaw() throws Exception {
        HttpURLConnection conn = openConn(RAW_URL);
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.raw");
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                while (true) {
                    int n = in.read(buf);
                    if (n == -1) {
                        in.close();
                        return bos.toByteArray();
                    }
                    bos.write(buf, 0, n);
                }
            } else {
                conn.disconnect();
                return null;
            }
        } finally {
            conn.disconnect();
        }
    }

    private static HttpURLConnection openConn(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);
        if (GITHUB_TOKEN != null && !GITHUB_TOKEN.isEmpty()) { conn.setRequestProperty("Authorization", "token " + GITHUB_TOKEN); }
        conn.setRequestProperty("User-Agent", "carmedia-hotupdate");
        return conn;
    }

    public static String resolveUiUrl(Context ctx) {
        File f = new File(ctx.getFilesDir(), LOCAL_FILE);
        if (f.exists() && f.length() > 1000) {
            return "file://" + f.getAbsolutePath();
        }
        return "file:///android_asset/ui.html";
    }
}
