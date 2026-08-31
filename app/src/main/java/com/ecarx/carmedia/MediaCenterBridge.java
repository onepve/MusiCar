package com.ecarx.carmedia;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.util.Log;
import com.ecarx.carmedia.CarMediaMusicClient;
import com.ecarx.eas.sdk.ECarXApiClient;
import com.ecarx.eas.sdk.mediacenter.MediaCenterAPI;
import com.ecarx.eas.sdk.mediacenter.MusicPlaybackInfo;
import java.util.List;

/* loaded from: classes3.dex */
public class MediaCenterBridge {
    private static volatile boolean sBluetoothSessionPresent = false;
    private static volatile CarMediaMusicClient.Controller sController;
    private static volatile String sControllerPkg;
    private static MediaCenterBridge sInstance;
    private MediaCenterAPI mApi;
    private Context mAppContext;
    private CarMediaMusicClient mClient;
    private boolean mInitStarted;
    private String mLastArtworkUri;
    private boolean mRegistered;
    private Object mToken;

    public static MediaCenterBridge get() {
        MediaCenterBridge mediaCenterBridge = sInstance;
        if (mediaCenterBridge != null) {
            return mediaCenterBridge;
        }
        MediaCenterBridge mediaCenterBridge2 = new MediaCenterBridge();
        sInstance = mediaCenterBridge2;
        return mediaCenterBridge2;
    }

    public void performRegistration() {
        try {
            MediaCenterAPI mediaCenterAPI = this.mApi;
            if (mediaCenterAPI != null && !this.mRegistered) {
                CarMediaMusicClient carMediaMusicClient = new CarMediaMusicClient();
                CarMediaMusicClient.Controller controller = sController;
                if (controller != null) {
                    carMediaMusicClient.setController(controller);
                }
                this.mClient = carMediaMusicClient;
                Object registerMusic = mediaCenterAPI.registerMusic(CarMediaService.PKG_SELF, carMediaMusicClient);
                this.mToken = registerMusic;
                if (registerMusic != null) {
                    this.mRegistered = true;
                    this.mApi.updateMediaSourceTypeList(this.mToken, new int[]{6});
                    this.mApi.declareMediaCenterCapability(this.mToken, new int[]{0, 2, 3});
                    this.mApi.declareSupportCollectTypes(this.mToken, new int[]{0, 3, 4});
                    this.mApi.updateCurrentSourceType(this.mToken, 6);
                }
            }
        } catch (Throwable th) {
            Log.e(CarMediaService.TAG, ">>> EAS registration exception", th);
        }
    }

    public static void setController(CarMediaMusicClient.Controller controller) {
        sController = controller;
    }

    public static void setControllerPkg(String str) {
        sControllerPkg = str;
    }

    public static void setBluetoothSessionPresent(boolean present) {
        sBluetoothSessionPresent = present;
    }

    public static boolean isBluetoothMode() {
        return CarMediaService.PKG_BLUETOOTH.equals(sControllerPkg);
    }

    public boolean isMultimediaRunning() {
        try {
            if (this.mApi != null && this.mRegistered && this.mToken != null) {
                String focus = this.mApi.queryCurrentFocusClient(this.mToken);
                boolean running = "com.ecarx.multimedia".equals(focus);
                Log.i(CarMediaService.TAG, ">>> isMultimediaRunning: focus=" + focus + " -> " + running);
                return running;
            }
            return false;
        } catch (Throwable t) {
            Log.w(CarMediaService.TAG, ">>> isMultimediaRunning error: " + t.getMessage());
            return false;
        }
    }

    public void arbitrateFocusToMultimedia() {
        try {
            if (this.mApi != null && this.mRegistered && this.mToken != null && this.mClient != null) {
                MusicPlaybackInfo info = this.mClient.getMusicPlaybackInfo();
                if (info == null) {
                    Log.d(CarMediaService.TAG, ">>> arbitrate focus: no cached playback info, skip");
                } else {
                    this.mApi.updateMusicPlaybackState(this.mToken, info);
                    Log.i(CarMediaService.TAG, ">>> EAS arbitrate focus to multimedia (re-push cached info)");
                }
            }
        } catch (Throwable t) {
            Log.w(CarMediaService.TAG, ">>> arbitrateFocusToMultimedia error: " + t.getMessage());
        }
    }

    public boolean shouldGrabEasFocus() {
        if (isBluetoothReallyPlaying()) {
            return false;
        }
        if (isBluetoothMode()) {
            return !isMultimediaRunning();
        }
        return true;
    }

    private boolean isBluetoothReallyPlaying() {
        Context ctx = null;
        MediaSessionManager msm;
        List<MediaController> sessions;
        PlaybackState ps;
        try {
            ctx = this.mAppContext;
        } catch (Throwable t) {
            Log.w(CarMediaService.TAG, ">>> isBluetoothReallyPlaying error: " + t.getMessage());
        }
        if (ctx == null || (msm = (MediaSessionManager) ctx.getSystemService("media_session")) == null || (sessions = msm.getActiveSessions(null)) == null) {
            return false;
        }
        for (MediaController c : sessions) {
            if (c != null && CarMediaService.PKG_BLUETOOTH.equals(c.getPackageName()) && (ps = c.getPlaybackState()) != null && ps.getState() == 3) {
                return true;
            }
        }
        return false;
    }

