package com.ecarx.carmedia;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import com.ecarx.carmedia.CarMediaMusicClient;
import com.ecarx.carmedia.CloudLyricFetcher;
import com.ecarx.carmedia.KeyMonitor;
import com.ecarx.carmedia.KuwoAidlClient;
import com.ecarx.carmedia.LogcatKeyMonitor;
import com.ecarx.carmedia.QQMusicLyricFetcher;
import com.ecarx.carmedia.SodaMusicLyricFetcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class CarMediaService extends Service implements CarMediaMusicClient.Controller {
    private static final long AUTO_SWITCH_COOLDOWN_MS = 3000;
    private static final long BT_SOURCE_KEEP_INTERVAL_MS = 10000;
    public static final String CHANNEL_ID = "carmedia_service";
    private static final long EAS_FOCUS_ARBITRATE_INTERVAL_MS = 5000;
    public static final int EAS_STATUS_PAUSED = 0;
    public static final int EAS_STATUS_PLAYING = 1;
    private static final long KEY_CHANNEL_DEDUP_MS = 500;
    public static final long LYRIC_POLL_INTERVAL_MS = 500;
    public static final String METADATA_ARTIST = "android.media.metadata.ARTIST";
    public static final String METADATA_DURATION = "android.media.metadata.DURATION";
    public static final String METADATA_TITLE = "android.media.metadata.TITLE";
    private static final long MULTIMEDIA_KILL_COOLDOWN_MS = 30000;
    public static final int NOTIFICATION_ID = 1;
    public static final String PKG_BLUETOOTH = "com.android.bluetooth";
    public static final String PKG_KUWO = "cn.kuwo.kwmusiccar";
    public static final String PKG_KUWO_CAS = "cn.kuwo.kwmusiccas";
    public static final String PKG_NETEASE = "com.netease.cloudmusic";
    public static final String PKG_NETEASE_IOT = "com.netease.cloudmusic.iot";
    public static final String PKG_QQ_MUSIC = "com.tencent.qqmusiccar";
    public static final String PKG_SELF = "com.ecarx.carmedia";
    public static final String PKG_SODA = "com.luna.music";
    public static final String PKG_SODA_CAR = "com.luna.music.car";
    public static final String PKG_TELECOM = "com.android.server.telecom";
    private static final long POSITION_TICKER_INTERVAL_MS = 2000;
    private static final String PREF_KEY_WE_DISABLED_MM = "multimedia_we_disabled";
    public static final int SOURCE_TYPE_GENERIC = 3;
    public static final int SOURCE_TYPE_NETEASE = 2;
    public static final int SOURCE_TYPE_NONE = 0;
    public static final int SOURCE_TYPE_QQ = 1;
    public static final int SOURCE_TYPE_SODA = 4;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_PLAYING = 3;
    public static final String TAG = "CarMedia";
    private static final long TELECOM_END_PROTECT_MS = 5000;
    private static final boolean USE_BLUETOOTH_FOCUS_HOLD = true;
    public static volatile CarMediaService sInstance;
    private BroadcastReceiver a2dpSinkReceiver;
    private AudioManager audioManager;
    private AudioManager.AudioPlaybackCallback audioPlaybackCallback;
    private String broadcastLyricLine;
    private String broadcastLyricLrc;
    private String broadcastLyricPkg;
    private String broadcastLyricTitle;
    private CloudLyricFetcher cloudLyricFetcher;
    private String controllerPkg;
    private boolean controllerStale;
    private String currentArtist;
    private String currentArtworkUri;
    private String currentTitle;
    private boolean dimAutoPush;
    private int firstPushRetryCount;
    private long frozenPlaybackPosition;
    private String ghostControllerPkg;
    private boolean inTelecomCall;
    private KeyMonitor keyMonitor;
    private KuwoAidlClient kuwoAidlClient;
    private String lastAudioPlayingPkg;
    private long lastAutoSwitchTime;
    private volatile String lastMediaSessionRoutePkg;
    private PlaybackState lastPlaybackState;
    private String lastPushedArtist;
    private String lastPushedArtworkUri;
    private String lastPushedLyricForTitle;
    private String lastPushedLyricLine;
    private int lastPushedLyricState;
    private int lastPushedPlayState;
    private String lastPushedTitle;
    private LogcatKeyMonitor logcatKeyMonitor;
    private Handler lyricPollHandler;
    private AuthMessageListener mAuthListener;
    private MediaController mediaController;
    private MediaSession mediaSession;
    private BroadcastReceiver neteaseLyricReceiver;
    private String notifArtist;
    private String notifPkg;
    private String notifTitle;
    private long playbackDuration;
    private boolean positionTickerRunning;
    private String preCallControllerPkg;
    private boolean qqArtistComplete;
    private QQMusicLyricFetcher qqMusicFetcher;
    private MediaSessionManager sessionManager;
    private SodaMusicLyricFetcher sodaMusicFetcher;
    private boolean switchingController;
    private static final String PKG_MULTIMEDIA = "com.ecarx.multimedia";
    private static final Set<String> SYSTEM_MEDIA_BLACKLIST = new HashSet(Arrays.asList("ecarx.xsf.mediacenter", PKG_MULTIMEDIA));
    private static final String[] NETEASE_LYRIC_KEYS = {"lyric", "lyrics", "lrc", "currentLyric", "currentLine", "current_line", "oneLineLyric", "line", CloudLyricFetcher.API_NETEASE_LYRIC, "curLyric"};
    private final LocalBinder binder = new LocalBinder();
    private List<MediaController> availableControllers = new ArrayList();
    private String lastMetadataTitle = "";
    private String lastMetadataArtist = "";
    private long lastBtPositionMs = 0;
    private long lastBtPositionTime = 0;
    private long lastNonBtPositionMs = 0;
    private long lastNonBtPositionTime = 0;
    private boolean firstPushPending = USE_BLUETOOTH_FOCUS_HOLD;
    private final Handler easFocusHandler = new Handler(Looper.getMainLooper());
    private volatile long lastEasFocusGrabMs = 0;
    private final Runnable easFocusRunnable = new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.1
        @Override // java.lang.Runnable
        public void run() {
            try {
                if (MediaCenterBridge.get() != null) {
                    MediaCenterBridge.get().logFocusOnce();
                }
                CarMediaService.this.maybeKillMultimediaOnBluetoothPlay();
            } finally {
                try {
                } finally {
                }
            }
        }
    };
    private volatile long lastMultimediaKillMs = 0;
    private volatile boolean wasMultimediaDisabled = false;
    private volatile boolean weDisabledMultimedia = false;
    private volatile boolean btFocusHeld = false;
    private final AudioManager.OnAudioFocusChangeListener btFocusListener = new AudioManager.OnAudioFocusChangeListener() { // from class: com.ecarx.carmedia.CarMediaService.2
        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int focusChange) {
            Log.i(CarMediaService.TAG, ">>> bluetooth MAY_DUCK focus change: " + focusChange);
        }
    };
    private volatile long lastBtSourceKeepMs = 0;
    private boolean lastBtPlayingForKeep = false;
    private final List<String> pausedOtherSources = new ArrayList();
    private volatile long lastEasArbitrateMs = 0;
    private volatile long lastLogcatKeyHandleMs = 0;
    private volatile long lastMdcKeyHandleMs = 0;
    private volatile int lastMediaSessionRouteKeyCode = -1;
    private volatile long lastMediaSessionRouteAt = 0;
    private volatile boolean suppressBtPushUntilPlay = false;
    private final Handler firstPushHandler = new Handler(Looper.getMainLooper());
    private final Runnable firstPushRunnable = new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.3
        @Override // java.lang.Runnable
        public void run() {
            try {
                if (CarMediaService.this.firstPushPending && CarMediaService.this.isDimPushEnabled()) {
                    CarMediaService.access$408(CarMediaService.this);
                    CarMediaService.this.pushMetadataToCard();
                    if (CarMediaService.this.firstPushPending && CarMediaService.this.mediaController == null) {
                        CarMediaService.this.pushStoppedToCard();
                    }
                    if (CarMediaService.this.firstPushPending) {
                        if (CarMediaService.this.firstPushRetryCount < 60) {
                            Log.i(CarMediaService.TAG, "first push retry #" + CarMediaService.this.firstPushRetryCount + ": EAS 注册未完成,首推未送达,继续重试");
                            CarMediaService.this.firstPushHandler.postDelayed(this, 800L);
                            return;
                        } else {
                            Log.w(CarMediaService.TAG, "first push retry: 60 次未送达,暂时放弃(后续音源回调/轮询仍会推送)");
                            return;
                        }
                    }
                    Log.i(CarMediaService.TAG, "first push delivered (service activated)");
                }
            } catch (Throwable t) {
                Log.w(CarMediaService.TAG, "first push retry error: " + t.getMessage());
            }
        }
    };
    private final Runnable positionTickerRunnable = new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.4
        @Override // java.lang.Runnable
        public void run() {
            if (!CarMediaService.this.positionTickerRunning) {
                return;
            }
            try {
            } catch (Throwable t) {
                Log.w(CarMediaService.TAG, "position ticker error: " + t.getMessage());
            }
            if (CarMediaService.this.getAudioRealPlayState() == 3) {
                CarMediaService.this.checkContentChangedAndPush();
                CarMediaService.this.lyricPollHandler.postDelayed(this, CarMediaService.POSITION_TICKER_INTERVAL_MS);
            } else {
                CarMediaService.this.positionTickerRunning = false;
                Log.d(CarMediaService.TAG, "position ticker stopped (not playing)");
            }
        }
    };
    private int currentSourceType = 0;
    private boolean voipActive = false;
    private long telecomEndProtectUntil = 0;
    private final Set<String> lyricBlockedPkgs = new HashSet();
    private long lastLyricPosition = -1;
    private boolean artworkRetryScheduled = false;
    private int artworkRetryCount = 0;
    private final MediaController.Callback controllerCallback = new MediaController.Callback() { // from class: com.ecarx.carmedia.CarMediaService.8
        @Override // android.media.session.MediaController.Callback
        public void onMetadataChanged(MediaMetadata metadata) {
            if (metadata != null) {
                CarMediaService.this.handleMetadata(metadata);
            }
        }

        @Override // android.media.session.MediaController.Callback
        public void onPlaybackStateChanged(PlaybackState state) {
            if (state != null) {
                if (state.getState() == 3) {
                    CarMediaService.this.suppressBtPushUntilPlay = false;
                }
                CarMediaService.this.updatePlaybackStatus(state);
                if (CarMediaService.this.isBluetoothController() && state.getState() == 3) {
                    CarMediaService.this.keepXcmediaOnBluetoothSource();
                    CarMediaService.this.pauseOtherSourcesOnBluetoothPlay();
                    CarMediaService.this.arbitrateEasFocusToMultimedia();
                }
            }
        }

        @Override // android.media.session.MediaController.Callback
        public void onSessionDestroyed() {
            Log.w(CarMediaService.TAG, "MediaController session destroyed");
            CarMediaService.this.pushStoppedToCard();
            CarMediaService.this.refreshController();
        }
    };
    private final MediaSessionManager.OnActiveSessionsChangedListener sessionChangedListener = new MediaSessionManager.OnActiveSessionsChangedListener() { // from class: com.ecarx.carmedia.CarMediaService.9
        @Override // android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
        public void onActiveSessionsChanged(List<MediaController> controllers) {
            try {
                if (CarMediaService.this.ghostControllerPkg == null) {
                    if (CarMediaService.this.mediaController != null && !CarMediaService.this.controllerStale) {
                        String curPkg = CarMediaService.this.mediaController.getPackageName();
                        boolean stillActive = false;
                        if (controllers != null) {
                            Iterator<MediaController> it = controllers.iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                MediaController c = it.next();
                                if (c != null && curPkg != null && curPkg.equals(c.getPackageName())) {
                                    stillActive = CarMediaService.USE_BLUETOOTH_FOCUS_HOLD;
                                    break;
                                }
                            }
                        }
                        if (stillActive) {
                            try {
                                MediaController playing = CarMediaService.this.findReallyPlayingController(curPkg);
                                if (playing != null && !curPkg.equals(playing.getPackageName()) && (!CarMediaService.PKG_BLUETOOTH.equals(playing.getPackageName()) || CarMediaService.PKG_BLUETOOTH.equals(curPkg))) {
                                    Log.i(CarMediaService.TAG, "active sessions changed, real playing " + playing.getPackageName() + " != current " + curPkg + ", switch");
                                    CarMediaService.this.selectController(playing);
                                    return;
                                }
                            } catch (Throwable t) {
                                Log.w(CarMediaService.TAG, "session changed real-playing check error: " + t.getMessage());
                            }
                            CarMediaService.this.refreshControllerList();
                            return;
                        }
                        Log.i(CarMediaService.TAG, "active sessions changed, current controller gone, re-select");
                        boolean anyOtherSession = false;
                        if (controllers != null) {
                            Iterator<MediaController> it2 = controllers.iterator();
                            while (true) {
                                if (!it2.hasNext()) {
                                    break;
                                }
                                MediaController c2 = it2.next();
                                if (c2 != null && !CarMediaService.PKG_SELF.equals(c2.getPackageName())) {
                                    anyOtherSession = CarMediaService.USE_BLUETOOTH_FOCUS_HOLD;
                                    break;
                                }
                            }
                        }
                        if (!anyOtherSession) {
                            CarMediaService.this.pushStoppedToCard();
                        }
                        CarMediaService.this.refreshController();
                        return;
                    }
                    Log.i(CarMediaService.TAG, "active sessions changed, re-select controller");
                    CarMediaService.this.refreshController();
                    return;
                }
                Log.d(CarMediaService.TAG, "active sessions changed, ghost controller active, skip re-select");
            } catch (Throwable t2) {
                Log.w(CarMediaService.TAG, "session changed refresh error: " + t2.getMessage());
            }
        }
    };
    private final Runnable lyricPollRunnable = new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.11
        @Override // java.lang.Runnable
        public void run() {
            boolean ok = CarMediaService.this.pollLyricOnce();
            if (ok) {
                CarMediaService.this.scheduleLyricPoll(this);
            }
        }
    };

    public interface AuthMessageListener {
        void onAuthMessage(String str);
    }

    static /* synthetic */ int access$408(CarMediaService x0) {
        int i = x0.firstPushRetryCount;
        x0.firstPushRetryCount = i + 1;
        return i;
    }

    public static boolean isKuwoPkg(String pkg) {
        if (PKG_KUWO.equals(pkg) || PKG_KUWO_CAS.equals(pkg)) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        return false;
    }

    public static boolean isSodaPkg(String pkg) {
        if (PKG_SODA_CAR.equals(pkg) || PKG_SODA.equals(pkg)) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        return false;
    }

    private static boolean isSystemMediaPackage(String pkg) {
        if (pkg == null || !SYSTEM_MEDIA_BLACKLIST.contains(pkg)) {
            return false;
        }
        return USE_BLUETOOTH_FOCUS_HOLD;
    }

    private static boolean isHiddenController(String pkg) {
        if (pkg == null) {
            return false;
        }
        if (isSystemMediaPackage(pkg) || PKG_TELECOM.equals(pkg)) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markLogcatKeyHandle() {
        this.lastLogcatKeyHandleMs = System.currentTimeMillis();
    }

    private boolean isEasFromRecentLogcat() {
        if (System.currentTimeMillis() - this.lastLogcatKeyHandleMs < 500) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        return false;
    }

    private boolean isSystemMediaKeyRoutedToController(int keyCode) {
        if (this.controllerPkg == null || this.controllerPkg.isEmpty() || this.lastMediaSessionRouteKeyCode != keyCode || this.lastMediaSessionRoutePkg == null || !this.lastMediaSessionRoutePkg.equals(this.controllerPkg) || System.currentTimeMillis() - this.lastMediaSessionRouteAt >= 500) {
            return false;
        }
        return USE_BLUETOOTH_FOCUS_HOLD;
    }

    private void markMdcKeyHandle() {
        this.lastMdcKeyHandleMs = System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMdcHandledRecently() {
        if (System.currentTimeMillis() - this.lastMdcKeyHandleMs < 500) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        return false;
    }

    private void restoreMultimediaDisabledState() {
        try {
            this.weDisabledMultimedia = getSharedPreferences("carmedia_prefs", 0).getBoolean(PREF_KEY_WE_DISABLED_MM, false);
            if (this.weDisabledMultimedia) {
                Log.i(TAG, "restoreMultimediaDisabledState: 恢复 weDisabledMultimedia=true (上次蓝牙播放时由 CarMedia 禁用,重启后保持)");
            }
        } catch (Throwable t) {
            Log.w(TAG, "restoreMultimediaDisabledState error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeKillMultimediaOnBluetoothPlay() {
        try {
            boolean btPlayingNow = (!isBluetoothController() || this.mediaController == null || this.mediaController.getPlaybackState() == null || this.mediaController.getPlaybackState().getState() != 3) ? false : USE_BLUETOOTH_FOCUS_HOLD;
            if (btPlayingNow) {
                keepXcmediaOnBluetoothSource();
                pauseOtherSourcesOnBluetoothPlay();
                arbitrateEasFocusToMultimedia();
            }
            this.lastBtPlayingForKeep = btPlayingNow;
            isBluetoothController();
        } catch (Throwable t) {
            Log.w(TAG, "maybeKillMultimediaOnBluetoothPlay error: " + t.getMessage());
        }
    }

    private void disableMultimedia() {
        try {
            if (this.weDisabledMultimedia) {
                return;
            }
            if (isMultimediaDisabled()) {
                Log.i(TAG, ">>> 多媒体本来已禁用(用户主动),记录状态,后续不解禁");
                this.wasMultimediaDisabled = USE_BLUETOOTH_FOCUS_HOLD;
                return;
            }
            this.wasMultimediaDisabled = false;
            Log.i(TAG, ">>> 蓝牙播放中,禁用多媒体(com.ecarx.multimedia) 防其暂停蓝牙");
            PackageManager pm = getPackageManager();
            pm.setApplicationEnabledSetting(PKG_MULTIMEDIA, 3, 0);
            this.weDisabledMultimedia = USE_BLUETOOTH_FOCUS_HOLD;
            getSharedPreferences("carmedia_prefs", 0).edit().putBoolean(PREF_KEY_WE_DISABLED_MM, USE_BLUETOOTH_FOCUS_HOLD).apply();
            Log.i(TAG, ">>> 多媒体已禁用(蓝牙播放期间保持)");
        } catch (Throwable t1) {
            Log.w(TAG, ">>> setApplicationEnabledSetting 禁用失败,尝试 force-stop: " + t1.getMessage());
            forceStopMultimedia();
        }
    }

    private boolean isMultimediaDisabled() {
        try {
            PackageManager pm = getPackageManager();
            int state = pm.getApplicationEnabledSetting(PKG_MULTIMEDIA);
            if (state == 2 || state == 3) {
                return USE_BLUETOOTH_FOCUS_HOLD;
            }
            return false;
        } catch (Throwable t) {
            Log.w(TAG, "isMultimediaDisabled error: " + t.getMessage());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableMultimedia() {
        try {
            if (this.wasMultimediaDisabled) {
                Log.i(TAG, ">>> 多媒体原本就禁用(用户主动),保持禁用,不解禁");
                return;
            }
            if (this.weDisabledMultimedia) {
                Log.i(TAG, ">>> 蓝牙结束,解禁多媒体(com.ecarx.multimedia)");
                PackageManager pm = getPackageManager();
                pm.setApplicationEnabledSetting(PKG_MULTIMEDIA, 1, 0);
                this.weDisabledMultimedia = false;
                getSharedPreferences("carmedia_prefs", 0).edit().remove(PREF_KEY_WE_DISABLED_MM).apply();
                Log.i(TAG, ">>> 多媒体已解禁");
            }
        } catch (Throwable t1) {
            Log.w(TAG, ">>> setApplicationEnabledSetting 解禁失败: " + t1.getMessage());
        }
    }

    private void forceStopMultimedia() {
        try {
            Log.i(TAG, ">>> force-stop 多媒体(com.ecarx.multimedia) 防其暂停蓝牙");
            ActivityManager am = (ActivityManager) getSystemService("activity");
            if (am == null) {
                return;
            }
            Method m = ActivityManager.class.getMethod("forceStopPackage", String.class);
            m.invoke(am, PKG_MULTIMEDIA);
            Log.i(TAG, ">>> force-stop 多媒体成功");
        } catch (Throwable t1) {
            Log.w(TAG, ">>> forceStopPackage 反射失败: " + t1.getMessage());
        }
    }

    private void requestBluetoothFocusIfNeeded() {
        if (this.btFocusHeld) {
            return;
        }
        if (this.audioManager == null) {
            this.audioManager = (AudioManager) getSystemService("audio");
        }
        if (this.audioManager == null) {
            return;
        }
        try {
            int r = this.audioManager.requestAudioFocus(this.btFocusListener, 3, 3);
            boolean z = USE_BLUETOOTH_FOCUS_HOLD;
            if (r != 1) {
                z = false;
            }
            this.btFocusHeld = z;
            Log.i(TAG, ">>> request BT MAY_DUCK focus, result=" + r + ", held=" + this.btFocusHeld);
        } catch (Throwable t) {
            Log.w(TAG, "requestBluetoothFocusIfNeeded error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void abandonBluetoothFocus() {
        if (!this.btFocusHeld || this.audioManager == null) {
            return;
        }
        try {
            this.audioManager.abandonAudioFocus(this.btFocusListener);
            Log.i(TAG, ">>> abandon BT MAY_DUCK focus");
        } catch (Throwable t) {
            Log.w(TAG, "abandonBluetoothFocus error: " + t.getMessage());
        }
        this.btFocusHeld = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void keepXcmediaOnBluetoothSource() {
        long now = System.currentTimeMillis();
        if (now - this.lastBtSourceKeepMs < BT_SOURCE_KEEP_INTERVAL_MS) {
            return;
        }
        try {
            Intent i = new Intent("ecarx.intent.broadcast.action.ECARX_WIDGET_BLUETOOTH_PLAY");
            i.setPackage(PKG_MULTIMEDIA);
            sendBroadcast(i);
            this.lastBtSourceKeepMs = now;
            Log.i(TAG, ">>> keep XCMedia2 on BT source (ECARX_WIDGET_BLUETOOTH_PLAY)");
        } catch (Throwable t) {
            Log.w(TAG, "keepXcmediaOnBluetoothSource error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isBluetoothSessionActive() {
        boolean present;
        try {
            present = findControllerByPackage(PKG_BLUETOOTH) != null ? USE_BLUETOOTH_FOCUS_HOLD : false;
        } catch (Throwable th) {
            present = false;
        }
        MediaCenterBridge.setBluetoothSessionPresent(present);
        return present;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void arbitrateEasFocusToMultimedia() {
        long now = System.currentTimeMillis();
        if (now - this.lastEasArbitrateMs < 5000) {
            return;
        }
        try {
            if (MediaCenterBridge.get() != null) {
                MediaCenterBridge.get().arbitrateFocusToMultimedia();
                this.lastEasArbitrateMs = now;
            }
        } catch (Throwable t) {
            Log.w(TAG, "arbitrateEasFocusToMultimedia error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pauseOtherSourcesOnBluetoothPlay() {
        List<MediaController> sessions;
        PlaybackState ps;
        try {
            MediaSessionManager msm = (MediaSessionManager) getSystemService("media_session");
            if (msm == null || (sessions = msm.getActiveSessions(null)) == null) {
                return;
            }
            for (MediaController c : sessions) {
                String pkg = c.getPackageName();
                if (pkg != null && !PKG_SELF.equals(pkg) && !PKG_BLUETOOTH.equals(pkg) && !isHiddenController(pkg) && (ps = c.getPlaybackState()) != null && ps.getState() == 3) {
                    Log.i(TAG, ">>> pause other source while BT playing: " + pkg);
                    c.getTransportControls().pause();
                    if (!this.pausedOtherSources.contains(pkg)) {
                        this.pausedOtherSources.add(pkg);
                    }
                }
            }
        } catch (Throwable t) {
            Log.w(TAG, "pauseOtherSourcesOnBluetoothPlay error: " + t.getMessage());
        }
    }

    private void resumeOtherSourcesOnBluetoothStop() {
        List<MediaController> sessions;
        PlaybackState ps;
        try {
            if (this.pausedOtherSources.isEmpty()) {
                return;
            }
            if (isControllerReallyPlaying()) {
                this.pausedOtherSources.clear();
                return;
            }
            List<String> toResume = new ArrayList<>(this.pausedOtherSources);
            this.pausedOtherSources.clear();
            MediaSessionManager msm = (MediaSessionManager) getSystemService("media_session");
            if (msm == null || (sessions = msm.getActiveSessions(null)) == null) {
                return;
            }
            for (MediaController c : sessions) {
                String pkg = c.getPackageName();
                if (pkg != null && toResume.contains(pkg) && !pkg.equals(this.controllerPkg) && ((ps = c.getPlaybackState()) == null || ps.getState() != 3)) {
                    Log.i(TAG, ">>> resume other source after BT stop: " + pkg);
                    c.getTransportControls().play();
                }
            }
        } catch (Throwable t) {
            Log.w(TAG, "resumeOtherSourcesOnBluetoothStop error: " + t.getMessage());
        }
    }

    private boolean isTelecomController() {
        return PKG_TELECOM.equals(this.controllerPkg);
    }

    public void setAuthMessageListener(AuthMessageListener l) {
        this.mAuthListener = l;
    }

    private BroadcastReceiver createNeteaseLyricReceiver() {
        return new BroadcastReceiver() { // from class: com.ecarx.carmedia.CarMediaService.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action;
                if (intent == null) {
                    action = null;
                } else {
                    try {
                        action = intent.getAction();
                    } catch (Throwable t) {
                        Log.w(CarMediaService.TAG, "netease broadcast error: " + t.getMessage());
                        return;
                    }
                }
                if (action == null) {
                    return;
                }
                String pkg = intent.getStringExtra("package");
                if (pkg == null) {
                    pkg = intent.getStringExtra("pkg");
                }
                if (pkg == null) {
                    pkg = intent.getStringExtra("packagename");
                }
                if (pkg == null && intent.getComponent() != null) {
                    pkg = intent.getComponent().getPackageName();
                }
                if (pkg != null && pkg.equals(CarMediaService.this.controllerPkg)) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        String bTitle = CarMediaService.firstString(extras, "title", "songName", "songname", "track", "name", CarMediaService.METADATA_TITLE);
                        if (bTitle == null || bTitle.isEmpty()) {
                            bTitle = CarMediaService.this.currentTitle;
                        }
                        String lrc = null;
                        String line = null;
                        for (String key : CarMediaService.NETEASE_LYRIC_KEYS) {
                            Object v = extras.get(key);
                            if (v != null) {
                                String s = v.toString().trim();
                                if (!s.isEmpty()) {
                                    if (s.indexOf(91) != 0 && s.indexOf(10) < 0) {
                                        line = s;
                                    }
                                    lrc = s;
                                    break;
                                }
                            }
                        }
                        if (lrc != null || line != null) {
                            if (CarMediaService.titlesMatch(bTitle, CarMediaService.this.currentTitle)) {
                                CarMediaService.this.broadcastLyricPkg = pkg;
                                CarMediaService.this.broadcastLyricTitle = CarMediaService.this.currentTitle;
                                CarMediaService.this.broadcastLyricLrc = lrc;
                                CarMediaService.this.broadcastLyricLine = line;
                                if (lrc != null && !lrc.isEmpty()) {
                                    Log.i(CarMediaService.TAG, "Netease broadcast LRC received: " + lrc.length() + " chars");
                                    CarMediaService.this.pushBroadcastLyric(lrc, CarMediaService.USE_BLUETOOTH_FOCUS_HOLD);
                                } else if (line != null && !line.isEmpty()) {
                                    Log.i(CarMediaService.TAG, "Netease broadcast line: " + line);
                                    CarMediaService.this.pushBroadcastLyric(line, false);
                                }
                                CarMediaService.this.applyBroadcastMetadata(extras, bTitle, pkg);
                                return;
                            }
                            Log.d(CarMediaService.TAG, "netease broadcast lyric skipped (title mismatch): " + bTitle + " vs " + CarMediaService.this.currentTitle);
                            return;
                        }
                        CarMediaService.this.applyBroadcastMetadata(extras, bTitle, pkg);
                        return;
                    }
                    return;
                }
                Log.d(CarMediaService.TAG, "netease broadcast ignored: pkg=" + pkg + " controller=" + CarMediaService.this.controllerPkg);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String firstString(Bundle b, String... keys) {
        for (String k : keys) {
            Object v = b.get(k);
            if (v != null) {
                String s = v.toString().trim();
                if (!s.isEmpty()) {
                    return s;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean titlesMatch(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return TextUtil.normalize(a).equals(TextUtil.normalize(b));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyBroadcastMetadata(Bundle extras, String bTitle, String pkg) {
        try {
            if (!pkg.equals(this.controllerPkg)) {
                return;
            }
            String artist = firstString(extras, "artist", "singer", "author", METADATA_ARTIST);
            if (bTitle != null && !bTitle.isEmpty() && titlesMatch(bTitle, this.currentTitle)) {
                this.currentTitle = bTitle;
            }
            if (artist != null && !artist.isEmpty()) {
                this.currentArtist = artist;
                this.qqArtistComplete = USE_BLUETOOTH_FOCUS_HOLD;
            }
            if (isDimPushEnabled()) {
                pushMetadataToCard();
            }
        } catch (Throwable t) {
            Log.w(TAG, "applyBroadcastMetadata error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pushBroadcastLyric(String content, boolean isLrc) {
        try {
            if (isLyricBlocked(this.controllerPkg)) {
                Log.d(TAG, "pushBroadcastLyric: lyric blocked for " + this.controllerPkg + ", ignore broadcast lyric");
                return;
            }
            if (isLrc) {
                if (this.cloudLyricFetcher != null) {
                    this.cloudLyricFetcher.setBroadcastLrc(content, this.currentTitle, this.currentArtist);
                    Log.i(TAG, "Broadcast LRC bound to fetcher, starting poll");
                    startLyricPoll();
                    return;
                }
                return;
            }
            if (content.equals(this.lastPushedLyricLine)) {
                return;
            }
            String display = this.currentTitle;
            if (this.currentArtist != null && !this.currentArtist.isEmpty()) {
                display = display + " - " + this.currentArtist;
            }
            this.lastPushedLyricLine = content;
            this.lastPushedLyricState = 3;
            Log.i(TAG, "LyricPoll line=" + content);
            try {
                MediaCenterBridge.get().pushLyric(display, 0, content);
            } catch (Throwable t) {
                Log.e(TAG, "pushLyric exception: " + t.getMessage());
            }
        } catch (Throwable t2) {
            Log.w(TAG, "pushBroadcastLyric error: " + t2.getMessage());
        }
    }

    private void registerNeteaseLyricReceiver() {
        try {
            if (this.neteaseLyricReceiver != null) {
                return;
            }
            this.neteaseLyricReceiver = createNeteaseLyricReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.netease.cloudmusic.metachanged");
            filter.addAction("com.netease.cloudmusic.lyricchanged");
            filter.addAction("com.android.music.metachanged");
            filter.addAction("com.android.music.lyricchanged");
            filter.addAction("com.android.music.playstatechanged");
            registerReceiver(this.neteaseLyricReceiver, filter);
            Log.i(TAG, "netease lyric receiver registered");
        } catch (Throwable t) {
            Log.w(TAG, "register netease receiver failed: " + t.getMessage());
        }
    }

    private void unregisterNeteaseLyricReceiver() {
        if (this.neteaseLyricReceiver != null) {
            try {
                unregisterReceiver(this.neteaseLyricReceiver);
            } catch (Throwable t) {
                Log.w(TAG, "unregister netease receiver error: " + t.getMessage());
            }
            this.neteaseLyricReceiver = null;
        }
    }

    private void registerA2dpSinkReceiver() {
        try {
            if (this.a2dpSinkReceiver != null) {
                return;
            }
            this.a2dpSinkReceiver = new BroadcastReceiver() { // from class: com.ecarx.carmedia.CarMediaService.6
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (intent == null) {
                        return;
                    }
                    String action = intent.getAction();
                    if (action == null) {
                        return;
                    }
                    int state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
                    Log.i(CarMediaService.TAG, "a2dp-sink state changed: " + state);
                    if (state == 2) {
                        CarMediaService.this.firstPushHandler.postDelayed(new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.6.1
                            @Override // java.lang.Runnable
                            public void run() {
                                Log.i(CarMediaService.TAG, "a2dp-sink reconnected, refresh controllers");
                                CarMediaService.this.refreshControllerList();
                                if (!CarMediaService.this.isBluetoothController()) {
                                    MediaController reallyPlaying = CarMediaService.this.findReallyPlayingController();
                                    if (reallyPlaying == null || CarMediaService.PKG_BLUETOOTH.equals(reallyPlaying.getPackageName())) {
                                        MediaController btController = CarMediaService.this.findControllerByPackage(CarMediaService.PKG_BLUETOOTH);
                                        if (btController != null) {
                                            Log.i(CarMediaService.TAG, "a2dp-sink: select bluetooth controller");
                                            CarMediaService.this.selectController(btController);
                                            return;
                                        }
                                        return;
                                    }
                                    Log.i(CarMediaService.TAG, "a2dp-sink: another source really playing (" + reallyPlaying.getPackageName() + "), keep current controller, don't switch to bluetooth");
                                }
                            }
                        }, CarMediaService.AUTO_SWITCH_COOLDOWN_MS);
                    } else {
                        Log.i(CarMediaService.TAG, "a2dp-sink disconnected, schedule re-enable multimedia in 5s");
                        CarMediaService.this.firstPushHandler.postDelayed(new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.6.2
                            @Override // java.lang.Runnable
                            public void run() {
                                CarMediaService.this.abandonBluetoothFocus();
                                CarMediaService.this.enableMultimedia();
                            }
                        }, 5000L);
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED");
            registerReceiver(this.a2dpSinkReceiver, filter);
            Log.i(TAG, "a2dp-sink receiver registered");
        } catch (Throwable t) {
            Log.w(TAG, "register a2dp-sink receiver failed: " + t.getMessage());
        }
    }

    private void unregisterA2dpSinkReceiver() {
        if (this.a2dpSinkReceiver != null) {
            try {
                unregisterReceiver(this.a2dpSinkReceiver);
            } catch (Throwable t) {
                Log.w(TAG, "unregister a2dp-sink receiver error: " + t.getMessage());
            }
            this.a2dpSinkReceiver = null;
        }
    }

    private void tryResumeAfterVoip() {
        this.firstPushHandler.postDelayed(new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (CarMediaService.this.isBluetoothController()) {
                        if (CarMediaService.this.mediaController != null) {
                            PlaybackState ps = CarMediaService.this.mediaController.getPlaybackState();
                            if (ps == null || ps.getState() != 3) {
                                if (CarMediaService.this.voipActive) {
                                    Log.d(CarMediaService.TAG, "voip-resume: voip still active, skip");
                                    return;
                                } else {
                                    Log.i(CarMediaService.TAG, "voip-resume: calling play() on " + CarMediaService.this.controllerPkg);
                                    CarMediaService.this.mediaController.getTransportControls().play();
                                    return;
                                }
                            }
                            Log.d(CarMediaService.TAG, "voip-resume: already playing, skip");
                            return;
                        }
                        Log.d(CarMediaService.TAG, "voip-resume: no controller, skip");
                        return;
                    }
                    Log.d(CarMediaService.TAG, "voip-resume: not bluetooth controller, skip");
                } catch (Throwable t) {
                    Log.w(CarMediaService.TAG, "voip-resume error: " + t.getMessage());
                }
            }
        }, POSITION_TICKER_INTERVAL_MS);
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public CarMediaService getService() {
            return CarMediaService.this;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Log.i(TAG, "CarMediaService onCreate");
        startForegroundNotification();
        initSessionManager();
        this.kuwoAidlClient = new KuwoAidlClient(this);
        this.kuwoAidlClient.setOnPosListener(new KuwoAidlClient.OnPosListener() { // from class: com.ecarx.carmedia.CarMediaService.10
            @Override // com.ecarx.carmedia.KuwoAidlClient.OnPosListener
            public void onPosReady() {
                CarMediaService.this.lastNonBtPositionMs = 0L;
                CarMediaService.this.lastNonBtPositionTime = 0L;
                Log.i(CarMediaService.TAG, "kuwo AIDL pos ready, reset non-bt accumulator");
            }
        });
        initLyricFetchers();
        initKeyMonitor();
        grantNotificationPermission();
        restoreDimAutoPush();
        restoreLyricBlockedList();
        restoreMultimediaDisabledState();
        initAudioPlaybackMonitor();
        registerA2dpSinkReceiver();
        registerNeteaseLyricReceiver();
        try {
            MediaCenterBridge.get().init(this);
            MediaCenterBridge.setController(this);
        } catch (Throwable t) {
            Log.w(TAG, "EAS bridge init failed: " + t.getMessage());
        }
        refreshController();
        startFirstPushRetry();
        startEasFocusGuard();
        Log.i(TAG, "CarMediaService onCreate done");
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: START_STICKY");
        if (intent != null && "com.ecarx.carmedia.DEBUG_SODA_LYRIC".equals(intent.getAction())) {
            String tid = intent.getStringExtra("track_id");
            if (tid != null && !tid.isEmpty() && this.sodaMusicFetcher != null) {
                this.sodaMusicFetcher.fetchByTrackId(tid, this.currentTitle, this.currentArtist);
                Log.i(TAG, "onStartCommand: DEBUG_SODA_LYRIC track_id=" + tid);
            }
            return 1;
        }
        if (intent == null) {
            Log.i(TAG, "onStartCommand: restarted by system (intent==null), service already initialized in onCreate");
        }
        startFirstPushRetry();
        return 1;
    }

    public void onEasRegistered() {
        startFirstPushRetry();
    }

    private void startEasFocusGuard() {
        this.easFocusHandler.removeCallbacks(this.easFocusRunnable);
        this.easFocusHandler.postDelayed(this.easFocusRunnable, POSITION_TICKER_INTERVAL_MS);
    }

    private boolean shouldHoldEasFocus() {
        if (this.mediaController == null) {
            return false;
        }
        try {
            if (isBluetoothController()) {
                PlaybackState ps = this.mediaController.getPlaybackState();
                if (ps == null || ps.getState() != 3) {
                    return false;
                }
                return USE_BLUETOOTH_FOCUS_HOLD;
            }
            if (getAudioRealPlayState() == 3) {
                return USE_BLUETOOTH_FOCUS_HOLD;
            }
            return false;
        } catch (Throwable th) {
            return false;
        }
    }

    public void onEasFocusLost() {
        try {
            if (MediaCenterBridge.get() != null) {
                MediaCenterBridge.get().logFocusOnce();
            }
        } catch (Throwable t) {
            Log.w(TAG, "onEasFocusLost error: " + t.getMessage());
        }
    }

    private void startFirstPushRetry() {
        this.firstPushRetryCount = 0;
        this.firstPushHandler.removeCallbacks(this.firstPushRunnable);
        if (this.firstPushPending && isDimPushEnabled()) {
            this.firstPushHandler.post(this.firstPushRunnable);
        }
    }

    @Override // android.app.Service
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "CarMediaService onTaskRemoved: restarting foreground service");
        try {
            Intent restart = new Intent(getApplicationContext(), (Class<?>) CarMediaService.class);
            startForegroundService(restart);
        } catch (Throwable t) {
            Log.w(TAG, "onTaskRemoved restart failed: " + t.getMessage());
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.i(TAG, "CarMediaService onDestroy");
        sInstance = null;
        unregisterAudioPlaybackMonitor();
        unregisterA2dpSinkReceiver();
        unregisterSessionChangedListener();
        unregisterControllerCallback();
        this.keyMonitor = null;
        stopLogcatKeyMonitor();
        unbindQqMusicFetcher();
        if (this.kuwoAidlClient != null) {
            this.kuwoAidlClient.stop();
            this.kuwoAidlClient = null;
        }
        stopLyricPoll();
        stopPositionTicker();
        unregisterNeteaseLyricReceiver();
        abandonBluetoothFocus();
        this.firstPushHandler.removeCallbacks(this.firstPushRunnable);
        super.onDestroy();
    }

    private void startForegroundNotification() {
        try {
            NotificationManager nm = (NotificationManager) getSystemService(NotificationManager.class);
            if (nm != null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "CarMedia Service", 2);
                channel.setDescription("CarMedia Foreground Service");
                channel.setShowBadge(false);
                nm.createNotificationChannel(channel);
            }
            Intent main = new Intent(this, (Class<?>) MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, main, 201326592);
            Notification notification = new Notification.Builder(this, CHANNEL_ID).setContentTitle(getString(R.string.service_notification_title)).setContentText(getString(R.string.service_notification_text)).setSmallIcon(R.mipmap.ic_notification).setContentIntent(pi).setOngoing(USE_BLUETOOTH_FOCUS_HOLD).build();
            startForeground(1, notification);
        } catch (Throwable t) {
            Log.w(TAG, "startForegroundNotification failed: " + t.getMessage());
        }
    }

    private void initSessionManager() {
        try {
            this.sessionManager = (MediaSessionManager) getSystemService("media_session");
            if (this.sessionManager != null) {
                ComponentName cn = new ComponentName(this, (Class<?>) MediaNotificationListener.class);
                this.sessionManager.addOnActiveSessionsChangedListener(this.sessionChangedListener, cn);
                Log.i(TAG, ">>> session change listener registered");
            }
        } catch (Throwable t) {
            Log.w(TAG, ">>> session listener register failed: " + t.getMessage());
        }
    }

    private void unregisterSessionChangedListener() {
        try {
            if (this.sessionManager != null && this.sessionChangedListener != null) {
                this.sessionManager.removeOnActiveSessionsChangedListener(this.sessionChangedListener);
            }
        } catch (Throwable t) {
            Log.w(TAG, "session listener unregister failed: " + t.getMessage());
        }
    }

    private void initMediaSession() {
        try {
            this.mediaSession = new MediaSession(this, "CarMediaSession");
            this.mediaSession.setFlags(3);
            PlaybackState state = new PlaybackState.Builder().setState(0, -1L, 1.0f).setActions(945L).build();
            this.mediaSession.setPlaybackState(state);
            this.mediaSession.setActive(USE_BLUETOOTH_FOCUS_HOLD);
            Log.i(TAG, "MediaSession registered & active");
        } catch (Throwable t) {
            Log.w(TAG, "initMediaSession failed: " + t.getMessage());
        }
    }

    private void releaseMediaSession() {
        if (this.mediaSession != null) {
            this.mediaSession.release();
            this.mediaSession = null;
        }
    }

    private void grantNotificationPermission() {
        boolean ok = NotificationPermissionHelper.grantNotificationListenerPermission(this);
        Log.i(TAG, "grantNotificationListenerPermission result=" + ok);
    }

    private void restoreDimAutoPush() {
        try {
            this.dimAutoPush = getSharedPreferences("carmedia_prefs", 0).getBoolean("dim_auto_push", USE_BLUETOOTH_FOCUS_HOLD);
        } catch (Throwable th) {
            this.dimAutoPush = USE_BLUETOOTH_FOCUS_HOLD;
        }
    }

    private void restoreLyricBlockedList() {
        try {
            String s = getSharedPreferences("carmedia_prefs", 0).getString("lyric_blocked_pkgs", "");
            this.lyricBlockedPkgs.clear();
            if (s != null && !s.isEmpty()) {
                String[] arr = s.split(",");
                for (String str : arr) {
                    String p = str.trim();
                    if (!p.isEmpty()) {
                        this.lyricBlockedPkgs.add(p);
                    }
                }
            }
            Log.i(TAG, "restoreLyricBlockedList: " + this.lyricBlockedPkgs);
        } catch (Throwable t) {
            Log.w(TAG, "restoreLyricBlockedList error: " + t.getMessage());
        }
    }

    public boolean isLyricBlocked(String pkg) {
        if (pkg == null) {
            return false;
        }
        return this.lyricBlockedPkgs.contains(pkg);
    }

    public void setLyricBlocked(String pkg, boolean blocked) {
        if (pkg == null || pkg.isEmpty()) {
            return;
        }
        if (blocked) {
            this.lyricBlockedPkgs.add(pkg);
        } else {
            this.lyricBlockedPkgs.remove(pkg);
        }
        if (pkg.equals(this.controllerPkg)) {
            if (blocked) {
                clearCurrentLyrics();
            } else {
                refetchLyricsForCurrent();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String p : this.lyricBlockedPkgs) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(p);
        }
        try {
            getSharedPreferences("carmedia_prefs", 0).edit().putString("lyric_blocked_pkgs", sb.toString()).apply();
        } catch (Throwable th) {
        }
        Log.i(TAG, "setLyricBlocked: " + pkg + " -> " + blocked + ", list=" + this.lyricBlockedPkgs);
    }

    private void clearCurrentLyrics() {
        try {
            if (this.cloudLyricFetcher != null) {
                this.cloudLyricFetcher.clearBroadcastLrc();
                this.cloudLyricFetcher.clearLoadedLrc();
                this.cloudLyricFetcher.resetApiStatus();
            }
            if (this.qqMusicFetcher != null) {
                this.qqMusicFetcher.resetApiStatus();
            }
            this.lastPushedLyricLine = null;
            this.lastPushedLyricForTitle = null;
        } catch (Throwable t) {
            Log.w(TAG, "clearCurrentLyrics error: " + t.getMessage());
        }
    }

    private void refetchLyricsForCurrent() {
        try {
            if (this.currentTitle != null && !this.currentTitle.isEmpty() && !isLyricBlocked(this.controllerPkg)) {
                Log.i(TAG, "refetchLyricsForCurrent: title=" + this.currentTitle + " artist=" + this.currentArtist + " sourceType=" + this.currentSourceType);
                if (this.currentSourceType == 1 && this.qqMusicFetcher != null) {
                    this.qqMusicFetcher.bind();
                    this.qqMusicFetcher.fetchCurrentSong();
                } else if (this.currentSourceType == 2 && this.cloudLyricFetcher != null) {
                    this.cloudLyricFetcher.search(this.currentTitle, this.currentArtist);
                } else if (this.currentSourceType == 3 && this.cloudLyricFetcher != null) {
                    this.cloudLyricFetcher.searchXmf2Only(this.currentTitle, this.currentArtist);
                }
            }
        } catch (Throwable t) {
            Log.w(TAG, "refetchLyricsForCurrent error: " + t.getMessage());
        }
    }

    private void initLyricFetchers() {
        try {
            this.cloudLyricFetcher = new CloudLyricFetcher(new CloudLyricCallback());
            this.qqMusicFetcher = new QQMusicLyricFetcher(this, new QQMusicLyricCallback());
            this.sodaMusicFetcher = new SodaMusicLyricFetcher(new SodaMusicLyricCallback());
            this.lyricPollHandler = new Handler(Looper.getMainLooper());
            Log.i(TAG, "initLyricFetchers done");
        } catch (Throwable t) {
            Log.w(TAG, "initLyricFetchers failed: " + t.getMessage());
        }
    }

    private class SodaMusicLyricCallback implements SodaMusicLyricFetcher.Callback {
        private SodaMusicLyricCallback() {
        }

        @Override // com.ecarx.carmedia.SodaMusicLyricFetcher.Callback
        public void onLyricReady(String lyric, String title, String artist) {
            try {
                CarMediaService.this.onSodaLyricReady(lyric, title, artist);
            } catch (Throwable t) {
                Log.w(CarMediaService.TAG, "onSodaLyricReady error: " + t.getMessage());
            }
        }

        @Override // com.ecarx.carmedia.SodaMusicLyricFetcher.Callback
        public void onError(String message) {
            Log.w(CarMediaService.TAG, "SodaMusic lyric error: " + message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSodaLyricReady(String lyric, String title, String artist) {
        if (isLyricBlocked(this.controllerPkg)) {
            Log.d(TAG, "onSodaLyricReady: lyric blocked for " + this.controllerPkg + ", ignore callback");
        } else {
            Log.i(TAG, "SODA lyric loaded: title=" + title + " artist=" + artist + " pkg=" + this.controllerPkg + " sourceType=" + this.currentSourceType);
        }
    }

    public JSONObject getApiStatus() {
        JSONObject o = new JSONObject();
        try {
            if (this.cloudLyricFetcher != null) {
                JSONObject c = this.cloudLyricFetcher.getApiStatus();
                Iterator<String> it = c.keys();
                while (it.hasNext()) {
                    String k = it.next();
                    o.put(k, c.optJSONObject(k));
                }
            }
            if (this.qqMusicFetcher != null) {
                JSONObject q = this.qqMusicFetcher.getApiStatus();
                JSONObject qq = new JSONObject();
                qq.put("status", q.optInt("status"));
                qq.put("msg", q.optString("msg"));
                o.put("qq_aidl", qq);
            }
            if (this.sodaMusicFetcher != null) {
                JSONObject s = new JSONObject();
                s.put("status", this.sodaMusicFetcher.getApiState());
                s.put("msg", this.sodaMusicFetcher.getApiMsg() != null ? this.sodaMusicFetcher.getApiMsg() : "");
                o.put("soda", s);
            }
        } catch (Throwable t) {
            Log.w(TAG, "getApiStatus error: " + t.getMessage());
        }
        return o;
    }

    private class QQMusicLyricCallback implements QQMusicLyricFetcher.Callback {
        private QQMusicLyricCallback() {
        }

        @Override // com.ecarx.carmedia.QQMusicLyricFetcher.Callback
        public void onLyricReady(String lyric, String title, String artist) {
            try {
                CarMediaService.this.onQqLyricReady(lyric, title, artist);
            } catch (Throwable t) {
                Log.w(CarMediaService.TAG, "onQqLyricReady error: " + t.getMessage());
            }
        }

        @Override // com.ecarx.carmedia.QQMusicLyricFetcher.Callback
        public void onError(String message) {
            Log.w(CarMediaService.TAG, "QQMusic lyric error: " + message);
            boolean qqRestarting = false;
            boolean qqHasLyric = CarMediaService.this.qqMusicFetcher != null && CarMediaService.this.qqMusicFetcher.hasLyric();
            if (CarMediaService.this.qqMusicFetcher != null && CarMediaService.this.qqMusicFetcher.isRestarting()) {
                qqRestarting = true;
            }
            if (CarMediaService.PKG_QQ_MUSIC.equals(CarMediaService.this.controllerPkg) && CarMediaService.this.currentSourceType == 1 && CarMediaService.this.cloudLyricFetcher != null && CarMediaService.this.currentTitle != null && !CarMediaService.this.currentTitle.isEmpty() && !CarMediaService.this.isLyricBlocked(CarMediaService.this.controllerPkg) && !qqHasLyric && !qqRestarting) {
                Log.i(CarMediaService.TAG, "QQ lyric failed, fallback to xmf2: " + CarMediaService.this.currentTitle);
                CarMediaService.this.cloudLyricFetcher.searchXmf2Only(CarMediaService.this.currentTitle, CarMediaService.this.currentArtist);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x00b3 A[Catch: all -> 0x017e, TRY_ENTER, TryCatch #0 {all -> 0x017e, blocks: (B:6:0x000c, B:10:0x0016, B:12:0x001e, B:16:0x004b, B:18:0x0053, B:19:0x0057, B:21:0x005d, B:24:0x0064, B:26:0x006e, B:28:0x0072, B:30:0x007a, B:32:0x0084, B:36:0x0093, B:42:0x00a2, B:44:0x00a8, B:48:0x00b3, B:50:0x00eb, B:53:0x00f2, B:55:0x00f8, B:57:0x0115, B:59:0x0119, B:61:0x0123, B:64:0x015c, B:66:0x0166, B:68:0x016e, B:72:0x017a), top: B:5:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0115 A[Catch: all -> 0x017e, TryCatch #0 {all -> 0x017e, blocks: (B:6:0x000c, B:10:0x0016, B:12:0x001e, B:16:0x004b, B:18:0x0053, B:19:0x0057, B:21:0x005d, B:24:0x0064, B:26:0x006e, B:28:0x0072, B:30:0x007a, B:32:0x0084, B:36:0x0093, B:42:0x00a2, B:44:0x00a8, B:48:0x00b3, B:50:0x00eb, B:53:0x00f2, B:55:0x00f8, B:57:0x0115, B:59:0x0119, B:61:0x0123, B:64:0x015c, B:66:0x0166, B:68:0x016e, B:72:0x017a), top: B:5:0x000c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void applyNotificationMeta(java.lang.String r17, java.lang.String r18, java.lang.String r19, int r20, java.lang.String r21) {
        /*
            Method dump skipped, instructions count: 411
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.CarMediaService.applyNotificationMeta(java.lang.String, java.lang.String, java.lang.String, int, java.lang.String):void");
    }

    private boolean notifActive() {
        if (this.notifArtist == null && this.notifTitle == null) {
            return false;
        }
        return this.notifPkg.equals(this.controllerPkg);
    }

    public void onNotificationRemoved(String pkg) {
        if (pkg == null) {
            return;
        }
        try {
            if (pkg.equals(this.controllerPkg) || pkg.equals(this.notifPkg)) {
                Log.i(TAG, "notification removed: pkg=" + pkg + ", clearing notif snapshot");
                this.notifPkg = null;
                this.notifTitle = null;
                this.notifArtist = null;
            }
        } catch (Throwable t) {
            Log.w(TAG, "onNotificationRemoved error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onQqLyricReady(String lyric, String title, String artist) {
        if (isLyricBlocked(this.controllerPkg)) {
            Log.d(TAG, "onQqLyricReady: lyric blocked for " + this.controllerPkg + ", discard");
            return;
        }
        if (lyric == null || lyric.isEmpty()) {
            if (this.currentTitle != null && !this.currentTitle.isEmpty() && this.cloudLyricFetcher != null && !isLyricBlocked(this.controllerPkg)) {
                Log.i(TAG, "QQ lyric empty, fallback to xmf2: " + this.currentTitle);
                this.cloudLyricFetcher.searchXmf2Only(this.currentTitle, this.currentArtist);
                return;
            }
            return;
        }
        String displayName = (title == null || title.isEmpty()) ? "?" : title;
        if (artist != null && !artist.isEmpty()) {
            displayName = displayName + " - " + artist;
        }
        String lrcTitle = "";
        String lrcArtist = "";
        if (lyric != null && !lyric.isEmpty()) {
            String[] rows = lyric.split("\n");
            for (String row : rows) {
                String s = row.trim();
                if (!s.isEmpty() && !s.startsWith("[ti:") && !s.startsWith("[ar:") && !s.startsWith("[al:") && !s.startsWith("[by:") && !s.startsWith("[offset:")) {
                    int close = s.indexOf(93);
                    if (close >= 0) {
                        String v = s.substring(close + 1).trim();
                        if (!v.isEmpty()) {
                            int sep = v.lastIndexOf(" - ");
                            if (sep > 0) {
                                String lrcTitle2 = v.substring(0, sep).trim();
                                lrcArtist = v.substring(sep + 3).trim();
                                lrcTitle = lrcTitle2;
                            } else {
                                lrcTitle = v;
                            }
                        }
                    }
                    if (!lrcTitle.isEmpty()) {
                        break;
                    }
                }
            }
        }
        if (lrcTitle.isEmpty()) {
            lrcTitle = (title == null || title.isEmpty()) ? "" : title;
        }
        if (lrcArtist.isEmpty() && artist != null && !artist.isEmpty()) {
            lrcArtist = artist;
        }
        if (!notifActive() || this.notifTitle == null || this.notifTitle.isEmpty()) {
            this.currentTitle = lrcTitle;
        }
        Log.d(TAG, "QQ lyric loaded for title=" + this.currentTitle);
        if (!lrcArtist.isEmpty()) {
            if (!notifActive()) {
                this.currentArtist = lrcArtist;
                this.qqArtistComplete = USE_BLUETOOTH_FOCUS_HOLD;
            }
        } else {
            this.currentArtist = displayName;
            this.qqArtistComplete = false;
        }
        Log.i(TAG, "onQqLyricReady: title=" + this.currentTitle + " artist=" + this.currentArtist);
        if (isDimPushEnabled()) {
            pushMetadataToCard();
        }
    }

    private class CloudLyricCallback implements CloudLyricFetcher.Callback {
        private CloudLyricCallback() {
        }

        @Override // com.ecarx.carmedia.CloudLyricFetcher.Callback
        public void onLyricReady(String lyric, String title, String artist) {
            try {
                CarMediaService.this.onCloudLyricReady(lyric, title, artist);
            } catch (Throwable t) {
                Log.w(CarMediaService.TAG, "onCloudLyricReady error: " + t.getMessage());
            }
        }

        @Override // com.ecarx.carmedia.CloudLyricFetcher.Callback
        public void onError(String message) {
            Log.w(CarMediaService.TAG, "Netease lyric error: " + message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCloudLyricReady(String lyric, String title, String artist) {
        if (isLyricBlocked(this.controllerPkg)) {
            Log.d(TAG, "onCloudLyricReady: lyric blocked for " + this.controllerPkg + ", ignore callback");
            return;
        }
        if (artist != null && !notifActive()) {
            String clean = artist.replace('\\', '/');
            StringBuilder sb = new StringBuilder(clean.length());
            for (int i = 0; i < clean.length(); i++) {
                char c = clean.charAt(i);
                if (c >= ' ' && c != 127) {
                    sb.append(c);
                }
            }
            this.currentArtist = sb.toString();
            Log.i(TAG, "CLOUD_ARTIST=" + this.currentArtist);
        }
        if (title != null && title.equals(this.currentTitle) && !notifActive()) {
            this.currentTitle = title;
        }
        if (title != null && title.equals(this.currentTitle)) {
            Log.d(TAG, "CLOUD lyric loaded for title=" + title);
        }
    }

    private void startLyricPoll() {
        if (this.lyricPollHandler == null || this.lyricPollRunnable == null) {
            return;
        }
        this.lyricPollHandler.removeCallbacks(this.lyricPollRunnable);
        this.lyricPollHandler.postDelayed(this.lyricPollRunnable, 500L);
    }

    private void stopLyricPoll() {
        if (this.lyricPollHandler == null || this.lyricPollRunnable == null) {
            return;
        }
        this.lyricPollHandler.removeCallbacks(this.lyricPollRunnable);
    }

    private void unbindQqMusicFetcher() {
        if (this.qqMusicFetcher != null) {
            try {
                this.qqMusicFetcher.unbind();
            } catch (Throwable t) {
                Log.w(TAG, "unbind qqMusicFetcher error: " + t.getMessage());
            }
        }
    }

    public void scheduleLyricPoll(Runnable r) {
        if (this.lyricPollHandler != null && r != null) {
            this.lyricPollHandler.postDelayed(r, 500L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean pollLyricOnce() {
        boolean shouldPush;
        if (1 == 0) {
            Log.d(TAG, "LyricPoll: stopped (controller not supported for lyric)");
            checkContentChangedAndPush();
            return false;
        }
        if (this.mediaController == null) {
            return false;
        }
        PlaybackState ps = this.mediaController.getPlaybackState();
        if (ps == null) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        ps.getPosition();
        int state = ps.getState();
        long position = getPlaybackPosition();
        if (position == this.lastLyricPosition) {
            Log.d(TAG, "LyricPoll: pos unchanged, still checking lyric");
        } else {
            this.lastLyricPosition = position;
        }
        String line = getBoundLyricLine();
        if (line == null) {
            Log.d(TAG, "LyricPoll: lyric not bound yet (switching?), skip lyric push");
            checkContentChangedAndPush();
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        if (line.isEmpty() || this.currentTitle == null || this.currentTitle.isEmpty()) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        if (this.lastPushedLyricLine == null) {
            shouldPush = USE_BLUETOOTH_FOCUS_HOLD;
        } else if (!line.equals(this.lastPushedLyricLine)) {
            shouldPush = USE_BLUETOOTH_FOCUS_HOLD;
        } else if (this.lastPushedLyricState == 3 && state == 3) {
            shouldPush = USE_BLUETOOTH_FOCUS_HOLD;
        } else {
            shouldPush = false;
        }
        if (shouldPush && isDimPushEnabled()) {
            this.lastPushedLyricLine = line;
            this.lastPushedLyricState = state;
            String display = this.currentTitle;
            if (this.currentArtist != null && !this.currentArtist.isEmpty()) {
                display = display + " - " + this.currentArtist;
            }
            Log.i(TAG, "LyricPoll line=" + line);
            try {
                MediaCenterBridge.get().pushLyric(display, 0, line);
            } catch (Throwable t) {
                Log.e(TAG, "LyricPoll pushLyric exception: " + t.getMessage());
            }
        }
        checkContentChangedAndPush();
        return USE_BLUETOOTH_FOCUS_HOLD;
    }

    public String getCurrentLyricLine() {
        try {
            return getBoundLyricLine();
        } catch (Exception e) {
            Log.w(TAG, "getCurrentLyricLine error: " + e.getMessage());
            return null;
        }
    }

    public String getDisplayTitle() {
        try {
            String line = getBoundLyricLine();
            if (line != null) {
                if (!line.isEmpty()) {
                    return line;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "getDisplayTitle error: " + e.getMessage());
        }
        return this.currentTitle;
    }

    public String getDisplayArtist() {
        try {
            String line = getBoundLyricLine();
            if (line != null && !line.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                if (this.currentTitle != null) {
                    sb.append(this.currentTitle);
                }
                sb.append(" - ");
                if (this.currentArtist != null) {
                    sb.append(this.currentArtist);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            Log.w(TAG, "getDisplayArtist error: " + e.getMessage());
        }
        return this.currentArtist;
    }

    public void refreshController() {
        if (this.ghostControllerPkg != null) {
            Log.d(TAG, "refreshController: ghost controller active, skip");
            return;
        }
        this.controllerStale = false;
        unregisterControllerCallback();
        this.mediaController = null;
        updateAvailableControllers();
    }

    public boolean isSourceReallyPlaying(String pkg) {
        return isAudioReallyPlaying(pkg);
    }

    public boolean switchToController(String pkg) {
        MediaController ctrl = findControllerByPackage(pkg);
        if (ctrl != null) {
            Log.i(TAG, "switchToController: directly switch to " + pkg);
            selectController(ctrl);
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        Log.w(TAG, "switchToController: no active session for " + pkg);
        return false;
    }

    public void refreshControllerList() {
        List<MediaController> all;
        MediaSessionManager msm = (MediaSessionManager) getSystemService("media_session");
        if (msm == null) {
            return;
        }
        List<MediaController> fresh = new ArrayList<>();
        try {
            ComponentName cn = new ComponentName(this, (Class<?>) MediaNotificationListener.class);
            List<MediaController> sessions = msm.getActiveSessions(cn);
            if (sessions != null) {
                for (MediaController c : sessions) {
                    String pkg = c.getPackageName();
                    if (pkg != null && !pkg.equals(PKG_SELF) && !isHiddenController(pkg)) {
                        fresh.add(c);
                    }
                }
            }
            if (fresh.isEmpty() && (all = msm.getActiveSessions(null)) != null) {
                for (MediaController c2 : all) {
                    String pkg2 = c2.getPackageName();
                    if (pkg2 != null && !pkg2.equals(PKG_SELF) && !isHiddenController(pkg2)) {
                        fresh.add(c2);
                    }
                }
            }
            mergeKnownControllers(fresh);
        } catch (Exception e) {
            Log.w(TAG, "refreshControllerList error: " + e.getMessage());
        }
        if (!fresh.isEmpty()) {
            this.availableControllers = fresh;
        }
    }

    private List<MediaController> getKnownControllers() {
        MediaSessionManager msm;
        List<MediaController> result = new ArrayList<>();
        msm = null;
        try {
            msm = (MediaSessionManager) getSystemService("media_session");
        } catch (Throwable t) {
            Log.w(TAG, "getKnownControllers error: " + t.getMessage());
        }
        if (msm == null) {
            return result;
        }
        Object sessions = null;
        try {
            Method m = MediaSessionManager.class.getMethod("getSessions", ComponentName.class);
            sessions = m.invoke(msm, null);
        } catch (Throwable th) {
            try {
                Method m2 = MediaSessionManager.class.getMethod("getSessions", new Class[0]);
                sessions = m2.invoke(msm, new Object[0]);
            } catch (Throwable t2) {
                Log.w(TAG, "getKnownControllers: public getSessions unavailable, fallback to binder: " + t2.getMessage());
            }
        }
        if (sessions == null) {
            sessions = getSessionsViaBinder(msm);
        }
        if (sessions instanceof List) {
            for (Object o : (List) sessions) {
                if (o != null) {
                    if (o instanceof MediaController) {
                        result.add((MediaController) o);
                    } else if (o instanceof MediaSession.Token) {
                        try {
                            result.add(new MediaController(this, (MediaSession.Token) o));
                        } catch (Throwable t3) {
                            Log.w(TAG, "getKnownControllers: token->controller fail: " + t3.getMessage());
                        }
                    } else if (o instanceof IBinder) {
                        MediaController mc = controllerFromBinder((IBinder) o);
                        if (mc != null) {
                            result.add(mc);
                            Log.d(TAG, "getKnownControllers: binder->controller pkg=" + mc.getPackageName());
                        }
                    } else {
                        Log.d(TAG, "getKnownControllers: unknown element class=" + o.getClass().getName());
                    }
                }
            }
        }
        return result;
    }

    private MediaController controllerFromBinder(IBinder binder) {
        try {
            Parcel p = Parcel.obtain();
            try {
                p.writeStrongBinder(binder);
                p.setDataPosition(0);
                MediaSession.Token token = (MediaSession.Token) MediaSession.Token.CREATOR.createFromParcel(p);
                return new MediaController(this, token);
            } finally {
                p.recycle();
            }
        } catch (Throwable t) {
            Log.w(TAG, "controllerFromBinder error: " + t.getMessage());
            return null;
        }
    }

    private Object getSessionsViaBinder(MediaSessionManager msm) {
        try {
            Field f = MediaSessionManager.class.getDeclaredField("mService");
            f.setAccessible(USE_BLUETOOTH_FOCUS_HOLD);
            try {
                Object svc = f.get(msm);
                Log.d(TAG, "getSessionsViaBinder: mService=" + svc);
                if (svc == null) {
                    return null;
                }
                try {
                    Method m = svc.getClass().getMethod("getSessions", ComponentName.class, Integer.TYPE);
                    int userId = 0;
                    try {
                        Method myUserId = UserHandle.class.getMethod("myUserId", new Class[0]);
                        userId = ((Integer) myUserId.invoke(null, new Object[0])).intValue();
                    } catch (Throwable ue) {
                        Log.w(TAG, "getSessionsViaBinder: myUserId reflect fail, use 0: " + ue.getMessage());
                    }
                    Object r = m.invoke(svc, null, Integer.valueOf(userId));
                    Log.d(TAG, "getSessionsViaBinder: getSessions(null," + userId + ") -> " + (r == null ? "null" : r.getClass().getName() + " size=" + ((List) r).size()));
                    return r;
                } catch (Throwable t1) {
                    Log.w(TAG, "getSessionsViaBinder: getSessions(ComponentName,int) fail: " + t1.getMessage());
                    try {
                        Method m2 = svc.getClass().getMethod("getSessions", ComponentName.class);
                        Object r2 = m2.invoke(svc, null);
                        Log.d(TAG, "getSessionsViaBinder: getSessions(null) -> " + (r2 != null ? r2.getClass().getName() + " size=" + ((List) r2).size() : "null"));
                        return r2;
                    } catch (Throwable t2) {
                        Log.w(TAG, "getSessionsViaBinder: getSessions(ComponentName) fail: " + t2.getMessage());
                        return null;
                    }
                }
            } catch (Throwable th) {
                Log.w(TAG, "getSessionsViaBinder error: " + th.getMessage());
                return null;
            }
        } catch (Throwable th2) {
            Log.w(TAG, "getSessionsViaBinder error: " + th2.getMessage());
            return null;
        }
    }

    private void mergeKnownControllers(List<MediaController> list) {
        try {
            List<MediaController> known = getKnownControllers();
            if (known.isEmpty()) {
                return;
            }
            Iterator<MediaController> it = known.iterator();
            while (it.hasNext()) {
                MediaController kc = it.next();
                String kp = kc != null ? kc.getPackageName() : null;
                if (kp != null && !kp.equals(PKG_SELF) && !isHiddenController(kp)) {
                    boolean exists = false;
                    Iterator<MediaController> it2 = list.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        MediaController c = it2.next();
                        if (c != null && kp.equals(c.getPackageName())) {
                            exists = USE_BLUETOOTH_FOCUS_HOLD;
                            break;
                        }
                    }
                    if (!exists) {
                        list.add(kc);
                        Log.d(TAG, "mergeKnownControllers: added paused/inactive session " + kp);
                    }
                }
            }
        } catch (Throwable t) {
            Log.w(TAG, "mergeKnownControllers error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAvailableControllers() {
        int s;
        this.availableControllers = new ArrayList();
        MediaSessionManager msm = (MediaSessionManager) getSystemService("media_session");
        if (msm == null) {
            Log.w(TAG, "updateAvailableControllers: msm is null");
            return;
        }
        List<MediaController> sessions = null;
        try {
            ComponentName cn = new ComponentName(this, (Class<?>) MediaNotificationListener.class);
            sessions = msm.getActiveSessions(cn);
        } catch (SecurityException e) {
            Log.w(TAG, "getActiveSessions security exception: " + e.getMessage());
        } catch (Exception e2) {
            Log.w(TAG, "getActiveSessions error: " + e2.getMessage());
        }
        if (sessions == null || sessions.isEmpty()) {
            List<MediaController> known = getKnownControllers();
            if (known.isEmpty()) {
                Log.w(TAG, "updateAvailableControllers: no sessions");
                if (this.mediaController != null) {
                    this.controllerStale = USE_BLUETOOTH_FOCUS_HOLD;
                }
                pushStoppedToCard();
                return;
            }
            Log.d(TAG, "updateAvailableControllers: active sessions empty, use known sessions fallback (" + known.size() + ")");
            sessions = known;
        }
        for (MediaController c : sessions) {
            String pkg = c.getPackageName();
            if (pkg != null && !pkg.equals(PKG_SELF) && !isHiddenController(pkg)) {
                this.availableControllers.add(c);
            }
        }
        if (this.availableControllers.isEmpty()) {
            try {
                List<MediaController> all = msm.getActiveSessions(null);
                if (all != null) {
                    for (MediaController c2 : all) {
                        String pkg2 = c2.getPackageName();
                        if (pkg2 != null && !pkg2.equals(PKG_SELF) && !isHiddenController(pkg2)) {
                            this.availableControllers.add(c2);
                        }
                    }
                }
            } catch (Exception e3) {
                Log.w(TAG, "getActiveSessions(null) fallback error: " + e3.getMessage());
            }
        }
        mergeKnownControllers(this.availableControllers);
        if (this.availableControllers.isEmpty()) {
            if (this.mediaController != null) {
                this.controllerStale = USE_BLUETOOTH_FOCUS_HOLD;
            }
            pushStoppedToCard();
            return;
        }
        if (this.inTelecomCall) {
            boolean telecomGone = true ^ isTelecomSessionActive();
            if (telecomGone) {
                this.inTelecomCall = false;
                this.telecomEndProtectUntil = System.currentTimeMillis() + 5000;
                Log.i(TAG, "telecom call ended (session gone), restore pre-call controller=" + this.preCallControllerPkg);
                if (this.preCallControllerPkg != null) {
                    MediaController restore = findControllerByPackage(this.preCallControllerPkg);
                    if (restore != null) {
                        Log.i(TAG, "telecom restore: switching back to " + this.preCallControllerPkg);
                        selectController(restore);
                        return;
                    }
                    Log.w(TAG, "telecom restore: pre-call controller " + this.preCallControllerPkg + " no longer active, fall through to normal selection");
                }
            }
        }
        MediaController selected = null;
        List<MediaController> candidates = new ArrayList<>();
        for (MediaController c3 : this.availableControllers) {
            if (!isSystemMediaPackage(c3.getPackageName())) {
                candidates.add(c3);
            }
        }
        if (candidates.isEmpty() && this.mediaController != null && !isSystemMediaPackage(this.mediaController.getPackageName())) {
            return;
        }
        Iterator<MediaController> it = candidates.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            MediaController c4 = it.next();
            if (!PKG_BLUETOOTH.equals(c4.getPackageName()) || (this.mediaController != null && PKG_BLUETOOTH.equals(this.mediaController.getPackageName()))) {
                if (isAudioReallyPlaying(c4.getPackageName())) {
                    selected = c4;
                    break;
                }
            }
        }
        if (selected == null) {
            for (MediaController c5 : candidates) {
                PlaybackState ps = c5.getPlaybackState();
                if (ps != null && ((s = ps.getState()) == 3 || s == 2)) {
                    selected = c5;
                    break;
                }
            }
        }
        if (selected == null && !candidates.isEmpty()) {
            MediaController selected2 = candidates.get(0);
            selected = selected2;
        }
        if (selected != null) {
            Log.i(TAG, "updateAvailableControllers: selected " + selected.getPackageName());
            selectController(selected);
        }
    }

    public void selectController(MediaController controller) {
        if (controller == null) {
            return;
        }
        if (isSystemMediaPackage(controller.getPackageName())) {
            Log.i(TAG, "selectController: blocked system media package " + controller.getPackageName());
            return;
        }
        this.switchingController = USE_BLUETOOTH_FOCUS_HOLD;
        try {
            selectControllerInner(controller);
        } finally {
            this.switchingController = false;
        }
    }

    private boolean isTelecomSessionActive() {
        List<MediaController> all;
        try {
            MediaSessionManager msm = (MediaSessionManager) getSystemService("media_session");
            if (msm != null && (all = msm.getActiveSessions(null)) != null) {
                for (MediaController c : all) {
                    if (c != null && PKG_TELECOM.equals(c.getPackageName())) {
                        return USE_BLUETOOTH_FOCUS_HOLD;
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "isTelecomSessionActive error: " + e.getMessage());
        }
        return false;
    }

    private void selectControllerInner(MediaController controller) {
        if (controller == null) {
            return;
        }
        String newPkg = controller.getPackageName();
        if (PKG_TELECOM.equals(newPkg)) {
            if (!PKG_TELECOM.equals(this.controllerPkg)) {
                this.preCallControllerPkg = this.controllerPkg;
                Log.i(TAG, "telecom call: entering call, remember pre-call controller=" + this.preCallControllerPkg);
            }
            this.inTelecomCall = USE_BLUETOOTH_FOCUS_HOLD;
        } else {
            if (this.inTelecomCall) {
                this.telecomEndProtectUntil = System.currentTimeMillis() + 5000;
                Log.i(TAG, "telecom call ended, protect until " + this.telecomEndProtectUntil);
            }
            this.inTelecomCall = false;
        }
        this.currentTitle = null;
        this.currentArtist = null;
        this.qqArtistComplete = false;
        this.frozenPlaybackPosition = 0L;
        this.playbackDuration = 0L;
        this.lastBtPositionMs = 0L;
        this.lastBtPositionTime = 0L;
        this.lastNonBtPositionMs = 0L;
        this.lastNonBtPositionTime = 0L;
        if (this.cloudLyricFetcher != null) {
            this.cloudLyricFetcher.clearBroadcastLrc();
        }
        if (isLyricBlocked(controller.getPackageName())) {
            clearCurrentLyrics();
        }
        this.notifPkg = null;
        this.notifTitle = null;
        this.notifArtist = null;
        unregisterControllerCallback();
        this.mediaController = controller;
        this.controllerPkg = controller.getPackageName();
        if (this.kuwoAidlClient != null) {
            if (isKuwoPkg(this.controllerPkg)) {
                this.kuwoAidlClient.start();
            } else {
                this.kuwoAidlClient.stop();
            }
        }
        MediaCenterBridge.setControllerPkg(this.controllerPkg);
        if (isBluetoothController()) {
            PlaybackState curPs = controller.getPlaybackState();
            this.suppressBtPushUntilPlay = curPs == null || curPs.getState() != 3;
            if (this.suppressBtPushUntilPlay) {
                this.firstPushHandler.postDelayed(new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.12
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            MediaCenterBridge.get().arbitrateFocusToMultimedia();
                        } catch (Throwable t) {
                            Log.w(CarMediaService.TAG, "arbitrate focus on bt switch error: " + t.getMessage());
                        }
                    }
                }, 400L);
            }
        } else {
            this.suppressBtPushUntilPlay = false;
            abandonBluetoothFocus();
            enableMultimedia();
            resumeOtherSourcesOnBluetoothStop();
        }
        this.controllerStale = false;
        try {
            controller.registerCallback(this.controllerCallback);
        } catch (Exception e) {
            Log.w(TAG, "registerCallback error: " + e.getMessage());
        }
        if (!PKG_QQ_MUSIC.equals(this.controllerPkg)) {
            this.currentSourceType = 0;
            Log.i(TAG, "Switched controller, reset currentSourceType");
        }
        MediaMetadata meta = controller.getMetadata();
        if (meta != null) {
            handleMetadata(meta);
        } else if (PKG_QQ_MUSIC.equals(this.controllerPkg) && this.qqMusicFetcher != null) {
            Log.i(TAG, "selectControllerInner: QQ metadata null, proactive bind+fetch");
            this.currentSourceType = 1;
            if (!isLyricBlocked(this.controllerPkg)) {
                this.qqMusicFetcher.bind();
                this.qqMusicFetcher.fetchCurrentSong();
            }
            startLyricPoll();
        } else if (isDimPushEnabled()) {
            pushSourceSwitch();
        }
        PlaybackState ps = controller.getPlaybackState();
        if (ps != null) {
            updatePlaybackStatus(ps);
        }
    }

    private void unregisterControllerCallback() {
        try {
            if (this.mediaController != null && this.controllerCallback != null) {
                this.mediaController.unregisterCallback(this.controllerCallback);
            }
        } catch (Exception e) {
            Log.w(TAG, "unregister controller callback error: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMetadata(MediaMetadata metadata) {
        if (metadata == null) {
            return;
        }
        CharSequence titleCs = metadata.getText(METADATA_TITLE);
        CharSequence artistCs = metadata.getText(METADATA_ARTIST);
        boolean notifActive = notifActive();
        String newTitleRaw = titleCs != null ? titleCs.toString() : "";
        if (newTitleRaw == null || newTitleRaw.isEmpty()) {
            newTitleRaw = "未知歌曲";
        }
        this.currentTitle = newTitleRaw;
        String newArtist = artistCs != null ? artistCs.toString() : null;
        if (newArtist != null) {
            String newArtist2 = newArtist.replace('\\', '/');
            StringBuilder sb = new StringBuilder(newArtist2.length());
            for (int i = 0; i < newArtist2.length(); i++) {
                char c = newArtist2.charAt(i);
                if (c >= ' ' && c != 127) {
                    sb.append(c);
                }
            }
            newArtist = sb.toString();
        }
        if (newArtist != null && !newArtist.isEmpty() && !notifActive && (!PKG_QQ_MUSIC.equals(this.controllerPkg) || !this.qqArtistComplete)) {
            this.currentArtist = newArtist;
        }
        this.playbackDuration = metadata.getLong(METADATA_DURATION);
        Log.i(TAG, "handleMetadata: title=" + this.currentTitle + " artist=" + this.currentArtist + " duration=" + this.playbackDuration + " pkg=" + this.controllerPkg);
        extractArtworkAndPush(metadata);
        boolean songChanged = (this.currentTitle.equals(this.lastMetadataTitle) && (this.currentArtist == null || this.currentArtist.equals(this.lastMetadataArtist))) ? false : true;
        if (songChanged) {
            this.lastMetadataTitle = this.currentTitle;
            this.lastMetadataArtist = this.currentArtist;
            if (this.cloudLyricFetcher != null) {
                this.cloudLyricFetcher.resetApiStatus();
            }
            if (this.qqMusicFetcher != null) {
                this.qqMusicFetcher.resetApiStatus();
            }
        }
        if (PKG_QQ_MUSIC.equals(this.controllerPkg)) {
            this.currentSourceType = 1;
            if (isLyricBlocked(this.controllerPkg)) {
                Log.i(TAG, "handleMetadata: lyric blocked for " + this.controllerPkg + ", skip QQ lyric fetch");
                startLyricPoll();
            } else if (this.qqMusicFetcher != null) {
                this.qqMusicFetcher.bind();
                if (songChanged) {
                    this.qqMusicFetcher.fetchCurrentSong();
                }
                startLyricPoll();
            }
        } else if (PKG_NETEASE_IOT.equals(this.controllerPkg) || PKG_NETEASE.equals(this.controllerPkg)) {
            this.currentSourceType = 2;
            if (isLyricBlocked(this.controllerPkg)) {
                Log.i(TAG, "handleMetadata: lyric blocked for " + this.controllerPkg + ", skip netease search");
                startLyricPoll();
            } else if (this.cloudLyricFetcher != null) {
                if (songChanged) {
                    this.cloudLyricFetcher.search(this.currentTitle, this.currentArtist);
                }
                startLyricPoll();
            }
        } else if (isSodaPkg(this.controllerPkg)) {
            this.currentSourceType = 4;
            String sodaTrackId = null;
            try {
                String mid = metadata.getString("android.media.metadata.MEDIA_ID");
                if (mid != null && !mid.isEmpty() && mid.matches("[0-9]{10,}")) {
                    sodaTrackId = mid;
                } else if (mid != null) {
                    Log.d(TAG, "handleMetadata: soda MEDIA_ID not numeric: " + mid);
                }
            } catch (Throwable t) {
                Log.w(TAG, "handleMetadata: soda MEDIA_ID read fail: " + t.getMessage());
            }
            if (sodaTrackId != null) {
                Log.i(TAG, "handleMetadata: soda MEDIA_ID=" + sodaTrackId + " fetchByTrackId bypass search");
            }
            if (isLyricBlocked(this.controllerPkg)) {
                Log.i(TAG, "handleMetadata: lyric blocked for " + this.controllerPkg + ", skip soda search");
                startLyricPoll();
            } else if (this.sodaMusicFetcher != null) {
                if (songChanged) {
                    if (sodaTrackId != null) {
                        this.sodaMusicFetcher.fetchByTrackId(sodaTrackId, this.currentTitle, this.currentArtist);
                    } else {
                        this.sodaMusicFetcher.search(this.currentTitle, this.currentArtist);
                    }
                }
                startLyricPoll();
            }
        } else {
            this.currentSourceType = 3;
            if (isLyricBlocked(this.controllerPkg)) {
                Log.i(TAG, "handleMetadata: lyric blocked for " + this.controllerPkg + ", skip xmf2");
                startLyricPoll();
            } else if (this.cloudLyricFetcher != null) {
                if (songChanged) {
                    this.cloudLyricFetcher.searchXmf2Only(this.currentTitle, this.currentArtist);
                }
                startLyricPoll();
            }
        }
        if (isDimPushEnabled()) {
            pushMetadataToCard();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void extractArtworkAndPush(MediaMetadata metadata) {
        try {
            String uriStr = metadata.getString("android.media.metadata.ART_URI");
            if (uriStr == null) {
                uriStr = metadata.getString("android.media.metadata.ALBUM_ART_URI");
            }
            Uri artworkUri = null;
            if (uriStr == null) {
                Bitmap bmp = metadata.getBitmap("android.media.metadata.ART");
                if (bmp == null) {
                    bmp = metadata.getBitmap("android.media.metadata.ALBUM_ART");
                }
                if (bmp != null) {
                    artworkUri = ArtworkHelper.saveArtwork(this, bmp);
                }
            } else {
                String lower = uriStr.toLowerCase();
                if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
                    Bitmap bmp2 = ArtworkHelper.loadBitmapFromUri(this, Uri.parse(uriStr));
                    if (bmp2 != null) {
                        artworkUri = ArtworkHelper.saveArtwork(this, bmp2);
                    }
                }
                artworkUri = Uri.parse(uriStr);
            }
            this.currentArtworkUri = artworkUri != null ? artworkUri.toString() : null;
            MediaCenterBridge.get().setArtworkUri(this.currentArtworkUri);
            Log.i(TAG, "extractArtworkAndPush: uri=" + this.currentArtworkUri);
            if (this.currentArtworkUri != null) {
                this.artworkRetryCount = 0;
            } else if (!this.artworkRetryScheduled && this.artworkRetryCount < 3) {
                this.artworkRetryCount++;
                this.artworkRetryScheduled = USE_BLUETOOTH_FOCUS_HOLD;
                this.firstPushHandler.postDelayed(new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.13
                    @Override // java.lang.Runnable
                    public void run() {
                        CarMediaService.this.artworkRetryScheduled = false;
                        try {
                            MediaMetadata cur = CarMediaService.this.mediaController != null ? CarMediaService.this.mediaController.getMetadata() : null;
                            if (cur != null) {
                                CarMediaService.this.extractArtworkAndPush(cur);
                            }
                        } catch (Throwable t) {
                            Log.w(CarMediaService.TAG, "artwork retry error: " + t.getMessage());
                        }
                    }
                }, 1500L);
            }
        } catch (Throwable t) {
            Log.w(TAG, "extractArtworkAndPush failed: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePlaybackStatus(PlaybackState state) {
        this.lastPlaybackState = state;
        if (state != null) {
            long newPos = state.getPosition();
            boolean isBt = isBluetoothController();
            int s = state.getState();
            boolean playing = s == 3 ? USE_BLUETOOTH_FOCUS_HOLD : false;
            long now = SystemClock.elapsedRealtime();
            long realPos = newPos;
            long updateTime = state.getLastPositionUpdateTime();
            if (playing && updateTime > 0 && newPos >= 0) {
                realPos = newPos + (now - updateTime);
            }
            boolean shouldUpdate = (isBt && newPos == 0 && this.frozenPlaybackPosition > 0) ? false : USE_BLUETOOTH_FOCUS_HOLD;
            if (shouldUpdate) {
                this.frozenPlaybackPosition = playing ? realPos : newPos;
            }
            if (isBt && playing && newPos >= 0 && this.lastBtPositionTime > 0) {
                long estimated = this.lastBtPositionMs + (now - this.lastBtPositionTime);
                if (Math.abs(realPos - estimated) > 500) {
                    this.lastBtPositionMs = realPos;
                    this.lastBtPositionTime = now;
                    Log.d(TAG, "bt calibrate in callback: realPos=" + realPos + " base=" + newPos + " estimated=" + estimated + " diff=" + Math.abs(realPos - estimated));
                }
            }
            if (isBt && playing && this.lastBtPositionTime == 0 && this.frozenPlaybackPosition > 0) {
                this.lastBtPositionMs = this.frozenPlaybackPosition;
                this.lastBtPositionTime = now;
                Log.d(TAG, "bt resume: init accumulator from frozen=" + this.frozenPlaybackPosition);
            }
        }
        Log.i(TAG, ">>> playback state=" + (state != null ? state.getState() : -1) + " pos=" + (state != null ? state.getPosition() : -1L) + " frozen=" + this.frozenPlaybackPosition);
        if (isDimPushEnabled()) {
            pushMetadataToCard();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00c3 A[Catch: Exception -> 0x00cd, all -> 0x0182, TRY_LEAVE, TryCatch #0 {all -> 0x0182, blocks: (B:3:0x0004, B:7:0x000b, B:9:0x000f, B:11:0x0015, B:13:0x0019, B:15:0x002a, B:17:0x003c, B:20:0x0040, B:22:0x0048, B:25:0x0052, B:37:0x00b7, B:40:0x00bb, B:42:0x00c3, B:49:0x00d0, B:52:0x00d7, B:54:0x00db, B:55:0x00e2, B:56:0x00e9, B:58:0x00ee, B:60:0x00ff, B:62:0x0109, B:65:0x0115, B:69:0x0121, B:74:0x012e, B:77:0x0139, B:81:0x0143, B:84:0x014b, B:86:0x016d, B:88:0x017d, B:89:0x017f, B:92:0x0132, B:100:0x0063, B:102:0x0067, B:104:0x006f, B:106:0x007b, B:108:0x0081, B:110:0x0085, B:111:0x0095, B:113:0x0099, B:114:0x001d, B:116:0x0024), top: B:2:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00db A[Catch: all -> 0x0182, TryCatch #0 {all -> 0x0182, blocks: (B:3:0x0004, B:7:0x000b, B:9:0x000f, B:11:0x0015, B:13:0x0019, B:15:0x002a, B:17:0x003c, B:20:0x0040, B:22:0x0048, B:25:0x0052, B:37:0x00b7, B:40:0x00bb, B:42:0x00c3, B:49:0x00d0, B:52:0x00d7, B:54:0x00db, B:55:0x00e2, B:56:0x00e9, B:58:0x00ee, B:60:0x00ff, B:62:0x0109, B:65:0x0115, B:69:0x0121, B:74:0x012e, B:77:0x0139, B:81:0x0143, B:84:0x014b, B:86:0x016d, B:88:0x017d, B:89:0x017f, B:92:0x0132, B:100:0x0063, B:102:0x0067, B:104:0x006f, B:106:0x007b, B:108:0x0081, B:110:0x0085, B:111:0x0095, B:113:0x0099, B:114:0x001d, B:116:0x0024), top: B:2:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00e2 A[Catch: all -> 0x0182, TryCatch #0 {all -> 0x0182, blocks: (B:3:0x0004, B:7:0x000b, B:9:0x000f, B:11:0x0015, B:13:0x0019, B:15:0x002a, B:17:0x003c, B:20:0x0040, B:22:0x0048, B:25:0x0052, B:37:0x00b7, B:40:0x00bb, B:42:0x00c3, B:49:0x00d0, B:52:0x00d7, B:54:0x00db, B:55:0x00e2, B:56:0x00e9, B:58:0x00ee, B:60:0x00ff, B:62:0x0109, B:65:0x0115, B:69:0x0121, B:74:0x012e, B:77:0x0139, B:81:0x0143, B:84:0x014b, B:86:0x016d, B:88:0x017d, B:89:0x017f, B:92:0x0132, B:100:0x0063, B:102:0x0067, B:104:0x006f, B:106:0x007b, B:108:0x0081, B:110:0x0085, B:111:0x0095, B:113:0x0099, B:114:0x001d, B:116:0x0024), top: B:2:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0127  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x012e A[Catch: all -> 0x0182, TryCatch #0 {all -> 0x0182, blocks: (B:3:0x0004, B:7:0x000b, B:9:0x000f, B:11:0x0015, B:13:0x0019, B:15:0x002a, B:17:0x003c, B:20:0x0040, B:22:0x0048, B:25:0x0052, B:37:0x00b7, B:40:0x00bb, B:42:0x00c3, B:49:0x00d0, B:52:0x00d7, B:54:0x00db, B:55:0x00e2, B:56:0x00e9, B:58:0x00ee, B:60:0x00ff, B:62:0x0109, B:65:0x0115, B:69:0x0121, B:74:0x012e, B:77:0x0139, B:81:0x0143, B:84:0x014b, B:86:0x016d, B:88:0x017d, B:89:0x017f, B:92:0x0132, B:100:0x0063, B:102:0x0067, B:104:0x006f, B:106:0x007b, B:108:0x0081, B:110:0x0085, B:111:0x0095, B:113:0x0099, B:114:0x001d, B:116:0x0024), top: B:2:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x016d A[Catch: all -> 0x0182, TryCatch #0 {all -> 0x0182, blocks: (B:3:0x0004, B:7:0x000b, B:9:0x000f, B:11:0x0015, B:13:0x0019, B:15:0x002a, B:17:0x003c, B:20:0x0040, B:22:0x0048, B:25:0x0052, B:37:0x00b7, B:40:0x00bb, B:42:0x00c3, B:49:0x00d0, B:52:0x00d7, B:54:0x00db, B:55:0x00e2, B:56:0x00e9, B:58:0x00ee, B:60:0x00ff, B:62:0x0109, B:65:0x0115, B:69:0x0121, B:74:0x012e, B:77:0x0139, B:81:0x0143, B:84:0x014b, B:86:0x016d, B:88:0x017d, B:89:0x017f, B:92:0x0132, B:100:0x0063, B:102:0x0067, B:104:0x006f, B:106:0x007b, B:108:0x0081, B:110:0x0085, B:111:0x0095, B:113:0x0099, B:114:0x001d, B:116:0x0024), top: B:2:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:91:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0132 A[Catch: all -> 0x0182, TryCatch #0 {all -> 0x0182, blocks: (B:3:0x0004, B:7:0x000b, B:9:0x000f, B:11:0x0015, B:13:0x0019, B:15:0x002a, B:17:0x003c, B:20:0x0040, B:22:0x0048, B:25:0x0052, B:37:0x00b7, B:40:0x00bb, B:42:0x00c3, B:49:0x00d0, B:52:0x00d7, B:54:0x00db, B:55:0x00e2, B:56:0x00e9, B:58:0x00ee, B:60:0x00ff, B:62:0x0109, B:65:0x0115, B:69:0x0121, B:74:0x012e, B:77:0x0139, B:81:0x0143, B:84:0x014b, B:86:0x016d, B:88:0x017d, B:89:0x017f, B:92:0x0132, B:100:0x0063, B:102:0x0067, B:104:0x006f, B:106:0x007b, B:108:0x0081, B:110:0x0085, B:111:0x0095, B:113:0x0099, B:114:0x001d, B:116:0x0024), top: B:2:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:93:0x0129  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void pushMetadataToCard() {
        /*
            Method dump skipped, instructions count: 414
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.CarMediaService.pushMetadataToCard():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pushStoppedToCard() {
        try {
            if (!isDimPushEnabled()) {
                return;
            }
            if (this.lastPushedPlayState != 1 && !this.firstPushPending) {
                return;
            }
            stopPositionTicker();
            stopLyricPoll();
            this.notifPkg = null;
            this.notifTitle = null;
            this.notifArtist = null;
            this.lastPlaybackState = null;
            String[] pair = buildLyricPushPair();
            boolean delivered = MediaCenterBridge.get().pushMetadata(pair[0], pair[1], null, this.playbackDuration, 0, USE_BLUETOOTH_FOCUS_HOLD, this.controllerPkg, 0L);
            if (delivered) {
                this.lastPushedTitle = this.currentTitle;
                this.lastPushedArtist = this.currentArtist;
                this.lastPushedArtworkUri = null;
                this.lastPushedPlayState = 0;
                this.firstPushPending = false;
            }
            Log.i(TAG, "pushStoppedToCard: source stopped, pushed EAS status 0 (paused/stopped)");
        } catch (Throwable t) {
            Log.w(TAG, "pushStoppedToCard error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkContentChangedAndPush() {
        if (isDimPushEnabled()) {
            pushMetadataToCard();
        }
    }

    private String[] buildLyricPushPair() {
        String title = this.currentTitle;
        String artist = this.currentArtist;
        String line = getBoundLyricLine();
        if (line != null && !line.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            if (title != null) {
                sb.append(title);
            }
            sb.append(" - ");
            if (artist != null) {
                sb.append(artist);
            }
            return new String[]{line, sb.toString(), line};
        }
        return new String[]{title, artist, null};
    }

    private String getBoundLyricLine() {
        return getLyricLine(false);
    }

    public String getNextBoundLyricLine() {
        return getLyricLine(USE_BLUETOOTH_FOCUS_HOLD);
    }

    private String getLyricLine(boolean next) {
        try {
        } catch (Throwable t) {
            Log.w(TAG, "getLyricLine(" + (next ? KeyMonitor.ACTION_NEXT : "cur") + ") error: " + t.getMessage());
        }
        if (this.currentTitle == null || this.currentTitle.isEmpty() || isLyricBlocked(this.controllerPkg)) {
            return null;
        }
        long position = -1;
        try {
            position = getPlaybackPosition();
        } catch (Exception e) {
        }
        if (position < 0) {
            return null;
        }
        if (this.currentSourceType == 1 && this.qqMusicFetcher != null) {
            String line = next ? this.qqMusicFetcher.getNextLyricLine(position) : this.qqMusicFetcher.getCurrentLyricLine(position);
            if (line != null && !line.trim().isEmpty()) {
                if (!lyricTitleMatches(this.qqMusicFetcher.getSongTitle(), this.currentTitle)) {
                    return null;
                }
                return line;
            }
            return getGenericLyricLine(position, next);
        }
        if (this.currentSourceType == 2 && this.cloudLyricFetcher != null) {
            String line2 = next ? this.cloudLyricFetcher.getNextLyricLine(position) : this.cloudLyricFetcher.getCurrentLyricLine(position);
            if (line2 == null || line2.trim().isEmpty() || !lyricTitleMatches(this.cloudLyricFetcher.getSongTitle(), this.currentTitle)) {
                return null;
            }
            return line2;
        }
        if (this.currentSourceType == 4 && this.sodaMusicFetcher != null) {
            String line3 = next ? this.sodaMusicFetcher.getNextLyricLine(position) : this.sodaMusicFetcher.getCurrentLyricLine(position);
            if (line3 == null || line3.trim().isEmpty() || !lyricTitleMatches(this.sodaMusicFetcher.getSongTitle(), this.currentTitle)) {
                return null;
            }
            return line3;
        }
        if (this.currentSourceType == 3 && this.cloudLyricFetcher != null) {
            return getGenericLyricLine(position, next);
        }
        return null;
    }

    private String getGenericLyricLine(long position, boolean next) {
        if (this.cloudLyricFetcher == null) {
            return null;
        }
        String line = next ? this.cloudLyricFetcher.getNextLyricLine(position) : this.cloudLyricFetcher.getCurrentLyricLine(position);
        if (line == null || line.trim().isEmpty() || !lyricTitleMatches(this.cloudLyricFetcher.getSongTitle(), this.currentTitle)) {
            return null;
        }
        return line;
    }

    private static boolean lyricTitleMatches(String lyricTitle, String currentTitle) {
        if (lyricTitle == null || lyricTitle.isEmpty()) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        return TextUtil.normalize(lyricTitle).equals(TextUtil.normalize(currentTitle));
    }

    private static boolean equalsSafe(String a, String b) {
        if (a != null) {
            return a.equals(b);
        }
        if (b == null) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        return false;
    }

    private void startPositionTicker() {
        if (this.positionTickerRunning) {
            return;
        }
        this.positionTickerRunning = USE_BLUETOOTH_FOCUS_HOLD;
        this.lyricPollHandler.postDelayed(this.positionTickerRunnable, POSITION_TICKER_INTERVAL_MS);
        Log.d(TAG, "position ticker started");
    }

    private void stopPositionTicker() {
        this.positionTickerRunning = false;
        if (this.lyricPollHandler != null) {
            this.lyricPollHandler.removeCallbacks(this.positionTickerRunnable);
        }
    }

    private void initAudioPlaybackMonitor() {
        try {
            this.audioManager = (AudioManager) getSystemService("audio");
            if (this.audioManager == null) {
                return;
            }
            this.audioPlaybackCallback = new AudioManager.AudioPlaybackCallback() { // from class: com.ecarx.carmedia.CarMediaService.14
                @Override // android.media.AudioManager.AudioPlaybackCallback
                public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> configs) {
                    CarMediaService.this.handlePlaybackConfigChanged(configs);
                }
            };
            this.audioManager.registerAudioPlaybackCallback(this.audioPlaybackCallback, null);
            Log.i(TAG, "audio playback monitor registered");
        } catch (Throwable t) {
            Log.w(TAG, "initAudioPlaybackMonitor failed: " + t.getMessage());
        }
    }

    private void unregisterAudioPlaybackMonitor() {
        try {
            if (this.audioManager != null && this.audioPlaybackCallback != null) {
                this.audioManager.unregisterAudioPlaybackCallback(this.audioPlaybackCallback);
                this.audioPlaybackCallback = null;
            }
        } catch (Throwable t) {
            Log.w(TAG, "unregisterAudioPlaybackMonitor failed: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePlaybackConfigChanged(List<AudioPlaybackConfiguration> configs) {
        int i;
        int usage;
        String pkg;
        int i2 = 0;
        if (configs != null) {
            try {
                if (!configs.isEmpty()) {
                    String playingPkg = null;
                    Iterator<AudioPlaybackConfiguration> it = configs.iterator();
                    boolean voipNow = false;
                    while (it.hasNext()) {
                        Object confObj = it.next();
                        i = 0;
                        usage = -1;
                        pkg = null;
                        try {
                            Method getStateM = confObj.getClass().getMethod("getPlayerState", new Class[0]);
                            int pstate = ((Integer) getStateM.invoke(confObj, new Object[0])).intValue();
                            i = (pstate == 1 || pstate == 2) ? 1 : 0;
                            Object attrsObj = null;
                            try {
                                Method getAttrsM = confObj.getClass().getMethod("getAudioAttributes", new Class[0]);
                                attrsObj = getAttrsM.invoke(confObj, new Object[0]);
                            } catch (Exception e) {
                            }
                            if (attrsObj != null) {
                                try {
                                    Method getUsageM = attrsObj.getClass().getMethod("getUsage", new Class[0]);
                                    usage = ((Integer) getUsageM.invoke(attrsObj, new Object[0])).intValue();
                                } catch (Exception e2) {
                                }
                            }
                            if (i != 0 && (usage == 2 || usage == 3)) {
                                voipNow = true;
                            }
                            Method getUidM = confObj.getClass().getMethod("getClientUid", new Class[0]);
                            int uid = ((Integer) getUidM.invoke(confObj, new Object[0])).intValue();
                            pkg = uidToPackage(uid);
                        } catch (Exception e3) {
                        }
                        if (isSystemMediaPackage(pkg)) {
                            if (i != 0) {
                                Log.d(TAG, "auto-switch: skip system media package audio track: " + pkg);
                            }
                        } else if (i != 0 && usage == 1) {
                            if (PKG_BLUETOOTH.equals(pkg) && !isBluetoothReallyPlaying()) {
                                Log.d(TAG, "auto-switch: bluetooth A2DP started but not really playing, skip");
                            } else {
                                if (pkg != null && !pkg.equals(PKG_SELF) && !pkg.equals(this.controllerPkg)) {
                                    playingPkg = pkg;
                                    break;
                                }
                            }
                        }
                    }
                    if (this.voipActive && !voipNow) {
                        Log.i(TAG, "voip: call ended, try resume");
                        this.voipActive = false;
                        tryResumeAfterVoip();
                    }
                    this.voipActive = voipNow;
                    if (playingPkg == null) {
                        return;
                    }
                    if (playingPkg.equals(this.lastAudioPlayingPkg) && (playingPkg.equals(this.controllerPkg) || isControllerReallyPlaying())) {
                        return;
                    }
                    this.lastAudioPlayingPkg = playingPkg;
                    if (playingPkg.equals(this.controllerPkg)) {
                        return;
                    }
                    if (System.currentTimeMillis() < this.telecomEndProtectUntil) {
                        Log.i(TAG, "auto-switch: " + playingPkg + " in telecom-end protect window, skip");
                        return;
                    }
                    long now = System.currentTimeMillis();
                    if (now - this.lastAutoSwitchTime < AUTO_SWITCH_COOLDOWN_MS) {
                        Log.i(TAG, "auto-switch: " + playingPkg + " in cooldown, skip");
                        return;
                    }
                    MediaController target = findControllerByPackage(playingPkg);
                    if (target == null) {
                        Log.i(TAG, "auto-switch: " + playingPkg + " has no active media session, skip");
                        return;
                    }
                    Log.i(TAG, "auto-switch: new source playing: " + playingPkg + ", switching");
                    this.lastAutoSwitchTime = now;
                    selectController(target);
                    return;
                }
            } catch (Throwable t) {
                Log.w(TAG, "handlePlaybackConfigChanged error: " + t.getMessage());
                return;
            }
        }
        if (this.voipActive) {
            Log.i(TAG, "voip: configs empty, VOIP ended");
            this.voipActive = false;
            tryResumeAfterVoip();
        }
    }

    private String uidToPackage(int uid) {
        try {
            return getPackageManager().getNameForUid(uid);
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MediaController findControllerByPackage(String pkg) {
        MediaSessionManager msm = null;
        List<MediaController> sessions;
        if (pkg == null) {
            return null;
        }
        try {
            msm = (MediaSessionManager) getSystemService("media_session");
        } catch (Throwable t) {
            Log.w(TAG, "findControllerByPackage error: " + t.getMessage());
        }
        if (msm == null) {
            return null;
        }
        try {
            ComponentName cn = new ComponentName(this, (Class<?>) MediaNotificationListener.class);
            sessions = msm.getActiveSessions(cn);
        } catch (Exception e) {
            sessions = null;
        }
        if (sessions == null || sessions.isEmpty()) {
            try {
                sessions = msm.getActiveSessions(null);
            } catch (Exception e2) {
                sessions = null;
            }
        }
        if (sessions == null) {
            return null;
        }
        for (MediaController c : sessions) {
            if (pkg.equals(c.getPackageName())) {
                return c;
            }
        }
        for (MediaController c2 : getKnownControllers()) {
            if (pkg.equals(c2.getPackageName())) {
                return c2;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MediaController findReallyPlayingController() {
        return findReallyPlayingController(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MediaController findReallyPlayingController(String excludePkg) {
        MediaSessionManager msm = null;
        List<MediaController> sessions;
        String pkg = null;
        try {
            msm = (MediaSessionManager) getSystemService("media_session");
        } catch (Throwable t) {
            Log.w(TAG, "findReallyPlayingController error: " + t.getMessage());
        }
        if (msm == null) {
            return null;
        }
        try {
            ComponentName cn = new ComponentName(this, (Class<?>) MediaNotificationListener.class);
            sessions = msm.getActiveSessions(cn);
        } catch (Exception e) {
            sessions = null;
        }
        if (sessions == null || sessions.isEmpty()) {
            try {
                sessions = msm.getActiveSessions(null);
            } catch (Exception e2) {
                sessions = null;
            }
        }
        if (sessions == null) {
            return null;
        }
        for (MediaController c : sessions) {
            if (c != null && (pkg = c.getPackageName()) != null && !pkg.equals(PKG_SELF) && (excludePkg == null || !excludePkg.equals(pkg))) {
                if (isAudioReallyPlaying(pkg)) {
                    return c;
                }
            }
        }
        return null;
    }

    private boolean isAudioReallyPlaying(String pkg) {
        if (pkg == null || isSystemMediaPackage(pkg)) {
            return false;
        }
        if (PKG_BLUETOOTH.equals(pkg)) {
            return isBluetoothReallyPlaying();
        }
        try {
            AudioManager am = (AudioManager) getSystemService("audio");
            if (am == null) {
                return false;
            }
            int targetUid = getPackageUid(pkg);
            if (targetUid < 0) {
                return false;
            }
            Method m = AudioManager.class.getMethod("getActivePlaybackConfigurations", new Class[0]);
            Object result = m.invoke(am, new Object[0]);
            if (!(result instanceof List)) {
                return false;
            }
            for (Object conf : (List) result) {
                try {
                    Method getUid = conf.getClass().getMethod("getClientUid", new Class[0]);
                    Method getState = conf.getClass().getMethod("getPlayerState", new Class[0]);
                    int uid = ((Integer) getUid.invoke(conf, new Object[0])).intValue();
                    int pstate = ((Integer) getState.invoke(conf, new Object[0])).intValue();
                    if (uid == targetUid && (pstate == 2 || pstate == 1)) {
                        return USE_BLUETOOTH_FOCUS_HOLD;
                    }
                } catch (Exception e2) {
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "isAudioReallyPlaying failed: " + e.getMessage());
        }
        return false;
    }

    private void pushSourceSwitch() {
        try {
            if (!isDimPushEnabled()) {
                return;
            }
            if (this.suppressBtPushUntilPlay) {
                Log.d(TAG, "pushSourceSwitch: bt pending play, skip");
                return;
            }
            int audioState = getAudioRealPlayState();
            int easState = audioState == 3 ? 1 : 0;
            String[] lyricPair = buildLyricPushPair();
            MediaCenterBridge.get().pushMetadata(lyricPair[0], lyricPair[1], null, 0L, easState, USE_BLUETOOTH_FOCUS_HOLD, this.controllerPkg, 0L);
        } catch (Throwable t) {
            Log.w(TAG, ">>> pushSourceSwitch failed: " + t.getMessage());
        }
    }

    public void sendMediaAction(String action) {
        if (this.ghostControllerPkg != null && (this.mediaController == null || (this.mediaController != null && this.ghostControllerPkg.equals(this.controllerPkg) && !this.ghostControllerPkg.equals(this.mediaController.getPackageName())))) {
            Log.i(TAG, "sendMediaAction: ghost controller " + this.ghostControllerPkg + " action=" + action + ", dispatching media key");
            dispatchMediaKeyForGhost(action);
            return;
        }
        if (this.mediaController == null || this.controllerStale) {
            refreshController();
            if (this.mediaController == null) {
                Log.w(TAG, "sendMediaAction: no controller after refresh, action=" + action + " dropped");
                return;
            }
        }
        try {
            if (KeyMonitor.ACTION_PLAYPAUSE.equals(action)) {
                Log.i(TAG, "sendMediaAction: playpause -> controller=" + this.controllerPkg + " stale=" + this.controllerStale);
                sendPlayPause();
            } else if (KeyMonitor.ACTION_NEXT.equals(action)) {
                sendNext();
            } else if (KeyMonitor.ACTION_PREV.equals(action)) {
                sendPrevious();
            }
        } catch (Exception e) {
            Log.w(TAG, "sendMediaAction error: " + e.getMessage());
            refreshController();
        }
    }

    private void dispatchMediaKeyForGhost(String action) {
        try {
            if (this.ghostControllerPkg == null) {
                return;
            }
            String broadcastAction = null;
            if (KeyMonitor.ACTION_PLAYPAUSE.equals(action)) {
                broadcastAction = "kuwo.musichd_car.playing";
            } else if (KeyMonitor.ACTION_NEXT.equals(action)) {
                broadcastAction = "kuwo.musichd_car.next";
            } else if (KeyMonitor.ACTION_PREV.equals(action)) {
                broadcastAction = "kuwo.musichd_car.pre";
            }
            if (broadcastAction != null) {
                Intent intent = new Intent(broadcastAction);
                intent.setPackage(this.ghostControllerPkg);
                intent.setComponent(new ComponentName(this.ghostControllerPkg, "cn.kuwo.mod.notification.NotificationReceiver"));
                sendBroadcast(intent);
                Log.i(TAG, "dispatchMediaKeyForGhost: sent broadcast " + broadcastAction + " to " + this.ghostControllerPkg);
            }
            this.easFocusHandler.postDelayed(new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.15
                @Override // java.lang.Runnable
                public void run() {
                    if (CarMediaService.this.ghostControllerPkg == null) {
                        return;
                    }
                    MediaController ctrl = CarMediaService.this.findControllerByPackage(CarMediaService.this.ghostControllerPkg);
                    if (ctrl != null) {
                        Log.i(CarMediaService.TAG, "dispatchMediaKeyForGhost: " + CarMediaService.this.ghostControllerPkg + " now active, clearing ghost");
                        CarMediaService.this.ghostControllerPkg = null;
                        CarMediaService.this.controllerStale = CarMediaService.USE_BLUETOOTH_FOCUS_HOLD;
                        CarMediaService.this.updateAvailableControllers();
                        return;
                    }
                    Log.d(CarMediaService.TAG, "dispatchMediaKeyForGhost: " + CarMediaService.this.ghostControllerPkg + " still inactive, keep ghost");
                }
            }, 1500L);
        } catch (Throwable t) {
            Log.w(TAG, "dispatchMediaKeyForGhost error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPlayPause() {
        final MediaController target = keyTargetController();
        if (target == null) {
            Log.w(TAG, "sendPlayPause: no controller");
            return;
        }
        boolean btTarget = PKG_BLUETOOTH.equals(target.getPackageName());
        boolean shouldPause = USE_BLUETOOTH_FOCUS_HOLD;
        if (btTarget) {
            PlaybackState ps = target.getPlaybackState();
            if (ps == null || ps.getState() != 3) {
                shouldPause = false;
            }
            Log.i(TAG, "sendPlayPause: bluetooth controller, psState=" + (ps != null ? ps.getState() : -1) + " -> " + (shouldPause ? "pause" : "play"));
            if (!shouldPause) {
                abandonBluetoothFocus();
                pauseOtherSourcesOnBluetoothPlay();
            }
        } else {
            int audioReal = getAudioRealPlayState();
            if (audioReal != 3) {
                shouldPause = false;
            }
            Log.i(TAG, "sendPlayPause: audioReal=" + audioReal + " (3=playing) -> " + (shouldPause ? "pause" : "play"));
        }
        if (shouldPause) {
            target.getTransportControls().pause();
            return;
        }
        target.getTransportControls().play();
        if (btTarget) {
            this.firstPushHandler.postDelayed(new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.16
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (target != null && CarMediaService.this.isBluetoothSessionActive()) {
                            PlaybackState ps2 = target.getPlaybackState();
                            if (ps2 == null || ps2.getState() != 3) {
                                Log.i(CarMediaService.TAG, "sendPlayPause: bt not playing after XCMedia2 no-focus pause, auto re-play once");
                                target.getTransportControls().play();
                            }
                        }
                    } catch (Throwable t) {
                        Log.w(CarMediaService.TAG, "sendPlayPause bt replay error: " + t.getMessage());
                    }
                }
            }, 800L);
        }
    }

    private MediaController keyTargetController() {
        return this.mediaController;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendNext() {
        MediaController target = keyTargetController();
        if (target != null) {
            target.getTransportControls().skipToNext();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPrevious() {
        MediaController target = keyTargetController();
        if (target != null) {
            target.getTransportControls().skipToPrevious();
        }
    }

    @Override // com.ecarx.carmedia.CarMediaMusicClient.Controller
    public void onPlayCommand() {
        if (isEasFromRecentLogcat()) {
            Log.d(TAG, "EAS ctrl dedup (play): logcat 通道刚处理同按键,忽略防双切");
            return;
        }
        Log.i(TAG, "EAS ctrl: PLAY");
        if (this.mediaController == null || this.controllerStale) {
            refreshController();
        }
        MediaController target = keyTargetController();
        if (target != null) {
            target.getTransportControls().play();
        }
    }

    @Override // com.ecarx.carmedia.CarMediaMusicClient.Controller
    public void onPauseCommand() {
        if (isEasFromRecentLogcat()) {
            Log.d(TAG, "EAS ctrl dedup (pause): logcat 通道刚处理同按键,忽略防双切");
            return;
        }
        Log.i(TAG, "EAS ctrl: PAUSE");
        if (this.mediaController == null || this.controllerStale) {
            refreshController();
        }
        MediaController target = keyTargetController();
        if (target != null) {
            target.getTransportControls().pause();
        }
    }

    @Override // com.ecarx.carmedia.CarMediaMusicClient.Controller
    public void onNextCommand() {
        if (isEasFromRecentLogcat()) {
            Log.d(TAG, "EAS ctrl dedup (next): logcat 通道刚处理同按键,忽略防双切");
            return;
        }
        if (!isKeyBlockedSetting() && isSystemMediaKeyRoutedToController(87)) {
            Log.i(TAG, "EAS ctrl dedup (next): system routed media key to controller, inject handles it (Bug#53)");
            return;
        }
        Log.i(TAG, "EAS ctrl: NEXT");
        if (this.mediaController == null || this.controllerStale) {
            refreshController();
        }
        MediaController target = keyTargetController();
        if (target != null) {
            target.getTransportControls().skipToNext();
        }
        markMdcKeyHandle();
    }

    @Override // com.ecarx.carmedia.CarMediaMusicClient.Controller
    public void onPreviousCommand() {
        if (isEasFromRecentLogcat()) {
            Log.d(TAG, "EAS ctrl dedup (previous): logcat 通道刚处理同按键,忽略防双切");
            return;
        }
        if (!isKeyBlockedSetting() && isSystemMediaKeyRoutedToController(88)) {
            Log.i(TAG, "EAS ctrl dedup (previous): system routed media key to controller, inject handles it (Bug#53)");
            return;
        }
        Log.i(TAG, "EAS ctrl: PREVIOUS");
        if (this.mediaController == null || this.controllerStale) {
            refreshController();
        }
        MediaController target = keyTargetController();
        if (target != null) {
            target.getTransportControls().skipToPrevious();
        }
        markMdcKeyHandle();
    }

    @Override // com.ecarx.carmedia.CarMediaMusicClient.Controller
    public void onSeekCommand(long pos) {
        Log.i(TAG, "EAS ctrl: SEEK " + pos);
        if (this.mediaController == null || this.controllerStale) {
            refreshController();
        }
        if (this.mediaController != null) {
            this.mediaController.getTransportControls().seekTo(pos);
        }
    }

    @Override // com.ecarx.carmedia.CarMediaMusicClient.Controller
    public void onStopCommand() {
        Log.i(TAG, "EAS ctrl: STOP");
        if (this.mediaController != null) {
            this.mediaController.getTransportControls().stop();
        }
    }

    @Override // com.ecarx.carmedia.CarMediaMusicClient.Controller
    public void onRewindCommand() {
        Log.i(TAG, "EAS ctrl: REWIND");
        if (this.mediaController != null) {
            this.mediaController.getTransportControls().rewind();
        }
    }

    @Override // com.ecarx.carmedia.CarMediaMusicClient.Controller
    public void onForwardCommand() {
        Log.i(TAG, "EAS ctrl: FAST_FORWARD");
        if (this.mediaController != null) {
            this.mediaController.getTransportControls().fastForward();
        }
    }

    private void initKeyMonitor() {
        try {
            this.keyMonitor = new KeyMonitor(this, new KeyMonitorCallback());
            this.keyMonitor.updateActions(loadKeyActions());
            boolean blocked = getSharedPreferences("carmedia_prefs", 0).getBoolean("key_blocked", false);
            setWheelKeysBlocked(blocked);
            Log.i(TAG, "initKeyMonitor: restored key_blocked=" + blocked + " (system MediaKeyReceiver " + (blocked ? "disabled" : "enabled") + ")");
            startLogcatKeyMonitor();
        } catch (Throwable t) {
            Log.e(TAG, "initKeyMonitor failed: " + t.getMessage());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x0190  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x019a  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x01a2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.util.Map<java.lang.String, java.lang.String> loadKeyActions() {
        /*
            Method dump skipped, instructions count: 474
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.CarMediaService.loadKeyActions():java.util.Map");
    }

    private void saveKeyActions(Map<String, String> map) {
        try {
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, String> e : map.entrySet()) {
                obj.put(e.getKey(), e.getValue());
            }
            getSharedPreferences("carmedia_prefs", 0).edit().putString("key_actions", obj.toString()).apply();
        } catch (Throwable th) {
        }
    }

    public Map<String, String> getKeyActions() {
        return loadKeyActions();
    }

    public void setKeyAction(int keyCode, int tap, String action) {
        if (keyCode <= 0 || action == null || action.isEmpty()) {
            return;
        }
        String key = keyCode + ":" + tap;
        Map<String, String> map = loadKeyActions();
        map.put(key, action);
        saveKeyActions(map);
        if (this.keyMonitor != null) {
            this.keyMonitor.updateActions(map);
        }
    }

    public void removeKeyAction(int keyCode, int tap) {
        String key = keyCode + ":" + tap;
        Map<String, String> map = loadKeyActions();
        map.remove(key);
        saveKeyActions(map);
        if (this.keyMonitor != null) {
            this.keyMonitor.updateActions(map);
        }
    }

    public void startKeyRecording(KeyMonitor.KeyRecorder recorder) {
        if (this.keyMonitor != null) {
            this.keyMonitor.startRecording(recorder);
        }
    }

    public void stopKeyRecording() {
        if (this.keyMonitor != null) {
            this.keyMonitor.stopRecording();
        }
    }

    public static class AppInfo {
        public final String appName;
        public final String iconUri;
        public final String packageName;

        public AppInfo(String pkg, String name, String icon) {
            this.packageName = pkg;
            this.appName = name;
            this.iconUri = icon;
        }
    }

    public List<AppInfo> getLaunchableApps() {
        List<AppInfo> result = new ArrayList<>();
        try {
            PackageManager pm = getPackageManager();
            Intent mainIntent = new Intent("android.intent.action.MAIN", (Uri) null);
            mainIntent.addCategory("android.intent.category.LAUNCHER");
            List<ResolveInfo> resolves = pm.queryIntentActivities(mainIntent, 0);
            Set<String> seenPkgs = new HashSet<>();
            for (ResolveInfo ri : resolves) {
                String pkg = ri.activityInfo.packageName;
                if (pkg != null && !pkg.isEmpty() && seenPkgs.add(pkg)) {
                    String name = ri.loadLabel(pm).toString();
                    if (name == null || name.isEmpty()) {
                        name = pkg;
                    }
                    String icon = AppIconResolver.drawableToDataUri(ri.loadIcon(pm));
                    result.add(new AppInfo(pkg, name, icon));
                }
            }
            Collections.sort(result, new Comparator<AppInfo>() { // from class: com.ecarx.carmedia.CarMediaService.17
                @Override // java.util.Comparator
                public int compare(AppInfo a, AppInfo b) {
                    return a.appName.compareToIgnoreCase(b.appName);
                }
            });
            result.add(0, new AppInfo("__home__", "回到桌面", null));
            boolean hasSelf = false;
            Iterator<AppInfo> it = result.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                AppInfo ai = it.next();
                if (getPackageName().equals(ai.packageName)) {
                    hasSelf = USE_BLUETOOTH_FOCUS_HOLD;
                    break;
                }
            }
            if (!hasSelf) {
                String selfIcon = AppIconResolver.drawableToDataUri(getPackageManager().getApplicationIcon(getPackageName()));
                result.add(1, new AppInfo(getPackageName(), "CarMedia 主界面", selfIcon));
            }
            Log.i(TAG, "getLaunchableApps: found " + result.size() + " launchable apps (incl. home + self)");
        } catch (Throwable t) {
            Log.e(TAG, "getLaunchableApps failed: " + t.getMessage());
        }
        return result;
    }

    public void launchAppByPackage(String pkg) {
        if (pkg == null || pkg.isEmpty()) {
            Log.w(TAG, "launchAppByPackage: empty package name");
            return;
        }
        try {
            if ("__home__".equals(pkg)) {
                Intent homeIntent = new Intent("android.intent.action.MAIN");
                homeIntent.addCategory("android.intent.category.HOME");
                homeIntent.addFlags(270532608);
                startActivity(homeIntent);
                Log.i(TAG, "launchAppByPackage: returned to home (HOME intent)");
                return;
            }
            Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
            if (intent == null) {
                Log.w(TAG, "launchAppByPackage: no launch intent for " + pkg);
                return;
            }
            intent.addFlags(270532608);
            startActivity(intent);
            Log.i(TAG, "launchAppByPackage: launched " + pkg);
        } catch (Throwable t) {
            Log.e(TAG, "launchAppByPackage failed: " + pkg + " - " + t.getMessage());
        }
    }

    private void startLogcatKeyMonitor() {
        try {
            if (this.logcatKeyMonitor != null) {
                return;
            }
            this.logcatKeyMonitor = new LogcatKeyMonitor(new LogcatKeyMonitor.Listener() { // from class: com.ecarx.carmedia.CarMediaService.18
                @Override // com.ecarx.carmedia.LogcatKeyMonitor.Listener
                public void onKeyDown(int keyCode) {
                    if (CarMediaService.this.keyMonitor != null) {
                        CarMediaService.this.keyMonitor.handleExternalKeyDown(keyCode);
                    }
                }

                @Override // com.ecarx.carmedia.LogcatKeyMonitor.Listener
                public void onKeyUp(int keyCode) {
                    if (CarMediaService.this.keyMonitor != null) {
                        CarMediaService.this.keyMonitor.handleExternalKeyUp(keyCode);
                    }
                }

                @Override // com.ecarx.carmedia.LogcatKeyMonitor.Listener
                public void onMediaButtonRouted(int keyCode, String pkg) {
                    CarMediaService.this.lastMediaSessionRouteKeyCode = keyCode;
                    CarMediaService.this.lastMediaSessionRoutePkg = pkg;
                    CarMediaService.this.lastMediaSessionRouteAt = System.currentTimeMillis();
                    if (pkg != null && pkg.equals(CarMediaService.this.controllerPkg)) {
                        CarMediaService.this.markLogcatKeyHandle();
                        Log.i(CarMediaService.TAG, "Bug#53: system routed media key " + keyCode + " to controller " + pkg + ", MDC forward will be deduped");
                    }
                }
            });
            this.logcatKeyMonitor.start();
        } catch (Throwable t) {
            Log.w(TAG, "startLogcatKeyMonitor error: " + t.getMessage());
        }
    }

    private void stopLogcatKeyMonitor() {
        if (this.logcatKeyMonitor != null) {
            try {
                this.logcatKeyMonitor.stop();
            } catch (Throwable t) {
                Log.w(TAG, "stopLogcatKeyMonitor error: " + t.getMessage());
            }
            this.logcatKeyMonitor = null;
        }
    }

    public boolean isWheelKeysBlocked() {
        return isKeyBlockedSetting();
    }

    public boolean isKeyBlockedSetting() {
        try {
            return getSharedPreferences("carmedia_prefs", 0).getBoolean("key_blocked", false);
        } catch (Throwable th) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMediaKeyAction(String action) {
        if (KeyMonitor.ACTION_PREV.equals(action) || KeyMonitor.ACTION_NEXT.equals(action)) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
        return false;
    }

    public void setWheelKeysBlocked(boolean blocked) {
        try {
            PackageManager pm = getPackageManager();
            ComponentName comp = new ComponentName("ecarx.xsf.mediacenter", "ecarx.xsf.mediacenter.MediaKeyReceiver");
            pm.setComponentEnabledSetting(comp, blocked ? 2 : 1, 1);
        } catch (Exception e) {
            Log.w(TAG, "MediaKeyReceiver toggle failed (not present on this device): " + e.getMessage());
        }
        getSharedPreferences("carmedia_prefs", 0).edit().putBoolean("key_blocked", blocked).apply();
    }

    private class KeyMonitorCallback implements KeyMonitor.Callback {
        private KeyMonitorCallback() {
        }

        @Override // com.ecarx.carmedia.KeyMonitor.Callback
        public void onKeyAction(String action, int keyCode) {
            Log.i(CarMediaService.TAG, "Key action=" + action + " keyCode=" + keyCode);
            if (!CarMediaService.this.isKeyBlockedSetting() && CarMediaService.this.isMediaKeyAction(action)) {
                Log.i(CarMediaService.TAG, "Key action=" + action + " skipped (key_blocked=false, system inject handles it)");
                return;
            }
            if (KeyMonitor.ACTION_MODE.equals(action)) {
                CarMediaService.this.openMainUi();
                return;
            }
            if (KeyMonitor.ACTION_HOME.equals(action)) {
                CarMediaService.this.openMainUi();
                return;
            }
            if (KeyMonitor.ACTION_PREV.equals(action)) {
                if (CarMediaService.this.isMdcHandledRecently()) {
                    Log.i(CarMediaService.TAG, "PREV key dedup: MDC 转发刚处理同按键,logcat 通道忽略防双切");
                    return;
                }
                Log.i(CarMediaService.TAG, "PREV key pressed - handled by CarMedia");
                CarMediaService.this.markLogcatKeyHandle();
                CarMediaService.this.sendPrevious();
                return;
            }
            if (KeyMonitor.ACTION_NEXT.equals(action)) {
                if (CarMediaService.this.isMdcHandledRecently()) {
                    Log.i(CarMediaService.TAG, "NEXT key dedup: MDC 转发刚处理同按键,logcat 通道忽略防双切");
                    return;
                }
                Log.i(CarMediaService.TAG, "NEXT key pressed - handled by CarMedia");
                CarMediaService.this.markLogcatKeyHandle();
                CarMediaService.this.sendNext();
                return;
            }
            if (KeyMonitor.ACTION_MUTE.equals(action)) {
                CarMediaService.this.markLogcatKeyHandle();
                CarMediaService.this.toggleMute();
                return;
            }
            if (KeyMonitor.ACTION_PLAYPAUSE.equals(action)) {
                CarMediaService.this.markLogcatKeyHandle();
                CarMediaService.this.sendPlayPause();
            } else if (KeyMonitor.ACTION_SOURCE.equals(action)) {
                CarMediaService.this.openCurrentSourceApp();
            } else if (action != null && action.startsWith("openapp:")) {
                String pkg = action.substring(8);
                Log.i(CarMediaService.TAG, "Key action=openapp pkg=" + pkg);
                CarMediaService.this.launchAppByPackage(pkg);
            }
        }

        @Override // com.ecarx.carmedia.KeyMonitor.Callback
        public void onMonitorStatus(String status) {
            Log.i(CarMediaService.TAG, status);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleMute() {
        try {
            AudioManager am = (AudioManager) getSystemService("audio");
            if (am == null) {
                return;
            }
            boolean muted = am.isStreamMute(3);
            am.setStreamMute(3, !muted ? USE_BLUETOOTH_FOCUS_HOLD : false);
            Log.i(TAG, "toggleMute -> " + (!muted ? "muted" : "unmuted"));
        } catch (Throwable t) {
            Log.w(TAG, "toggleMute error: " + t.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openMainUi() {
        try {
            Intent intent = new Intent(this, (Class<?>) MainActivity.class);
            intent.addFlags(335544320);
            startActivity(intent);
        } catch (Exception e) {
            Log.w(TAG, "openMainUi failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openCurrentSourceApp() {
        try {
            String pkg = this.controllerPkg;
            if (pkg != null && !pkg.isEmpty()) {
                Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
                if (launch == null) {
                    Log.w(TAG, "openCurrentSourceApp: no launch intent for " + pkg + ", fallback to main ui");
                    openMainUi();
                    return;
                } else {
                    launch.addFlags(268435456);
                    startActivity(launch);
                    Log.i(TAG, "openCurrentSourceApp: launching " + pkg);
                    return;
                }
            }
            Log.i(TAG, "openCurrentSourceApp: no controller pkg, fallback to main ui");
            openMainUi();
        } catch (Throwable t) {
            Log.w(TAG, "openCurrentSourceApp failed: " + t.getMessage());
            openMainUi();
        }
    }

    public List<MediaController> getAvailableControllers() {
        return this.availableControllers;
    }

    public List<String> getGhostControllerPkgs() {
        int idx;
        List<String> result = new ArrayList<>();
        try {
            Process p = new ProcessBuilder("dumpsys", "media_session").redirectErrorStream(USE_BLUETOOTH_FOCUS_HOLD).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String currentPkg = null;
            boolean currentActive = false;
            boolean inSession = false;
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                if (line.contains("(userId=") && line.contains("/") && !line.contains("session is")) {
                    if (inSession && currentPkg != null && !currentActive) {
                        addGhostIfMissing(result, currentPkg);
                    }
                    inSession = USE_BLUETOOTH_FOCUS_HOLD;
                    currentPkg = null;
                    currentActive = false;
                }
                if (inSession) {
                    if (line.contains("package=") && (idx = line.indexOf("package=")) >= 0) {
                        currentPkg = line.substring(idx + 8).trim();
                    }
                    if (line.contains("active=")) {
                        int idx2 = line.indexOf("active=");
                        if (idx2 >= 0) {
                            currentActive = line.substring(idx2 + 7, idx2 + 11).startsWith("true");
                        }
                    }
                }
            }
            if (inSession && currentPkg != null && !currentActive) {
                addGhostIfMissing(result, currentPkg);
            }
            p.waitFor();
        } catch (Throwable t) {
            Log.w(TAG, "getGhostControllerPkgs error: " + t.getMessage());
        }
        return result;
    }

    private void addGhostIfMissing(List<String> list, String pkg) {
        if (pkg == null || pkg.isEmpty() || pkg.equals(PKG_SELF) || isHiddenController(pkg)) {
            return;
        }
        for (MediaController mc : this.availableControllers) {
            if (mc != null && pkg.equals(mc.getPackageName())) {
                return;
            }
        }
        if (!list.contains(pkg)) {
            list.add(pkg);
            Log.d(TAG, "getGhostControllerPkgs: found paused session " + pkg);
        }
    }

    public String getCurrentTitle() {
        return this.currentTitle;
    }

    public String getCurrentArtist() {
        return this.currentArtist;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isBluetoothController() {
        return PKG_BLUETOOTH.equals(this.controllerPkg);
    }

    private boolean isBluetoothReallyPlaying() {
        try {
            MediaController bt = findControllerByPackage(PKG_BLUETOOTH);
            if (bt != null && bt.getPlaybackState() != null) {
                if (bt.getPlaybackState().getState() == 3) {
                    return USE_BLUETOOTH_FOCUS_HOLD;
                }
                return false;
            }
            return false;
        } catch (Throwable th) {
            return false;
        }
    }

    private boolean isControllerReallyPlaying() {
        if (this.controllerPkg == null) {
            return false;
        }
        if (isBluetoothController()) {
            return isBluetoothReallyPlaying();
        }
        return isAudioReallyPlaying(this.controllerPkg);
    }

    public int getPlaybackState() {
        int audioState = getAudioRealPlayState();
        if (!isBluetoothController() && audioState > 0) {
            return audioState;
        }
        if (this.mediaController == null) {
            return 0;
        }
        if (isBluetoothController()) {
            try {
                PlaybackState live = this.mediaController.getPlaybackState();
                if (live != null) {
                    int s = live.getState();
                    if (s == 3) {
                        return s;
                    }
                    return 0;
                }
            } catch (Exception e) {
            }
        }
        try {
            PlaybackState live2 = this.mediaController.getPlaybackState();
            if (live2 != null) {
                int s2 = live2.getState();
                if (s2 == 1 || s2 == 0) {
                    return s2;
                }
            }
        } catch (Exception e2) {
        }
        if (notifActive() && this.lastPlaybackState != null) {
            if (this.lastPlaybackState.getState() == 3) {
                return 2;
            }
            return this.lastPlaybackState.getState();
        }
        if (this.mediaController.getPlaybackState() != null) {
            int s3 = this.mediaController.getPlaybackState().getState();
            if (s3 == 3) {
                return 2;
            }
            return s3;
        }
        if (this.lastPlaybackState != null) {
            return this.lastPlaybackState.getState();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public int getAudioRealPlayState() {
        String str;
        Iterator<?> it;
        String str2;
        Method m;
        int pstate;
        int usage;
        AudioManager am;
        if (!isBluetoothController()) {
            try {
                AudioManager am2 = (AudioManager) getSystemService("audio");
                if (am2 == null) {
                    Log.d(TAG, "audioReal: am null");
                    return 0;
                }
                Method m2 = AudioManager.class.getMethod("getActivePlaybackConfigurations", new Class[0]);
                Object result = m2.invoke(am2, new Object[0]);
                if (!(result instanceof List)) {
                    return 0;
                }
                List<?> configs = (List) result;
                int targetUid = getPackageUid(this.controllerPkg);
                boolean isBt = PKG_BLUETOOTH.equals(this.controllerPkg);
                Log.d(TAG, "audioReal: pkg=" + this.controllerPkg + " uid=" + targetUid + " cfgs=" + configs.size() + " bt=" + isBt);
                boolean anyStarted = false;
                Iterator<?> it2 = configs.iterator();
                boolean maybePausedAosp = false;
                boolean aospStarted = false;
                boolean anyPaused = false;
                while (true) {
                    boolean anyPaused2 = it2.hasNext();
                    str = "getPlayerState";
                    if (!anyPaused2) {
                        break;
                    }
                    Object conf = it2.next();
                    try {
                        am = am2;
                        try {
                            Method getUid = conf.getClass().getMethod("getClientUid", new Class[0]);
                            Method getState = conf.getClass().getMethod("getPlayerState", new Class[0]);
                            try {
                                int uid = ((Integer) getUid.invoke(conf, new Object[0])).intValue();
                                int pstate2 = ((Integer) getState.invoke(conf, new Object[0])).intValue();
                                if (uid == targetUid) {
                                    if (pstate2 == 1) {
                                        aospStarted = USE_BLUETOOTH_FOCUS_HOLD;
                                    }
                                    if (pstate2 == 2) {
                                        maybePausedAosp = USE_BLUETOOTH_FOCUS_HOLD;
                                    }
                                    if (pstate2 == 3) {
                                        anyPaused = USE_BLUETOOTH_FOCUS_HOLD;
                                    }
                                }
                            } catch (Exception e) {
                            }
                        } catch (Exception e2) {
                        }
                    } catch (Exception e3) {
                        am = am2;
                    }
                    am2 = am;
                }
                if (aospStarted) {
                    anyStarted = USE_BLUETOOTH_FOCUS_HOLD;
                } else if (maybePausedAosp) {
                    anyStarted = USE_BLUETOOTH_FOCUS_HOLD;
                }
                if (!anyStarted && !anyPaused && isBt) {
                    Log.d(TAG, "audioReal: bt fallback global scan");
                    for (Object conf2 : configs) {
                        try {
                            Method getState2 = conf2.getClass().getMethod("getPlayerState", new Class[0]);
                            pstate = ((Integer) getState2.invoke(conf2, new Object[0])).intValue();
                            usage = -1;
                            try {
                                Object attrs = conf2.getClass().getMethod("getAudioAttributes", new Class[0]).invoke(conf2, new Object[0]);
                                if (attrs != null) {
                                    Method getUsage = attrs.getClass().getMethod("getUsage", new Class[0]);
                                    usage = ((Integer) getUsage.invoke(attrs, new Object[0])).intValue();
                                }
                            } catch (Exception e4) {
                            }
                            if (usage == 1) {
                                if (pstate == 1 || pstate == 2) {
                                    anyStarted = USE_BLUETOOTH_FOCUS_HOLD;
                                } else if (pstate == 3) {
                                    anyPaused = USE_BLUETOOTH_FOCUS_HOLD;
                                }
                            }
                        } catch (Exception e7) {
                        }
                    }
                }
                Log.d(TAG, "audioReal: -> state=" + (anyStarted ? 3 : anyPaused ? 2 : 0));
                if (anyStarted) {
                    return 3;
                }
                return anyPaused ? 2 : 0;
            } catch (Exception e8) {
                Log.w(TAG, "getAudioRealPlayState failed: " + e8.getMessage());
                return 0;
            }
        }
        boolean playing = isBluetoothReallyPlaying();
        Log.d(TAG, "audioReal: bt controller, isBluetoothReallyPlaying=" + playing + " -> state=" + (playing ? 3 : 0));
        return playing ? 3 : 0;
    }

    private int getPackageUid(String pkg) {
        if (pkg == null) {
            return -1;
        }
        try {
            return getPackageManager().getApplicationInfo(pkg, 0).uid;
        } catch (Exception e) {
            return -1;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00c5  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x010b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public long getPlaybackPosition() {
        /*
            Method dump skipped, instructions count: 600
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.CarMediaService.getPlaybackPosition():long");
    }

    public long getPlaybackDuration() {
        return this.playbackDuration;
    }

    public String getControllerPkg() {
        return this.controllerPkg;
    }

    public String getGhostControllerPkg() {
        return this.ghostControllerPkg;
    }

    public void setGhostControllerPkg(String pkg) {
        this.ghostControllerPkg = pkg;
        if (pkg != null) {
            this.controllerPkg = pkg;
            this.currentTitle = null;
            this.currentArtist = null;
            this.qqArtistComplete = false;
            this.frozenPlaybackPosition = 0L;
            this.playbackDuration = 0L;
            this.lastBtPositionMs = 0L;
            this.lastBtPositionTime = 0L;
            this.lastNonBtPositionMs = 0L;
            this.lastNonBtPositionTime = 0L;
            if (this.cloudLyricFetcher != null) {
                this.cloudLyricFetcher.clearLoadedLrc();
            }
            this.mediaController = null;
            this.controllerStale = USE_BLUETOOTH_FOCUS_HOLD;
            if (this.kuwoAidlClient != null) {
                if (isKuwoPkg(pkg)) {
                    this.kuwoAidlClient.start();
                } else {
                    this.kuwoAidlClient.stop();
                }
            }
            Log.i(TAG, "setGhostControllerPkg: " + pkg + " (cleared old metadata)");
            MediaNotificationListener.requestActiveNotifications(pkg);
        }
    }

    public boolean isDimAutoPush() {
        return this.dimAutoPush;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDimPushEnabled() {
        try {
            return getSharedPreferences("carmedia_prefs", 0).getBoolean("dim_auto_push", USE_BLUETOOTH_FOCUS_HOLD);
        } catch (Throwable th) {
            return USE_BLUETOOTH_FOCUS_HOLD;
        }
    }

    public void setDimAutoPush(boolean v) {
        this.dimAutoPush = v;
        try {
            getSharedPreferences("carmedia_prefs", 0).edit().putBoolean("dim_auto_push", v).apply();
        } catch (Throwable th) {
        }
    }

    public boolean isQqMusicInstalled() {
        if (this.qqMusicFetcher == null || !this.qqMusicFetcher.isQqMusicInstalled()) {
            return false;
        }
        return USE_BLUETOOTH_FOCUS_HOLD;
    }

    public int getQqAuthStatus() {
        if (this.qqMusicFetcher == null) {
            return 0;
        }
        try {
            return this.qqMusicFetcher.getAuthStatus();
        } catch (Throwable t) {
            Log.w(TAG, "getQqAuthStatus error: " + t.getMessage());
            return 3;
        }
    }

    public void authorizeQqMusic() {
        if (this.qqMusicFetcher == null) {
            return;
        }
        this.qqMusicFetcher.probeRoot(new QQMusicLyricFetcher.RootProbeCallback() { // from class: com.ecarx.carmedia.CarMediaService.19
            @Override // com.ecarx.carmedia.QQMusicLyricFetcher.RootProbeCallback
            public void onRootProbeResult(boolean hasRoot) {
                Log.i(CarMediaService.TAG, "authorizeQqMusic: hasRoot=" + hasRoot);
                if (!hasRoot) {
                    if (CarMediaService.this.mAuthListener != null) {
                        CarMediaService.this.mAuthListener.onAuthMessage("无 root 环境,登录授权暂不支持");
                    }
                } else {
                    Thread t = new Thread(new Runnable() { // from class: com.ecarx.carmedia.CarMediaService.19.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                CarMediaService.this.qqMusicFetcher.forceAuthWrite();
                                CarMediaService.this.qqMusicFetcher.refreshAuthState();
                                CarMediaService.this.qqMusicFetcher.restartQqProcess();
                                CarMediaService.this.qqMusicFetcher.bind();
                                CarMediaService.this.qqMusicFetcher.fetchCurrentSong();
                                Log.i(CarMediaService.TAG, "authorizeQqMusic: root direct write done");
                            } catch (Throwable t2) {
                                Log.w(CarMediaService.TAG, "authorizeQqMusic error: " + t2.getMessage());
                            }
                        }
                    }, "qq-auth");
                    t.start();
                }
            }
        });
    }
}
