package com.ecarx.carmedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.ecarx.carmedia.CarMediaService;
import com.ecarx.carmedia.KeyMonitor;
import com.ecarx.carmedia.UiHotUpdate;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class MainActivity extends Activity {
    private static final String APP_VERSION = "1.3.4";
    private static final String BG_CUSTOM = "custom";
    private static final String BG_DARK = "dark";
    private static final String BG_LIGHT = "light";
    private static final String PREFS = "carmedia_prefs";
    private static final String PREF_BG_MODE = "bg_mode";
    private static final String PREF_BG_SRC = "bg_src";
    private static final String TAG = "CarMedia";
    private static final String UPDATE_URL = "https://jqt.czrui.cn/CarMedia.txt";
    private boolean mBound;
    private ServiceConnection mConnection;
    private Object mDayNightMode;
    private Object mNightCbProxy;
    private boolean mNightRegistered;
    private boolean mNightState;
    private CarMediaService mService;
    private String pendingUpdateDialog;
    private boolean recording;
    private Runnable uiPollRunnable;
    private WebView webView;
    private boolean webViewReady;
    private static boolean sCheckedInProcess = false;
    private static volatile String sCachedRemoteVer = null;
    private static volatile String sCachedNotes = null;
    private static volatile String sCachedRemoteUrl = null;
    private static volatile String sCachedApkMd5 = null;
    private String bgMode = BG_DARK;
    private final Handler uiHandler = new Handler();

    private interface ProgressCallback {
        void onProgress(int i);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        hideSystemBars();
        this.webView = new WebView(this);
        WebSettings s = this.webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setAllowFileAccess(true);
        s.setDomStorageEnabled(true);
        s.setAllowContentAccess(true);
        this.webView.setBackgroundColor(-16250093);
        this.webView.setOverScrollMode(2);
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setHorizontalScrollBarEnabled(false);
        this.webView.addJavascriptInterface(new JsBridge(), "CarMediaBridge");
        this.webView.setWebViewClient(new WebViewClient() { // from class: com.ecarx.carmedia.MainActivity.1
            @Override // android.webkit.WebViewClient
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri != null && "content".equals(uri.getScheme()) && ArtworkContentProvider.AUTHORITY.equals(uri.getAuthority())) {
                    try {
                        InputStream is = MainActivity.this.getContentResolver().openInputStream(uri);
                        if (is != null) {
                            return new WebResourceResponse("image/png", "UTF-8", is);
                        }
                    } catch (Throwable t) {
                        Log.w("CarMedia", "intercept artwork failed: " + uri + " - " + t.getMessage());
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView view, String url) {
                MainActivity.this.webViewReady = true;
                MainActivity.this.pushAll();
                if (MainActivity.this.pendingUpdateDialog != null) {
                    String js = MainActivity.this.pendingUpdateDialog;
                    MainActivity.this.pendingUpdateDialog = null;
                    Log.i("CarMedia", "runJs: flush pending update dialog after page finished");
                    MainActivity.this.runJs(js);
                }
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient() { // from class: com.ecarx.carmedia.MainActivity.2
            @Override // android.webkit.WebChromeClient
            public boolean onConsoleMessage(ConsoleMessage cm) {
                String msg = cm.message();
                if (msg != null && !msg.isEmpty()) {
                    if (msg.startsWith("VP:")) {
                        Log.w("CarMedia_VP", msg);
                        return true;
                    }
                    Log.w("CarMedia_JS", cm.sourceId() + ":" + cm.lineNumber() + " " + msg);
                    return true;
                }
                return true;
            }
        });
        setContentView(this.webView);
        this.webView.loadUrl(UiHotUpdate.resolveUiUrl(this));
        hideSystemBars();
        this.bgMode = readBgMode();
        checkForUpdateOnce();
        UiHotUpdate.checkAndUpdate(this, new AnonymousClass3());
        this.mConnection = new AnonymousClass4();
        bindService(new Intent(this, (Class<?>) CarMediaService.class), this.mConnection, 1);
        this.uiPollRunnable = new Runnable() { // from class: com.ecarx.carmedia.MainActivity.5
            @Override // java.lang.Runnable
            public void run() {
                MainActivity.this.pushAll();
                MainActivity.this.scheduleUiPoll(this);
            }
        };
        this.uiHandler.postDelayed(this.uiPollRunnable, 1000L);
    }

    /* renamed from: com.ecarx.carmedia.MainActivity$3, reason: invalid class name */
    class AnonymousClass3 implements UiHotUpdate.Callback {
        AnonymousClass3() {
        }

        @Override // com.ecarx.carmedia.UiHotUpdate.Callback
        public void onResult(boolean updated, String msg) {
            if (updated) {
                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        MainActivity.this.webView.evaluateJavascript("if(document.getElementById('noticeMask').classList.contains('open')){localStorage.removeItem('carmedia_notice_ver');}", new ValueCallback<String>() { // from class: com.ecarx.carmedia.MainActivity.3.1.1
                            @Override // android.webkit.ValueCallback
                            public void onReceiveValue(String value) {
                                MainActivity.this.webView.loadUrl(UiHotUpdate.resolveUiUrl(MainActivity.this));
                                MainActivity.this.webViewReady = false;
                                Log.i("CarMedia", "hot update: ui.html refreshed");
                            }
                        });
                    }
                });
            }
        }
    }

    /* renamed from: com.ecarx.carmedia.MainActivity$4, reason: invalid class name */
    class AnonymousClass4 implements ServiceConnection {
        AnonymousClass4() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("CarMedia", ">>> MainActivity: CarMediaService connected");
            MainActivity.this.mService = ((CarMediaService.LocalBinder) service).getService();
            MainActivity.this.mBound = true;
            MainActivity.this.mService.setAuthMessageListener(new CarMediaService.AuthMessageListener() { // from class: com.ecarx.carmedia.MainActivity.4.1
                @Override // com.ecarx.carmedia.CarMediaService.AuthMessageListener
                public void onAuthMessage(final String msg) {
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.4.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.showToast(msg);
                        }
                    });
                }
            });
            MainActivity.this.mService.refreshControllerList();
            MainActivity.this.pushAll();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            MainActivity.this.mService = null;
            MainActivity.this.mBound = false;
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        pushAll();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.uiHandler.removeCallbacksAndMessages(null);
        unregisterNightCallback();
        if (this.mBound && this.mConnection != null) {
            unbindService(this.mConnection);
            this.mBound = false;
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideSystemBars();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.recording) {
            cancelRecording();
        } else {
            moveTaskToBack(true);
        }
    }

    private void hideSystemBars() {
        try {
            getWindow().addFlags(1024);
            getWindow().getDecorView().setSystemUiVisibility(4356);
        } catch (Throwable th) {
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemBars();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runJs(final String js) {
        if (this.webView == null) {
            return;
        }
        if (!this.webViewReady && js.startsWith("CarMediaUI.showUpdate")) {
            this.pendingUpdateDialog = js;
            Log.i("CarMedia", "runJs: update dialog pending (webView not ready)");
        } else {
            runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.6
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        MainActivity.this.webView.evaluateJavascript(js, null);
                    } catch (Throwable t) {
                        Log.w("CarMedia", "runJs failed: " + t.getMessage());
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(14:138|139|140|141|142|(3:154|155|(7:159|145|146|147|148|150|151))|144|145|146|147|148|150|151|136) */
    /* JADX WARN: Can't wrap try/catch for region: R(9:(3:107|108|109)|(3:120|121|(7:125|112|113|114|115|116|117))|111|112|113|114|115|116|117) */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void pushAll() {
        /*
            Method dump skipped, instructions count: 875
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.MainActivity.pushAll():void");
    }

    private String customBgPath() {
        try {
            File f = new File(getFilesDir(), "custom_bg.jpg");
            if (f.exists()) {
                String src = getSharedPreferences(PREFS, 0).getString(PREF_BG_SRC, "");
                String base = Uri.fromFile(f).toString();
                if (!src.isEmpty()) {
                    return base + "?src=" + src;
                }
                return base;
            }
        } catch (Throwable th) {
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBgSrc(String src) {
        try {
            getSharedPreferences(PREFS, 0).edit().putString(PREF_BG_SRC, src == null ? "" : src).apply();
        } catch (Throwable th) {
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private String clockNow() {
        try {
            return new SimpleDateFormat("HH:mm MM/dd", Locale.getDefault()).format(new Date());
        } catch (Throwable th) {
            return "--:--";
        }
    }

    private class JsBridge {
        private JsBridge() {
        }

        @JavascriptInterface
        public void onToggleDim() {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.1
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.mService != null) {
                        MainActivity.this.mService.setDimAutoPush(!MainActivity.this.mService.isDimAutoPush());
                        MainActivity.this.pushAll();
                    }
                }
            });
        }

        @JavascriptInterface
        public void onToggleKeyBlock() {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.2
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.mService != null) {
                        MainActivity.this.mService.setWheelKeysBlocked(!MainActivity.this.mService.isKeyBlockedSetting());
                        MainActivity.this.pushAll();
                    }
                }
            });
        }

        @JavascriptInterface
        public void onMediaAction(final String action) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.3
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.mService != null) {
                        MainActivity.this.mService.sendMediaAction(action);
                    }
                }
            });
        }

        @JavascriptInterface
        public void onBrowseModeChange(final String mode) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.4
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.BG_CUSTOM.equals(mode)) {
                        MainActivity.this.pickCustomWallpaper();
                        return;
                    }
                    MainActivity.this.bgMode = mode;
                    MainActivity.this.saveBgMode(mode);
                    MainActivity.this.pushAll();
                }
            });
        }

        @JavascriptInterface
        public void onPickWallpaper(final String file) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.5
                @Override // java.lang.Runnable
                public void run() {
                    if (file == null || file.isEmpty()) {
                        MainActivity.this.pushAll();
                        MainActivity.this.runJs("CarMediaUI.showWallpaperSheet();");
                        return;
                    }
                    try {
                        MainActivity.this.copyAssetToFiles(file);
                        MainActivity.this.setBgSrc(file);
                        MainActivity.this.bgMode = MainActivity.BG_LIGHT;
                        MainActivity.this.saveBgMode(MainActivity.BG_LIGHT);
                        MainActivity.this.pushAll();
                    } catch (Throwable t) {
                        Log.w("CarMedia", "apply light wallpaper failed: " + t.getMessage());
                    }
                }
            });
        }

        @JavascriptInterface
        public void onSetKeyAction(final String action, final int code) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.6
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.mService != null) {
                        MainActivity.this.mService.setKeyAction(code, 1, action);
                    }
                    MainActivity.this.pushAll();
                }
            });
        }

        @JavascriptInterface
        public void onRemoveKeyAction(final int code, final int tap) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.7
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.mService != null) {
                        MainActivity.this.mService.removeKeyAction(code, tap);
                    }
                    MainActivity.this.pushAll();
                }
            });
        }

        @JavascriptInterface
        public void onRecordKey(final String action, final int tap) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.8
                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.this.startRecording(action, tap);
                }
            });
        }

        @JavascriptInterface
        public void onCancelRecord() {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.9
                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.this.cancelRecording();
                }
            });
        }

        @JavascriptInterface
        public String onRequestAppList() {
            if (MainActivity.this.mService == null) {
                return "[]";
            }
            try {
                List<CarMediaService.AppInfo> apps = MainActivity.this.mService.getLaunchableApps();
                JSONArray arr = new JSONArray();
                for (CarMediaService.AppInfo app : apps) {
                    JSONObject obj = new JSONObject();
                    obj.put("pkg", app.packageName);
                    obj.put("name", app.appName);
                    if (app.iconUri != null) {
                        obj.put("icon", app.iconUri);
                    }
                    arr.put(obj);
                }
                return arr.toString();
            } catch (Throwable t) {
                Log.e("CarMedia", "onRequestAppList failed: " + t.getMessage());
                return "[]";
            }
        }

        @JavascriptInterface
        public void onLaunchApp(final String pkg) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.10
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.mService != null) {
                        MainActivity.this.mService.launchAppByPackage(pkg);
                    }
                }
            });
        }

        @JavascriptInterface
        public void onToggleLyricBlock(final String pkg) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.11
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.mService != null && pkg != null && !pkg.isEmpty()) {
                        MainActivity.this.mService.setLyricBlocked(pkg, !MainActivity.this.mService.isLyricBlocked(pkg));
                    }
                    MainActivity.this.pushAll();
                }
            });
        }

        @JavascriptInterface
        public void onSelectController() {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.12
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.mService != null) {
                        MainActivity.this.mService.refreshControllerList();
                    }
                    MainActivity.this.pushAll();
                    MainActivity.this.runJs("CarMediaUI.showControllerSheet();");
                }
            });
        }

        @JavascriptInterface
        public void onSelectControllerIndex(final int index) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.13
                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.this.confirmControllerSelection(index);
                    MainActivity.this.pushAll();
                }
            });
        }

        @JavascriptInterface
        public void onExit() {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.14
                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.this.moveTaskToBack(true);
                }
            });
        }

        @JavascriptInterface
        public void onRegisterNightCallback() {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.15
                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.this.registerNightCallback();
                }
            });
        }

        @JavascriptInterface
        public void onOpenUpdate() {
            final String apkUrl = MainActivity.sCachedRemoteUrl;
            if (apkUrl != null && !apkUrl.isEmpty()) {
                MainActivity.this.showToast("正在下载更新包...");
                new Thread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.17
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            byte[] data = MainActivity.this.httpGetBytes(apkUrl, new ProgressCallback() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.17.1
                                @Override // com.ecarx.carmedia.MainActivity.ProgressCallback
                                public void onProgress(int percent) {
                                    MainActivity.this.runJs("CarMediaUI.updateDownloadProgress(" + percent + ");");
                                }
                            });
                            if (data != null && data.length >= 10000) {
                                String md5 = MainActivity.sCachedApkMd5;
                                if (md5 != null && !md5.isEmpty()) {
                                    String got = MainActivity.md5Hex(data);
                                    if (!md5.equalsIgnoreCase(got)) {
                                        Log.w("CarMedia", "apk md5 mismatch: expect=" + md5 + " got=" + got);
                                        MainActivity.this.runJs("CarMediaUI.updateDownloadFailed();");
                                        MainActivity.this.showToast("更新包校验失败");
                                        return;
                                    }
                                }
                                Log.i("CarMedia", "apk downloaded bytes=" + data.length);
                                MainActivity.this.runJs("CarMediaUI.updateDownloadDone();");
                                MainActivity.this.installApkBytes(data);
                                return;
                            }
                            MainActivity.this.runJs("CarMediaUI.updateDownloadFailed();");
                            MainActivity.this.showToast("下载失败，请重试");
                        } catch (Throwable t) {
                            Log.w("CarMedia", "download apk failed: " + t.getMessage());
                            MainActivity.this.runJs("CarMediaUI.updateDownloadFailed();");
                            MainActivity.this.showToast("下载失败，请重试");
                        }
                    }
                }, "update-download").start();
            } else {
                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.16
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(MainActivity.UPDATE_URL)).addFlags(268435456));
                        } catch (Throwable t) {
                            Log.w("CarMedia", "open update url failed: " + t.getMessage());
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void onCheckUpdate() {
            new Thread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.18
                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.this.checkForUpdate(true);
                }
            }, "update-check-manual").start();
        }

        @JavascriptInterface
        public void onShowUpdateInfo() {
            String ver = MainActivity.sCachedRemoteVer;
            String notes = MainActivity.sCachedNotes;
            if (ver != null) {
                MainActivity.this.showUpdateViewOnly(ver, notes);
            } else {
                MainActivity.this.showToast("正在获取更新信息...");
                new Thread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.19
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            String page = MainActivity.this.httpGet(MainActivity.UPDATE_URL, null);
                            if (page != null && !page.isEmpty()) {
                                String[] v = MainActivity.this.parseRemoteManifest(page);
                                if (v == null) {
                                    MainActivity.this.showToast("获取更新信息失败，请重试");
                                    return;
                                }
                                String unused = MainActivity.sCachedRemoteVer = v[0];
                                String unused2 = MainActivity.sCachedNotes = v[1];
                                String unused3 = MainActivity.sCachedRemoteUrl = v[2];
                                String unused4 = MainActivity.sCachedApkMd5 = v[3];
                                MainActivity.this.showUpdateViewOnly(v[0], v[1]);
                                return;
                            }
                            MainActivity.this.showToast("获取更新信息失败，请检查网络");
                        } catch (Throwable t) {
                            Log.w("CarMedia", "show update info failed: " + t.getMessage());
                            MainActivity.this.showToast("获取更新信息失败");
                        }
                    }
                }, "update-info").start();
            }
        }

        @JavascriptInterface
        public void onQqAuthorize() {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.20
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.mService != null) {
                        MainActivity.this.mService.authorizeQqMusic();
                    }
                    MainActivity.this.uiHandler.postDelayed(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.JsBridge.20.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.pushAll();
                        }
                    }, 8000L);
                }
            });
        }
    }

    void confirmControllerSelection(int index) {
        if (this.mService == null) {
            return;
        }
        List<MediaController> list = this.mService.getAvailableControllers();
        if (list != null && index >= 0 && index < list.size()) {
            this.mService.selectController(list.get(index));
            return;
        }
        int ghostIdx = list != null ? list.size() : 0;
        int offset = index - ghostIdx;
        List<String> ghosts = this.mService.getGhostControllerPkgs();
        if (offset >= 0 && offset < ghosts.size()) {
            String pkg = ghosts.get(offset);
            Log.i("CarMedia", "confirmControllerSelection: selecting ghost controller " + pkg);
            this.mService.setGhostControllerPkg(pkg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startRecording(final String action, final int tap) {
        if (this.mService == null) {
            return;
        }
        this.recording = true;
        this.mService.startKeyRecording(new KeyMonitor.KeyRecorder() { // from class: com.ecarx.carmedia.MainActivity.7
            @Override // com.ecarx.carmedia.KeyMonitor.KeyRecorder
            public void onKeyRecorded(final int keyCode) {
                MainActivity.this.uiHandler.post(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.7.1
                    @Override // java.lang.Runnable
                    public void run() {
                        MainActivity.this.recording = false;
                        if (MainActivity.this.mService != null) {
                            MainActivity.this.mService.setKeyAction(keyCode, tap, action);
                        }
                        MainActivity.this.runJs("CarMediaUI.endRecord(" + keyCode + ");");
                        MainActivity.this.pushAll();
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelRecording() {
        this.recording = false;
        if (this.mService != null) {
            this.mService.stopKeyRecording();
        }
        runJs("CarMediaUI.cancelRecord();");
    }

    private String readBgMode() {
        try {
            String m = getSharedPreferences(PREFS, 0).getString(PREF_BG_MODE, BG_DARK);
            if (m != null && !m.isEmpty()) {
                if (BG_CUSTOM.equals(m)) {
                    String src = getSharedPreferences(PREFS, 0).getString(PREF_BG_SRC, "");
                    if (!src.isEmpty()) {
                        return BG_LIGHT;
                    }
                }
                return m;
            }
            return BG_DARK;
        } catch (Throwable th) {
            return BG_DARK;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveBgMode(String mode) {
        try {
            getSharedPreferences(PREFS, 0).edit().putString(PREF_BG_MODE, mode).apply();
        } catch (Throwable th) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pickCustomWallpaper() {
        try {
            final List<String> names = scanWallpapers();
            if (names.isEmpty()) {
                return;
            }
            new AlertDialog.Builder(this).setTitle("选择壁纸图片").setItems((CharSequence[]) names.toArray(new String[0]), new DialogInterface.OnClickListener() { // from class: com.ecarx.carmedia.MainActivity.8
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    String file = (String) names.get(which);
                    try {
                        MainActivity.this.copyAssetToFiles(file);
                        MainActivity.this.setBgSrc(file);
                        MainActivity.this.bgMode = MainActivity.BG_LIGHT;
                        MainActivity.this.saveBgMode(MainActivity.BG_LIGHT);
                        MainActivity.this.pushAll();
                    } catch (Throwable t) {
                        Log.w("CarMedia", "apply wallpaper failed: " + t.getMessage());
                    }
                }
            }).setNegativeButton("取消", (DialogInterface.OnClickListener) null).show();
        } catch (Throwable t) {
            Log.w("CarMedia", "pickCustomWallpaper failed: " + t.getMessage());
        }
    }

    private List<String> scanWallpapers() {
        ArrayList<String> list = new ArrayList<>();
        try {
            String[] files = getAssets().list("wallpapers");
            if (files != null) {
                for (String f : files) {
                    String lower = f.toLowerCase();
                    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")) {
                        list.add(f);
                    }
                }
            }
        } catch (Throwable t) {
            Log.w("CarMedia", "scan wallpapers failed: " + t.getMessage());
        }
        Collections.sort(list);
        return list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void copyAssetToFiles(String file) throws Exception {
        InputStream in = getAssets().open("wallpapers/" + file);
        try {
            File out = new File(getFilesDir(), "custom_bg.jpg");
            FileOutputStream fos = new FileOutputStream(out);
            try {
                byte[] buf = new byte[8192];
                while (true) {
                    int n = in.read(buf);
                    if (n > 0) {
                        fos.write(buf, 0, n);
                    } else {
                        return;
                    }
                }
            } finally {
                fos.close();
            }
        } finally {
            in.close();
        }
    }

    private String getAppDisplayName(String pkg) {
        if (pkg == null) {
            return "";
        }
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(pkg, 0);
            CharSequence label = getPackageManager().getApplicationLabel(ai);
            if (label != null && label.length() > 0) {
                return label.toString();
            }
        } catch (Exception e) {
        }
        return pkg;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleUiPoll(Runnable r) {
        if (this.uiHandler != null) {
            this.uiHandler.postDelayed(r, 1000L);
        }
    }

    private Object reflectDayNightMode() {
        try {
            Object api = invokeStatic("com.ecarx.sdk.device.DeviceAPI", "get", new Class[]{Context.class}, this);
            if (api != null) {
                Object dm = invoke(api, "getDayNightMode", null, new Object[0]);
                if (dm != null) {
                    return dm;
                }
            }
        } catch (Throwable th) {
        }
        try {
            Object device = invokeStatic("com.ecarx.xui.adaptapi.device.Device", "create", new Class[]{Context.class}, this);
            if (device != null) {
                Object dm2 = invoke(device, "getDayNightMode", null, new Object[0]);
                if (dm2 != null) {
                    return dm2;
                }
            }
        } catch (Throwable th2) {
        }
        return null;
    }

    private static Object invokeStatic(String cls, String method, Class<?>[] types, Object... args) throws Exception {
        return Class.forName(cls).getMethod(method, types).invoke(null, args);
    }

    private static Object invoke(Object obj, String method, Class<?>[] types, Object... args) throws Exception {
        if (obj == null) {
            return null;
        }
        if (types == null) {
            for (Method m : obj.getClass().getMethods()) {
                if (m.getName().equals(method)) {
                    return m.invoke(obj, args);
                }
            }
            return null;
        }
        return obj.getClass().getMethod(method, types).invoke(obj, args);
    }

    private boolean readNightState() {
        try {
            Object dm = this.mDayNightMode != null ? this.mDayNightMode : reflectDayNightMode();
            if (dm == null) {
                return false;
            }
            if (this.mDayNightMode == null) {
                this.mDayNightMode = dm;
            }
            Object v = invoke(dm, "getDayNight", null, new Object[0]);
            if (v instanceof Integer) {
                return ((Integer) v).intValue() == 2;
            }
            return false;
        } catch (Throwable t) {
            Log.w("CarMedia", "readNightState failed: " + t.getMessage());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerNightCallback() {
        if (this.mNightRegistered) {
            return;
        }
        try {
            Object dm = this.mDayNightMode != null ? this.mDayNightMode : reflectDayNightMode();
            if (dm == null) {
                return;
            }
            if (this.mDayNightMode == null) {
                this.mDayNightMode = dm;
            }
            this.mNightState = readNightState();
            Class<?> cbCls = Class.forName("com.ecarx.xui.adaptapi.device.daynigntmode.IDayNightMode$IDayNightChangeCallBack");
            boolean z = true;
            this.mNightCbProxy = Proxy.newProxyInstance(cbCls.getClassLoader(), new Class[]{cbCls}, new InvocationHandler() { // from class: com.ecarx.carmedia.MainActivity.9
                @Override // java.lang.reflect.InvocationHandler
                public Object invoke(Object proxy, Method method, Object[] args) {
                    if ("onDayNightChanged".equals(method.getName()) && args != null && args.length > 0) {
                        final int v = ((Number) args[0]).intValue();
                        MainActivity.this.uiHandler.post(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.9.1
                            @Override // java.lang.Runnable
                            public void run() {
                                MainActivity.this.mNightState = v == 2;
                                MainActivity.this.runJs("CarMediaUI.onNightChanged(" + MainActivity.this.mNightState + ");");
                            }
                        });
                        return null;
                    }
                    return null;
                }
            });
            Boolean ok = (Boolean) invoke(dm, "registerDayNightModeCallBack", new Class[]{cbCls}, this.mNightCbProxy);
            if (ok == null || !ok.booleanValue()) {
                z = false;
            }
            this.mNightRegistered = z;
            Log.i("CarMedia", "registerNightCallback ok=" + this.mNightRegistered + " night=" + this.mNightState);
            if (!this.mNightRegistered) {
                this.mNightCbProxy = null;
            }
        } catch (Throwable t) {
            Log.w("CarMedia", "registerNightCallback failed: " + t.getMessage());
        }
    }

    private void unregisterNightCallback() {
        if (!this.mNightRegistered || this.mDayNightMode == null || this.mNightCbProxy == null) {
            this.mNightRegistered = false;
            return;
        }
        try {
            invoke(this.mDayNightMode, "unregisterDayNigntModeCallBack", new Class[]{this.mNightCbProxy.getClass().getInterfaces()[0]}, this.mNightCbProxy);
        } catch (Throwable th) {
        }
        this.mNightRegistered = false;
        this.mNightCbProxy = null;
    }

    private void checkForUpdateOnce() {
        if (sCheckedInProcess) {
            return;
        }
        sCheckedInProcess = true;
        new Thread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.10
            @Override // java.lang.Runnable
            public void run() {
                MainActivity.this.checkForUpdate(false);
            }
        }, "update-check").start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00b7, code lost:
    
        showToast("检测失败，请检查网络");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void checkForUpdate(boolean r10) {
        /*
            r9 = this;
            java.lang.String r0 = "1.3.4"
            java.lang.String r1 = "检测失败，请重试"
            java.lang.String r2 = "CarMedia"
            java.lang.String r3 = "https://jqt.czrui.cn/CarMedia.txt"
            r4 = 0
            java.lang.String r3 = r9.httpGet(r3, r4)     // Catch: java.lang.Throwable -> Lc2
            if (r3 == 0) goto Lb5
            boolean r4 = r3.isEmpty()     // Catch: java.lang.Throwable -> Lc2
            if (r4 == 0) goto L17
            goto Lb5
        L17:
            java.lang.String[] r4 = r9.parseRemoteManifest(r3)     // Catch: java.lang.Throwable -> Lc2
            if (r4 != 0) goto L28
            if (r10 == 0) goto L22
            r9.showToast(r1)     // Catch: java.lang.Throwable -> Lc2
        L22:
            java.lang.String r0 = "update check: version info not found"
            android.util.Log.i(r2, r0)     // Catch: java.lang.Throwable -> Lc2
            return
        L28:
            r5 = 0
            r5 = r4[r5]     // Catch: java.lang.Throwable -> Lc2
            r6 = 1
            r6 = r4[r6]     // Catch: java.lang.Throwable -> Lc2
            com.ecarx.carmedia.MainActivity.sCachedRemoteVer = r5     // Catch: java.lang.Throwable -> Lc2
            com.ecarx.carmedia.MainActivity.sCachedNotes = r6     // Catch: java.lang.Throwable -> Lc2
            r7 = 2
            r7 = r4[r7]     // Catch: java.lang.Throwable -> Lc2
            com.ecarx.carmedia.MainActivity.sCachedRemoteUrl = r7     // Catch: java.lang.Throwable -> Lc2
            r7 = 3
            r7 = r4[r7]     // Catch: java.lang.Throwable -> Lc2
            com.ecarx.carmedia.MainActivity.sCachedApkMd5 = r7     // Catch: java.lang.Throwable -> Lc2
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lc2
            r7.<init>()     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r8 = "update check: remote=["
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch: java.lang.Throwable -> Lc2
            java.lang.StringBuilder r7 = r7.append(r5)     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r8 = "] notes=["
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch: java.lang.Throwable -> Lc2
            java.lang.StringBuilder r7 = r7.append(r6)     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r8 = "]"
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> Lc2
            android.util.Log.i(r2, r7)     // Catch: java.lang.Throwable -> Lc2
            int r7 = compareVersion(r5, r0)     // Catch: java.lang.Throwable -> Lc2
            if (r7 > 0) goto L96
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lc2
            r7.<init>()     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r8 = "update check: no newer version (remote="
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch: java.lang.Throwable -> Lc2
            java.lang.StringBuilder r7 = r7.append(r5)     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r8 = ", local="
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch: java.lang.Throwable -> Lc2
            java.lang.StringBuilder r0 = r7.append(r0)     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r7 = ")"
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Throwable -> Lc2
            android.util.Log.i(r2, r0)     // Catch: java.lang.Throwable -> Lc2
            if (r10 == 0) goto L95
            java.lang.String r0 = "当前已是最新版本"
            r9.showToast(r0)     // Catch: java.lang.Throwable -> Lc2
        L95:
            return
        L96:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lc2
            r0.<init>()     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r7 = "update check: newer version found "
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch: java.lang.Throwable -> Lc2
            java.lang.StringBuilder r0 = r0.append(r5)     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Throwable -> Lc2
            android.util.Log.i(r2, r0)     // Catch: java.lang.Throwable -> Lc2
            com.ecarx.carmedia.MainActivity$11 r0 = new com.ecarx.carmedia.MainActivity$11     // Catch: java.lang.Throwable -> Lc2
            r0.<init>()     // Catch: java.lang.Throwable -> Lc2
            r9.runOnUiThread(r0)     // Catch: java.lang.Throwable -> Lc2
            goto Le2
        Lb5:
            if (r10 == 0) goto Lbc
            java.lang.String r0 = "检测失败，请检查网络"
            r9.showToast(r0)     // Catch: java.lang.Throwable -> Lc2
        Lbc:
            java.lang.String r0 = "update check: page empty"
            android.util.Log.i(r2, r0)     // Catch: java.lang.Throwable -> Lc2
            return
        Lc2:
            r0 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "update check failed: "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = r0.getMessage()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Log.w(r2, r3)
            if (r10 == 0) goto Le2
            r9.showToast(r1)
        Le2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.MainActivity.checkForUpdate(boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.12
            @Override // java.lang.Runnable
            public void run() {
                try {
                    MainActivity.this.runJs("showToast(" + JSONObject.quote(msg) + ");");
                } catch (Throwable t) {
                    Log.w("CarMedia", "showToast failed: " + t.getMessage());
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String[] parseRemoteManifest(String page) {
        try {
            String s = page.trim();
            if (s.startsWith("\ufeff")) {
                s = s.substring(1).trim();
            }
            StringBuilder sb = new StringBuilder();
            for (String line : s.split("\n")) {
                String t = line.trim();
                if (!t.startsWith("#") && !t.startsWith("//")) {
                    sb.append(line).append('\n');
                }
            }
            String json = sb.toString().trim();
            JSONObject o = new JSONObject(json);
            String remoteVer = o.optString("version", "").trim();
            String notes = o.optString("notes", "").trim();
            String url = o.optString("url", "").trim();
            String md5 = o.optString("md5", "").trim();
            if (remoteVer.isEmpty()) {
                return null;
            }
            return new String[]{remoteVer, notes, url, md5};
        } catch (Throwable th) {
            Matcher vm = Pattern.compile("\"version\"\\s*:\\s*\"([^\"]+)\"").matcher(page);
            if (!vm.find()) {
                return null;
            }
            String remoteVer2 = vm.group(1).trim();
            Matcher nm = Pattern.compile("\"notes\"\\s*:\\s*\"([^\"]*)\"").matcher(page);
            String notes2 = nm.find() ? nm.group(1).trim() : "";
            Matcher um = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]*)\"").matcher(page);
            String url2 = um.find() ? um.group(1).trim() : "";
            return new String[]{remoteVer2, notes2, url2, ""};
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String httpGet(String url, String cookie) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 9; SM-G960F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            if (cookie != null && !cookie.isEmpty()) {
                conn.setRequestProperty("Cookie", "acw_sc__v2=" + cookie);
            }
            conn.setInstanceFollowRedirects(true);
            int code = conn.getResponseCode();
            if (code != 200) {
                Log.w("CarMedia", "update check: http " + code);
                return null;
            }
            InputStream in = conn.getInputStream();
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                while (true) {
                    int n = in.read(buf);
                    if (n <= 0) {
                        break;
                    }
                    bos.write(buf, 0, n);
                }
                String str = new String(bos.toByteArray(), "UTF-8");
                if (conn != null) {
                    conn.disconnect();
                }
                return str;
            } finally {
                in.close();
            }
        } catch (Throwable t) {
            try {
                Log.w("CarMedia", "update check: httpGet " + url + " -> " + t.getMessage());
                if (conn != null) {
                    conn.disconnect();
                }
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    private static int compareVersion(String a, String b) {
        String[] pa = a.split("\\.");
        String[] pb = b.split("\\.");
        int n = Math.max(pa.length, pb.length);
        int i = 0;
        while (true) {
            if (i >= n) {
                return 0;
            }
            int x = i < pa.length ? Integer.parseInt(pa[i]) : 0;
            int y = i < pb.length ? Integer.parseInt(pb[i]) : 0;
            if (x == y) {
                i++;
            } else {
                return x - y;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] httpGetBytes(String url, ProgressCallback progress) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 9; SM-G960F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
            conn.setInstanceFollowRedirects(true);
            int code = conn.getResponseCode();
            if (code != 200) {
                Log.w("CarMedia", "download apk: http " + code);
                return null;
            }
            long total = conn.getContentLengthLong();
            InputStream in = conn.getInputStream();
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                long read = 0;
                int lastPct = -1;
                while (true) {
                    int n = in.read(buf);
                    if (n <= 0) {
                        break;
                    }
                    bos.write(buf, 0, n);
                    read += n;
                    if (progress != null) {
                        if (total > 0) {
                            int pct = (int) ((100 * read) / total);
                            if (pct != lastPct) {
                                progress.onProgress(pct);
                                lastPct = pct;
                            }
                        } else if (lastPct == -1) {
                            lastPct = 0;
                            progress.onProgress(0);
                        }
                    }
                }
                if (progress != null) {
                    progress.onProgress(100);
                }
                byte[] byteArray = bos.toByteArray();
                if (conn != null) {
                    conn.disconnect();
                }
                return byteArray;
            } finally {
                in.close();
            }
        } catch (Throwable t) {
            try {
                Log.w("CarMedia", "download apk failed: " + t.getMessage());
                if (conn == null) {
                    return null;
                }
                conn.disconnect();
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String md5Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] d = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : d) {
                String h = Integer.toHexString(b & 255);
                if (h.length() == 1) {
                    sb.append('0');
                }
                sb.append(h);
            }
            return sb.toString();
        } catch (Throwable th) {
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void installApkBytes(byte[] data) {
        try {
            File apkFile = new File(getCacheDir(), ApkProvider.APK_NAME);
            FileOutputStream fos = new FileOutputStream(apkFile);
            fos.write(data);
            fos.flush();
            fos.close();
            Log.i("CarMedia", "apk saved: " + apkFile.getAbsolutePath() + " bytes=" + data.length);
            runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.13
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setDataAndType(ApkProvider.buildApkUri(), "application/vnd.android.package-archive");
                        intent.addFlags(268435456);
                        intent.addFlags(1);
                        MainActivity.this.startActivity(intent);
                    } catch (Throwable t) {
                        Log.w("CarMedia", "open apk installer failed: " + t.getMessage());
                        MainActivity.this.showToast("无法打开安装器");
                    }
                }
            });
        } catch (Throwable t) {
            Log.w("CarMedia", "save apk failed: " + t.getMessage());
            showToast("保存更新包失败");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showUpdateDialog(final String remoteVer, String notes) {
        final String n;
        if (notes == null) {
            n = "";
        } else {
            try {
                n = notes.replace("。", "\n");
            } catch (Throwable t) {
                Log.w("CarMedia", "showUpdateDialog failed: " + t.getMessage());
                return;
            }
        }
        runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.14
            @Override // java.lang.Runnable
            public void run() {
                MainActivity.this.runJs("CarMediaUI.showUpdate(" + JSONObject.quote(remoteVer) + "," + JSONObject.quote(n) + ");");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showUpdateViewOnly(final String remoteVer, String notes) {
        final String n;
        if (notes == null) {
            n = "";
        } else {
            try {
                n = notes.replace("。", "\n");
            } catch (Throwable t) {
                Log.w("CarMedia", "showUpdateViewOnly failed: " + t.getMessage());
                return;
            }
        }
        runOnUiThread(new Runnable() { // from class: com.ecarx.carmedia.MainActivity.15
            @Override // java.lang.Runnable
            public void run() {
                MainActivity.this.runJs("CarMediaUI.showUpdate(" + JSONObject.quote(remoteVer) + "," + JSONObject.quote(n) + ",true);");
            }
        });
    }
}