    public void init(Context context) {
        if (this.mInitStarted) {
            return;
        }
        this.mInitStarted = true;
        this.mAppContext = context.getApplicationContext();
        MediaCenterAPI mediaCenterAPI = MediaCenterAPI.get(context);
        this.mApi = mediaCenterAPI;
        mediaCenterAPI.init(context, new ECarXApiClient.Callback() { // from class: com.ecarx.carmedia.MediaCenterBridge.1
            @Override // com.ecarx.eas.sdk.ECarXApiClient.Callback
            public void onAPIReady(boolean z) {
                if (!z) {
                    Log.e(CarMediaService.TAG, ">>> EAS onAPIReady: FAILED");
                    return;
                }
                MediaCenterBridge.this.performRegistration();
                Log.i(CarMediaService.TAG, ">>> EAS onAPIReady: registered");
                try {
                    CarMediaService service = CarMediaService.sInstance;
                    if (service != null) {
                        service.onEasRegistered();
                    }
                } catch (Throwable th) {
                    Log.w(CarMediaService.TAG, ">>> EAS onAPIReady re-push error", th);
                }
            }
        });
    }

    public void setArtworkUri(String uri) {
        this.mLastArtworkUri = uri;
    }

    private String resolveAppIcon(String pkg) {
        Context ctx = this.mAppContext;
        if (ctx == null || pkg == null) {
            return null;
        }
        String fallback = null;
        try {
            int iconRes = ctx.getApplicationInfo().icon;
            if (iconRes != 0) {
                fallback = "android.resource://" + ctx.getPackageName() + "/" + iconRes;
            }
        } catch (Throwable t) {
            Log.w(CarMediaService.TAG, ">>> resolve own icon fallback failed", t);
        }
        try {
            return AppIconResolver.resolve(ctx, pkg, fallback);
        } catch (Throwable t2) {
            Log.w(CarMediaService.TAG, ">>> AppIconResolver failed", t2);
            return fallback;
        }
    }

    public void pushLyric(String str) {
        MediaCenterAPI mediaCenterAPI = this.mApi;
        if (mediaCenterAPI == null || !this.mRegistered || str == null) {
            return;
        }
        mediaCenterAPI.updateCurrentLyric(this.mToken, str);
    }

    public void pushLyric(String str, int i, String str2) {
        pushLyric(str);
    }

    public boolean pushMetadata(String str, String str2, String str3, long j, int i, boolean z, String str4, long pos) {
        boolean z2;
        ApplicationInfo applicationInfo;
        int i2;
        Intent launchIntentForPackage;
        try {
            if (this.mApi != null) {
                try {
                    if (!this.mRegistered) {
                        return false;
                    }
                    MusicPlaybackInfo musicPlaybackInfo = new MusicPlaybackInfo();
                    if (str != null) {
                        musicPlaybackInfo.setTitle(str);
                    }
                    if (str2 != null) {
                        musicPlaybackInfo.setArtist(str2);
                    }
                    if (str3 != null) {
                        musicPlaybackInfo.setAlbum(str3);
                    }
                    try {
                        musicPlaybackInfo.setDuration(j);
                        musicPlaybackInfo.setPlaybackStatus(i == 1 ? 1 : 0);
                        musicPlaybackInfo.setSourceType(6);
                        musicPlaybackInfo.setPackageName(CarMediaService.PKG_SELF);
                        if (str4 != null) {
                            try {
                                Context context = this.mAppContext;
                                if (context != null) {
                                    musicPlaybackInfo.setAppName((String) this.mAppContext.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(str4, 0)));
                                }
                            } catch (Throwable th) {
                                Log.e(CarMediaService.TAG, ">>> EAS pushMetadata exception"); return false;
                            }
                        }
                        if (str4 != null) {
                            try {
                                Context context2 = this.mAppContext;
                                if (context2 != null && (applicationInfo = context2.getPackageManager().getApplicationInfo(str4, 0)) != null && (i2 = applicationInfo.icon) != 0) {
                                    String resolvedIcon = resolveAppIcon(str4);
                                    if (resolvedIcon != null) {
                                        musicPlaybackInfo.setAppIcon(resolvedIcon);
                                    } else {
                                        musicPlaybackInfo.setAppIcon("android.resource://" + str4 + "/" + i2);
                                    }
                                }
                            } catch (Throwable th2) {
                                Log.w(CarMediaService.TAG, ">>> setAppIcon failed", th2);
                            }
                        }
                        musicPlaybackInfo.setMediaType("music");
                        musicPlaybackInfo.setSupportLoopModeSwitch(true);
                        musicPlaybackInfo.setSupportVrCtrlPlayStatus(true);
                        musicPlaybackInfo.setSupportCollect(false);
                        musicPlaybackInfo.setPlayingMediaListId("com.ecarx.carmedia-play-list-common");
                        StringBuilder sb = new StringBuilder();
                        if (str != null) {
                            sb.append(str);
                        }
                        sb.append("|");
                        if (str2 != null) {
                            sb.append(str2);
                        }
                        musicPlaybackInfo.setUuid(sb.toString());
                        if (this.mLastArtworkUri != null) {
                            musicPlaybackInfo.setArtwork(Uri.parse(this.mLastArtworkUri));
                        } else {
                            musicPlaybackInfo.setArtwork(null);
                        }
                        Context context3 = this.mAppContext;
                        if (context3 != null) {
                            if (str4 != null && (launchIntentForPackage = context3.getPackageManager().getLaunchIntentForPackage(str4)) != null) {
                                launchIntentForPackage.addFlags(268435456);
                                PendingIntent activity2 = PendingIntent.getActivity(this.mAppContext, 0, launchIntentForPackage, 201326592);
                                musicPlaybackInfo.setLaunchIntent(activity2);
                                musicPlaybackInfo.setPlayerIntent(activity2);
                            }
                            Intent intent = new Intent(this.mAppContext, (Class<?>) MainActivity.class);
                            intent.addFlags(268435456);
                            PendingIntent activity = PendingIntent.getActivity(this.mAppContext, 0, intent, 0);
                            musicPlaybackInfo.setLaunchIntent(activity);
                            musicPlaybackInfo.setPlayerIntent(activity);
                        }
                        CarMediaMusicClient carMediaMusicClient = this.mClient;
                        if (carMediaMusicClient != null) {
                            carMediaMusicClient.setPlaybackInfo(musicPlaybackInfo);
                        }
                        try {
                            if (this.mApi != null && this.mToken != null) {
                                if (shouldGrabEasFocus()) {
                                    if (!CarMediaService.PKG_SELF.equals(this.mApi.queryCurrentFocusClient(this.mToken))) {
                                        Log.i(CarMediaService.TAG, ">>> EAS grab focus (bt=" + isBluetoothMode() + " multimedia=" + isMultimediaRunning() + ")");
                                        this.mApi.requestPlay(this.mToken);
                                        this.mApi.updateCurrentSourceType(this.mToken, 6);
                                    }
                                } else {
                                    Log.d(CarMediaService.TAG, ">>> EAS skip focus grab (bluetooth + multimedia running)");
                                }
                            }
                        } catch (Throwable t) {
                            Log.w(CarMediaService.TAG, ">>> EAS focus grab failed: " + t.getMessage());
                        }
                        this.mApi.updateMusicPlaybackState(this.mToken, musicPlaybackInfo);
                    } catch (Throwable th3) {
                        Log.e(CarMediaService.TAG, ">>> EAS pushMetadata exception"); return false;
                    }
                    try {
                        this.mApi.updateCurrentProgress(this.mToken, pos);
                        MediaCenterAPI mediaCenterAPI = this.mApi;
                        int i3 = 1;
                        if (i != 1) {
                            i3 = 0;
                        }
                        mediaCenterAPI.updatePlayState(CarMediaService.PKG_SELF, i3, 0);
                        return true;
                    } catch (Throwable th4) {
                        Log.e(CarMediaService.TAG, ">>> EAS pushMetadata exception"); return false;
                    }
                } catch (Throwable th5) {
                    Log.e(CarMediaService.TAG, ">>> EAS pushMetadata exception"); return false;
                }
            } else {
                return false;
            }
        } catch (Throwable th6) {
            Log.e(CarMediaService.TAG, ">>> EAS pushMetadata exception");
            return false;
        }
    }

    public void pushPlayState(int i, int i2) {
        MediaCenterAPI mediaCenterAPI = this.mApi;
        if (mediaCenterAPI == null || !this.mRegistered) {
            return;
        }
        mediaCenterAPI.updatePlayState(CarMediaService.PKG_SELF, i, i2);
    }

    public void pushProgress(long j) {
        MediaCenterAPI mediaCenterAPI = this.mApi;
        if (mediaCenterAPI == null || !this.mRegistered) {
            return;
        }
        mediaCenterAPI.updateCurrentProgress(this.mToken, j);
    }

    public void ensureFocus() {
        try {
            if (this.mApi != null && this.mRegistered && this.mToken != null) {
                String focus = this.mApi.queryCurrentFocusClient(this.mToken);
                Log.i(CarMediaService.TAG, ">>> ensureFocus(DEPRECATED): focus=" + focus + " -> 不抢焦点,保持空让 XCMedia2 判定自己有焦点");
            }
        } catch (Throwable t) {
            Log.w(CarMediaService.TAG, ">>> ensureFocus failed: " + t.getMessage());
        }
    }

    public void logFocusOnce() {
        try {
            if (this.mApi != null && this.mRegistered && this.mToken != null) {
                String focus = this.mApi.queryCurrentFocusClient(this.mToken);
                Log.i(CarMediaService.TAG, ">>> focus observer: focus=" + focus);
            }
        } catch (Throwable t) {
            Log.w(CarMediaService.TAG, ">>> logFocusOnce failed: " + t.getMessage());
        }
    }
}
